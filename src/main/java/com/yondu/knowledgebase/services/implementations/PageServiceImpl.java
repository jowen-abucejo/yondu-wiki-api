package com.yondu.knowledgebase.services.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.repositories.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import static com.yondu.knowledgebase.Utils.MultipleSort.*;
import static com.yondu.knowledgebase.Utils.NativeQueryUtils.*;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.enums.Permission;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.services.PageRightsService;
import com.yondu.knowledgebase.services.PageService;
import com.yondu.knowledgebase.services.UserPermissionValidatorService;

@Service
public class PageServiceImpl extends PageServiceUtilities implements PageService {

	private final PageRepository pageRepository;
	private final PageVersionRepository pageVersionRepository;
	private final PageRightsService pageRightsService;
	private final AuditorAware<User> auditorAware;
	private final ReadPageRepository readPageRepository;
	private final UserPageAccessRepository userPageAccessRepository;

	private final Boolean IS_EXACT_MATCH_ONLY = true;
	private final Boolean IS_ARCHIVED_ONLY = true;
	private final Boolean IS_PUBLISHED_ONLY = true;
	private final Boolean INCLUDE_ALL_VERSIONS = true;
	private final Boolean INCLUDE_PENDING = true;
	private final Boolean INCLUDE_DRAFT = true;
	private final String AUTHOR_EMAIL = null;
	private final Boolean IS_SAVED_ONLY = true;
	private final Boolean IS_UP_VOTED_ONLY = true;

	public PageServiceImpl(PageRepository pageRepository, PageVersionRepository pageVersionRepository,
			UserPermissionValidatorService userPermissionValidatorService,
			PageRightsService pageRightsService,
			AuditorAware<User> auditorAware, TagRepository tagRepository,
			CategoryRepository categoryRepository,
			ReadPageRepository readPageRepository, UserPageAccessRepository userPageAccessRepository) {
		super(userPermissionValidatorService, auditorAware, categoryRepository, tagRepository);
		this.pageRepository = pageRepository;
		this.pageVersionRepository = pageVersionRepository;
		this.pageRightsService = pageRightsService;
		this.auditorAware = auditorAware;
		this.readPageRepository = readPageRepository;
		this.userPageAccessRepository = userPageAccessRepository;
	}

	private List<Order> nativeSort(String[] sortBy, String[] defaultSorting) {
		if (defaultSorting.length == 0)
			defaultSorting = new String[] { DATE_MODIFIED };

		return sortWithOrders(sortBy, defaultSorting,
				new HashSet<>(VALID_SORT_ALIASES));
	}

	private HashMap<String, Object> getOtherConfiguration(
			org.springframework.data.domain.Page<Map<String, Object>> optionalPageVersions) {
		var otherConfiguration = new HashMap<String, Object>();
		otherConfiguration.put("available_sorting", VALID_SORT_ALIASES);
		otherConfiguration.put("applied_sorting", optionalPageVersions.getSort()
				.map(order -> String.format("%s,%s", order.getProperty(), order.getDirection()))
				.toList());
		return otherConfiguration;
	}

	@Override
	public PageDTO findById(Long id) {
		var page = pageRepository.findById(id).orElse(null);

		if (page == null)
			throw new ResourceNotFoundException(pageNotFoundPhrase(id, PageType.WIKI));

		PageType pageType = page.getType().equals(PageType.WIKI.getCode()) ? PageType.WIKI : PageType.ANNOUNCEMENT;
		return findById(pageType, id);
	}

	@Override
	public PageDTO createNewPage(PageType pageType, Long directoryId, PageVersionDTO pageDTO) {
		if (!directoryPermissionGranted(directoryId, Permission.CREATE_CONTENT.getCode())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
		}

		var newPageVersion = new PageVersion();
		var newPage = new Page();
		var newDirectory = new Directory();

		setTitleAndContents(pageDTO, newPageVersion);
		newPageVersion.setPage(newPage);

		newDirectory.setId(directoryId);

		newPage.setDirectory(newDirectory);
		newPage.getPageVersions().add(newPageVersion);
		newPage.setType(pageType.getCode());
		newPage.setLockEnd(LocalDateTime.now().plusHours(1));

		var categories = setCategories(pageDTO.getCategories(), newPage);
		var tags = setTags(pageDTO.getTags(), newPage);

		newPage = pageRepository.save(newPage);

		pageRightsService.createPageRights(newPage);

		return pageDTODefault(newPageVersion, new Long[] { 0L, 0L, 0L }).categories(categories).tags(tags)
				.build();

	}

	@Override
	public PageDTO updatePageDraft(PageType pageType, Long pageId, Long versionId, PageVersionDTO pageDTO) {
		var pageDraft = pageVersionRepository
				.findByPageIdAndPageTypeAndId(pageId, pageType.getCode(), versionId)
				.orElseThrow(() -> new ResourceNotFoundException(
						pageNotFoundPhrase(pageId, versionId, pageType)));

		String requiredPermission = Permission.UPDATE_CONTENT.getCode();
		if (pagePermissionGranted(pageId, requiredPermission)) {
			// lock the page
			checkLock(pageDraft.getPage(), true);

			var categories = setCategories(pageDTO.getCategories(), pageDraft.getPage());
			var tags = setTags(pageDTO.getTags(), pageDraft.getPage());

			var reviewsCount = getReviewsCountByStatus(pageDraft);
			var pageVersionIsApproved = reviewsCount[0] > 0;
			var pageVersionIsDisapproved = reviewsCount[1] > 0;
			var pageVersionIsPendingApproval = reviewsCount[2] > 0;

			// save new page version if the version provided is already submitted
			if (pageVersionIsApproved || pageVersionIsDisapproved || pageVersionIsPendingApproval) {
				var newVersion = copyApprovedPageVersion(pageDraft);
				pageDraft = newVersion;
				reviewsCount = new Long[] { 0L, 0L, 0L };
			}

			setTitleAndContents(pageDTO, pageDraft);

			pageDraft = pageVersionRepository.save(pageDraft);

			return pageDTODefault(pageDraft, reviewsCount).categories(categories).tags(tags).build();

		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
	}

	@Override
	public PageDTO deletePage(PageType pageType, Long pageId) {
		var pageVersion = pageVersionRepository
				.findTopByPageIdAndPageTypeAndPageDeletedOrderByDateModifiedDesc(pageId,
						pageType.getCode(), false)
				.orElseThrow(() -> new ResourceNotFoundException(pageNotFoundPhrase(pageId, pageType)));
		var page = pageVersion.getPage();

		String requiredPermission = Permission.DELETE_CONTENT.getCode();
		if (pagePermissionGranted(pageId, requiredPermission)) {

			// check if page is locked
			checkLock(page, false);

			page.setDeleted(true);

			// remove userPageAccess objects associated with page
			List<UserPageAccess> toRemoveUserPageAccesses = page.getUserPageAccesses()
					.stream()
					.filter(userPageAccess -> userPageAccess.getPage().equals(page))
					.toList();

			toRemoveUserPageAccesses.forEach(page.getUserPageAccesses()::remove);
			userPageAccessRepository.deleteAll(toRemoveUserPageAccesses);

			Page updatedPage = pageRepository.save(page);
			pageVersion.setPage(updatedPage);

			return pageDTODefault(pageVersion).deleted(pageVersion.getPage().getDeleted()).build();
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
	}

	@Override
	public PageDTO updateActiveStatus(PageType pageType, Long pageId, Boolean isActive) {
		var pageVersion = pageVersionRepository
				.findTopByPageIdAndPageTypeAndPageDeletedOrderByDateModifiedDesc(pageId,
						pageType.getCode(), false)
				.orElseThrow(() -> new ResourceNotFoundException(pageNotFoundPhrase(pageId, pageType)));
		var page = pageVersion.getPage();

		String requiredPermission = Permission.DELETE_CONTENT.getCode();
		if (pagePermissionGranted(pageId, requiredPermission)) {

			checkLock(page, false);

			page.setActive(isActive);
			pageVersion.setPage(pageRepository.save(page));

			return pageDTODefault(pageVersion).build();
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
	}

	@Override
	public PageDTO updateCommenting(PageType pageType, Long pageId, Boolean allowCommenting) {
		var pageVersion = pageVersionRepository
				.findTopByPageIdAndPageTypeAndPageDeletedOrderByDateModifiedDesc(pageId,
						pageType.getCode(), false)
				.orElseThrow(() -> new ResourceNotFoundException(pageNotFoundPhrase(pageId, pageType)));
		var page = pageVersion.getPage();
		String requiredPermission = Permission.COMMENT_AVAILABILITY.getCode();
		if (pagePermissionGranted(pageId, requiredPermission)) {

			checkLock(page, false);

			page.setAllowComment(allowCommenting);
			pageVersion.setPage(pageRepository.save(page));

			return pageDTODefault(pageVersion).build();
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
	}

	@Override
	public PageDTO findByIdWithVersions(PageType pageType, Long id) {
		boolean isNotPost = !pageType.equals(PageType.DISCUSSION);
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();

		if (isNotPost && !pageRepository.existsByIdAndTypeAndDeleted(id, pageType.getCode(), false))
			throw new ResourceNotFoundException(pageNotFoundPhrase(id, pageType));

		var nativeSort = nativeSort(new String[] { DATE_MODIFIED }, new String[] {});
		Pageable paging = PageRequest.of(0, 100, Sort.by(nativeSort));
		paging = sortByAliases(paging);

		var optionalPageVersions = pageVersionRepository
				.searchAll(arrayToSqlStringList(new String[] { pageType.getCode() }), "", !IS_EXACT_MATCH_ONLY,
						null, !IS_PUBLISHED_ONLY, INCLUDE_ALL_VERSIONS, arrayToSqlStringList(new String[] {}),
						arrayToSqlStringList(new String[] {}), userId,
						arrayToSqlStringList(new Long[] { id }), null, INCLUDE_PENDING,
						INCLUDE_DRAFT, null, AUTHOR_EMAIL, !IS_SAVED_ONLY, !IS_UP_VOTED_ONLY, paging)
				.orElse(null);

		if (optionalPageVersions == null || optionalPageVersions.getContent().isEmpty()) {
			if (isNotPost) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
			} else {
				throw new ResourceNotFoundException(pageNotFoundPhrase(id, pageType));
			}
		}

		var pageList = optionalPageVersions.getContent();

		return convertMapToPageDTO(pageList.get(0), pageList);
	}

	@Override
	public PageDTO findById(PageType pageType, Long id) {
		return findByIdWithVersions(pageType, id);
	}

	@Override
	public PaginatedResponse<PageDTO> findAllByDirectoryIdAndFullTextSearch(PageType pageType, Long directoryId,
			String searchKey, String[] categories, String[] tags, Boolean isArchived, Boolean isPublished,
			Boolean exactSearch, Integer pageNumber, Integer pageSize, String[] sortBy) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		int retrievedPage = Math.max(1, pageNumber);

		var nativeSort = nativeSort(sortBy, new String[] {});
		Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
		paging = sortByAliases(paging);

		// format search key words
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		var optionalPageVersions = pageVersionRepository
				.searchAll(arrayToSqlStringList(new String[] { pageType.getCode() }), searchKey, exactSearch,
						isArchived, isPublished, !INCLUDE_ALL_VERSIONS, arrayToSqlStringList(new String[] {}),
						arrayToSqlStringList(new String[] {}), userId,
						arrayToSqlStringList(new Long[] {}), directoryId, !INCLUDE_PENDING,
						!INCLUDE_DRAFT, null, AUTHOR_EMAIL, !IS_SAVED_ONLY, !IS_UP_VOTED_ONLY, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
				optionalPageVersions.getTotalElements(), getOtherConfiguration(optionalPageVersions));

	}

	@Override
	public PaginatedResponse<PageDTO> findAllPendingVersions(PageType pageType, String searchKey, Integer pageNumber,
			Integer pageSize, String[] sortBy) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		int retrievedPage = Math.max(1, pageNumber);

		var nativeSort = nativeSort(sortBy, new String[] {});
		Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
		paging = sortByAliases(paging);

		// format search key words
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		var optionalPageVersions = pageVersionRepository
				.searchAll(arrayToSqlStringList(new String[] { pageType.getCode() }), searchKey, !IS_EXACT_MATCH_ONLY,
						!IS_ARCHIVED_ONLY, !IS_PUBLISHED_ONLY, !INCLUDE_ALL_VERSIONS,
						arrayToSqlStringList(new String[] {}),
						arrayToSqlStringList(new String[] {}), userId,
						arrayToSqlStringList(new Long[] {}), null, INCLUDE_PENDING,
						!INCLUDE_DRAFT, null, AUTHOR_EMAIL, !IS_SAVED_ONLY, !IS_UP_VOTED_ONLY, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
				optionalPageVersions.getTotalElements(), getOtherConfiguration(optionalPageVersions));

	}

	@Override
	public PaginatedResponse<PageDTO> findAllDraftVersions(PageType pageType, String searchKey, Integer pageNumber,
			Integer pageSize, String[] sortBy) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		int retrievedPage = Math.max(1, pageNumber);

		var nativeSort = nativeSort(sortBy, new String[] {});
		Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
		paging = sortByAliases(paging);

		// format search key words
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		var optionalPageVersions = pageVersionRepository
				.searchAll(arrayToSqlStringList(new String[] { pageType.getCode() }), searchKey, !IS_EXACT_MATCH_ONLY,
						!IS_ARCHIVED_ONLY, !IS_PUBLISHED_ONLY, !INCLUDE_ALL_VERSIONS,
						arrayToSqlStringList(new String[] {}),
						arrayToSqlStringList(new String[] {}), userId,
						arrayToSqlStringList(new Long[] {}), null, !INCLUDE_PENDING, INCLUDE_DRAFT,
						null, AUTHOR_EMAIL, !IS_SAVED_ONLY, !IS_UP_VOTED_ONLY, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
				optionalPageVersions.getTotalElements(), getOtherConfiguration(optionalPageVersions));

	}

	@Override
	public PageDTO movePageToDirectory(PageType pageType, Long directoryId, Long pageId) {

		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();

		if (userId == null)
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

		if (!pageRepository.existsByIdAndTypeAndDeleted(pageId, pageType.getCode(), false))
			throw new ResourceNotFoundException(pageNotFoundPhrase(pageId, pageType));

		if (!directoryPermissionGranted(directoryId, Permission.CREATE_CONTENT.getCode()))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Missing required directory permission");

		var page = pageRepository.findByIdAndTypeAndDeleted(pageId, pageType.getCode(), false).orElse(null);

		if (!page.getAuthor().getId().equals(userId))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Only page author is allowed to moved content to different directory");

		Directory newParentDirectory = new Directory();
		newParentDirectory.setId(directoryId);
		page.setDirectory(newParentDirectory);

		page = pageRepository.save(page);

		return findByIdWithVersions(pageType, pageId);
	}

	@Override
	public PageDTO findVersion(PageType pageType, Long pageId, Long versionId) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();

		if (!pageRepository.existsByIdAndTypeAndDeleted(pageId, pageType.getCode(), false))
			throw new ResourceNotFoundException(pageNotFoundPhrase(pageId, versionId, pageType));

		var nativeSort = nativeSort(new String[] { DATE_MODIFIED }, new String[] {});
		Pageable paging = PageRequest.of(0, 100, Sort.by(nativeSort));
		paging = sortByAliases(paging);

		var optionalPageVersions = pageVersionRepository
				.searchAll(arrayToSqlStringList(new String[] { pageType.getCode() }), "", IS_EXACT_MATCH_ONLY, null,
						IS_PUBLISHED_ONLY, INCLUDE_ALL_VERSIONS, arrayToSqlStringList(new String[] {}),
						arrayToSqlStringList(new String[] {}), userId, arrayToSqlStringList(new Long[] { pageId }),
						null, INCLUDE_PENDING, INCLUDE_DRAFT, null, AUTHOR_EMAIL, !IS_SAVED_ONLY, !IS_UP_VOTED_ONLY,
						paging)
				.orElse(null);

		// throw error if page is not found in active and archive search
		if (optionalPageVersions == null || optionalPageVersions.getContent().isEmpty())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");

		var pageList = optionalPageVersions.getContent();
		var versionAsPageBody = pageList.stream().filter(version -> {
			Long streamId = (Long) (version.get("versionId"));
			if (streamId == null)
				return false;
			return streamId.equals(versionId);
		}).collect(Collectors.toList());

		if (versionAsPageBody.size() == 0)
			throw new ResourceNotFoundException(pageNotFoundPhrase(pageId, versionId, pageType));

		return convertMapToPageDTO(versionAsPageBody.get(0), pageList);
	}

	@Override
	public PageDTO markAsRead(PageType pageType, Long pageId) {
		User user = auditorAware.getCurrentAuditor().orElse(new User());

		if (!pageRepository.existsByIdAndTypeAndDeleted(pageId, pageType.getCode(), false))
			throw new ResourceNotFoundException(pageNotFoundPhrase(pageId, pageType));

		Page pageToMarkAsRead = new Page();
		pageToMarkAsRead.setId(pageId);

		readPageRepository.save(new ReadPage(user, pageToMarkAsRead));

		return findById(pageId);
	}

	@Override
	public List<PageDTO> getUnreadPages(PageType pageType) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		var pages = searchAll(new String[] { pageType.getCode() }, "", new Long[] {},
				new String[] {}, new String[] {}, !IS_ARCHIVED_ONLY, IS_PUBLISHED_ONLY, !IS_EXACT_MATCH_ONLY,
				1, 100, 0L, AUTHOR_EMAIL, !IS_SAVED_ONLY, !IS_UP_VOTED_ONLY, new String[] { DATE_MODIFIED });
		var readPages = readPageRepository.findAllPageIdByUserIdAndPageType(userId, pageType.getCode());

		return pages.getData().stream().filter(page -> !readPages.contains(page.getId())).toList();
	}

	@Override
	public PaginatedResponse<PageDTO> searchAll(String[] pageTypeFilter, String searchKey, Long[] primaryKeys,
			String[] categories, String[] tags, Boolean isArchived, Boolean isPublished, Boolean exactSearch,
			Integer pageNumber, Integer pageSize, Long days, String author, Boolean savedOnly,
			Boolean upVotedOnly, String[] sortBy) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		LocalDateTime fromDate = null;

		if (days != null) {
			fromDate = LocalDate.now().minusDays(days).atStartOfDay();
		}

		int retrievedPage = Math.max(1, pageNumber);
		var nativeSort = nativeSort(sortBy, new String[] {});
		Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
		paging = sortByAliases(paging);

		// format search key words
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		var optionalPageVersions = pageVersionRepository
				.searchAll(arrayToSqlStringList(pageTypeFilter), searchKey, exactSearch,
						isArchived, isPublished, !INCLUDE_ALL_VERSIONS, arrayToSqlStringList(categories),
						arrayToSqlStringList(tags), userId,
						arrayToSqlStringList(primaryKeys),
						null, INCLUDE_PENDING, INCLUDE_DRAFT,
						fromDate, author, savedOnly, upVotedOnly, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
				optionalPageVersions.getTotalElements(), getOtherConfiguration(optionalPageVersions));
	}
}

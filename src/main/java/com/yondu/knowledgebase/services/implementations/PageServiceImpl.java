package com.yondu.knowledgebase.services.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.Utils.MultipleSort;
import com.yondu.knowledgebase.Utils.NativeQueryUtils;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.entities.ReadPage;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.enums.Permission;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import com.yondu.knowledgebase.repositories.ReadPageRepository;
import com.yondu.knowledgebase.repositories.TagRepository;
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

	private final Logger log = LoggerFactory.getLogger(PageServiceImpl.class);

	public PageServiceImpl(PageRepository pageRepository, PageVersionRepository pageVersionRepository,
			UserPermissionValidatorService userPermissionValidatorService,
			PageRightsService pageRightsService,
			AuditorAware<User> auditorAware, TagRepository tagRepository,
			CategoryRepository categoryRepository,
			ReadPageRepository readPageRepository) {
		super(userPermissionValidatorService, auditorAware, categoryRepository, tagRepository);
		this.pageRepository = pageRepository;
		this.pageVersionRepository = pageVersionRepository;
		this.pageRightsService = pageRightsService;
		this.auditorAware = auditorAware;
		this.readPageRepository = readPageRepository;
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
		if (pagePermissionGranted(pageId, requiredPermission)
				|| directoryPermissionGranted(pageDraft.getPage().getDirectory().getId(),
						requiredPermission)) {
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
		if (pagePermissionGranted(pageId, requiredPermission)
				|| directoryPermissionGranted(page.getDirectory().getId(), requiredPermission)) {

			// check if page is locked
			checkLock(page, false);

			page.setDeleted(true);
			pageVersion.setPage(pageRepository.save(page));

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
		if (pagePermissionGranted(pageId, requiredPermission)
				|| directoryPermissionGranted(page.getDirectory().getId(), requiredPermission)) {

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
		if (pagePermissionGranted(pageId, requiredPermission)
				|| directoryPermissionGranted(page.getDirectory().getId(), requiredPermission)) {

			checkLock(page, false);

			page.setAllowComment(allowCommenting);
			pageVersion.setPage(pageRepository.save(page));

			return pageDTODefault(pageVersion).build();
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
	}

	@Override
	public Page getPage(Long id) {
		var page = pageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Page with id: " + id + " not found!"));
		Long directoryId = page.getDirectory().getId();

		String requiredPermission = Permission.READ_CONTENT.getCode();
		if (pagePermissionGranted(id, requiredPermission)
				|| directoryPermissionGranted(directoryId, requiredPermission)) {
			return page;
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
	}

	@Override
	public PageDTO findByIdWithVersions(PageType pageType, Long id) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();

		if (!pageRepository.existsByIdAndTypeAndDeleted(id, pageType.getCode(), false))
			throw new ResourceNotFoundException(pageNotFoundPhrase(id, pageType));

		// configure pageable size and orders
		var validSortAliases = Arrays.asList("dateModified", "dateCreated", "relevance", "totalComments",
				"totalRatings");
		var nativeSort = MultipleSort.sortWithOrders(new String[] { "dateModified,desc" },
				new String[] { "dateModified,desc" },
				new HashSet<>(validSortAliases));
		Pageable paging = PageRequest.of(0, 100, Sort.by(nativeSort));
		paging = MultipleSort.sortByAliases(paging);

		var optionalPageVersions = pageVersionRepository
				.findByFullTextSearch(pageType.getCode(), "", true, false, true, true,
						null, null, userId,
						NativeQueryUtils.arrayToSqlStringList(new Long[] { id }), null,
						true, true, false, null, paging)
				.orElse(null);

		if (optionalPageVersions == null || optionalPageVersions.getContent().isEmpty())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");

		var pageList = optionalPageVersions.getContent();

		return convertMapToPageDTO(pageList.get(0), pageList);
	}

	public PaginatedResponse<PageDTO> findAllByFullTextSearch(PageType pageType, String searchKey,
			Long[] primaryKeys, String[] categories,
			String[] tags, Boolean isArchive, Boolean isPublished, Boolean exactSearch, Integer pageNumber,
			Integer pageSize, String startDate, String[] sortBy) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();

		LocalDateTime fromDate = null;
		if (startDate != null && !startDate.isEmpty()) {
			try {
				fromDate = LocalDate.parse(startDate).atStartOfDay();
			} catch (Exception e) {
			}
		}

		int retrievedPage = Math.max(1, pageNumber);

		// configure pageable size and orders
		var validSortAliases = Arrays.asList("dateModified", "dateCreated", "relevance", "totalComments",
				"totalRatings");
		var nativeSort = MultipleSort.sortWithOrders(sortBy, new String[] { "dateModified,desc" },
				new HashSet<>(validSortAliases));
		Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
		paging = MultipleSort.sortByAliases(paging);

		// format search key words
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		var optionalPageVersions = pageVersionRepository
				.findByFullTextSearch(pageType.getCode(), searchKey, exactSearch, isArchive,
						isPublished, false, NativeQueryUtils.arrayToSqlStringList(categories),
						NativeQueryUtils.arrayToSqlStringList(tags), userId,
						NativeQueryUtils.arrayToSqlStringList(primaryKeys), null, true, true,
						false, fromDate, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		var otherConfiguration = new HashMap<String, Object>();
		otherConfiguration.put("available_sorting", validSortAliases);
		otherConfiguration.put("applied_sorting", optionalPageVersions.getSort()
				.map(order -> String.format("%s,%s", order.getProperty(), order.getDirection()))
				.toList());
		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
				optionalPageVersions.getTotalElements(), otherConfiguration);

	}

	@Override
	public PageDTO findById(PageType pageType, Long id) {

		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();

		if (!pageRepository.existsByIdAndTypeAndDeleted(id, pageType.getCode(), false))
			throw new ResourceNotFoundException(pageNotFoundPhrase(id, pageType));

		var optionalPageVersions = pageVersionRepository
				.findByFullTextSearch(pageType.getCode(), "", true, false, true, false,
						null, null, userId,
						NativeQueryUtils.arrayToSqlStringList(new Long[] { id }), null,
						false, false, false, null,
						PageRequest.of(0, 100))
				.orElse(null);

		// if page is not active, search page from archives
		if (optionalPageVersions == null || optionalPageVersions.getContent().isEmpty()) {
			optionalPageVersions = pageVersionRepository
					.findByFullTextSearch(pageType.getCode(), "", true, true, true, false,
							null, null, userId,
							NativeQueryUtils.arrayToSqlStringList(new Long[] { id }), null,
							false, false, false, null,
							PageRequest.of(0, 100))
					.orElse(null);
		}

		// throw error if page is not found in active and archive search
		if (optionalPageVersions == null || optionalPageVersions.getContent().isEmpty())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");

		var pageList = optionalPageVersions.getContent();

		return convertMapToPageDTO(pageList.get(0), pageList);
	}

	@Override
	public Page getPage(PageType pageType, Long pageId) {
		var page = pageRepository.findByIdAndType(pageId, pageType.getCode())
				.orElseThrow(() -> new ResourceNotFoundException(pageNotFoundPhrase(pageId, pageType)));
		Long directoryId = page.getDirectory().getId();

		String requiredPermission = Permission.READ_CONTENT.getCode();
		if (pagePermissionGranted(pageId, requiredPermission)
				|| directoryPermissionGranted(directoryId, requiredPermission)) {
			return page;
		}

		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
	}

	@Override
	public PaginatedResponse<PageDTO> findAllByDirectoryIdAndFullTextSearch(PageType pageType, Long directoryId,
			String searchKey, String[] categories, String[] tags, Boolean isArchive, Boolean isPublished,
			Boolean exactSearch, Integer pageNumber, Integer pageSize, String[] sortBy) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		int retrievedPage = Math.max(1, pageNumber);

		// configure pageable size and orders
		var validSortAliases = Arrays.asList("dateModified", "dateCreated", "relevance", "totalComments",
				"totalRatings");
		var nativeSort = MultipleSort.sortWithOrders(sortBy, new String[] { "dateModified,desc" },
				new HashSet<>(validSortAliases));
		Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
		paging = MultipleSort.sortByAliases(paging);

		// format search key words
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		var optionalPageVersions = pageVersionRepository
				.findByFullTextSearch(pageType.getCode(), searchKey, exactSearch, isArchive,
						isPublished, false, NativeQueryUtils.arrayToSqlStringList(categories),
						NativeQueryUtils.arrayToSqlStringList(tags), userId, null, directoryId,
						true, true, false, null, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		var otherConfiguration = new HashMap<String, Object>();
		otherConfiguration.put("available_sorting", validSortAliases);
		otherConfiguration.put("applied_sorting", optionalPageVersions.getSort()
				.map(order -> String.format("%s,%s", order.getProperty(), order.getDirection()))
				.toList());
		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
				optionalPageVersions.getTotalElements(), otherConfiguration);

	}

	@Override
	public PaginatedResponse<PageDTO> findPagesByUser(int page, int size, String type, String[] sortBy) {
		log.info("PageServiceImpl.findPagesByUser()");
		log.info("page : " + page);
		log.info("size : " + size);
		log.info("type : " + type);

		User user = auditorAware.getCurrentAuditor().get();
		Long userId = user.getId();
		int retrievedPage = Math.max(1, page);

		// configure pageable size and orders
		var validSortAliases = Arrays.asList("dateModified", "dateCreated", "relevance", "totalComments",
				"totalRatings");
		var nativeSort = MultipleSort.sortWithOrders(sortBy, new String[] { "dateModified,desc" },
				new HashSet<>(validSortAliases));
		Pageable paging = PageRequest.of(retrievedPage - 1, size, Sort.by(nativeSort));
		paging = MultipleSort.sortByAliases(paging);

		var optionalPageVersions = pageVersionRepository
				.findByFullTextSearch(type, "", false, false,
						false, false, NativeQueryUtils.arrayToSqlStringList(new String[] {}),
						NativeQueryUtils.arrayToSqlStringList(new String[] {}), userId,
						NativeQueryUtils.arrayToSqlStringList(new Long[] {}), null, false,
						false, false, null, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		var otherConfiguration = new HashMap<String, Object>();
		otherConfiguration.put("available_sorting", validSortAliases);
		otherConfiguration.put("applied_sorting", optionalPageVersions.getSort()
				.map(order -> String.format("%s,%s", order.getProperty(), order.getDirection()))
				.toList());
		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, size,
				optionalPageVersions.getTotalElements(), otherConfiguration);
	}

	public PaginatedResponse<PageDTO> findAllPendingVersions(PageType pageType, String searchKey, Boolean isArchive,
			Boolean approverOnly, Integer pageNumber, Integer pageSize, String[] sortBy) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		int retrievedPage = Math.max(1, pageNumber);

		// configure pageable size and orders
		var validSortAliases = Arrays.asList("dateModified", "dateCreated", "relevance", "totalComments",
				"totalRatings");
		var nativeSort = MultipleSort.sortWithOrders(sortBy, new String[] { "dateModified,desc" },
				new HashSet<>(validSortAliases));
		Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
		paging = MultipleSort.sortByAliases(paging);

		// format search key words
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		var optionalPageVersions = pageVersionRepository
				.findByFullTextSearch(pageType.getCode(), searchKey, false, isArchive,
						false, false, NativeQueryUtils.arrayToSqlStringList(new String[] {}),
						NativeQueryUtils.arrayToSqlStringList(new String[] {}), userId,
						NativeQueryUtils.arrayToSqlStringList(new Long[] {}), null, true,
						false, approverOnly, null, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		var otherConfiguration = new HashMap<String, Object>();
		otherConfiguration.put("available_sorting", validSortAliases);
		otherConfiguration.put("applied_sorting", optionalPageVersions.getSort()
				.map(order -> String.format("%s,%s", order.getProperty(), order.getDirection()))
				.toList());
		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
				optionalPageVersions.getTotalElements(), otherConfiguration);

	}

	public PaginatedResponse<PageDTO> findAllDraftVersions(PageType pageType, String searchKey, Boolean isArchive,
			Integer pageNumber,
			Integer pageSize, String[] sortBy) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		int retrievedPage = Math.max(1, pageNumber);

		// configure pageable size and orders
		var validSortAliases = Arrays.asList("dateModified", "dateCreated", "relevance", "totalComments",
				"totalRatings");
		var nativeSort = MultipleSort.sortWithOrders(sortBy, new String[] { "dateModified,desc" },
				new HashSet<>(validSortAliases));
		Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
		paging = MultipleSort.sortByAliases(paging);

		// format search key words
		searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		var optionalPageVersions = pageVersionRepository
				.findByFullTextSearch(pageType.getCode(), searchKey, false, isArchive,
						false, false, NativeQueryUtils.arrayToSqlStringList(new String[] {}),
						NativeQueryUtils.arrayToSqlStringList(new String[] {}), userId,
						NativeQueryUtils.arrayToSqlStringList(new Long[] {}), null, false, true,
						false, null, paging)
				.orElse(null);

		var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
			return convertMapToPageDTO(pageVersion);
		}).collect(Collectors.toList());

		var otherConfiguration = new HashMap<String, Object>();
		otherConfiguration.put("available_sorting", validSortAliases);
		otherConfiguration.put("applied_sorting", optionalPageVersions.getSort()
				.map(order -> String.format("%s,%s", order.getProperty(), order.getDirection()))
				.toList());
		return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
				optionalPageVersions.getTotalElements(), otherConfiguration);

	}

	public Boolean getLockStatus(Long pageId, Boolean lockAfter) {
		Page page = pageRepository.findById(pageId)
				.orElseThrow(() -> new ResourceNotFoundException("Cannot find the page."));
		checkLock(page, lockAfter);

		return true;
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

		// configure pageable size and orders
		var validSortAliases = Arrays.asList("dateModified", "dateCreated", "relevance", "totalComments",
				"totalRatings");
		var nativeSort = MultipleSort.sortWithOrders(new String[] { "dateModified,desc" },
				new String[] { "dateModified,desc" },
				new HashSet<>(validSortAliases));
		Pageable paging = PageRequest.of(0, 100, Sort.by(nativeSort));
		paging = MultipleSort.sortByAliases(paging);

		var optionalPageVersions = pageVersionRepository
				.findByFullTextSearch(pageType.getCode(), "", true, false, true, true,
						null, null, userId,
						NativeQueryUtils.arrayToSqlStringList(new Long[] { pageId }), null,
						true, false, false, null, paging)
				.orElse(null);

		// if page is not active, search page from archives
		if (optionalPageVersions == null || optionalPageVersions.getContent().isEmpty()) {
			optionalPageVersions = pageVersionRepository
					.findByFullTextSearch(pageType.getCode(), "", true, true, true, true,
							null, null, userId,
							NativeQueryUtils.arrayToSqlStringList(new Long[] { pageId }), null,
							true, false, false, null, paging)
					.orElse(null);
		}

		// throw error if page is not found in active and archive search
		if (optionalPageVersions == null || optionalPageVersions.getContent().isEmpty())
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");

		var pageList = optionalPageVersions.getContent();
		var versionAsPageBody = pageList.stream().filter(version -> {
			Long streamId = (Long) (version.get("versionId"));
			if (streamId == null)
				return false;
			return streamId.equals(versionId);
		})
				.collect(Collectors.toList());

		if (versionAsPageBody.size() == 0)
			throw new ResourceNotFoundException(pageNotFoundPhrase(pageId, versionId, pageType));

		return convertMapToPageDTO(versionAsPageBody.get(0), pageList);
	}

	@Override
	public PageDTO markAsRead(PageType pageType, Long pageId) {
		User user = auditorAware.getCurrentAuditor().orElse(new User());

		if (!pageRepository.existsByIdAndTypeAndDeleted(pageId, pageType.getCode(), false))
			throw new ResourceNotFoundException(pageNotFoundPhrase(pageId, pageType));

		Page announcementToMarkAsRead = new Page();
		announcementToMarkAsRead.setId(pageId);

		readPageRepository.save(new ReadPage(user, announcementToMarkAsRead));

		return findById(pageId);
	}

	@Override
	public List<PageDTO> getUnreadPages(PageType pageType) {
		Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
		String fromDate = LocalDate.now().minusDays(30).toString();

		var pages = findAllByFullTextSearch(pageType, "", new Long[] {},
				new String[] {}, new String[] {}, false, true,
				false, 1, 100, fromDate, new String[] {});
		var readPages = readPageRepository.findAllPageIdByUserIdAndPageType(userId, pageType.getCode());

		return pages.getData().stream().filter(page -> !readPages.contains(page.getId())).toList();
	}
}

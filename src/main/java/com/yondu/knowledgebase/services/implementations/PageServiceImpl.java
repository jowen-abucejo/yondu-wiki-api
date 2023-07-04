package com.yondu.knowledgebase.services.implementations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.enums.Permission;
import com.yondu.knowledgebase.enums.ReviewStatus;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
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

    private final Logger log = LoggerFactory.getLogger(PageServiceImpl.class);

    /**
     * @param pageRepository
     * @param pageVersionRepository
     * @param auditorAware
     */
    public PageServiceImpl(PageRepository pageRepository, PageVersionRepository pageVersionRepository,
            UserPermissionValidatorService userPermissionValidatorService,
            PageRightsService pageRightsService,
            AuditorAware<User> auditorAware, TagRepository tagRepository,
            CategoryRepository categoryRepository) {
        super(userPermissionValidatorService, auditorAware, categoryRepository, tagRepository);
        this.pageRepository = pageRepository;
        this.pageVersionRepository = pageVersionRepository;
        this.pageRightsService = pageRightsService;
        this.auditorAware = auditorAware;
    }

    @Override
    public PageDTO findById(Long id) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedAndReviewsStatusOrderByDateModifiedDesc(id, false,
                        ReviewStatus.APPROVED.getCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));

        String requiredPermission = Permission.READ_CONTENT.getCode();
        if (pagePermissionGranted(id, requiredPermission)
                || directoryPermissionGranted(pageVersion.getPage().getDirectory().getId(),
                        requiredPermission)) {
            return pageDTODefault(pageVersion).build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
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

            setTitleAndContents(pageDTO, pageDraft);

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

        String requiredPermission = Permission.UPDATE_CONTENT.getCode();
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
        String requiredPermission = Permission.UPDATE_CONTENT.getCode();
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
                        true, true, false, paging)
                .orElse(null);

        if (optionalPageVersions == null || optionalPageVersions.getContent().isEmpty())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");

        var pageList = optionalPageVersions.getContent();

        return convertMapToPageDTO(pageList.get(0), pageList);
    }

    public PaginatedResponse<PageDTO> findAllByFullTextSearch(PageType pageType, String searchKey,
            Long[] primaryKeys, String[] categories,
            String[] tags, Boolean isArchive, Boolean isPublished, Boolean exactSearch, Integer pageNumber,
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
                .findByFullTextSearch(pageType.getCode(), searchKey, exactSearch, isArchive,
                        isPublished, false, NativeQueryUtils.arrayToSqlStringList(categories),
                        NativeQueryUtils.arrayToSqlStringList(tags), userId,
                        NativeQueryUtils.arrayToSqlStringList(primaryKeys), null, true, true,
                        false, paging)
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
                        false, false, false,
                        PageRequest.of(0, 100))
                .orElse(null);

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
                        isPublished, true, NativeQueryUtils.arrayToSqlStringList(categories),
                        NativeQueryUtils.arrayToSqlStringList(tags), userId, null, directoryId,
                        true,
                        true, false, paging)
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
                        false, false, paging)
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
                        false, approverOnly, paging)
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
                        false, paging)
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

}

package com.yondu.knowledgebase.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageDTO.PageDTOBuilder;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO.PageVersionDTOBuilder;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.page.UserDTO;
import com.yondu.knowledgebase.Utils.MultipleSort;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.enums.ReviewStatus;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.Permission;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import com.yondu.knowledgebase.services.PageRightsService;
import com.yondu.knowledgebase.services.PageService;
import com.yondu.knowledgebase.services.UserPermissionValidatorService;

@Service
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;
    private final PageVersionRepository pageVersionRepository;
    private final PageRightsService pageRightsService;
    private final UserPermissionValidatorService userPermissionValidatorService;
    private final AuditorAware<User> auditorAware;

    /**
     * @param pageRepository
     * @param pageVersionRepository
     * @param auditorAware
     */
    public PageServiceImpl(PageRepository pageRepository, PageVersionRepository pageVersionRepository,
            UserPermissionValidatorService userPermissionValidatorService,
            PageRightsService pageRightsService,
            AuditorAware<User> auditorAware) {
        this.pageRepository = pageRepository;
        this.pageVersionRepository = pageVersionRepository;
        this.pageRightsService = pageRightsService;
        this.userPermissionValidatorService = userPermissionValidatorService;
        this.auditorAware = auditorAware;
    }

    private PageDTOBuilder pageDTODefault(PageVersion version) {
        return PageDTO.builder()
                .id(version.getPage().getId())
                .dateCreated(version.getPage().getDateCreated())
                .author(convertToUserDTO(version.getPage().getAuthor()))
                .active(version.getPage().getActive())
                .allowComment(version.getPage().getAllowComment())
                .lockStart(version.getPage().getLockStart())
                .lockEnd(version.getPage().getLockEnd())
                .body(pageVersionDTODefault(version).build());
    }

    private PageDTOBuilder pageWithVersionsDTODefault(Page page) {
        return PageDTO.builder()
                .id(page.getId())
                .versions(page.getPageVersions().stream().map(version -> {
                    return pageVersionDTODefault(version).build();
                }).collect(Collectors.toList()))
                .dateCreated(page.getDateCreated())
                .author(convertToUserDTO(page.getAuthor()))
                .active(page.getActive())
                .allowComment(page.getAllowComment())
                .lockStart(page.getLockStart())
                .lockEnd(page.getLockEnd());
    }

    private PageVersionDTOBuilder pageVersionDTODefault(PageVersion version) {
        return PageVersionDTO.builder()
                .id(version.getId())
                .title(version.getTitle())
                .content(version.getOriginalContent())
                .dateModified(version.getDateModified())
                .modifiedBy(convertToUserDTO(version.getModifiedBy()));
    }

    private PageDTO convertMapToPageDTO(Map<String, Object> pageVersion) {
        var dateCreated = pageVersion.getOrDefault("dateCreated", "");
        var dateModified = pageVersion.getOrDefault("dateModified", "");
        var lockStart = pageVersion.getOrDefault("lockStart", "");
        var lockEnd = pageVersion.getOrDefault("lockEnd", "");
        return PageDTO.builder()
                .id((Long) pageVersion.getOrDefault("pageId", 0L))
                .dateCreated(parseAndFormat(dateCreated))
                .totalComments((Long) pageVersion.getOrDefault("totalComments", 0L))
                .totalRatings((Long) pageVersion.getOrDefault("totalRatings", 0L))
                .relevance(BigDecimal.valueOf((Double) pageVersion.getOrDefault("relevance", 0.0)))
                .active((Boolean) pageVersion.get("isActive"))
                .allowComment((Boolean) pageVersion.get("allowComment"))
                .lockStart(parseAndFormat(lockStart))
                .lockEnd(parseAndFormat(lockEnd))
                .author(UserDTO.builder()
                        .email((String) pageVersion.getOrDefault("authorEmail", ""))
                        .firstName((String) pageVersion.getOrDefault("authorFirstName", ""))
                        .lastName((String) pageVersion.getOrDefault("authorLastName", ""))
                        .build())
                .body(PageVersionDTO.builder()
                        .id((Long) pageVersion.getOrDefault("versionId", 0L))
                        .content((String) pageVersion.getOrDefault("versionContent", ""))
                        .title((String) pageVersion.getOrDefault("versionTitle", ""))
                        .dateModified(parseAndFormat(dateModified))
                        .modifiedBy(UserDTO.builder()
                                .email((String) pageVersion.getOrDefault("modifiedByEmail", ""))
                                .firstName((String) pageVersion.getOrDefault("modifiedByFirstName", ""))
                                .lastName((String) pageVersion.getOrDefault("modifiedByLastName", ""))
                                .build())
                        .build())
                .categories(getAsArray(pageVersion.getOrDefault("pageCategories", null)))
                .tags(getAsArray(pageVersion.getOrDefault("pageTags", null)))
                .build();

    }

    private UserDTO convertToUserDTO(User user) {
        return user != null ? UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build() : null;
    }

    private LocalDateTime parseAndFormat(Object date) {
        return date != null && !date.toString().isEmpty()
                ? LocalDateTime.parse(date.toString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
                : null;
    }

    private String[] getAsArray(Object concatenatedElements) {
        if (concatenatedElements != null)
            return concatenatedElements.toString().split("\\|");
        return new String[] {};
    }

    private void lockPage(Page page) {
        var currentTime = LocalDateTime.now();
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isSameUser = page.getLockedBy().getId().equals(currentUser.getId());
        boolean isPageUnlocked = currentTime.isAfter(page.getLockEnd());

        // checked if page can be edit by current user
        if (!isSameUser && !isPageUnlocked)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Document is currently locked");

        if (!isSameUser)
            page.setLockedBy(currentUser);

        if (isPageUnlocked)
            page.setLockStart(currentTime);

        page.setLockEnd(currentTime.plusHours(1));
    }

    private boolean pagePermissionGranted(Long pageId, String permission) {
        Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
        return userPermissionValidatorService.userHasPagePermission(userId, pageId,
                permission);
    }

    private boolean directoryPermissionGranted(Long directoryId, String permission) {
        Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
        return userPermissionValidatorService.userHasDirectoryPermission(userId, directoryId,
                permission);
    }

    @Override
    public PageDTO findById(Long id) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedAndReviewsStatusOrderByDateModifiedDesc(id, false,
                        ReviewStatus.APPROVED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));

        String requiredPermission = Permission.READ_CONTENT.getCode();
        if (pagePermissionGranted(id, requiredPermission)
                || directoryPermissionGranted(pageVersion.getPage().getDirectory().getId(), requiredPermission)) {
            return pageDTODefault(pageVersion).build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
    }

    @Override
    public PageDTO createNewPage(Long directoryId, PageVersionDTO pageDTO) {
        if (directoryPermissionGranted(directoryId, Permission.CREATE_CONTENT.getCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
        }

        var newPageVersion = new PageVersion();
        var newPage = new Page();
        var newDirectory = new Directory();

        newPageVersion.setTitle(pageDTO.getTitle());
        newPageVersion.setContent(pageDTO.getContent().replaceAll("<[^>]+>", ""));
        newPageVersion.setOriginalContent(pageDTO.getContent());
        newPageVersion.setPage(newPage);

        newDirectory.setId(directoryId);

        newPage.setDirectory(newDirectory);
        newPage.getPageVersions().add(newPageVersion);

        newPage = pageRepository.save(newPage);

        pageRightsService.createPageRights(newPage);

        return pageDTODefault(newPageVersion).build();

    }

    @Override
    public PageDTO updatePageDraft(Long pageId, Long versionId, PageVersionDTO pageDTO) {
        var pageDraft = pageVersionRepository.findByPageIdAndId(pageId, versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));

        String requiredPermission = Permission.UPDATE_CONTENT.getCode();
        if (pagePermissionGranted(pageId, requiredPermission)
                || directoryPermissionGranted(pageDraft.getPage().getDirectory().getId(), requiredPermission)) {
            pageDraft.setTitle(pageDTO.getTitle());
            pageDraft.setContent(pageDTO.getContent().replaceAll("<[^>]+>", ""));
            pageDraft.setOriginalContent(pageDTO.getContent());

            // lock the page
            lockPage(pageDraft.getPage());

            pageDraft = pageVersionRepository.save(pageDraft);

            return pageDTODefault(pageDraft).build();

        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
    }

    @Override
    public PageDTO deletePage(Long pageId) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));
        var page = pageVersion.getPage();

        String requiredPermission = Permission.DELETE_CONTENT.getCode();
        if (pagePermissionGranted(pageId, requiredPermission)
                || directoryPermissionGranted(page.getDirectory().getId(), requiredPermission)) {
            page.setDeleted(true);
            pageVersion.setPage(pageRepository.save(page));

            return pageDTODefault(pageVersion).deleted(pageVersion.getPage().getDeleted()).build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
    }

    @Override
    public PageDTO updateActiveStatus(Long pageId, Boolean isActive) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));
        var page = pageVersion.getPage();

        String requiredPermission = Permission.UPDATE_CONTENT.getCode();
        if (pagePermissionGranted(pageId, requiredPermission)
                || directoryPermissionGranted(page.getDirectory().getId(), requiredPermission)) {
            page.setActive(isActive);
            pageVersion.setPage(pageRepository.save(page));

            return pageDTODefault(pageVersion).build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
    }

    @Override
    public PageDTO updateCommenting(Long pageId, Boolean allowCommenting) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));
        var page = pageVersion.getPage();
        String requiredPermission = Permission.UPDATE_CONTENT.getCode();
        if (pagePermissionGranted(pageId, requiredPermission)
                || directoryPermissionGranted(page.getDirectory().getId(), requiredPermission)) {
            page.setAllowComment(allowCommenting);
            pageVersion.setPage(pageRepository.save(page));

            return pageDTODefault(pageVersion).build();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
    }

    @Override
    public Page getPage(Long id) {
        var page = pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page with id: " + id + " not found!"));
        Long directoryId = page.getDirectory().getId();

        String requiredPermission = Permission.READ_CONTENT.getCode();
        if (pagePermissionGranted(id, requiredPermission)
                || directoryPermissionGranted(directoryId, requiredPermission)) {
            return page;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
    }

    @Override
    public PageDTO findByIdWithVersions(Long id) {
        var page = getPage(id);

        Long directoryId = page.getDirectory().getId();
        List<ReviewStatus> reviewsStatus = new ArrayList<>();
        reviewsStatus.add(ReviewStatus.APPROVED);

        String requiredPermission = Permission.CONTENT_APPROVAL.getCode();
        if (pagePermissionGranted(id, requiredPermission)
                || directoryPermissionGranted(directoryId, requiredPermission)) {
            reviewsStatus.add(ReviewStatus.PENDING);
            reviewsStatus.add(ReviewStatus.DISAPPROVED);
        }

        requiredPermission = Permission.UPDATE_CONTENT.getCode();
        if (pagePermissionGranted(id, requiredPermission)
                || directoryPermissionGranted(directoryId, requiredPermission)) {
            page = pageRepository.findTopByIdAndDeletedAndPageVersionsReviewsStatusInOrPageVersionsReviewsIsEmpty(id,
                    false, reviewsStatus).orElseThrow();
        } else {
            page = pageRepository.findTopByIdAndDeletedAndPageVersionsReviewsStatusIn(id, false, reviewsStatus)
                    .orElseThrow();
        }

        return pageWithVersionsDTODefault(page).build();
    }

    public PaginatedResponse<PageDTO> findAllByFullTextSearch(String searchKey, String[] categories, String[] tags,
            Boolean isArchive, Boolean isPublished, Boolean exactSearch, Integer pageNumber, Integer pageSize,
            String[] sortBy) {
        Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
        int retrievedPage = Math.max(1, pageNumber);
        retrievedPage = Math.min(100, retrievedPage);

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
                .findByFullTextSearch(searchKey, exactSearch, isArchive, isPublished, Arrays.asList(categories),
                        Arrays.asList(tags), userId, paging)
                .orElse(null);

        var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
            return convertMapToPageDTO(pageVersion);
        }).collect(Collectors.toList());

        var otherConfiguration = new HashMap<String, Object>();
        otherConfiguration.put("available_sorting", validSortAliases);
        otherConfiguration.put("applied_sorting", optionalPageVersions.getSort()
                .map(order -> String.format("%s,%s", order.getProperty(), order.getDirection())).toList());
        return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
                optionalPageVersions.getTotalElements(), otherConfiguration);

    }
}

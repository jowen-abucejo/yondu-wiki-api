package com.yondu.knowledgebase.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.page.UserDTO;
import com.yondu.knowledgebase.DTO.page.PageDTO.PageDTOBuilder;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO.PageVersionDTOBuilder;
import com.yondu.knowledgebase.Utils.MultipleSort;
import com.yondu.knowledgebase.services.PageService;

@Service
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;
    private final PageVersionRepository pageVersionRepository;
    private final PageRightsRepository pageRightsRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    /**
     * @param pageRepository
     * @param pageVersionRepository
     * @param pageRightsRepository
     * @param permissionRepository
     * @param userRepository
     */
    public PageServiceImpl(PageRepository pageRepository, PageVersionRepository pageVersionRepository,
    PageRightsRepository pageRightsRepository, PermissionRepository permissionRepository, UserRepository userRepository) {
        this.pageRepository = pageRepository;
        this.pageVersionRepository = pageVersionRepository;
        this.pageRightsRepository = pageRightsRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    private org.springframework.data.domain.Page<Page> filterPagesByTagsAndCategories(String[] categories,
            String[] tags, boolean deleted, String versionStatus, Pageable paging) {
        org.springframework.data.domain.Page<Page> optionalPages;
        if (categories.length > 0 && tags.length > 0) {
            optionalPages = pageRepository
                    .findByCategoriesInAndTagsInAndDeletedAndPageVersionsReviewsStatus(categories,
                            tags, deleted,
                            versionStatus, paging)
                    .orElse(null);
        } else if (categories.length > 0) {
            optionalPages = pageRepository
                    .findByCategoriesInAndDeletedAndPageVersionsReviewsStatus(categories, deleted,
                            versionStatus,
                            paging)
                    .orElse(null);
        } else if (tags.length > 0) {
            optionalPages = pageRepository
                    .findByTagsInAndDeletedAndPageVersionsReviewsStatus(tags, deleted,
                            versionStatus, paging)
                    .orElse(null);
        } else
            optionalPages = pageRepository
                    .findByDeletedAndPageVersionsReviewsStatus(deleted, versionStatus, paging)
                    .orElse(null);
        return optionalPages;
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
                .content(version.getContent())
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

    @Override
    public PageDTO findById(Long id) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedAndReviewsStatusOrderByDateModifiedDesc(id, false,
                        "approved")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));

        return pageDTODefault(pageVersion).build();
    }

    @Override
    public PaginatedResponse<PageDTO> findAllVersionsByTagsAndCategories(String[] categories, String[] tags,
            Integer pageNumber, Integer pageSize, String[] sortBy) {
        int retrievedPage = Math.max(1, pageNumber);
        var multipleSort = MultipleSort.sortWithOrders(sortBy, new String[] { "dateCreated,desc" });
        Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(multipleSort));

        org.springframework.data.domain.Page<Page> optionalPages;
        boolean deleted = false;
        String versionStatus = "approved";

        optionalPages = filterPagesByTagsAndCategories(categories, tags, deleted, versionStatus, paging);

        var pageList = optionalPages.getContent().stream().map(page -> {
            return pageWithVersionsDTODefault(page).build();
        }).collect(Collectors.toList());

        return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
                optionalPages.getTotalElements());
    }

    @Override
    public PageDTO createNewPage(Long directoryId, PageVersionDTO pageDTO) {
        var newPageVersion = new PageVersion();
        var newPage = new Page();
        var newDirectory = new Directory();

        newPageVersion.setTitle(pageDTO.getTitle());
        newPageVersion.setContent(pageDTO.getContent().replaceAll("<[^>]+>", ""));
        newPageVersion.setOriginalContent(pageDTO.getContent());
        newPageVersion.setPage(newPage);

        newDirectory.setId(directoryId);

        newPage.setDirectory(newDirectory);
        newPage.getPageVersions().add(newPageVersion); // Add the new PageVersion to

        newPage = pageRepository.save(newPage);

        createPageRights(newPage);


        return pageDTODefault(newPageVersion).build();

    }

    private void createPageRights(Page newPage) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PageRights> savedRights = pageRightsRepository
                .saveAll(permissionRepository
                        .findAllByCategoryOrCategoryOrCategoryOrCategory("Content", "Content Moderation", "Comment", "Page Editor")
                        .stream()
                        .map(obj -> pageRightsRepository.save(new PageRights(newPage, obj))).toList());

        Set<Rights> updatedRights = new HashSet<>(currentUser.getRights());
        updatedRights.addAll(savedRights);

        currentUser.setRights(updatedRights);
        userRepository.save(currentUser);
    }

    @Override
    public PageDTO updatePageDraft(Long pageId, Long versionId, PageVersionDTO pageDTO) {
        var pageDraft = pageVersionRepository.findByPageIdAndId(pageId, versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));

        pageDraft.setTitle(pageDTO.getTitle());
        pageDraft.setContent(pageDTO.getContent().replaceAll("<[^>]+>", ""));
        pageDraft.setOriginalContent(pageDTO.getContent());

        // lock the page
        lockPage(pageDraft.getPage());

        pageDraft = pageVersionRepository.save(pageDraft);

        return pageDTODefault(pageDraft).build();

    }

    @Override
    public PageDTO deletePage(Long pageId) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));
        var page = pageVersion.getPage();
        page.setDeleted(true);
        pageVersion.setPage(pageRepository.save(page));

        return pageDTODefault(pageVersion).deleted(pageVersion.getPage().getDeleted()).build();
    }

    @Override
    public PageDTO updateActiveStatus(Long pageId, Boolean isActive) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));
        var page = pageVersion.getPage();
        page.setActive(isActive);
        pageVersion.setPage(pageRepository.save(page));

        return pageDTODefault(pageVersion).build();
    }

    @Override
    public PageDTO updateCommenting(Long pageId, Boolean allowCommenting) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find document"));
        var page = pageVersion.getPage();
        page.setAllowComment(allowCommenting);
        pageVersion.setPage(pageRepository.save(page));

        return pageDTODefault(pageVersion).build();
    }

    @Override
    public Page getPage(Long id) {
        return pageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Page with id: " + id +" not found!"));
    }

    public PaginatedResponse<PageDTO> findAllByFullTextSearch(String searchKey, String[] categories, String[] tags,
            Boolean isArchive, Boolean isPublished, Boolean exactSearch, Integer pageNumber, Integer pageSize,
            String[] sortBy) {
        int retrievedPage = Math.max(1, pageNumber);

        var validSortAliases = Arrays.asList("dateCreated", "dateModified", "relevance", "totalComments",
                "totalRatings");
        var nativeSort = MultipleSort.sortWithOrders(sortBy, new String[] { "dateModified,desc" },
                new HashSet<>(validSortAliases));
        Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
        paging = MultipleSort.sortByAliases(paging);

        searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        var optionalPageVersions = pageVersionRepository
                .findByFullTextSearch(searchKey, exactSearch, isArchive, isPublished, Arrays.asList(categories),
                        Arrays.asList(tags), paging)
                .orElse(null);

        var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
            return convertMapToPageDTO(pageVersion);
        }).collect(Collectors.toList());

        return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
                optionalPageVersions.getTotalElements(), validSortAliases);

    }

    //TODO: private method to add all possible page rights in permissions (content, comment, page editor, content mod)
}

package com.yondu.knowledgebase.services.implementations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.page.UserDTO;
import com.yondu.knowledgebase.DTO.page.PageDTO.PageDTOBuilder;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO.PageVersionDTOBuilder;
import com.yondu.knowledgebase.Utils.MultipleSort;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PageVersionRepository;
import com.yondu.knowledgebase.services.PageService;

@Service
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;
    private final PageVersionRepository pageVersionRepository;

    /**
     * @param pageRepository
     * @param pageVersionRepository
     */
    public PageServiceImpl(PageRepository pageRepository, PageVersionRepository pageVersionRepository) {
        this.pageRepository = pageRepository;
        this.pageVersionRepository = pageVersionRepository;
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

    private UserDTO convertToUserDTO(User user) {
        return user != null ? UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build() : null;
    }

    private void lockPage(Page page) {
        var currentTime = LocalDateTime.now();
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isSameUser = page.getLockedBy().getId().equals(currentUser.getId());
        boolean isPageUnlocked = currentTime.isAfter(page.getLockEnd());

        // checked if page can be edited by current user
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
    public PaginatedResponse<PageDTO> findAll(String searchKey, Integer pageNumber, Integer pageSize,
            String[] sortBy) {
        int retrievedPage = Math.max(1, pageNumber);
        var multipleSort = MultipleSort.sortWithOrders(sortBy, new String[] { "modifiedBy,desc" });
        Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(multipleSort));
        searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        var optionalPageVersions = pageVersionRepository
                .findByTitleOrContent(searchKey, false, "approved", paging)
                .orElse(null);

        var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
            return pageDTODefault(pageVersion).build();
        }).collect(Collectors.toList());

        return new PaginatedResponse<PageDTO>(pageList, retrievedPage, pageSize,
                optionalPageVersions.getTotalElements());

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
        newPageVersion.setContent(pageDTO.getContent());
        newPageVersion.setPage(newPage);

        newDirectory.setId(directoryId);

        newPage.setDirectory(newDirectory);
        newPage.getPageVersions().add(newPageVersion); // Add the new PageVersion to

        newPage = pageRepository.save(newPage);

        return pageDTODefault(newPageVersion).build();

    }

    @Override
    public PageDTO updatePageDraft(Long pageId, Long versionId, PageVersionDTO pageDTO) {
        var pageDraft = pageVersionRepository.findByPageIdAndId(pageId, versionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find document"));

        pageDraft.setTitle(pageDTO.getTitle());
        pageDraft.setContent(pageDTO.getContent());

        // lock the page
        lockPage(pageDraft.getPage());

        pageDraft = pageVersionRepository.save(pageDraft);

        return pageDTODefault(pageDraft).build();

    }

    @Override
    public PageDTO deletePage(Long pageId) {
        var pageVersion = pageVersionRepository.findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find document"));
        var page = pageVersion.getPage();
        page.setDeleted(true);
        pageVersion.setPage(pageRepository.save(page));

        return pageDTODefault(pageVersion).deleted(pageVersion.getPage().getDeleted()).build();
    }

    @Override
    public PageDTO updateActiveStatus(Long pageId, Boolean isActive) {
        var pageVersion = pageVersionRepository.findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find document"));
        var page = pageVersion.getPage();
        page.setActive(isActive);
        pageVersion.setPage(pageRepository.save(page));

        return pageDTODefault(pageVersion).build();
    }

    @Override
    public PageDTO updateCommenting(Long pageId, Boolean allowCommenting) {
        var pageVersion = pageVersionRepository.findTopByPageIdAndPageDeletedOrderByDateModifiedDesc(pageId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find document"));
        var page = pageVersion.getPage();
        page.setAllowComment(allowCommenting);
        pageVersion.setPage(pageRepository.save(page));

        return pageDTODefault(pageVersion).build();
    }

    @Override
    public Page getPage(Long id){
        return pageRepository.findById(id).orElseThrow();
    }
}

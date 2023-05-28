package com.yondu.knowledgebase.services.implementations;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.page.UserDTO;
import com.yondu.knowledgebase.Utils.MultipleSort;
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

    @Override
    public PageDTO findById(Long id) {
        var pageVersion = pageVersionRepository
                .findTopByPageIdAndPageDeletedAndReviewsStatusOrderByDateModifiedDesc(id, false, "approved")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource"));

        return PageDTO.builder()
                .dateCreated(pageVersion.getPage().getDateCreated())
                .author(UserDTO.builder()
                        .firstName(pageVersion.getPage().getAuthor().getFirstName())
                        .email(pageVersion.getPage().getAuthor().getEmail())
                        .build())
                .active(pageVersion.getPage().getActive())
                .lockStart(pageVersion.getPage().getLockStart())
                .lockEnd(pageVersion.getPage().getLockEnd())
                .body(
                        PageVersionDTO.builder()
                                .title(pageVersion.getTitle())
                                .content(pageVersion.getContent())
                                .dateModified(pageVersion.getDateModified())
                                .modifiedBy(
                                        UserDTO.builder()
                                                .firstName(pageVersion.getModifiedBy().getFirstName())
                                                .email(pageVersion.getModifiedBy().getEmail())
                                                .build())
                                .build())
                .build();
    }

    @Override
    public PaginatedResponse<PageDTO> findAll(String searchKey, Integer pageNumber, Integer pageSize, String[] sortBy) {
        int retrievedPage = Math.max(1, pageNumber);
        var orderBy = Sort.by(MultipleSort.sortWithOrders(sortBy));
        Pageable paging = orderBy.isEmpty() ? PageRequest.of(retrievedPage - 1, pageSize)
                : PageRequest.of(retrievedPage - 1, pageSize, orderBy);
        searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        var optionalPageVersions = pageVersionRepository
                .findByTitleOrContent(searchKey, paging)
                .orElse(null);

        var pageList = optionalPageVersions.getContent().stream().map(pageVersion -> {
            return PageDTO.builder()
                    .dateCreated(pageVersion.getPage().getDateCreated())
                    .author(UserDTO.builder()
                            .firstName(pageVersion.getPage().getAuthor().getFirstName())
                            .email(pageVersion.getPage().getAuthor().getEmail())
                            .build())
                    .active(pageVersion.getPage().getActive())
                    .lockStart(pageVersion.getPage().getLockStart())
                    .lockEnd(pageVersion.getPage().getLockEnd())
                    .body(
                            PageVersionDTO.builder()
                                    .title(pageVersion.getTitle())
                                    .content(pageVersion.getContent())
                                    .dateModified(pageVersion.getDateModified())
                                    .modifiedBy(
                                            UserDTO.builder()
                                                    .firstName(pageVersion.getModifiedBy().getFirstName())
                                                    .email(pageVersion.getModifiedBy().getEmail())
                                                    .build())
                                    .build())
                    .build();
        }).collect(Collectors.toList());

        return new PaginatedResponse<PageDTO>(pageList, pageNumber, pageSize, optionalPageVersions.getTotalElements());

    }
}

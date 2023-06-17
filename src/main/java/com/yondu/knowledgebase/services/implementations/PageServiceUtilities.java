package com.yondu.knowledgebase.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PageDTO.PageDTOBuilder;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO;
import com.yondu.knowledgebase.DTO.page.PageVersionDTO.PageVersionDTOBuilder;
import com.yondu.knowledgebase.DTO.page.UserDTO;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.entities.Review;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.enums.ReviewStatus;
import com.yondu.knowledgebase.services.UserPermissionValidatorService;

class PageServiceUtilities {
    private final UserPermissionValidatorService userPermissionValidatorService;
    private final AuditorAware<User> auditorAware;

    /**
     * @param userPermissionValidatorService
     * @param auditorAware
     */
    public PageServiceUtilities(UserPermissionValidatorService userPermissionValidatorService,
            AuditorAware<User> auditorAware) {
        this.userPermissionValidatorService = userPermissionValidatorService;
        this.auditorAware = auditorAware;
    }

    private PageVersionDTOBuilder pageVersionBase(PageVersion version, Long[] reviewsCount) {
        return PageVersionDTO.builder()
                .id(version.getId())
                .title(version.getTitle())
                .content(version.getOriginalContent())
                .dateModified(version.getDateModified())
                .totalApprovedReviews(reviewsCount[0])
                .totalDisapprovedReviews(reviewsCount[1])
                .modifiedBy(convertToUserDTO(version.getModifiedBy()));
    }

    private PageDTOBuilder pageDTOBase(PageVersion version) {
        return PageDTO.builder()
                .id(version.getPage().getId())
                .dateCreated(version.getPage().getDateCreated())
                .author(convertToUserDTO(version.getPage().getAuthor()))
                .active(version.getPage().getActive())
                .allowComment(version.getPage().getAllowComment())
                .lockStart(version.getPage().getLockStart())
                .lockEnd(version.getPage().getLockEnd())
                .pageType(version.getPage().getType());
    }

    protected PageDTOBuilder pageDTODefault(PageVersion version) {
        return pageDTOBase(version)
                .body(pageVersionDTODefault(version).build());
    }

    protected PageDTOBuilder pageDTODefault(PageVersion version, Long[] reviewsCount) {
        return pageDTOBase(version).body(pageVersionBase(version, reviewsCount).build());
    }

    protected PageDTOBuilder pageWithVersionsDTODefault(Page page) {
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
                .lockEnd(page.getLockEnd())
                .pageType(page.getType());
    }

    protected PageVersionDTOBuilder pageVersionDTODefault(PageVersion version) {
        var reviewsCount = getReviewsCountByStatus(version);
        return pageVersionBase(version, reviewsCount);
    }

    protected PageDTO convertMapToPageDTO(Map<String, Object> pageVersion) {
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
                        .profilePhoto((String) pageVersion.getOrDefault("authorProfilePhoto", ""))
                        .position((String) pageVersion.getOrDefault("authorPosition", ""))
                        .build())
                .body(PageVersionDTO.builder()
                        .id((Long) pageVersion.getOrDefault("versionId", 0L))
                        .content((String) pageVersion.getOrDefault("versionContent", ""))
                        .title((String) pageVersion.getOrDefault("versionTitle", ""))
                        .dateModified(parseAndFormat(dateModified))
                        .totalApprovedReviews((Long) pageVersion.getOrDefault("totalApprovedReviews", 0L))
                        .totalDisapprovedReviews((Long) pageVersion.getOrDefault("totalDisapprovedReviews", 0L))
                        .modifiedBy(UserDTO.builder()
                                .email((String) pageVersion.getOrDefault("modifiedByEmail", ""))
                                .firstName((String) pageVersion.getOrDefault("modifiedByFirstName", ""))
                                .lastName((String) pageVersion.getOrDefault("modifiedByLastName", ""))
                                .profilePhoto((String) pageVersion.getOrDefault("modifiedByProfilePhoto", ""))
                                .position((String) pageVersion.getOrDefault("modifiedByPosition", ""))
                                .build())
                        .build())
                .categories(getAsArray(pageVersion.getOrDefault("pageCategories", null)))
                .tags(getAsArray(pageVersion.getOrDefault("pageTags", null)))
                .pageType((String) pageVersion.getOrDefault("pageType", "wiki"))
                .build();

    }

    protected UserDTO convertToUserDTO(User user) {
        return user != null ? UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build() : null;
    }

    protected LocalDateTime parseAndFormat(Object date) {
        return date != null && !date.toString().isEmpty()
                ? LocalDateTime.parse(date.toString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))
                : null;
    }

    protected String[] getAsArray(Object concatenatedElements) {
        if (concatenatedElements != null)
            return concatenatedElements.toString().split("\\|");
        return new String[] {};
    }

    protected void lockPage(Page page) {
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

    protected boolean pagePermissionGranted(Long pageId, String permission) {
        Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
        return userPermissionValidatorService.currentUserIsSuperAdmin()
                || userPermissionValidatorService.userHasPagePermission(userId, pageId,
                        permission);
    }

    protected boolean directoryPermissionGranted(Long directoryId, String permission) {
        Long userId = auditorAware.getCurrentAuditor().orElse(new User()).getId();
        return userPermissionValidatorService.currentUserIsSuperAdmin()
                || userPermissionValidatorService.userHasDirectoryPermission(userId, directoryId,
                        permission);
    }

    protected Long[] getReviewsCountByStatus(PageVersion pageVersion) {
        Long totalApproved = 0L;
        Long totalDisapproved = 0L;

        var reviews = new HashSet<>(pageVersion.getReviews());
        for (Review review : reviews) {
            if (review.getStatus().equals(ReviewStatus.APPROVED.getCode())) {
                totalApproved += 1;
            } else if (review.getStatus().equals(ReviewStatus.DISAPPROVED.getCode())) {
                totalDisapproved += 1;
            }
        }

        return new Long[] { totalApproved, totalDisapproved };
    }

    protected String pageNotFoundPhrase(Long id, PageType type) {
        return String.format("%s with an id of %d not found!",
                StringUtils.capitalize(type.getCode().toLowerCase()),
                id);
    }

    protected String pageNotFoundPhrase(Long id, Long versionId, PageType type) {
        return String.format("%s with an id of %d and version id of %d not found!",
                StringUtils.capitalize(type.getCode().toLowerCase()),
                id, versionId);
    }

}

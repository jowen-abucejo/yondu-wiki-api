package com.yondu.knowledgebase.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import com.yondu.knowledgebase.entities.Tag;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.enums.ReviewStatus;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.repositories.TagRepository;
import com.yondu.knowledgebase.services.UserPermissionValidatorService;

class PageServiceUtilities {
    private final UserPermissionValidatorService userPermissionValidatorService;
    private final AuditorAware<User> auditorAware;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    /**
     * @param userPermissionValidatorService
     * @param auditorAware
     * @param categoryRepository
     * @param tagRepository
     */
    public PageServiceUtilities(UserPermissionValidatorService userPermissionValidatorService,
            AuditorAware<User> auditorAware, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.userPermissionValidatorService = userPermissionValidatorService;
        this.auditorAware = auditorAware;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    private PageVersionDTOBuilder pageVersionDTOBase(PageVersion version, Long[] reviewsCount) {
        return PageVersionDTO.builder()
                .id(version.getId())
                .title(version.getTitle())
                .content(version.getOriginalContent())
                .dateModified(version.getDateModified())
                .totalApprovedReviews(reviewsCount[0])
                .totalDisapprovedReviews(reviewsCount[1])
                .isDraft(reviewsCount[2] == 0)
                .modifiedBy(convertToUserDTO(version.getModifiedBy()));
    }

    private PageDTOBuilder pageDTOBase(PageVersion version) {
        return PageDTO.builder()
                .id(version.getPage().getId())
                .dateCreated(version.getPage().getDateCreated())
                .author(convertToUserDTO(version.getPage().getAuthor()))
                .lockedBy(convertToUserDTO(version.getPage().getLockedBy()))
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
        return pageDTOBase(version).body(pageVersionDTOBase(version, reviewsCount).build());
    }

    protected PageDTOBuilder pageWithVersionsDTODefault(Page page) {
        return PageDTO.builder()
                .id(page.getId())
                .versions(page.getPageVersions().stream().map(version -> {
                    return pageVersionDTODefault(version).build();
                }).collect(Collectors.toList()))
                .dateCreated(page.getDateCreated())
                .author(convertToUserDTO(page.getAuthor()))
                .lockedBy(convertToUserDTO(page.getLockedBy()))
                .active(page.getActive())
                .allowComment(page.getAllowComment())
                .lockStart(page.getLockStart())
                .lockEnd(page.getLockEnd())
                .pageType(page.getType());
    }

    protected PageVersionDTOBuilder pageVersionDTODefault(PageVersion version) {
        var reviewsCount = getReviewsCountByStatus(version);
        return pageVersionDTOBase(version, reviewsCount);
    }

    protected PageDTO convertMapToPageDTO(Map<String, Object> pageVersion) {
        return mapToPageDTOBase(pageVersion).build();
    }

    protected PageDTO convertMapToPageDTO(Map<String, Object> page, List<Map<String, Object>> pageVersions) {
        var accessibleVersions = pageVersions.stream().map(version -> {
            return convertMapToPageVersionDTO(version);
        }).collect(Collectors.toList());
        return mapToPageDTOBase(page).versions(accessibleVersions).build();
    }

    private PageDTOBuilder mapToPageDTOBase(Map<String, Object> pageVersion) {
        var dateCreated = pageVersion.getOrDefault("dateCreated", "");
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
                .lockedBy(UserDTO.builder()
                        .email((String) pageVersion.getOrDefault("lockedByEmail", ""))
                        .firstName((String) pageVersion.getOrDefault("lockedByFirstName", ""))
                        .lastName((String) pageVersion.getOrDefault("lockedByLastName", ""))
                        .profilePhoto((String) pageVersion.getOrDefault("lockedByProfilePhoto", ""))
                        .position((String) pageVersion.getOrDefault("lockedByPosition", ""))
                        .build())
                .author(UserDTO.builder()
                        .email((String) pageVersion.getOrDefault("authorEmail", ""))
                        .firstName((String) pageVersion.getOrDefault("authorFirstName", ""))
                        .lastName((String) pageVersion.getOrDefault("authorLastName", ""))
                        .profilePhoto((String) pageVersion.getOrDefault("authorProfilePhoto", ""))
                        .position((String) pageVersion.getOrDefault("authorPosition", ""))
                        .build())
                .body(convertMapToPageVersionDTO(pageVersion))
                .categories(getAsArray(pageVersion.getOrDefault("pageCategories", null)))
                .tags(getAsArray(pageVersion.getOrDefault("pageTags", null)))
                .pageType((String) pageVersion.getOrDefault("pageType", "Wiki"))
                .directoryId((Long) pageVersion.getOrDefault("directoryId", 0L))
                .directoryWorkflowId((Long) pageVersion.getOrDefault("workflowId", 0L))
                .directoryWorkflowStepCount((Long) pageVersion.getOrDefault("workflowStepCount", 0L))
                .directoryName((String) pageVersion.getOrDefault("directoryName", "-----"));
    }

    protected PageVersionDTO convertMapToPageVersionDTO(Map<String, Object> pageVersion) {
        var dateModified = pageVersion.getOrDefault("dateModified", "");
        return PageVersionDTO.builder()
                .id((Long) pageVersion.getOrDefault("versionId", 0L))
                .content((String) pageVersion.getOrDefault("versionContent", ""))
                .title((String) pageVersion.getOrDefault("versionTitle", ""))
                .dateModified(parseAndFormat(dateModified))
                .totalApprovedReviews((Long) pageVersion.getOrDefault("totalApprovedReviews", 0L))
                .totalDisapprovedReviews((Long) pageVersion.getOrDefault("totalDisapprovedReviews", 0L))
                .isDraft(((Long) pageVersion.getOrDefault("totalPendingReviews", 0L)) == 0L)
                .modifiedBy(UserDTO.builder()
                        .email((String) pageVersion.getOrDefault("modifiedByEmail", ""))
                        .firstName((String) pageVersion.getOrDefault("modifiedByFirstName", ""))
                        .lastName((String) pageVersion.getOrDefault("modifiedByLastName", ""))
                        .profilePhoto((String) pageVersion.getOrDefault("modifiedByProfilePhoto", ""))
                        .position((String) pageVersion.getOrDefault("modifiedByPosition", ""))
                        .build())
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
        String formatPattern1 = "yyyy-MM-dd HH:mm:ss.S";
        String formatPattern2 = "yyyy-MM-dd HH:mm:ss.SSSSSS";
        if (date == null || date.toString().isEmpty())
            return null;
        String dateTimeString = date.toString();
        try {
            return LocalDateTime.parse(dateTimeString,
                    DateTimeFormatter.ofPattern(formatPattern1));

        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateTimeString,
                        DateTimeFormatter.ofPattern(formatPattern2));

            } catch (DateTimeParseException er) {
                return null;
            }
        }
    }

    protected String[] getAsArray(Object concatenatedElements) {
        if (concatenatedElements != null)
            return concatenatedElements.toString().split("\\|");
        return new String[] {};
    }

    protected void checkLock(Page page, Boolean lockAfterCheck) {
        var currentTime = LocalDateTime.now();
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isSameUser = page.getLockedBy().getId().equals(currentUser.getId());
        boolean isPageUnlocked = currentTime.isAfter(page.getLockEnd());

        // checked if page can be edit by current user
        if (!isSameUser && !isPageUnlocked)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Document is currently locked");

        if (lockAfterCheck)
            lockPage(page, currentTime, currentUser, isSameUser, isPageUnlocked);
    }

    private void lockPage(Page page, LocalDateTime currentTime, User currentUser, boolean isSameUser,
            boolean isPageUnlocked) {
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
        Long totalPending = 0L;

        var reviews = new HashSet<>(pageVersion.getReviews());
        for (Review review : reviews) {
            if (review.getStatus().equals(ReviewStatus.APPROVED.getCode())) {
                totalApproved += 1;
            } else if (review.getStatus().equals(ReviewStatus.DISAPPROVED.getCode())) {
                totalDisapproved += 1;
            } else if (review.getStatus().equals(ReviewStatus.PENDING.getCode())) {
                totalPending += 1;
            }
        }

        return new Long[] { totalApproved, totalDisapproved, totalPending };
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

    private Set<Tag> getTagsFromArray(String[] tags) {
        var attachedTags = tagRepository.findByNameIn(Arrays.asList(tags));

        for (String tag : tags) {
            var newTag = new Tag();
            newTag.setName(tag);

            if (!attachedTags.contains(newTag)) {
                attachedTags.add(newTag);
            }
        }
        return attachedTags;
    }

    protected String[] setTags(String[] tags, Page newPage) {
        if (tags != null && tags.length > 0) {
            var attachedTags = getTagsFromArray(tags);
            newPage.setTags(attachedTags);
        } else {
            tags = new String[] {};
        }
        return tags;
    }

    protected String[] setCategories(String[] categories, Page newPage) {
        if (categories != null && categories.length > 0) {
            var attachedCategories = categoryRepository.findByNameIn(Arrays.asList(categories));
            newPage.setCategories(attachedCategories);

            categories = attachedCategories.stream().map(c -> c.getName()).toArray(String[]::new);
        } else {
            categories = new String[] {};
        }
        return categories;
    }

    protected PageVersion copyApprovedPageVersion(PageVersion pageDraft) {
        var newVersion = new PageVersion();
        newVersion.setPage(pageDraft.getPage());
        newVersion.setTitle(pageDraft.getTitle());
        newVersion.setContent(pageDraft.getContent());
        newVersion.setOriginalContent(pageDraft.getOriginalContent());
        return newVersion;
    }

    protected void setTitleAndContents(PageVersionDTO pageDTO, PageVersion pageDraft) {
        pageDraft.setTitle(pageDTO.getTitle());
        String pageContent = pageDTO.getContent();
        if (Objects.nonNull(pageContent) && !pageContent.isBlank()) {
            String tags = "";

            if (pageDTO.getTags() != null)
                tags = " Tags| " + String.join(" | ", pageDTO.getTags());

            pageDraft.setContent(
                    pageContent.replaceAll("<[^>]+>", "") + tags);
        }
        pageDraft.setOriginalContent(pageDTO.getContent());
    }

}

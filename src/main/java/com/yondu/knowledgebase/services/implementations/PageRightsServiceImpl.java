package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.*;
import com.yondu.knowledgebase.services.PageRightsService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PageRightsServiceImpl implements PageRightsService {

    /**
     * TODO:
     * get all rights of a page
     * get all page rights separated by page
     *
     * add user to page right,
     * delete user to page right,
     * get page rights of a user
     * get all page rights separated by user
     *
     * add usergroup to page right
     * remove usergroup to page right
     * get page rights of a usergroup
     * get all page rights by user group
     *
     * fetch all pages that a user can access (sort by view, edit access, review
     * access)
     * fetch all pages that a usergroup can access (sort by view, edit access,
     * review access)
     * fetch all users and user group that can access the page
     *
     **/

    private final PageRightsRepository pageRightsRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PageRepository pageRepository;

    public PageRightsServiceImpl(PageRightsRepository pageRightsRepository, UserRepository userRepository,
            PermissionRepository permissionRepository, PageRepository pageRepository) {
        this.pageRightsRepository = pageRightsRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.pageRepository = pageRepository;
    }

    @Override
    public Set<PageRightsDTO.PageRightBaseResponse> getPageRightsOfPage(Long pageId) {

        Page page = pageRepository.findByIdAndActive(pageId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found."));

        Set<PageRights> pageRights = pageRightsRepository.findAllByPage(page);

        return pageRights.stream().map(PageRightsDTOMapper::mapToPageRightResponse).collect(Collectors.toSet());
    }

    @Override
    public void createPageRights(Page page) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<PageRights> savedRights = pageRightsRepository
                .saveAll(permissionRepository
                        .findAllByCategoryOrCategoryOrCategoryOrCategory("Content", "Content Moderation", "Comment",
                                "Page Editor")
                        .stream()
                        .map(obj -> pageRightsRepository.save(new PageRights(page, obj))).toList());

        Set<Rights> updatedRights = new HashSet<>(currentUser.getRights());
        updatedRights.addAll(savedRights);

        currentUser.setRights(updatedRights);
        userRepository.save(currentUser);
    }

    // public boolean hasManagePagePermission(){
    // String email =
    // SecurityContextHolder.getContext().getAuthentication().getName();
    // User currentUser = userRepository.findByEmail(email).orElseThrow(()-> new
    // ResourceNotFoundException("User not found with email: " + email));
    //
    // Long requiredPermission = 26L;
    // Permission permission =
    // permissionRepository.findById(requiredPermission).orElseThrow(()-> new
    // ResourceNotFoundException("Page Permission not found with ID: "+
    // requiredPermission));
    //
    // return currentUser.getPageRights()
    // .stream()
    // .noneMatch(access ->
    // access.getPermission().getId().equals(permission.getId()));
    // }

}

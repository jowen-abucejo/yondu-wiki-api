package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page_rights.PageDTO;
import com.yondu.knowledgebase.DTO.page_rights.PageDTOMapper;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTOMapper;
import com.yondu.knowledgebase.DTO.permission.PermissionDTO;
import com.yondu.knowledgebase.DTO.permission.PermissionDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.AccessDeniedException;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.exceptions.UserException;
import com.yondu.knowledgebase.repositories.*;
import com.yondu.knowledgebase.services.PageRightsService;
import com.yondu.knowledgebase.services.UserPermissionValidatorService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PageRightsServiceImpl implements PageRightsService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PageRepository pageRepository;
    private final GroupRepository groupRepository;
    private final UserPageAccessRepository userPageAccessRepository;
    private final GroupPageAccessRepository groupPageAccessRepository;
    private final UserPermissionValidatorService userPermissionValidatorService;

    public PageRightsServiceImpl(UserRepository userRepository,GroupRepository groupRepository,
                                 PermissionRepository permissionRepository, PageRepository pageRepository,
                                 UserPageAccessRepository userPageAccessRepository,
                                 GroupPageAccessRepository groupPageAccessRepository, UserPermissionValidatorService userPermissionValidatorService) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.pageRepository = pageRepository;
        this.groupRepository = groupRepository;
        this.userPageAccessRepository = userPageAccessRepository;
        this.groupPageAccessRepository = groupPageAccessRepository;
        this.userPermissionValidatorService = userPermissionValidatorService;
    }

    @Override
    public PageRightsDTO.GetUserPageRightBaseResponse addUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email) {

        if (!checkIfManagePermissionExists(pageId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
        }

        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page with PageID "+pageId+" does not exist."));
        Permission permission = permissionRepository.findById(rightsId).orElseThrow(()-> new ResourceNotFoundException("Permission does not exist."));
        User user = userRepository.findByEmail(email.email()).orElseThrow(()-> new ResourceNotFoundException("User with email "+email+" does not exist."));
        UserPageAccess pageAccess = userPageAccessRepository.findByPageAndUserAndPermission(page, user, permission).orElse(null);


        if (pageAccess != null){
            throw new DuplicateResourceException("User already has this permission on this page.");
        } else{
            userPageAccessRepository.save(new UserPageAccess(permission, user, page));
            Set<UserPageAccess> pageRight = userPageAccessRepository.findByUserAndPage(user, page);
            PageRightsDTO.GetPageRightResponse dto = PageRightsDTOMapper.mapToPageRightResponse(page, pageRight.stream()
                    .map(PageRightsDTOMapper::mapToBaseResponse).collect(Collectors.toSet()));
            return PageRightsDTOMapper.mapToUserRightBaseResponse(user, dto);
        }
    }

    @Override
    public PageRightsDTO.GetUserPageRightBaseResponse removeUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email) {

        if (!checkIfManagePermissionExists(pageId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
        }

        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page with PageID "+pageId+" does not exist."));
        Permission permission = permissionRepository.findById(rightsId).orElseThrow(()-> new ResourceNotFoundException("Permission does not exist."));
        User user = userRepository.findByEmail(email.email()).orElseThrow(()-> new ResourceNotFoundException("User with email "+email+" does not exist."));
        UserPageAccess pageAccess = userPageAccessRepository.findByPageAndUserAndPermission(page, user, permission).orElse(null);

        if (pageAccess == null){
            throw new ResourceNotFoundException("User with access on this page right does not exist");
        } else{
            userPageAccessRepository.delete(pageAccess);
            Set<UserPageAccess> pageRight = userPageAccessRepository.findByUserAndPage(user, page);
            PageRightsDTO.GetPageRightResponse dto = PageRightsDTOMapper.mapToPageRightResponse(page, pageRight.stream()
                    .map(PageRightsDTOMapper::mapToBaseResponse).collect(Collectors.toSet()));
            return PageRightsDTOMapper.mapToUserRightBaseResponse(user, dto);
        }

    }

    @Override
    public PageRightsDTO.GetUserPageRightResponse getPageRightsOfUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User does not exist."));

        Set<UserPageAccess> pageRights = userPageAccessRepository.findByUser(user);

        Set<Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>>> resultMapPage = pageRights.stream()
                .collect(Collectors.groupingBy(UserPageAccess::getPage,Collectors.mapping(PageRightsDTOMapper::mapToBaseResponse, Collectors.toSet()))).entrySet();

        Set<PageRightsDTO.GetPageRightResponse> dto = new HashSet<>();
        for (Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>> resultMap : resultMapPage){
            dto.add(PageRightsDTOMapper.mapToPageRightResponse(resultMap.getKey(), resultMap.getValue()));
        }

        return PageRightsDTOMapper.mapToUserRightResponse(user, dto);

    }


    @Override
    public PageRightsDTO.UserGroupBaseResponse addUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId) {

        if (!checkIfManagePermissionExists(pageId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
        }

        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page with PageID "+pageId+" does not exist."));
        Permission permission = permissionRepository.findById(rightsId).orElseThrow(()-> new ResourceNotFoundException("Permission does not exist."));
        Group group = groupRepository.findByIdAndIsActive(groupId.groupId(), true).orElseThrow(()-> new ResourceNotFoundException("User Group does not exist."));
        GroupPageAccess pageAccess = groupPageAccessRepository.findByPageAndGroupAndPermission(page, group, permission).orElse(null);

        if (pageAccess != null){
            throw new DuplicateResourceException("User Group already has access on this page right");
        } else{
            groupPageAccessRepository.save(new GroupPageAccess(permission, group, page));
            Set<GroupPageAccess> pageRight = groupPageAccessRepository.findByGroupAndPage(group, page);
            PageRightsDTO.GetPageRightResponse dto = PageRightsDTOMapper.mapToPageRightResponse(page, pageRight.stream()
                    .map(PageRightsDTOMapper::mapToBaseResponse).collect(Collectors.toSet()));
            return PageRightsDTOMapper.mapToAddPageRightResponse(group, dto);
        }
    }

    @Override
    public PageRightsDTO.UserGroupBaseResponse removeUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId) {

        if (!checkIfManagePermissionExists(pageId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing required permission");
        }

        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page with PageID "+pageId+" does not exist."));
        Permission permission = permissionRepository.findById(rightsId).orElseThrow(()-> new ResourceNotFoundException("Permission does not exist."));
        Group group = groupRepository.findById(groupId.groupId()).orElseThrow(()-> new ResourceNotFoundException("User Group does not exist."));
        GroupPageAccess pageAccess = groupPageAccessRepository.findByPageAndGroupAndPermission(page, group, permission).orElse(null);

        if (pageAccess == null){
            throw new ResourceNotFoundException("User with access on this page right does not exist");
        } else{
            groupPageAccessRepository.delete(pageAccess);
            Set<GroupPageAccess> pageRight = groupPageAccessRepository.findByGroupAndPage(group, page);
            PageRightsDTO.GetPageRightResponse dto = PageRightsDTOMapper.mapToPageRightResponse(page, pageRight.stream()
                    .map(PageRightsDTOMapper::mapToBaseResponse).collect(Collectors.toSet()));
            return PageRightsDTOMapper.mapToAddPageRightResponse(group, dto);
        }

    }

    @Override
    public void createPageRights(Page page) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userPageAccessRepository.saveAll(permissionRepository
                        .findAllByCategoryOrCategoryOrCategoryOrCategory("Content", "Content Moderation", "Comment",
                                "Page Editor")
                        .stream()
                        .map(obj -> userPageAccessRepository.save(new UserPageAccess(obj, currentUser, page))).toList());
    }



    @Override
    public PageRightsDTO.GetUserGroupPageRightResponse getPageRightsOfUserGroup(Long userGroupId) {
        Group group = groupRepository.findById(userGroupId).orElseThrow(()-> new ResourceNotFoundException("User Group does not exist."));
        Set<GroupPageAccess> pageRights = groupPageAccessRepository.findByGroup(group);

        Set<Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>>> resultMapPage = pageRights.stream()
                .collect(Collectors.groupingBy(GroupPageAccess::getPage,Collectors.mapping(PageRightsDTOMapper::mapToBaseResponse, Collectors.toSet()))).entrySet();

        Set<PageRightsDTO.GetPageRightResponse> dto = new HashSet<>();
        for (Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>> resultMap : resultMapPage){
            dto.add(PageRightsDTOMapper.mapToPageRightResponse(resultMap.getKey(), resultMap.getValue()));
        }

        return PageRightsDTOMapper.mapToUserGroupRightResponse(group, dto);
    }


    @Override
    public PageRightsDTO.GetPageRightOfPageResponse getAllUsersOfPage(Long pageId) {
        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(() -> new ResourceNotFoundException("Page with PageID " + pageId + " does not exist."));

        Set<GroupPageAccess> groupAccess = groupPageAccessRepository.findByPage(page);
        Set<UserPageAccess> userAccess = userPageAccessRepository.findByPage(page);


        Set<Map.Entry<Group, Set<PageRightsDTO.RightsPermissionResponse>>> resultMapPage = groupAccess.stream()
                .collect(Collectors.groupingBy(GroupPageAccess::getGroup,Collectors.mapping(PageRightsDTOMapper::mapToBaseResponse, Collectors.toSet()))).entrySet();

        Set<PageRightsDTO.GetPageRightOfGroups> groupDTO = new HashSet<>();
        for (Map.Entry<Group, Set<PageRightsDTO.RightsPermissionResponse>> resultMap : resultMapPage){
            groupDTO.add(PageRightsDTOMapper.mapToPageRightOfGroups(resultMap.getKey(), resultMap.getValue()));
        }

        Set<Map.Entry<User, Set<PageRightsDTO.RightsPermissionResponse>>> resultMapPage2 = userAccess.stream()
                .collect(Collectors.groupingBy(UserPageAccess::getUser,Collectors.mapping(PageRightsDTOMapper::mapToBaseResponse, Collectors.toSet()))).entrySet();

        Set<PageRightsDTO.GetPageRightOfUsers> userDTO = new HashSet<>();
        for (Map.Entry<User, Set<PageRightsDTO.RightsPermissionResponse>> resultMap : resultMapPage2){
            userDTO.add(PageRightsDTOMapper.mapToPageRightOfUsers(resultMap.getKey(), resultMap.getValue()));
        }

        return PageRightsDTOMapper.mapToPageRightsOfPageResponse(page, groupDTO, userDTO );

    }

    @Override
    public PageRightsDTO.GetPageRightResponse getPageRightsOfUserToPage(Long pageId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page page = pageRepository.findById(pageId).orElseThrow(() -> new ResourceNotFoundException("Page not found."));

        Set<UserPageAccess> userPageAccesses = userPageAccessRepository.findByUserAndPage(currentUser, page);

        Set<PageRightsDTO.RightsPermissionResponse> permissions = userPageAccesses
                .stream()
                .map(upa -> new PageRightsDTO.RightsPermissionResponse(upa.getId(), PermissionDTOMapper.mapToBaseResponse(upa.getPermission())))
                .collect(Collectors.toSet());

        PageRightsDTO.GetPageRightResponse response = PageRightsDTOMapper.mapToPageRightResponse(page, permissions);
        return response;
    }

    @Override
    public void checkUserHasRights(Long pageId, Set<PermissionDTO.BaseRequest> permissions) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page page = pageRepository.findById(pageId).orElseThrow(() -> new ResourceNotFoundException("Page not found."));

        Set<UserPageAccess> userPageAccesses = userPageAccessRepository.findByUserAndPage(currentUser, page);
        Set<Permission> fetchedPermissions = permissions
                .stream()
                .map(p -> permissionRepository.findById(p.id()).orElseThrow(() -> new ResourceNotFoundException("Permission not found.")))
                .collect(Collectors.toSet());

        for(Permission p : fetchedPermissions) {
            /**
             * Check if the permission has match.
             */
            boolean hasMatch = userPageAccesses.stream()
                    .anyMatch(upa -> upa.getPermission().getId() == p.getId());
            /**
             * Throws access denied
             * if a single permission is missing.
             */
            if(!hasMatch) throw new AccessDeniedException();
        }
    }

    @Override
    public void checkIfOtherUserHasRights(Long pageId, Long userId, Set<PermissionDTO.BaseRequest> permissions) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new UserException("The user does not exist."));
        Page page = pageRepository.findById(pageId).orElseThrow(() -> new ResourceNotFoundException("Page not found."));

        Set<UserPageAccess> userPageAccesses = userPageAccessRepository.findByUserAndPage(currentUser, page);
        Set<Permission> fetchedPermissions = permissions
                .stream()
                .map(p -> permissionRepository.findById(p.id()).orElseThrow(() -> new ResourceNotFoundException("Permission not found.")))
                .collect(Collectors.toSet());

        for(Permission p : fetchedPermissions) {
            /**
             * Check if the permission has match.
             */
            boolean hasMatch = userPageAccesses.stream()
                    .anyMatch(upa -> upa.getPermission().getId() == p.getId());
            /**
             * Throws access denied
             * if a single permission is missing.
             */
            if(!hasMatch) throw new AccessDeniedException();
        }
    }

    @Override
    public void checkIfGroupHasRights(Long pageId, Long groupId, Set<PermissionDTO.BaseRequest> permissions) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("No group found."));
        Page page = pageRepository.findById(pageId).orElseThrow(() -> new ResourceNotFoundException("Page not found."));

        Set<GroupPageAccess> groupPageAccesses = groupPageAccessRepository.findByGroupAndPage(group, page);
        Set<Permission> fetchedPermissions = permissions
                .stream()
                .map(p -> permissionRepository.findById(p.id()).orElseThrow(() -> new ResourceNotFoundException("Permission not found.")))
                .collect(Collectors.toSet());

        for(Permission p : fetchedPermissions) {
            /**
             * Check if the permission has match.
             */
            boolean hasMatch = groupPageAccesses.stream()
                    .anyMatch(upa -> upa.getPermission().getId() == p.getId());
            /**
             * Throws access denied
             * if a single permission is missing.
             */
            if(!hasMatch) throw new AccessDeniedException();
        }
    }

    private boolean checkIfManagePermissionExists(Long pageId){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPermissionValidatorService.userHasPagePermission(currentUser.getId(), pageId, "MANAGE_PAGE_PERMISSIONS");
    }
}

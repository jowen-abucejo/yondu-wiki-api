package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.group.GroupDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.page_rights.PageDTOMapper;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTO;
import com.yondu.knowledgebase.DTO.page_rights.user_access.PageRightsDTOMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.DuplicateResourceException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.*;
import com.yondu.knowledgebase.services.PageRightsService;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PageRightsServiceImpl implements PageRightsService {

    private final PageRightsRepository pageRightsRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PageRepository pageRepository;
    private final GroupRepository groupRepository;

    public PageRightsServiceImpl(PageRightsRepository pageRightsRepository, UserRepository userRepository,
                                 GroupRepository groupRepository,
            PermissionRepository permissionRepository, PageRepository pageRepository) {
        this.pageRightsRepository = pageRightsRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.pageRepository = pageRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public PageRightsDTO.GetPageRightResponse getPageRightsOfPage(Long pageId) {

        com.yondu.knowledgebase.entities.Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page not found."));

        return convertToPageRightsDTO(page);
    }

    @Override
    public PaginatedResponse<PageRightsDTO.GetPageRightResponse> getAllPageRightsOfPage(int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        List<PageRights> pageRights = pageRightsRepository.findAllGroupByPage(pageRequest).getContent();

        List<PageRightsDTO.GetPageRightResponse> pageRightsList = pageRights.stream().map(pageRight->convertToPageRightsDTO(pageRight.getPage())).toList();

        return new PaginatedResponse<>(pageRightsList, page,size, (long)pageRightsList.size());

    }

    @Override
    public PageRightsDTO.GetUserPageRightBaseResponse addUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email) {

        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page with PageID "+pageId+" does not exist."));
        PageRights pageRights = pageRightsRepository.findByIdAndPage(rightsId, page).orElseThrow(()-> new ResourceNotFoundException("Page Right does not exist."));
        User user = userRepository.findByEmail(email.email()).orElseThrow(()-> new ResourceNotFoundException("User with email "+email+" does not exist."));

        if (user.getRights().contains(pageRights) ){
            throw new DuplicateResourceException("User already has access on this page right");
        } else{
            user.getRights().add(pageRights);
            userRepository.save(user);
            Set<PageRights> pageRight = user.getRights().stream().filter(right -> right instanceof PageRights).map(pRight-> (PageRights) pRight).collect(Collectors.toSet());
            PageRightsDTO.GetPageRightResponse dto = PageRightsDTOMapper.mapToPageRightResponse(page, pageRight.stream().filter(pageR->page.equals(pageR))
                    .map(PageRightsDTOMapper::mapToBaseResponse).collect(Collectors.toSet()));
            return PageRightsDTOMapper.mapToUserRightBaseResponse(user, dto);
        }
    }

    @Override
    public PageRightsDTO.GetUserPageRightBaseResponse removeUserToPageRights(Long pageId, Long rightsId, PageRightsDTO.AddUserRequest email) {
        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page with PageID "+pageId+" does not exist."));
        PageRights pageRights = pageRightsRepository.findByIdAndPage(rightsId, page).orElseThrow(()-> new ResourceNotFoundException("Page Right does not exist."));
        User user = userRepository.findByEmail(email.email()).orElseThrow(()-> new ResourceNotFoundException("User with email "+email+" does not exist."));

        if (user.getRights().contains(pageRights)||pageRights.getUsers().contains(user)){
            user.getRights().remove(pageRights);
            userRepository.save(user);
        } else{
            throw new ResourceNotFoundException("User with access on this page right does not exist");
        }

        Set<PageRights> pageRight = user.getRights().stream().filter(right -> right instanceof PageRights).map(pRight-> (PageRights) pRight).collect(Collectors.toSet());
        PageRightsDTO.GetPageRightResponse dto = PageRightsDTOMapper.mapToPageRightResponse(page, pageRight.stream().filter(pageR->page.equals(pageR))
                .map(PageRightsDTOMapper::mapToBaseResponse).collect(Collectors.toSet()));
        return PageRightsDTOMapper.mapToUserRightBaseResponse(user, dto);

    }

    @Override
    public PageRightsDTO.GetUserPageRightResponse getPageRightsOfUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User does not exist."));

        Set<Rights> rights = userRepository.findRightsById(userId);

        Set<PageRights> pageRights = rights.stream().filter(right -> right instanceof PageRights).map(pageRight-> (PageRights) pageRight).collect(Collectors.toSet());

        Set<Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>>> resultMapPage = pageRights.stream()
                .collect(Collectors.groupingBy(PageRights::getPage,Collectors.mapping(PageRightsDTOMapper::mapToBaseResponse, Collectors.toSet()))).entrySet();

        Set<PageRightsDTO.GetPageRightResponse> dto = new HashSet<>();
        for (Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>> resultMap : resultMapPage){
            dto.add(PageRightsDTOMapper.mapToPageRightResponse(resultMap.getKey(), resultMap.getValue()));
        }

        return PageRightsDTOMapper.mapToUserRightResponse(user, dto);

    }


    @Override
    public PageRightsDTO.UserGroupBaseResponse addUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId) {
        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page with PageID "+pageId+" does not exist."));
        PageRights pageRights = pageRightsRepository.findByIdAndPage(rightsId, page).orElseThrow(()-> new ResourceNotFoundException("Page Right does not exist."));
        Group group = groupRepository.findById(groupId.groupId()).orElseThrow(()-> new ResourceNotFoundException("User Group does not exist."));

        if (group.getRights().contains(pageRights)){
            throw new DuplicateResourceException("User Group already has access on this page right");
        } else{
            group.getRights().add(pageRights);
            groupRepository.save(group);

            Set<PageRights> pageRight = group.getRights().stream().filter(right -> right instanceof PageRights).map(pRight-> (PageRights) pRight).collect(Collectors.toSet());

        PageRightsDTO.GetPageRightResponse dto = PageRightsDTOMapper.mapToPageRightResponse(page, pageRight.stream().filter(pageR->page.equals(pageR.getPage()))
                .map(PageRightsDTOMapper::mapToBaseResponse).collect(Collectors.toSet()));
        return PageRightsDTOMapper.mapToAddPageRightResponse(group, dto);
        }
    }

    @Override
    public PageRightsDTO.UserGroupBaseResponse removeUserGroupToPageRights(Long pageId, Long rightsId, GroupDTO.AddRightsRequest groupId) {
        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(()-> new ResourceNotFoundException("Page with PageID "+pageId+" does not exist."));
        PageRights pageRights = pageRightsRepository.findByIdAndPage(rightsId, page).orElseThrow(()-> new ResourceNotFoundException("Page Right does not exist."));
        Group group = groupRepository.findById(groupId.groupId()).orElseThrow(()-> new ResourceNotFoundException("User Group does not exist."));

        if (group.getRights().contains(pageRights)){
            group.getRights().remove(pageRights);
            groupRepository.save(group);

            Set<PageRights> pageRight = group.getRights().stream().filter(right -> right instanceof PageRights).map(pRight-> (PageRights) pRight).collect(Collectors.toSet());

            PageRightsDTO.GetPageRightResponse dto = PageRightsDTOMapper.mapToPageRightResponse(page, pageRight.stream().filter(pageR->page.equals(pageR.getPage()))
                    .map(PageRightsDTOMapper::mapToBaseResponse).collect(Collectors.toSet()));
            return PageRightsDTOMapper.mapToAddPageRightResponse(group, dto);
        } else{
            throw new DuplicateResourceException("User Group already has access on this page right");
        }
    }


    private PageRightsDTO.GetPageRightResponse convertToPageRightsDTO(com.yondu.knowledgebase.entities.Page page){
        Set<PageRights> pageRights = pageRightsRepository.findAllByPage(page);

        Set<PageRightsDTO.RightsPermissionResponse> pageRightsDTO = pageRights.stream()
                .map(PageRightsDTOMapper::mapToBaseResponse)
                .collect(Collectors.toSet());

        return new PageRightsDTO.GetPageRightResponse(PageDTOMapper.mapToBaseResponse(page), pageRightsDTO);
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



    @Override
    public PageRightsDTO.GetUserGroupPageRightResponse getPageRightsOfUserGroup(Long userGroupId) {
        Group group = groupRepository.findById(userGroupId).orElseThrow(()-> new ResourceNotFoundException("User Group does not exist."));


        Set<Rights> rights = groupRepository.findRightsById(userGroupId);


        Set<PageRights> pageRights = rights.stream().filter(right -> right instanceof PageRights).map(pageRight-> (PageRights) pageRight).collect(Collectors.toSet());


        Set<Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>>> resultMapPage = pageRights.stream()
                .collect(Collectors.groupingBy(PageRights::getPage,Collectors.mapping(PageRightsDTOMapper::mapToBaseResponse, Collectors.toSet()))).entrySet();


        Set<PageRightsDTO.GetPageRightResponse> dto = new HashSet<>();
        for (Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>> resultMap : resultMapPage){
            dto.add(PageRightsDTOMapper.mapToPageRightResponse(resultMap.getKey(), resultMap.getValue()));
        }


        return PageRightsDTOMapper.mapToUserGroupRightResponse(group, dto);
    }


    @Override
    public PageRightsDTO.GetPageRightOfPageResponse getAllUsersOfPage(Long pageId) {
        Page page = pageRepository.findByIdAndActive(pageId, true).orElseThrow(() -> new ResourceNotFoundException("Page with PageID " + pageId + " does not exist."));
//        Set<Rights> rights = groupRepository.findRightsById(userGroupId);
//
//        Set<PageRights> pageRights = rights.stream().filter(right -> right instanceof PageRights).map(pageRight-> (PageRights) pageRight).collect(Collectors.toSet());
//
//        Set<Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>>> resultMapPage = pageRights.stream()
//                .collect(Collectors.groupingBy(PageRights::getPage,Collectors.mapping(PageRightsDTOMapper::mapToBaseResponse, Collectors.toSet()))).entrySet();
//
//        Set<PageRightsDTO.GetPageRightResponse> dto = new HashSet<>();
//        for (Map.Entry<Page, Set<PageRightsDTO.RightsPermissionResponse>> resultMap : resultMapPage){
//            dto.add(PageRightsDTOMapper.mapToPageRightResponse(resultMap.getKey(), resultMap.getValue()));
//        }
        return null;
    }

}

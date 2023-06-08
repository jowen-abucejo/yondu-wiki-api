package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.comment.*;
import com.yondu.knowledgebase.DTO.email.EmailRequestDTO;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private  final UserRepository userRepository;
    private final PageRepository pageRepository;
    private final PostRepository postRepository;
    private final EmailServiceImpl emailService;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PageRepository pageRepository, PostRepository postRepository, EmailServiceImpl emailService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.pageRepository = pageRepository;
        this.postRepository = postRepository;
        this.emailService = emailService;
    }

    @Override
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, Long parentCommentId) {
        User user = userRepository.findById(commentRequestDTO.getUserId()).orElseThrow(()->new ResourceNotFoundException(String.format("User ID not found: %d", commentRequestDTO.getUserId())));
        if (commentRequestDTO.getEntityType().equals("PAGE")){
            pageRepository.findById(commentRequestDTO.getEntityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Page ID not found: %d", commentRequestDTO.getEntityId())));
        }else if (commentRequestDTO.getEntityType().equals("POST")){
            postRepository.findById(commentRequestDTO.getEntityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Post ID not found: %d", commentRequestDTO.getEntityId())));
        }

        LocalDateTime currentDate = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setDateCreated(currentDate);
        comment.setComment(commentRequestDTO.getComment());
        comment.setEntityId(commentRequestDTO.getEntityId());
        comment.setEntityType(commentRequestDTO.getEntityType());
        comment.setUser(user);
        comment.setCommentMentions(getMentionedUsers(commentRequestDTO.getComment()));

        if(parentCommentId != null){
            if(commentRepository.existsById(parentCommentId)){
                comment.setParentCommentId(parentCommentId);
                //Notify the User of the Parent Comment being replied on
                Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found : %d",parentCommentId)));
                User parentCommentUser = userRepository.findById(parentComment.getUser().getId()).orElseThrow(()->new ResourceNotFoundException(String.format("User ID not found : %d",parentComment.getUser().getId())));
                emailService.sendEmail(new EmailRequestDTO(parentCommentUser.getEmail(),"COMMENT","Comment","http://localhost:8080/comments/1"));
            }else{
                throw new ResourceNotFoundException(String.format("Comment ID not found: %d",parentCommentId));
            }
        }

        commentRepository.save(comment);
        return mapToCommentResponseDTO(comment);
    }

    @Override
    public List<CommentResponseDTO> getAllComments(String entity, Long id) {
        List<Comment> comments = commentRepository.findByEntityTypeAndEntityId(entity,id);
        List <CommentResponseDTO> commentResponseList = new ArrayList<>();

        for (Comment comment : comments){
            CommentResponseDTO commentResponseDTO = mapToCommentResponseDTO(comment);
            commentResponseList.add(commentResponseDTO);
        }
        return commentResponseList;
    }

    @Override
    public CommentCountResponseDTO getTotalComments(String entity, Long id) {
        Long totalCommentCount = commentRepository.countByEntityTypeAndEntityId(entity,id);
        CommentCountResponseDTO commentCountResponseDTO = new CommentCountResponseDTO(id,entity,totalCommentCount);
        return commentCountResponseDTO;
    }

    @Override
    public CommentResponseDTO getComment (Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found: %d", commentId)));
        return mapToCommentResponseDTO(comment);
    }

    private CommentResponseDTO mapToCommentResponseDTO(Comment comment) {
        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setId(comment.getId());
        commentResponseDTO.setDate(comment.getDateCreated());
        commentResponseDTO.setComment(comment.getComment());
        commentResponseDTO.setUserId(comment.getUser().getId());
        commentResponseDTO.setEntityId(comment.getEntityId());
        commentResponseDTO.setEntityType(comment.getEntityType());
        commentResponseDTO.setTotalReplies(commentRepository.countAllReplies(comment.getId()));
        commentResponseDTO.setCommentMentions(mapToMentionedUserResponseDTO(comment.getComment()));

        //Fetch Replies
        List<Comment> comments = commentRepository.findAllCommentReplies(comment.getEntityType(),comment.getEntityId(),comment.getId());
        List <CommentDTO> commentReplies = comments.stream().map(reply ->
        {
            CommentDTO replyDTO = new CommentDTO();
            replyDTO.setId(reply.getId());
            replyDTO.setDate(reply.getDateCreated());
            replyDTO.setComment(reply.getComment());
            replyDTO.setUserId(reply.getUser().getId());
            replyDTO.setCommentMentions(mapToMentionedUserResponseDTO(reply.getComment()));
            return replyDTO;
        }).collect(Collectors.toList());
        commentResponseDTO.setReplies(commentReplies);

        return commentResponseDTO;
    }

    private Set<User> getMentionedUsers(String comment){
        Set<String> mentionedUsernames = parseMentions(comment);
        Set<User> mentionedUsers = new HashSet<>();
        for (String name : mentionedUsernames) {
            User user = userRepository.findByFirstNameOrLastName(name,name);
            if (user != null) {
                mentionedUsers.add(user);
            }
        }
        return mentionedUsers;
    }

    private Set<String> parseMentions (String comment){
        Set<String> mentionedUsers = new HashSet<>();
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(comment);

        while (matcher.find()) {
            String mentionedUser = matcher.group(1);
            mentionedUsers.add(mentionedUser);
        }
        return mentionedUsers;
    }

    private Set<MentionedUserResponseDTO> mapToMentionedUserResponseDTO (String comment){
        Set <User> mentionedUsers = getMentionedUsers(comment);
        //Fetch all mentioned users
        Set<MentionedUserResponseDTO> userDTOS = new HashSet<>();
        for (User user : mentionedUsers){
            MentionedUserResponseDTO mentionedUserResponseDTO = new MentionedUserResponseDTO();
            String name = user.getFirstName() + " " + user.getLastName();
            mentionedUserResponseDTO.setId(user.getId());
            mentionedUserResponseDTO.setName(name);
            mentionedUserResponseDTO.setEmail(user.getEmail());
            userDTOS.add(mentionedUserResponseDTO);
        }
        return userDTOS;
    }
}

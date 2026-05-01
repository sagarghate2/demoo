package com.starto.service;

import com.starto.model.Comment;
import com.starto.model.Signal;
import com.starto.model.User;
import com.starto.model.NearbySpace;
import com.starto.repository.CommentRepository;
import com.starto.repository.SignalRepository;
import com.starto.repository.NearbySpaceRepository;

import lombok.RequiredArgsConstructor;
import com.starto.dto.CommentResponseDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final SignalRepository signalRepository;
    private final NearbySpaceRepository nearbySpaceRepository; 
    private final NotificationService notificationService;

    //  add top level comment
    @Transactional
    public CommentResponseDTO addComment(User user, UUID postId, String content) {

        //  Step 1: check Signal
        Signal signal = signalRepository.findById(postId).orElse(null);

        if (signal != null) {
            Comment comment = Comment.builder()
                    .signal(signal)
                    .user(user)
                    .username(user.getUsername())
                    .content(content)
                    .parentId(null)
                    .build();

            Comment saved = commentRepository.save(comment);

            // Increment signal response count
            signal.setResponseCount((signal.getResponseCount() != null ? signal.getResponseCount() : 0) + 1);
            signalRepository.save(signal);

            // Only notify signal owner if commenter is a different person - use toString() for safety
            if (!user.getId().toString().equals(signal.getUser().getId().toString())) {
                notificationService.send(
                        signal.getUser().getId(),
                        "NEW_COMMENT",
                        "New Comment",
                        user.getName() + " commented on your signal: " + signal.getTitle(),
                        Map.of("signalId", signal.getId().toString())
                );
            }

            return toDTO(saved);
        }

        //  Step 2: check NearbySpace
        NearbySpace space = nearbySpaceRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .nearbySpace(space) 
                .user(user)
                .username(user.getUsername())
                .content(content)
                .parentId(null)
                .build();

        Comment saved = commentRepository.save(comment);

        // Increment space response count
        space.setResponseCount((space.getResponseCount() != null ? space.getResponseCount() : 0) + 1);
        nearbySpaceRepository.save(space);

        // Notify space owner if commenter is different person
        if (!user.getId().toString().equals(space.getUser().getId().toString())) {
            notificationService.send(
                    space.getUser().getId(),
                    "NEW_COMMENT",
                    "New Comment",
                    user.getName() + " commented on your space: " + space.getName(),
                    Map.of("spaceId", space.getId().toString())
            );
        }

        return toDTO(saved);
    }

    //  reply to a comment
    @Transactional
    public CommentResponseDTO addReply(User user, UUID postId, UUID parentId, String content) {

        //  Step 1: check Signal
        Signal signal = signalRepository.findById(postId).orElse(null);

        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (signal != null) {
            Comment reply = Comment.builder()
                    .signal(signal)
                    .user(user)
                    .username(user.getUsername())
                    .content(content)
                    .parentId(parentId)
                    .build();

            Comment saved = commentRepository.save(reply);

            // Increment signal response count
            signal.setResponseCount((signal.getResponseCount() != null ? signal.getResponseCount() : 0) + 1);
            signalRepository.save(signal);

            // Notify parent comment owner if replier is different person
            if (!user.getId().toString().equals(parentComment.getUser().getId().toString())) {
                notificationService.send(
                        parentComment.getUser().getId(),
                        "REPLY",
                        "New Reply",
                        user.getName() + " replied to your comment on: " + signal.getTitle(),
                        Map.of("signalId", signal.getId().toString())
                );
            }

            return toDTO(saved);
        }

        //  Step 2: check NearbySpace
        NearbySpace space = nearbySpaceRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment reply = Comment.builder()
                .nearbySpace(space)
                .user(user)
                .username(user.getUsername())
                .content(content)
                .parentId(parentId)
                .build();

        Comment saved = commentRepository.save(reply);

        // Increment space response count
        space.setResponseCount((space.getResponseCount() != null ? space.getResponseCount() : 0) + 1);
        nearbySpaceRepository.save(space);

        // Notify parent comment owner if replier is different person
        if (!user.getId().toString().equals(parentComment.getUser().getId().toString())) {
            notificationService.send(
                    parentComment.getUser().getId(),
                    "REPLY",
                    "New Reply",
                    user.getName() + " replied to your comment on space: " + space.getName(),
                    Map.of("spaceId", space.getId().toString())
            );
        }

        return toDTO(saved);
    }

    //  delete comment
    @Transactional
    public void deleteComment(User user, UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: not your comment");
        }

        Signal signal = comment.getSignal();
        NearbySpace space = comment.getNearbySpace();

        int totalDeleted = countAllReplies(comment) + 1;

        commentRepository.delete(comment);

        //  update signal response count
        if (signal != null) {
            signal.setResponseCount(Math.max(0, (signal.getResponseCount() != null ? signal.getResponseCount() : 0) - totalDeleted));
            signalRepository.save(signal);
        }

        // update space response count
        if (space != null) {
            space.setResponseCount(Math.max(0, (space.getResponseCount() != null ? space.getResponseCount() : 0) - totalDeleted));
            nearbySpaceRepository.save(space);
        }
    }

    //  DTO conversion
    private CommentResponseDTO toDTO(Comment comment) {
        // Since avatarUrl is a formula, it might be null on a newly saved object.
        // We can get it from the user object if available.
        String avatar = comment.getAvatarUrl();
        if (avatar == null && comment.getUser() != null) {
            avatar = comment.getUser().getAvatarUrl();
        }
        
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .signalId(comment.getSignalId())
                .spaceId(comment.getSpaceId())
                .userId(comment.getUser() != null ? comment.getUser().getId() : comment.getUserId())
                .username(comment.getUsername())
                .avatarUrl(avatar)
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .createdAt(comment.getCreatedAt())
                .replies(comment.getReplies() != null
                        ? comment.getReplies().stream().map(this::toDTO).toList()
                        : List.of())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getComments(UUID postId) {
        List<Comment> comments = commentRepository.findBySignalIdAndParentIdIsNullOrderByCreatedAtDesc(postId);
        if (comments.isEmpty()) {
            comments = commentRepository.findBySpaceIdAndParentIdIsNullOrderByCreatedAtDesc(postId);
        }
        return comments.stream()
                .map(this::toDTO)
                .toList();
    }

    private int countAllReplies(Comment comment) {
        if (comment.getReplies() == null || comment.getReplies().isEmpty()) return 0;
        int count = comment.getReplies().size();
        for (Comment reply : comment.getReplies()) {
            count += countAllReplies(reply);
        }
        return count;
    }
}
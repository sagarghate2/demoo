package com.starto.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starto.model.Signal;
import com.starto.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_id", nullable = true)
    private Signal signal;

    @Column(name = "signal_id", insertable = false, updatable = false)
    private UUID signalId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = true)
    private NearbySpace nearbySpace;

    @Column(name = "space_id", insertable = false, updatable = false)
    private UUID spaceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    @Column(name = "username")
    private String username;

    @org.hibernate.annotations.Formula("(SELECT u.avatar_url FROM users u WHERE u.id = user_id)")
    private String avatarUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //  null = top level comment, has value = reply to a comment
    @Column(name = "parent_id")
    private UUID parentId;

    //  load replies automatically
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    private List<Comment> replies;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
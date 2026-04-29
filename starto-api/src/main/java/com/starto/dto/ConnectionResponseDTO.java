package com.starto.dto;

import com.starto.model.Connection;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ConnectionResponseDTO {

    private UUID id;
    private String status;
    private String message;
    private OffsetDateTime createdAt;

    // Nested user info — avoid exposing full User entity
    private UUID requesterId;
    private String requesterName;
    private String requesterUsername;
    private String requesterAvatarUrl;
    private String requesterRole;

    private UUID receiverId;
    private String receiverName;
    private String receiverUsername;
    private String receiverAvatarUrl;
    private String receiverRole;

    private UUID signalId; // nullable if from profile
    private UUID spaceId;

    public static ConnectionResponseDTO from(Connection connection) {
        return ConnectionResponseDTO.builder()
                .id(connection.getId())
                .status(connection.getStatus())
                .message(connection.getMessage())
                .createdAt(connection.getCreatedAt())
                // Use formula fields if available, otherwise fallback to lazy-loaded entity
                .requesterId(connection.getRequesterId())
                .requesterName(connection.getRequesterName() != null ? connection.getRequesterName() : (connection.getRequester() != null ? connection.getRequester().getName() : null))
                .requesterUsername(connection.getRequesterUsername() != null ? connection.getRequesterUsername() : (connection.getRequester() != null ? connection.getRequester().getUsername() : null))
                .requesterAvatarUrl(connection.getRequesterAvatarUrl() != null ? connection.getRequesterAvatarUrl() : (connection.getRequester() != null ? connection.getRequester().getAvatarUrl() : null))
                .requesterRole(connection.getRequesterRole() != null ? connection.getRequesterRole() : (connection.getRequester() != null ? connection.getRequester().getRole() : null))
                
                .receiverId(connection.getReceiverId())
                .receiverName(connection.getReceiverName() != null ? connection.getReceiverName() : (connection.getReceiver() != null ? connection.getReceiver().getName() : null))
                .receiverUsername(connection.getReceiverUsername() != null ? connection.getReceiverUsername() : (connection.getReceiver() != null ? connection.getReceiver().getUsername() : null))
                .receiverAvatarUrl(connection.getReceiverAvatarUrl() != null ? connection.getReceiverAvatarUrl() : (connection.getReceiver() != null ? connection.getReceiver().getAvatarUrl() : null))
                .receiverRole(connection.getReceiverRole() != null ? connection.getReceiverRole() : (connection.getReceiver() != null ? connection.getReceiver().getRole() : null))
                
                .signalId(connection.getSignalId())
                .spaceId(connection.getSpaceId())
                .build();
    }
}
package com.starto.dto;

import com.starto.model.Offer;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class OfferResponseDTO {

    private UUID id;
    private UUID requesterId;
    private String requesterName;
    private String requesterAvatarUrl;
    private String requesterRole;

    private UUID receiverId;
    private String receiverName;
    private String receiverAvatarUrl;

    private UUID signalId;
    private String signalTitle;

    private String requesterUsername;
    private String organizationName;
    private String portfolioLink;
    private String message;
    private String status;
    private OffsetDateTime createdAt;

    public static OfferResponseDTO from(Offer offer) {
        return OfferResponseDTO.builder()
                .id(offer.getId())
                .signalId(offer.getSignal().getId())
                .signalTitle(offer.getSignal().getTitle())
                .requesterId(offer.getRequester().getId())
                .requesterUsername(offer.getRequester().getUsername())
                .requesterName(offer.getRequester().getName())
                .requesterAvatarUrl(offer.getRequester().getAvatarUrl())
                .requesterRole(offer.getRequester().getRole())
                .receiverId(offer.getReceiver().getId())
                .receiverName(offer.getReceiver().getName())
                .receiverAvatarUrl(offer.getReceiver().getAvatarUrl())
                .organizationName(offer.getOrganizationName())
                .portfolioLink(offer.getPortfolioLink())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .createdAt(offer.getCreatedAt())
                .build();
    }
}
package com.starto.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ConnectionRequestDTO {
    private UUID receiverId;
    private UUID signalId;
    private UUID spaceId;
    private String message;
}
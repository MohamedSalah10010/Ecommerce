package com.learn.ecommerce.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder

@Schema(description = "Standard error response")
public class ErrorResponseDTO {

    @Schema(example = "UNAUTHORIZED")
    private HttpStatus errorStatus;

    @Schema(example = "Invalid username or password")
    private String errorMessage;

    @Schema(example = "uri=/auth/login")
    private String errorDescription;

    @Schema(example = "2025-01-01T12:00:00")
    private LocalDateTime errorTimestamp;
}
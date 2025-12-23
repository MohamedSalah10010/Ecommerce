package com.learn.ecommerce.DTO;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder

public class ErrorResponseDTO {

    private HttpStatus errorStatus;
    private String errorDescription;
    private String errorMessage;
    private LocalDateTime errorTimestamp;

}

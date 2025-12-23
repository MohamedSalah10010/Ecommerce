package com.learn.ecommerce.exceptionhandler;


import com.learn.ecommerce.DTO.ErrorResponseDTO;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Builder
@RequiredArgsConstructor
@Getter
@AllArgsConstructor
@Setter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = -741215074424755266L;

    private ErrorResponseDTO errorResponseDTO;
    public ApplicationException(Throwable e) {
        super(e);
    }

}

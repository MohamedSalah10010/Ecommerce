package com.learn.ecommerce.exceptionhandler;


import com.learn.ecommerce.DTO.ErrorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Builder
@AllArgsConstructor
@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = -741215074424755266L;

    private ErrorResponseDTO errorResponseDTO;
    public ApplicationException(Throwable e) {
        super(e);
    }

}

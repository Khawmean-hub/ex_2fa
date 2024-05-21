package com.beple.ex_2fa.exception;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.beple.ex_2fa.payload.BaseResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.Arrays;


@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(CustomException.class)
    public Object customerException(CustomException ex){

        return BaseResponse.builder()
                .message(ex.getMessage())
                .code(ex.getCause().getMessage())
                .isError(true)
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    public Object authenticationException(AuthenticationException ex){

        return BaseResponse.builder()
                .message(ex.getMessage())
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .isError(true)
                .build();
    }

    //Enum validation
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleJsonErrors(HttpMessageNotReadableException ex){

        String message = ex.getMessage();
        if (ex.getCause() != null && ex.getCause() instanceof InvalidFormatException) {
            message = ((InvalidFormatException) ex.getCause()).getPath().get(0).getFieldName();
            message += ": '" + ((InvalidFormatException) ex.getCause()).getValue();
            message += "'는 다음 " + Arrays.toString(((InvalidFormatException) ex.getCause()).getTargetType().getEnumConstants())+"가 아닙니다";
        }
        return BaseResponse.builder()
                .message(message)
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .isError(true)
                .build();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public Object illegalArgumentHandle(IllegalArgumentException ex){
        return BaseResponse.builder()
                .message(ex.getMessage())
                .isError(true)
                .build();
    }

    @ExceptionHandler (ConstraintViolationException.class)
    public Object constraintViolationHandle(ConstraintViolationException ex){
        return BaseResponse.builder()
                .message(ex.getMessage())
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .isError(true)
                .build();
    }

    @ExceptionHandler(Throwable.class)
    public BaseResponse globalExceptionHandler(Throwable ex) {
        return BaseResponse.builder()
                .message(ex.getCause().getMessage())
                .code("500")
                .isError(true)
                .build();
    }

    //Json Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException ex){

        StringBuilder message = new StringBuilder();
        for(int i=0; i<ex.getBindingResult().getFieldErrors().size(); i++){
            message.append("[").append(ex.getBindingResult().getFieldErrors().get(i).getField()).append("]").append(ex.getBindingResult().getFieldErrors().get(i).getDefaultMessage());
            if(i != ex.getBindingResult().getFieldErrors().size()-1){
                message.append(", ");
            }
        }

        return BaseResponse.builder()
                .message(message.toString())
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .isError(true)
                .build();
    }

}

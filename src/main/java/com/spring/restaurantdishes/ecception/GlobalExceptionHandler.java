package com.spring.restaurantdishes.ecception;

import com.spring.restaurantdishes.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handValidation(MethodArgumentNotValidException exception){
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数错误或参数校验失败" : fieldError.getField() + ": " + fieldError.getDefaultMessage();
        return ResponseEntity.status(400).body(Result.error(400,message));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleUnreadableJson() {
        return ResponseEntity
                .status(400)
                .body(Result.error(400, "请求体不是有效的JSON"));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKey() {
        return ResponseEntity
                .status(409)
                .body(Result.error(409, "数据冲突"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception exception) {
        log.error("服务器内部错误", exception);

        return ResponseEntity
                .status(500)
                .body(Result.error(500, "服务器内部错误"));
    }

}

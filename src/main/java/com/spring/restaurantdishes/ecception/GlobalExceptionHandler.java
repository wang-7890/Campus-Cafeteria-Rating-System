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
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

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

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Result<Void>> handleMethodValidation(HandlerMethodValidationException exception) {
        List<ParameterValidationResult> results = exception.getAllValidationResults();
        if (results.isEmpty() || results.get(0).getResolvableErrors().isEmpty()) {
            return badRequest("参数错误或参数校验失败");
        }
        ParameterValidationResult first = results.get(0);
        String parameter = first.getMethodParameter().getParameterName();
        String message = (parameter == null ? "参数" : parameter) + ": "
                + first.getResolvableErrors().get(0).getDefaultMessage();
        return badRequest(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return badRequest("参数格式不正确");
    }

    private ResponseEntity<Result<Void>> badRequest(String message) {
        return ResponseEntity
                .status(400)
                .body(Result.error(400, message));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception exception) {
        log.error("服务器内部错误", exception);

        return ResponseEntity
                .status(500)
                .body(Result.error(500, "服务器内部错误"));
    }

}

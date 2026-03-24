package com.haru.LogMe.global.exception;

import com.haru.LogMe.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice //모든 @RestController의 예외를 처리.
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("CustomException occurred: {}", e.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * JSON 파싱 오류 처리 (Enum 타입 불일치 등)
     * 잘못된 Enum 값이 들어오면 500 대신 400(INVALID_INPUT)을 반환합니다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException occurred: {}", e.getMessage());

        // ErrorCode.INVALID_INPUT(C002)을 사용하여 응답
        return ResponseEntity
                .status(ErrorCode.INVALID_EMOTION_TYPE.getHttpStatus())
                .body(ApiResponse.error(ErrorCode.INVALID_EMOTION_TYPE));
    }

    // @Valid 유효성 검사 실패 시 처리 -> 나중에 필요하면 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        log.warn("Validation failed: {}", errorMessage);

        ApiResponse<?> apiResponse = ApiResponse.error(ErrorCode.INVALID_INPUT.getCode(), errorMessage);

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus()) // ErrorCode의 HttpStatus 사용
                .body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception e) {

        // 어떤 에러인지 콘솔에 로그 출력
        log.error("Unhandled Exception occurred: ", e);

        // 사용자에겐 "서버 내부 오류"로 통일하여 응답
        ApiResponse<?> errorResponse = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}

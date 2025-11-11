package com.haru.LogMe.global.response;

import com.haru.LogMe.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final Error error;

    public ApiResponse(boolean success, T data, Error error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    // 성공 응답 생성 메서드
    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(true, data, null);
    }

    // 실패 응답 생성 메서드
    public static <T> ApiResponse<T> error(ErrorCode errorCode){
        return new ApiResponse<>(false, null, new Error(errorCode));
    }

    public static <T> ApiResponse<T> error(String code, String message){
        return new ApiResponse<>(false, null, new Error(code,message));
    }

    @Getter
    public static class Error {
        private final String code;
        private final String message;

        // ErrorCode로부터 에러 정보 설정
        public Error(ErrorCode errorCode) {
            this.code = errorCode.getCode();
            this.message = errorCode.getMessage();
        }

        // 직접 코드와 메시지로 에러 정보 설정 -> 유효성 검사 실패 등
        public Error(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

}

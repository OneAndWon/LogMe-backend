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

    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode){
        return new ApiResponse<>(false, null, new Error(errorCode));
    }

    @Getter
    public static class Error {
        private final String code;
        private final String message;

        public Error(ErrorCode errorCode) {
            this.code = errorCode.getCode();
            this.message = errorCode.getMessage();
        }
    }

}

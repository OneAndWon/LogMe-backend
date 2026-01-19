package com.haru.LogMe.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor //final 필드만 받는 생성자
public enum ErrorCode {
    // === 공통 에러 (Common) ===
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C002", "입력값이 유효하지 않습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "파일 업로드 중 오류가 발생했습니다."),

    // === 인증/인가 (Auth) ===
    INVALID_ACCESSTOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 접근입니다."),
    UNAUTHORIZED_USER(HttpStatus.FORBIDDEN, "A002", "권한이 없는 사용자입니다."),

    // === 사용자 (User) ===
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),

    // === 투두 (Todo) ===
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "해당 할 일을 찾을 수 없습니다."),
    SELF_PARENT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "T002", "자기 자신을 상위 할 일로 지정할 수 없습니다."),
    PARENT_TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "T003", "상위 할 일을 찾을 수 없습니다."),
    TODO_TITLE_EMPTY(HttpStatus.BAD_REQUEST, "T004", "할 일의 제목은 비어 있을 수 없습니다."),

    // === 카테고리 (Category) ===
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CA001", "해당 카테고리를 찾을 수 없습니다."),

    // === 일기 (Diary) ===
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "해당 날짜의 일기를 찾을 수 없습니다."),

    //=== 가계부 (Budget/Transaction) ===
    ASSET_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "해당 자산을 찾을 수 없습니다."),
    FINANCE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "B002", "해당 카테고리를 찾을 수 없습니다."),
    BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND,"B003", "해당 예산을 찾을 수 없습니다."),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "B004", "해당 거래 내역을 찾을 수 없습니다."),
    TRANSACTION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "B005", "거래 내역에 접근할 권한이 없습니다."),

    // === 서버 (Server) ===
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다.");


    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String code;           // 앱에서 정의한 고유 에러 코드
    private final String message;        // 에러 메시지

}

package com.idigiwave.cbs.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CbsApiResponse<T> {

    private String status;       // SUCCESS or FAILURE
    private String statusCode;   // CBS-200, CBS-400, CBS-404, CBS-500
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> CbsApiResponse<T> success(String message, T data) {
        return CbsApiResponse.<T>builder()
                .status("SUCCESS")
                .statusCode("CBS-200")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> CbsApiResponse<T> failure(String statusCode, String message) {
        return CbsApiResponse.<T>builder()
                .status("FAILURE")
                .statusCode(statusCode)
                .message(message)
                .build();
    }
}

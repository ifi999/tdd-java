package io.hhplus.tdd;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int httpStatus,
        T data
) {

    public static <T> ApiResponse<T> isOk(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), data);
    }

}

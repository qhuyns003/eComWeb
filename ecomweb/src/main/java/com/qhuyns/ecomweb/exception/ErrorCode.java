package com.qhuyns.ecomweb.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1009, "Your password is not match with repeat password", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1010, "Product not exists", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(1011, "Category not exists", HttpStatus.BAD_REQUEST),
    CANT_NOT_UPLOAD(1012, "Can not upload image", HttpStatus.BAD_GATEWAY),
    VARIANT_NOT_FOUND(1013, "Variant not found", HttpStatus.BAD_REQUEST),
    DETAIL_ATTRIBUTE_NOT_EXISTS(1014, "Detail attribute is not exists", HttpStatus.BAD_GATEWAY),
    PRODUCT_HAS_ORDER(1015, "Không thể xóa sản phẩm '%s' do đã phát sinh đơn hàng", HttpStatus.CONFLICT),
    USER_ADDRESS_NOT_EXISTS(1016, "Không tồn tại địa chỉ khách hàng này", HttpStatus.NOT_FOUND),
    COUPON_NOT_EXISTS(1017, "Không tồn tại mã giảm giá này", HttpStatus.NOT_FOUND),
    ORDER_NOT_EXISTS(1018, "Không tồn tại đơn hàng này", HttpStatus.NOT_FOUND),
    SHOP_NOT_EXISTS(1019, "Không tồn tại shop này", HttpStatus.NOT_FOUND),
    DONNT_ENOUGH_PRODUCT(1020, "Hết hàng", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1020, "Sai mật khẩu", HttpStatus.BAD_REQUEST),
    DO_NOT_DELETE_USER_ADDRESS(1021, "Không thể xóa địa chỉ mặc định", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTS(1022, "Không tồn tại role", HttpStatus.BAD_GATEWAY),
    NOTIFICATION_RECIPIENT_NOT_EXISTS(1023, "Không tồn tại ", HttpStatus.BAD_GATEWAY),
    VERIFICATION_TOKEN_IS_EXPRIRED(1024, "Quá hạn token", HttpStatus.BAD_GATEWAY),
    ROOM_NOT_FOUND(1024, "Phong chat khong ton tai", HttpStatus.BAD_GATEWAY)

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}

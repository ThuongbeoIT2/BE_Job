package com.example.oauth2.vnpay;

public enum VNPayResponseCode {
    SUCCESS("00", "Giao dịch thành công"),
    FRAUD_SUSPECTED("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường)."),
    INTERNET_BANKING_NOT_REGISTERED("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng."),
    AUTH_FAILED("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần"),
    TIMEOUT("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch."),
    ACCOUNT_LOCKED("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa."),
    OTP_INCORRECT("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch."),
    CANCELED("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch"),
    INSUFFICIENT_BALANCE("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch."),
    DAILY_LIMIT_EXCEEDED("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày."),
    BANK_MAINTENANCE("75", "Ngân hàng thanh toán đang bảo trì."),
    PAYMENT_PASSWORD_EXCEEDED("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch"),
    OTHER("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)");

    private final String code;
    private final String description;

    VNPayResponseCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static VNPayResponseCode fromCode(String code) {
        for (VNPayResponseCode responseCode : values()) {
            if (responseCode.getCode().equals(code)) {
                return responseCode;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
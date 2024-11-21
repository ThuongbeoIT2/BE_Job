package com.example.oauth2.vnpay;


public enum VNPayTransactionStatus {
    SUCCESS("00", "Giao dịch thành công"),
    INCOMPLETE("01", "Giao dịch chưa hoàn tất"),
    ERROR("02", "Giao dịch bị lỗi"),
    REVERSED("04", "Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY)"),
    REFUND_PROCESSING("05", "VNPAY đang xử lý giao dịch này (GD hoàn tiền)"),
    REFUND_REQUESTED("06", "VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền)"),
    FRAUD_SUSPECTED("07", "Giao dịch bị nghi ngờ gian lận"),
    REFUND_DECLINED("09", "GD Hoàn trả bị từ chối");

    private final String code;
    private final String description;

    VNPayTransactionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static VNPayTransactionStatus fromCode(String code) {
        for (VNPayTransactionStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
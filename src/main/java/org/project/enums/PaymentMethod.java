package org.project.enums;

public enum PaymentMethod {
    CASH("Tiền mặt"),
    CARD("Thẻ tín dụng"),
    MOMO("Ví MoMo");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static void main(String[] args) {
        PaymentMethod paymentMethod = PaymentMethod.CASH;
        System.out.println(paymentMethod.getValue());
    }
}

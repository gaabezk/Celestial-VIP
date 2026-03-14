package br.com.celestialvip.domain.exception;

public class PaymentNotApprovedException extends CelestialVipException {
    public static final String MESSAGE_KEY = "config.messages.payment_not_approved";

    public PaymentNotApprovedException() {
        super(MESSAGE_KEY);
    }
}

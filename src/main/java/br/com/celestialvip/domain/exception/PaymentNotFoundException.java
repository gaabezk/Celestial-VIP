package br.com.celestialvip.domain.exception;

public class PaymentNotFoundException extends CelestialVipException {
    public static final String MESSAGE_KEY = "config.messages.payment_not_found";

    public PaymentNotFoundException() {
        super(MESSAGE_KEY);
    }
}

package br.com.celestialvip.domain.exception;

public class InvalidPaymentFormatException extends CelestialVipException {
    private final String messageKey;

    public InvalidPaymentFormatException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }
}

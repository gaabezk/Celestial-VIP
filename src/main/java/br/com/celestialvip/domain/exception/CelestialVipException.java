package br.com.celestialvip.domain.exception;

/**
 * Exceção base para erros de domínio do CelestialVIP.
 */
public class CelestialVipException extends Exception {

    private final String messageKey;

    public CelestialVipException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public CelestialVipException(String messageKey, Throwable cause) {
        super(messageKey, cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}

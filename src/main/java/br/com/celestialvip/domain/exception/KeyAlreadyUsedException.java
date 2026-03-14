package br.com.celestialvip.domain.exception;

public class KeyAlreadyUsedException extends CelestialVipException {
    public static final String MESSAGE_KEY = "config.messages.key_already_used";

    public KeyAlreadyUsedException() {
        super(MESSAGE_KEY);
    }
}

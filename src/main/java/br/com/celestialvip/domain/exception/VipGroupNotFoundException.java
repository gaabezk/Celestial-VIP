package br.com.celestialvip.domain.exception;

public class VipGroupNotFoundException extends CelestialVipException {
    public static final String MESSAGE_KEY = "config.messages.vip_group_not_found";

    public VipGroupNotFoundException() {
        super(MESSAGE_KEY);
    }
}

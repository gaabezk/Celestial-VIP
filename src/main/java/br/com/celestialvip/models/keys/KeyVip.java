package br.com.celestialvip.models.keys;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
public class KeyVip {
    private String keyCode;
    private String vipName;
    private Integer durationInDays;
    private boolean isActive;
    private boolean isPermanent;
    private Date creationDate;

    public KeyVip(
        String keyCode,
        String vipName,
        Integer durationInDays,
        boolean isActive,
        boolean isPermanent,
        Date creationDate
    ) {
        this.keyCode = keyCode;
        this.vipName = vipName;
        this.durationInDays = durationInDays;
        this.isActive = isActive;
        this.isPermanent = isPermanent;
        this.creationDate = creationDate;
    }
}

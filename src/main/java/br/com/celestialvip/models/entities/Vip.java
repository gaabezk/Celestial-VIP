package br.com.celestialvip.models.entities;

import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Vip {
    private static final ZoneId BRAZIL_TIMEZONE = ZoneId.of("America/Sao_Paulo");
    private Integer id;
    private String playerNick;
    private String group;
    private boolean isActive;
    private int vipDays;
    private boolean isPermanent;
    private LocalDate creationDate;
    private LocalDate expirationDate;

    public void definirDatas() {
        this.creationDate = LocalDate.now(BRAZIL_TIMEZONE);
        this.expirationDate = LocalDate.now(BRAZIL_TIMEZONE).plusDays(vipDays);
        if(vipDays==0){
            this.expirationDate = null;
        }
    }

    public boolean isVipExpired() {
        if(expirationDate==null){
            return false;
        }
        LocalDate today = LocalDate.now(BRAZIL_TIMEZONE);
        return today.isAfter(expirationDate);
    }
}
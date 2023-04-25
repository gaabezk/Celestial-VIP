package br.com.celestialvip.models.entities;

import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public long daysLeft(){
        return ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(isPermanent){
            builder.append(" \n");
            builder.append("§eVip: §f").append(group).append("    §eCreated: §f").append(creationDate).append("    §eExpiration: §fPermanent").append("\n");
        }else {
            builder.append(" \n");
            builder.append("§eVip: §f").append(group).append("    §eCreated: §f").append(creationDate).append("    §eExpiration: §f").append(expirationDate).append("\n");
            builder.append("§eTotal Days: §f").append(vipDays).append("    §eDays Left: §f").append(daysLeft()).append("\n");
        }
        return builder.toString();
    }
}
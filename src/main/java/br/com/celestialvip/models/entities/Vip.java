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
    private LocalDate creationDate;
    private LocalDate expirationDate;

    public void definirDatas() {
        LocalDate dataAtual = LocalDate.now(BRAZIL_TIMEZONE);
        LocalDate dataExpiracao = dataAtual.plusDays(vipDays);

        this.creationDate = dataAtual;
        this.expirationDate = dataExpiracao;
    }

    public boolean isVipExpired() {
        LocalDate today = LocalDate.now(BRAZIL_TIMEZONE);
        return today.isAfter(expirationDate);
    }
}
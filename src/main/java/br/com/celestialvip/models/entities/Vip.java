package br.com.celestialvip.models.entities;

import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Vip {
    private String playerNick;
    private String group;
    private boolean hasVip;
    private int vipDays;
    private LocalDate creationDate;
    private LocalDate expirationDate;
    private static final ZoneId BRAZIL_TIMEZONE = ZoneId.of("America/Sao_Paulo");

    public void definirDataDeExpiracao(int dias) {
        LocalDate dataAtual = LocalDate.now(BRAZIL_TIMEZONE);
        LocalDate dataExpiracao = dataAtual.plusDays(dias);

        this.creationDate = dataAtual;
        this.expirationDate = dataExpiracao;
    }
    public boolean isVipExpired() {
        LocalDate today = LocalDate.now(BRAZIL_TIMEZONE);
        return today.isAfter(expirationDate);
    }
}

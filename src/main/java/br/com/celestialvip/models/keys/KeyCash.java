package br.com.celestialvip.models.keys;

import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@ToString
public class KeyCash {
    private String keyCode;
    private Double amountOfCash;
    private boolean isActive;
    private LocalDate creationDate;
    public KeyCash(String keyCode, Double amountOfCash) {
        this.keyCode = keyCode;
        this.amountOfCash = amountOfCash;
        this.isActive = true;
        this.creationDate = LocalDate.now(ZoneId.of("America/Sao_Paulo"));;
    }
    public KeyCash(String keyCode, Double amountOfCash,boolean isActive) {
        this.keyCode = keyCode;
        this.amountOfCash = amountOfCash;
        this.isActive = isActive;
        this.creationDate = LocalDate.now(ZoneId.of("America/Sao_Paulo"));;
    }

}

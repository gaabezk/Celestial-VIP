package br.com.celestialvip.models.keys;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CashKey {
    private String keyCode;
    private String usedBy;
    private Double amountOfCash;
    private boolean isActive;
    private LocalDate creationDate;
}

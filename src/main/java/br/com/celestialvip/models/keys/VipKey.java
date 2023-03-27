package br.com.celestialvip.models.keys;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VipKey {
    private String keyCode;
    private String usedBy;
    private String vipName;
    private Integer durationInDays;
    private boolean isActive;
    private boolean isPermanent;
    private LocalDate creationDate;
}

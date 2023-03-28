package br.com.celestialvip.models.keys;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("§6VipKey Information§f\n");
        builder.append("§eKey Code: §f").append(keyCode).append("\n");
        builder.append("§eVIP Name: §f").append(vipName).append("\n");
        builder.append("§eDuration: §f").append(durationInDays).append(" day(s)\n");
        builder.append("§eIs Active: §f").append(isActive).append("\n");
        builder.append("§eIs Permanent: §f").append(isPermanent).append("\n");
        builder.append("§eCreated on: §f").append(creationDate).append("\n");
        builder.append("§eUsed By: §f").append(usedBy).append("\n");
        return builder.toString();
    }

}

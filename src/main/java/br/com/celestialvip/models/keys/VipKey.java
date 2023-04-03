package br.com.celestialvip.models.keys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VipKey {
    private String keyCode;
    private String usedBy;
    private String vipName;
    private int durationInDays;
    private boolean isActive;
    private boolean isPermanent;
    private LocalDate creationDate;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(usedBy==null){
            builder.append(" \n");
            builder.append("§eKey: §f").append(keyCode).append("\n");
            builder.append("§eCreated: §f").append(creationDate).append("    §eVip: §f").append(vipName).append("    §eDuration: §f").append(isPermanent?"Permanent":(durationInDays+" day(s)")).append("\n");
        }else{
            builder.append(" \n");
            builder.append("§eKey: §f").append(keyCode).append("\n");
            builder.append("§eCreated: §f").append(creationDate).append("    §eVip: §f").append(vipName).append("    §eDuration: §f").append(isPermanent?"Permanent":(durationInDays+" day(s)")).append("    §eUsed By: §f").append(usedBy).append("\n");
        }
        return builder.toString();
    }
}

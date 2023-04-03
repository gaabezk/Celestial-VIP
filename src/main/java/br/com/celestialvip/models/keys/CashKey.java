package br.com.celestialvip.models.keys;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashKey {
    private String keyCode;
    private String usedBy;
    private Integer amountOfCash;
    private boolean isActive;
    private LocalDate creationDate;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(usedBy==null){
            builder.append(" \n");
            builder.append("§eKey: §f").append(keyCode).append("\n");
            builder.append("§eCreated: §f").append(creationDate).append("    §eAmount: §f").append(amountOfCash).append("\n");
        }else{
            builder.append(" \n");
            builder.append("§eKey: §f").append(keyCode).append("\n");
            builder.append("§eCreated: §f").append(creationDate).append("    §eUsed By: §f").append(usedBy).append("    §eAmount: §f").append(amountOfCash).append("\n");
        }
        return builder.toString();
    }
}

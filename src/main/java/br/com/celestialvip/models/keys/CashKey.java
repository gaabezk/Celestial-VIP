package br.com.celestialvip.models.keys;

import java.time.LocalDate;

public class CashKey {
    private String keyCode;
    private String usedBy;
    private Integer amountOfCash;
    private boolean isActive;
    private LocalDate creationDate;

    public CashKey() {}

    public CashKey(String keyCode, String usedBy, Integer amountOfCash, boolean isActive, LocalDate creationDate) {
        this.keyCode = keyCode;
        this.usedBy = usedBy;
        this.amountOfCash = amountOfCash;
        this.isActive = isActive;
        this.creationDate = creationDate;
    }

    public String getKeyCode() { return keyCode; }

    public String getUsedBy() { return usedBy; }

    public Integer getAmountOfCash() { return amountOfCash; }

    public boolean isActive() { return isActive; }

    public LocalDate getCreationDate() { return creationDate; }

    public void setUsedBy(String usedBy) { this.usedBy = usedBy; }

    public void setActive(boolean active) { isActive = active; }

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

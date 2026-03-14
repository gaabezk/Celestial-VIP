package br.com.celestialvip.models.keys;

import java.time.LocalDate;

public class VipKey {
    private String keyCode;
    private String usedBy;
    private String vipName;
    private int durationInDays;
    private boolean isActive;
    private boolean isPermanent;
    private LocalDate creationDate;

    public VipKey() {}

    public VipKey(String keyCode, String usedBy, String vipName, int durationInDays, boolean isActive, boolean isPermanent, LocalDate creationDate) {
        this.keyCode = keyCode;
        this.usedBy = usedBy;
        this.vipName = vipName;
        this.durationInDays = durationInDays;
        this.isActive = isActive;
        this.isPermanent = isPermanent;
        this.creationDate = creationDate;
    }

    public String getKeyCode() { return keyCode; }

    public String getUsedBy() { return usedBy; }

    public String getVipName() { return vipName; }

    public int getDurationInDays() { return durationInDays; }

    public boolean isActive() { return isActive; }

    public boolean isPermanent() { return isPermanent; }

    public LocalDate getCreationDate() { return creationDate; }

    public void setUsedBy(String usedBy) { this.usedBy = usedBy; }

    public void setActive(boolean active) { isActive = active; }

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

package br.com.celestialvip.models.entities;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Vip {
    private Integer id;
    private String playerNick;
    private String group;
    private boolean isActive;
    private int vipDays;
    private boolean isPermanent;
    private LocalDate creationDate;
    private LocalDate expirationDate;

    public Vip() {}

    public Vip(Integer id, String playerNick, String group, boolean isActive, int vipDays, boolean isPermanent, LocalDate creationDate, LocalDate expirationDate) {
        this.id = id;
        this.playerNick = playerNick;
        this.group = group;
        this.isActive = isActive;
        this.vipDays = vipDays;
        this.isPermanent = isPermanent;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
    }

    public Integer getId() { return id; }

    public String getPlayerNick() { return playerNick; }

    public String getGroup() { return group; }

    public boolean isActive() { return isActive; }

    public int getVipDays() { return vipDays; }

    public boolean isPermanent() { return isPermanent; }

    public LocalDate getCreationDate() { return creationDate; }

    public LocalDate getExpirationDate() { return expirationDate; }

    public void setVipDays(int vipDays) { this.vipDays = vipDays; }

    public void setPlayerNick(String playerNick) { this.playerNick = playerNick; }

    public void setGroup(String group) { this.group = group; }

    public void setActive(boolean active) { isActive = active; }

    public void setPermanent(boolean permanent) { isPermanent = permanent; }

    /** Define creation and expiration dates using the given timezone. */
    public void definirDatas(ZoneId zoneId) {
        ZoneId zone = zoneId != null ? zoneId : ZoneId.systemDefault();
        this.creationDate = LocalDate.now(zone);
        this.expirationDate = vipDays == 0 ? null : LocalDate.now(zone).plusDays(vipDays);
    }

    public boolean isVipExpired(ZoneId zoneId) {
        if (expirationDate == null) {
            return false;
        }
        ZoneId zone = zoneId != null ? zoneId : ZoneId.systemDefault();
        return LocalDate.now(zone).isAfter(expirationDate);
    }

    /** Days left until expiration; 0 if permanent or already expired. */
    public long daysLeft(ZoneId zoneId) {
        if (expirationDate == null) {
            return 0;
        }
        ZoneId zone = zoneId != null ? zoneId : ZoneId.systemDefault();
        long between = ChronoUnit.DAYS.between(LocalDate.now(zone), expirationDate);
        return Math.max(0, between);
    }

    /** Extends the VIP duration by the given amount of days. */
    public void extendDays(int daysToAdd, ZoneId zoneId) {
        if (isPermanent) {
            return;
        }
        ZoneId zone = zoneId != null ? zoneId : ZoneId.systemDefault();
        if (expirationDate == null || LocalDate.now(zone).isAfter(expirationDate)) {
            this.expirationDate = LocalDate.now(zone).plusDays(daysToAdd);
        } else {
            this.expirationDate = expirationDate.plusDays(daysToAdd);
        }
        this.vipDays += daysToAdd;
        // Ensure the record remains active
        this.isActive = true;
    }

    @Override
    public String toString() {
        return toString(ZoneId.systemDefault());
    }

    public String toString(ZoneId zoneId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder builder = new StringBuilder();
        builder.append("§6=== Informações do VIP ===\n");
        builder.append("§eGrupo: §f").append(group).append("\n");
        builder.append("§eCriado em: §f").append(creationDate != null ? creationDate.format(formatter) : "N/A").append("\n");
        if (isPermanent) {
            builder.append("§eExpiração: §aPermanente\n");
        } else if (expirationDate != null) {
            builder.append("§eExpira em: §f").append(expirationDate.format(formatter)).append("\n");
            long daysLeft = daysLeft(zoneId);
            if (daysLeft > 0) {
                LocalDateTime now = LocalDateTime.now(zoneId);
                LocalDateTime exp = expirationDate.atStartOfDay();
                Duration duration = Duration.between(now, exp);
                long days = duration.toDays();
                long hours = duration.toHours() % 24;
                long minutes = duration.toMinutes() % 60;
                builder.append("§eTempo restante: §b").append(days).append(" dias, ").append(hours).append(" horas e ").append(minutes).append(" minutos\n");
            } else {
                builder.append("§eStatus: §cExpirado\n");
            }
        } else {
            builder.append("§eStatus: §cErro - Data de expiração não definida\n");
        }
        builder.append("§6========================");
        return builder.toString();
    }
}
package br.com.celestialvip.hooks;

import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.entities.Vip;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlaceholderHook extends PlaceholderExpansion {

    private final VipRepository vipRepository;
    private final ZoneId zoneId;

    public PlaceholderHook(VipRepository vipRepository, String timezone) {
        this.vipRepository = vipRepository;
        this.zoneId = ZoneId.of(timezone);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "celestialvip";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gabezk";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        String[] parts = params.split("_", 2);
        String param = parts[0].toLowerCase();
        String group = parts.length > 1 ? parts[1] : null;

        List<Vip> vips = vipRepository.getAllVipsByPlayerNick(player.getName(), true);
        if (vips.isEmpty()) {
            return getDefaultValue(param);
        }

        Vip selectedVip;
        if (group != null) {
            // Se grupo especificado, pega o VIP desse grupo
            selectedVip = vips.stream().filter(v -> v.getGroup().equalsIgnoreCase(group)).findFirst().orElse(null);
            if (selectedVip == null) return getDefaultValue(param);
        } else {
            // Pega o VIP com mais tempo restante (ou permanente)
            selectedVip = vips.stream()
                    .max((v1, v2) -> {
                        if (v1.isPermanent()) return 1;
                        if (v2.isPermanent()) return -1;
                        return Long.compare(v1.daysLeft(zoneId), v2.daysLeft(zoneId));
                    })
                    .orElse(vips.get(0));
        }

        return getVipValue(selectedVip, param);
    }

    private String getDefaultValue(String param) {
        return switch (param) {
            case "status" -> "Nenhum";
            case "group" -> "Nenhum";
            case "timeleft" -> "0";
            case "lastupdate" -> formatTimestamp(vipRepository.getLastCacheUpdate());
            case "expirationdate" -> "N/A";
            case "creationdate" -> "N/A";
            case "daysleft" -> "0";
            case "hoursleft" -> "0";
            case "ispermanent" -> "false";
            case "isactive" -> "false";
            default -> null;
        };
    }

    private String getVipValue(Vip vip, String param) {
        return switch (param) {
            case "status" -> vip.isActive() ? "Ativo" : "Inativo";
            case "group" -> vip.getGroup();
            case "timeleft" -> vip.isPermanent() ? "Permanente" : String.valueOf(vip.daysLeft(zoneId));
            case "lastupdate" -> formatTimestamp(vipRepository.getLastCacheUpdate());
            case "expirationdate" -> vip.getExpirationDate() != null ? vip.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Permanente";
            case "creationdate" -> vip.getCreationDate() != null ? vip.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
            case "daysleft" -> vip.isPermanent() ? "∞" : String.valueOf(vip.daysLeft(zoneId));
            case "hoursleft" -> {
                if (vip.isPermanent()) yield "∞";
                LocalDateTime now = LocalDateTime.now(zoneId);
                LocalDateTime exp = vip.getExpirationDate().atStartOfDay();
                Duration duration = Duration.between(now, exp);
                yield String.valueOf(duration.toHours());
            }
            case "ispermanent" -> String.valueOf(vip.isPermanent());
            case "isactive" -> String.valueOf(vip.isActive());
            default -> null;
        };
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp == 0) return "Nunca";
        return Instant.ofEpochMilli(timestamp).atZone(zoneId).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
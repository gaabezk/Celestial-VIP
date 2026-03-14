package br.com.celestialvip.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Set;

/**
 * Acesso tipado à configuração do plugin (evita magic strings espalhados).
 */
public class PluginConfig {

    private static final String CONFIG_PREFIX = "config.";
    private final FileConfiguration config;

    public PluginConfig(FileConfiguration config) {
        this.config = config;
    }

    public String getPrefix() {
        return config.getString(CONFIG_PREFIX + "prefix", "&6[CelestialVIP]");
    }

    public int getVipExpirationCheckInterval() {
        return config.getInt(CONFIG_PREFIX + "vip-expiration-check-interval", 1800);
    }

    public int getCacheDurationMinutes() {
        return config.getInt(CONFIG_PREFIX + "cache-duration-minutes", 1);
    }

    public int getCacheDurationSeconds() {
        return config.getInt(CONFIG_PREFIX + "cache-duration-seconds", 60);
    }

    public boolean isBackupEnabled() {
        return config.getBoolean(CONFIG_PREFIX + "backup.enabled", true);
    }

    public int getBackupIntervalHours() {
        return config.getInt(CONFIG_PREFIX + "backup.interval-hours", 24);
    }

    public int getKeySize() {
        return config.getInt(CONFIG_PREFIX + "key-size", 15);
    }

    public String getTimezone() {
        return config.getString(CONFIG_PREFIX + "timezone", "America/Sao_Paulo");
    }

    public int getVipPricePerDay(String group) {
        return config.getInt(CONFIG_PREFIX + "vips." + group + ".price-per-day", 1);
    }

    public boolean isVipRenewable(String group) {
        return config.getBoolean(CONFIG_PREFIX + "vips." + group + ".renewable", true);
    }

    public boolean isAnnounceActive() {
        return config.getBoolean(CONFIG_PREFIX + "announce.active", true);
    }

    public String getAnnounceType() {
        return config.getString(CONFIG_PREFIX + "announce.type", "chat");
    }

    public String getAnnounceMessage() {
        return config.getString(CONFIG_PREFIX + "announce.chat-and-actionbar.message", "&a%player% se tornou um %tag%");
    }

    public String getAnnounceTitle() {
        return config.getString(CONFIG_PREFIX + "announce.title.title", "%player% se tornou um");
    }

    public String getAnnounceSubtitle() {
        return config.getString(CONFIG_PREFIX + "announce.title.subtitle", "%tag%");
    }

    public String getDatabaseDrive() {
        return config.getString(CONFIG_PREFIX + "database.drive", "sqlite");
    }

    public String getDatabaseHost() {
        return config.getString(CONFIG_PREFIX + "database.host", "localhost");
    }

    public String getDatabasePort() {
        return config.getString(CONFIG_PREFIX + "database.port", "3306");
    }

    public String getDatabaseName() {
        return config.getString(CONFIG_PREFIX + "database.database", "teste");
    }

    public String getDatabaseUser() {
        return config.getString(CONFIG_PREFIX + "database.user", "root");
    }

    public String getDatabasePassword() {
        return config.getString(CONFIG_PREFIX + "database.password", "password");
    }

    public String getDatabaseTablePrefix() {
        return config.getString(CONFIG_PREFIX + "database.tb_prefix", "celestialvip_");
    }

    public String getMercadoPagoClientId() {
        return config.getString(CONFIG_PREFIX + "mercadopago.CLIENT_ID", "");
    }

    public String getMercadoPagoClientSecret() {
        return config.getString(CONFIG_PREFIX + "mercadopago.CLIENT_SECRET", "");
    }

    public ConfigurationSection getVipsSection() {
        return config.getConfigurationSection(CONFIG_PREFIX + "vips");
    }

    public ConfigurationSection getCashSection() {
        return config.getConfigurationSection(CONFIG_PREFIX + "cash");
    }

    public Set<String> getVipGroupKeys() {
        ConfigurationSection section = getVipsSection();
        return section != null ? section.getKeys(false) : Set.of();
    }

    public String getMessage(String key) {
        String path = key.startsWith("config.messages.") ? key : CONFIG_PREFIX + "messages." + key;
        return config.getString(path, "&cMensagem não encontrada: " + key);
    }

    public String getMessageRaw(String fullKey) {
        String path = fullKey.startsWith("config.") ? fullKey : CONFIG_PREFIX + "messages." + fullKey;
        return config.getString(path, "&c" + fullKey);
    }

    public List<String> getVipActivationCommands(String vipGroup) {
        ConfigurationSection section = config.getConfigurationSection(CONFIG_PREFIX + "vips." + vipGroup);
        return section != null ? section.getStringList("activation-commands") : List.of();
    }

    public List<String> getVipAfterExpirationCommands(String vipGroup) {
        ConfigurationSection section = config.getConfigurationSection(CONFIG_PREFIX + "vips." + vipGroup);
        return section != null ? section.getStringList("after-expiration-commands") : List.of();
    }

    public ConfigurationSection getVipSection(String vipGroup) {
        return config.getConfigurationSection(CONFIG_PREFIX + "vips." + vipGroup);
    }

    public List<String> getCashActivationCommands() {
        ConfigurationSection section = getCashSection();
        return section != null ? section.getStringList("activation-commands") : List.of();
    }

    public FileConfiguration getRaw() {
        return config;
    }
}

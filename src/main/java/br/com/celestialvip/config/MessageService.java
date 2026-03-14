package br.com.celestialvip.config;

import br.com.celestialvip.utils.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Centraliza prefixo e mensagens; suporta Adventure Component para Paper 1.21.
 */
public class MessageService {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private final PluginConfig pluginConfig;

    public MessageService(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public String getPrefix() {
        return Utilities.translateColorCodes(pluginConfig.getPrefix()) + " ";
    }

    public String getMessage(String messageKey) {
        String key = messageKey.startsWith("config.messages.") ? messageKey : "config.messages." + messageKey;
        String raw = pluginConfig.getMessageRaw(key);
        return raw != null ? Utilities.translateColorCodes(raw) : "";
    }

    public String getMessage(String messageKey, Map<String, String> params) {
        String msg = getMessage(messageKey);
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                msg = msg.replace("{" + e.getKey() + "}", e.getValue());
            }
        }
        return msg;
    }

    public void send(CommandSender sender, String messageKey) {
        sender.sendMessage(LEGACY_SERIALIZER.deserialize(getPrefix() + getMessage(messageKey)));
    }

    public void send(CommandSender sender, String messageKey, Map<String, String> params) {
        sender.sendMessage(LEGACY_SERIALIZER.deserialize(getPrefix() + getMessage(messageKey, params)));
    }

    public void sendRaw(CommandSender sender, String text) {
        sender.sendMessage(LEGACY_SERIALIZER.deserialize(getPrefix() + Utilities.translateColorCodes(text)));
    }

    public Component toComponent(String text) {
        return LEGACY_SERIALIZER.deserialize(Utilities.translateColorCodes(text));
    }

    public String formatWithPrefix(String messageKey) {
        return getPrefix() + getMessage(messageKey);
    }

    public String formatWithPrefix(String messageKey, Map<String, String> params) {
        return getPrefix() + getMessage(messageKey, params);
    }
}

package br.com.celestialvip.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logger that writes to a file under the plugin data folder.
 */
public class LoggerUtil {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static Path logFile;

    public static void init(JavaPlugin plugin) {
        try {
            Path logsDir = plugin.getDataFolder().toPath().resolve("logs");
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
            }
            logFile = logsDir.resolve("celestialvip.log");
            if (!Files.exists(logFile)) {
                Files.createFile(logFile);
            }
            logInfo("Logger initialized.");
        } catch (IOException e) {
            plugin.getLogger().warning("Falha ao inicializar LoggerUtil: " + e.getMessage());
        }
    }

    public static void logInfo(String message) {
        log("INFO", message, null);
    }

    public static void logWarn(String message) {
        log("WARN", message, null);
    }

    public static void logError(String message, Throwable throwable) {
        log("ERROR", message, throwable);
    }

    private static synchronized void log(String level, String message, Throwable t) {
        if (logFile == null) {
            return;
        }
        String timestamp = LocalDateTime.now().format(TIMESTAMP);
        StringBuilder line = new StringBuilder();
        line.append("[").append(timestamp).append("] ").append(level).append(" - ").append(message).append(System.lineSeparator());
        if (t != null) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            line.append(sw).append(System.lineSeparator());
        }
        try {
            Files.writeString(logFile, line.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }
}

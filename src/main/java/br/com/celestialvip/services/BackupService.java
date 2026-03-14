package br.com.celestialvip.services;

import br.com.celestialvip.data.DatabaseManager;
import br.com.celestialvip.utils.LoggerUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class BackupService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;

    public BackupService(JavaPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    public void scheduleBackups(int intervalHours) {
        if (intervalHours <= 0) {
            return;
        }
        long intervalTicks = TimeUnit.HOURS.toSeconds(intervalHours) * 20L;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                runBackup();
            } catch (Exception e) {
                LoggerUtil.logError("Erro ao executar backup do banco de dados", e);
            }
        }, 20L, intervalTicks);
    }

    public void runBackup() {
        Path sqliteFile = databaseManager.getSqliteFilePath();
        if (sqliteFile == null) {
            LoggerUtil.logInfo("Backup não executado: não é um banco SQLite.");
            return;
        }

        try {
            Path backupsDir = plugin.getDataFolder().toPath().resolve("backups");
            if (!Files.exists(backupsDir)) {
                Files.createDirectories(backupsDir);
            }
            String timestamp = LocalDateTime.now().format(FORMATTER);
            Path backupFile = backupsDir.resolve("sqlite_backup_" + timestamp + ".db");
            Files.copy(sqliteFile, backupFile);
            LoggerUtil.logInfo("Backup do banco criado: " + backupFile.getFileName());
        } catch (IOException e) {
            LoggerUtil.logError("Falha ao criar backup do banco de dados", e);
        }
    }
}

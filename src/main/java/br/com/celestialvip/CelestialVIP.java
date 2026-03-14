package br.com.celestialvip;

import br.com.celestialvip.commands.CelestialVipCommand;
import br.com.celestialvip.commands.*;
import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.DatabaseManager;
import br.com.celestialvip.data.repositories.CashRepository;
import br.com.celestialvip.data.repositories.PlayerRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.mercadopago.MercadoPagoAPI;
import br.com.celestialvip.services.ActivationService;
import br.com.celestialvip.services.BackupService;
import br.com.celestialvip.services.DeactivationService;
import br.com.celestialvip.utils.LoggerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

public final class CelestialVIP extends JavaPlugin {

    private PluginConfig pluginConfig;
    private MessageService messageService;
    private DatabaseManager databaseManager;
    private VipRepository vipRepository;
    private PlayerRepository playerRepository;
    private CashRepository cashRepository;
    private MercadoPagoAPI mercadoPagoAPI;
    private ActivationService activationService;
    private DeactivationService deactivationService;
    private BukkitTask expirationTask;

    @Override
    public void onEnable() {
        getLogger().info("\033[92mBy: gabezk | Obrigado por usar!\033[0m");
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();

        LoggerUtil.init(this);

        try {
            initServices();
        } catch (Exception e) {
            getLogger().severe("\033[31mFalha ao iniciar o plugin: " + e.getMessage() + "\033[0m");
            LoggerUtil.logError("Falha ao iniciar o plugin", e);
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerCommands();
        scheduleVipExpiration();

        if (pluginConfig.isBackupEnabled()) {
            new BackupService(this, databaseManager).scheduleBackups(pluginConfig.getBackupIntervalHours());
        }
    }

    private void initServices() {
        pluginConfig = new PluginConfig(getConfig());
        databaseManager = new DatabaseManager(pluginConfig, getDataFolder());
        DataSource ds = databaseManager.getDataSource();
        String prefix = pluginConfig.getDatabaseTablePrefix();
        vipRepository = new VipRepository(ds, prefix, pluginConfig.getCacheDurationSeconds());
        playerRepository = new PlayerRepository(ds, prefix);
        cashRepository = new CashRepository(ds, prefix);
        messageService = new MessageService(pluginConfig);
        mercadoPagoAPI = new MercadoPagoAPI(pluginConfig);
        activationService = new ActivationService(pluginConfig, vipRepository, playerRepository, this);
        deactivationService = new DeactivationService(pluginConfig, vipRepository, this);
    }

    private void scheduleVipExpiration() {
        if (expirationTask != null) {
            expirationTask.cancel();
        }
        long intervalTicks = pluginConfig.getVipExpirationCheckInterval() * 20L;
        expirationTask = getServer().getScheduler().runTaskTimer(this, deactivationService, 100L, intervalTicks);
    }

    private void registerCommands() {
        getCommand("celestialvip").setExecutor(new CelestialVipCommand(this, pluginConfig, messageService, vipRepository, deactivationService));
        getCommand("gerarchave").setExecutor(new GenerateKeyCommand(messageService, vipRepository, cashRepository, pluginConfig));
        getCommand("resgatar").setExecutor(new RedeemCommand(messageService, vipRepository, cashRepository, activationService, mercadoPagoAPI, pluginConfig));
        getCommand("usarchave").setExecutor(new UseKeyCommand(messageService, vipRepository, cashRepository, activationService));
        getCommand("listarchaves").setExecutor(new ListKeysCommand(messageService, vipRepository, cashRepository));
        getCommand("infovip").setExecutor(new VipInfoCommand(messageService, vipRepository, pluginConfig));
        getCommand("apagarchave").setExecutor(new DeleteKeyCommand(messageService, vipRepository, cashRepository));
        getCommand("removervip").setExecutor(new RemoveVipCommand(messageService, vipRepository, deactivationService, pluginConfig));
        getCommand("darvip").setExecutor(new GiveVipCommand(messageService, vipRepository, activationService, pluginConfig));
        // Register PlaceholderAPI hook if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new br.com.celestialvip.hooks.PlaceholderHook(vipRepository, pluginConfig.getTimezone()).register();
        }
    }

    public void reloadPlugin(CommandSender sender) {
        try {
            databaseManager.getConnection().close();
        } catch (SQLException ignored) {
        }
        if (expirationTask != null) {
            expirationTask.cancel();
        }
        reloadConfig();
        try {
            initServices();
            registerCommands();
            scheduleVipExpiration();
        } catch (Exception e) {
            messageService.send(sender, "reload_error", Map.of("error", e.getMessage()));
            return;
        }
        messageService.send(sender, "reload_success");
    }

    @Override
    public void onDisable() {
        if (expirationTask != null) {
            expirationTask.cancel();
        }
        try {
            if (databaseManager != null && databaseManager.getDataSource() != null && !databaseManager.getDataSource().isClosed()) {
                databaseManager.getDataSource().close();
            }
        } catch (Exception e) {
            getLogger().warning("\033[33mErro ao fechar conexão: " + e.getMessage() + "\033[0m");
        }
        getLogger().info("\033[91mPlugin desativado!\033[0m");
    }
}

package br.com.celestialvip.data;

import br.com.celestialvip.config.PluginConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private final String drive;
    private final String host;
    private final String port;
    private final String database;
    private final String user;
    private final String password;
    private final String tbPrefix;
    private final HikariDataSource dataSource;
    private Path sqliteFilePath;

    public HikariDataSource getDataSource() { return dataSource; }

    public Path getSqliteFilePath() { return sqliteFilePath; }

    public DatabaseManager(PluginConfig pluginConfig, File dataFolder) {
        this.drive = pluginConfig.getDatabaseDrive();
        this.host = pluginConfig.getDatabaseHost();
        this.port = pluginConfig.getDatabasePort();
        this.database = pluginConfig.getDatabaseName();
        this.user = pluginConfig.getDatabaseUser();
        this.password = pluginConfig.getDatabasePassword();
        this.tbPrefix = pluginConfig.getDatabaseTablePrefix();
        this.dataSource = createDataSource(dataFolder);
        if (this.dataSource == null) {
            throw new IllegalStateException("Falha ao criar DataSource. Verifique a configuração do banco.");
        }
        createTables();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private HikariDataSource createDataSource(File dataFolder) {
        HikariConfig hikariConfig = new HikariConfig();
        String driver = Objects.requireNonNull(drive, "config.database.drive não pode ser nulo").toLowerCase();

        Path sqlitePath = null;
        try {
            switch (driver) {
                case "mysql":
                    hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", drive, host, port, database));
                    break;
                case "postgresql":
                    hikariConfig.setDriverClassName("org.postgresql.Driver");
                    hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", drive, host, port, database));
                    break;
                default:
                    if (!dataFolder.exists()) {
                        dataFolder.mkdirs();
                    }
                    Path filePath = dataFolder.toPath().resolve("sqlite.db");
                    if (!Files.exists(filePath)) {
                        Files.createFile(filePath);
                    }
                    sqlitePath = filePath;
                    hikariConfig.setDriverClassName("org.sqlite.JDBC");
                    hikariConfig.setJdbcUrl("jdbc:sqlite:" + filePath.toAbsolutePath());
                    break;
            }

            hikariConfig.setUsername(user);
            hikariConfig.setPassword(password);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            this.sqliteFilePath = sqlitePath;
            return new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            logger.error("Erro ao configurar ou conectar ao banco de dados: {}", e.getMessage());
            throw new IllegalStateException("Erro ao configurar conexão com o banco: " + e.getMessage(), e);
        }
    }

    public void createTables() {
        String driver = Objects.requireNonNull(drive).toLowerCase();
        String playerData;
        String vip;
        String vipKey;
        String cashKey;
        String mercadoPagoVipKey;
        String mercadoPagoCashKey;

        switch (driver) {
            case "mysql":
                playerData = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "player_data (nick VARCHAR(60) PRIMARY KEY, uuid VARCHAR(100) NOT NULL)";
                vip = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip (id INT AUTO_INCREMENT PRIMARY KEY, player_nick VARCHAR(60), vip_group VARCHAR(60) NOT NULL, is_active BOOLEAN NOT NULL, vip_days INT, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, expiration_date DATE, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                vipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip_key (key_code VARCHAR(255) PRIMARY KEY, vip_name VARCHAR(255) NOT NULL, duration_in_days INT, is_active BOOLEAN NOT NULL, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                cashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "cash_key (key_code VARCHAR(255) PRIMARY KEY, amount_of_cash INT NOT NULL, is_active BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                mercadoPagoVipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_vip_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL, player_nick VARCHAR(60), FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                mercadoPagoCashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_cash_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL, player_nick VARCHAR(60), FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                break;
            case "postgresql":
                playerData = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "player_data (nick VARCHAR(60) PRIMARY KEY, uuid VARCHAR(100) NOT NULL)";
                vip = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip (id SERIAL PRIMARY KEY, player_nick VARCHAR(60), vip_group VARCHAR(60) NOT NULL, is_active BOOLEAN NOT NULL, vip_days INT, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, expiration_date DATE, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                vipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip_key (key_code VARCHAR(255) PRIMARY KEY, vip_name VARCHAR(255) NOT NULL, duration_in_days INT, is_active BOOLEAN NOT NULL, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                cashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "cash_key (key_code VARCHAR(255) PRIMARY KEY, amount_of_cash INT NOT NULL, is_active BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                mercadoPagoVipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_vip_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL, player_nick VARCHAR(60), FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                mercadoPagoCashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_cash_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL, player_nick VARCHAR(60), FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                break;
            default:
                playerData = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "player_data (nick TEXT PRIMARY KEY, uuid TEXT NOT NULL)";
                vip = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip (id INTEGER PRIMARY KEY AUTOINCREMENT, player_nick TEXT, vip_group TEXT NOT NULL, is_active BOOLEAN NOT NULL, vip_days INTEGER, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, expiration_date DATE, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                vipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip_key (key_code TEXT PRIMARY KEY, vip_name TEXT NOT NULL, duration_in_days INTEGER, is_active BOOLEAN NOT NULL, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by TEXT)";
                cashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "cash_key (key_code TEXT PRIMARY KEY, amount_of_cash INTEGER NOT NULL, is_active BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by TEXT)";
                mercadoPagoVipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_vip_codes (key_code TEXT PRIMARY KEY, creation_date DATE NOT NULL, player_nick TEXT, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                mercadoPagoCashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_cash_codes (key_code TEXT PRIMARY KEY, creation_date DATE NOT NULL, player_nick TEXT, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                break;
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(playerData);
            stmt.executeUpdate(vip);
            stmt.executeUpdate(vipKey);
            stmt.executeUpdate(cashKey);
            stmt.executeUpdate(mercadoPagoVipKey);
            stmt.executeUpdate(mercadoPagoCashKey);
        } catch (SQLException e) {
            logger.error("Erro ao criar tabelas em plugins/CelestialVIP: {}", e.getMessage());
            throw new IllegalStateException("Erro ao criar tabelas: " + e.getMessage(), e);
        }
    }
}

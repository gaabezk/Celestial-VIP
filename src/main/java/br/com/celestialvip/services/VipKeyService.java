package br.com.celestialvip.services;

import br.com.celestialvip.data.repositories.VipKeyRepository;
import br.com.celestialvip.models.keys.VipKey;
import lombok.var;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.eclipse.aether.RepositoryException;

import javax.sql.DataSource;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.rmi.server.RemoteServer.getLog;
import static org.bukkit.Bukkit.getLogger;

public class VipKeyService {

    VipKeyRepository vipKeyRepository;
    ConfigurationSection vips;
    FileConfiguration config;
    Set<String> vipKeys;

    public VipKeyService(DataSource dataSource, FileConfiguration config) {
        this.vipKeyRepository = new VipKeyRepository(dataSource,config);

        this.vips = config.getConfigurationSection("config.vips");
        this.config = config;

        if (vips != null) {
            this.vipKeys = vips.getKeys(false); // Obtém as chaves dos VIPS (diamond, gold, iron)
        }
    }
    public String generateSecureRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public boolean gerarChaveVip(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("gerarchavevip") && args.length == 2) {
            List<String> vipsGroups = new ArrayList<>(vipKeys);
            Boolean isPerm = false;
            VipKey vipKey;
            if (vipsGroups.contains(args[0])) {
                if(args[1].equalsIgnoreCase("perm")){
                    isPerm = true;
                }
                try {
                    vipKey = new VipKey(
                            generateSecureRandomString((int) config.get("config.key-size")),
                            null,
                            args[0],
                            isPerm?null:Integer.parseInt(args[1]),
                            true,
                            isPerm,
                            LocalDate.now());
                    vipKeyRepository.saveVipKey(vipKey);
                    sender.sendMessage(vipKey.toString());
                }catch (Exception e){
                    getLogger().info(("Erro ao criar a chave vip: "+e.getMessage()));
                }
            } else {
                sender.sendMessage("Erro: o grupo VIP " + args[0] + " não foi encontrado. Verifique se digitou corretamente e se o grupo está definido no plugin.");
            }
            return true;
        }
        return false;
    }

}
//    ConfigurationSection vipSection = vips.getConfigurationSection(vipKey);
//
//                    if (vipSection != null) {
//                            String groupName = vipSection.getString("tag");
//                            // Faça algo com o nome do grupo, como imprimir no console
//                            System.out.println(groupName);
//                            }
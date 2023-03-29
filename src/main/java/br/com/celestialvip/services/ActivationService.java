package br.com.celestialvip.services;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.data.repositories.PlayerRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.mercadopago.MercadoPagoAPI;
import br.com.celestialvip.models.entities.PayamentStatus;
import br.com.celestialvip.models.entities.PlayerData;
import br.com.celestialvip.models.entities.Vip;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.eclipse.aether.RepositoryException;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ActivationService {

    private final MercadoPagoAPI mercadoPagoAPI;
    private final FileConfiguration config;
    private final VipRepository vipRepository;
    private final PlayerRepository playerRepository;

    public ActivationService(DataSource dataSource, FileConfiguration config) {
        this.mercadoPagoAPI = new MercadoPagoAPI(config);
        this.playerRepository = new PlayerRepository(dataSource,config);
        this.vipRepository = new VipRepository(dataSource,config);
        this.config = config;
    }

    public boolean resgatarVip(CommandSender sender, Command cmd, String label, String[] args) throws IOException, RepositoryException {
        if (cmd.getName().equalsIgnoreCase("resgatarvip")&&
                args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Este comando só pode ser executado por um jogador.");
                return true;
            }

            Player player = (Player) sender;

            List<Vip> vips = vipRepository.getAllVipsByPlayerNick(player.getName(),true);
            if(!vips.isEmpty()){
                player.sendMessage("Voce ja esta no vip "+vips.get(0).getGroup()+", aguarde o termino do vip atual para ativar o proximo! voce pode usar a mesma chave!");
                return true;
            }

            String code = vipRepository.getMercadoPagoVipKey(args[0]);

            if(code!=null){
                sender.sendMessage("Essa chave ja foi usada!");
                return true;
            }

            PayamentStatus result = mercadoPagoAPI.getPaymentStatus(args[0]);

            if(result.getStatus()==null){
                sender.sendMessage("Pagamento nao encontrado, verifique o codigo de pagamento e tente mais tarde");
                return true;
            }

            if(!result.getStatus().equals("approved")){
                sender.sendMessage("Pagamento ainda nao foi aprovado, tente novamente mais tarde!");
                return true;
            }

            String[] partes = result.getExternalReference().split("\\.");

            partes = Arrays.stream(partes)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

            List<String> vipsGroups = new ArrayList<>(Objects.requireNonNull(config.getConfigurationSection("config.vips")).getKeys(false));
            if(!vipsGroups.contains(partes[0])){
                player.sendMessage("Me desculpe, parece que a staff deixou passar um pequeno erro e seu vip terá que ser ativo manualmente, contate um de nossos Staffs para lhe ajudar!");
                return true;
            }

            ativarVIP(player, partes[0],partes[1]);

            Vip vip = new Vip();
            vip.setVipDays(Integer.parseInt(partes[1]));
            vip.setPlayerNick(player.getName());
            vip.setGroup(partes[0]);
            vip.setActive(true);
            vip.definirDatas();


            PlayerData playerData = playerRepository.getPlayerDataByNick(player.getName());
            if (playerData == null) {
                playerRepository.savePlayerData(new PlayerData(player.getName(), player.getUniqueId().toString()));
            }
            vipRepository.saveMercadoPagoVipKey(args[0]);
            vipRepository.saveVip(vip);

            return true;
        }
        return false;
    }
    public void ativarVIP(Player player, String vipType, String days) {
        ConfigurationSection vipSection = config.getConfigurationSection("config.vips." + vipType); // obtém a seção de configuração para o tipo de VIP escolhido

        if (vipSection != null) {
            List<String> activationCommands = vipSection.getStringList("activation-commands"); // obtém a lista de comandos de ativação para o tipo de VIP escolhido

            if(config.getBoolean("config.announce.active")){
                String message = ColorUtils.translateColorCodes(config.getString("config.prefix")+" "+replaceVariables(config.getString("config.announce.chat-and-actionbar.message"), player, days, vipType, vipSection));
                String announceType = config.getString("config.announce.type");

                switch(announceType) {
                    case "chat":
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            p.sendMessage(message);
                        }
                        break;
                    case "actionbar":
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            p.sendActionBar(message);
                        }
                        break;
                    case "title":
                        String title = replaceVariables(config.getString("config.announce.title.title"), player, days, vipType, vipSection);
                        String subTitle = replaceVariables(config.getString("config.announce.title.subtitle"), player, days, vipType, vipSection);
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            p.sendTitle(title, subTitle);
                        }
                        break;
                }
            }

//            Title title = Title.title(
//                    Component.text("§b"+player.getName()+" §bse tornou um"),
//                    Component.text(tagFormatada)
//            );

            for (String command : activationCommands) {
                command = command.replace("%player%", player.getName()); // substitui %player% pelo nome do jogador
                command = command.replace("%tag%", ColorUtils.translateColorCodes(vipSection.getString("tag"))); // substitui %tag% pela tag do VIP escolhido
                command = command.replace("%days%",days);
                command = command.replace("%group%",vipType);

                if (command.startsWith("[console] ")) {
                    CelestialVIP.getPlugin(CelestialVIP.class).getServer().dispatchCommand(getServer().getConsoleSender(), command.substring(10)); // executa o comando como console
                } else if (command.startsWith("[player] ")) {
                    player.performCommand(command.substring(9)); // executa o comando como jogador
                } else if (command.startsWith("[message] ")) {
                    command = command.replace("&","§");
                    player.sendMessage(command.substring(10)); // envia a mensagem para o jogador
                }
            }
        }
    }

    private String replaceVariables(String message, Player player, String days, String vipType, ConfigurationSection vipSection) {
        message = message.replace("%player%", player.getName());
        message = message.replace("%days%", days);
        message = message.replace("%group%", vipType);
        message = message.replace("%tag%", ChatColor.translateAlternateColorCodes('&', vipSection.getString("tag")));

        return message;
    }
}

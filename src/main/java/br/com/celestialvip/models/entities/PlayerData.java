package br.com.celestialvip.models.entities;

import br.com.celestialvip.data.DatabaseManager;
import lombok.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerData {
    private String nick;
    private String uuid;
}

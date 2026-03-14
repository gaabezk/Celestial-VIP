package br.com.celestialvip.models.entities;

public class PlayerData {
    private String nick;
    private String uuid;

    public PlayerData() {}

    public PlayerData(String nick, String uuid) {
        this.nick = nick;
        this.uuid = uuid;
    }

    public String getNick() { return nick; }

    public String getUuid() { return uuid; }

    @Override
    public String toString() {
        return "PlayerData{" +
                "nick='" + nick + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}

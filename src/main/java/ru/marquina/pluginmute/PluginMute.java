package ru.marquina.pluginmute;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import ru.marquina.pluginmute.MuteManager;
import ru.marquina.pluginmute.AntiFloodManager;

public final class PluginMute extends JavaPlugin {

    private MuteManager muteManager;
    private AntiFloodManager antiFloodManager;

    @Override
    public void onEnable() {
        getLogger().info("§aPluginMute has been enabled!");

        this.muteManager = new MuteManager();
        //Пример: порог флуда 2 секунды, не более 4 сообщений, 3 флуд-страйка = мут на 30 минут
        //2 капс-страйка = мут на 10 минут
        //Повторяющееся сообщение 2 раза = мут на 15 минут
        //Последовательный капс 2 раза = мут на 10 минут
        this.antiFloodManager = new AntiFloodManager(2000, 4, 3, 30, 2, 10, 2, 15, 2, 10);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new MuteCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("§cPluginMute has been disabled!");
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public AntiFloodManager getAntiFloodManager() {
        return antiFloodManager;
    }
}

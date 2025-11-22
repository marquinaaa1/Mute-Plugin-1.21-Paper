package ru.marquina.pluginmute;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class ChatListener implements Listener {

    private final PluginMute plugin;

    public ChatListener(PluginMute plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        // Проверка на мут
        if (plugin.getMuteManager().isMuted(player.getUniqueId())) {
            player.sendMessage("§cВы замучены и не можете отправлять сообщения.");
            event.setCancelled(true);
            return;
        }

        AntiFloodManager antiFloodManager = plugin.getAntiFloodManager();
        // Проверка на антифлуд
        if (antiFloodManager.isFlooding(player.getUniqueId())) {
            int floodStrikes = antiFloodManager.getFloodStrikes(player.getUniqueId());
            if (floodStrikes >= antiFloodManager.getMaxFloodStrikes()) {
                long duration = antiFloodManager.getMuteDurationMinutesFlood() * 60; // Переводим минуты в секунды
                plugin.getMuteManager().mutePlayer(player.getUniqueId(), duration);
                player.sendMessage("§cВы были замучены на " + antiFloodManager.getMuteDurationMinutesFlood() + " минут за флуд!");
                antiFloodManager.resetFloodStrikes(player.getUniqueId()); // Сбрасываем страйки после мута
                antiFloodManager.resetFloodCount(player.getUniqueId()); // Сбрасываем счетчик сообщений
                // Можно добавить оповещение для модераторов
            } else {
                player.sendMessage("§cВы отправляете сообщения слишком быстро! (" + floodStrikes + "/" + antiFloodManager.getMaxFloodStrikes() + ") Пожалуйста, подождите.");
            }
            event.setCancelled(true);
            return;
        }

        // Проверка на капс
        if (antiFloodManager.checkCaps(player.getUniqueId(), event.getMessage())) {
            int capsStrikes = antiFloodManager.getCapsStrikes(player.getUniqueId());
            if (capsStrikes >= antiFloodManager.getMaxCapsStrikes()) {
                long duration = antiFloodManager.getMuteDurationMinutesCaps() * 60; // Переводим минуты в секунды
                plugin.getMuteManager().mutePlayer(player.getUniqueId(), duration);
                player.sendMessage("§cВы были замучены на " + antiFloodManager.getMuteDurationMinutesCaps() + " минут за спам капсом!");
                antiFloodManager.resetCapsStrikes(player.getUniqueId()); // Сбрасываем страйки после мута
            } else {
                player.sendMessage("§cПожалуйста, не используйте слишком много заглавных букв! (" + capsStrikes + "/" + antiFloodManager.getMaxCapsStrikes() + ")");
            }
            event.setCancelled(true);
            return;
        }
    }
}

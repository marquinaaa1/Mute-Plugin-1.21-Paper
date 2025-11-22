package ru.marquina.pluginmute;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.Arrays;

public class MuteCommand implements CommandExecutor {

    private final PluginMute plugin;

    public MuteCommand(PluginMute plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pluginmute.mute")) {
            sender.sendMessage("§cУ вас нет разрешения на использование этой команды.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /mute <игрок> <время_в_секундах> [причина]");
            sender.sendMessage("§cИспользование: /unmute <игрок>");
            return true;
        }

        String playerName = args[0];
        Player target = Bukkit.getPlayer(playerName);
        UUID targetUUID;

        if (target != null) {
            targetUUID = target.getUniqueId();
        } else {
            
            sender.sendMessage("§eПредупреждение: игрок может быть оффлайн или имя неточное.");
                    
            return false;
        }

        if (command.getName().equalsIgnoreCase("mute")) {
            try {
                long duration = Long.parseLong(args[1]);
                String reason = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "Без причины";

                plugin.getMuteManager().mutePlayer(targetUUID, duration);
                sender.sendMessage("§aИгрок " + playerName + " замучен на " + duration + " секунд. Причина: " + reason);
                if (target != null) {
                    target.sendMessage("§cВы были замучены на " + duration + " секунд. Причина: " + reason);
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cНеверное время мута. Используйте числовое значение в секундах.");
            }
        } else if (command.getName().equalsIgnoreCase("unmute")) {
            plugin.getMuteManager().unmutePlayer(targetUUID);
            sender.sendMessage("§aИгрок " + playerName + " был размучен.");
            if (target != null) {
                target.sendMessage("§aВы были размучены.");
            }
        }

        return true;
    }
}

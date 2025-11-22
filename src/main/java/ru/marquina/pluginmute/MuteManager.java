package ru.marquina.pluginmute;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuteManager {

    private final Map<UUID, Long> mutedPlayers; // UUID игрока -> время окончания мута (timestamp)

    public MuteManager() {
        this.mutedPlayers = new HashMap<>();
    }

    public void mutePlayer(UUID playerUUID, long durationSeconds) {
        long muteEndTime = System.currentTimeMillis() + (durationSeconds * 1000);
        mutedPlayers.put(playerUUID, muteEndTime);
    }

    public void unmutePlayer(UUID playerUUID) {
        mutedPlayers.remove(playerUUID);
    }

    public boolean isMuted(UUID playerUUID) {
        if (mutedPlayers.containsKey(playerUUID)) {
            long muteEndTime = mutedPlayers.get(playerUUID);
            if (System.currentTimeMillis() < muteEndTime) {
                return true;
            } else {
                // Время мута истекло, убираем из списка
                mutedPlayers.remove(playerUUID);
                return false;
            }
        }
        return false;
    }

    public long getMuteEndTime(UUID playerUUID) {
        return mutedPlayers.getOrDefault(playerUUID, 0L);
    }
}

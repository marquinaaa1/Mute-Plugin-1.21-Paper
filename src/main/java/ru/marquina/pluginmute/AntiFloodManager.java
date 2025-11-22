package ru.marquina.pluginmute;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiFloodManager {

    private final Map<UUID, Long> lastMessageTime;
    private final Map<UUID, Integer> messageCount;
    private final Map<UUID, Integer> floodStrikeCount;
    private final Map<UUID, Integer> capsStrikeCount;
    private final Map<UUID, String> lastSentMessage;
    private final Map<UUID, Integer> duplicateMessageStrikeCount;
    private final int maxDuplicateMessageStrikes;
    private final int muteDurationMinutesDuplicate;
    private final Map<UUID, Boolean> lastMessageWasCaps;
    private final Map<UUID, Integer> consecutiveCapsStrikeCount;
    private final int maxConsecutiveCapsStrikes;
    private final int muteDurationMinutesConsecutiveCaps;
    private final long floodThresholdMillis;
    private final int maxMessages;
    private final int maxFloodStrikes;
    private final int muteDurationMinutesFlood;
    private final int maxCapsStrikes;
    private final int muteDurationMinutesCaps;
    private final int capsPercentageThreshold;

    public AntiFloodManager(long floodThresholdMillis, int maxMessages, int maxFloodStrikes, int muteDurationMinutesFlood,
                            int maxCapsStrikes, int muteDurationMinutesCaps,
                            int maxDuplicateMessageStrikes, int muteDurationMinutesDuplicate,
                            int maxConsecutiveCapsStrikes, int muteDurationMinutesConsecutiveCaps) {
        this.lastMessageTime = new HashMap<>();
        this.messageCount = new HashMap<>();
        this.floodStrikeCount = new HashMap<>();
        this.capsStrikeCount = new HashMap<>();
        this.lastSentMessage = new HashMap<>();
        this.duplicateMessageStrikeCount = new HashMap<>();
        this.lastMessageWasCaps = new HashMap<>();
        this.consecutiveCapsStrikeCount = new HashMap<>();

        this.floodThresholdMillis = floodThresholdMillis;
        this.maxMessages = maxMessages;
        this.maxFloodStrikes = maxFloodStrikes;
        this.muteDurationMinutesFlood = muteDurationMinutesFlood;
        this.maxCapsStrikes = maxCapsStrikes;
        this.muteDurationMinutesCaps = muteDurationMinutesCaps;
        this.maxDuplicateMessageStrikes = maxDuplicateMessageStrikes;
        this.muteDurationMinutesDuplicate = muteDurationMinutesDuplicate;
        this.maxConsecutiveCapsStrikes = maxConsecutiveCapsStrikes;
        this.muteDurationMinutesConsecutiveCaps = muteDurationMinutesConsecutiveCaps;
        this.capsPercentageThreshold = 70;
    }

    public boolean isFlooding(UUID playerUUID) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastMessageTime.getOrDefault(playerUUID, 0L);
        int count = messageCount.getOrDefault(playerUUID, 0);

        if (currentTime - lastTime > floodThresholdMillis) {
            messageCount.put(playerUUID, 1);
            lastMessageTime.put(playerUUID, currentTime);
            return false;
        } else {
            count++;
            messageCount.put(playerUUID, count);
            lastMessageTime.put(playerUUID, currentTime);

            boolean isFlooding = count > maxMessages;
            if (isFlooding) {
                floodStrikeCount.merge(playerUUID, 1, Integer::sum);
            }
            return isFlooding;
        }
    }

    public int getFloodStrikes(UUID playerUUID) {
        return floodStrikeCount.getOrDefault(playerUUID, 0);
    }

    public int getMaxFloodStrikes() {
        return maxFloodStrikes;
    }

    public int getMuteDurationMinutesFlood() {
        return muteDurationMinutesFlood;
    }

    public boolean checkDuplicateMessage(UUID playerUUID, String currentMessage) {
        String lastMessage = lastSentMessage.get(playerUUID);
        lastSentMessage.put(playerUUID, currentMessage);

        if (currentMessage.equalsIgnoreCase(lastMessage)) {
            duplicateMessageStrikeCount.merge(playerUUID, 1, Integer::sum);
        } else {
            duplicateMessageStrikeCount.put(playerUUID, 0);
        }
        return duplicateMessageStrikeCount.getOrDefault(playerUUID, 0) >= maxDuplicateMessageStrikes;
    }

    public int getDuplicateMessageStrikes(UUID playerUUID) {
        return duplicateMessageStrikeCount.getOrDefault(playerUUID, 0);
    }

    public int getMaxDuplicateMessageStrikes() {
        return maxDuplicateMessageStrikes;
    }

    public int getMuteDurationMinutesDuplicate() {
        return muteDurationMinutesDuplicate;
    }

    public void resetDuplicateMessageStrikes(UUID playerUUID) {
        duplicateMessageStrikeCount.remove(playerUUID);
        lastSentMessage.remove(playerUUID);
    }

    public boolean isCapsMessage(String message) {
        if (message.length() < 5) return false;

        int upperCaseCount = 0;
        for (char c : message.toCharArray()) {
            if (Character.isUpperCase(c)) {
                upperCaseCount++;
            }
        }
        return ((double) upperCaseCount / message.length() * 100 > capsPercentageThreshold);
    }

    public boolean checkConsecutiveCaps(UUID playerUUID, String message) {
        boolean currentMessageIsCaps = isCapsMessage(message);
        boolean lastMessageWasCaps = this.lastMessageWasCaps.getOrDefault(playerUUID, false);
        this.lastMessageWasCaps.put(playerUUID, currentMessageIsCaps);

        if (currentMessageIsCaps) {
            if (lastMessageWasCaps) {
                consecutiveCapsStrikeCount.merge(playerUUID, 1, Integer::sum);
            } else {
                consecutiveCapsStrikeCount.put(playerUUID, 1);
            }
        } else {
            consecutiveCapsStrikeCount.put(playerUUID, 0);
        }
        return consecutiveCapsStrikeCount.getOrDefault(playerUUID, 0) >= maxConsecutiveCapsStrikes;
    }

    public int getConsecutiveCapsStrikes(UUID playerUUID) {
        return consecutiveCapsStrikeCount.getOrDefault(playerUUID, 0);
    }

    public int getMaxConsecutiveCapsStrikes() {
        return maxConsecutiveCapsStrikes;
    }

    public int getMuteDurationMinutesConsecutiveCaps() {
        return muteDurationMinutesConsecutiveCaps;
    }

    public void resetConsecutiveCapsStrikes(UUID playerUUID) {
        consecutiveCapsStrikeCount.remove(playerUUID);
        lastMessageWasCaps.remove(playerUUID);
    }

    // Old checkCaps, renamed and modified
    @Deprecated
    public boolean checkCaps(UUID playerUUID, String message) {
        return isCapsMessage(message);
    }

    public int getCapsStrikes(UUID playerUUID) {
        return consecutiveCapsStrikeCount.getOrDefault(playerUUID, 0);
    }

    public int getMaxCapsStrikes() {
        return maxConsecutiveCapsStrikes;
    }

    public int getMuteDurationMinutesCaps() {
        return muteDurationMinutesConsecutiveCaps;
    }

    public void resetFloodStrikes(UUID playerUUID) {
        floodStrikeCount.remove(playerUUID);
    }

    public void resetCapsStrikes(UUID playerUUID) {
        capsStrikeCount.remove(playerUUID);
    }

    public void resetFloodCount(UUID playerUUID) {
        messageCount.remove(playerUUID);
        lastMessageTime.remove(playerUUID);
    }
}

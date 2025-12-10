package n1xend.newyear.daily;

import n1xend.newyear.Main;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.*;

/**
 * Менеджер ежедневных квестов:
 * - раз в день выбирает набор квестов из config.yml -> daily-quests.pool
 * - назначает игроку один из квестов
 * - выдаёт награду, используя QuestManager
 */
public class DailyQuestManager {

    private final Main plugin;
    private final DailyQuestStorage storage;

    public DailyQuestManager(Main plugin) {
        this.plugin = plugin;
        this.storage = plugin.getDailyQuestStorage();

        // Периодически проверяем, не наступил ли новый день
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkReset, 20L, 20L * 60L);
    }

    private void checkReset() {
        if (!plugin.getConfig().getBoolean("daily-quests.enabled", true))
            return;

        String today = LocalDate.now().toString();
        if (!today.equals(storage.getLastReset())) {
            resetDailyQuests(today);
        }
    }

    private void resetDailyQuests(String today) {
        List<String> pool = plugin.getConfig().getStringList("daily-quests.pool");
        if (pool.isEmpty()) return;

        int perDay = plugin.getConfig().getInt("daily-quests.quests-per-day", 2);
        Collections.shuffle(pool);

        List<String> selected = pool.subList(0, Math.min(perDay, pool.size()));
        storage.setActiveQuests(selected);
        storage.setLastReset(today);

        plugin.getLogger().info("[NewYear] Ежедневные квесты обновлены: " + selected);
    }

    public List<String> getTodayQuests() {
        return storage.getActiveQuests();
    }

    /**
     * Назначить игроку конкретный ежедневный квест
     */
    public void assign(Player p, String questKey) {
        if (!plugin.getConfig().getBoolean("daily-quests.enabled", true)) {
            p.sendMessage("§cЕжедневные квесты отключены.");
            return;
        }

        List<String> today = getTodayQuests();
        if (today.isEmpty() || !today.contains(questKey)) {
            p.sendMessage("§cЭтот квест сегодня недоступен.");
            return;
        }

        // Если уже есть квест и он не выполнен
        String current = storage.getPlayerQuest(p.getUniqueId());
        if (current != null && !storage.isPlayerCompleted(p.getUniqueId())) {
            p.sendMessage("§eУ вас уже есть активный ежедневный квест!");
            return;
        }

        storage.setPlayerQuest(p.getUniqueId(), questKey);
        p.sendMessage("§a📅 Вам выдан ежедневный квест: §e" + questKey);
    }

    public void complete(Player p) {
        String questKey = storage.getPlayerQuest(p.getUniqueId());
        if (questKey == null) {
            p.sendMessage("§cУ вас нет активного ежедневного квеста.");
            return;
        }

        if (storage.isPlayerCompleted(p.getUniqueId())) {
            p.sendMessage("§eВы уже выполнили ежедневный квест сегодня!");
            return;
        }

        // Используем общий механизм наград
        plugin.getQuestManager().giveRewardFromConfig(p, questKey);

        storage.setPlayerCompleted(p.getUniqueId(), true);
        p.sendMessage("§6✨ Ежедневный квест выполнен!");
    }

    public void cancelDaily(Player p) {
        storage.clearPlayer(p.getUniqueId());
        p.sendMessage("§cВаш ежедневный квест был сброшен.");
    }

    public String getPlayerQuestKey(Player p) {
        return storage.getPlayerQuest(p.getUniqueId());
    }
}

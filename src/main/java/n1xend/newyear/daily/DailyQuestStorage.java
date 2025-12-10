package n1xend.newyear.daily;

import n1xend.newyear.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Хранение ежедневных квестов в файле dailyquests.yml:
 * - дата последнего обновления
 * - список активных квестов дня
 * - прогресс игрока (какой квест ему выдан, выполнен ли)
 */
public class DailyQuestStorage {

    private final Main plugin;
    private final File file;
    private FileConfiguration cfg;

    public DailyQuestStorage(Main plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "dailyquests.yml");

        if (!file.exists()) {
            plugin.saveResource("dailyquests.yml", false);
        }
        load();
    }

    public void load() {
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLastReset() {
        return cfg.getString("last-reset", "never");
    }

    public void setLastReset(String date) {
        cfg.set("last-reset", date);
        save();
    }

    public List<String> getActiveQuests() {
        return cfg.getStringList("active");
    }

    public void setActiveQuests(List<String> list) {
        cfg.set("active", list);
        save();
    }

    public void setPlayerQuest(UUID uuid, String questKey) {
        cfg.set("players." + uuid + ".quest", questKey);
        cfg.set("players." + uuid + ".completed", false);
        save();
    }

    public String getPlayerQuest(UUID uuid) {
        return cfg.getString("players." + uuid + ".quest", null);
    }

    public boolean isPlayerCompleted(UUID uuid) {
        return cfg.getBoolean("players." + uuid + ".completed", false);
    }

    public void setPlayerCompleted(UUID uuid, boolean value) {
        cfg.set("players." + uuid + ".completed", value);
        save();
    }

    public void clearPlayer(UUID uuid) {
        cfg.set("players." + uuid, null);
        save();
    }
}

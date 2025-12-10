package n1xend.newyear.features;

import n1xend.newyear.Main;
import n1xend.newyear.quests.QuestType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Менеджер квестов:
 * - хранит активный квест игрока
 * - считает прогресс
 * - выдаёт награды по config.yml
 */
public class QuestManager {

    private final Main plugin;

    // Активный квест игрока
    private final Map<UUID, QuestType> activeQuest = new HashMap<>();
    // Прогресс по квесту
    private final Map<UUID, Integer> progress = new HashMap<>();

    public QuestManager(Main plugin) {
        this.plugin = plugin;
    }

    public boolean hasQuest(Player p) {
        return activeQuest.containsKey(p.getUniqueId());
    }

    public QuestType getQuest(Player p) {
        return activeQuest.get(p.getUniqueId());
    }

    /**
     * Запуск квеста
     */
    public void startQuest(Player p, QuestType type) {
        String key = "quests." + type.getKey();

        if (!plugin.getConfig().getBoolean("quests.enabled", true)
                || !plugin.getConfig().getBoolean(key + ".enabled", true)) {
            p.sendMessage("§cЭтот квест сейчас отключён.");
            return;
        }

        activeQuest.put(p.getUniqueId(), type);
        progress.put(p.getUniqueId(), 0);

        String displayName = plugin.getConfig().getString(key + ".display.name", "§eКвест");
        p.sendMessage(displayName + " §a— начат!");
    }

    /**
     * Отмена квеста (при логауте, по таймауту и т.п.)
     */
    public void cancelQuest(Player p) {
        activeQuest.remove(p.getUniqueId());
        progress.remove(p.getUniqueId());
        p.sendMessage("§cВаш активный квест был отменён.");
    }

    /**
     * Увеличение прогресса
     */
    public void addProgress(Player p, int amount) {
        UUID id = p.getUniqueId();
        if (!activeQuest.containsKey(id)) return;

        int newVal = progress.getOrDefault(id, 0) + amount;
        progress.put(id, newVal);

        QuestType type = activeQuest.get(id);
        String key = "quests." + type.getKey();
        int required = plugin.getConfig().getInt(key + ".required", 1);

        p.sendMessage("§7Прогресс: §a" + newVal + "§7/§a" + required);

        if (newVal >= required) {
            finishQuest(p, type);
        }
    }

    /**
     * Завершение квеста
     */
    private void finishQuest(Player p, QuestType type) {
        String key = type.getKey();

        giveRewardFromConfig(p, key);

        activeQuest.remove(p.getUniqueId());
        progress.remove(p.getUniqueId());

        p.sendMessage("§6✨ Вы выполнили квест!");

        World w = p.getWorld();
        w.spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation().add(0, 1, 0),
                80, 0.5, 0.5, 0.5, 0.02);
    }

    /**
     * Награда по config.yml -> quests.<key>.reward
     */
    public void giveRewardFromConfig(Player player, String questKey) {
        String path = "quests." + questKey + ".reward";
        ConfigurationSection r = plugin.getConfig().getConfigurationSection(path);
        if (r == null) return;

        // 1. Предметы: список вида "DIAMOND:3"
        List<String> items = r.getStringList("items");
        for (String entry : items) {
            String[] split = entry.split(":");
            Material mat = Material.matchMaterial(split[0].toUpperCase());
            int amount = 1;

            if (split.length > 1) {
                try {
                    amount = Integer.parseInt(split[1]);
                } catch (NumberFormatException ignored) {}
            }

            if (mat != null) {
                player.getInventory().addItem(new ItemStack(mat, amount));
            }
        }

        // 2. Опыт
        if (r.contains("exp")) {
            int exp = r.getInt("exp");
            player.giveExp(exp);
        }

        // 3. Деньги через Vault (если есть)
        if (r.contains("money") && plugin.getEconomy() != null) {
            double money = r.getDouble("money");
            plugin.getEconomy().depositPlayer(player, money);
        }

        // 4. Команды
        List<String> cmds = r.getStringList("commands");
        for (String cmd : cmds) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    cmd.replace("%player%", player.getName()));
        }
    }

    /**
     * Специальный обработчик: открыт подарок.
     * Используется квестом OPEN_GIFTS.
     */
    public void onGiftOpened(Player p) {
        UUID id = p.getUniqueId();
        if (!activeQuest.containsKey(id)) return;

        if (activeQuest.get(id) == QuestType.OPEN_GIFTS) {
            addProgress(p, 1);
        }
    }

    /**
     * Специальный обработчик: ёлка построена (TreeGenerator)
     */
    public void onTreeGenerated(Player p) {
        UUID id = p.getUniqueId();
        if (!activeQuest.containsKey(id)) return;

        if (activeQuest.get(id) == QuestType.BUILD_TREE) {
            addProgress(p, 1);
        }
    }
}

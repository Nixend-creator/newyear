package n1xend.newyear.listeners;

import n1xend.newyear.Main;
import n1xend.newyear.features.QuestManager;
import n1xend.newyear.quests.QuestType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;

/**
 * Слушатель, который отслеживает действия игрока и обновляет прогресс квеста:
 * - убийство мобов
 * - ломание блоков
 * - броски снежков
 * - извлечение предметов из печи
 * - варка зелий
 * - поднятие предметов
 */
public class QuestProgressListener implements Listener {

    private final Main plugin;
    private final QuestManager questManager;

    public QuestProgressListener(Main plugin) {
        this.plugin = plugin;
        this.questManager = plugin.getQuestManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!questManager.hasQuest(p)) return;

        if (questManager.getQuest(p) == QuestType.BREAK_BLOCKS) {
            questManager.addProgress(p, 1);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;

        if (!questManager.hasQuest(killer)) return;

        if (questManager.getQuest(killer) == QuestType.KILL_MOBS) {
            questManager.addProgress(killer, 1);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        if (!(proj instanceof Snowball snowball)) return;

        if (!(snowball.getShooter() instanceof Player p)) return;
        if (!questManager.hasQuest(p)) return;

        if (questManager.getQuest(p) == QuestType.THROW_SNOWBALLS) {
            questManager.addProgress(p, 1);
        }
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent e) {
        Player p = e.getPlayer();
        if (!questManager.hasQuest(p)) return;

        if (questManager.getQuest(p) == QuestType.COOK_ITEMS) {
            int amount = e.getItemAmount();
            questManager.addProgress(p, amount);
        }
    }

    @EventHandler
    public void onBrew(BrewEvent e) {
        // Тут нет прямой ссылки на игрока, берём ближайшего
        Entity nearest = e.getBlock().getWorld().getNearbyPlayers(e.getBlock().getLocation(), 5)
                .stream().findFirst().orElse(null);
        if (!(nearest instanceof Player p)) return;

        if (!questManager.hasQuest(p)) return;

        if (questManager.getQuest(p) == QuestType.BREW_POTIONS) {
            questManager.addProgress(p, 1);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!questManager.hasQuest(p)) return;

        if (questManager.getQuest(p) == QuestType.COLLECT_ITEMS) {
            questManager.addProgress(p, 1);
        }
    }
}

package n1xend.newyear.security;

import n1xend.newyear.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Защита от выхода во время квеста:
 * - запоминаем время выхода
 * - если вернулся слишком поздно, квест отменяется и может наложиться штраф
 */
public class AntiLogoutManager implements Listener {

    private final Main plugin;
    private final Map<UUID, Long> logoutTimes = new HashMap<>();

    public AntiLogoutManager(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        boolean hasMainQuest = plugin.getQuestManager().hasQuest(p);
        boolean hasDailyQuest = plugin.getDailyQuestManager().getPlayerQuestKey(p) != null;

        if (!hasMainQuest && !hasDailyQuest) return;
        if (!plugin.getConfig().getBoolean("anti-logout.enabled", true)) return;

        logoutTimes.put(p.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (!logoutTimes.containsKey(p.getUniqueId())) return;

        long when = logoutTimes.get(p.getUniqueId());
        long now = System.currentTimeMillis();

        long timeoutMs = plugin.getConfig().getInt("anti-logout.timeout-minutes", 15) * 60_000L;

        if (now - when > timeoutMs) {
            // слишком долго был оффлайн -> штраф
            if (plugin.getQuestManager().hasQuest(p)) {
                plugin.getQuestManager().cancelQuest(p);
            }
            if (plugin.getDailyQuestManager().getPlayerQuestKey(p) != null) {
                plugin.getDailyQuestManager().cancelDaily(p);
            }

            if (plugin.getConfig().getBoolean("anti-logout.punishment.enabled", false)) {
                int expLoss = plugin.getConfig().getInt("anti-logout.punishment.exp-loss", 0);
                int moneyLoss = plugin.getConfig().getInt("anti-logout.punishment.money-loss", 0);

                if (expLoss > 0) {
                    p.giveExp(-expLoss);
                }

                if (moneyLoss > 0 && plugin.getEconomy() != null) {
                    plugin.getEconomy().withdrawPlayer(p, moneyLoss);
                }

                String msg = plugin.getConfig().getString(
                        "anti-logout.punishment.message",
                        "§cВаш квест был отменён из-за выхода во время выполнения!"
                );
                p.sendMessage(msg);
            }
        } else {
            p.sendMessage("§6📌 Ваш квест был восстановлен после возврата на сервер.");
        }

        logoutTimes.remove(p.getUniqueId());
    }
}

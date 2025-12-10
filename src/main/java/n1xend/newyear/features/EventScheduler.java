package n1xend.newyear.features;

import n1xend.newyear.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Автоматический новогодний ивент:
 * - каждый X минут падают подарки
 * - идет снег
 * - пролетает Санта
 */
public class EventScheduler {

    private final Main plugin;
    private BukkitTask task;

    public EventScheduler(Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        int minutes = plugin.getConfig().getInt("auto-event.interval-minutes", 30);

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                player.sendMessage("§6✨ Начался новогодний ивент!");

                if (plugin.getConfig().getBoolean("auto-event.run.snow", true))
                    plugin.getSnowManager().sendSnow(player);

                if (plugin.getConfig().getBoolean("auto-event.run.spawn-gifts", true))
                    plugin.getGiftDropper().dropGift(player);

                if (plugin.getConfig().getBoolean("auto-event.run.santa-fly", true))
                    plugin.getSantaManager().startSantaFlight(player);
            }
        }, 40, minutes * 60L * 20L);
    }

    public void stop() {
        if (task != null)
            task.cancel();
    }
}

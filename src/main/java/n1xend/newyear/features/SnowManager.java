package n1xend.newyear.features;

import n1xend.newyear.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Менеджер реального снега:
 * - частицы
 * - укладка снежных слоёв
 * - звуки
 */
public class SnowManager {

    private final Main plugin;

    public SnowManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Создать снег для игрока
     */
    public void sendSnow(Player p) {
        if (!plugin.getConfig().getBoolean("snow.enabled", true))
            return;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!p.isOnline()) {
                    cancel();
                    return;
                }

                ticks++;

                Location loc = p.getLocation().clone();
                World world = loc.getWorld();

                if (plugin.getConfig().getBoolean("snow.particles", true)) {
                    world.spawnParticle(Particle.SNOWFLAKE, loc.add(0, 1, 0),
                            40, 2, 2, 2, 0.01);
                }

                if (ticks % 20 == 0 &&
                        plugin.getConfig().getBoolean("snow.layering", true)) {

                    Block block = p.getLocation().subtract(0, 1, 0).getBlock();
                    if (block.getType() == Material.AIR ||
                        block.getType() == Material.SNOW) {
                        block.setType(Material.SNOW);
                    }
                }

                if (ticks % 40 == 0 &&
                        plugin.getConfig().getBoolean("snow.sound", true)) {
                    p.playSound(p.getLocation(),
                            Sound.BLOCK_SNOW_BREAK, 1f, 1f);
                }
            }

        }.runTaskTimer(plugin, 0L, 5L);
    }
}

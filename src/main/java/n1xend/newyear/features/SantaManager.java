package n1xend.newyear.features;

import n1xend.newyear.Main;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Летающий Санта — реализован на ArmorStand:
 * - невидимый стенд
 * - голова — голова Санты
 * - траектория круга
 */
public class SantaManager {

    private final Main plugin;

    public SantaManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Запускает полёт Санты над игроком
     */
    public void startSantaFlight(Player p) {

        World world = p.getWorld();
        Location start = p.getLocation().clone().add(0, 15, 0);

        ArmorStand santa = world.spawn(start, ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setGravity(false);
            as.setMarker(true);
            as.getEquipment().setHelmet(new ItemStack(Material.CARVED_PUMPKIN)); // можно заменить моделью
        });

        new BukkitRunnable() {
            double angle = 0;

            @Override
            public void run() {
                if (!santa.isValid()) {
                    cancel();
                    return;
                }

                angle += 0.15;

                Location target = p.getLocation().clone()
                        .add(Math.cos(angle) * 10, 15, Math.sin(angle) * 10);

                santa.teleport(target);
                world.spawnParticle(Particle.CLOUD, target, 10, 0.2, 0.2, 0.2, 0.01);
                world.playSound(target, Sound.ENTITY_PHANTOM_FLAP, 0.2f, 1.6f);
            }
        }.runTaskTimer(plugin, 0L, 2L);

        p.sendMessage("§c🎅 Санта пролетает над вами!");
    }
}

package n1xend.newyear.features;

import n1xend.newyear.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Менеджер выпадения новогодних подарков:
 * - 3D коробка
 * - выбор предмета по редкости
 * - защита от разрушения (анти-дюп)
 */
public class GiftDropper {

    private final Main plugin;
    private final Random random = new Random();

    private final Set<Location> protectedBlocks = new HashSet<>();

    public GiftDropper(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Генерирует подарок над игроком и спавнит лут внизу
     */
    public void dropGift(Player p) {
        World world = p.getWorld();

        new BukkitRunnable() {
            double y = 20;

            @Override
            public void run() {
                Location loc = p.getLocation().clone().add(0, y, 0);
                world.spawnParticle(Particle.FIREWORKS_SPARK, loc, 20, 0.3, 0.3, 0.3, 0.01);
                y -= 0.7;

                if (y <= 1.5) {

                    Location impact = p.getLocation().getBlock().getLocation().add(0, 1, 0);

                    if (plugin.getConfig().getBoolean("gifts-3d.enabled", true)) {
                        buildGift(impact);
                    }

                    world.playSound(impact, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    ItemStack reward = getRandomGift();
                    world.dropItemNaturally(impact.clone().add(0, 1, 0), reward);

                    p.sendMessage("§6🎁 Вы получили подарок: §e" + reward.getType() +
                            " §7x" + reward.getAmount());

                    plugin.getQuestManager().onGiftOpened(p);

                    // Удаляем 3D подарок
                    int despawn = plugin.getConfig().getInt("gifts-3d.despawn", 10);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (Location loc : new HashSet<>(protectedBlocks)) {
                            Block block = loc.getBlock();
                            block.setType(Material.AIR);
                            protectedBlocks.remove(loc);
                        }
                    }, despawn * 20L);

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    /**
     * Построение 3D подарка (куб 3x3x3)
     */
    private void buildGift(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        Material boxMat = Material.matchMaterial(plugin.getConfig().getString("gifts-3d.block", "RED_WOOL"));
        Material ribbonMat = Material.matchMaterial(plugin.getConfig().getString("gifts-3d.ribbon-block", "WHITE_WOOL"));

        if (boxMat == null) boxMat = Material.RED_WOOL;
        if (ribbonMat == null) ribbonMat = Material.WHITE_WOOL;

        int h = 1;
        for (int x = -h; x <= h; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -h; z <= h; z++) {
                    Location b = center.clone().add(x, y, z);
                    b.getBlock().setType(boxMat);
                    protectedBlocks.add(b.clone());
                }
            }
        }

        // лента вертикальная
        for (int y = 0; y <= 2; y++) {
            Location b = center.clone().add(0, y, 0);
            b.getBlock().setType(ribbonMat);
            protectedBlocks.add(b.clone());
        }
    }

    /**
     * Выбор подарка по редкости
     */
    private ItemStack getRandomGift() {

        int roll = random.nextInt(100) + 1;

        int common = plugin.getConfig().getInt("gifts.common.chance", 60);
        int rare = plugin.getConfig().getInt("gifts.rare.chance", 30);

        if (roll <= common)
            return pick("gifts.common.items");

        roll -= common;

        if (roll <= rare)
            return pick("gifts.rare.items");

        return pick("gifts.epic.items");
    }

    private ItemStack pick(String path) {
        List<String> list = plugin.getConfig().getStringList(path);
        if (list.isEmpty()) return new ItemStack(Material.COOKIE, 1);

        String s = list.get(random.nextInt(list.size()));
        String[] parts = s.split(":");
        Material mat = Material.matchMaterial(parts[0], true);

        int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;

        if (mat == null) mat = Material.DIRT;
        return new ItemStack(mat, amount);
    }

    /**
     * Признак, что блок принадлежит подарку (нельзя ломать)
     */
    public boolean isProtectedBlock(Location loc) {
        return protectedBlocks.contains(loc);
    }
}

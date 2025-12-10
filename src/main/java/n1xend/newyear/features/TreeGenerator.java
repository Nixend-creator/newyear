package n1xend.newyear.features;

import n1xend.newyear.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Стандартная мини-ёлка:
 * - 1 блок ствола
 * - листья вокруг и сверху
 * - участвует в квесте "build_tree"
 */
public class TreeGenerator {

    private final Main plugin;

    public TreeGenerator(Main plugin) {
        this.plugin = plugin;
    }

    public void buildTree(Player p) {

        Location loc = p.getLocation().clone().add(0, 0, 0).getBlock().getLocation();

        World world = loc.getWorld();
        if (world == null) return;

        Block base = world.getBlockAt(loc);
        base.setType(Material.SPRUCE_LOG);

        // листва слоями
        world.getBlockAt(loc.clone().add(0, 1, 0)).setType(Material.SPRUCE_LEAVES);
        world.getBlockAt(loc.clone().add(1, 1, 0)).setType(Material.SPRUCE_LEAVES);
        world.getBlockAt(loc.clone().add(-1, 1, 0)).setType(Material.SPRUCE_LEAVES);
        world.getBlockAt(loc.clone().add(0, 1, 1)).setType(Material.SPRUCE_LEAVES);
        world.getBlockAt(loc.clone().add(0, 1, -1)).setType(Material.SPRUCE_LEAVES);

        // верхушка
        world.getBlockAt(loc.clone().add(0, 2, 0)).setType(Material.GLOWSTONE);

        plugin.getQuestManager().onTreeGenerated(p);

        p.sendMessage("§2🎄 Вы построили новогоднюю ёлку!");
    }
}

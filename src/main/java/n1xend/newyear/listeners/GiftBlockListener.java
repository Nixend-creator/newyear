package n1xend.newyear.listeners;

import n1xend.newyear.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Запрещает ломать блоки, принадлежащие 3D-подарку (анти-дюп)
 */
public class GiftBlockListener implements Listener {

    private final Main plugin;

    public GiftBlockListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (plugin.getGiftDropper().isProtectedBlock(e.getBlock().getLocation())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c🎁 Нельзя ломать новогодние подарки!");
        }
    }
}

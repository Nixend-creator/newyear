package n1xend.newyear.daily;

import n1xend.newyear.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Обработка кликов по меню ежедневных квестов
 */
public class DailyQuestMenuListener implements Listener {

    private final Main plugin;

    public DailyQuestMenuListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().equals("📅 Ежедневные квесты")) return;

        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player p)) return;

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        // Защита от спама по GUI
        if (!plugin.getGuiProtector().canSelect(p)) return;

        Material clicked = item.getType();

        for (String key : plugin.getDailyQuestManager().getTodayQuests()) {
            String cfgBase = "quests." + key;
            String matName = plugin.getConfig().getString(cfgBase + ".display.material", "BOOK");
            Material mat = Material.matchMaterial(matName.toUpperCase());
            if (mat == null) continue;

            if (mat == clicked) {
                plugin.getDailyQuestManager().assign(p, key);
                p.closeInventory();
                return;
            }
        }
    }
}

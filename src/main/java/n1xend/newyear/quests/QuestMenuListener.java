package n1xend.newyear.quests;

import n1xend.newyear.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Обработчик кликов по меню "Новогодние квесты"
 */
public class QuestMenuListener implements Listener {

    private final Main plugin;

    public QuestMenuListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!e.getView().getTitle().equals("🎄 Новогодние квесты")) return;

        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        // Защита от слишком быстрых кликов
        if (!plugin.getGuiProtector().canSelect(player)) return;

        Material clicked = item.getType();

        // Ищем квест по material
        for (QuestType type : QuestType.values()) {
            String base = "quests." + type.getKey();

            String matName = plugin.getConfig().getString(base + ".display.material", "BOOK");
            Material mat = Material.matchMaterial(matName.toUpperCase());
            if (mat == null) continue;

            if (mat == clicked) {
                plugin.getQuestManager().startQuest(player, type);
                player.closeInventory();
                return;
            }
        }
    }
}

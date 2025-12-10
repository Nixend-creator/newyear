package n1xend.newyear.quests;

import n1xend.newyear.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.List;

/**
 * Меню выбора квестов /ny quest
 */
public class QuestMenu {

    private final Main plugin;

    public QuestMenu(Main plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "🎄 Новогодние квесты");

        // фон
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        int slot = 10;

        for (QuestType type : QuestType.values()) {
            String key = "quests." + type.getKey();
            ConfigurationSection sec = plugin.getConfig().getConfigurationSection(key);
            if (sec == null) continue;
            if (!sec.getBoolean("enabled", true)) continue;

            String matName = sec.getString("display.material", "BOOK");
            Material mat = Material.matchMaterial(matName.toUpperCase());
            if (mat == null) mat = Material.BOOK;

            String name = sec.getString("display.name", "§eКвест");
            List<String> lore = sec.getStringList("display.lore");

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
            slot += 2;
        }

        player.openInventory(inv);
    }
}

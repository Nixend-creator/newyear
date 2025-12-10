package n1xend.newyear.daily;

import n1xend.newyear.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Меню "/ny daily" — список доступных сегодня ежедневных квестов
 */
public class DailyQuestMenu {

    private final Main plugin;

    public DailyQuestMenu(Main plugin) {
        this.plugin = plugin;
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "📅 Ежедневные квесты");

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);

        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        List<String> today = plugin.getDailyQuestManager().getTodayQuests();
        int slot = 10;

        for (String key : today) {
            String base = "quests." + key;
            ConfigurationSection sec = plugin.getConfig().getConfigurationSection(base);
            if (sec == null) continue;

            String matName = sec.getString("display.material", "BOOK");
            Material mat = Material.matchMaterial(matName.toUpperCase());
            if (mat == null) mat = Material.BOOK;

            String name = sec.getString("display.name", "§eКвест");
            List<String> lore = sec.getStringList("display.lore");

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name + " §b(ежедневный)");
            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
            slot += 2;
        }

        p.openInventory(inv);
    }
}

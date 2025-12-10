package n1xend.newyear.security;

import n1xend.newyear.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Защита от частых открытий GUI и быстрого спама кликов
 */
public class GUIClickProtector {

    private final Main plugin;
    private final Map<UUID, Long> openCooldown = new HashMap<>();
    private final Map<UUID, Long> selectCooldown = new HashMap<>();

    public GUIClickProtector(Main plugin) {
        this.plugin = plugin;
    }

    public boolean canOpen(Player p) {
        if (!plugin.getConfig().getBoolean("antiguiclick.enabled", true))
            return true;

        long now = System.currentTimeMillis();
        long cd = plugin.getConfig().getInt("antiguiclick.gui-open-cd", 30) * 50L;
        long until = openCooldown.getOrDefault(p.getUniqueId(), 0L);

        if (now < until) {
            return false;
        }

        openCooldown.put(p.getUniqueId(), now + cd);
        return true;
    }

    public boolean canSelect(Player p) {
        if (!plugin.getConfig().getBoolean("antiguiclick.enabled", true))
            return true;

        long now = System.currentTimeMillis();
        long cd = plugin.getConfig().getInt("antiguiclick.gui-select-cd", 50) * 50L;
        long until = selectCooldown.getOrDefault(p.getUniqueId(), 0L);

        if (now < until) {
            return false;
        }

        selectCooldown.put(p.getUniqueId(), now + cd);
        return true;
    }
}

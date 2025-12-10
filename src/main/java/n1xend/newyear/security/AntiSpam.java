package n1xend.newyear.security;

import n1xend.newyear.Main;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Анти-спам команд: /ny gift, /ny santa, /ny daily и т.д.
 */
public class AntiSpam {

    private final Main plugin;
    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public AntiSpam(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Проверка и установка кулдауна по ключу (например "gift")
     */
    public boolean check(Player p, String key) {

        if (!plugin.getConfig().getBoolean("antispam.enabled", true))
            return true;

        int cdSeconds = plugin.getConfig().getInt("antispam.cooldowns." + key, 0);
        if (cdSeconds <= 0) return true;

        cooldowns.putIfAbsent(key, new HashMap<>());
        Map<UUID, Long> map = cooldowns.get(key);

        long now = System.currentTimeMillis();
        long until = map.getOrDefault(p.getUniqueId(), 0L);

        if (now < until) {
            long left = (until - now) / 1000;
            p.sendMessage("§c⏳ Подождите " + left + " сек. перед повторным использованием.");
            return false;
        }

        map.put(p.getUniqueId(), now + cdSeconds * 1000L);
        return true;
    }
}

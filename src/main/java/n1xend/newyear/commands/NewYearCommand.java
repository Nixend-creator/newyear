package n1xend.newyear.commands;

import n1xend.newyear.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Главная команда /ny — содержит все подкоманды.
 */
public class NewYearCommand implements CommandExecutor {

    private final Main plugin;

    public NewYearCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

        if (!(s instanceof Player p)) {
            s.sendMessage("Эту команду может использовать только игрок!");
            return true;
        }

        // /ny
        if (args.length == 0) {
            sendHelp(p);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            case "help":
                sendHelp(p);
                return true;

            case "gift":
                return handleGift(p);

            case "santa":
                return handleSanta(p);

            case "snow":
                return handleSnow(p, false);

            case "realsnow":
                return handleSnow(p, true);

            case "tree":
                return handleTree(p);

            case "quests":
            case "quest":
                return handleQuestMenu(p);

            case "daily":
                return handleDailyMenu(p);

            case "cancel":
                return handleCancel(p);
        }

        p.sendMessage(ChatColor.RED + "Неизвестная команда. Используйте: /ny help");
        return true;
    }

    // ================================================================
    // /ny help
    // ================================================================
    private void sendHelp(Player p) {
        p.sendMessage("§6§lНовогодний плагин — команды:");
        p.sendMessage("§e/ny gift §7— получить подарок");
        p.sendMessage("§e/ny santa §7— вызвать пролёт Санты");
        p.sendMessage("§e/ny snow §7— локальный снег");
        p.sendMessage("§e/ny realsnow §7— снег + укладка слоёв");
        p.sendMessage("§e/ny tree §7— построить ёлку");
        p.sendMessage("§e/ny quests §7— меню новогодних квестов");
        p.sendMessage("§e/ny daily §7— ежедневные задания");
        p.sendMessage("§e/ny cancel §7— отменить ваш текущий квест");
    }

    // ================================================================
    // /ny gift
    // ================================================================
    private boolean handleGift(Player p) {
        if (!plugin.getAntiSpam().check(p, "gift")) return true;

        plugin.getGiftDropper().dropGift(p);
        p.sendMessage("§6🎁 Подарок падает с неба...");
        return true;
    }

    // ================================================================
    // /ny santa
    // ================================================================
    private boolean handleSanta(Player p) {
        if (!plugin.getAntiSpam().check(p, "santa")) return true;

        plugin.getSantaManager().startSantaFlight(p);
        return true;
    }

    // ================================================================
    // /ny snow + /ny realsnow
    // ================================================================
    private boolean handleSnow(Player p, boolean full) {
        if (!plugin.getAntiSpam().check(p, "snow")) return true;

        if (full) {
            plugin.getSnowManager().sendSnow(p);
            p.sendMessage("§f❄ Полный снег активирован!");
        } else {
            p.sendMessage("§f❄ Снегопад включён!");
            p.spawnParticle(org.bukkit.Particle.SNOWFLAKE, p.getLocation().add(0,1,0), 70, 2.5, 3, 2.5, 0.01);
        }
        return true;
    }

    // ================================================================
    // /ny tree
    // ================================================================
    private boolean handleTree(Player p) {
        if (!plugin.getAntiSpam().check(p, "tree")) return true;

        plugin.getTreeGenerator().buildTree(p);
        return true;
    }

    // ================================================================
    // /ny quests
    // ================================================================
    private boolean handleQuestMenu(Player p) {
        if (!plugin.getGuiProtector().canOpen(p)) return true;

        plugin.getQuestMenu().open(p);
        return true;
    }

    // ================================================================
    // /ny daily
    // ================================================================
    private boolean handleDailyMenu(Player p) {
        if (!plugin.getGuiProtector().canOpen(p)) return true;

        plugin.getDailyQuestMenu().open(p);
        return true;
    }

    // ================================================================
    // /ny cancel
    // ================================================================
    private boolean handleCancel(Player p) {

        boolean hadQuest = false;

        if (plugin.getQuestManager().hasQuest(p)) {
            plugin.getQuestManager().cancelQuest(p);
            hadQuest = true;
        }

        if (plugin.getDailyQuestManager().getPlayerQuestKey(p) != null) {
            plugin.getDailyQuestManager().cancelDaily(p);
            hadQuest = true;
        }

        if (!hadQuest) {
            p.sendMessage("§cУ вас нет активных квестов.");
        }

        return true;
    }
}

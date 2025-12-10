package n1xend.newyear;

import n1xend.newyear.commands.NewYearCommand;
import n1xend.newyear.daily.*;
import n1xend.newyear.features.*;
import n1xend.newyear.listeners.*;
import n1xend.newyear.quests.*;
import n1xend.newyear.security.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

/**
 * Главный класс новогоднего плагина
 * Загружает все модули: снег, квесты, Санту, подарки, защиту, ежедневные задания.
 */
public class Main extends JavaPlugin {

    private SnowManager snowManager;
    private GiftDropper giftDropper;
    private SantaManager santaManager;
    private TreeGenerator treeGenerator;
    private QuestManager questManager;
    private EventScheduler eventScheduler;

    private QuestMenu questMenu;

    private DailyQuestManager dailyQuestManager;
    private DailyQuestMenu dailyQuestMenu;
    private DailyQuestStorage dailyQuestStorage;

    private AntiSpam antiSpam;
    private GUIClickProtector guiProtector;

    private Economy economy;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        setupEconomy();

        // === Менеджеры функций ===
        snowManager = new SnowManager(this);
        giftDropper = new GiftDropper(this);
        santaManager = new SantaManager(this);
        treeGenerator = new TreeGenerator(this);
        questManager = new QuestManager(this);
        eventScheduler = new EventScheduler(this);

        questMenu = new QuestMenu(this);

        // === Ежедневные квесты ===
        dailyQuestStorage = new DailyQuestStorage(this);
        dailyQuestManager = new DailyQuestManager(this);
        dailyQuestMenu = new DailyQuestMenu(this);

        // === Защита ===
        antiSpam = new AntiSpam(this);
        guiProtector = new GUIClickProtector(this);

        // === Регистрация команд ===
        if (getCommand("ny") != null) {
            getCommand("ny").setExecutor(new NewYearCommand(this));
        }

        // === Регистрация событий ===
        Bukkit.getPluginManager().registerEvents(new QuestMenuListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DailyQuestMenuListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GiftBlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new QuestProgressListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AntiLogoutManager(this), this);

        // === Авто-ивент ===
        if (getConfig().getBoolean("auto-event.enabled", false)) {
            eventScheduler.start();
        }

        getLogger().info("🎄 NewYear v2 успешно включён!");
    }

    @Override
    public void onDisable() {
        if (eventScheduler != null) eventScheduler.stop();
        getLogger().info("❄ NewYear v2 выключен.");
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            economy = null;
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) economy = rsp.getProvider();
    }

    public SnowManager getSnowManager() { return snowManager; }
    public GiftDropper getGiftDropper() { return giftDropper; }
    public SantaManager getSantaManager() { return santaManager; }
    public TreeGenerator getTreeGenerator() { return treeGenerator; }
    public QuestManager getQuestManager() { return questManager; }
    public EventScheduler getEventScheduler() { return eventScheduler; }

    public QuestMenu getQuestMenu() { return questMenu; }

    public DailyQuestManager getDailyQuestManager() { return dailyQuestManager; }
    public DailyQuestMenu getDailyQuestMenu() { return dailyQuestMenu; }
    public DailyQuestStorage getDailyQuestStorage() { return dailyQuestStorage; }

    public AntiSpam getAntiSpam() { return antiSpam; }
    public GUIClickProtector getGuiProtector() { return guiProtector; }
    public Economy getEconomy() { return economy; }
}

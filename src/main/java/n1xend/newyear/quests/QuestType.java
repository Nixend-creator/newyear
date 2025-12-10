package n1xend.newyear.quests;

/**
 * Типы квестов. Ключи совпадают с разделами в config.yml -> quests.<key>
 */
public enum QuestType {

    OPEN_GIFTS("open_gifts"),
    KILL_MOBS("kill_mobs"),
    BREAK_BLOCKS("break_blocks"),
    COLLECT_ITEMS("collect_items"),
    THROW_SNOWBALLS("throw_snowballs"),
    BUILD_TREE("build_tree"),
    COOK_ITEMS("cook_items"),
    BREW_POTIONS("brew_potions");

    private final String configKey;

    QuestType(String configKey) {
        this.configKey = configKey;
    }

    public String getKey() {
        return configKey;
    }
}

package lab.solarstorm.boblaborknockffa;

import lab.solarstorm.boblaborknockffa.game.*;
import lab.solarstorm.boblaborknockffa.token.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.logging.Logger;

public final class BobLaborKnockFFA extends JavaPlugin {
    public static JavaPlugin javaPlugin;
    public static Plugin plugin;
    public static BobLaborKnockFFA instance;

    public static Logger logger;
    public static CommandSender console;

    public static String PREFIX = "§b§l[BobTony]§r§f ";

    public static Team team;

    @Override
    public void onEnable() {
        ItemManager.initialize(getDataFolder());
        plugin = BobLaborKnockFFA.getPlugin(BobLaborKnockFFA.class);
        javaPlugin = JavaPlugin.getPlugin(BobLaborKnockFFA.class);
        instance = this;

        logger = plugin.getLogger();
        console = Bukkit.getConsoleSender();

        createVanillaStyleTeam();
        getCommand("token").setExecutor(new TokenManager());

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new GameManager(), this);
        pluginManager.registerEvents(new ItemManager(), this);
        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new InventoryLayoutEditor(), this);
        pluginManager.registerEvents(new CPSListener(), this);

    }

    @Override
    public void onDisable() {
        ItemManager.saveData();
    }

    private void createVanillaStyleTeam() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getMainScoreboard(); // Vanilla-Scoreboard verwenden

        team = board.getTeam("playerbase");
        if (team == null) {
            team = board.registerNewTeam("playerbase");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setAllowFriendlyFire(true);
        }
    }
}

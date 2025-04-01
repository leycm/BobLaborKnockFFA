package lab.solarstorm.boblaborknockffa.game;

import lab.solarstorm.boblaborknockffa.map.Maps;
import lab.solarstorm.boblaborknockffa.minigame.CoinFlip;
import lab.solarstorm.boblaborknockffa.token.KopfGeldManager;
import lab.solarstorm.boblaborknockffa.token.TokenManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import java.util.*;

import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.*;

public final class GameManager implements Listener {

    private static HashMap<Location, Integer> playerBlocksHash = new HashMap<>();
    private static final int blockSec = 3;
    @Getter
    private static int timer = resetTimer();
    private static final HashMap<UUID, Player> lastDamager = new HashMap<>();
    private static final Random random = new Random();
    private static Scoreboard scoreboard = null;
    private static Objective objective = null;

    public GameManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                handelPlayerBlocks();
                handelTimer();
                CPSListener.updateCPS();
                updateScoreboard();
                CoinFlip.handleRequestTimeouts();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private static void handelTimer() {
        timer = timer - 1;

        //Bukkit.broadcast(Component.text(Maps.getTimeToNextSwitch(TimeUnit.MINUTES) + "min"));
        //Bukkit.broadcast(Component.text(Maps.getTimeToNextSwitch(TimeUnit.SECONDS) + "sec"));
        //Bukkit.broadcast(Component.text(Maps.getTimeToNextSwitch() + "knock"));
        //Bukkit.broadcast(Component.text(Maps.getTimeToNextSwitch() * 2 + "ticks"));

        if (timer == 0) {
            Maps.switchMap();
            resetTimer();
        }

        else if (timer == 60 * 10){
            Bukkit.broadcast(Component.text(PREFIX + "§fMap wechselt in §e" + timer / 10 + "sek§f. Macht euch bereit auf die neue Map"));
        }

        else if (timer < 10 * 10 && timer > 3 * 10 && timer % 10 == 0) {
            Bukkit.broadcast(Component.text(PREFIX + "§fMap restard in §e" + timer / 10 + "sek"));
        }

        else if (timer < 4 * 10 && timer % 10 == 0){
            Bukkit.broadcast(Component.text(PREFIX + "§fMap restard in §c" + timer / 10 + "sek"));
        }
    }

    private static int resetTimer(){return timer = Maps.getMapSwitchTime() * 60 * 10;}

    private static void handelPlayerBlocks() {
        Iterator<Map.Entry<Location, Integer>> iterator = playerBlocksHash.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, Integer> entry = iterator.next();
            int ticks = entry.getValue() - 1;
            entry.setValue(ticks);
            Location loc = entry.getKey();

            if (ticks < 5) {
                Location particleLoc = loc.clone().add(0.5, 0.7, 0.5);
                loc.getWorld().spawnParticle(
                        Particle.BLOCK_CRUMBLE,
                        particleLoc,
                        5,
                        0.2, 0.2, 0.2,
                        0.01,
                        loc.getBlock().getBlockData()
                );
            }

            if (ticks == 0) {
                loc.getBlock().setType(Material.AIR);
                iterator.remove();
            }
        }
    }

    @EventHandler
    public static void onPlayerBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!player.getGameMode().equals(GameMode.CREATIVE)){
            playerBlocksHash.put(event.getBlock().getLocation(), blockSec * 10);
            if(player.getLocation().getBlockY() > Maps.getLocation().getBlockY() - 5) event.setCancelled(true);}
    }

    @EventHandler
    public static void onPlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.CREATIVE))
            event.setCancelled(true);
    }

    @EventHandler
    public static void onPlayerCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.getGameMode().equals(GameMode.CREATIVE))
            event.setCancelled(true);
    }

    @EventHandler
    public static void onPlayerDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.CREATIVE))
            event.setCancelled(true);
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Maps.teleportPlayer(player);
        team.addEntry(player.getName());
    }

    @EventHandler
    public static void onHungerChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public static void onHungerChange(PlayerItemConsumeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack cursorItem = player.getItemOnCursor();

        if (!cursorItem.getType().isAir()) {
            player.setItemOnCursor(null);
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public static void onPlayerEnderPearlHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;

        Player player = (Player) pearl.getShooter();

        if (event.getHitBlock().getLocation().getY() > Maps.getLocation().getBlockY() - 5) {
            event.setCancelled(true);
            player.getInventory().addItem(ItemManager.createEnderPearl());
            pearl.remove();
        } else {
            player.setVelocity(new Vector( 0, 0, 0));
        }
    }

    private static final String[] VOID_PUSHED_MESSAGES = {
            "§b%s §7wurde von §b%s §7ins Void geschubst.",
            "§b%s §7ist durch §b%s §7ins Nichts gestürzt.",
            "§b%s §7wurde von §b%s §7vom Rand gedrängt.",
            "§b%s §7fiel durch §b%s§7s Aktion ins Void.",
            "§b%s §7wurde von §b%s §7ins Void befördert.",
            "§b%s §7verlor den Kampf gegen §b%s §7am Abgrund.",
            "§b%s §7wurde von §b%s §7zum Void-Fall gezwungen.",
            "§b%s §7landete durch §b%s §7im Nichts.",
            "§b%s §7wurde von §b%s §7ins Leere geschickt.",
            "§b%s §7stürzte dank §b%s §7ins Void.",

            "§b%s §7ließ sich von §b%s §7überzeugen, dass 'Void-Diving' ein Olympia-Sport ist",
            "§b%s §7wurde von §b%s §7direkt ins Bewerbungsgespräch bei NASA geschubst",
            "§b%s §7glaubte §b%s§7s Lüge, das Void sei ein neuer Nether-Portal-Typ",
            "§b%s §7nahm §b%s§7s 'Trustfall Challenge' etwas zu wörtlich",
            "§b%s §7dachte, §b%s §7zeige ihm 'coole Loot-Spots' im Void",
            "§b%s §7wurde von §b%s §7als lebendiger Müll ins Void recycelt",
            "§b%s §7vertraute §b%s§7s Tipp 'Elytren braucht man hier nicht!'",
            "§b%s §7wollte mit §b%s §7zur Mondbasis. Leider ohne Rakete",
            "§b%s §7testete §b%s§7s neu erfundenes 'Abgrund-Teleportgerät'",
            "§b%s §7bekam von §b%s §7eine kostenlose Void-Cruise geschenkt",

            "§b%s §7wurde von §b%s §7mit bayerischer Effizienz ins Void befördert - 'Mia san mia im Abgrund!'",
            "§b%s §7erhielt von §b%s §7eine bayerische Variante von 'Flugangst' - direkt ins Void!",
            "§b%s §7wurde von §b%s §7zum 'Void-Wiesn-Besuch' überredet - ohne Rückflugticket!"
    };

    private static final String[] DIRECT_KILL_MESSAGES = {
            "§b%s §7wurde von §b%s §7besiegt.",
            "§b%s §7ist durch §b%s §7gefallen.",
            "§b%s §7wurde von §b%s §7eliminiert.",
            "§b%s §7konnte §b%s §7nicht widerstehen.",
            "§b%s §7wurde von §b%s §7ausgeschaltet.",
            "§b%s §7verlor den Kampf gegen §b%s.",
            "§b%s §7wurde von §b%s §7getötet.",
            "§b%s §7erlag §b%s§7s Angriff.",
            "§b%s §7wurde von §b%s §7vernichtet.",
            "§b%s §7wurde von §b%s §7gestoppt.",

            "§b%s §7wurde von §b%s §7in die Minecraft-Folge 'Dinner ohne Winner' verwandelt",
            "§b%s §7lernte von §b%s §7auf die harte Tour: Nein heißt Nein",
            "§b%s §7versuchte, §b%s §7zu reporten. Antrag abgelehnt",
            "§b%s §7wurde von §b%s §7zum menschlichen Creeper-Ersatz befördert",
            "§b%s §7dachte, §b%s §7sei ein Noob. Teuerster Irrtum ever",
            "§b%s §7erhielt von §b%s §7eine Gratis-Masterclass im Item-Droppen",
            "§b%s §7wurde von §b%s §7zur Laufenden Loot-Container degradiert",
            "§b%s §7glaubte, §b%s §7sei ein Tutorial-Bot",

            "§b%s §7wurde von §b%s §7erledigt - 'Des war a klarer Fall fürn Freistaat!'",
            "§b%s §7erhielt von §b%s §7eine bayerische Standpauke - mit tödlichem Ausgang",
            "§b%s §7wurde von §b%s §7abserviert - 'So geht bayerische Politik!'"
    };

    private static final String[] VOID_SUICIDE_MESSAGES = {
            "§b%s §7stürzte ins Void.",
            "§b%s §7verlor den Kampf gegen die Schwerkraft.",
            "§b%s §7fiel ins Nichts.",
            "§b%s §7landete im Void.",
            "§b%s §7konnte dem Abgrund nicht entkommen.",
            "§b%s §7wurde vom Void verschlungen.",
            "§b%s §7fiel in die Leere.",
            "§b%s §7verpasste den Rand.",
            "§b%s §7starb durch Void-Schaden.",
            "§b%s §7versank im Abgrund.",

            "§b%s §7probierte den TikTok-Hack 'Wie man Elytren glitcht'",
            "§b%s §7wollte beweisen, dass Flat-Earther recht haben",
            "§b%s §7bräuchte eine Anti-Gravity-Potion",
            "§b%s §7dachte, 'Void is Lava' wäre ein lustiger Game-Mode",
            "§b%s §7versuchte, als erster Influencer ins Void zu 'contenten'",
            "§b%s §7vertraute einem '5-Minuten-Void-Hack' Youtube-Tutorial",
            "§b%s §7wollte testen, ob das Void Endermen-Safe ist",
            "§b%s §7ging auf 'Freefall-Date' mit sich selbst",
            "§b%s §7dachte, der Abgrund wäre nur ein Render-Distanz-Bug",
            "§b%s §7suchte nach Herobrine. Fand stattdessen das Void",

            "§b%s §7verwechselte das Void mit dem Münchner Oktoberfest-Zelt - 'O'zapft is... im Abgrund!'",
            "§b%s §7suchte das bayerische Reinheitsgebot im Void - und fand nur den Tod",
            "§b%s §7dachte, das Void wäre der neue Freistaat Bayern - 'Leider falsch gedacht!'"
    };

    @EventHandler
    public static void onPlayerDamage(EntityDamageEvent event) {
        if(event.getDamageSource().getDamageType().equals(DamageType.FALL)){
            event.setCancelled(true); return;
        }

        if(event.getDamageSource().getDamageType().equals(DamageType.ENDER_PEARL)){
            event.setCancelled(true); return;
        }

        if (!(event.getEntity() instanceof Player player)) return;

        if(player.getLocation().getBlockY() > Maps.getLocation().getBlockY() - 5) event.setCancelled(true);

        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Player) {
                lastDamager.put(player.getUniqueId(), (Player) damager);
            }
        }

        if (isFatalDamage(player, event)) {
            event.setCancelled(true);
            handleRespawn(player);
            updateStatsAndBroadcast(player, event);
        }
    }

    private static boolean isFatalDamage(Player player, EntityDamageEvent event) {
        return event.getCause() == EntityDamageEvent.DamageCause.VOID
                || player.getHealth() - event.getFinalDamage() <= 1.0;
    }

    private static void handleRespawn(Player player) {
        player.teleport(Maps.getLocation());
        player.setHealth(20);
        player.setFoodLevel(20);
        ItemManager.reloadPlayerInv(player);

        List<EnderPearl> enderPearls = getActiveEnderPearls(player);

        for (EnderPearl enderPearl : enderPearls)
            enderPearl.remove();
    }

    private static List<EnderPearl> getActiveEnderPearls(Player player) {
        List<EnderPearl> enderPearls = new ArrayList<>();

        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof EnderPearl enderPearl) {
                Player shooter = (Player) enderPearl.getShooter();

                assert shooter != null;

                if (shooter.getUniqueId().equals(player.getUniqueId()))
                    enderPearls.add(enderPearl);
            }
        }

        return enderPearls;
    }

    private static void updateStatsAndBroadcast(Player player, EntityDamageEvent event) {
        Player killer = lastDamager.get(player.getUniqueId());

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (killer != null) {
                handleVoidPushedDeath(player, killer);
            } else {
                handleVoidSuicide(player);
            }
        } else if (event instanceof EntityDamageByEntityEvent) {
            handleDirectKill(player, (EntityDamageByEntityEvent) event);
        }
    }

    private static void handleVoidPushedDeath(Player victim, Player killer) {
        killer.setStatistic(Statistic.PLAYER_KILLS, killer.getStatistic(Statistic.PLAYER_KILLS) + 1);
        victim.setStatistic(Statistic.DEATHS, victim.getStatistic(Statistic.DEATHS) + 1);
        broadcastRandomMessage(VOID_PUSHED_MESSAGES, victim.getName(), killer.getName());
        lastDamager.remove(victim.getUniqueId());
        TokenManager.addPlayerKill(killer);
        TokenManager.breakStreak(victim);
        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                100,
                0,
                true,
                true
        ));
        KopfGeldManager.claimKopfgeld(killer, victim);
    }

    private static void handleDirectKill(Player victim, EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player killer = (Player) event.getDamager();
            killer.setStatistic(Statistic.PLAYER_KILLS, killer.getStatistic(Statistic.PLAYER_KILLS) + 1);
            victim.setStatistic(Statistic.DEATHS, victim.getStatistic(Statistic.DEATHS) + 1);
            broadcastRandomMessage(DIRECT_KILL_MESSAGES, victim.getName(), killer.getName());
            TokenManager.addPlayerKill(killer);
            TokenManager.breakStreak(victim);
            killer.addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    100,
                    0,
                    true,
                    true
            ));
            KopfGeldManager.claimKopfgeld(killer, victim);
        }
    }

    private static void handleVoidSuicide(Player player) {
        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1);
        broadcastRandomMessage(VOID_SUICIDE_MESSAGES, player.getName());
        TokenManager.breakStreak(player);
    }

    private static void broadcastRandomMessage(String[] messages, String... args) {
        String rawMessage = messages[random.nextInt(messages.length)];
        Bukkit.broadcastMessage(String.format(rawMessage, args));
    }

    public static void updateScoreboard() {
        for(Player player : Bukkit.getOnlinePlayers()){
            updateScoreboard(player);
        }
    }

    public static void updateScoreboard(Player player) {
        Scoreboard playerScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective playerObjective = playerScoreboard.registerNewObjective("playerSidebar", "dummy", "  §b§lBobTonyFFA  ");
        playerObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> scoreboardText = new ArrayList<>();

        int kills = player.getStatistic(Statistic.PLAYER_KILLS);
        int deaths = player.getStatistic(Statistic.DEATHS);
        int playtime = player.getStatistic(Statistic.TOTAL_WORLD_TIME);

        scoreboardText.add("§0");
        scoreboardText.add("§bToken");
        scoreboardText.add("   §e" + TokenManager.getPlayerTokens(player));

        scoreboardText.add("§1");
        scoreboardText.add("§bStats");
        scoreboardText.add("   §4\uD83D\uDDE1§f " + kills);
        scoreboardText.add("   §e☠§f " + deaths);
        scoreboardText.add("   §4K§f/§eD§f " + String.format("%.2f", (double) kills / deaths));
        scoreboardText.add("   §6Streak " + TokenManager.getKillStreak(player));

        scoreboardText.add("§2");
        scoreboardText.add("§bMap Switch");
        scoreboardText.add("   §b⌚§f " + convertTicksToTime(Maps.getTimeToNextSwitch() * 2));
        scoreboardText.add("   §b⚐§f " + Maps.getMap().getName());
        scoreboardText.add("     §7by §f" + Maps.getMap().getBuilder());

        scoreboardText.add("§4");

        for (String line : scoreboardText) {
            playerObjective.getScore(" " + line).setScore(scoreboardText.size() - scoreboardText.indexOf(line));
        }

        player.setScoreboard(playerScoreboard);
    }

    public static void createScoreboard() {
        if (scoreboard != null) return;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        objective = scoreboard.registerNewObjective("playerSidebar", "dummy", "  §b§lBobTonyFFA  ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public static String convertTicksToTime(long ticks) {
        long ticksPerSecond = 20;
        long ticksPerMinute = ticksPerSecond * 60;
        long ticksPerHour = ticksPerMinute * 60;
        long ticksPerDay = ticksPerHour * 24;

        long days = ticks / ticksPerDay;
        ticks %= ticksPerDay;

        long hours = ticks / ticksPerHour;
        ticks %= ticksPerHour;

        long minutes = ticks / ticksPerMinute;
        ticks %= ticksPerMinute;

        long seconds = ticks / ticksPerSecond;

        StringBuilder timeString = new StringBuilder();
        if (days > 0) {
            timeString.append(days).append("d ");
        }
        if (hours > 0) {
            timeString.append(hours).append("h ");
        }
        if (minutes > 0 && days == 0) {
            timeString.append(minutes).append("min ");
        }
        if (seconds > 0 && hours == 0 && days == 0) {
            timeString.append(seconds).append("s ");
        }

        String result = timeString.toString().trim();
        return result.isEmpty() ? "0s" : result;
    }

}
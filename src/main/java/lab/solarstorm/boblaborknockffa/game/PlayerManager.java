package lab.solarstorm.boblaborknockffa.game;

import lab.solarstorm.boblaborknockffa.map.Maps;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.plugin;
import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.team;

public final class PlayerManager implements Listener {

    private static HashMap<Location, Integer> playerBlocksHash = new HashMap<>();
    private static int blockSec = 3;
    @Getter
    private static int timer = resetTimer();
    private static final HashMap<UUID, Player> lastDamager = new HashMap<>();
    private static final Random random = new Random();

    public PlayerManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                handelPlayerBlocks();
                handelTimer();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private static void handelTimer() {
        timer = timer - 1;
        if(timer == 0){
            Maps.switchMap();
            resetTimer();
        }
    }

    private static int resetTimer(){return timer = Maps.getMapSwitchTime() * 60 * 20;}

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
        if (!player.getGameMode().equals(GameMode.CREATIVE))
            playerBlocksHash.put(event.getBlock().getLocation(), blockSec * 10);
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Maps.teleportPlayer(player);
        team.addEntry(player.getName());
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
            "§b%s §7bekam von §b%s §7eine kostenlose Void-Cruise geschenkt"
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
            "§b%s §7wurde von §b%s §7zum Walking Loot-Container degradiert",
            "§b%s §7glaubte, §b%s §7sei ein Tutorial-Bot"
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
            "§b%s §7suchte nach Herobrine. Fand stattdessen das Void"
    };

    @EventHandler
    public static void onPlayerDamage(EntityDamageEvent event) {
        if(event.getDamageSource().getDamageType().equals(DamageType.FALL)){
            event.setCancelled(true); return;
        }
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

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
    }

    private static void handleDirectKill(Player victim, EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player killer = (Player) event.getDamager();
            killer.setStatistic(Statistic.PLAYER_KILLS, killer.getStatistic(Statistic.PLAYER_KILLS) + 1);
            victim.setStatistic(Statistic.DEATHS, victim.getStatistic(Statistic.DEATHS) + 1);
            broadcastRandomMessage(DIRECT_KILL_MESSAGES, victim.getName(), killer.getName());
        }
    }

    private static void handleVoidSuicide(Player player) {
        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1);
        broadcastRandomMessage(VOID_SUICIDE_MESSAGES, player.getName());
    }

    private static void broadcastRandomMessage(String[] messages, String... args) {
        String rawMessage = messages[random.nextInt(messages.length)];
        Bukkit.broadcastMessage(String.format(rawMessage, args));
    }

}
package lab.solarstorm.boblaborknockffa.game;

import lab.solarstorm.boblaborknockffa.map.Maps;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.plugin;

public class PlayerEvent implements Listener {

    private static HashMap<Location, Integer> playerBlocksHash = new HashMap<>();
    private int blockSec = 3;
    private final HashMap<UUID, Player> lastDamager = new HashMap<>();
    private final Random random = new Random();

    public PlayerEvent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                handelPlayerBlocks();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void handelPlayerBlocks() {
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
    public void onPlayerBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.CREATIVE))
            playerBlocksHash.put(event.getBlock().getLocation(), blockSec * 10);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Maps.teleportPlayer(player);
    }

    // Listen mit zufälligen Nachrichten für jeden Todestyp
    private static final String[] VOID_PUSHED_MESSAGES = {
            "§6%s §cwurde von §6%s §cins Void befördert. Hat wohl vergessen, dass unten KEIN TRAMPOLIN IST!",
            "§6%s §cdachte, §6%s §cwäre ein Taxi ins Nichts. Falsch gedacht!",
            "§6%s §cwurde von §6%s §czum Weltraumprogramm gezwungen. Ohne Rakete!",
            "§6%s §cfliegt jetzt dank §6%s §cin die Unendlichkeit... und noch viel weiter!",
            "§6%s §cvertraut §6%s §cblind. Jetzt hängt er/sie im Digitalen Nichts fest!",
            "§6%s §cwurde von §6%s §czum Abgrund-Yoga gezwungen. Downward Dog → Game Over!",
            "§6%s §cversucht §6%s§cs Push als 'Soziale Interaktion' zu melden. FAIL!",
            "§6%s §cwollte fliegen lernen. Dank §6%s §cjetzt mit 100% mehr VOID!",
            "§6%s §cwurde von §6%s §centführt. Plot Twist: Das Void ist KEIN Hot Tub!",
            "§6%s §cging mit §6%s §c'Abgrund-Sightseeing'. Letzter Stopp: Respawn!",
            "§6%s §cließ sich von §6%s §cüberzeugen: 'Das Void ist nur psychosomatisch!'",
            "§6%s §cvertraute §6%s§cs 'Trustfall'-Challenge. Spoiler: Kein Auffangen!",
            "§6%s §cdachte, §6%s §csei ein Reiseführer. Jetzt: One-Way-Ticket ins Nichts!",
            "§6%s §cwurde von §6%s §cals menschlicher Müll ins Void entsorgt!",
            "§6%s §cging mit §6%s §cauf Tauchstation. Tiefster Tauchgang aller Zeiten!"
    };

    private static final String[] DIRECT_KILL_MESSAGES = {
            "§6%s §cwurde von §6%s §czum Loot-Drop degradiert. Gratis Items!",
            "§6%s §cversuchte §6%s §canzugurken. Ergebnis: LETZTER Gurkenlord!",
            "§6%s §cwollte mit §6%s §cranken. Jetzt ist er/sie der Bodenbelag!",
            "§6%s §cdachte, §6%s §csei ein Sparringspartner. Spoiler: Nein. Einfach nein.",
            "§6%s §cwurde von §6%s §cgekillt. Todesursache: Existieren ohne Erlaubnis!",
            "§6%s §cspielte 'Wer hat Angst vorm bösen §6%s§c?'. Antwort: ZU RECHT!",
            "§6%s §cwurde von §6%s §cals 'Schwierigkeitsstufe: Leicht' markiert!",
            "§6%s §cversuchte §6%s §czu mobben. Backfired harder than a creeper!",
            "§6%s §cwurde von §6%s §causgelöscht. Wie ein Tab, den niemand vermisst!",
            "§6%s §cging zu nah an §6%s §cran. Jetzt: Human-Kebab Edition!",
            "§6%s §cließ §6%s §cnicht ausreden. Jetzt ist er/sie stumm... für IMMER!",
            "§6%s §cwurde von §6%s §czur Statistik hinzugefügt. Yay für KPIs!",
            "§6%s §cprovoziert §6%s §cund gewinnt... den Darwin-Award!",
            "§6%s §cwurde von §6%s §czum Pro-Gamer-Move. Spoiler: War keiner!",
            "§6%s §clag vor §6%s§cs Füßen. Wie ein Teppich aus Schmerz und Scham!"
    };

    private static final String[] VOID_SUICIDE_MESSAGES = {
            "§6%s §cwollte testen, ob das Void barfuß-friendly ist. Spoiler: NEIN!",
            "§6%s §cdachte, die Schwerkraft sei nur ein Vorschlag. Falsch gedacht!",
            "§6%s §cging auf Void-Date. Jetzt ist er/sie ghosted für immer!",
            "§6%s §cvertraute einem TikTok-Lifehack: 'Fliegen ohne Elytren!'",
            "§6%s §cwollte als Ghostbuster ins Void. Jetzt ist er/sie der Ghost!",
            "§6%s §cversuchte den 'Leap of Faith'. Glaube allein reicht NICHT!",
            "§6%s §cging Void-Swimming. Letzte Worte: 'Wo ist das Wasser?!'",
            "§6%s §cdachte, der Abgrund wäre nur eine soziale Konstruktion!",
            "§6%s §cprobierte das neue 'Void-Detox'. Ergebnis: Zu erfolgreich!",
            "§6%s §cwollte als Influencer ins Void. Jetzt: 0 Followers, 0 Life!",
            "§6%s §cging 'zufällig' ins Void. Beste Entscheidung seit Windows Vista!",
            "§6%s §cspielte Floor-is-Lava... mit dem Void als Floor!",
            "§6%s §cvertauschte 'Abgrund' mit 'Kuscheldecke'. Tragisches Ende!",
            "§6%s §cdachte, das Void wäre ein Backrooms-Level. Nope, einfach tot!",
            "§6%s §cwollte Gravity Checks machen. Ergebnis: F in Physik!"
    };

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.getDamageSource().getDamageType().equals(DamageType.FALL)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        // Tracke letzten Schläger
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

    private boolean isFatalDamage(Player player, EntityDamageEvent event) {
        return event.getCause() == EntityDamageEvent.DamageCause.VOID
                || player.getHealth() - event.getFinalDamage() <= 1.0;
    }

    private void handleRespawn(Player player) {
        player.teleport(Maps.getLocation());
        player.setHealth(20);
    }

    private void updateStatsAndBroadcast(Player player, EntityDamageEvent event) {
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

    private void handleVoidPushedDeath(Player victim, Player killer) {
        killer.setStatistic(Statistic.PLAYER_KILLS, killer.getStatistic(Statistic.PLAYER_KILLS) + 1);
        victim.setStatistic(Statistic.DEATHS, victim.getStatistic(Statistic.DEATHS) + 1);
        broadcastRandomMessage(VOID_PUSHED_MESSAGES, victim.getName(), killer.getName());
        lastDamager.remove(victim.getUniqueId());
    }

    private void handleDirectKill(Player victim, EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player killer = (Player) event.getDamager();
            killer.setStatistic(Statistic.PLAYER_KILLS, killer.getStatistic(Statistic.PLAYER_KILLS) + 1);
            victim.setStatistic(Statistic.DEATHS, victim.getStatistic(Statistic.DEATHS) + 1);
            broadcastRandomMessage(DIRECT_KILL_MESSAGES, victim.getName(), killer.getName());
        }
    }

    private void handleVoidSuicide(Player player) {
        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1);
        broadcastRandomMessage(VOID_SUICIDE_MESSAGES, player.getName());
    }

    private void broadcastRandomMessage(String[] messages, String... args) {
        String rawMessage = messages[random.nextInt(messages.length)];
        Bukkit.broadcastMessage(String.format(rawMessage, args));
    }

}
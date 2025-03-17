package lab.solarstorm.boblaborknockffa.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.plugin;

public class PlayerEvent implements Listener {
    private static HashMap<Location, Integer> playerBlocksHash = new HashMap<>();
    private int blockSec = 3;

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
        AttributeInstance attackSpeed = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attackSpeed != null) {
            attackSpeed.setBaseValue(100);
        }
    }
}
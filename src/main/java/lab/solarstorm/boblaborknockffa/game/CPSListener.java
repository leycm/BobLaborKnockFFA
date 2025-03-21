package lab.solarstorm.boblaborknockffa.game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CPSListener implements Listener {

    private static final double MAX_CPS = 18.0;
    private static final double LERP_FACTOR = 0.3;

    private static final Map<UUID, Queue<Long>> clickTimes = new ConcurrentHashMap<>();
    private static final Map<UUID, Double> animatedCPS = new ConcurrentHashMap<>();

    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            clickTimes.computeIfAbsent(uuid, k -> new LinkedList<>()).add(System.currentTimeMillis());
        }
    }

    public static void updateCPS() {
        long now = System.currentTimeMillis();

        clickTimes.forEach((uuid, times) -> {
            synchronized (times) {
                while (!times.isEmpty() && now - times.peek() > 1000) {
                    times.poll();
                }

                Player player = Bukkit.getPlayer(uuid);
                if (player == null) return;

                double realCPS = times.size();
                double currentAnimated = animatedCPS.getOrDefault(uuid, realCPS);
                double smoothedCPS = lerp(currentAnimated, realCPS, LERP_FACTOR);
                animatedCPS.put(uuid, smoothedCPS);

                int level = (int) smoothedCPS;
                float progress = (float) (smoothedCPS / MAX_CPS);
                progress = Math.min(progress, 1.0f);
                progress = Math.round(progress * 10) / 10.0f;

                player.setLevel(level);
                player.setExp(progress);
            }
        });
    }

    public static double getCps(UUID uuid) {
        Queue<Long> times = clickTimes.get(uuid);
        return times != null ? times.size() : 0.0;
    }

    public static double getSmoothCps(UUID uuid) {
        return animatedCPS.getOrDefault(uuid, 0.0);
    }

    private static double lerp(double start, double end, double factor) {
        return start + (end - start) * factor;
    }
}
package lab.solarstorm.boblaborknockffa.map;

import lab.solarstorm.boblaborknockffa.game.GameManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.PREFIX;

public final class Maps implements CommandExecutor {
    private static final HashMap<UUID, Boolean> voteMap = new HashMap<>();
    @Getter
    private static KnockMap map = switchMap();
    private static int min = 15;
    private static boolean voteInProgress = false;

    public void switchMap(KnockMap newmap) {
        map = newmap;
        switchAllPlayer();
    }

    public static KnockMap switchMap() {
        voteMap.clear();
        voteInProgress = false;
        KnockMap[] availableMaps = Arrays.stream(KnockMap.values())
                .filter(knockMap -> knockMap != map)
                .toArray(KnockMap[]::new);
        map = availableMaps.length > 0 ? availableMaps[new Random().nextInt(availableMaps.length)] : map;
        switchAllPlayer();
        return map;
    }

    public static Location getLocation() {
        return (map.getLocation());
    }

    public static void teleportPlayer(Player player) {
        player.teleport(map.getLocation());
    }

    private static void switchAllPlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(map.getLocation());
        }
    }

    public static int getMapSwitchTime() { return min; }

    public static int getTimeToNextSwitch() { return GameManager.getTimer(); }

    public static int getTimeToNextSwitch(TimeUnit typ) {
        switch (typ) {
            case SECONDS:
                GameManager.getTimer();
                return GameManager.getTimer() / (20);
            case MINUTES:
                return GameManager.getTimer() / (20 * 60);
            case HOURS:
                return GameManager.getTimer() / (20 * 60 * 60);
            default:
                return GameManager.getTimer();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        if (player.hasPermission("knock.ffa.admin") && player.getGameMode().equals(GameMode.CREATIVE)) {
            Bukkit.broadcastMessage(PREFIX + "An admin has forced a map switch!");
            switchMap();
            return true;
        }

        if (voteInProgress) {
            if (voteMap.containsKey(player.getUniqueId())) {
                player.sendMessage(PREFIX + "You have already voted!");
            } else {
                voteMap.put(player.getUniqueId(), true);
                player.sendMessage(PREFIX + "You have voted to skip the current map!");
                checkVoteStatus();
            }
            return true;
        }

        if (onlinePlayers < 1) {
            player.sendMessage(PREFIX + "Not enough players to start a vote!");
            return true;
        }

        voteInProgress = true;
        voteMap.put(player.getUniqueId(), true);
        Bukkit.broadcastMessage(PREFIX + player.getName() + " has started a vote to skip the current map!");
        Bukkit.broadcastMessage(PREFIX + "Type /skip to vote!");

        if (onlinePlayers < 2) {
            Bukkit.broadcastMessage(PREFIX + "Map will be skipped automatically due to low player count!");
            switchMap();
        } else {
            checkVoteStatus();
        }

        return true;
    }

    private static void checkVoteStatus() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int votesNeeded;

        if (onlinePlayers < 4) {
            votesNeeded = onlinePlayers;
        } else if (onlinePlayers < 10) {
            votesNeeded = (int) Math.ceil(onlinePlayers * 2.0 / 3.0);
        } else {
            votesNeeded = (int) Math.ceil(onlinePlayers / 2.0);
        }

        Bukkit.broadcastMessage(PREFIX + "Votes: " + voteMap.size() + "/" + votesNeeded + " needed to skip!");

        if (voteMap.size() >= votesNeeded) {
            Bukkit.broadcastMessage(PREFIX + "Vote passed! Switching map...");
            switchMap();
        }
    }
}
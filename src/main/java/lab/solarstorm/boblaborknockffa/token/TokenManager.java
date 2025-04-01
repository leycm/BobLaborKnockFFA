package lab.solarstorm.boblaborknockffa.token;

import lab.solarstorm.boblaborknockffa.game.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public final class TokenManager implements CommandExecutor, Listener {
    public static HashMap<UUID, Integer> killStreakMap = new HashMap<>();

    public static int getPlayerTokens(Player player) {
        int timeCrafted = player.getStatistic(Statistic.CRAFT_ITEM, org.bukkit.Material.SUNFLOWER);
        int timeUsed = player.getStatistic(Statistic.USE_ITEM, org.bukkit.Material.SUNFLOWER);
        return timeCrafted - timeUsed;
    }

    public static void removePlayerTokens(Player player, int amount) {
        if (amount <= 0) return;

        int currentTokens = getPlayerTokens(player);
        if (currentTokens < amount) {return;}

        int timeUsed = player.getStatistic(Statistic.USE_ITEM, org.bukkit.Material.SUNFLOWER);
        player.setStatistic(Statistic.USE_ITEM, org.bukkit.Material.SUNFLOWER, timeUsed + amount);
    }

    public static int getKillStreak(Player player){
        return killStreakMap.getOrDefault(player.getUniqueId(), 0);
    }

    public static void addPlayerTokens(Player player, int amount) {
        if (amount <= 0) return;
        int timeCrafted = player.getStatistic(Statistic.CRAFT_ITEM, org.bukkit.Material.SUNFLOWER);
        player.setStatistic(Statistic.CRAFT_ITEM, org.bukkit.Material.SUNFLOWER, timeCrafted + amount);
    }
    public static void addPlayerKill(Player player) {
        int killStreak = killStreakMap.getOrDefault(player.getUniqueId(), 0);
        int amount = 1;
        if(killStreak / 5 != 0) {
            amount = amount * (killStreak / 5 + 1);
        }
        player.sendActionBar("§e+" + amount + "§6 Token     §f" + (killStreak + 1) + "§7 KillStreak    §a" + killStreak / 5 + "§2 Bonus");
        addPlayerTokens(player, amount);
        addStreak(player);
    }

    public static int getPlayerEarnedTokens(Player player) {
        return player.getStatistic(Statistic.CRAFT_ITEM, org.bukkit.Material.SUNFLOWER);
    }

    public static void breakStreak(Player player) {
        killStreakMap.remove(player.getUniqueId());
    }

    public static void addStreak(Player player) {
        addStreak(player, 1);
    }

    public static void addStreak(Player player, int i) {
        Integer killStreak = killStreakMap.getOrDefault(player.getUniqueId() , 0);
        if(killStreak != null){
            killStreakMap.put(player.getUniqueId(), killStreak + 1);
            handelStreakReward(player, killStreak + 1);
        }
    }

    public static void handelStreakReward(Player player, int streak){
        if (streak == 2) {
            if (!player.getInventory().contains(Material.COBWEB))
                player.getInventory().addItem(ItemManager.createCobweb());
        }

        if (streak % 5 == 0) {
            if (!player.getInventory().contains(Material.ENDER_PEARL))
                player.getInventory().addItem(ItemManager.createEnderPearl());
            player.getInventory().addItem(ItemManager.createBuildingBlock(ItemManager.getPlayerSkinData(player.getUniqueId()).getEquippedBlockSkin()));
            KopfGeldManager.addKopfgeldDirect(player,(streak / 5 - 1) * 10);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return false;
        Player player = ((Player) sender).getPlayer();
        Player target;
        int token;
        if (args.length == 1) {
            target = player;
            token = Integer.parseInt(args[0]);
        }
        else if (args.length == 2) {
            target = Bukkit.getPlayer(args[0]);
            token = Integer.parseInt(args[1]);
        }
        else {
            return false;
        }
        addPlayerTokens(target, token);
        return false;
    }

}
package lab.solarstorm.boblaborknockffa.token;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.PREFIX;

public final class KopfGeldManager implements CommandExecutor, TabCompleter {
    private static final Map<UUID, Integer> kopfgeldMap = new HashMap<>();
    private static final int MIN_TOKENS = 10;

    public static void addKopfgeldDirect(Player target, int amount) {
        if (target == null || amount <= 0) return;
        kopfgeldMap.merge(target.getUniqueId(), amount, Integer::sum);
        Bukkit.broadcastMessage(PREFIX + "§e" + target.getName() + " §7hat nun §e" + getKopfgeld(target) + " Tokens §7Kopfgeld!");
    }

    public static void setKopfgeld(Player setter, Player target, int amount) {
        if (amount < MIN_TOKENS) {
            setter.sendMessage(PREFIX + "§cMindestbetrag: §e" + MIN_TOKENS + " Tokens");
            return;
        }

        int cost = amount * 2;
        if (TokenManager.getPlayerTokens(setter) < cost) {
            setter.sendMessage(PREFIX + "§cDu benötigst §e" + cost + " Tokens§c!");
            return;
        }

        TokenManager.removePlayerTokens(setter, cost);
        kopfgeldMap.merge(target.getUniqueId(), amount, Integer::sum);
        setter.sendMessage(PREFIX + "§aDu hast §e5 Tokens §aauf §e" + target.getName() + " §agesetzt! (§e" + cost + " Tokens §aabgezogen)");
        Bukkit.broadcastMessage(PREFIX + "§e" + target.getName() + " §7hat nun §e" + getKopfgeld(target) + " Tokens §7Kopfgeld!");
    }

    public static int getKopfgeld(Player target) {
        return kopfgeldMap.getOrDefault(target.getUniqueId(), 0);
    }

    public static void claimKopfgeld(Player killer, Player killed) {
        Integer amount = kopfgeldMap.remove(killed.getUniqueId());
        if (amount == null) return;

        TokenManager.addPlayerTokens(killer, amount);
        killer.sendMessage(PREFIX + "§aDu hast §e" + amount + " Tokens §aerhalten!");
        Bukkit.broadcastMessage(PREFIX + "§e" + killer.getName() + " §7hat §e" + amount + " Tokens §7Kopfgeld erhalten!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set":
                if (args.length < 3) return false;
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || target.equals(player)) return false;
                try {
                    setKopfgeld(player, target, Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    player.sendMessage(PREFIX + "§cUngültige Zahl!");
                }
                break;

            case "check":
                Player checkTarget = args.length > 1 ? Bukkit.getPlayer(args[1]) : player;
                if (checkTarget == null) return false;
                player.sendMessage(PREFIX + "§e" + checkTarget.getName() + " §7hat §e" + getKopfgeld(checkTarget) + " Tokens");
                break;

            case "list":
                if (kopfgeldMap.isEmpty()) {
                    player.sendMessage(PREFIX + "§7Keine aktiven Kopfgeld");
                    return true;
                }
                player.sendMessage(PREFIX + "§6Aktive Kopfgeld:");
                kopfgeldMap.forEach((uuid, amt) ->
                        player.sendMessage("§7- §e" + Bukkit.getOfflinePlayer(uuid).getName() + " §7(§e" + amt + "§7)"));
                break;

            default:
                return false;
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(PREFIX + "§6Befehle:");
        p.sendMessage("§e/kopfgeld set <Spieler> <Betrag> §7(Mind. " + MIN_TOKENS + " Tokens)");
        p.sendMessage("§e/kopfgeld check [Spieler]");
        p.sendMessage("§e/kopfgeld list");
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("set", "check", "list");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("check"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
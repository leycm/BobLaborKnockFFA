package lab.solarstorm.boblaborknockffa.minigame;

import lab.solarstorm.boblaborknockffa.token.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.PREFIX;

public class CoinFlip implements CommandExecutor, TabCompleter {
    private static HashMap<UUID, UUID> requests = new HashMap<>();
    private static HashMap<UUID, Integer> tokenPrice = new HashMap<>();
    private static HashMap<UUID, Integer> timeMap = new HashMap<>();
    private static Random random = new Random();
    private static final int BROADCAST_THRESHOLD = 500;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + "§cNur Spieler können diesen Befehl nutzen!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(PREFIX + "§cVerwendung: /cf <spieler> <token|accept|deny>");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "acc":
            case "accept":
                handleAccept(player, args);
                break;
            case "block":
            case "deny":
                handleDeny(player, args);
                break;
            default:
                handleRequest(player, args);
        }
        return true;
    }

    private void handleAccept(Player player, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(PREFIX + "§cDer Spieler muss online sein");
            return;
        }

        if (!requests.containsKey(target.getUniqueId()) || !requests.get(target.getUniqueId()).equals(player.getUniqueId())) {
            player.sendMessage(PREFIX + "§cDu hast keine CoinFlip-Anfrage von " + target.getName() + " erhalten!");
            return;
        }

        int tokens = tokenPrice.get(target.getUniqueId());

        if (TokenManager.getPlayerTokens(player) < tokens) {
            requests.remove(target.getUniqueId());
            tokenPrice.remove(target.getUniqueId());
            timeMap.remove(target.getUniqueId());
            target.sendMessage(PREFIX + "§c" + player.getName() + " hat nicht genug Token für den CoinFlip!");
            player.sendMessage(PREFIX + "§cDu hast nicht genug Token!");
            return;
        }

        if (TokenManager.getPlayerTokens(target) < tokens) {
            requests.remove(target.getUniqueId());
            tokenPrice.remove(target.getUniqueId());
            timeMap.remove(target.getUniqueId());
            player.sendMessage(PREFIX + "§c" + target.getName() + " hat nicht genug Token!");
            target.sendMessage(PREFIX + "§cDu hast nicht genug Token für den CoinFlip!");
            return;
        }

        TokenManager.removePlayerTokens(player, tokens);
        TokenManager.removePlayerTokens(target, tokens);

        boolean playerWins = random.nextBoolean();
        Player winner = playerWins ? player : target;
        Player loser = playerWins ? target : player;

        TokenManager.addPlayerTokens(winner, tokens * 2);

        if (tokens >= BROADCAST_THRESHOLD) {
            Bukkit.broadcastMessage(PREFIX + "§6§lHIGHSTAKES COINFLIP!");
            Bukkit.broadcastMessage(PREFIX + "§a" + player.getName() + " §7vs §a" + target.getName());
            Bukkit.broadcastMessage(PREFIX + "§eEinsatz: §6" + tokens + " Token §7(§6" + (tokens*2) + " Token§7 gesamt)");
            Bukkit.broadcastMessage(PREFIX + "§6§lGEWINNER: §a" + winner.getName() + " §7(+" + tokens + " Token)");
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(PREFIX + "§7[§eCoinFlip§7] §f" + player.getName() + " §7hat gegen §f" + target.getName() + " §7um §e" + tokens + " Token §7gecoinflippt!");
                p.sendMessage(PREFIX + "§7Gewinner: §a" + winner.getName() + "§7! Nutze §e/coinflip <Spieler> <Token>§7!");
            });
        }

        requests.remove(target.getUniqueId());
        tokenPrice.remove(target.getUniqueId());
        timeMap.remove(target.getUniqueId());
    }

    private void handleDeny(Player player, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(PREFIX + "§cDer Spieler muss online sein");
            return;
        }

        if (!requests.containsKey(target.getUniqueId())) {
            player.sendMessage(PREFIX + "§cDu hast keine CoinFlip-Anfrage von " + target.getName() + " erhalten!");
            return;
        }

        requests.remove(target.getUniqueId());
        tokenPrice.remove(target.getUniqueId());
        timeMap.remove(target.getUniqueId());

        player.sendMessage(PREFIX + "§aDu hast die CoinFlip-Anfrage von " + target.getName() + " abgelehnt.");
        target.sendMessage(PREFIX + "§c" + player.getName() + " hat deine CoinFlip-Anfrage abgelehnt.");
    }

    private void handleRequest(Player player, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(PREFIX + "§cDer Spieler muss online sein");
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(PREFIX + "§cDu kannst nicht gegen dich selbst coinflippen!");
            return;
        }

        int tokens;
        try {
            tokens = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + "§cDie Token-Anzahl muss eine Zahl sein");
            return;
        }

        if (tokens <= 0) {
            player.sendMessage(PREFIX + "§cDie Token-Anzahl muss größer als 0 sein");
            return;
        }

        if (requests.containsKey(player.getUniqueId())) {
            player.sendMessage(PREFIX + "§cDu hast bereits eine CoinFlip-Anfrage gesendet!");
            return;
        }

        if (TokenManager.getPlayerTokens(player) < tokens) {
            player.sendMessage(PREFIX + "§cDu hast nicht genug Token!");
            return;
        }

        requests.put(player.getUniqueId(), target.getUniqueId());
        tokenPrice.put(player.getUniqueId(), tokens);
        timeMap.put(player.getUniqueId(), 2 * 60 * 10);

        player.sendMessage(PREFIX + "§aCoinFlip-Anfrage an " + target.getName() + " für " + tokens + " Token gesendet!");
        target.sendMessage(PREFIX + "§e" + player.getName() + " möchte mit dir um " + tokens + " Token coinflippen!");
        target.sendMessage(PREFIX + "§eNutze §a/cf " + player.getName() + " accept §eoder §c/cf " + player.getName() + " deny");

        if (tokens >= BROADCAST_THRESHOLD) {
            Bukkit.broadcastMessage(PREFIX + "§6§lHIGHSTAKES COINFLIP ANGEBOT!");
            Bukkit.broadcastMessage(PREFIX + "§a" + player.getName() + " §7bietet einen CoinFlip um §6" + tokens + " Token§7 an!");
        }
    }

    public static void handleRequestTimeouts() {
        timeMap.entrySet().removeIf(entry -> {
            int timeLeft = entry.getValue() - 1;
            if (timeLeft <= 0) {
                UUID sender = entry.getKey();
                UUID target = requests.get(sender);

                Player senderPlayer = Bukkit.getPlayer(sender);
                Player targetPlayer = Bukkit.getPlayer(target);

                if (senderPlayer != null) senderPlayer.sendMessage(PREFIX + "§cDeine CoinFlip-Anfrage ist abgelaufen!");
                if (targetPlayer != null) targetPlayer.sendMessage(PREFIX + "§cDie CoinFlip-Anfrage von " + senderPlayer.getName() + " ist abgelaufen!");

                requests.remove(sender);
                tokenPrice.remove(sender);
                return true;
            }
            entry.setValue(timeLeft);
            return false;
        });
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!p.getName().equalsIgnoreCase(sender.getName())) {
                    completions.add(p.getName());
                }
            });
        } else if (args.length == 2) {
            completions.add("100");
            completions.add("500");
            completions.add("1000");
            completions.add("5000");
        }
        return completions;
    }
}
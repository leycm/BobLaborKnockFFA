package lab.solarstorm.boblaborknockffa.map;

import lab.solarstorm.boblaborknockffa.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class Maps {
    private static KnockMap map = switchMap();
    private static int min = 15;

    public void switchMap(KnockMap newmap){
        map = newmap;
        switchAllPlayer();
    }

    public static KnockMap switchMap() {
        KnockMap[] availableMaps = Arrays.stream(KnockMap.values())
                .filter(knockMap -> knockMap != map)
                .toArray(KnockMap[]::new);
        map = availableMaps.length > 0 ? availableMaps[new Random().nextInt(availableMaps.length)] : map;
        switchAllPlayer();
        return map;
    }

    public static Location getLocation(){
        return (map.getLocation());
    }

    public static void teleportPlayer(Player player){
        player.teleport(map.getLocation());
    }

    private static void switchAllPlayer(){
        for(Player player : Bukkit.getOnlinePlayers()){
            player.teleport(map.getLocation());
        }
    }

    public static int getMapSwitchTime(){return min;}

    public static int getTimeToNextSwitch(){return GameManager.getTimer();}
    public static int getTimeToNextSwitch(TimeUnit typ){
        switch (typ){
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
}

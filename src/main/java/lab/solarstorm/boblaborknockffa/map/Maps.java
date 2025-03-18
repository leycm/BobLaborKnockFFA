package lab.solarstorm.boblaborknockffa.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Random;

public final class Maps {
    private static KnockMap map = KnockMap.WHITE_BOXES;

    public void switchMap(KnockMap newmap){
        map = newmap;
    }

    public static KnockMap switchMap() {
        KnockMap[] availableMaps = Arrays.stream(KnockMap.values())
                .filter(knockMap -> knockMap != map)
                .toArray(KnockMap[]::new);
        map = availableMaps.length > 0 ? availableMaps[new Random().nextInt(availableMaps.length)] : map;
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
}

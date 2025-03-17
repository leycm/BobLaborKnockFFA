package lab.solarstorm.boblaborknockffa.map;

import lab.solarstorm.boblaborknockffa.PlayerWarper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Random;

public final class Map {
    private KnockMap map;

    public void switchMap(KnockMap map){
        this.map = map;
    }

    public KnockMap switchMap() {
        KnockMap[] availableMaps = Arrays.stream(KnockMap.values())
                .filter(knockMap -> knockMap != this.map)
                .toArray(KnockMap[]::new);
        this.map = availableMaps.length > 0 ? availableMaps[new Random().nextInt(availableMaps.length)] : this.map;
        return this.map;
    }

    private void switchAllPlayer(){
        for(Player player : Bukkit.getOnlinePlayers()){
            player.teleport(map.getLocation());
        }
    }

    public Location getLocation(){
        return (this.map.getLocation());
    }
}

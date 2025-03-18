package lab.solarstorm.boblaborknockffa.map;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Vector;


@Getter
public enum KnockMap {
    WHITE_BOXES(new Vector<Integer>() {{add(-1000);add(-21);add(0);}}, "White Boxes"),
    HONEY(new Vector<Integer>() {{add(1000);add(-21);add(0);}}, "Honey"),
    STEAM_PUNK(new Vector<Integer>() {{add(0);add(-21);add(0);}}, "Steam Punk");

    private final Vector<Integer> loc;
    private final String name;

    KnockMap(Vector<Integer> loc, String name) {
        this.loc = loc;
        this.name = name;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld("world");
        return new Location(world, loc.get(0) + 0.5, loc.get(1), loc.get(2)+ 0.5);
    }

}

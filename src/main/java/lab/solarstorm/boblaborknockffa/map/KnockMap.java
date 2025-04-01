package lab.solarstorm.boblaborknockffa.map;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Vector;


@Getter
public enum KnockMap {
    WHITE_BOXES(new Vector<Integer>() {{add(-1000);add(-21);add(0);}}, "§fWhite Boxes", "LeyCM"),
    HONEY(new Vector<Integer>() {{add(1000);add(-21);add(0);}}, "§6Honey Combs", "Froggy"),
    STEAM_PUNK(new Vector<Integer>() {{add(0);add(-21);add(0);}}, "§6Steam Punk", "Froggy"),
    BOB_LABOR(new Vector<Integer>() {{add(0);add(-15);add(1000);}}, "§2BobLabor", "Froggy"),
    BAKERY(new Vector<Integer>() {{add(0);add(-21);add(-1000);}}, "§cBakery", "Froggy"),
    FISH(new Vector<Integer>() {{add(1000);add(-21);add(-1000);}}, "§bFish", "Froggy"),
    SOLAR_SYSTEM(new Vector<Integer>() {{add(-1000);add(-21);add(-1000);}}, "§eSolar §9System", "Froggy");

    private final Vector<Integer> loc;
    private final String name;
    private final String builder;

    KnockMap(Vector<Integer> loc, String name, String builder) {
        this.loc = loc;
        this.name = name;
        this.builder = builder;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld("world");
        return new Location(world, loc.get(0) + 0.5, loc.get(1), loc.get(2)+ 0.5);
    }
}

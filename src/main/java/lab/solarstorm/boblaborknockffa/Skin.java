package lab.solarstorm.boblaborknockffa;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum Skin {
    STICK("§3Stick", true, Material.STICK, 0),
    BLAZE_ROD("§3Stick", true, Material.BLAZE_ROD, 100),
    BONE("§3Stick", true, Material.BONE, 200),
    FEATHER("§3Stick", true, Material.FEATHER, 300);


    private boolean stick;
    private Material material;
    private String name;
    private int price;

    Skin(String name, boolean stick, Material material, int price){
        this.name = name;
        this.stick = stick;
        this.material = material;
        this.price = price;
    }

}

package lab.solarstorm.boblaborknockffa;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerWarper {
    private List<ItemStack> hotbar;
    private int kills;
    private int deaths;
    private int token;

    public PlayerWarper(){

    }

    public PlayerWarper(List<ItemStack> hotbar, int kills, int deaths, int token){
        this.hotbar = hotbar;
        this.kills = kills;
        this.deaths = deaths;
        this.token = token;
    }
}

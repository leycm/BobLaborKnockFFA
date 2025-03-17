package lab.solarstorm.boblaborknockffa.game;

import lab.solarstorm.boblaborknockffa.Skin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInventory implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.getInventory().clear();

        for (Skin skin : Skin.values()) {
            ItemStack item = new ItemStack(skin.getMaterial());
            ItemMeta meta = item.getItemMeta();
            item.setItemMeta(meta);


            player.getInventory().addItem(item);
        }

    }

}
package lab.solarstorm.boblaborknockffa.game;

import lab.solarstorm.boblaborknockffa.Skin;
import lab.solarstorm.boblaborknockffa.game.ItemManager;
import lab.solarstorm.boblaborknockffa.map.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if (title.equals("§6Main Shop")) {
            handleMainShop(event, player);
        }
        else if (title.startsWith(ItemManager.STICK_SHOP_NAME)) {
            handleStickShop(event, player, title);
        }
        else if (title.startsWith(ItemManager.BLOCK_SHOP_NAME)) {
            handleBlockShop(event, player, title);
        }
        else {
            event.setCancelled(false);
        }
    }

    private void handleMainShop(InventoryClickEvent event, Player player) {
        if (!isValidClick(event)) return;

        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (displayName.equals("§aStick Skins Shop")) {
            ItemManager.openStickShop(player);
        } else if (displayName.equals("§aBlock Skins Shop")) {
            ItemManager.openBlockShop(player);
        } else if (displayName.equals("§aInventory Layout Editor")) {
            ItemManager.openLayoutEditor(player);
        }
    }

    private void handleStickShop(InventoryClickEvent event, Player player, String title) {
        if (!isValidClick(event)) return;

        ItemStack clicked = event.getCurrentItem();
        String displayName = clicked.getItemMeta().getDisplayName();

        if (clicked.getType() == Material.ARROW) {
            int page = getCurrentPage(title);
            if (event.getSlot() == 48) page--;
            else if (event.getSlot() == 50) page++;
            ItemManager.openBlockShopPage(player, page);
        }

        else if (displayName.equalsIgnoreCase("§cBack to Main Shop")) {
            ItemManager.openMainShopMenu(player);
        }

        else {
            try {
                Skin skin = Skin.getByName(displayName);
                ItemManager.purchaseSkin(player, skin);
            } catch (Exception ignored) {}
        }
    }

    private void handleBlockShop(InventoryClickEvent event, Player player, String title) {
        if (!isValidClick(event)) return;

        ItemStack clicked = event.getCurrentItem();
        String displayName = clicked.getItemMeta().getDisplayName();

        if (clicked.getType() == Material.ARROW) {
            int page = getCurrentPage(title);
            if (event.getSlot() == 48) page--;
            else if (event.getSlot() == 50) page++;
            ItemManager.openBlockShopPage(player, page);
        }

        else if (displayName.equalsIgnoreCase("§cBack to Main Shop")) {
            ItemManager.openMainShopMenu(player);
        }

        else {
            try {
                Skin skin = Skin.getByName(displayName);
                ItemManager.purchaseSkin(player, skin);
            } catch (Exception ignored) {}
        }
    }

    private boolean isValidClick(InventoryClickEvent event) {
        return event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName();
    }

    private int getCurrentPage(String title) {
        try {
            String[] parts = title.split("§7");
            return Integer.parseInt(parts[1].replaceAll("[^0-9]", "")) - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null
                && item.hasItemMeta()
                && item.getItemMeta().getDisplayName().equals("§bItem Shop")
                && player.getLocation().getBlockY() > Maps.getLocation().getBlockY() - 5
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            event.setCancelled(true);
            ItemManager.openMainShopMenu(player);
        }
    }
}
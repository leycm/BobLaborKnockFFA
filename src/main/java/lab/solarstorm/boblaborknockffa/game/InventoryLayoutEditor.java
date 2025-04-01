package lab.solarstorm.boblaborknockffa.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.PREFIX;
import static lab.solarstorm.boblaborknockffa.BobLaborKnockFFA.logger;

public class InventoryLayoutEditor implements Listener {

    private static final String EDITOR_TITLE = "§aInventory Layout Editor";
    private static Map<UUID, Map<Integer, String>> tempLayouts = new HashMap<>();

    public static void openLayoutEditor(Player player) {
        UUID playerUuid = player.getUniqueId();
        ItemManager.PlayerSkinData skinData = ItemManager.getPlayerSkinData(playerUuid);

        Map<Integer, String> currentLayout = new HashMap<>(skinData.getInventoryLayout());
        tempLayouts.put(playerUuid, currentLayout);

        // Create simple 9-slot inventory that mimics the player's hotbar
        Inventory editorInventory = Bukkit.createInventory(null, 9, EDITOR_TITLE);

        // Add current items to inventory
        for (int i = 0; i < 9; i++) {
            String itemType = currentLayout.getOrDefault(i, "");
            ItemStack item = createItemForType(itemType, skinData);
            if (item != null) {
                editorInventory.setItem(i, item);
            }
        }

        player.openInventory(editorInventory);
    }

    private static ItemStack createItemForType(String itemType, ItemManager.PlayerSkinData skinData) {
        if (itemType == null || itemType.isEmpty()) {
            return null;
        }

        switch (itemType) {
            case "knockback_stick":
                return ItemManager.createKnockbackStick(skinData.getEquippedStickSkin());
            case "block":
                return ItemManager.createBuildingBlock(skinData.getEquippedBlockSkin());
            case "wooden_sword":
                return ItemManager.createWoodenSword();
            case "cobweb":
                return ItemManager.createCobweb();
            case "ender_pearl":
                return ItemManager.createEnderPearl();
            case "shop":
                return ItemManager.createShopItem();
            default:
                return null;
        }
    }

    private static String getItemTypeFromStack(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        String displayName = meta.getDisplayName();
        if (displayName.contains("Knockback Stick")) {
            return "knockback_stick";
        } else if (displayName.contains("Building Block")) {
            return "block";
        } else if (displayName.contains("Wooden Sword") || displayName.contains("Knockback Sword")) {
            return "wooden_sword";
        } else if (displayName.contains("Cobweb")) {
            return "cobweb";
        } else if (displayName.contains("Ender Pearl")) {
            return "ender_pearl";
        } else if (displayName.contains("Shop")) {
            return "shop";
        }

        logger.info("Item type not recognized: " + displayName);
        return null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (!title.equalsIgnoreCase(EDITOR_TITLE)) {
            return;
        }

        event.setCancelled(true);

        int clickedSlot = event.getRawSlot();
        if (clickedSlot < 0 || clickedSlot > 8) {
            return;
        }

        if (event.isLeftClick() || event.isRightClick()) {
            ItemStack cursorItem = event.getCursor();
            ItemStack currentItem = event.getCurrentItem();

            if ((cursorItem == null || cursorItem.getType() == Material.AIR) &&
                    (currentItem == null || currentItem.getType() == Material.AIR)) {
                return;
            }

            UUID playerUuid = player.getUniqueId();
            Map<Integer, String> layout = tempLayouts.get(playerUuid);

            if (event.isLeftClick() && currentItem != null &&
                    (cursorItem == null || cursorItem.getType() == Material.AIR)) {
                String itemType = getItemTypeFromStack(currentItem);
                if (itemType != null) {
                    layout.remove(clickedSlot);
                    event.getView().setCursor(currentItem.clone());
                    event.getInventory().setItem(clickedSlot, null);
                }
                return;
            }

            if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                String itemType = getItemTypeFromStack(cursorItem);
                if (itemType != null) {
                    layout.put(clickedSlot, itemType);
                    // Place the item
                    event.getInventory().setItem(clickedSlot, cursorItem.clone());
                    event.getView().setCursor(currentItem);
                }
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(EDITOR_TITLE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();

        if (!title.equalsIgnoreCase(EDITOR_TITLE)) {
            return;
        }

        UUID playerUuid = player.getUniqueId();
        if (!tempLayouts.containsKey(playerUuid)) {
            return;
        }

        Map<Integer, String> layout = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            ItemStack item = event.getInventory().getItem(i);
            if (item != null) {
                String itemType = getItemTypeFromStack(item);
                if (itemType != null) {
                    layout.put(i, itemType);
                }
            }
        }

        boolean hasStick = layout.containsValue("knockback_stick");
        boolean hasBlock = layout.containsValue("block");
        boolean hasShop = layout.containsValue("shop");
        boolean hasCobweb = layout.containsValue("cobweb");
        boolean hasEnderPearl = layout.containsValue("ender_pearl");

        if (!hasEnderPearl) {
            for (int i = 0; i < 9; i++) {
                if (!layout.containsKey(i)) {
                    layout.put(i, "ender_pearl");
                    break;
                }
            }
        }

        if (!hasStick) {
            for (int i = 0; i < 9; i++) {
                if (!layout.containsKey(i)) {
                    layout.put(i, "knockback_stick");
                    break;
                }
            }
        }

        if (!hasBlock) {
            for (int i = 0; i < 9; i++) {
                if (!layout.containsKey(i)) {
                    layout.put(i, "block");
                    break;
                }
            }
        }

        if (!hasShop) {
            for (int i = 0; i < 9; i++) {
                if (!layout.containsKey(i)) {
                    layout.put(i, "shop");
                    break;
                }
            }
        }

        if (!hasCobweb) {
            for (int i = 0; i < 9; i++) {
                if (!layout.containsKey(i)) {
                    layout.put(i, "cobweb");
                    break;
                }
            }
        }

        ItemManager.PlayerSkinData skinData = ItemManager.getPlayerSkinData(playerUuid);
        skinData.setInventoryLayout(layout);

        tempLayouts.remove(playerUuid);

        ItemManager.saveData();
        ItemManager.reloadPlayerInv(player);

        player.sendMessage(PREFIX + "§aYour inventory layout has been updated!");
    }
}
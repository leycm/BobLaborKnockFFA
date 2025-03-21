package lab.solarstorm.boblaborknockffa.game;

import lab.solarstorm.boblaborknockffa.Skin;
import lab.solarstorm.boblaborknockffa.token.TokenManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ItemManager implements Listener {
    private static File dataFile;
    private static FileConfiguration dataConfig;
    private static Map<UUID, PlayerSkinData> playerSkinData = new HashMap<>();
    private static final String SHOP_NAME = "§bItem Shop";
    public static final String STICK_SHOP_NAME = "§bStick Shop";
    public static final String BLOCK_SHOP_NAME = "§bBlock Shop";

    public static void initialize(File dataFolder) {
        dataFile = new File(dataFolder, "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadAllPlayerSkinData();
    }

    public static PlayerSkinData getPlayerSkinData(UUID uuid) {
        return playerSkinData.get(uuid);
    }

    private static void loadAllPlayerSkinData() {
        if (dataConfig.contains("players")) {
            for (String uuidStr : dataConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                PlayerSkinData skinData = new PlayerSkinData(uuid);

                String equippedStickSkinName = dataConfig.getString("players." + uuidStr + ".equippedStickSkin", "STICK");
                String equippedBlockSkinName = dataConfig.getString("players." + uuidStr + ".equippedBlockSkin", "SANDSTONE");

                try {
                    skinData.setEquippedStickSkin(Skin.valueOf(equippedStickSkinName));
                    skinData.setEquippedBlockSkin(Skin.valueOf(equippedBlockSkinName));
                } catch (IllegalArgumentException e) {
                    skinData.setEquippedStickSkin(Skin.STICK);
                    skinData.setEquippedBlockSkin(Skin.SANDSTONE);
                }

                List<String> ownedSkins = dataConfig.getStringList("players." + uuidStr + ".ownedSkins");
                for (String skinName : ownedSkins) {
                    try {
                        skinData.addOwnedSkin(Skin.valueOf(skinName));
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                if (dataConfig.contains("players." + uuidStr + ".inventory")) {
                    for (String slotStr : dataConfig.getConfigurationSection("players." + uuidStr + ".inventory").getKeys(false)) {
                        int slot = Integer.parseInt(slotStr);
                        String itemType = dataConfig.getString("players." + uuidStr + ".inventory." + slotStr);
                        skinData.setInventoryItem(slot, itemType);
                    }
                } else {
                    setDefaultInventory(skinData);
                }

                playerSkinData.put(uuid, skinData);
            }
        }
    }

    private static void setDefaultInventory(PlayerSkinData skinData) {
        skinData.setInventoryItem(0, "knockback_stick");
        skinData.setInventoryItem(1, "block");
        skinData.setInventoryItem(2, "wooden_sword");
        skinData.setInventoryItem(3, "cobweb");
        skinData.setInventoryItem(4, "ender_pearl");
        skinData.setInventoryItem(8, "shop");
    }

    public static void saveData() {
        for (UUID uuid : playerSkinData.keySet()) {
            PlayerSkinData skinData = playerSkinData.get(uuid);
            String uuidStr = uuid.toString();

            dataConfig.set("players." + uuidStr + ".equippedStickSkin", skinData.getEquippedStickSkin().name());
            dataConfig.set("players." + uuidStr + ".equippedBlockSkin", skinData.getEquippedBlockSkin().name());

            List<String> ownedSkinNames = new ArrayList<>();
            for (Skin skin : skinData.getOwnedSkins()) {
                ownedSkinNames.add(skin.name());
            }
            dataConfig.set("players." + uuidStr + ".ownedSkins", ownedSkinNames);

            for (Map.Entry<Integer, String> entry : skinData.getInventoryLayout().entrySet()) {
                dataConfig.set("players." + uuidStr + ".inventory." + entry.getKey(), entry.getValue());
            }
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!playerSkinData.containsKey(uuid)) {
            PlayerSkinData skinData = new PlayerSkinData(uuid);

            skinData.addOwnedSkin(Skin.STICK);
            skinData.addOwnedSkin(Skin.SANDSTONE);

            setDefaultInventory(skinData);

            playerSkinData.put(uuid, skinData);
        }

        reloadPlayerInv(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("BoblaborKnockFFA"), () -> {
            reloadPlayerInv(player);
        }, 5L);
    }

    public static void reloadPlayerInv(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerSkinData.containsKey(uuid)) {
            return;
        }

        PlayerSkinData skinData = playerSkinData.get(uuid);
        player.getInventory().clear();

        for (Map.Entry<Integer, String> entry : skinData.getInventoryLayout().entrySet()) {
            int slot = entry.getKey();
            String itemType = entry.getValue();

            ItemStack item = null;

            switch (itemType) {
                case "knockback_stick":
                    item = createKnockbackStick(skinData.getEquippedStickSkin());
                    break;
                case "block":
                    item = createBuildingBlock(skinData.getEquippedBlockSkin());
                    break;
                case "shop":
                    item = createShopItem();
                    break;
                case "wooden_sword":
                    item = createWoodenSword();
                    break;
                case "cobweb":
                    item = createCobweb();
                    break;
                case "ender_pearl":
                    item = createEnderPearl();
                    break;
            }

            if (item != null) {
                player.getInventory().setItem(slot, item);
            }
        }
    }

    public static void reloadPlayerInv() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            reloadPlayerInv(player);
        }
    }

    public static ItemStack createKnockbackStick(Skin skin) {
        ItemStack item = new ItemStack(skin.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        meta.setDisplayName("§eKnockback Stick");
        List<String> lore = new ArrayList<>();
        lore.add("§7Knock your enemies away!");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createBuildingBlock(Skin skin) {
        ItemStack item = new ItemStack(skin.getMaterial(), 64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eBuilding Block");
        List<String> lore = new ArrayList<>();
        lore.add("§7Place blocks to build!");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createWoodenSword() {
        ItemStack item = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        meta.setDisplayName("§eKnockback Sword");
        List<String> lore = new ArrayList<>();
        lore.add("§7For close combat!");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createCobweb() {
        ItemStack item = new ItemStack(Material.COBWEB, 3);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eCobweb");
        List<String> lore = new ArrayList<>();
        lore.add("§7Slow down your enemies!");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createEnderPearl() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§eEnder Pearl");
        List<String> lore = new ArrayList<>();
        lore.add("§7Teleport to safety!");
        meta.setLore(lore);

        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createShopItem() {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SHOP_NAME);
        List<String> lore = new ArrayList<>();
        lore.add("§7Right-click to open the shop.");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static void openMainShopMenu(Player player) {
        Inventory shopInventory = Bukkit.createInventory(null, 3 * 9, "§6Main Shop");

        ItemStack stickShopButton = new ItemStack(Material.STICK);
        ItemMeta stickMeta = stickShopButton.getItemMeta();
        stickMeta.setDisplayName("§aStick Skins Shop");
        List<String> stickLore = new ArrayList<>();
        stickLore.add("§7Click to browse stick skins");
        stickMeta.setLore(stickLore);
        stickShopButton.setItemMeta(stickMeta);
        shopInventory.setItem(11, stickShopButton);

        ItemStack blockShopButton = new ItemStack(Material.SANDSTONE);
        ItemMeta blockMeta = blockShopButton.getItemMeta();
        blockMeta.setDisplayName("§aBlock Skins Shop");
        List<String> blockLore = new ArrayList<>();
        blockLore.add("§7Click to browse block skins");
        blockMeta.setLore(blockLore);
        blockShopButton.setItemMeta(blockMeta);
        shopInventory.setItem(15, blockShopButton);

        ItemStack layoutEditorButton = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta layoutMeta = layoutEditorButton.getItemMeta();
        layoutMeta.setDisplayName("§aInventory Layout Editor");
        List<String> layoutLore = new ArrayList<>();
        layoutLore.add("§7Click to customize your inventory layout");
        layoutMeta.setLore(layoutLore);
        layoutEditorButton.setItemMeta(layoutMeta);
        shopInventory.setItem(22, layoutEditorButton);

        player.openInventory(shopInventory);
    }

    public static void openStickShop(Player player) {
        Inventory shopInventory = Bukkit.createInventory(null, 5 * 9, STICK_SHOP_NAME);

        UUID uuid = player.getUniqueId();
        PlayerSkinData skinData = playerSkinData.get(uuid);

        int slot = 0;
        for (Skin skin : Skin.values()) {
            if (skin.isStick()) {
                ItemStack skinItem = new ItemStack(skin.getMaterial());
                ItemMeta meta = skinItem.getItemMeta();
                meta.setDisplayName(skin.getName());

                List<String> lore = new ArrayList<>();
                lore.add("§7Cost: §6" + skin.getPrice() + " tokens");

                if (skinData.ownsItem(skin)) {
                    lore.add("§aOwned");

                    if (skinData.getEquippedStickSkin() == skin) {
                        lore.add("§bEquipped");
                    } else {
                        lore.add("§7Click to equip");
                    }
                } else {
                    lore.add("§7Click to purchase");
                }

                meta.setLore(lore);
                skinItem.setItemMeta(meta);

                shopInventory.setItem(slot, skinItem);
                slot++;
            }
        }

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§cBack to Main Shop");
        backButton.setItemMeta(backMeta);
        shopInventory.setItem(40, backButton);

        player.openInventory(shopInventory);
    }

    public static void openBlockShop(Player player) {
        openBlockShopPage(player, 0);
    }

    public static void openBlockShopPage(Player player, int page) {
        Inventory shopInventory = Bukkit.createInventory(null, 6 * 9, BLOCK_SHOP_NAME + " §7(Seite " + (page + 1) + ")");

        UUID uuid = player.getUniqueId();
        PlayerSkinData skinData = playerSkinData.get(uuid);

        List<Skin> blockSkins = new ArrayList<>();
        for (Skin skin : Skin.values()) {
            if (!skin.isStick()) {
                blockSkins.add(skin);
            }
        }

        int itemsPerPage = 45;
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, blockSkins.size());

        for (int i = startIndex; i < endIndex; i++) {
            Skin skin = blockSkins.get(i);
            int slot = i - startIndex;

            ItemStack skinItem = new ItemStack(skin.getMaterial());
            ItemMeta meta = skinItem.getItemMeta();
            meta.setDisplayName(skin.getName());

            List<String> lore = new ArrayList<>();
            lore.add("§7Preis: §6" + skin.getPrice() + " Tokens");

            if (skinData.ownsItem(skin)) {
                lore.add("§aFreigeschaltet");
                if (skinData.getEquippedBlockSkin() == skin) {
                    lore.add("§bAktiviert");
                } else {
                    lore.add("§7Klick zum Aktivieren");
                }
            } else {
                lore.add("§7Klick zum Kaufen");
            }

            meta.setLore(lore);
            skinItem.setItemMeta(meta);
            shopInventory.setItem(slot, skinItem);
        }

        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta meta = prevPage.getItemMeta();
            meta.setDisplayName("§aVorherige Seite");
            prevPage.setItemMeta(meta);
            shopInventory.setItem(48, prevPage);
        }

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("§cZurück zum Shop");
        backButton.setItemMeta(backMeta);
        shopInventory.setItem(49, backButton);

        if (endIndex < blockSkins.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName("§aNächste Seite");
            nextPage.setItemMeta(meta);
            shopInventory.setItem(50, nextPage);
        }

        player.openInventory(shopInventory);
    }

    public static void openLayoutEditor(Player player) {
        InventoryLayoutEditor.openLayoutEditor(player);
    }

    public static void purchaseSkin(Player player, Skin skin) {
        UUID uuid = player.getUniqueId();
        PlayerSkinData skinData = playerSkinData.get(uuid);

        if (skinData.ownsItem(skin)) {
            if (skin.isStick()) {
                skinData.setEquippedStickSkin(skin);
                player.sendMessage("§aYou've equipped the " + skin.name() + " skin!");
            } else {
                skinData.setEquippedBlockSkin(skin);
                player.sendMessage("§aYou've equipped the " + skin.name() + " skin!");
            }

            saveData();
            reloadPlayerInv(player);
            return;
        }

        int playerTokens = TokenManager.getPlayerTokens(player);

        if (playerTokens >= skin.getPrice()) {
            TokenManager.removePlayerTokens(player, skin.getPrice());

            skinData.addOwnedSkin(skin);

            if (skin.isStick()) {
                skinData.setEquippedStickSkin(skin);
            } else {
                skinData.setEquippedBlockSkin(skin);
            }

            player.sendMessage("§aYou've purchased and equipped the " + skin.name() + " skin!");

            saveData();
            reloadPlayerInv(player);

            // Reopen the shop
            if (skin.isStick()) {
                openStickShop(player);
            } else {
                openBlockShop(player);
            }
        } else {
            player.sendMessage("§cYou don't have enough tokens to purchase this skin!");
        }
    }

    public static void updateInventoryLayout(Player player, int slot, String itemType) {
        UUID uuid = player.getUniqueId();
        PlayerSkinData skinData = playerSkinData.get(uuid);

        if (slot >= 0 && slot <= 8) {
            skinData.setInventoryItem(slot, itemType);

            boolean hasShop = false;
            for (String type : skinData.getInventoryLayout().values()) {
                if (type.equals("shop")) {
                    hasShop = true;
                    break;
                }
            }

            if (!hasShop) {
                skinData.setInventoryItem(8, "shop");
            }
            saveData();
            reloadPlayerInv(player);

            player.sendMessage("§aYour inventory layout has been updated!");
        }
    }

    public static class PlayerSkinData {
        private UUID playerUuid;
        private Skin equippedStickSkin = Skin.STICK;
        private Skin equippedBlockSkin = Skin.SANDSTONE;
        private List<Skin> ownedSkins = new ArrayList<>();
        private Map<Integer, String> inventoryLayout = new HashMap<>();

        public PlayerSkinData(UUID uuid) {
            this.playerUuid = uuid;
            this.ownedSkins.add(Skin.STICK);
            this.ownedSkins.add(Skin.SANDSTONE);
        }

        public void setInventoryLayout(Map<Integer, String> layout) {
            this.inventoryLayout = new HashMap<>(layout);
        }

        public UUID getPlayerUuid() {
            return playerUuid;
        }

        public Skin getEquippedStickSkin() {
            return equippedStickSkin;
        }

        public void setEquippedStickSkin(Skin skin) {
            if (skin.isStick()) {
                this.equippedStickSkin = skin;
            }
        }

        public Skin getEquippedBlockSkin() {
            return equippedBlockSkin;
        }

        public void setEquippedBlockSkin(Skin skin) {
            if (!skin.isStick()) {
                this.equippedBlockSkin = skin;
            }
        }

        public List<Skin> getOwnedSkins() {
            return ownedSkins;
        }

        public void addOwnedSkin(Skin skin) {
            if (!ownedSkins.contains(skin)) {
                ownedSkins.add(skin);
            }
        }

        public boolean ownsItem(Skin skin) {
            return ownedSkins.contains(skin);
        }

        public Map<Integer, String> getInventoryLayout() {
            return inventoryLayout;
        }

        public void setInventoryItem(int slot, String itemType) {
            inventoryLayout.put(slot, itemType);
        }
    }
}
package lab.solarstorm.boblaborknockffa;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum Skin {

    // Stick Skins
    STICK("§7Stick", true, Material.STICK, 0),

    // Price 25
    BONE("§2Bone", true, Material.BONE, 25),
    BAMBOO("§2Bamboo", true, Material.BAMBOO, 25),

    // Price 100
    CARROT("§9Carrot", true, Material.CARROT, 100),
    ARROW("§9Arrow", true, Material.ARROW, 100),
    KELP("§9Kelp", true, Material.KELP, 100),
    FEATHER("§9Feather", true, Material.FEATHER, 100),
    POPPY("§9Poppy", true, Material.POPPY, 100),
    DANDELION("§9Dandelion", true, Material.DANDELION, 100),
    RED_MUSHROOM("§9Red Mushroom", true, Material.RED_MUSHROOM, 100),
    BROWN_MUSHROOM("§9Brown Mushroom", true, Material.BROWN_MUSHROOM, 100),

    // Price 250
    ALLIUM("§5Allium", true, Material.ALLIUM, 250),
    WHEAT("§5Wheat", true, Material.WHEAT, 250),
    BREAD("§5Bread", true, Material.BREAD, 250),
    SALMON("§5Salmon", true, Material.SALMON, 250),
    COD("§5Cod", true, Material.COD, 250),
    BOOK("§5Book", true, Material.BOOK, 250),

    // Price 500
    BREEZE_ROD("§eBreeze Rod", true, Material.BREEZE_ROD, 500),
    BRUSH("§eBrush", true, Material.BRUSH, 500),
    FIREWORK("§eFirework", true, Material.FIREWORK_ROCKET, 500),
    TORCH_FLOWER("§eTorch Flower", true, Material.TORCHFLOWER, 500),
    RABBIT_FOOT("§eRabbit Foot", true, Material.RABBIT_FOOT, 500),
    AMETHYST_SHARD("§eAmethyst Shard", true, Material.AMETHYST_SHARD, 500),
    CLOWN_FISH("§eClown Fish", true, Material.TROPICAL_FISH, 500),
    PRISMARINE_SHARD("§ePrismarine Shard", true, Material.PRISMARINE_SHARD, 500),

    // Price 1000
    PUFFER_FISH("§4Puffer Fish", true, Material.PUFFERFISH, 1000),
    BLAZE_ROD("§4Blaze Rod", true, Material.BLAZE_ROD, 1000),
    SPECTRAL_ARROW("§4Spectral Arrow", true, Material.SPECTRAL_ARROW, 1000),
    GOLDEN_CARROT("§4Golden Carrot", true, Material.GOLDEN_CARROT, 1000),
    ECHO_SHARD("§4Echo Shard", true, Material.ECHO_SHARD, 1000),
    GOAT_HORN("§4Goat Horn", true, Material.GOAT_HORN, 1000),
    SPYGLASS("§4Spyglass", true, Material.SPYGLASS, 1000),

    // Block Skins
    SANDSTONE("§7Sandstone", false, Material.SANDSTONE, 0),

    // Price 25
    RED_SANDSTONE("§2Red Sandstone", false, Material.RED_SANDSTONE, 25),

    // Price 100
    STONE_BRICKS("§9Stone Bricks", false, Material.STONE_BRICKS, 100),
    TUFF_BRICKS("§9Tuff Bricks", false, Material.TUFF, 100),
    DEEPSLATE_BRICKS("§9Deepslate Bricks", false, Material.DEEPSLATE_BRICKS, 100),
    BLACKSTONE_BRICKS("§9Blackstone Bricks", false, Material.POLISHED_BLACKSTONE_BRICKS, 100),
    BRICKS("§9Bricks", false, Material.BRICKS, 100),
    MUD_BRICKS("§9Mud Bricks", false, Material.MUD_BRICKS, 100),
    WOOL_WHITE("§9White Wool", false, Material.WHITE_WOOL, 100),
    WOOL_ORANGE("§9Orange Wool", false, Material.ORANGE_WOOL, 100),
    WOOL_MAGENTA("§9Magenta Wool", false, Material.MAGENTA_WOOL, 100),
    WOOL_LIGHT_BLUE("§9Light Blue Wool", false, Material.LIGHT_BLUE_WOOL, 100),
    WOOL_YELLOW("§9Yellow Wool", false, Material.YELLOW_WOOL, 100),
    WOOL_LIME("§9Lime Wool", false, Material.LIME_WOOL, 100),
    WOOL_PINK("§9Pink Wool", false, Material.PINK_WOOL, 100),
    WOOL_GRAY("§9Gray Wool", false, Material.GRAY_WOOL, 100),
    WOOL_LIGHT_GRAY("§9Light Gray Wool", false, Material.LIGHT_GRAY_WOOL, 100),
    WOOL_CYAN("§9Cyan Wool", false, Material.CYAN_WOOL, 100),
    WOOL_PURPLE("§9Purple Wool", false, Material.PURPLE_WOOL, 100),
    WOOL_BLUE("§9Blue Wool", false, Material.BLUE_WOOL, 100),
    WOOL_BROWN("§9Brown Wool", false, Material.BROWN_WOOL, 100),
    WOOL_GREEN("§9Green Wool", false, Material.GREEN_WOOL, 100),
    WOOL_RED("§9Red Wool", false, Material.RED_WOOL, 100),
    WOOL_BLACK("§9Black Wool", false, Material.BLACK_WOOL, 100),
    DIRT("§9Dirt", false, Material.DIRT, 100),
    STONE("§9Stone", false, Material.STONE, 100),
    NETHERRACK("§9Netherrack", false, Material.NETHERRACK, 100),
    ENDSTONE("§9Endstone", false, Material.END_STONE, 100),
    COBBLESTONE("§9Cobblestone", false, Material.COBBLESTONE, 100),
    COBBLE_DEEPSLATE("§9Cobble Deepslate", false, Material.COBBLED_DEEPSLATE, 100),
    SNOW("§9Snow", false, Material.SNOW_BLOCK, 100),
    CLAY("§9Clay", false, Material.CLAY, 100),

    // Price 250
    OAK_PLANKS("§5Oak Planks", false, Material.OAK_PLANKS, 250),
    BIRCH_PLANKS("§5Birch Planks", false, Material.BIRCH_PLANKS, 250),
    SPRUCE_PLANKS("§5Spruce Planks", false, Material.SPRUCE_PLANKS, 250),
    JUNGLE_PLANKS("§5Jungle Planks", false, Material.JUNGLE_PLANKS, 250),
    ACACIA_PLANKS("§5Acacia Planks", false, Material.ACACIA_PLANKS, 250),
    DARK_OAK_PLANKS("§5Dark Oak Planks", false, Material.DARK_OAK_PLANKS, 250),
    CHERRY_PLANKS("§5Cherry Planks", false, Material.CHERRY_PLANKS, 250),
    MANGROVE_PLANKS("§5Mangrove Planks", false, Material.MANGROVE_PLANKS, 250),
    WARPED_PLANKS("§5Warped Planks", false, Material.WARPED_PLANKS, 250),
    CRIMSON_PLANKS("§5Crimson Planks", false, Material.CRIMSON_PLANKS, 250),
    BAMBOO_PLANKS("§5Bamboo Planks", false, Material.BAMBOO_PLANKS, 250),
    WHITE_CONCRETE("§5White Concrete", false, Material.WHITE_CONCRETE, 250),
    ORANGE_CONCRETE("§5Orange Concrete", false, Material.ORANGE_CONCRETE, 250),
    MAGENTA_CONCRETE("§5Magenta Concrete", false, Material.MAGENTA_CONCRETE, 250),
    LIGHT_BLUE_CONCRETE("§5Light Blue Concrete", false, Material.LIGHT_BLUE_CONCRETE, 250),
    YELLOW_CONCRETE("§5Yellow Concrete", false, Material.YELLOW_CONCRETE, 250),
    LIME_CONCRETE("§5Lime Concrete", false, Material.LIME_CONCRETE, 250),
    PINK_CONCRETE("§5Pink Concrete", false, Material.PINK_CONCRETE, 250),
    GRAY_CONCRETE("§5Gray Concrete", false, Material.GRAY_CONCRETE, 250),
    LIGHT_GRAY_CONCRETE("§5Light Gray Concrete", false, Material.LIGHT_GRAY_CONCRETE, 250),
    CYAN_CONCRETE("§5Cyan Concrete", false, Material.CYAN_CONCRETE, 250),
    PURPLE_CONCRETE("§5Purple Concrete", false, Material.PURPLE_CONCRETE, 250),
    BLUE_CONCRETE("§5Blue Concrete", false, Material.BLUE_CONCRETE, 250),
    BROWN_CONCRETE("§5Brown Concrete", false, Material.BROWN_CONCRETE, 250),
    GREEN_CONCRETE("§5Green Concrete", false, Material.GREEN_CONCRETE, 250),
    RED_CONCRETE("§5Red Concrete", false, Material.RED_CONCRETE, 250),
    BLACK_CONCRETE("§5Black Concrete", false, Material.BLACK_CONCRETE, 250),
    POLISHED_ANDESITE("§5Polished Andesite", false, Material.POLISHED_ANDESITE, 250),
    POLISHED_DIAMOND("§5Polished Diorite", false, Material.POLISHED_DIORITE, 250),
    POLISHED_GRANITE("§5Polished Granite", false, Material.POLISHED_GRANITE, 250),
    QUARTZ_BLOCK("§5Quartz Block", false, Material.QUARTZ_BLOCK, 250),
    PURPUR_BLOCK("§5Purpur Block", false, Material.PURPUR_BLOCK, 250),
    MOSSY_COBBLESTONE("§5Mossy Cobblestone", false, Material.MOSSY_COBBLESTONE, 250),
    PRISMARINE_BLOCK("§5Prismarine Block", false, Material.PRISMARINE, 250),
    PRISMARINE_BRICKS("§5Prismarine Bricks", false, Material.PRISMARINE_BRICKS, 250),
    DARK_PRISMARINE("§5Dark Prismarine", false, Material.DARK_PRISMARINE, 250),
    NETHER_BRICKS("§5Nether Brick", false, Material.NETHER_BRICKS, 250),
    RED_NETHER_BRICK("§5Red Nether Brick", false, Material.RED_NETHER_BRICKS, 250),
    MOSS_BLOCK("§5Moss Block", false, Material.MOSS_BLOCK, 250),
    BONE_BLOCK("§5Bone Block", false, Material.BONE_BLOCK, 250),
    NETHERWART_BLOCK("§5Netherwart Block", false, Material.NETHER_WART_BLOCK, 250),
    WARPED_WART_BLOCK("§5Warped Wart Block", false, Material.WARPED_WART_BLOCK, 250),

    // Price 500
    PUMPKIN("§ePumpkin", false, Material.PUMPKIN, 500),
    HAY_BALE("§eHay Bale", false, Material.HAY_BLOCK, 500),
    MELON_BLOCK("§eMelon Block", false, Material.MELON, 500),
    HONEYCOMB("§eHoneycomb", false, Material.HONEYCOMB_BLOCK, 500),
    OBSIDIAN("§eObsidian", false, Material.OBSIDIAN, 500),
    SCULK_BLOCK("§eSculk Block", false, Material.SCULK, 500),
    WAXED_COPPER("§eWaxed Copper Block", false, Material.WAXED_COPPER_BLOCK, 500),
    WAXED_OXIDIZED_COPPER("§eWaxed Oxidized Copper", false, Material.WAXED_OXIDIZED_COPPER, 500),
    WAXED_GRATE("§eWaxed Copper Grate", false, Material.WAXED_COPPER_GRATE, 500),
    WAXED_OXIDIZED_GRATE("§eWaxed Oxidized Grate", false, Material.WAXED_OXIDIZED_COPPER_GRATE, 500),
    GLASS("§eGlass", false, Material.GLASS, 500),
    WHITE_GLASS("§eWhite Glass", false, Material.WHITE_STAINED_GLASS, 500),
    ORANGE_GLASS("§eOrange Glass", false, Material.ORANGE_STAINED_GLASS, 500),
    MAGENTA_GLASS("§eMagenta Glass", false, Material.MAGENTA_STAINED_GLASS, 500),
    LIGHT_BLUE_GLASS("§eLight Blue Glass", false, Material.LIGHT_BLUE_STAINED_GLASS, 500),
    YELLOW_GLASS("§eYellow Glass", false, Material.YELLOW_STAINED_GLASS, 500),
    LIME_GLASS("§eLime Glass", false, Material.LIME_STAINED_GLASS, 500),
    PINK_GLASS("§ePink Glass", false, Material.PINK_STAINED_GLASS, 500),
    GRAY_GLASS("§eGray Glass", false, Material.GRAY_STAINED_GLASS, 500),
    LIGHT_GRAY_GLASS("§eLight Gray Glass", false, Material.LIGHT_GRAY_STAINED_GLASS, 500),
    CYAN_GLASS("§eCyan Glass", false, Material.CYAN_STAINED_GLASS, 500),
    PURPLE_GLASS("§ePurple Glass", false, Material.PURPLE_STAINED_GLASS, 500),
    BLUE_GLASS("§eBlue Glass", false, Material.BLUE_STAINED_GLASS, 500),
    BROWN_GLASS("§eBrown Glass", false, Material.BROWN_STAINED_GLASS, 500),
    GREEN_GLASS("§eGreen Glass", false, Material.GREEN_STAINED_GLASS, 500),
    RED_GLASS("§eRed Glass", false, Material.RED_STAINED_GLASS, 500),
    BLACK_GLASS("§eBlack Glass", false, Material.BLACK_STAINED_GLASS, 500),
    AMETHYST_BLOCK("§eAmethyst Block", false, Material.AMETHYST_BLOCK, 500),
    SHROOMLIGHT("§eShroomlight", false, Material.SHROOMLIGHT, 500),
    PRISMARINE_LIGHT("§ePrismarine Light", false, Material.SEA_LANTERN, 500),
    GLOWSTONE_BLOCK("§eGlowstone", false, Material.GLOWSTONE, 500),
    BOOKSHELF("§eBookshelf", false, Material.BOOKSHELF, 500),
    REDSTONE_BLOCK("§eRedstone Block", false, Material.REDSTONE_BLOCK, 500),
    LAPIS_BLOCK("§eLapis Block", false, Material.LAPIS_BLOCK, 500),
    IRON_BLOCK("§eIron Block", false, Material.IRON_BLOCK, 500),
    COAL_BLOCK("§eCoal Block", false, Material.COAL_BLOCK, 500),

    // Price 1000
    GOLD_BLOCK("§4Gold Block", false, Material.GOLD_BLOCK, 1000),
    EMERALD_BLOCK("§4Emerald Block", false, Material.EMERALD_BLOCK, 1000),
    DIAMOND_BLOCK("§4Diamond Block", false, Material.DIAMOND_BLOCK, 1000),
    NETHERITE_BLOCK("§4Netherite Block", false, Material.NETHERITE_BLOCK, 1000),
    RAW_GOLD_BLOCK("§4Raw Gold Block", false, Material.RAW_GOLD_BLOCK, 1000),
    ANCIENT_DEBRIS("§4Ancient Debris", false, Material.ANCIENT_DEBRIS, 1000),
    DIAMOND_ORE("§4Diamond Ore", false, Material.DIAMOND_ORE, 1000),
    BEDROCK("§4Bedrock", false, Material.BEDROCK, 1000),
    CRYING_OBSIDIAN("§4Crying Obsidian", false, Material.CRYING_OBSIDIAN, 1000),
    LODESTONE("§4Lodestone", false, Material.LODESTONE, 1000),
    ORCHRE_FROGLIGHT("§4Orchre Froglight", false, Material.OCHRE_FROGLIGHT, 1000),
    PEARL_FROGLIGHT("§4Pearl Froglight", false, Material.PEARLESCENT_FROGLIGHT, 1000),
    VERDANT_FROGLIGHT("§4Verdant Froglight", false, Material.VERDANT_FROGLIGHT, 1000),
    MOB_SPAWNER("§4Mob Spawner", false, Material.SPAWNER, 1000),
    AIR_SPAWNER("§4Barrier", false, Material.BARRIER, 202);

    private final boolean stick;
    private final Material material;
    private final String name;
    private final int price;

    Skin(String name, boolean stick, Material material, int price){
        this.name = name;
        this.stick = stick;
        this.material = material;
        this.price = price;
    }

    public static Skin getByName(String name) {
        for (Skin skin : values()) {
            if (skin.getName().equals(name)) return skin;
        }
        return null;
    }

}

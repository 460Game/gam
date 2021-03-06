package images;

import javafx.scene.image.Image;

public abstract class Images {

    public static final Image BOX_IMAGE = getImage("barrier.png");
    public static final Image WALL_IMAGE = getImage("wall.png");
    public static final Image LOGO_IMAGE = getImage("logo.png");
    public static final Image LOGO_TEXT_IMAGE = getImage("logotext.png");
    public static final Image CURSOR_IMAGE = getImage("cursor.png");
    public static final Image GAME_CURSOR_IMAGE = getImage("gamecursor.png");
    public static final Image UPGRADE_CURSOR_IMAGE = getImage("upgrade_cursor.png");
    public static final Image DELETE_CURSOR_IMAGE = getImage("delete_cursor.png");
    public static final Image UPGRADE_CURSOR_UI_IMAGE = getImage("upgrade_UI.png");
    public static final Image DELETE_CURSOR_UI_IMAGE = getImage("delete_UI.png");

    public static final Image WALL_BUILDABLE_IMAGE = getImage("wall_buildable.png");
    public static final Image WALLS_BUILDABLE_IMAGE = getImage("walls_buildable.png");
    public static final Image WALL_BUILDABLE_UI_IMAGE = getImage("wall_buildable_UI.png");
    public static final Image RED_RESOURCE_COLLECTOR_IMAGE = getImage("resource_collector_red.png");
    public static final Image BLUE_RESOURCE_COLLECTOR_IMAGE = getImage("resource_collector_blue.png");
    public static final Image RED_RESOURCE_COLLECTORS_IMAGE = getImage("resource_collectors_red.png");
    public static final Image BLUE_RESOURCE_COLLECTORS_IMAGE = getImage("resource_collectors_blue.png");
    public static final Image RESOURCE_COLLECTOR_UI_IMAGE = getImage("resource_collector_UI.png");
    public static final Image RED_BARRACKS_IMAGE = getImage("barracks_red.png");
    public static final Image BLUE_BARRACKS_IMAGE = getImage("barracks_blue.png");
    public static final Image RED_WOOD_BARRACKS_IMAGE = getImage("barracks_wood_red.png");
    public static final Image BLUE_WOOD_BARRACKS_IMAGE = getImage("barracks_wood_blue.png");
    public static final Image BARRACKS_UI_IMAGE = getImage("barracks_UI.png");
    public static final Image RED_ARROW_TOWER_IMAGE = getImage("arrow_towers_red.png");
    public static final Image BLUE_ARROW_TOWER_IMAGE = getImage("arrow_towers_blue.png");
    public static final Image RED_WOOD_ARROW_TOWER_IMAGE = getImage("arrow_tower_wood_red.png");
    public static final Image BLUE_WOOD_ARROW_TOWER_IMAGE = getImage("arrow_tower_wood_blue.png");
    public static final Image ARROW_TOWER_UI_IMAGE = getImage("arrow_tower_UI.png");

    public static final Image TREASURE_IMAGE = getImage("treasure.png");
    public static final Image ARROW_IMAGE = getImage("arrow.png");

    public static final Image RANGED_SYMBOL_IMAGE = getImage("ranged_symbol.png");
    public static final Image SIEGE_SYMBOL_IMAGE = getImage("siege_symbol.png");
    public static final Image EXPLORATION_SYMBOL_IMAGE = getImage("exploration_symbol.png");

    public static final Image SHOOTING_SYMBOL_IMAGE = getImage("shootIcon.png");
    public static final Image CHARGING_SYMBOL_IMAGE = getImage("chargeIcon.png");

    public static Image TILE_IMAGE = getImage("tile_map.png");
    public static Image MENU_SPASH_BG_IMAGE = getImage("castleSplash.jpg");
    public static final Image[] METAL_IMAGES = getImages("iron_ingots.png",
        "iron_ingots_1.png", "iron_ingots_2.png", "iron_ingots_3.png","iron_ingots_4.png","iron_ingots_5.png", "iron_ingots_6.png",
        "iron_ingots_7.png", "iron_ingots_8.png", "iron_ingots_9.png");
    public static final Image[] STONE_IMAGES = getImages("boulder.png","boulder_1.png","boulder_2.png","boulder_3.png", "boulder_4.png");
    public static final Image[] TREE_IMAGES = getImages("tree.png", "tree_1.png", "tree_2.png", "tree_3.png", "tree_4.png");

    public static final Image RED_KING_IMAGE_SHEET = getImage("king_red_sheet_new.png");
    public static final Image BLUE_KING_IMAGE_SHEET = getImage("king_blue_sheet_new.png");
    public static final Image RED_SLAYER_IMAGE_SHEET = getImage("slayer_red_sheet.png");
    public static final Image BLUE_SLAYER_IMAGE_SHEET = getImage("slayer_blue_sheet.png");
    public static final Image BLUE_RANGED_IMAGE_SHEET = getImage("minions_ranged_blue.png");
    public static final Image RED_RANGED_IMAGE_SHEET = getImage("minions_ranged_red.png"); // TODO
    public static final Image BLUE_MELEE_IMAGE_SHEET = getImage("slayer_blue_sheet.png");
    public static final Image RED_MELEE_IMAGE_SHEET = getImage("slayer_red_sheet.png");
    public static final Image BLUE_SIEGE_IMAGE_SHEET = getImage("minions_siege_blue.png");
    public static final Image RED_SIEGE_IMAGE_SHEET = getImage("minions_siege_red.png");
    public static final Image BLUE_EXPLORER_IMAGE_SHEET = getImage("minions_exploration_blue.png"); // TODO
    public static final Image RED_EXPLORER_IMAGE_SHEET = getImage("minions_exploration_red.png"); // TODO
    public static final Image BLUE_RESOURCE_MIONION_IMAGE_SHEET = getImage("minions_resource_blue.png");
    public static final Image RED_RESOURCE_MINION_IMAGE_SHEET = getImage("minions_resource_red.png");

    public static final Image RED_KING_SELECT = getImage("red_king_select.png");
    public static final Image RED_SLAYER_SELECT = getImage("red_slayer_select.png");
    public static final Image BLUE_KING_SELECT = getImage("blue_king_select.png");
    public static final Image BLUE_SLAYER_SELECT = getImage("blue_slayer_select.png");


    public static final Image FOG_BLACK_IMAGE = getImage("fog_black.png");
    public static final Image FOG_GREY_IMAGE = getImage("fog_grey.png");
    public static final Image FOG_GREY_EXPLORED_IMAGE = getImage("explored_fog.png");

    public static final Image METAL_ICON = getImage("metal_icon.png");
    public static final Image STONE_ICON = getImage("stone_icon.png");
    public static final Image WOOD_ICON = getImage("wood_icon.png");

    private static Image getImage(String s) {
        return new Image(Images.class.getResourceAsStream(s));
    }

    private static Image[] getImages(String... s) {
        Image[] arr = new Image[s.length];
        for(int i = 0; i < s.length; i++)
            arr[i] = getImage(s[i]);
        return arr;
    }
}

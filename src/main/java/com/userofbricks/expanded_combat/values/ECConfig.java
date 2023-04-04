package com.userofbricks.expanded_combat.values;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.util.IngredientUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ECConfig {

    public static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;
    public static final String CONFIG_PREFIX = "config." + ExpandedCombat.MODID + ".server.";
    private static final String TwilightForestMOD_ID = "twilightforest";

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
                .configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Server {
        public final ForgeConfigSpec.BooleanValue forceLoadTwilightForestCompat;
        public final ForgeConfigSpec.BooleanValue enableGauntlets;
        public final GauntletMaterial netheriteGauntlet, diamondGauntlet, goldGauntlet, ironGauntlet, leatherGauntlet;
        public final GauntletMaterial steelGauntlet, bronzeGauntlet, silverGauntlet, leadGauntlet;
        public final GauntletMaterial nagaGauntlet, ironwoodGauntlet, fieryGauntlet, steeleafGauntlet, knightlyGauntlet, yetiGauntlet, arcticGauntlet;
        public List<GauntletMaterial> gauntletMaterials = new ArrayList<>();

        public final ShieldMaterial emptyShield;
        public final ShieldMaterial netheriteShield, diamondShield, goldShield, ironShield;
        public final ShieldMaterial steelShield, bronzeShield, silverShield, leadShield;
        public final ShieldMaterial nagaShield, ironwoodShield, fieryShield,steeleafShield, knightlyShield;
        public List<ShieldMaterial> shieldMaterials = new ArrayList<>();

        public Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server only settings. ", "all options here require minecraft to restart")
                    .push("server");
            forceLoadTwilightForestCompat = builder.comment("Default value: false").translation(ECConfig.CONFIG_PREFIX + "ForceLoadTwilightForestCompat").define("ForceLoadTwilightForestCompat", false);

            builder.push("Gauntlets");
            enableGauntlets = builder.comment("Default value: true").translation(ECConfig.CONFIG_PREFIX + "EnableGauntlets").define("EnableGauntlets", true);

            netheriteGauntlet = new GauntletMaterial(builder, "Netherite", Tiers.NETHERITE, ArmorMaterials.NETHERITE,0.2, true, true, gauntletMaterials);
            diamondGauntlet =   new GauntletMaterial(builder, "Diamond",   Tiers.DIAMOND,   ArmorMaterials.DIAMOND,  -0.1,true, false, gauntletMaterials);
            goldGauntlet =      new GauntletMaterial(builder, "Gold",      Tiers.GOLD,      ArmorMaterials.GOLD,     2,   true, false, gauntletMaterials);
            ironGauntlet =      new GauntletMaterial(builder, "Iron",      Tiers.IRON,      ArmorMaterials.IRON,     0,   false,false, gauntletMaterials);
            leatherGauntlet =   new GauntletMaterial(builder, "Leather",   Tiers.STONE,     ArmorMaterials.LEATHER,  0,   false,false, gauntletMaterials);

            steelGauntlet =     new GauntletMaterial(builder, "Steel",    482,  10, 0d,  2, 2.5d, IngredientUtil.getTagedIngredientOrEmpty("forge", "ingots/steel"),  ArmorMaterials.IRON.getEquipSound(), 1d,    0d, false, gauntletMaterials);
            bronzeGauntlet =    new GauntletMaterial(builder, "Bronze",   225,  12, 0.1d,2, 2d,   IngredientUtil.getTagedIngredientOrEmpty("forge", "ingots/bronze"), ArmorMaterials.GOLD.getEquipSound(), 0.5d,  0d, false, gauntletMaterials);
            silverGauntlet =    new GauntletMaterial(builder, "Silver",   325,  23, 1d,  2, 1d,   IngredientUtil.getTagedIngredientOrEmpty("forge", "ingots/silver"), ArmorMaterials.GOLD.getEquipSound(), 0d,    0d, false, gauntletMaterials);
            leadGauntlet =      new GauntletMaterial(builder, "Lead",     1761, 10, 0.1d,3, 3d,   IngredientUtil.getTagedIngredientOrEmpty("forge", "ingots/lead"),   ArmorMaterials.GOLD.getEquipSound(), 0.25d, 0.5d, false, gauntletMaterials);

            //Twilight Forest
            ironwoodGauntlet =  new GauntletMaterial(builder, "Ironwood", 512, 25, 1.5, 2, 2, TwilightForestMOD_ID+":ironwood_ingot", new ResourceLocation("item.armor.equip_generic"), 0, 0, false, gauntletMaterials);
            fieryGauntlet =     new GauntletMaterial(builder, "Fiery",    1024, 10, 0, 4, 4, TwilightForestMOD_ID+":fiery_ingot", new ResourceLocation("item.armor.equip_generic"), 1.5, 0, true, gauntletMaterials);
            steeleafGauntlet =  new GauntletMaterial(builder, "Steeleaf", 131, 9, 0, 3, 3, TwilightForestMOD_ID+":steeleaf_ingot", new ResourceLocation("item.armor.equip_generic"), 0, 0, false, gauntletMaterials);
            knightlyGauntlet =  new GauntletMaterial(builder, "Knightly", 512, 8, 0,  3, 3, TwilightForestMOD_ID+":knightmetal_ingot", new ResourceLocation(TwilightForestMOD_ID, "item.twilightforest.armor.equip_knightmetal"), 1, 0, false, gauntletMaterials);
            nagaGauntlet =      new GauntletMaterial(builder, "Naga",     (int)(512*1.05), 15, 0.1, 3, 2.1d, TwilightForestMOD_ID+":naga_scale", new ResourceLocation("item.armor.equip_generic"), 0.5, 0, false, gauntletMaterials);
            yetiGauntlet =      new GauntletMaterial(builder, "Yeti",     512, 15, 0, 3, 2.5d, TwilightForestMOD_ID+":alpha_yeti_fur", new ResourceLocation("item.armor.equip_generic"), 3, 0, false, gauntletMaterials);
            arcticGauntlet =    new GauntletMaterial(builder, "Arctic",   131, 8, 0, 2, 2d, TwilightForestMOD_ID+":arctic_fur", new ResourceLocation("item.armor.equip_generic"), 2, 0, false, gauntletMaterials);

            builder.pop(1);
            builder.push("Shields");

            steelShield =       new ShieldMaterial(builder, "steel",    0,    3.5, 0.65, IngredientUtil.getTagedIngredientOrEmpty("forge", "ingots/steel"),    200, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            bronzeShield =      new ShieldMaterial(builder, "bronze",   0.1,  2.75,0.5,  IngredientUtil.getTagedIngredientOrEmpty("forge", "ingots/bronze"),   125, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            silverShield =      new ShieldMaterial(builder, "silver",   1,    2.5, 0.4,  IngredientUtil.getTagedIngredientOrEmpty("forge", "ingots/silver"),   175, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            leadShield =        new ShieldMaterial(builder, "lead",     0.1,  5,   0.6,  IngredientUtil.getTagedIngredientOrEmpty("forge", "ingots/lead"),     350, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);

            emptyShield =       new ShieldMaterial(builder, "empty", 0, 0, 0, "empty", 0, false, false, new ArrayList<>(), new ArrayList<>());

            //Twilight Forest
            nagaShield =        new ShieldMaterial(builder, "naga",     0.1,4,   0.65, TwilightForestMOD_ID+":naga_scale",     260, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            ironwoodShield =    new ShieldMaterial(builder, "ironwood", 1.5,3.5, 0.6,  TwilightForestMOD_ID+":ironwood_ingot", 250, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            fieryShield =       new ShieldMaterial(builder, "fiery",    0,  4.5, 0.7,  TwilightForestMOD_ID+":fiery_ingot",    275, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            steeleafShield =    new ShieldMaterial(builder, "steeleaf", 0,  3.5, 0.6,  TwilightForestMOD_ID+":steeleaf_ingot", 180, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            knightlyShield =    new ShieldMaterial(builder, "knightly", 0,  4,   0.6,  TwilightForestMOD_ID+":knightmetal_ingot", 250, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);

            ironShield =        new ShieldMaterial(builder, "iron",      0,   3,   0.6,  Tiers.IRON.getRepairIngredient(),     150,false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            goldShield =        new ShieldMaterial(builder, "gold",      2,   3,   0.4,  Tiers.GOLD.getRepairIngredient(),     40, false,false, new ArrayList<>(), new ArrayList<>(), shieldMaterials);
            diamondShield =     new ShieldMaterial(builder, "diamond",   -0.1,5,   0.75, Tiers.DIAMOND.getRepairIngredient(),  300,false,false, new ArrayList<>(Arrays.asList(steelShield.getName(), bronzeShield.getName(), silverShield.getName(), leadShield.getName(), nagaShield.getName(), ironwoodShield.getName(), fieryShield.getName(), steeleafShield.getName(), knightlyShield.getName())), new ArrayList<>(), shieldMaterials);
            netheriteShield =   new ShieldMaterial(builder, "netherite", 0.2, 6.5, 0.85, Tiers.NETHERITE.getRepairIngredient(),375,true, true, new ArrayList<>(Collections.singleton(diamondShield.getName())), new ArrayList<>(Collections.singleton(diamondShield.getName())), shieldMaterials);
        }
    }
}

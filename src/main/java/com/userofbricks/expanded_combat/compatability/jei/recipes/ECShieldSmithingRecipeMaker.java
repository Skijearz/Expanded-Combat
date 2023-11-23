package com.userofbricks.expanded_combat.compatability.jei.recipes;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.registry.ShieldToMaterials;
import com.userofbricks.expanded_combat.init.ECItems;
import com.userofbricks.expanded_combat.item.ECShieldItem;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.init.MaterialInit;
import com.userofbricks.expanded_combat.plugins.VanillaECPlugin;
import com.userofbricks.expanded_combat.item.recipes.IShieldSmithingRecipe;
import com.userofbricks.expanded_combat.item.recipes.StanderStyleShieldSmithingRecipe;
import com.userofbricks.expanded_combat.util.IngredientUtil;
import mezz.jei.api.helpers.IStackHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

import static com.userofbricks.expanded_combat.init.MaterialInit.shieldToMaterialsList;

public class ECShieldSmithingRecipeMaker {
    public static List<IShieldSmithingRecipe> createShieldSmithingRecipes(IStackHelper stackHelper) {
        List<IShieldSmithingRecipe> recipes = new ArrayList<>();
        List<ItemStack> bases = new ArrayList<>();
        bases.add(new ItemStack(Items.SHIELD));
        for (ShieldToMaterials shieldToMaterials : shieldToMaterialsList) {
            bases.add(new ItemStack(shieldToMaterials.itemLikeSupplier().get()));
        }
        for (Material material :
                MaterialInit.shieldMaterials) {
            ItemStack shield = new ItemStack(material.getConfig().fireResistant ? ECItems.SHIELD_TIER_3.get() : ECItems.SHIELD_TIER_1.get());
            shield.getOrCreateTag().putString(ECShieldItem.ULMaterialTagName, material.getName());
            shield.getOrCreateTag().putString(ECShieldItem.URMaterialTagName, material.getName());
            shield.getOrCreateTag().putString(ECShieldItem.DLMaterialTagName, material.getName());
            shield.getOrCreateTag().putString(ECShieldItem.DRMaterialTagName, material.getName());
            shield.getOrCreateTag().putString(ECShieldItem.MMaterialTagName, material.shieldUse == Material.ShieldUse.NOT_TRIM ? VanillaECPlugin.IRON.getName() : material.getName());
            bases.add(shield);
        }

        for (Material material :
                MaterialInit.shieldMaterials) {
            Ingredient addition = Ingredient.of(IngredientUtil.toItemLikeArray(material.getConfig().crafting.repairItem));
            if (addition.isEmpty() || addition.test(ItemStack.EMPTY) || addition.test(new ItemStack(Items.AIR))) continue;

            ItemStack resultShield = new ItemStack(material.getConfig().fireResistant ? ECItems.SHIELD_TIER_3.get() : ECItems.SHIELD_TIER_1.get());
            resultShield.getOrCreateTag().putString(ECShieldItem.ULMaterialTagName, material.getName());
            resultShield.getOrCreateTag().putString(ECShieldItem.URMaterialTagName, material.getName());
            resultShield.getOrCreateTag().putString(ECShieldItem.DLMaterialTagName, material.getName());
            resultShield.getOrCreateTag().putString(ECShieldItem.DRMaterialTagName, material.getName());
            if (material.shieldUse == Material.ShieldUse.ALL) resultShield.getOrCreateTag().putString(ECShieldItem.MMaterialTagName, material.getName());
            else resultShield.getOrCreateTag().putString(ECShieldItem.MMaterialTagName, VanillaECPlugin.IRON.getName());

            ResourceLocation id = new ResourceLocation(ExpandedCombat.MODID, "jei.shield.smithing." + resultShield.getDescriptionId());

            if (material.getConfig().crafting.isSingleAddition && material.getCraftedFrom() != null) {
                ItemStack baseShield = new ItemStack(material.getCraftedFrom().getConfig().fireResistant ? ECItems.SHIELD_TIER_3.get() : ECItems.SHIELD_TIER_1.get());
                baseShield.getOrCreateTag().putString(ECShieldItem.ULMaterialTagName, material.getCraftedFrom().getName());
                baseShield.getOrCreateTag().putString(ECShieldItem.URMaterialTagName, material.getCraftedFrom().getName());
                baseShield.getOrCreateTag().putString(ECShieldItem.DLMaterialTagName, material.getCraftedFrom().getName());
                baseShield.getOrCreateTag().putString(ECShieldItem.DRMaterialTagName, material.getCraftedFrom().getName());
                baseShield.getOrCreateTag().putString(ECShieldItem.MMaterialTagName, material.getCraftedFrom().getName());

                recipes.add(new StanderStyleShieldSmithingRecipe(id, Ingredient.of(baseShield), Ingredient.EMPTY, Ingredient.EMPTY, addition, Ingredient.EMPTY, Ingredient.EMPTY, resultShield));
            } else {
                Ingredient basesIngrediant = Ingredient.of(bases.stream());
                Ingredient ironIngotIngredient = Ingredient.of(Items.IRON_INGOT);

                if (material.shieldUse == Material.ShieldUse.ALL) recipes.add(new StanderStyleShieldSmithingRecipe(id, basesIngrediant, addition, resultShield));
                else recipes.add(new StanderStyleShieldSmithingRecipe(id, basesIngrediant, addition, addition, ironIngotIngredient, addition, addition, resultShield));
            }
        }
        return recipes;
    }
}

package com.userofbricks.expanded_combat.compatability.jei;

import com.tterrag.registrate.util.entry.RegistryEntry;
import com.userofbricks.expanded_combat.client.renderer.gui.screen.inventory.FletchingTableScreen;
import com.userofbricks.expanded_combat.compatability.jei.container_handelers.CuriosContainerHandler;
import com.userofbricks.expanded_combat.compatability.jei.item_subtype.ShieldSubtypeInterpreter;
import com.userofbricks.expanded_combat.compatability.jei.recipe_category.FletchingRecipeCategory;
import com.userofbricks.expanded_combat.compatability.jei.recipes.ECFletchingTippedArrowRecipeMaker;
import com.userofbricks.expanded_combat.compatability.jei.recipes.ECPotionWeaponRecipeMaker;
import com.userofbricks.expanded_combat.compatability.jei.recipes.ECTippedArrowRecipeMaker;
import com.userofbricks.expanded_combat.inventory.container.ECContainers;
import com.userofbricks.expanded_combat.inventory.container.FletchingTableMenu;
import com.userofbricks.expanded_combat.item.ECItems;
import com.userofbricks.expanded_combat.item.ECWeaponItem;
import com.userofbricks.expanded_combat.item.materials.Material;
import com.userofbricks.expanded_combat.item.materials.MaterialInit;
import com.userofbricks.expanded_combat.item.recipes.IFletchingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.plugins.vanilla.brewing.PotionSubtypeInterpreter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.client.gui.CuriosScreen;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.userofbricks.expanded_combat.ExpandedCombat.MODID;
import static com.userofbricks.expanded_combat.compatability.jei.recipe_category.FletchingRecipeCategory.FLETCHING;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ECJEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private IRecipeCategory<IFletchingRecipe> fletchingCategory;
    //@Nullable
    //private IRecipeCategory<ShieldSmithingRecipie> shieldSmithingCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (Material material :
                MaterialInit.arrowMaterials) {
            registration.registerSubtypeInterpreter(material.getTippedArrowEntry().get(), PotionSubtypeInterpreter.INSTANCE);
        }
        for (Material material :
                MaterialInit.weaponMaterials) {
            material.getWeapons().forEach((weaponName, registryEntry) -> {
                if (registryEntry.get() instanceof ECWeaponItem.HasPotion) registration.registerSubtypeInterpreter(registryEntry.get(), PotionSubtypeInterpreter.INSTANCE);
            });
        }
        registration.registerSubtypeInterpreter(ECItems.SHIELD_TIER_1.get(), ShieldSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(ECItems.SHIELD_TIER_2.get(), ShieldSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(ECItems.SHIELD_TIER_3.get(), ShieldSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(ECItems.SHIELD_TIER_4.get(), ShieldSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(
                fletchingCategory = new FletchingRecipeCategory(guiHelper)
                //shieldSmithingCategory = new ShieldSmithingRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ErrorUtil.checkNotNull(fletchingCategory, "fletchingCategory");

        IIngredientManager ingredientManager = registration.getIngredientManager();
        IVanillaRecipeFactory vanillaRecipeFactory = registration.getVanillaRecipeFactory();
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        ECRecipes vanillaRecipes = new ECRecipes(ingredientManager);
        for (Material material :
                MaterialInit.arrowMaterials) {
            registration.addRecipes(RecipeTypes.CRAFTING, ECTippedArrowRecipeMaker.createRecipes(stackHelper, material.getArrowEntry().get()));
        }

        for (RegistryEntry<? extends Item> item:ECItems.ITEMS) {
            if (item.get() instanceof ECWeaponItem.HasPotion) {
                registration.addRecipes(RecipeTypes.CRAFTING, ECPotionWeaponRecipeMaker.createRecipes(stackHelper, (ECWeaponItem.HasPotion) item.get()));
            }
        }

        registration.addRecipes(FLETCHING, vanillaRecipes.getFletchingRecipes(fletchingCategory));
        registration.addRecipes(FLETCHING, ECFletchingTippedArrowRecipeMaker.createTippedArrowRecipes(stackHelper));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(FletchingTableMenu.class, ECContainers.FLETCHING.get(), FLETCHING, 0, 2, 3, 36);
    }
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.FLETCHING_TABLE), FLETCHING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FletchingTableScreen.class, 102, 48, 22, 15, FLETCHING);
        registration.addGuiContainerHandler(CuriosScreen.class, new CuriosContainerHandler());
    }
}
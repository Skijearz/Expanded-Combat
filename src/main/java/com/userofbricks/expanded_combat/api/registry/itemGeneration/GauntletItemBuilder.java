package com.userofbricks.expanded_combat.api.registry.itemGeneration;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.userofbricks.expanded_combat.api.NonNullQuadConsumer;
import com.userofbricks.expanded_combat.api.NonNullTriConsumer;
import com.userofbricks.expanded_combat.api.NonNullTriFunction;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.material.MaterialBuilder;
import com.userofbricks.expanded_combat.api.material.WeaponMaterial;
import com.userofbricks.expanded_combat.item.ECItemTags;
import com.userofbricks.expanded_combat.plugins.VanillaECPlugin;
import com.userofbricks.expanded_combat.item.recipes.conditions.ECConfigBooleanCondition;
import com.userofbricks.expanded_combat.item.recipes.conditions.ECMaterialBooleanCondition;
import com.userofbricks.expanded_combat.util.IngredientUtil;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.userofbricks.expanded_combat.ExpandedCombat.MODID;
import static com.userofbricks.expanded_combat.ExpandedCombat.REGISTRATE;

public class GauntletItemBuilder extends MaterialItemBuilder {
    public static final List<TrimModelData> GENERATED_TRIM_MODELS = List.of(
            new TrimModelData("quartz", 0.1F, Map.of()),
            new TrimModelData("iron", 0.2F, Map.of("Iron", "iron_darker")),
            new TrimModelData("netherite", 0.3F, Map.of("Netherite", "netherite_darker")),
            new TrimModelData("redstone", 0.4F, Map.of()),
            new TrimModelData("copper", 0.5F, Map.of()),
            new TrimModelData("gold", 0.6F, Map.of("Gold", "gold_darker")),
            new TrimModelData("emerald", 0.7F, Map.of()),
            new TrimModelData("diamond", 0.8F, Map.of("Diamond", "diamond_darker")),
            new TrimModelData("lapis", 0.9F, Map.of()),
            new TrimModelData("amethyst", 1.0F, Map.of()));

    public final MaterialBuilder materialBuilder;
    public final Material material, craftedFrom;
    public final ItemBuilder<? extends Item, Registrate> itemBuilder;
    private String lang = "";
    private NonNullTriConsumer<ItemBuilder<? extends Item, Registrate>, Material, Boolean> modelBuilder;
    private NonNullTriConsumer<ItemBuilder<? extends Item, Registrate>, Material, @Nullable Material> recipeBuilder;
    private NonNullConsumer<ItemBuilder<? extends Item, Registrate>> colorBuilder;

    public GauntletItemBuilder(MaterialBuilder materialBuilder, Registrate registrate, Material material, Material craftedFrom, NonNullBiFunction<Item.Properties, Material, ? extends Item> constructor) {
        ItemBuilder<? extends Item, Registrate> itemBuilder = registrate.item(material.getLocationName().getPath() + "_gauntlet", (p) -> constructor.apply(p, material));

        itemBuilder.tag(ECItemTags.GAUNTLETS, ItemTags.TRIMMABLE_ARMOR);

        this.material = material;
        this.itemBuilder = itemBuilder;
        this.materialBuilder = materialBuilder;
        this.craftedFrom = craftedFrom;
        lang = material.getName() + " Gauntlet";
        modelBuilder = GauntletItemBuilder::generateModel;
        recipeBuilder = GauntletItemBuilder::generateRecipes;
        colorBuilder = GauntletItemBuilder::colors;
    }
    public GauntletItemBuilder lang(String englishName) {
        lang = englishName;
        return this;
    }
    public GauntletItemBuilder model(NonNullTriConsumer<ItemBuilder<? extends Item, Registrate>, Material, Boolean> modelBuilder) {
        this.modelBuilder = modelBuilder;
        return this;
    }
    public GauntletItemBuilder recipes(NonNullTriConsumer<ItemBuilder<? extends Item, Registrate>, Material, Material> recipeBuilder) {
        this.recipeBuilder = recipeBuilder;
        return this;
    }

    public GauntletItemBuilder color(NonNullConsumer<ItemBuilder<? extends Item, Registrate>> colorBuilder) {
        this.colorBuilder = colorBuilder;
        return this;
    }

    public MaterialBuilder build(boolean dyeable) {
        itemBuilder.lang(lang);
        modelBuilder.apply(itemBuilder, material, dyeable);
        recipeBuilder.apply(itemBuilder, material, craftedFrom);
        if (dyeable) colorBuilder.accept(itemBuilder);

        materialBuilder.gauntlet(m -> itemBuilder.register());
        return materialBuilder;
    }

    public static void colors(ItemBuilder<? extends Item, Registrate> itemBuilder) {
        itemBuilder.color(() -> () -> (stack, itemLayer) -> (itemLayer == 0) ? ((DyeableLeatherItem)stack.getItem()).getColor(stack) : -1);
    }
    public static void generateModel(ItemBuilder<? extends Item, Registrate> itemBuilder, Material material, boolean dyeable) {
        String locationName = material.getLocationName().getPath();
        itemBuilder.model((ctx, prov) -> {
            ResourceLocation main_texture = new ResourceLocation(material.getLocationName().getNamespace(), "item/gauntlet/" + locationName);
            ResourceLocation overlay_texture = new ResourceLocation(material.getLocationName().getNamespace(), "item/gauntlet/" + locationName + "_overlay");
            ItemModelBuilder mainModel;
            if (!dyeable) mainModel = prov.generated(ctx, main_texture);
            else mainModel = prov.generated(ctx, main_texture, overlay_texture);

            for (TrimModelData trimModelData : GENERATED_TRIM_MODELS) {
                ResourceLocation trim_texture = new ResourceLocation(MODID, "trims/items/gauntlet_trim_" + trimModelData.name(material));

                ItemModelBuilder trimModel = prov.getBuilder(prov.name(ctx) + "_" + trimModelData.name(material) + "_trim").parent(new ModelFile.UncheckedModelFile("item/generated"));
                trimModel.texture("layer0", main_texture);
                if (!dyeable) {
                    trimModel.texture("layer1", trim_texture);
                }
                else {
                    trimModel.texture("layer1", overlay_texture);
                    trimModel.texture("layer2", trim_texture);
                }

                mainModel.override().predicate(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, trimModelData.itemModelIndex())
                        .model(trimModel);
            }
        });
    }
    public static void generateRecipes(ItemBuilder<? extends Item, Registrate> itemBuilder, Material material, @Nullable Material craftedFrom) {
        String name = material.getName();
        itemBuilder.recipe((ctx, prov) -> {
            Ingredient craftingIngredient = null;
            InventoryChangeTrigger.TriggerInstance triggerInstance = null;
            boolean useCraftingItem = !material.getConfig().crafting.craftingItem.isEmpty();
            if (useCraftingItem) {
                craftingIngredient = Ingredient.of(ForgeRegistries.ITEMS.getValue(new ResourceLocation(material.getConfig().crafting.craftingItem)));
                triggerInstance = getTriggerInstance((ArrayList<String>) Collections.singletonList(material.getConfig().crafting.craftingItem));
            }
            else if (!material.getConfig().crafting.repairItem.isEmpty()) {
                craftingIngredient = IngredientUtil.getIngrediantFromItemString(material.getConfig().crafting.repairItem);
                triggerInstance = getTriggerInstance(material.getConfig().crafting.repairItem);
            }

            if (craftingIngredient != null) {
                ECConfigBooleanCondition enableGauntlets = new ECConfigBooleanCondition("gauntlet");
                ECMaterialBooleanCondition isSingleAddition = new ECMaterialBooleanCondition(name, "config", "crafting", "is_single_addition");

                Map<Character, Ingredient> recipe = new HashMap<>();
                recipe.put('b', craftingIngredient);
                conditionalShapedRecipe(ctx, prov, new String[]{"bb","b "}, recipe, 1, new ICondition[]{enableGauntlets, new NotCondition(isSingleAddition)}, triggerInstance, "");

                if (craftedFrom != null) {
                    if (material.getConfig().crafting.smithingTemplate != null && !Objects.equals(material.getConfig().crafting.smithingTemplate, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Items.AIR)).toString())) {
                        conditionalSmithing120Recipe(ctx, prov,
                                Ingredient.of(ForgeRegistries.ITEMS.getValue(new ResourceLocation(material.getConfig().crafting.smithingTemplate))),
                                craftingIngredient,
                                Ingredient.of(craftedFrom.getGauntletEntry().get()),
                                new ICondition[]{enableGauntlets, isSingleAddition}, triggerInstance, "");
                    } else {
                        conditionalSmithingWithoutTemplateRecipe(ctx, prov,
                                craftingIngredient,
                                Ingredient.of(craftedFrom.getGauntletEntry().get()),
                                new ICondition[]{enableGauntlets, isSingleAddition}, triggerInstance, "");
                    }
                }
            }
        });
    }

    public record TrimModelData(String name, float itemModelIndex, Map<String, String> overrideMaterials) {
        public String name(@Nullable Material p_268105_) {
            if (p_268105_ != null) {
                return this.overrideMaterials.getOrDefault(p_268105_.getName(), this.name);
            }
            return this.name;
        }
    }
}

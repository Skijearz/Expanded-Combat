package com.userofbricks.expanded_combat.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.Supplier;

public class AddItemFromGauntletModifier extends LootModifier {
    public static final Supplier<Codec<AddItemFromGauntletModifier>> CODEC = Suppliers.memoize(() ->
                    RecordCodecBuilder.create(inst -> codecStart(inst)
                            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("loot_item").forGetter(m -> m.lootItem))
                            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("gauntlet").forGetter(m -> m.gauntlet)).apply(inst, AddItemFromGauntletModifier::new))
            );

    protected final Item lootItem, gauntlet;
    public AddItemFromGauntletModifier(LootItemCondition[] conditionsIn, Item lootItem, Item gauntlet) {
        super(conditionsIn);
        this.lootItem = lootItem;
        this.gauntlet = gauntlet;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (LootItemCondition condition: conditions) {
            if (!condition.test(context)) return generatedLoot;
        }


        if (context.getParamOrNull(LootContextParams.KILLER_ENTITY) instanceof LivingEntity killer) {
            Optional<SlotResult> optionalSlotResult  = CuriosApi.getCuriosHelper().findFirstCurio(killer, gauntlet);
            if (optionalSlotResult.isPresent()) {
                generatedLoot.add(new ItemStack(lootItem));
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}

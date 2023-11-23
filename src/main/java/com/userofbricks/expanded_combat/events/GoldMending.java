package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.item.ECItemTags;
import com.userofbricks.expanded_combat.item.ISimpleMaterialItem;
import com.userofbricks.expanded_combat.init.LangStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.List;

@Mod.EventBusSubscriber(modid = "expanded_combat", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldMending
{
    @SubscribeEvent
    public void MendingBonus( PlayerXpEvent.PickupXp event) {
         Player player = event.getEntity();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() || stack.getEnchantmentLevel(Enchantments.MENDING) > 0 || stack.isDamaged() || doesGoldMendingContainItem(stack)) {
                event.setCanceled(true);
                ExperienceOrb orb = event.getOrb();
                player.takeXpDelay = 2;
                player.take(orb, 1);
                int toRepair = Math.min(orb.value * 4, stack.getDamageValue());
                orb.value -= toRepair / 4;
                stack.setDamageValue(stack.getDamageValue() - toRepair);

                if (orb.value > 0) {
                    player.giveExperiencePoints(orb.value);
                }
                orb.remove(Entity.RemovalReason.KILLED);
            }
        }
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handleToolTip( ItemTooltipEvent event) {
         ItemStack itemStack = event.getItemStack();
        List<Component> list = event.getToolTip();
        if (doesGoldMendingContainItem(itemStack)) {
            list.add(1, Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP).withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(ChatFormatting.BLUE + " +" + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(2L))));
        }
        if (itemStack.getItem() instanceof ISimpleMaterialItem simpleMaterialItem) {
            if (simpleMaterialItem.getMendingBonus() != 0.0f) {
                if (simpleMaterialItem.getMendingBonus() > 0.0f) {
                    list.add(1, Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP).withStyle(ChatFormatting.BLUE)
                            .append(Component.literal(ChatFormatting.BLUE + " +" + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(simpleMaterialItem.getMendingBonus()))));
                }
                else if (simpleMaterialItem.getMendingBonus() < 0.0f) {
                    list.add(1, Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP).withStyle(ChatFormatting.RED)
                            .append(Component.literal(ChatFormatting.RED + " " + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(simpleMaterialItem.getMendingBonus()))));
                }
            }
        }
    }
    
    public static boolean doesGoldMendingContainItem( ItemStack itemStack) {
        return doesGoldMendingContainItem(itemStack.getItem());
    }
    
    public static boolean doesGoldMendingContainItem( Item item) {
        ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
        assert tagManager != null;
        return tagManager.getTag(ECItemTags.NON_EC_MENDABLE_GOLD).contains(item);
    }
}

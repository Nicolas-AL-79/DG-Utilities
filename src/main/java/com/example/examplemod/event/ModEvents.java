package com.example.examplemod.event;

import com.example.examplemod.manager.AFKManager;
import com.example.examplemod.manager.ForbiddenItemsManager;
import com.example.examplemod.manager.PunishmentManager;
import com.example.examplemod.util.ModMessages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mod_de_teste")
public class ModEvents {

    @SubscribeEvent
    public static void onPickupItem(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItem().getItem();

        if (ForbiddenItemsManager.isForbidden(player.getUUID(), itemStack.getItem())) {
            event.getItem().discard();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            // Checa os itens proibidos no inventário
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);

                if (!stack.isEmpty() && ForbiddenItemsManager.isForbidden(player.getUUID(), stack.getItem())) {
                    player.getInventory().removeItemNoUpdate(i);
                }
            }

            // Checa se o jogador está congelado e aplica as poções que bloqueiam movimento
            if (player instanceof ServerPlayer serverPlayer && PunishmentManager.isFrozen(serverPlayer)) {
                // Lentidão absurdamente alta e cansaço (para não quebrar nada com a mão livre)
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 255, false, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2, 255, false, false, false));
                // Jump Boost alto e negativo no código base paralisa o pulo
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 2, 250, false, false, false));
            }

            // Checa se ele está em modo AFK (para ver se ele se moveu ou para ativá-lo)
            if (player instanceof ServerPlayer serverPlayer) {
                AFKManager.checkMovement(serverPlayer);
            }
        }
    }

    // ----------------------------------------------------
    // NOVOS EVENTOS PARA PUNIÇÕES E UTILIDADES
    // ----------------------------------------------------

    // Mute do Servidor
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (PunishmentManager.isMuted(player)) {
            event.setCanceled(true);
            player.sendSystemMessage(
                    ModMessages.get(
                            player,
                            "command.mod_de_teste.event.muted",
                            "You are muted and cannot speak."
                    )
            );
        }
    }

    // Congelar - Impedir de quebrar blocos (server-side)
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return; // segurança extra
        Player player = event.getPlayer();
        if (player instanceof ServerPlayer serverPlayer
                && PunishmentManager.isFrozen(serverPlayer)) {
            event.setCanceled(true);
            serverPlayer.sendSystemMessage(
                    ModMessages.get(
                            serverPlayer,
                            "command.mod_de_teste.event.frozen_break",
                            "You cannot break blocks while frozen!"
                    )
            );
        }
    }

    // Congelar - Impedir interação (seja específico com os subtipos)
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide()) return;
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer && PunishmentManager.isFrozen(serverPlayer)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer && PunishmentManager.isFrozen(serverPlayer)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer && PunishmentManager.isFrozen(serverPlayer)) {
            event.setCanceled(true);
        }
    }

    // AFK - Cancelar Dano
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (AFKManager.isAFK(player.getUUID())) {
                event.setCanceled(true); // Cancela qualquer dano se estiver AFK
            }
        }
    }

    // AFK - Cancelar Knockback (repulsão) de ataques e explosões
    @SubscribeEvent
    public static void onLivingKnockBack(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (AFKManager.isAFK(player.getUUID())) {
                event.setCanceled(true);
            }
        }
    }

    // AFK - Fazer com que os mobs ignorem o jogador
    @SubscribeEvent
    public static void onLivingChangeTarget(net.minecraftforge.event.entity.living.LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof ServerPlayer player) {
            if (AFKManager.isAFK(player.getUUID())) {
                event.setCanceled(true); // Cancela o target, fazendo o mob não atacar
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();

        if (oldData.contains("mod_de_teste", Tag.TAG_COMPOUND)) {
            newData.put(
                    "mod_de_teste",
                    oldData.getCompound("mod_de_teste").copy()
            );
        }
    }
}

package com.dgutilities.common.event;

import com.dgutilities.server.manager.AFKManager;
import com.dgutilities.admin.manager.ForbiddenItemsManager;
import com.dgutilities.admin.manager.PunishmentManager;
import com.dgutilities.common.util.ModMessages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "dg_utilities")
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            // Checa os itens proibidos no inventário
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.isEmpty()) continue;
                if (!ForbiddenItemsManager.isForbidden(player.getUUID(), stack.getItem())) continue;
                ItemStack forbiddenStack = stack.copy();

                // Remove do jogador primeiro.
                player.getInventory().removeItemNoUpdate(i);

                if (player instanceof ServerPlayer serverPlayer) {
                    returnToOpenContainer(serverPlayer, forbiddenStack);
                }
            }

            // Checa se o jogador está congelado e aplica as poções que bloqueiam movimento
            if (player instanceof ServerPlayer serverPlayer && PunishmentManager.isFrozen(serverPlayer)) {
                //Trava fisicamente o jogador na posição onde /freeze foi aplicado.
                PunishmentManager.enforceFreezePosition(serverPlayer);
                // Mantém o efeito visual de congelamento.
                player.setTicksFrozen(Math.max(player.getTicksFrozen(), player.getTicksRequiredToFreeze() - 1));
                // Mining Fatigue como camada extra para interação.
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2, 255, false, false, false));
            }

            // Checa se ele está em modo AFK (para ver se ele se moveu ou para ativá-lo)
            if (player instanceof ServerPlayer serverPlayer) {
                AFKManager.checkMovement(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        AFKManager.tickAutoAFK(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AFKManager.registerPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AFKManager.removePlayer(player);
        }
    }

    @SubscribeEvent
    public static void onTabListName(PlayerEvent.TabListNameFormat event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Component name = player.getName();
        if (AFKManager.isAFK(player.getUUID())) {
            name = Component.literal("[AFK] ").append(name);
        }
        if (PunishmentManager.isMuted(player)) {
            name = Component.literal("[MUTED] ").append(name);
        }
        if (PunishmentManager.isFrozen(player)) {
            name = Component.literal("[FROZEN] ").append(name);
        }
        event.setDisplayName(name);
    }

    private static void returnToOpenContainer(ServerPlayer player, ItemStack stack) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu == player.inventoryMenu) return;
        ItemStack remaining = stack.copy();

        for (Slot slot : menu.slots) {
            if (remaining.isEmpty()) break;
            // Não devolve para o inventário do próprio jogador.
            if (slot.container == player.getInventory()) continue;
            if (!slot.mayPlace(remaining)) continue;
            ItemStack slotStack = slot.getItem();
            // Slot vazio
            if (slotStack.isEmpty()) {
                int amount = Math.min(remaining.getCount(),
                        Math.min(remaining.getMaxStackSize(),
                                slot.getMaxStackSize(remaining)
                        )
                );
                ItemStack inserted = remaining.copy();
                inserted.setCount(amount);
                slot.set(inserted);
                remaining.shrink(amount);
                continue;
            }

            // Slot já contém o mesmo item
            if (!ItemStack.isSameItemSameTags(slotStack, remaining)) continue;

            int maxStackSize = Math.min(
                    slot.getMaxStackSize(remaining),
                    remaining.getMaxStackSize()
            );
            int space = maxStackSize - slotStack.getCount();

            if (space <= 0) continue;

            int amount = Math.min(space, remaining.getCount());

            slotStack.grow(amount);
            slot.setChanged();
            remaining.shrink(amount);
        }
        menu.broadcastChanges();
    }

    // ----------------------------------------------------
    // NOVOS EVENTOS PARA PUNIÇÕES E UTILIDADES
    // ----------------------------------------------------


    @SubscribeEvent
    public static void onPickupItem(EntityItemPickupEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer
                && PunishmentManager.isFrozen(serverPlayer)) {
            event.setCanceled(true);
            return;
        }

        ItemStack itemStack = event.getItem().getItem();

        if (ForbiddenItemsManager.isForbidden(player.getUUID(), itemStack.getItem())) {
            event.getItem().discard();
            event.setCanceled(true);
        }
    }

    // Mute do Servidor
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (PunishmentManager.isMuted(player)) {
            event.setCanceled(true);
            player.sendSystemMessage(
                    ModMessages.get(
                            player,
                            "command.dg_utilities.event.muted",
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
                            "command.dg_utilities.event.frozen_break",
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
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof ServerPlayer player) {
            if (AFKManager.isAFK(player.getUUID()) || PunishmentManager.isFrozen(player)) {
                event.setCanceled(true); // Cancela o target, fazendo o mob não atacar
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();

        if (oldData.contains("dg_utilities", Tag.TAG_COMPOUND)) {
            newData.put("dg_utilities",
                    oldData.getCompound("dg_utilities").copy()
            );
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer
                && PunishmentManager.isFrozen(serverPlayer)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getLevel().isClientSide()) return;
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer
                && PunishmentManager.isFrozen(serverPlayer)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer
                && PunishmentManager.isFrozen(serverPlayer)) {
            event.setCanceled(true);
        }
    }
}

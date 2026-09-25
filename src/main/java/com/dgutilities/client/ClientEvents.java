package com.dgutilities.client;

import com.dgutilities.Config;
import com.dgutilities.DGUtilities;
import com.dgutilities.common.network.ClientHelloPacket;
import com.dgutilities.common.network.ModNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = DGUtilities.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientEvents {
    @SubscribeEvent
    public static void registerClientCommands(
            RegisterClientCommandsEvent event
    ) {
        if (Config.COMMAND_IGNORE_ENABLED.get()) {
            IgnoreCommand.register(event.getDispatcher());
        }
    }

    @SubscribeEvent
    public static void onClientLogin(
            ClientPlayerNetworkEvent.LoggingIn event
    ) {
        ModNetwork.CHANNEL.sendToServer(
                new ClientHelloPacket()
        );
    }

    @SubscribeEvent
    public static void onClientChatReceived(
            ClientChatReceivedEvent.Player event
    ) {
        if (IgnoreManager.isIgnored(
                event.getSender()
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        IgnoredTabManager.clear();
        ignoredTabTickCounter = 0;
    }

    private static int ignoredTabTickCounter = 0;
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ignoredTabTickCounter++;
            if (ignoredTabTickCounter >= 20) {
                ignoredTabTickCounter = 0;
                IgnoredTabManager.update();
            }
        }
    }
}

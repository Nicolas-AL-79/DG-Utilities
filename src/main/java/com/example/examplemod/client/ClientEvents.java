package com.example.examplemod.client;

import com.example.examplemod.Config;
import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.ClientHelloPacket;
import com.example.examplemod.network.ModNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = ExampleMod.MODID,
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
}

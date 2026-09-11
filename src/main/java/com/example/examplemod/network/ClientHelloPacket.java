package com.example.examplemod.network;

import com.example.examplemod.manager.ClientModTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientHelloPacket {

    public static void encode(
            ClientHelloPacket packet,
            FriendlyByteBuf buffer
    ) {
    }

    public static ClientHelloPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new ClientHelloPacket();
    }

    public static void handle(
            ClientHelloPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if (player != null) {
                ClientModTracker.register(player);
            }
        });

        context.setPacketHandled(true);
    }
}
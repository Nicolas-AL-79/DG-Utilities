package com.example.examplemod.network;

import com.example.examplemod.manager.ClientModTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientHelloPacket {

    private final boolean hasMod;

    public ClientHelloPacket() {
        this.hasMod = true;
    }

    private ClientHelloPacket(boolean hasMod) {
        this.hasMod = hasMod;
    }

    public static void encode(
            ClientHelloPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeBoolean(packet.hasMod);
    }

    public static ClientHelloPacket decode(
            FriendlyByteBuf buffer
    ) {
        buffer.readBoolean();
        return new ClientHelloPacket(buffer.readBoolean());
    }

    public static void handle(
            ClientHelloPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {

        NetworkEvent.Context context =
                contextSupplier.get();

        context.enqueueWork(() -> {

            ServerPlayer player =
                    context.getSender();

            if (player != null && packet.hasMod) {
                ClientModTracker.register(player);
            }
        });

        context.setPacketHandled(true);
    }
}
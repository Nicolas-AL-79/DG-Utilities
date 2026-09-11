package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    ResourceLocation.fromNamespaceAndPath(
                            ExampleMod.MODID,
                            "main"
                    ),
                    () -> PROTOCOL_VERSION,
                    version -> true,
                    version -> true
            );

    private static int packetId = 0;

    public static void register() {

        CHANNEL.registerMessage(
                packetId++,
                ClientHelloPacket.class,
                ClientHelloPacket::encode,
                ClientHelloPacket::decode,
                ClientHelloPacket::handle
        );
    }
}
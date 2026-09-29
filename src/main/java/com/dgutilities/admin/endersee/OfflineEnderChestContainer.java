package com.dgutilities.admin.endersee;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

import java.io.File;
import java.io.IOException;

public class OfflineEnderChestContainer extends PlayerEnderChestContainer {
    private final CompoundTag playerData;
    private final File playerDataFile;
    public OfflineEnderChestContainer(CompoundTag playerData, File playerDataFile) {
        this.playerData = playerData;
        this.playerDataFile = playerDataFile;
        fromTag(playerData.getList("EnderItems", Tag.TAG_COMPOUND));
    }
    @Override
    public void stopOpen(Player player) {
        super.stopOpen(player);
        playerData.put("EnderItems", createTag());
        try {
            NbtIo.writeCompressed(playerData, playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

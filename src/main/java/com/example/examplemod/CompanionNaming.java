package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class CompanionNaming {
    private final EntityCompanion companion;
    private String displayName = "Компаньон";

    public CompanionNaming(EntityCompanion companion) {
        this.companion = companion;
        companion.setCustomNameVisible(true);
    }

    public void setDisplayName(String name) {
        this.displayName = name;
        companion.setCustomName(Component.literal(name));
        companion.setCustomNameVisible(true);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void saveNBT(CompoundTag tag) {
        tag.putString("CompName", displayName);
    }
    public void loadNBT(CompoundTag tag) {
        if (tag.contains("CompName")) {
            setDisplayName(tag.getString("CompName"));
        }
    }
}

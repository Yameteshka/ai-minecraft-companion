package com.example.examplemod;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.NbtIo;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.UUID;
import java.util.Random;

public class CompanionInventory {
    private final EntityCompanion companion;
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(36, ItemStack.EMPTY);

    public CompanionInventory(EntityCompanion companion) {
        this.companion = companion;
    }

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    /** Подбор предметов, отдаём owner-у сообщение */
    public void handleItemPickup() {
        UUID uuid = companion.getOwnerUUID();
        if (uuid == null) return;
        Player owner = companion.level().getPlayerByUUID(uuid);
        if (owner == null) return;

        double r = 2.5D;
        List<ItemEntity> items = companion.level()
                .getEntitiesOfClass(ItemEntity.class, companion.getBoundingBox().inflate(r));
        for (ItemEntity it : items) {
            if (!it.isAlive() || it.hasPickUpDelay()) continue;
            ItemStack stack = it.getItem();
            boolean success = false, full = true;
            for (int i=0;i<inventory.size();i++){
                ItemStack slot = inventory.get(i);
                if (slot.isEmpty()){
                    inventory.set(i, stack.copy());
                    success = true; full = false; break;
                } else if (ItemStack.isSameItemSameTags(slot, stack)
                        && slot.getCount()+stack.getCount()<=slot.getMaxStackSize()) {
                    slot.grow(stack.getCount());
                    success = true; full = false; break;
                }
            }
            String name = companion.namingLogic.getDisplayName();
            if (success) {
                companion.level().broadcastEntityEvent(companion,(byte)7);
                it.discard();
                ExampleMod.sendTemporaryMessage(owner,name+" подобрал "+stack.getCount()
                        +" "+stack.getHoverName().getString());
            } else if (full) {
                ExampleMod.sendTemporaryMessage(owner,"У "+name+" полный инвентарь");
            }
        }
    }

    /** Выпадение всех вещей при смерти */
    public void dropAllItemsOnDeath() {
        if (companion.level().isClientSide) return;

        Random rand = new Random();
        int dropped = 0;

        for (ItemStack stack : inventory) {
            if (!stack.isEmpty() && stack.getCount() > 0) {
                ItemEntity item = new ItemEntity(
                        companion.level(),
                        companion.getX(),
                        companion.getY() + 0.5, // 💥 немного выше, чтобы не провалился
                        companion.getZ(),
                        stack.copy()
                );

                item.setPickUpDelay(20); // 🕒 1 секунда задержки перед подбором

                // 💨 немного случайного движения
                item.setDeltaMovement(
                        (rand.nextDouble() - 0.5) * 0.1,
                        0.2,
                        (rand.nextDouble() - 0.5) * 0.1
                );

                companion.level().addFreshEntity(item);
                dropped++;
            }
        }

        inventory.replaceAll(s -> ItemStack.EMPTY); // 🔄 очищаем после дропа

        Player owner = companion.level().getPlayerByUUID(companion.getOwnerUUID());
        if (owner != null)
            ExampleMod.sendTemporaryMessage(owner, "[DEBUG] Компаньон дропнул предметов: " + dropped);
    }

    public void saveNBT(CompoundTag tag) {
        for (int i=0;i<inventory.size();i++){
            CompoundTag t = new CompoundTag();
            inventory.get(i).save(t);
            tag.put("Item"+i,t);
        }
    }
    public void loadNBT(CompoundTag tag) {
        for (int i=0;i<inventory.size();i++){
            if (tag.contains("Item"+i)){
                inventory.set(i, ItemStack.of(tag.getCompound("Item"+i)));
            }
        }
    }

    public void saveToDisk(UUID playerId) {
        CompoundTag tag = new CompoundTag();
        saveNBT(tag);
        File file = new File("companion_data/inv_" + playerId + ".dat");
        try {
            file.getParentFile().mkdirs();
            NbtIo.writeCompressed(tag, file);
        } catch (IOException e) {
            System.err.println("[Companion] Не удалось сохранить инвентарь: " + e.getMessage());
        }
    }

    public void loadFromDisk(UUID playerId) {
        File file = new File("companion_data/inv_" + playerId + ".dat");
        if (!file.exists()) return;

        try {
            CompoundTag tag = NbtIo.readCompressed(file);
            loadNBT(tag);

            // 💥 Удаляем после загрузки, чтобы избежать дюпа
            if (!file.delete()) {
                System.err.println("[Companion] Не удалось удалить сохранённый файл после загрузки.");
            }

        } catch (IOException e) {
            System.err.println("[Companion] Не удалось загрузить инвентарь: " + e.getMessage());
        }
    }
}

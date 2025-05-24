package com.example.examplemod;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class CompanionCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(
                LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal("companion")
                        .requires(cs -> cs.hasPermission(2)) // доступ операторам
                        .then(Commands.literal("reset_name")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    UUID pid = player.getUUID();
                                    ExampleMod.PLAYER_NAMES.remove(pid);
                                    ExampleMod.WAIT_NAME.put(pid, true);
                                    ExampleMod.sendTemporaryMessage(player, "Имя компаньона сброшено. Введите новое.");
                                    return 1;
                                }))
                        .then(Commands.literal("clear_inventory")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    UUID pid = player.getUUID();

                                    if (player.level() instanceof ServerLevel sl) {
                                        for (Entity e : sl.getAllEntities()) {
                                            if (e instanceof EntityCompanion c && pid.equals(c.getOwnerUUID())) {
                                                c.inventoryLogic.getInventory().replaceAll(s -> net.minecraft.world.item.ItemStack.EMPTY);
                                                ExampleMod.sendTemporaryMessage(player, "Инвентарь компаньона очищен.");
                                                return 1;
                                            }
                                        }
                                    }

                                    ExampleMod.sendTemporaryMessage(player, "Компаньон не найден.");
                                    return 0;
                                }))
                        .then(Commands.literal("die")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    UUID pid = player.getUUID();

                                    if (player.level() instanceof ServerLevel sl) {
                                        for (Entity e : sl.getAllEntities()) {
                                            if (e instanceof EntityCompanion c && pid.equals(c.getOwnerUUID())) {
                                                c.kill();
                                                ExampleMod.sendTemporaryMessage(player, "Компаньон погиб.");
                                                return 1;
                                            }
                                        }
                                    }

                                    ExampleMod.sendTemporaryMessage(player, "Компаньон не найден.");
                                    return 0;
                                }))
                        .then(Commands.literal("summon")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    UUID pid = player.getUUID();

                                    if (player.level() instanceof ServerLevel sl) {
                                        EntityCompanion existing = null;

                                        for (Entity e : sl.getAllEntities()) {
                                            if (e instanceof EntityCompanion c && pid.equals(c.getOwnerUUID())) {
                                                existing = c;
                                                break;
                                            }
                                        }

                                        if (existing != null) {
                                            existing.inventoryLogic.saveToDisk(pid);
                                            existing.discard();
                                        }

                                        EntityCompanion comp = ExampleMod.COMPANION.get().create(sl);
                                        if (comp != null) {
                                            comp.setOwnerUUID(pid);
                                            String name = ExampleMod.PLAYER_NAMES.getOrDefault(pid, "Компаньон");
                                            comp.namingLogic.setDisplayName(name);
                                            var pos = EntityCompanion.findSafeSpawn(sl, player.getX(), player.getY(), player.getZ());
                                            comp.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                                            sl.addFreshEntity(comp);
                                            comp.inventoryLogic.loadFromDisk(pid);
                                            ExampleMod.sendTemporaryMessage(player, "Компаньон призван.");
                                            return 1;
                                        }
                                    }

                                    ExampleMod.sendTemporaryMessage(player, "Не удалось призвать компаньона.");
                                    return 0;
                                }))
        );
    }
}

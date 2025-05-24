package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.UUID;

@Mod("examplemod")
public class ExampleMod {
    public static final String MODID = "examplemod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<EntityCompanion>> COMPANION =
            ENTITY_TYPES.register("companion",
                    () -> EntityType.Builder.<EntityCompanion>of(EntityCompanion::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F).build("companion"));

    public static final HashMap<UUID, String> PLAYER_NAMES = new HashMap<>();
    public static final HashMap<UUID, Boolean> WAIT_NAME = new HashMap<>();
    private final HashMap<UUID, Integer> pendingRespawns = new HashMap<>();

    public ExampleMod() {
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus()
                .addListener(this::onAttributeCreate);
    }

    private void onAttributeCreate(EntityAttributeCreationEvent ev) {
        ev.put(COMPANION.get(), EntityCompanion.createAttributes().build());
    }

    public static void sendTemporaryMessage(Player p, String txt) {
        if (p instanceof ServerPlayer sp) {
            sp.displayClientMessage(Component.literal(txt), true);
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent ev) {
        Player p = ev.getEntity();
        Level w = p.level();
        if (w.isClientSide) return;

        UUID pid = p.getUUID();

        if (!PLAYER_NAMES.containsKey(pid)) {
            sendTemporaryMessage(p, "ИИ подключился, назови его.");
            WAIT_NAME.put(pid, true);
        } else {
            sendTemporaryMessage(p, PLAYER_NAMES.get(pid) + " подключился к игре ;)");
        }

        // удаляем всех старых
        w.getEntitiesOfClass(EntityCompanion.class, p.getBoundingBox().inflate(128))
                .forEach(e -> e.discard());

        // создаём нового
        EntityCompanion comp = COMPANION.get().create(w);
        if (comp != null) {
            comp.setOwnerUUID(pid);
            String name = PLAYER_NAMES.getOrDefault(pid, "Компаньон");
            comp.namingLogic.setDisplayName(name);
            BlockPos pos = EntityCompanion.findSafeSpawn(w, p.getX(), p.getY(), p.getZ());
            comp.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            ((ServerLevel) w).addFreshEntity(comp);
            comp.inventoryLogic.loadFromDisk(pid); // загрузка инвентаря
        }
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent ev) {
        Player p = ev.getPlayer();
        UUID pid = p.getUUID();
        if (WAIT_NAME.getOrDefault(pid, false)) {
            String name = ev.getRawText();
            PLAYER_NAMES.put(pid, name);
            WAIT_NAME.put(pid, false);
            sendTemporaryMessage(p, "Теперь твоего ИИ помощника зовут " + name);
            ev.setCanceled(true);

            // Обновим имя у нового бота
            if (p.level() instanceof ServerLevel sl) {
                for (EntityCompanion c : sl.getEntitiesOfClass(EntityCompanion.class, p.getBoundingBox().inflate(128))) {
                    if (c.getOwnerUUID() != null && c.getOwnerUUID().equals(pid)) {
                        c.namingLogic.setDisplayName(name);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onCompanionDeath(LivingDeathEvent ev) {
        if (!(ev.getEntity() instanceof EntityCompanion ex)) return;
        if (ex.level().isClientSide) return;

        UUID playerId = ex.getOwnerUUID();
        if (playerId == null) return;

        ex.inventoryLogic.dropAllItemsOnDeath();

        Player owner = ex.level().getPlayerByUUID(playerId);
        String name = PLAYER_NAMES.getOrDefault(playerId, "Компаньон");
        if (owner != null) {
            sendTemporaryMessage(owner, name + " умер :(");
        }

        pendingRespawns.put(playerId, 40); // 2 секунды
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END) return;

        var copy = new HashMap<>(pendingRespawns);
        for (var entry : copy.entrySet()) {
            UUID playerId = entry.getKey();
            int time = entry.getValue();
            if (time > 1) {
                pendingRespawns.put(playerId, time - 1);
            } else {
                pendingRespawns.remove(playerId);
                respawnCompanion(playerId);
            }
        }
    }

    private void respawnCompanion(UUID playerId) {
        ServerPlayer owner = null;
        for (ServerPlayer p : net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (p.getUUID().equals(playerId)) {
                owner = p;
                break;
            }
        }

        if (owner == null) return;

        ServerLevel w = owner.serverLevel();
        EntityCompanion nc = ExampleMod.COMPANION.get().create(w);
        if (nc != null) {
            nc.setOwnerUUID(playerId);
            String name = PLAYER_NAMES.getOrDefault(playerId, "Компаньон");
            nc.namingLogic.setDisplayName(name);
            BlockPos pos = EntityCompanion.findSafeSpawn(w, owner.getX(), owner.getY(), owner.getZ());
            nc.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            w.addFreshEntity(nc);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent ev) {
        Player p = ev.getEntity();
        UUID pid = p.getUUID();

        if (p.level() instanceof ServerLevel sl) {
            for (EntityCompanion c : sl.getEntitiesOfClass(EntityCompanion.class, p.getBoundingBox().inflate(128))) {
                if (c.getOwnerUUID() != null && c.getOwnerUUID().equals(pid)) {
                    c.inventoryLogic.saveToDisk(pid);
                    break;
                }
            }
        }
    }
}

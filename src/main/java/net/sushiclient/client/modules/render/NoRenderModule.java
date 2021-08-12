package net.sushiclient.client.modules.render;

import io.netty.buffer.Unpooled;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.MobEffects;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.LightUpdateEvent;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.render.*;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;

import java.io.IOException;

public class NoRenderModule extends BaseModule {

    private final Configuration<Boolean> skyLight;
    private final Configuration<Boolean> blockLight;
    private final Configuration<Boolean> mapIcons;

    private final Configuration<Boolean> hurtCam;
    private final Configuration<Boolean> totem;
    private final Configuration<Boolean> portal;
    private final Configuration<Boolean> tutorial;
    private final Configuration<Boolean> potionIcons;
    private final Configuration<Boolean> vignette;
    private final Configuration<Boolean> blindness;
    private final Configuration<Boolean> nausea;
    private final Configuration<Boolean> pumpkin;
    private final Configuration<Boolean> block;
    private final Configuration<Boolean> fire;
    private final Configuration<Boolean> water;

    private final Configuration<Boolean> explosion;
    private final Configuration<Boolean> firework;

    public NoRenderModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        ConfigurationCategory chunkBan = provider.getCategory("chunk_ban", "Chunk BAN", null);
        skyLight = chunkBan.get("sky_light", "Sky Light", null, Boolean.class, false);
        blockLight = chunkBan.get("block_light", "Block Light", null, Boolean.class, false);
        mapIcons = chunkBan.get("map_icons", "Map Icons", null, Boolean.class, false);

        ConfigurationCategory overlay = provider.getCategory("overlay", "Overlay", null);
        hurtCam = overlay.get("hurt_cam", "Hurt Cam", null, Boolean.class, false);
        totem = overlay.get("totem", "Totem", null, Boolean.class, false);
        portal = overlay.get("portal", "Portal", null, Boolean.class, false);
        tutorial = overlay.get("tutorial", "Tutorial", null, Boolean.class, false);
        potionIcons = overlay.get("potion_icons", "Potion Icons", null, Boolean.class, false);
        vignette = overlay.get("vignette", "Vignette", null, Boolean.class, false);
        blindness = overlay.get("blindness", "Blindness", null, Boolean.class, false);
        nausea = overlay.get("nausea", "Nausea", null, Boolean.class, false);
        pumpkin = overlay.get("pumpkin", "Pumpkin", null, Boolean.class, false);
        block = overlay.get("block", "Block", null, Boolean.class, false);
        fire = overlay.get("fire", "Fire", null, Boolean.class, false);
        water = overlay.get("water", "Water", null, Boolean.class, false);

        ConfigurationCategory world = provider.getCategory("world", "World", null);
        explosion = world.get("explosion", "Explosion", null, Boolean.class, false);
        firework = world.get("firework", "Firework", null, Boolean.class, false);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "NoRender";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (blindness.getValue()) getPlayer().removeActivePotionEffect(MobEffects.BLINDNESS);
        if (nausea.getValue()) getPlayer().removeActivePotionEffect(MobEffects.NAUSEA);
        if (tutorial.getValue()) getClient().gameSettings.tutorialStep = TutorialSteps.NONE;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onGameOverlayRender(GameOverlayRenderEvent e) {
        if (potionIcons.getValue() && e.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS ||
                portal.getValue() && e.getType() == RenderGameOverlayEvent.ElementType.PORTAL ||
                vignette.getValue() && e.getType() == RenderGameOverlayEvent.ElementType.VIGNETTE ||
                pumpkin.getValue() && e.getType() == RenderGameOverlayEvent.ElementType.HELMET) {

            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onItemActivationRender(ItemActivationRenderEvent e) {
        if (totem.getValue()) e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onHurtCameraEffect(HurtCameraEffectEvent e) {
        if (hurtCam.getValue()) e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onBlockOverlayRender(BlockOverlayRenderEvent e) {
        if (block.getValue() && e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK ||
                fire.getValue() && e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE ||
                water.getValue() && e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) {
            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onEntityRender(EntityRenderEvent e) {
        if (firework.getValue() && e.getEntityIn() instanceof EntityFireworkRocket) {
            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onCheckLight(LightUpdateEvent e) {
        if ((skyLight.getValue() && e.getEnumSkyBlock() == EnumSkyBlock.SKY) ||
                (blockLight.getValue() && e.getEnumSkyBlock() == EnumSkyBlock.BLOCK))
            e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) throws IOException {
        if (e.getPacket() instanceof SPacketMaps) {
            if (!mapIcons.getValue()) return;
            SPacketMaps packet = (SPacketMaps) e.getPacket();
            PacketBuffer read = new PacketBuffer(Unpooled.buffer());
            PacketBuffer write = new PacketBuffer(Unpooled.buffer());
            packet.writePacketData(read);
            // map-id, mapScale, trackingPosition, iconsLength, icons, columns
            int mapId = read.readVarInt();
            write.writeVarInt(mapId);
            if (mapId < 0) return;
            write.writeByte(read.readByte());
            write.writeBoolean(read.readBoolean());
            int iconsLength = read.readVarInt();
            write.writeVarInt(0);
            read.skipBytes(iconsLength * 3);
            byte columns = read.readByte();
            write.writeByte(columns);
            if (columns > 0) {
                // rows, minX, minZ, length
                write.writeByte(read.readByte());
                write.writeByte(read.readByte());
                write.writeByte(read.readByte());
                write.writeByteArray(read.readByteArray());
            }
            packet.readPacketData(write);
        } else if (e.getPacket() instanceof SPacketExplosion) {
            if (explosion.getValue()) e.setCancelled(true);
        }
    }
}
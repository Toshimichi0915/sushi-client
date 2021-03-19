package net.toshimichi.sushi.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.modules.BaseModule;
import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.Modules;
import net.toshimichi.sushi.modules.config.Configurations;

import java.lang.reflect.Field;
import java.util.Set;

public class NoRotateModule extends BaseModule {

    private static final Field YAW_FIELD;
    private static final Field PITCH_FIELD;
    private static final Field FLAGS_FIELD;

    static {
        YAW_FIELD = ObfuscationReflectionHelper.findField(SPacketPlayerPosLook.class, "yaw");
        PITCH_FIELD = ObfuscationReflectionHelper.findField(SPacketPlayerPosLook.class, "pitch");
        FLAGS_FIELD = ObfuscationReflectionHelper.findField(SPacketPlayerPosLook.class, "flags");
        YAW_FIELD.setAccessible(true);
        PITCH_FIELD.setAccessible(true);
        FLAGS_FIELD.setAccessible(true);
    }

    public NoRotateModule(String id, String name, Modules modules, Categories categories, Configurations provider) {
        super(id, name, modules, categories, provider);
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketPlayerPosLook)) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook) e.getPacket();
        try {
            YAW_FIELD.set(packet, minecraft.player.cameraYaw);
            PITCH_FIELD.set(packet, minecraft.player.cameraPitch);
            Set<SPacketPlayerPosLook.EnumFlags> flags = (Set<SPacketPlayerPosLook.EnumFlags>) FLAGS_FIELD.get(packet);
            flags.remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
            flags.remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    @Override
    public int getDefaultKeybind() {
        return 0;
    }
}

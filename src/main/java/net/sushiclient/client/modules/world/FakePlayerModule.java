package net.sushiclient.client.modules.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.combat.DamageUtils;
import org.apache.commons.lang3.RandomUtils;

import java.text.DecimalFormat;
import java.util.UUID;

public class FakePlayerModule extends BaseModule {

    @Config(id = "gapple_effects", name = "Gapple Effects")
    public Boolean gappleEffects = true;

    @Config(id = "show_damage", name = "Show Damage")
    public Boolean showDamage = true;

    private EntityOtherPlayerMP fakePlayer;
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");

    public FakePlayerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    private void addPotionEffect(Potion potion, int level) {
        PotionEffect effect = new PotionEffect(potion, Integer.MAX_VALUE, level);
        fakePlayer.addPotionEffect(effect);
        effect.getPotion().applyAttributesModifiersToEntity(fakePlayer, fakePlayer.getAttributeMap(), effect.getAmplifier());
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (fakePlayer != null) return;
        if (getPlayer().ticksExisted < 5) return;
        fakePlayer = new EntityOtherPlayerMP(getWorld(), new GameProfile(UUID.randomUUID(), "Hiyokomame0144"));
        fakePlayer.copyLocationAndAnglesFrom(getPlayer());
        fakePlayer.rotationYawHead = getPlayer().rotationYawHead;
        fakePlayer.inventory.copyInventory(getPlayer().inventory);
        if (gappleEffects) {
            addPotionEffect(MobEffects.REGENERATION, 1);
            addPotionEffect(MobEffects.ABSORPTION, 3);
            addPotionEffect(MobEffects.RESISTANCE, 0);
            addPotionEffect(MobEffects.FIRE_RESISTANCE, 0);
        }

        int entityId = RandomUtils.nextInt(0, Integer.MAX_VALUE) + Integer.MIN_VALUE;
        getWorld().addEntityToWorld(entityId, fakePlayer);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onAttack(PacketSendEvent e) {
        if (!showDamage) return;
        if (fakePlayer == null) return;
        if (!(e.getPacket() instanceof SPacketDestroyEntities)) return;
        SPacketDestroyEntities packet = (SPacketDestroyEntities) e.getPacket();
        for (int entityId : packet.getEntityIDs()) {
            Entity entity = getWorld().getEntityByID(entityId);
            if (entity == null) continue;
            double raw, damage;
            if (entity instanceof EntityEnderCrystal) {
                raw = DamageUtils.getCrystalDamage(fakePlayer, entity.getPositionVector());
                damage = DamageUtils.applyModifier(fakePlayer, raw, DamageUtils.EXPLOSION);
            } else {
                continue;
            }
            Sushi.getProfile().getLogger().send(LogLevel.INFO, "Damage: " + FORMATTER.format(damage) +
                    "(" + FORMATTER.format(raw) + ")");
        }
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        fakePlayer.setDead();
        getWorld().removeEntity(fakePlayer);
        fakePlayer = null;
    }

    @Override
    public String getDefaultName() {
        return "FakePlayer";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}

package net.toshimichi.sushi.modules.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.modules.*;
import org.apache.commons.lang3.RandomUtils;

import java.util.UUID;

public class FakePlayerModule extends BaseModule {

    @Config(id = "gapple_effects", name = "Gapple Effects")
    public Boolean gappleEffects = true;
    private EntityOtherPlayerMP fakePlayer;

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

    @Override
    public void onDisable() {
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

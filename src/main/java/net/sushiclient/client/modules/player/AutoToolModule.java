package net.sushiclient.client.modules.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.BlockLeftClickEvent;
import net.sushiclient.client.events.player.PlayerAttackEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.ItemSlotSwitchTask;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import org.lwjgl.input.Mouse;

public class AutoToolModule extends BaseModule {

    @Config(id = "tool", name = "Tool")
    public Boolean tool = true;

    @Config(id = "prefer_silk_touch", name = "Prefer Silk Touch")
    public Boolean preferSilkTouch = false;

    @Config(id = "prefer_silk_touch_for_ender_chest", name = "Enderchest", when = "prefer_silk_touch")
    public Boolean preferSilkTouchForEnderChest = false;

    @Config(id = "weapon", name = "Weapon")
    public Boolean weapon = false;

    @Config(id = "prefer_axe", name = "Prefer Axe", when = "weapon")
    public Boolean preferAxe = false;

    @Config(id = "switchBack", name = "Switch back")
    public Boolean switchBack = false;

    @Config(id = "from_inventory", name = "From Inventory")
    public Boolean fromInventory = true;

    private int hotbarItemSlot;
    private ItemStack moved;
    private boolean running;

    public AutoToolModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onBlockLeftClick(BlockLeftClickEvent e) {
        if (running) return;
        if (!tool) return;
        IBlockState blockState = getWorld().getBlockState(e.getPos());
        boolean silkTouch = preferSilkTouch;
        if (preferSilkTouch && blockState.getBlock() == Blocks.ENDER_CHEST) silkTouch = preferSilkTouchForEnderChest;
        ItemSlot item = InventoryUtils.findBestTool(fromInventory, silkTouch, getWorld().getBlockState(e.getPos()));
        switchItem(item, switchBack);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerAttack(PlayerAttackEvent e) {
        if (running) return;
        if (!weapon) return;
        if (!(e.getTarget() instanceof EntityLivingBase)) return;
        ItemSlot item = InventoryUtils.findBestWeapon(fromInventory, preferAxe);
        switchItem(item, switchBack);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (running) return;
        if (moved == null || moved.getItem() == Items.AIR) return;
        if (Mouse.isButtonDown(0)) return;
        int currentItem = getPlayer().inventory.currentItem;
        if (currentItem != hotbarItemSlot && new ItemSlot(hotbarItemSlot).getItemStack().equals(moved)) {
            InventoryUtils.moveHotbar(hotbarItemSlot);
        } else {
            for (int i = 0; i < 36; i++) {
                ItemSlot itemSlot = new ItemSlot(i);
                if (!ItemStack.areItemStacksEqual(itemSlot.getItemStack(), moved)) continue;
                switchItem(itemSlot, false);
            }
        }
        moved = null;
    }

    private void switchItem(ItemSlot itemSlot, boolean switchBack) {
        if (getPlayer().inventory.currentItem == itemSlot.getIndex()) return;
        if (switchBack) {
            hotbarItemSlot = getPlayer().inventory.currentItem;
            moved = new ItemSlot(hotbarItemSlot, getPlayer()).getItemStack();
        }
        running = true;
        TaskExecutor.newTaskChain()
                .supply(itemSlot)
                .then(new ItemSlotSwitchTask())
                .then(() -> running = false)
                .execute();
    }

    @Override
    public String getDefaultName() {
        return "AutoTool";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}

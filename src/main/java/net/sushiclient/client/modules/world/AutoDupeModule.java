package net.sushiclient.client.modules.world;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.BlockLeftClickEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.world.BlockFace;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

import static net.sushiclient.client.modules.world.AutoDupeModule.AutoDupeState.*;

public class AutoDupeModule extends BaseModule {

    private BlockPos chest;
    private BlockPos button;
    private int entityId;
    private boolean ready;
    private AutoDupeState state;

    public AutoDupeModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        chest = null;
        button = null;
        entityId = -1;
        ready = false;
        state = AutoDupeState.RIDING;
        EventHandlers.register(this);
        tutorial();
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private Container getHorseContainer() {
        if (!(getClient().currentScreen instanceof GuiScreenHorseInventory)) {
            Entity ridingEntity = getPlayer().getRidingEntity();
            if (ridingEntity != null && ridingEntity.getEntityId() == entityId) {
                ((AbstractHorse) ridingEntity).openGUI(getPlayer());
            } else {
                ready = false;
                tutorial();
                return null;
            }
        }
        GuiScreenHorseInventory screen = (GuiScreenHorseInventory) getClient().currentScreen;
        if (screen == null) {
            ready = false;
            tutorial();
            return null;
        }
        return screen.inventorySlots;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (!ready) return;
        if (state == PUSHING_BUTTON) {
            PositionUtils.lookAt(BlockUtils.toVec3d(button).add(0.5, 0.5, 0.5), DesyncMode.LOOK);
            IBlockState buttonState = getWorld().getBlockState(button);
            BlockPlaceInfo place = new BlockFace(button, buttonState.getValue(BlockDirectional.FACING)).toBlockPlaceInfo(getWorld());
            BlockUtils.place(place, true);
            state = WAITING_UNLOAD;
        } else if (state == WAITING_UNLOAD) {
            Entity entity = getWorld().getEntityByID(entityId);
            if (entity != null && getWorld().getChunk(entity.getPosition()).isLoaded()) {
                getConnection().sendPacket(new CPacketUseEntity(entity, EnumHand.MAIN_HAND));
                state = RIDING;
            }
        } else if (state == RIDING) {
            boolean empty = true;
            Container container = getHorseContainer();
            if (container == null) return;
            for (ItemStack itemStack : container.inventoryItemStacks) {
                if (!(itemStack.getItem() instanceof ItemAir)) {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                state = PUSHING_BUTTON;
            } else {
                state = OBTAINING;
            }
        } else if (state == OBTAINING) {
            Container container = getHorseContainer();
            if (container == null) return;
            boolean filling = false;
//            for(ItemStack itemStack : container.inventorySlots) {
//
//            }
            state = PUSHING_BUTTON;
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onAttack(BlockLeftClickEvent e) {
        if (ready) return;
        setup(e.getPos());
        e.setCancelled(true);
        if (tutorial()) {
            Entity ridingEntity = getPlayer().getRidingEntity();
            if (ridingEntity instanceof EntityLlama || ridingEntity instanceof EntityDonkey) {
                entityId = ridingEntity.getEntityId();
                ready = true;
            } else {
                Sushi.getProfile().getLogger().send(LogLevel.INFO, "ロバかラマに乗ってください");
            }
        }
    }

    public boolean tutorial() {
        Logger logger = Sushi.getProfile().getLogger();
        if (chest == null) {
            logger.send(LogLevel.INFO, "チェストを左クリックしてください");
            logger.send(LogLevel.INFO, "Please left-click the chest");
            return false;
        }
        if (button == null) {
            logger.send(LogLevel.INFO, "ボタンを左クリックしてください");
            logger.send(LogLevel.INFO, "Please left-click the button");
            return false;
        }
        logger.send(LogLevel.INFO, "準備が完了しました．アイテム増殖を開始します");
        logger.send(LogLevel.INFO, "Everything is now ready. Starting dupe...");
        return true;
    }

    public void setup(BlockPos pos) {
        if (chest == null) {
            chest = pos;
        } else if (button == null) {
            button = pos;
        }
    }

    @Override
    public String getDefaultName() {
        return "AutoDupe";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }

    public enum AutoDupeState {
        PUSHING_BUTTON, WAITING_UNLOAD, RIDING, OBTAINING, FILLING
    }
}

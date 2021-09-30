package net.sushiclient.client.modules.world;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.player.PlayerPacketEvent;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.modules.movement.StepMode;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockPlaceUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.List;

public class ScaffoldModule extends BaseModule {

    @Config(id = "rotate", name = "Rotate")
    public ScaffoldMode mode = ScaffoldMode.NCP;

    @Config(id = "switch", name = "Switch")
    public Boolean autoSwitch = true;

    @Config(id = "refill", name = "Refill")
    public Boolean refill = true;

    @Config(id = "tower", name = "Tower")
    public Boolean tower = true;

    @Config(id = "sprint_spoof", name = "Spring Spoof")
    public Boolean sprintSpoof = true;

    @Config(id = "smooth", name = "Smooth")
    public Boolean smooth = true;

    @Config(id = "delay", name = "Delay")
    public IntRange delay = new IntRange(1, 20, 1, 1);

    @Config(id = "tower_delay", name = "Tower Delay", when = "tower")
    public IntRange towerDelay = new IntRange(1, 20, 1, 1);

    @Config(id = "threshold", name = "Threshold")
    public IntRange threshold = new IntRange(32, 64, 1, 1);

    private int timeout;
    private boolean hasBlock;
    private List<BlockPlaceInfo> tasks;
    private DesyncOperator operator;
    private boolean buildingUp;
    private int sleep;
    private int sprintSleep;
    private int towerSleep;

    public ScaffoldModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        if (sprintSpoof) {
            sendPacket(new CPacketEntityAction(getPlayer(), CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        PositionUtils.close(operator);
        operator = null;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (!sprintSpoof) return;
        if (!(e.getPacket() instanceof CPacketEntityAction)) return;
        CPacketEntityAction packet = (CPacketEntityAction) e.getPacket();
        if (packet.getAction() != CPacketEntityAction.Action.START_SPRINTING) return;
        e.setCancelled(true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerTravel(PlayerTravelEvent e) {
        BlockPos floor = BlockUtils.toBlockPos(getPlayer().getPositionVector()).add(0, -1, 0);
        if (BlockUtils.isAir(getWorld(), floor)) return;

        Vec3d input = MovementUtils.getMoveInputs(getPlayer());
        if (hasBlock && tower && input.x == 0 && input.y == 1 && input.z == 0 && EntityUtils.isOnGround(getPlayer())) {
            buildingUp = true;
            e.setCancelled(true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerPacket1(PlayerPacketEvent e) {
        sleep--;
        sprintSleep--;
        towerSleep--;
        timeout--;

        if (buildingUp) {
            buildingUp = false;
            if (towerSleep > 0) return;
            StepMode.NCP.step(0, 1, 0, false);
            getPlayer().motionY = 0;
            towerSleep = towerDelay.getCurrent();
        }
    }

    @EventHandler(timing = EventTiming.PRE, priority = 1)
    public void onPlayerPacket2(PlayerPacketEvent e) {
        Vec3d floor = getPlayer().getPositionVector().add(0, -1, 0);
        BlockPos floorPos = BlockUtils.toBlockPos(floor);
        tasks = BlockPlaceUtils.search(getWorld(), floorPos, 3);
        if (tasks == null || tasks.isEmpty()) return;
        hasBlock = false;
        Item current = ItemSlot.current().getItemStack().getItem();
        if (current instanceof ItemBlock && current != Item.getItemFromBlock(Blocks.ENDER_CHEST)) {
            hasBlock = true;
            ItemSlot slot = InventoryType.MAIN.findStackable(ItemSlot.current().getItemStack());
            if (refill && ItemSlot.current().getItemStack().getCount() < threshold.getCurrent() && slot != null) {
                InventoryUtils.moveTo(slot, ItemSlot.current());
            }
        } else if (autoSwitch) {
            for (ItemSlot itemSlot : InventoryType.HOTBAR) {
                if (itemSlot.getItemStack().getItem() instanceof ItemBlock) {
                    InventoryUtils.moveHotbar(itemSlot.getIndex());
                }
            }
            tasks = null;
        }
    }

    @EventHandler(timing = EventTiming.PRE, priority = 2)
    public void onPlayerPacket3(PlayerPacketEvent e) {
        if (sleep > 0) return;
        if (!hasBlock || tasks == null || tasks.isEmpty() ||
                getPlayer().isInWater() || getPlayer().isInLava()) {
            if (timeout <= 0) {
                PositionUtils.close(operator);
                operator = null;
            }
            return;
        }
        timeout = 5;
        if (operator == null) {
            operator = PositionUtils.desync();
        }
        sleep = delay.getCurrent();
        BlockPlaceInfo info = tasks.get(0);
        tasks.remove(info);
        ScaffoldMode.NCP.rotate(info, operator);
        PositionUtils.on(() -> {
            BlockUtils.place(info, false);
            sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        });
    }

    @EventHandler(timing = EventTiming.PRE, priority = 100)
    public void onPlayerMove(ClientTickEvent e) {
        if (getPlayer().movementInput.jump) sprintSleep = 5;
        if (!smooth) return;
        if (timeout <= 0) return;
        if (sprintSleep < 0) return;
        getPlayer().setSprinting(false);
    }

    @Override
    public String getDefaultName() {
        return "Scaffold";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}

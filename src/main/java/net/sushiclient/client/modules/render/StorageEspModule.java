package net.sushiclient.client.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.render.RenderUtils;

import java.awt.Color;
import java.util.HashSet;

public class StorageEspModule extends BaseModule {

    private static final Color CHEST_COLOR = new Color(160, 110, 40, 100);
    private static final Color ENDER_CHEST_COLOR = new Color(40, 54, 56, 100);
    private static final Color DISPENSER_COLOR = new Color(100, 100, 100, 100);
    private static final Color FURNACE_COLOR = new Color(122, 122, 122, 100);
    private static final Color HOPPER_COLOR = new Color(72, 72, 72, 100);
    private final Frustum frustum = new Frustum();
    private final HashSet<TileEntityChest> rendered = new HashSet<>();

    @Config(id = "outline", name = "Outline")
    public Boolean outline = true;

    @Config(id = "fill", name = "Fill")
    public Boolean fill = true;

    @Config(id = "tracers", name = "Tracers")
    public Boolean tracers = true;

    @Config(id = "chest", name = "Chest")
    public Boolean chest = true;

    @Config(id = "chest_color", name = "Chest Color", when = "chest")
    public EspColor chestColor = new EspColor(CHEST_COLOR, false, true);

    @Config(id = "ender_chest", name = "Ender Chest")
    public Boolean enderChest = false;

    @Config(id = "ender_chest_color", name = "Ender Chest color", when = "ender_chest")
    public EspColor enderChestColor = new EspColor(ENDER_CHEST_COLOR, false, true);

    @Config(id = "dispenser", name = "Dispenser")
    public Boolean dispenser = false;

    @Config(id = "dispenser_color", name = "Dispenser Color", when = "dispenser")
    public EspColor dispenserColor = new EspColor(DISPENSER_COLOR, false, true);

    @Config(id = "shulker_box", name = "Shulker Box")
    public Boolean shulkerBox = false;

    @Config(id = "shulker_box_custom_color", name = "Custom Shulker Box", when = "shulker_box")
    public Boolean shulkerBoxCustomColor = false;

    @Config(id = "shulker_box_color", name = "Shulker Box Color", when = "shulker_box")
    public EspColor shulkerBoxColor = new EspColor(new Color(100, 100, 100, 100), false, true);

    @Config(id = "furnace", name = "Furnace")
    public Boolean furnace = false;

    @Config(id = "furnace_color", name = "Furnace Color", when = "furnace")
    public EspColor furnaceColor = new EspColor(FURNACE_COLOR, false, true);

    @Config(id = "hopper", name = "Hopper")
    public Boolean hopper = false;

    @Config(id = "hopper_color", name = "Hopper Color", when = "hopper")
    public EspColor hopperColor = new EspColor(HOPPER_COLOR, false, true);

    public StorageEspModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    private Color getColor(TileEntity entity) {
        if (entity instanceof TileEntityChest && chest) {
            return chestColor.getCurrentColor();
        } else if (entity instanceof TileEntityEnderChest && enderChest) {
            return enderChestColor.getCurrentColor();
        } else if (entity instanceof TileEntityDispenser && dispenser) {
            return dispenserColor.getCurrentColor();
        } else if (entity instanceof TileEntityShulkerBox && shulkerBox) {
            if (shulkerBoxCustomColor) shulkerBoxColor.getCurrentColor();
            else return new Color(((TileEntityShulkerBox) entity).getColor().getColorValue());
        } else if (entity instanceof TileEntityFurnace && furnace) {
            return furnaceColor.getCurrentColor();
        } else if (entity instanceof TileEntityHopper && hopper) {
            return hopperColor.getCurrentColor();
        }
        return null;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onRender(WorldRenderEvent e) {
        rendered.clear();
        GlStateManager.disableDepth();
        Vec3d interpolated = RenderUtils.getInterpolatedPos();
        frustum.setPosition(interpolated.x, interpolated.y, interpolated.z);
        Vec3d camera = RenderUtils.getCameraPos();
        for (TileEntity tileEntity : getWorld().loadedTileEntityList) {
            Color color = getColor(tileEntity);
            if (color == null) continue;
            BlockPos pos = tileEntity.getPos();
            AxisAlignedBB box = getWorld().getBlockState(pos).getBoundingBox(getWorld(), pos).offset(pos);
            if (tileEntity instanceof TileEntityChest) {
                if (rendered.contains(tileEntity)) continue;
                TileEntityChest chest = (TileEntityChest) tileEntity;
                double width = 0.94;
                if (chest.adjacentChestXPos != null) box = box.expand(width, 0, 0);
                else if (chest.adjacentChestZPos != null) box = box.expand(0, 0, width);
                else if (chest.adjacentChestXNeg != null) box = box.expand(-width, 0, 0);
                else if (chest.adjacentChestZNeg != null) box = box.expand(0, 0, -width);
                rendered.add(chest.adjacentChestXPos);
                rendered.add(chest.adjacentChestZPos);
                rendered.add(chest.adjacentChestXNeg);
                rendered.add(chest.adjacentChestZNeg);
            }
            box = box.grow(0.002);
            if (tracers) {
                RenderUtils.drawLine(camera, box.getCenter(), new Color(color.getRGB()), 1);
            }
            if (!frustum.isBoundingBoxInFrustum(box)) continue;
            if (outline) {
                RenderUtils.drawOutline(box, new Color(color.getRGB()), 1);
            }
            if (fill) {
                RenderUtils.drawFilled(box, color);
            }
        }
        GlStateManager.enableDepth();
    }

    @Override
    public String getDefaultName() {
        return "StorageESP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}

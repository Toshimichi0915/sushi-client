package net.toshimichi.sushi.modules.render;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.world.WorldRenderEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.player.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.HashSet;

public class StorageEspModule extends BaseModule {

    private static final Color CHEST_COLOR = new Color(160, 110, 40);
    private static final Color ENDER_CHEST_COLOR = new Color(40, 54, 56);
    private static final Color DISPENSER_COLOR = new Color(100, 100, 100);
    private static final Color FURNACE_COLOR = new Color(122, 122, 122);
    private static final Color HOPPER_COLOR = new Color(72, 72, 72);
    private final Frustum frustum = new Frustum();
    private final HashSet<TileEntityChest> rendered = new HashSet<>();

    @Config(id = "outline", name = "Outline")
    private Boolean outline = true;

    @Config(id = "fill", name = "Fill")
    private Boolean fill = true;

    @Config(id = "tracers", name = "Tracers")
    private Boolean tracers = true;

    @Config(id = "chest", name = "Chest")
    private Boolean chest = true;

    @Config(id = "ender_chest", name = "Ender Chest")
    private Boolean enderChest = false;

    @Config(id = "dispenser", name = "Dispenser")
    private Boolean dispenser = false;

    @Config(id = "shulker Box", name = "Shulker Box")
    private Boolean shulkerBox = false;

    @Config(id = "furnace", name = "Furnace")
    private Boolean furnace = false;

    @Config(id = "hopper", name = "Hopper")
    private Boolean hopper = false;

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
            return CHEST_COLOR;
        } else if (entity instanceof TileEntityEnderChest && enderChest) {
            return ENDER_CHEST_COLOR;
        } else if (entity instanceof TileEntityDispenser && dispenser) {
            return DISPENSER_COLOR;
        } else if (entity instanceof TileEntityShulkerBox && shulkerBox) {
            return new Color(((TileEntityShulkerBox) entity).getColor().getColorValue());
        } else if (entity instanceof TileEntityFurnace && furnace) {
            return FURNACE_COLOR;
        } else if (entity instanceof TileEntityHopper && hopper) {
            return HOPPER_COLOR;
        }
        return null;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onRender(WorldRenderEvent e) {
        rendered.clear();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
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
            box = box.grow(0.002, 0.002, 0.002);
            if (tracers) {
                RenderUtils.drawLine(camera, box.getCenter(), color, 1);
            }
            if (!frustum.isBoundingBoxInFrustum(box)) continue;
            if (outline) {
                RenderUtils.drawOutline(box, new Color(color.getRGB()), 1);
            }
            if (fill) {
                RenderUtils.drawFilled(box, new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
            }
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
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

package net.toshimichi.sushi.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.mixin.AccessorEntityRenderer;
import org.lwjgl.util.vector.Vector4f;

import java.awt.Color;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderUtils {

    private static final Matrix4f modelView = new Matrix4f();
    private static final Matrix4f projection = new Matrix4f();
    private static Vec3d interpolated;
    private static Vec3d cameraPos;
    private static Vec3d viewerPos;

    public static void tick() {
        // matrix
        FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
        glGetFloat(GL_MODELVIEW_MATRIX, buffer);
        modelView.load(buffer);
        buffer.clear();
        glGetFloat(GL_PROJECTION_MATRIX, buffer);
        projection.load(buffer);

        //interpolated
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        interpolated = getInterpolatedPos(player);
        cameraPos = getInterpolatedPos().add(ActiveRenderInfo.getCameraPosition());
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        viewerPos = new Vec3d(renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ);
    }

    private static Vector4f toVec4f(Vec3d vec) {
        return new Vector4f((float) vec.x, (float) vec.y, (float) vec.z, 1);
    }

    private static void mulMVP(Vector4f vec) {
        Matrix4f.transform(modelView, vec, vec);
        Matrix4f.transform(projection, vec, vec);
    }

    public static double getScale(Vec3d pos) {
        Minecraft client = Minecraft.getMinecraft();
        double rad = Math.toRadians(((AccessorEntityRenderer) client.entityRenderer).invokeGetFOVModifier(client.getRenderPartialTicks(), true));
        return 1 / (rad * MathHelper.sqrt(pos.subtract(getViewerPos()).squareDistanceTo(Vec3d.ZERO)));
    }

    public static Vec2f fromWorld(Vec3d pos) {
        Vector4f vec = toVec4f(pos.subtract(getViewerPos()));
        mulMVP(vec);
        if (vec.w < 0) return null; // invisible area
        vec.x *= 1 / vec.w;
        vec.y *= 1 / vec.w;
        int width = GuiUtils.getWidth();
        int height = GuiUtils.getHeight();
        vec.x = width / 2F + (0.5F * vec.x * width + 0.5F);
        vec.y = height / 2F - (0.5F * vec.y * height + 0.5F);
        return new Vec2f(vec.x, vec.y);
    }

    public static void prepare3D() {
        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_CLAMP);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void release3D() {
        glPopAttrib();
    }

    public static Vec3d getInterpolatedPos(Entity entity) {
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
        return new Vec3d(x, y, z);
    }

    public static Vec3d getInterpolatedPos() {
        return interpolated;
    }

    public static Vec3d getViewerPos() {
        return viewerPos;
    }

    public static Vec3d getCameraPos() {
        return cameraPos;
    }

    public static void drawLine(Vec3d from, Vec3d to, Color color, double width) {
        glLineWidth((float) width);
        GuiUtils.setColor(color);
        prepare3D();
        Vec3d d = getViewerPos();
        glBegin(GL_LINES);
        glVertex3d(from.x - d.x, from.y - d.y, from.z - d.z);
        glVertex3d(to.x - d.x, to.y - d.y, to.z - d.z);
        glEnd();
        release3D();
    }

    public static void drawOutline(AxisAlignedBB box, Color color, double width) {
        glLineWidth((float) width);
        GuiUtils.setColor(color);
        prepare3D();
        glBegin(GL_LINE_STRIP);
        Vec3d d = getViewerPos();
        glVertex3d(box.minX - d.x, box.minY - d.y, box.minZ - d.z);
        glVertex3d(box.minX - d.x, box.minY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.minY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.minY - d.y, box.minZ - d.z);
        glVertex3d(box.minX - d.x, box.minY - d.y, box.minZ - d.z);
        glVertex3d(box.minX - d.x, box.maxY - d.y, box.minZ - d.z);
        glVertex3d(box.minX - d.x, box.maxY - d.y, box.maxZ - d.z);
        glVertex3d(box.minX - d.x, box.minY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.minY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.maxY - d.y, box.maxZ - d.z);
        glVertex3d(box.minX - d.x, box.maxY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.maxY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.maxY - d.y, box.minZ - d.z);
        glVertex3d(box.maxX - d.x, box.minY - d.y, box.minZ - d.z);
        glVertex3d(box.maxX - d.x, box.maxY - d.y, box.minZ - d.z);
        glVertex3d(box.minX - d.x, box.maxY - d.y, box.minZ - d.z);
        glEnd();
        release3D();
    }

    public static void drawFilled(AxisAlignedBB box, Color color) {
        GuiUtils.setColor(color);
        prepare3D();
        Vec3d d = getViewerPos();
        glBegin(GL_QUAD_STRIP);
        glVertex3d(box.maxX - d.x, box.maxY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.maxY - d.y, box.minZ - d.z);
        glVertex3d(box.minX - d.x, box.maxY - d.y, box.maxZ - d.z);
        glVertex3d(box.minX - d.x, box.maxY - d.y, box.minZ - d.z);
        glVertex3d(box.minX - d.x, box.minY - d.y, box.maxZ - d.z);
        glVertex3d(box.minX - d.x, box.minY - d.y, box.minZ - d.z);
        glVertex3d(box.maxX - d.x, box.minY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.minY - d.y, box.minZ - d.z);
        glEnd();
        glBegin(GL_QUAD_STRIP);
        glVertex3d(box.minX - d.x, box.minY - d.y, box.minZ - d.z);
        glVertex3d(box.minX - d.x, box.maxY - d.y, box.minZ - d.z);
        glVertex3d(box.maxX - d.x, box.minY - d.y, box.minZ - d.z);
        glVertex3d(box.maxX - d.x, box.maxY - d.y, box.minZ - d.z);
        glVertex3d(box.maxX - d.x, box.minY - d.y, box.maxZ - d.z);
        glVertex3d(box.maxX - d.x, box.maxY - d.y, box.maxZ - d.z);
        glVertex3d(box.minX - d.x, box.minY - d.y, box.maxZ - d.z);
        glVertex3d(box.minX - d.x, box.maxY - d.y, box.maxZ - d.z);
        glEnd();
        release3D();
    }
}

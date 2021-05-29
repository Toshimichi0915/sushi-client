package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderUtils {

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

    public static Vec3d getInterpolatedPos() {
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
        return new Vec3d(x, y, z);
    }

    public static Vec3d getCameraPos() {
        return getInterpolatedPos().add(ActiveRenderInfo.getCameraPosition());
    }

    public static void drawLine(Vec3d from, Vec3d to, Color color, double width) {
        glLineWidth((float) width);
        GuiUtils.setColor(color);
        prepare3D();
        Vec3d d = getInterpolatedPos();
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
        Vec3d d = getInterpolatedPos();
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
        Vec3d d = getInterpolatedPos();
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

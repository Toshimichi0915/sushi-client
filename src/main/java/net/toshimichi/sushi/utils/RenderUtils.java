package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils {

    public static void prepare3D() {
        glPushMatrix();
        glPushAttrib(GL_ENABLE_BIT);
        glDisable(GL_TEXTURE_2D);
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
        glTranslated(-dx, -dy, -dz);
    }

    public static void release3D() {
        glPopMatrix();
        glPopAttrib();
    }

    public static void drawOutline(AxisAlignedBB box, Color color, double width) {
        glLineWidth((float) width);
        GuiUtils.setColor(color);
        prepare3D();
        glBegin(GL_LINE_STRIP);
        glVertex3d(box.minX, box.minY, box.minZ);
        glVertex3d(box.minX, box.minY, box.maxZ);
        glVertex3d(box.maxX, box.minY, box.maxZ);
        glVertex3d(box.maxX, box.minY, box.minZ);
        glVertex3d(box.minX, box.minY, box.minZ);
        glVertex3d(box.minX, box.maxY, box.minZ);
        glVertex3d(box.minX, box.maxY, box.maxZ);
        glVertex3d(box.minX, box.minY, box.maxZ);
        glVertex3d(box.maxX, box.minY, box.maxZ);
        glVertex3d(box.maxX, box.maxY, box.maxZ);
        glVertex3d(box.minX, box.maxY, box.maxZ);
        glVertex3d(box.maxX, box.maxY, box.maxZ);
        glVertex3d(box.maxX, box.maxY, box.minZ);
        glVertex3d(box.maxX, box.minY, box.minZ);
        glVertex3d(box.maxX, box.maxY, box.minZ);
        glVertex3d(box.minX, box.maxY, box.minZ);
        glEnd();
        release3D();
    }
}

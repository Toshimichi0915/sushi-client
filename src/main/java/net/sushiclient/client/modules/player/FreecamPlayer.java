package net.sushiclient.client.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.sushiclient.client.utils.player.MovementUtils;

public class FreecamPlayer extends EntityOtherPlayerMP {

    private final EntityPlayerSP original;
    private float initYaw;
    private float initPitch;

    public FreecamPlayer(World worldIn) {
        super(worldIn, Minecraft.getMinecraft().getSession().getProfile());
        original = Minecraft.getMinecraft().player;
        if (original == null) throw new IllegalStateException();
        copyLocationAndAnglesFrom(original);
        capabilities.allowFlying = true;
        capabilities.isFlying = true;
        initYaw = original.rotationYaw;
        initPitch = original.rotationPitch;
    }

    private int toInt(KeyBinding plus, KeyBinding minus) {
        int result = 0;
        result += plus.isKeyDown() ? 1 : 0;
        result -= minus.isKeyDown() ? 1 : 0;
        return result;
    }

    @Override
    public void onLivingUpdate() {
        // rotation
        float deltaYaw = original.rotationYaw - initYaw;
        float deltaPitch = original.rotationPitch - initPitch;
        rotationYaw += deltaYaw;
        rotationPitch += deltaPitch;
        original.rotationYaw = initYaw;
        original.rotationPitch = initPitch;

        setHealth(original.getHealth());
        setAbsorptionAmount(original.getAbsorptionAmount());
        inventory.copyInventory(original.inventory);
        updateEntityActionState();
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        moveForward = toInt(settings.keyBindForward, settings.keyBindBack);
        moveVertical = toInt(settings.keyBindJump, settings.keyBindSneak);
        moveStrafing = toInt(settings.keyBindLeft, settings.keyBindRight);
        Vec2f motionXZ = MovementUtils.toWorld(new Vec2f(moveForward, moveStrafing), rotationYaw);
        Vec3d motionXYZ = new Vec3d(motionXZ.x, moveVertical, motionXZ.y);
        motionX = motionXYZ.x;
        motionY = motionXYZ.y;
        motionZ = motionXYZ.z;
        setSprinting(settings.keyBindSprint.isKeyDown());
        if (isSprinting()) {
            motionX *= 1.5;
            motionY *= 1.5;
            motionZ *= 1.5;
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return true;
    }
}

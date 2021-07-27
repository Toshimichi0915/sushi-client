package net.toshimichi.sushi.command.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;

@CommandAlias(value = "setblock", description = "Sets a ghost block to a specific location")
public class GhostBlockCommand {

    @Default
    public void onDefault(MessageHandler out, Integer x, Integer y, Integer z, Integer id) {
        Block block = Block.getBlockById(id);
        if (block == null) {
            out.send("Could not find Block with block id " + id, LogLevel.ERROR);
            return;
        }
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) {
            out.send("World is not loaded", LogLevel.ERROR);
            return;
        }
        world.setBlockState(new BlockPos(x, y, z), block.getDefaultState());
    }
}

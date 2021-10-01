package net.sushiclient.client.modules.world;

import net.sushiclient.client.utils.player.PositionOperator;
import net.sushiclient.client.utils.world.BlockPlaceInfo;

public interface Scaffold {
    void rotate(BlockPlaceInfo info, PositionOperator operator);
}

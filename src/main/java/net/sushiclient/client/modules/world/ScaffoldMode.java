package net.sushiclient.client.modules.world;

import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.utils.player.AutoDesyncOperator;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

public enum ScaffoldMode implements Scaffold, Named {

    VANILLA("Vanilla") {
        @Override
        public void rotate(BlockPlaceInfo info, AutoDesyncOperator operator) {

        }
    },

    NCP("2B2T") {
        @Override
        public void rotate(BlockPlaceInfo info, AutoDesyncOperator operator) {
            Vec3d lookAt = BlockUtils.toVec3d(info.getBlockPos())
                    .add(0.5, 0.5, 0.5)
                    .add(new Vec3d(info.getBlockFace().getFacing().getOpposite().getDirectionVec()));
            operator.desyncMode(DesyncMode.LOOK).lookAt(lookAt);
        }
    };

    private final String name;

    ScaffoldMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}

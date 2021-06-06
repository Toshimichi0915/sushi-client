package net.toshimichi.sushi.handlers.forge;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.BlockLeftClickEvent;

public class BlockLeftClickHandler {

    @SubscribeEvent
    public void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock e) {
        BlockLeftClickEvent event = new BlockLeftClickEvent(EventTiming.PRE, e.getEntityPlayer(), e.getPos(), e.getFace(), e.getHitVec());
        event.setUseBlock(e.getUseBlock());
        event.setUseItem(e.getUseItem());
        event.setCancellationResult(e.getCancellationResult());
        event.setCancelled(e.isCanceled());

        EventHandlers.callEvent(event);
        e.setUseBlock(event.getUseBlock());
        e.setUseItem(event.getUseItem());
        e.setCancellationResult(e.getCancellationResult());
        e.setCanceled(event.isCancelled());
    }
}

package net.sushiclient.client.handlers.forge;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.BlockLeftClickEvent;

public class BlockLeftClickHandler {

    @SubscribeEvent
    public void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock e) {
        BlockLeftClickEvent event = new BlockLeftClickEvent(EventTiming.PRE, e);

        EventHandlers.callEvent(event);
        e.setUseBlock(event.getUseBlock());
        e.setUseItem(event.getUseItem());
        e.setCancellationResult(event.getCancellationResult());
        e.setCanceled(event.isCancelled());
    }


}

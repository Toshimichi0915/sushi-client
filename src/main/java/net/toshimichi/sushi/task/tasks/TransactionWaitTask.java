package net.toshimichi.sushi.task.tasks;

import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.task.TaskAdapter;

public class TransactionWaitTask extends TaskAdapter<Short, Boolean> {

    private final int maxTicks;
    private boolean received;
    private boolean accepted;
    private int counter;

    public TransactionWaitTask(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    public TransactionWaitTask() {
        this(5);
    }

    @Override
    public void start(Short input) throws Exception {
        super.start(input);
        EventHandlers.register(this);
    }

    @Override
    public void tick() throws Exception {
        if (!received && counter++ < maxTicks) return;
        stop(accepted);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketConfirmTransaction)) return;
        SPacketConfirmTransaction packet = (SPacketConfirmTransaction) e.getPacket();
        if (packet.getActionNumber() != getInput()) return;
        received = true;
        accepted = packet.wasAccepted();
    }

    @Override
    public void stop(Boolean result) {
        super.stop(result);
        EventHandlers.unregister(this);
    }
}

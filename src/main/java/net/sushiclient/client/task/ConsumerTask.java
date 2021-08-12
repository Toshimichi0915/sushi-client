package net.sushiclient.client.task;

@FunctionalInterface
public interface ConsumerTask<I> {
    void tick(I item) throws Exception;
}

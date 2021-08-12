package net.sushiclient.client.task;

public interface ConsumerRepeatTask<I> {
    boolean tick(I item) throws Exception;
}

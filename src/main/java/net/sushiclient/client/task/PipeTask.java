package net.sushiclient.client.task;

@FunctionalInterface
public interface PipeTask<I, R> {
    R tick(I item) throws Exception;
}

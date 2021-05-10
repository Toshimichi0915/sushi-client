package net.toshimichi.sushi.task;

@FunctionalInterface
public interface ConsumerTask<I> {
    void tick(I item) throws Exception;
}

package net.toshimichi.sushi.task;

public interface ConsumerRepeatTask<I> {
    boolean tick(I item) throws Exception;
}

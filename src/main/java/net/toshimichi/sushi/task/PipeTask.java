package net.toshimichi.sushi.task;

@FunctionalInterface
public interface PipeTask<I, R> {
    R tick(I item) throws Exception;
}

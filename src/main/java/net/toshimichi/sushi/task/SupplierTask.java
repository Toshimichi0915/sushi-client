package net.toshimichi.sushi.task;

@FunctionalInterface
public interface SupplierTask<R> {
    R tick() throws Exception;
}

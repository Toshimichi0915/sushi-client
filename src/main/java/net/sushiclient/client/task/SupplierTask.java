package net.sushiclient.client.task;

@FunctionalInterface
public interface SupplierTask<R> {
    R tick() throws Exception;
}

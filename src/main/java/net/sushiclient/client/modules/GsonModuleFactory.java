package net.sushiclient.client.modules;

public class GsonModuleFactory implements ModuleFactory {

    private final String id;
    private final ModuleConstructor constructor;

    public GsonModuleFactory(String id, ModuleConstructor constructor) {
        this.id = id;
        this.constructor = constructor;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ModuleConstructor getConstructor() {
        return constructor;
    }
}

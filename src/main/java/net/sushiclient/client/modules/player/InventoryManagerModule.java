package net.sushiclient.client.modules.player;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityInfo;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

import java.util.*;
import java.util.function.Predicate;

public class InventoryManagerModule extends BaseModule {

    private final Configuration<Boolean> hotbar;
    private final ArrayList<SwitchInfo> all;

    private void add(String id, String name, int min, int max, ConfigurationCategory category, int stackSize, Predicate<ItemStack> item) {
        Configuration<IntRange> conf1 = category.get("min_" + id, "Min " + name, null, IntRange.class, new IntRange(min, 36, 0, 1));
        Configuration<IntRange> conf2 = category.get("max_" + id, "Max " + name, null, IntRange.class, new IntRange(max, 36, 0, 1));
        all.add(new SwitchInfo(stackSize, item, conf1, conf2));
    }

    private void add(String id, String name, int min, int max, ConfigurationCategory category, Item item) {
        add(id, name, min, max, category, item.getItemStackLimit(), it -> it.getItem() == item);
    }

    private void addArrow(String id, String name, int min, int max, ConfigurationCategory category, Potion effect) {
        add(id, name, min, max, category, 64, it -> {
            if (it.getItem() != Items.TIPPED_ARROW) return false;
            return PotionUtils.getEffectsFromStack(it).stream()
                    .anyMatch(e -> e.getPotion() == effect);
        });
    }

    public InventoryManagerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        hotbar = provider.get("hotbar", "Hotbar", null, Boolean.class, false);
        all = new ArrayList<>();

        ConfigurationCategory combat = provider.getCategory("combat", "Combat Settings", null);
        add("sword", "Sword", 1, 1, combat, Items.DIAMOND_SWORD);
        add("pickaxe", "Pickaxe", 1, 1, combat, Items.DIAMOND_PICKAXE);
        add("crystal", "Crystal", 3, 9, combat, Items.END_CRYSTAL);
        add("exp_bottles", "Exp Bottles", 4, 8, combat, Items.EXPERIENCE_BOTTLE);
        add("gapple", "Gapple", 2, 2, combat, Items.GOLDEN_APPLE);
        add("totem", "Totem", 4, 4, combat, Items.TOTEM_OF_UNDYING);

        ConfigurationCategory bow = provider.getCategory("bow", "Bow Settings", null);
        add("bow", "Bow", 0, 0, bow, Items.BOW);
        addArrow("strength_arrow", "Strength Arrow", 0, 0, bow, MobEffects.STRENGTH);
        addArrow("weakness_arrow", "Weakness Arrow", 0, 0, bow, MobEffects.WEAKNESS);
        addArrow("damage_arrow", "Damage Arrow", 0, 0, bow, MobEffects.INSTANT_DAMAGE);

        ConfigurationCategory armor = provider.getCategory("armor", "Armor Settings", null);
        add("helmet", "Helmet", 1, 2, armor, Items.DIAMOND_HELMET);
        add("chest_plate", "Chest Plate", 1, 2, armor, Items.DIAMOND_CHESTPLATE);
        add("leggings", "Leggings", 1, 2, armor, Items.DIAMOND_LEGGINGS);
        add("boots", "Boots", 1, 2, armor, Items.DIAMOND_BOOTS);

        ConfigurationCategory block = provider.getCategory("block", "Block Settings", null);
        add("obsidian", "Obsidian", 1, 1, block, Item.getItemFromBlock(Blocks.OBSIDIAN));
        add("enderchest", "Enderchest", 1, 1, block, Item.getItemFromBlock(Blocks.ENDER_CHEST));
        add("piston", "Piston", 1, 2, block, Item.getItemFromBlock(Blocks.PISTON));
        add("redstone_block", "Redstone Block", 1, 2, block, Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
        add("redstone_torch", "Redstone Torch", 0, 0, block, Item.getItemFromBlock(Blocks.REDSTONE_TORCH));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private SwitchInfo getSwitchInfo(ItemStack item) {
        return all.stream()
                .filter(it -> it.getPredicate().test(item))
                .findAny()
                .orElse(null);
    }

    private List<SwitchCandidate> getSwitchCandidates() {
        ArrayList<SwitchCandidate> result = new ArrayList<>();
        HashMap<ItemStack, Integer> items = new HashMap<>();
        for (EntityInfo<EntityItem> entityInfo : EntityUtils.getNearbyEntities(getPlayer().getPositionVector(), EntityItem.class)) {
            if (entityInfo.getDistanceSq() > 1) continue;
            ItemStack itemStack = entityInfo.getEntity().getItem();
            items.put(itemStack, items.getOrDefault(itemStack, 0) + itemStack.getCount());
        }

        for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
            SwitchInfo info = getSwitchInfo(entry.getKey());
            if (info == null) continue;
            int inv = info.checkInv();
            if (inv >= 0) continue;
            result.add(new SwitchCandidate(entry.getKey(), entry.getValue() > -inv ? -inv : entry.getValue()));
        }
        return result;
    }

    private void throwAwayUnwanted(int amount) {
        all.sort(null);
        int current = amount;
        for (SwitchInfo info : all) {
            if (info.checkInv() <= 0) continue;
            InventoryType[] types = hotbar.getValue() ? new InventoryType[]{InventoryType.MAIN, InventoryType.HOTBAR} : new InventoryType[]{InventoryType.MAIN};
            ItemSlot[] itemSlots = InventoryUtils.find(it -> info.getPredicate().test(it.getItemStack()), null, types);
            if (info.checkInv() <= 0) return;
            for (ItemSlot itemSlot : itemSlots) {
                if (current <= 0) return;
                current -= itemSlot.getItemStack().getCount();
                InventoryUtils.clickItemSlot(itemSlot, ClickType.THROW, 1);
            }
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        List<SwitchCandidate> candidates = getSwitchCandidates();
        int total = candidates.stream().reduce(0, (i1, i2) -> i1 + i2.getAmount(), Integer::sum);
        int empty = (int) Arrays.stream(ItemSlot.valueOf(InventoryType.HOTBAR, InventoryType.MAIN))
                .filter(it -> it.getItemStack().getItem() == Items.AIR).count() * 64;
        int throwAway = total - empty;
        if (throwAway <= 0) return;
        throwAwayUnwanted(throwAway);
    }

    @Override
    public String getDefaultName() {
        return "InventoryManager";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    private static class SwitchCandidate {
        private final ItemStack item;
        private final int amount;

        public SwitchCandidate(ItemStack item, int amount) {
            this.item = item;
            this.amount = amount;
        }

        public ItemStack getItemStack() {
            return item;
        }

        public int getAmount() {
            return amount;
        }
    }

    private class SwitchInfo implements Comparable<SwitchInfo> {
        private final int stackSize;
        private final Predicate<ItemStack> item;
        private final Configuration<IntRange> min;
        private final Configuration<IntRange> max;

        public SwitchInfo(int stackSize, Predicate<ItemStack> item, Configuration<IntRange> min, Configuration<IntRange> max) {
            this.stackSize = stackSize;
            this.item = item;
            this.min = min;
            this.max = max;
        }

        public Predicate<ItemStack> getPredicate() {
            return item;
        }

        public int getMin() {
            return min.getValue().getCurrent() * stackSize;
        }

        public int getMax() {
            return max.getValue().getCurrent() * stackSize;
        }

        public int checkInv() {
            int current = 0;
            for (ItemSlot itemSlot : ItemSlot.values()) {
                if (hotbar.getValue() && itemSlot.getInventoryType() == InventoryType.HOTBAR) continue;
                if (item.test(itemSlot.getItemStack())) {
                    current += itemSlot.getItemStack().getCount();
                }
            }

            if (getMin() > current) return current - getMin();
            else if (getMax() < current) return current - getMax();
            else return 0;
        }

        @Override
        public int compareTo(SwitchInfo o) {
            return Integer.compare(o.checkInv(), checkInv());
        }
    }
}

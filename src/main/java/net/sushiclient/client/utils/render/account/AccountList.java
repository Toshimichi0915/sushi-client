package net.sushiclient.client.utils.render.account;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.sushiclient.client.account.MojangAccount;
import net.sushiclient.client.account.MojangAccounts;

import java.util.ArrayList;

public class AccountList extends GuiListExtended {

    public static final MojangAccount EMPTY_ENTRY = new MojangAccount(null, null, null, null, null, null);
    private final GuiAccounts owner;
    private final ArrayList<AccountEntry> entries = new ArrayList<>();
    private final AccountEmptyEntry accountEmptyEntry = new AccountEmptyEntry(mc);

    public AccountList(GuiAccounts owner, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = owner;
    }

    public void refresh(MojangAccounts accounts) {
        entries.clear();
        for (MojangAccount account : accounts.getAll()) {
            entries.add(new AccountEntry(owner, account));
        }
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        if (entries.isEmpty() && index == 0) {
            return accountEmptyEntry;
        } else {
            return entries.get(index);
        }
    }

    public int indexOf(IGuiListEntry entry) {
        if (entry instanceof AccountEntry) {
            return entries.indexOf(entry);
        } else {
            return -1;
        }
    }

    @Override
    protected int getSize() {
        return Math.max(entries.size(), 1);
    }
}

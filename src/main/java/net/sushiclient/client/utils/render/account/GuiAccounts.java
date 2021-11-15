package net.sushiclient.client.utils.render.account;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import net.sushiclient.client.account.AccountStatus;
import net.sushiclient.client.account.MojangAccount;
import net.sushiclient.client.account.MojangAccounts;
import net.sushiclient.client.utils.player.SessionUtils;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GuiAccounts extends GuiScreen {

    private final GuiScreen parent;
    private final MojangAccounts accounts;
    private AccountList accountList;
    private GuiButton login;
    private GuiButton directLogin;
    private GuiButton clipboard;
    private GuiButton add;
    private GuiButton remove;
    private GuiButton cancel;
    private GuiTextField email;
    private GuiTextField password;
    private int selected;
    private AccountStatus lastStatus = AccountStatus.UNKNOWN;

    public GuiAccounts(GuiScreen parent, MojangAccounts accounts) {
        this.parent = parent;
        this.accounts = accounts;
    }

    public void setSelected(AccountEntry entry) {
        selected = accountList.indexOf(entry);
        if (selected == -1) {
            login.enabled = false;
            remove.enabled = false;
        } else {
            login.enabled = true;
            remove.enabled = true;
        }
    }

    public GuiListExtended.IGuiListEntry getSelected() {
        if (selected != -1 && selected < accountList.getSize()) {
            return accountList.getListEntry(selected);
        } else {
            return null;
        }
    }

    @Override
    public void initGui() {
        accountList = new AccountList(this, mc, this.width, this.height, 30, this.height - 100, 30);
        accountList.refresh(accounts);

        buttonList.clear();
        login = add(new GuiButton(0, this.width / 2 - 30, this.height - 58, 75, 20, "Login"));
        directLogin = add(new GuiButton(1, this.width / 2 + 50, this.height - 58, 75, 20, "Direct Login"));
        clipboard = add(new GuiButton(2, this.width / 2 + 130, this.height - 58, 75, 20, "Clipboard"));
        add = add(new GuiButton(3, this.width / 2 - 30, this.height - 34, 75, 20, "Add"));
        remove = add(new GuiButton(4, this.width / 2 + 50, this.height - 34, 75, 20, "Remove"));
        cancel = add(new GuiButton(5, this.width / 2 + 130, this.height - 34, 75, 20, I18n.format("gui.cancel")));

        email = new GuiTextField(1, mc.fontRenderer, 20, this.height - 64, this.width / 2 - 60, 20);
        password = new GuiTextField(2, mc.fontRenderer, 20, this.height - 25, this.width / 2 - 60, 20);
        setSelected(null);
    }

    private GuiButton add(GuiButton button) {
        buttonList.add(button);
        return button;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        accountList.mouseClicked(mouseX, mouseY, mouseButton);
        email.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        email.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        accountList.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        accountList.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRenderer, "Accounts", this.width / 2, 10, 16777215);
        drawString(fontRenderer, "E-mail", 20, this.height - 77, 10526880);
        drawString(fontRenderer, "Password", 20, this.height - 38, 10526880);
        email.drawTextBox();
        password.drawTextBox();

        Session session = mc.getSession();
        GuiUtils.drawRect(10, 40, this.width / 2 - 130, this.height - 150, new Color(0, 0, 0, 80));
        mc.fontRenderer.drawString("Status:", 15, 55, Color.WHITE.getRGB());
        if (lastStatus == AccountStatus.VALID) {
            mc.fontRenderer.drawString("Valid", 20, 65, Color.GREEN.getRGB());
        } else if (lastStatus == AccountStatus.INVALID) {
            mc.fontRenderer.drawString("Invalid", 20, 65, Color.RED.getRGB());
        } else if (lastStatus == AccountStatus.UNKNOWN) {
            mc.fontRenderer.drawString("Unknown", 20, 65, Color.YELLOW.getRGB());
        }
        mc.fontRenderer.drawString("Name:", 15, 80, Color.WHITE.getRGB());
        mc.fontRenderer.drawString(session.getUsername(), 20, 90, Color.WHITE.getRGB());


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void login(MojangAccount acc) {
        Session session;
        if (!acc.getPassword().isEmpty()) {
            boolean success = accounts.auth(acc);
            if (success) lastStatus = AccountStatus.VALID;
            else lastStatus = AccountStatus.INVALID;
            session = new Session(acc.getName(), acc.getId(), acc.getAccessToken(), "mojang");
        } else {
            acc.setName(acc.getEmail());
            acc.setId(acc.getEmail());
            session = new Session(acc.getEmail(), acc.getEmail(), "", "mojang");
        }
        SessionUtils.setSession(session);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == login) {
            GuiListExtended.IGuiListEntry selected = getSelected();
            if (!(selected instanceof AccountEntry)) return;
            AccountEntry entry = (AccountEntry) selected;
            login(entry.getAccount());
            accounts.save();
        } else if (button == directLogin) {
            MojangAccount account = new MojangAccount(email.getText(), password.getText());
            email.setText("");
            password.setText("");
            login(account);
        } else if (button == clipboard) {
            String clipboard;
            try {
                clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e) {
                clipboard = "";
            }
            String[] split = clipboard.split(":");
            if (split.length < 2) return;
            email.setText(split[0]);
            password.setText(split[1]);
            actionPerformed(directLogin);
        } else if (button == add) {
            MojangAccount account = new MojangAccount(email.getText(), password.getText());
            email.setText("");
            password.setText("");
            accounts.add(account);
            accountList.refresh(accounts);
            accounts.save();
        } else if (button == remove) {
            GuiListExtended.IGuiListEntry selected = getSelected();
            if (!(selected instanceof AccountEntry)) return;
            AccountEntry entry = (AccountEntry) selected;
            accounts.remove(entry.getAccount());
            accountList.refresh(accounts);
            accounts.save();
            setSelected(null);
        } else if (button == cancel) { // cancel
            mc.displayGuiScreen(parent);
        }
    }
}

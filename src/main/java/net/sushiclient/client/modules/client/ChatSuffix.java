package net.sushiclient.client.modules.client;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.modules.*;

import java.io.IOException;

public class ChatSuffix extends BaseModule {

    private static final String ASCII = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ASCII_1 = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘQʀꜱᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘQʀꜱᴛᴜᴠᴡxʏᴢ";
    private static final String ASCII_2 = "ᵃᵇᶜᵈᵉᶠᵍʰⁱʲᵏˡᵐⁿᵒᵖqʳˢᵗᵘᵛʷˣʸᶻᴬᴮᶜᴰᴱᶠᴳᴴᴵᴶᴷᴸᴹᴺᴼᴾQᴿˢᵀᵁⱽᵂˣʸᶻ";

    private final Configuration<String> suffix;
    private final Configuration<IntRange> mode;

    public ChatSuffix(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        suffix = provider.get("suffix", "Suffix", null, String.class, " | sushi");
        mode = provider.get("mode", "Mode", null, IntRange.class, new IntRange(0, 2, 0, 1));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private String getASCII() {
        switch (mode.getValue().getCurrent()) {
            case 1:
                return ASCII_1;
            case 2:
                return ASCII_2;
            default:
                return ASCII;
        }
    }

    private String convert(String from, String ascii) {
        char[] table = ascii.toCharArray();
        char[] conv = new char[from.length()];
        for (int i = 0; i < from.toCharArray().length; i++) {
            int index;
            char c = from.toCharArray()[i];
            if (c >= 'A' && c <= 'Z') {
                index = c - 'A' + 26;
            } else if (c >= 'a' && c <= 'z') {
                index = c - 'a';
            } else {
                conv[i] = c;
                continue;
            }
            conv[i] = table[index];
        }
        return new String(conv);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) throws IOException {
        if (!(e.getPacket() instanceof CPacketChatMessage)) return;
        CPacketChatMessage packet = (CPacketChatMessage) e.getPacket();

        PacketBuffer buff = new PacketBuffer(Unpooled.buffer(256));
        packet.writePacketData(buff);
        String text = buff.readString(256);

        text += convert(suffix.getValue(), getASCII());
        text = text.substring(0, Math.min(text.length(), 256));

        buff.clear();
        buff.writeString(text);
        packet.readPacketData(buff);
    }

    @Override
    public String getDefaultName() {
        return "ChatSuffix";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}

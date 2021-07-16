package net.toshimichi.sushi.modules.client;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.modules.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Proxy;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AutoVote extends BaseModule {

    private static final String URL = "https://minecraftservers.org/vote/609608";
    private static final String ALREADY_VOTED = "すでに投票されています | Already voted today";
    private static final String ERROR = "2b2t.jp での自動投票に失敗しました | Failed to automatically vote on 2b2t.jp";

    public AutoVote(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    private void tryVote() {
        MessageHandler messageHandler = Sushi.getProfile().getMessageHandler();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String getRes = IOUtils.toString(httpClient.execute(new HttpGet(URL)).getEntity().getContent(), UTF_8);
            Document document = Jsoup.parse(getRes);
            String csrf = document.select("input[name=csrf]").val();
            String siteKey = document.select(".h-captcha").attr("data-sitekey");
            if(csrf.isEmpty() || siteKey.isEmpty()) {
                messageHandler.send(ALREADY_VOTED, LogLevel.INFO);
                return;
            }

        } catch (Exception e) {
            messageHandler.send(ERROR, LogLevel.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        tryVote();
    }

    @Override
    public String getDefaultName() {
        return "AutoVote";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}

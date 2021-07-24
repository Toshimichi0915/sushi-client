package net.toshimichi.sushi.modules.client;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.hwid.annotations.Value;
import net.toshimichi.sushi.hwid.gen.EncryptUtils;
import net.toshimichi.sushi.modules.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AutoVoteModule extends BaseModule {

    private static final String URL = "https://minecraftservers.org/vote/609608";
    private static final String ALREADY_VOTED = "すでに投票されています | Already voted today";
    private static final String ERROR = "2b2t.jp での自動投票に失敗しました | Failed to automatically vote on 2b2t.jp";
    private static final String BROKEN_API_KEY = "現在自動投票機能に問題が生じています | Currently auto-vote feature has a problem";
    private static final String SUCCESS = "投票に成功しました";
    @Value("encryptedApiKey")
    public static String encryptedApiKey;
    @Value("targetUrl")
    public static String targetUrl;
    private volatile boolean running;

    public AutoVoteModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    private void tryVote() {
        MessageHandler messageHandler = Sushi.getProfile().getMessageHandler();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            running = true;
            String getRes;
            try (CloseableHttpResponse response = httpClient.execute(new HttpGet(URL))) {
                getRes = IOUtils.toString(response.getEntity().getContent(), UTF_8);
            }
            Document document = Jsoup.parse(getRes);
            String csrf = document.select("input[name=csrf]").val();
            String dataSiteKey = document.select(".h-captcha").attr("data-sitekey");
            if (csrf.isEmpty() || dataSiteKey.isEmpty()) {
                messageHandler.send(ALREADY_VOTED, LogLevel.INFO);
                return;
            }
            byte[] apiKeyBytes = EncryptUtils.decrypt(EncryptUtils.getHWID(), Base64.getDecoder().decode(encryptedApiKey));
            if (apiKeyBytes == null) {
                messageHandler.send(BROKEN_API_KEY, LogLevel.ERROR);
                return;
            }
            HttpPost post = new HttpPost(targetUrl + "/solve-captcha");
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("apiKey", new String(apiKeyBytes, UTF_8)));
            pairs.add(new BasicNameValuePair("dataSiteKey", dataSiteKey));
            post.setEntity(new UrlEncodedFormEntity(pairs));
            httpClient.execute(post).close();
            String answer = null;
            for (int i = 0; i < 10; i++) {
                Thread.sleep(i == 0 ? 5000 : 10000);
                ArrayList<BasicNameValuePair> pairs2 = new ArrayList<>();
                HttpPost post2 = new HttpPost(targetUrl + "/get-answer");
                pairs2.add(new BasicNameValuePair("apiKey", new String(apiKeyBytes, UTF_8)));
                post2.setEntity(new UrlEncodedFormEntity(pairs2));
                try (CloseableHttpResponse response = httpClient.execute(post2)) {
                    if (response.getStatusLine().getStatusCode() == 202) continue;
                    answer = IOUtils.toString(response.getEntity().getContent(), UTF_8);
                    break;
                }
            }
            if (answer == null) {
                messageHandler.send(BROKEN_API_KEY, LogLevel.ERROR);
                return;
            }

            ArrayList<BasicNameValuePair> pairs3 = new ArrayList<>();
            pairs3.add(new BasicNameValuePair("g-captcha-response", answer));
            pairs3.add(new BasicNameValuePair("h-captcha-response", answer));
            pairs3.add(new BasicNameValuePair("captcha", "1"));
            pairs3.add(new BasicNameValuePair("username", getPlayer().getName()));
            pairs3.add(new BasicNameValuePair("csrf", csrf));
            HttpPost post3 = new HttpPost(URL);
            post3.setEntity(new UrlEncodedFormEntity(pairs3, StandardCharsets.UTF_8));
            try (CloseableHttpResponse res = httpClient.execute(post3)) {
                EntityUtils.consume(res.getEntity());
            }
            messageHandler.send(SUCCESS, LogLevel.INFO);
        } catch (Exception e) {
            messageHandler.send(ERROR, LogLevel.ERROR);
            e.printStackTrace();
        } finally {
            running = false;
        }
    }

    @Override
    public void onEnable() {
        if (running) return;
        new Thread(this::tryVote).start();
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

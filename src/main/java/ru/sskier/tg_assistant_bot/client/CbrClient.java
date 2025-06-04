package ru.sskier.tg_assistant_bot.client;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sskier.tg_assistant_bot.exception.BotException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CbrClient {

    private final OkHttpClient client;

    @Value("${cbr.currency.rates.url}")
    private String ratesUrl;

    public String getCurrentRatesXml() throws BotException {
        Request request = new Request.Builder()
                .url(ratesUrl)
                .build();
        try (Response response = client.newCall(request).execute();) {
            ResponseBody body = response.body();
            return body == null ? null : body.string();
        } catch (IOException e) {
            throw new BotException("Error during get currency rates", e);
        }
    }

}

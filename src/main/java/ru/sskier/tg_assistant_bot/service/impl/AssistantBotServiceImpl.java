package ru.sskier.tg_assistant_bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.xml.sax.InputSource;
import ru.sskier.tg_assistant_bot.client.CbrClient;
import ru.sskier.tg_assistant_bot.entity.ValuteCbr;
import ru.sskier.tg_assistant_bot.exception.BotException;
import ru.sskier.tg_assistant_bot.service.AssistantBotService;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssistantBotServiceImpl implements AssistantBotService {

    private final static String EXCHANGE_RATES_XPATH = "/ValCurs//Valute[R01235]/Value";

    private final CbrClient cbrClient;

    @Override
    public String getExchangeRates() {
        String xml = cbrClient.getCurrentRatesXml();
        //Map<String, ValuteCbr> exchangeRates =

        return extractCurrencyValuesFromXML(xml, EXCHANGE_RATES_XPATH);
    }

    private static String extractCurrencyValuesFromXML(String xml, String xpathExpression){
        var source = new InputSource(new StringReader(xml));
        try{
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e){
            throw new BotException("Не удалось распарсить XML", e);
        }
    }



}

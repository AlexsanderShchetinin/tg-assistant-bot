package ru.sskier.tg_assistant_bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import ru.sskier.tg_assistant_bot.client.CbrClient;
import ru.sskier.tg_assistant_bot.entity.AssistantBotHistory;
import ru.sskier.tg_assistant_bot.entity.User;
import ru.sskier.tg_assistant_bot.entity.valute.CompositeValuteKey;
import ru.sskier.tg_assistant_bot.entity.valute.Valute;
import ru.sskier.tg_assistant_bot.entity.valute.ValuteParser;
import ru.sskier.tg_assistant_bot.exception.BotException;
import ru.sskier.tg_assistant_bot.repository.AssistantBotHistoryRepository;
import ru.sskier.tg_assistant_bot.repository.UserRepository;
import ru.sskier.tg_assistant_bot.repository.ValuteRepository;
import ru.sskier.tg_assistant_bot.service.AssistantBotService;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssistantBotServiceImpl implements AssistantBotService {

    private final CbrClient cbrClient;
    private final ValuteRepository valuteRepository;
    private final UserRepository userRepository;
    private final AssistantBotHistoryRepository botHistoryRepository;

    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public void saveUser(User user) {
        if (userRepository.findById(user.getId()).isEmpty()) {
            userRepository.save(user);
        }
    }

    @Override
    public void saveUserHistory(AssistantBotHistory assistantBotHistory) {
        botHistoryRepository.save(assistantBotHistory);
    }

    @Override
    public String getExchangeRates() {
        List<Valute> exchangeRates = valuteRepository.findAllByIdDate(LocalDate.now());
        // если в БД за текущий день нет записей, то обратимся в Центробанк и получим курсы валют на текущий момент
        if (exchangeRates.isEmpty()) {
            String xml = cbrClient.getCurrentRatesXml();
            var valuteMap = extractCurrencyValuesFromXML(xml);
            exchangeRates = valuteRepository.saveAll(valuteMap.values()); // сохраняем в БД и наполняем список
        }
        exchangeRates = exchangeRates.stream()
                .sorted(Comparator.comparing(Valute::getName))
                .toList();
        var sb = new StringBuilder();
        sb.append("Курсы валют на ")
                .append(exchangeRates.getFirst().getId().getDate().format(DATE_FORMAT))
                .append(" составляют:\n \n");
        for (Valute exchangeRate : exchangeRates) {
            sb.append(exchangeRate.getName())
                    .append(" ( /").append(exchangeRate.getCharCode()).append("): ")
                    .append(exchangeRate.getValueUnitRate())
                    .append("\n");
        }
        return sb.toString();
    }

    private static Map<CompositeValuteKey, Valute> extractCurrencyValuesFromXML(String xml) throws BotException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
            return ValuteParser.parse(document);
        } catch (XPathExpressionException e) {
            throw new BotException("Не удалось распарсить XML", e);
        }
    }

}

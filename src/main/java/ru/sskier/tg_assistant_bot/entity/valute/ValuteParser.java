package ru.sskier.tg_assistant_bot.entity.valute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ValuteParser {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static Map<CompositeValuteKey, Valute> parse(Document doc) {
        Map<CompositeValuteKey, Valute> valuteMap = new HashMap<>();

        Element root = doc.getDocumentElement(); // <ValCurs>
        LocalDate date = LocalDate.parse(root.getAttribute("Date"), formatter); // daily exchange rates date
        NodeList valuteNodes = root.getElementsByTagName("Valute");

        for (int i = 0; i < valuteNodes.getLength(); i++) {
            Element valuteElem = (Element) valuteNodes.item(i);

            String id = valuteElem.getAttribute("ID");
            String numCodeStr = getChildTextContent(valuteElem, "NumCode");
            String charCode = getChildTextContent(valuteElem, "CharCode");
            String name = getChildTextContent(valuteElem, "Name");
            String vunitRateStr = getChildTextContent(valuteElem, "VunitRate");

            Valute valute = new Valute();
            var valuteKey = new CompositeValuteKey(id, date);
            valute.setId(valuteKey);
            valute.setCharCode(charCode);
            valute.setName(name);
            if (numCodeStr != null && !numCodeStr.isEmpty()) {
                valute.setNumCode(Short.parseShort(numCodeStr));
            }
            if (vunitRateStr != null && !vunitRateStr.isEmpty()) {
                valute.setValueUnitRate(new BigDecimal(vunitRateStr.replace(',', '.'))
                        .setScale(4, RoundingMode.UP));
            }
            valuteMap.put(valuteKey, valute);
        }
        return valuteMap;
    }

    // Вспомогательный метод для получения текста дочернего элемента по имени
    private static String getChildTextContent(Element parent, String childName) {
        NodeList list = parent.getElementsByTagName(childName);
        if (list.getLength() == 0) return null;
        return list.item(0).getTextContent();
    }
}



package com.dkartik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static Document getPage() throws IOException {
        String url = "https://pogoda.spb.ru/";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }

    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private static String getDateFromString(String stringDate) {
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()) {
            return matcher.group();
        }
        try {
            throw new Exception("Can't extract date from string! ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int printFourValues(Elements values, int index) {
        int iterationCount = 4;
        if (index == 0) {
            Element valueLine = values.get(3);
            boolean isMorning = valueLine.text().contains("Утро");
            if (isMorning) {
                iterationCount = 3;
            }
        }

        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);
            Elements tds = valueLine.select("td");

            System.out.printf("%-20s %-50s %-20s %-15s\n",
                    tds.get(0).text(),
                    tds.get(1).text(),
                    tds.get(2).text(),
                    tds.get(3).text());
        }
        return iterationCount;
    }

    public static void main(String[] args) throws IOException {
        Document page = getPage();
        // css query language
        Element tableWthr = page.select("table[class=wt]").first();
        Elements names = tableWthr.select("tr[class=wth]");
        Elements values = tableWthr.select("tr[valign=top]");
        int index = 0;
        System.out.printf("%-10s %-20s %-50s %-20s %-15s\n",
                "Дата", "Время суток", "Явления", "Температура", "Давление");
        System.out.println("------------------------------------------------------------------------------------------------------------");
        for (Element name : names) {
            String dateString = name.select("th[id=dt]").text();
            String date = getDateFromString(dateString);
            System.out.printf("%-10s\n", date);
            int iterationCount = printFourValues(values, index);
            index = index + iterationCount;
            System.out.println("------------------------------------------------------------------------------------------------------------");
        }
    }
}

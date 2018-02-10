package jp.gr.java_conf.ya.holidayswidget; // Copyright (c) 2018 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvUtil {

    private final static SimpleDateFormat sdFormatLoad = new SimpleDateFormat("yyyy-MM-dd");

    public static List<ListItem> parse(final String csvString) {
        List<ListItem> result = new ArrayList<>();

        try {
            final InputStream inputStream = new ByteArrayInputStream(csvString.getBytes("UTF-8"));
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;
            int i = -1;
            while ((line = bufferReader.readLine()) != null) {
                i++;

                if (i == 0)
                    continue;

                final ListItem item = new ListItem();
                final String[] lineArray = line.split(",");

                try {
                    final Date date = sdFormatLoad.parse(lineArray[0]);
                    item.setDate(date);
                    item.setName(lineArray[1]);
                    result.add(item);
                } catch (Exception e) {
                }
            }
            bufferReader.close();

            return result;
        } catch (IOException e) {
        }

        return null;
    }
}

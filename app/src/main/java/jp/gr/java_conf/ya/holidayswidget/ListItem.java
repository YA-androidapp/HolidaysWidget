package jp.gr.java_conf.ya.holidayswidget; // Copyright (c) 2018 YA <ya.androidapp@gmail.com> All rights reserved.

import java.text.SimpleDateFormat;
import java.util.Date;

public class ListItem {
    Date date;
    public static final SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd (E)");
    String name;

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return sdFormat.format(date) + " " + name;
    }
}

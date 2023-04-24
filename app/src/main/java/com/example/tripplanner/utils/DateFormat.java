package com.example.tripplanner.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat {


    public DateFormat() {
    }

    public String getDateOnly(Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = format.format(date);

        try {
            Date dateOnly = format.parse(formattedDate); // parse formatted string back into Date object
            return format.format(dateOnly);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Date getDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return dateFormat.parse(dateString);
    }
}

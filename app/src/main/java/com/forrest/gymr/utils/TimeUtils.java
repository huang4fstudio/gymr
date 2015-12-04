package com.forrest.gymr.utils;

import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;

/**
 * Created by Forrest on 19/11/15.
 */
public class TimeUtils {

    /**
     * Compares the Last Configured Time with the current time and returns whether it is more than or equal to a day ago.
     * @param user - the user to be compared
     * @return boolean value that the whether last configured time is a day ago
     */
    public static boolean checkConfiguredTimeDayAgo(ParseUser user) {
        DateTimeZone timeZone = DateTimeZone.forID("America/Montreal");
        DateTime now = DateTime.now(timeZone);
        DateTime todayStart = now.minusHours(24);
        Date configuredDate = user.getDate("configuredDate");
        return configuredDate == null || (configuredDate.before(todayStart.toDate()));
    }
}

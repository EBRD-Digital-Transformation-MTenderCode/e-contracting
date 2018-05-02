package com.procurement.contracting.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtil {

    public LocalDateTime getNowUTC() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    public Date localToDate(final LocalDateTime dateTime) {
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }

    public LocalDateTime dateToLocal(final Date dateTime) {
        return dateTime.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public long getMilliUTC(final LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }
}

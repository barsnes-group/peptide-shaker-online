package com.uib.web.peptideshaker.utils;

import io.netty.channel.local.LocalAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This class provide a common utilities
 *
 * @author Yehia Mokhtar Farag
 */
public class CoreUtils {

    private final DateFormat dateFormater;

    public CoreUtils() {
        dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    }

    public DateFormat getDateFormater() {
        return dateFormater;
    }

}

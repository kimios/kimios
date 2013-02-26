package org.kimios.kernel.converter.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Wrap the file game generation
 */
public class FileNameGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public static String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

}
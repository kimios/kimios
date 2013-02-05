/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.user.impl.factory.joomla;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class Joomla15PasswordHash
{
    public static boolean check(String passwd, String dbEntry)
    {
        if (passwd == null || dbEntry == null || dbEntry.length() == 0) {
            throw new IllegalArgumentException();
        }
        String[] arr = dbEntry.split(":", 2);
        if (arr.length == 2) {
            // new format as {HASH}:{SALT}
            String cryptpass = arr[0];
            String salt = arr[1];

            return md5(passwd + salt).equals(cryptpass);
        } else {
            // old format as {HASH} just like PHPbb and many other apps
            String cryptpass = dbEntry;

            return md5(passwd).equals(cryptpass);
        }
    }

    static java.util.Random _rnd;

    public static String create(String passwd)
    {
        StringBuffer saltBuf = new StringBuffer();
        synchronized (Joomla15PasswordHash.class) {
            if (_rnd == null) {
                _rnd = new SecureRandom();
            }
            int i;
            for (i = 0; i < 32; i++) {
                saltBuf.append(Integer.toString(_rnd.nextInt(36), 36));
            }
        }
        String salt = saltBuf.toString();

        return md5(passwd + salt) + ":" + salt;
    }

    /**
     * Takes the MD5 hash of a sequence of ASCII or LATIN1 characters, and returns it as a 32-character lowercase hex
     * string.
     *
     * Equivalent to MySQL's MD5() function and to perl's Digest::MD5::md5_hex(), and to PHP's md5().
     *
     * Does no error-checking of the input, but only uses the low 8 bits from each input character.
     */
    private static String md5(String data)
    {
        byte[] bdata = new byte[data.length()];
        int i;
        byte[] hash;

        for (i = 0; i < data.length(); i++) {
            bdata[i] = (byte) (data.charAt(i) & 0xff);
        }

        try {
            MessageDigest md5er = MessageDigest.getInstance("MD5");
            hash = md5er.digest(bdata);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        StringBuffer r = new StringBuffer(32);
        for (i = 0; i < hash.length; i++) {
            String x = Integer.toHexString(hash[i] & 0xff);
            if (x.length() < 2) {
                r.append("0");
            }
            r.append(x);
        }
        return r.toString();
    }
}



/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * This class provides methods for message hashing with hash algorithms such as SHA-1, MD5 and so on.
 */
public class HashCalculator
{
    private MessageDigest algorithm;

    /**
     * SHA-1 algorithm.
     */
    public static final String SHA1 = "SHA-1";

    /**
     * SHA-256 algorithm.
     */
    public static final String SHA256 = "SHA-256";

    /**
     * SHA-384 algorithm.
     */
    public static final String SHA384 = "SHA-384";

    /**
     * SHA-512 algorithm.
     */
    public static final String SHA512 = "SHA-512";

    /**
     * MD2 algorithm.
     */
    public static final String MD2 = "MD2";

    /**
     * MD5 algorithm.
     */
    public static final String MD5 = "MD5";

    /**
     * Creates a Hasher with a default algorithm. {@link org.umh.security.Hasher#SHA256}.
     */
    public HashCalculator()
    {
        try {
            this.algorithm = MessageDigest.getInstance(SHA256);
        } catch (NoSuchAlgorithmException ex) //never thrown
        {
            ex.printStackTrace();
        }
    }

    /**
     * Create a Hasher that will uses the specified algorithm.
     *
     * @param algorithm the algorithm
     */
    public HashCalculator(MessageDigest algorithm)
    {
        this.algorithm = algorithm;
    }

    /**
     * Create a Hasher that will uses the specified algorithm.
     *
     * @param algorithm the algorithm to use.
     * @throws java.security.NoSuchAlgorithmException if the specified algorithm is not a valid algorithm for message
     * hashing.
     */
    public HashCalculator(String algorithm) throws NoSuchAlgorithmException
    {
        this.algorithm = MessageDigest.getInstance(algorithm);
    }

    /**
     * Set the algorithm of the hasher.
     *
     * @param algorithm the algorithm to set.
     */
    public void setAlgorithm(MessageDigest algorithm)
    {
        this.algorithm = algorithm;
    }

    /**
     * Set the algorithm of the hasher.
     *
     * @param algorithm the algorithm to set.
     * @throws java.security.NoSuchAlgorithmException if the specified algorithm is not a valid algorithm for message
     * hashing.
     */
    public void setAlgorithm(String algorithm) throws NoSuchAlgorithmException
    {
        setAlgorithm(MessageDigest.getInstance(algorithm));
    }

    /**
     * Returns the algorithm.
     *
     * @return the algorithm
     */
    public MessageDigest getAlgorithm()
    {
        return algorithm;
    }

    /**
     * Hashes the file denoted by the specified location to a bytes array.
     *
     * @param pathname file location.
     * @return the hash as a bytes array.
     * @throws java.io.FileNotFoundException if the file to hash doesn't exist.
     * @throws java.io.IOException if an error occurs during reading the file to hash.
     */
    public byte[] hash(String pathname)
            throws FileNotFoundException, IOException
    {
        return hash(new File(pathname));
    }

    /**
     * Hashes the specified file to a bytes array.
     *
     * @param file the file to hash.
     * @return the hash as a bytes array .
     * @throws java.io.FileNotFoundException if the file to hash doesn't exist.
     * @throws java.io.IOException if an error occurs during reading the file to hash.
     */
    public byte[] hash(File file) throws FileNotFoundException, IOException
    {
        FileInputStream fis = new FileInputStream(file);
        byte[] hash = null;
        try {
            hash = hash(fis);
        } finally {
            fis.close();
        }
        return hash;
    }

    /**
     * Hashes the specified input stream.
     *
     * @param is the input stream to hash
     * @return the hash as a bytes array .
     * @throws java.io.IOException if an error occurs during reading the stream to hash.
     */
    public byte[] hash(InputStream is) throws IOException
    {
        BufferedInputStream bis = new BufferedInputStream(is);
        algorithm.reset();
        byte[] data = new byte[2048];
        int nbRead = 0;
        while ((nbRead = bis.read(data)) > 0) {
            algorithm.update(data, 0, nbRead);
        }
        bis.close();
        bis = null;
        return algorithm.digest();
    }

    /**
     * Hashes the specified datas under bytes array format.
     *
     * @param datas the datas to hash.
     * @return the hash under bytes array format.
     */
    public byte[] hash(byte[] datas)
    {
        algorithm.reset();
        algorithm.update(datas);
        return algorithm.digest();
    }

    /**
     * Hashes the specified chars array.
     *
     * @param chars the chars to hash.
     * @return the hash under bytes array format.
     */
    public byte[] hash(char[] chars)
    {
        byte[] bytes = toByteArray(chars);
        return hash(bytes);
    }

    /**
     * Hashes the file denoted by the specified location to a String.
     *
     * @param pathname file location.
     * @return the hash as a String.
     * @throws java.io.FileNotFoundException if the file to hash doesn't exist.
     * @throws java.io.IOException if an error occurs during reading the file to hash.
     */
    public String hashToString(String pathname)
            throws FileNotFoundException, IOException
    {
        return buildHexaString(hash(pathname));
    }

    /**
     * Hashes the specified file to a String.
     *
     * @param file the file to hash.
     * @return the hash as a String.
     * @throws java.io.FileNotFoundException if the file to hash doesn't exist.
     * @throws java.io.IOException if an error occurs during reading the file to hash.
     */
    public String hashToString(File file)
            throws FileNotFoundException, IOException
    {
        return buildHexaString(hash(file));
    }

    /**
     * Hashes the specified input stream to a String.
     *
     * @param is the input stream to hash.
     * @return the hash as a String.
     * @throws java.io.IOException if an error occurs during reading the file to hash.
     */
    public String hashToString(InputStream is)
            throws IOException
    {
        return buildHexaString(hash(is));
    }

    /**
     * Hashes the specified datas under bytes array format and returns the hash as an hexadecimal String.
     *
     * @param datas the datas to hash.
     * @return the hash as an hexadecimal String.
     */
    public String hashToString(byte[] datas)
    {
        return buildHexaString(hash(datas));
    }

    /**
     * Hashes the specified chars array and returns the hash as an hexadecimal String.
     *
     * @param chars the chars to hash.
     * @return the hash as an hexadecimal String.
     */
    public String hashToString(char[] chars)
    {
        return buildHexaString(hash(chars));
    }

    /**
     * Returns the specified bytes array as an hexadecimal String.
     *
     * @param hash the datas as a bytes array.
     * @return the hash as an hexadecimal String.
     */
    public String buildHexaString(byte[] hash)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            int v = hash[i] & 0xFF;
            if (v < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(v, 16).toUpperCase() + " ");
        }
        return sb.toString();
    }

    /**
     * Converts the specified chars array to a bytes array using the default charset.
     *
     * @param chars the chars to convert.
     * @return the specified chars converted to a bytes array.
     */
    public static byte[] toByteArray(char[] chars)
    {
        return toByteArray(chars, Charset.defaultCharset());
    }

    /**
     * Converts the specified chars array to a bytes array using the charset of the specified name.
     *
     * @param chars the chars to convert.
     * @param charsetName the name of the charset used for chars encoding.
     * @return the specified chars converted to a bytes array.
     */
    public static byte[] toByteArray(char[] chars, String charsetName)
    {
        Charset cs = Charset.forName(charsetName);
        return toByteArray(chars, cs);
    }

    /**
     * Converts the specified chars array to a bytes array using the specified charset.
     *
     * @param chars the chars to convert.
     * @param charset the charset used for chars encoding.
     * @return the specified chars converted to a bytes array.
     */
    public static byte[] toByteArray(char[] chars, Charset charset)
    {
        CharBuffer cb = CharBuffer.wrap(chars);
        ByteBuffer bb = charset.encode(cb);
        byte[] bytes = bb.array();
        byte[] result = HashCalculator.copy(bytes, bb.limit());//modified by LS (1.5 compliant)
        Arrays.fill(bytes, (byte) 0);
        return result;
    }

    private static byte[] copy(byte[] bytes, int newLength)
    {
        byte[] r = new byte[newLength];
        for (int i = 0; i < newLength; i++) {
            r[i] = bytes[i];
        }
        return r;
    }
}
 

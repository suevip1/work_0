package com.dtstack.engine.common.util;

import com.dtstack.engine.common.exception.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-07-31
 * Time: 10:32
 */
public class MD5Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(MD5Util.class);

    /**
     * 获得字符串的md5值
     *
     * @param str 待加密的字符串
     * @return md5加密后的字符串
     */
    public static String getMd5String(String str) {
        byte[] bytes = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            bytes = md5.digest(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            LOGGER.error("MD5Util.getMd5String error:", e);
        }
        return HexUtil.bytes2Hex(bytes);

    }



    /**
     * 获得字符串的md5大写值
     *
     * @param str 待加密的字符串
     * @return md5加密后的大写字符串
     */
    public static String getMd5UpperString(String str) {
        return getMd5String(str).toUpperCase();
    }

    /**
     * 获得文件的md5值
     *
     * @param file 文件对象
     * @return 文件的md5
     */
    public static String getFileMd5String(File file) {
        String ret = "";
        FileInputStream in = null;
        FileChannel ch = null;
        try {
            in = new FileInputStream(file);
            ch = in.getChannel();
            ByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            ret = HexUtil.bytes2Hex(md5.digest());
        } catch (IOException |NoSuchAlgorithmException e) {
            LOGGER.error("MD5Util.getFileMd5String error:", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("MD5Util.getFileMd5String error:", e);
                }
            }
            if (ch != null) {
                try {
                    ch.close();
                } catch (IOException e) {
                    LOGGER.error("MD5Util.getFileMd5String error:", e);
                }
            }
        }
        return ret;
    }

    /**
     * 获得文件的md5值
     *
     * @param files 文件对象
     * @return 文件的md5
     */
    public static String getMd5StringFromFiles(File... files) {
        MessageDigest md5 = getMessageDigest("MD5");
        byte[] buffer = new byte[1024];
        for (File file : files) {
            update(md5, file, buffer);
        }
        return HexUtil.bytes2Hex(md5.digest());
    }

    private static MessageDigest getMessageDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No " + algorithm, e);
        }
    }

    private static void update(MessageDigest digest, File file, byte[] buffer) {
        try (InputStream input = openFile(file)) {
            int num = input.read(buffer);
            while (num != -1) {
                digest.update(buffer, 0, num);
                num = input.read(buffer);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Read error from file " + file.getAbsolutePath(), e);
        }
    }

    private static InputStream openFile(File file) {
        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " not found.", e);
        }
    }

}
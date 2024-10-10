package com.dtstack.engine.common.exception;

import com.dtstack.engine.common.io.UnsafeStringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

/**
 * Date: 2016年11月31日 下午1:26:07
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class ExceptionUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil.class);

    /**
     * 获取错误的堆栈信息
     *
     * @param e throwable
     * @return 堆栈信息
     */
    public static String getErrorMessage(Throwable e) {
        UnsafeStringWriter w = null;
        PrintWriter printWriter = null;
        try {
            w = new UnsafeStringWriter();
            printWriter = new PrintWriter(w);
            e.printStackTrace(printWriter);
        } finally {
            if (w != null) {
                w.close();
            }
            if (printWriter != null) {
                printWriter.close();
            }
        }
        return w.toString();
    }

    public static String stackTrack() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        StringBuffer mBuffer = new StringBuffer();
        mBuffer.append(System.getProperty("line.separator"));

        for (StackTraceElement e : st) {
            if (mBuffer.length() > 0) {
                mBuffer.append("  ");
                mBuffer.append(System.getProperty("line.separator"));
            }
            mBuffer.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , e.getClassName()
                    , e.getMethodName()
                    , e.getLineNumber()));
        }
        return mBuffer.toString();
    }
}

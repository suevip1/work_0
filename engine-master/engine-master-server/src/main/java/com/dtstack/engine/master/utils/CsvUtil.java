package com.dtstack.engine.master.utils;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrinter;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;


public class CsvUtil {

    // CSV 文件默认分隔符
    private static final char DELIMITER_DEFAULT = ',';

    // 上传文件限制大小，默认 1M
    private static final Long LIMIT_SIZE = 1024 * 1024L;

    // 上传文件限制条数，默认 1000条
    private static final Long LIMIT_LINE = 1000L;

    /**
     * 从csv文件中读取数据
     *
     * @param path    文件路径
     * @param start   开始行
     * @param readNum 读取多少行
     * @return 读出来的数据
     */
    public static List<List<String>> readFromCsv(String path, Integer start, Integer readNum) {
        return readFromCsv(path, start, readNum, DELIMITER_DEFAULT,null);
    }
    /**
     * 从csv文件中读取数据
     *
     * @param path    文件路径
     * @param start   开始行
     * @param readNum 读取多少行
     * @return 读出来的数据
     */
    public static List<List<String>> readFromCsv(String path, Integer start, Integer readNum, CsvChecker csvChecker) {
        return readFromCsv(path, start, readNum, DELIMITER_DEFAULT,csvChecker);
    }

    /**
     * 从csv文件中读取数据
     *
     * @param path      文件路径
     * @param start     开始行
     * @param readNum   读取多少行
     * @param delimiter 列分隔符
     * @return 读出来的数据
     */
    public static List<List<String>> readFromCsv(String path, Integer start, Integer readNum, char delimiter,CsvChecker csvChecker) {
        checkFileExists(path);
        // 读取的结果数据
        List<List<String>> result = Lists.newArrayList();
        // 从哪一行开始读取，小于1行从第一行读取，currentLineNum = 0 即为第一行
        int startLine = start <= 1 ? 1 : start;
        // 文件结束行号 算上标题行
        int engLine = startLine + readNum - 1;
        try (FileInputStream in = new FileInputStream(path)) {
            CSVParser csvParser = new CSVParser(in);
            csvParser.changeDelimiter(delimiter);
            // 当前行数据
            String[] currentLineData = csvParser.getLine();
            // 当前行号
            int currentLineNum = csvParser.getLastLineNumber();
            while (ArrayUtils.isNotEmpty(currentLineData)) {
                // 当前行大于结束行时候不再读取
                if (currentLineNum > engLine) {
                    break;
                }
                if (currentLineNum >= startLine) {
                    if(null != csvChecker){
                        csvChecker.checkData(currentLineData,result);
                    }
                    result.add(Lists.newArrayList(currentLineData));
                }
                currentLineData = csvParser.getLine();
                currentLineNum = csvParser.getLastLineNumber();
            }
        } catch (Exception e) {
            if(e instanceof RdosDefineException){
                throw (RdosDefineException)e;
            }
            throw new RdosDefineException(String.format("文件路径：%s 读取异常：%s", path, e.getMessage()));
        }
        return result;
    }

    /**
     * 写入csv 文件
     *
     * @param path   文件路径
     * @param header csv 标题行信息
     * @param values 写入数据
     * @param append 是否追加
     */
    public static void writerCsv(String path, String[] header, List<String[]> values, Boolean append) {
        writerCsv(path, header, values, append, DELIMITER_DEFAULT);
    }

    /**
     * 写入csv 文件
     *
     * @param path      文件路径
     * @param header    csv 标题行信息
     * @param values    写入数据
     * @param append    是否追加
     * @param delimiter 列分隔符
     */
    public static void writerCsv(String path, String[] header, List<String[]> values, Boolean append, char delimiter) {
        try (FileWriter out = new FileWriter(path, append)) {
            CSVPrinter csvPrinter = new CSVPrinter(out);
            csvPrinter.changeDelimiter(delimiter);
            // 不采用追加的方式如果标题行存在需要写入标题行信息
            if (!append) {
                if (ArrayUtils.isNotEmpty(header)) {
                    csvPrinter.writeln(header);
                }
            }
            if (CollectionUtils.isNotEmpty(values)) {
                for (String[] value : values) {
                    if (ArrayUtils.isNotEmpty(value)) {
                        csvPrinter.writeln(value);
                    }
                }
            }
        } catch (Exception e) {
            throw new RdosDefineException(String.format("csv文件写入异常：%s", e.getMessage()), e);
        }
    }

    /**
     * 获取csv文件行数
     *
     * @param path 文件路径
     */
    public static Long countLine(String path) {
        checkFileExists(path);
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            // 返回csv文件行数，忽略空行
            return lines.filter(StringUtils::isNotBlank).count();
        } catch (Exception e) {
            throw new RdosDefineException(String.format("csv文件读取异常：%s", e.getMessage()), e);
        }
    }

    /**
     * 检查csv 文件大小和条数
     *
     * @param path csv 本地路径
     */
    public static void checkSizeAndLine(String path) {
        checkSizeAndLine(path, LIMIT_SIZE, LIMIT_LINE);
    }

    /**
     * 检查csv 文件大小和条数
     *
     * @param path      csv 文件本地路径
     * @param limitSize 限制大小
     * @param limitLine 限制条数
     */
    public static void checkSizeAndLine(String path, Long limitSize, Long limitLine) {
        checkFileExists(path);
        StringBuilder errMsg = new StringBuilder();
        File csvFile = new File(path);
        // 文件大小
        long fileSize = csvFile.length();
        if (fileSize > limitSize) {
            errMsg.append(String.format("数据文件大小应该小于%s, 当前大小：%s \n", covertByteToMb(limitSize), covertByteToMb(fileSize)));
        }
        // 数据条数
        Long fileLine = countLine(path);
        if (fileLine > limitLine) {
            errMsg.append(String.format("数据文件条数应小于%s, 当前条数：%s \n", limitLine, fileLine));
        }
        if (StringUtils.isNotBlank(errMsg.toString())) {
            errMsg.append("请重新上传");
            throw new RdosDefineException(errMsg.toString());
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param path 文件路径
     */
    private static void checkFileExists(String path) {
        File filePath = new File(path);
        if (!filePath.exists()) {
            throw new RdosDefineException(String.format("文件不存在：%s", path));
        }
    }

    /**
     * 转换byte 为 MB
     *
     * @param val 字节单位
     * @return MB单位大小
     */
    private static String covertByteToMb(long val) {
        double mbVal = BigDecimal
                .valueOf((double) val / (1024 * 1024))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        return mbVal + "MB";
    }
}



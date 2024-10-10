package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/7/20
 */
public class Xml2JsonUtil {

    private final static String PROPERTY = "property";

    /**
     * xml转map
     */
    public static Map<String, Object> xml2map(File xmlFile) throws Exception {
        JSONObject json = xml2Json(xmlFile);
        if (json.containsKey(PROPERTY)) {
            Object o = json.get(PROPERTY);
            JSONArray jsona;
            if (o instanceof JSONObject) {
                JSONObject jsono = (JSONObject) o;
                jsona = new JSONArray();
                jsona.add(jsono);
            } else if (o instanceof JSONArray) {
                jsona = (JSONArray) o;
            } else {
                return Collections.emptyMap();
            }
            Map<String, Object> map = new HashMap<>(jsona.size());
            for (Object obj : jsona) {
                Map subMap = (Map) obj;
                Object name = subMap.get("name");
                if (name == null || StringUtils.isBlank(name.toString())) {
                    // 忽略脏数据
                    continue;
                }
                map.put(name.toString(), subMap.get("value"));
            }
            return map;
        }
        return Collections.emptyMap();
    }

    /**
     * xml转json
     *
     * @throws DocumentException
     */
    public static JSONObject xml2Json(File xmlFile) throws Exception {
        String xmlStr = readFile(xmlFile);
        Document doc = DocumentHelper.parseText(xmlStr);
        JSONObject json = new JSONObject();
        dom4j2Json(doc.getRootElement(), json);
        return json;
    }

    public static String readFile(File file) throws IOException {
        String str = null;
        FileChannel fc = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fc = fis.getChannel();
            ByteBuffer bb = ByteBuffer.allocate(new Long(file.length()).intValue());
            fc.read(bb);
            bb.flip();
            str = new String(bb.array(), "UTF8");
        } catch (IOException e) {
            throw new IOException("读取文件失败");
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
            if (null != fc) {
                try {
                    fc.close();
                } catch (IOException e) {
                }
            }
        }
        return str;
    }

    /**
     * xml转json
     *
     * @param element
     * @param json
     */
    private static void dom4j2Json(Element element, JSONObject json) {
        //如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (!isEmpty(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && !isEmpty(element.getText())) {//如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for (Element e : chdEl) {//有子元素
            if (!e.elements().isEmpty()) {//子元素也有子元素
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {//如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                        json.put(e.getName(), jsona);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }


            } else {//子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (!isEmpty(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty() && !json.containsKey(e.getName())) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

    private static boolean isEmpty(String str) {

        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * 添加配置信息到对于的xml文件中
     *
     * @param xmlFile
     * @throws Exception
     */
    public static void operatorXml(File xmlFile, BiConsumer<Element, List<String>> operatorConsumer) throws Exception {
        if(null == operatorConsumer){
            return;
        }
        SAXReader reader = new SAXReader();
        Document read = reader.read(xmlFile);
        //得到根节点
        Element root = read.getRootElement();
        List<String> keys = parseAllKeyNameFromXml(root);
        operatorConsumer.accept(root, keys);
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("UTF-8");
        XMLWriter xmlWriter = new XMLWriter(new FileWriter(xmlFile), outputFormat);
        xmlWriter.write(read);
        xmlWriter.close();
    }

    public static List<String> parseAllKeyNameFromXml(Element root) {
        List<String> keys = new ArrayList<>();
        List propertys = root.elements("property");
        for (Object o : propertys) {
            Element e = (Element) o;
            if (CollectionUtils.isNotEmpty(e.elements())) {
                for (Object element : e.elements()) {
                    if (element instanceof Element) {
                        QName qName = ((DefaultElement) element).getQName();
                        if ("name".equalsIgnoreCase(qName.getName())) {
                            keys.add(((Element) element).getText());
                        }
                    }
                }
            }
        }
        return keys;
    }

    public static void removePropertyNode(Element root, String key) {
        List propertysCopy = root.elements("property");
        // 先删同名元素
        for (Object o : propertysCopy) {
            Element e = (Element) o;
            if (CollectionUtils.isNotEmpty(e.elements())) {
                for (Object element : e.elements()) {
                    if (element instanceof Element) {
                        QName qName = ((DefaultElement) element).getQName();
                        if ("name".equalsIgnoreCase(qName.getName())) {
                            String nameKey = ((Element) element).getText();
                            if (key.equals(nameKey)) {
                                root.remove(e);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void appendPropertyNode(Element root, Map<String, Object> extraConfig) {
        if (MapUtils.isEmpty(extraConfig)) {
            return;
        }
        Element property = root.addElement("property");
        Element name = property.addElement("name");
        for (String key : extraConfig.keySet()) {
            name.setText(key);
            Element value = property.addElement("value");
            value.setText((String) extraConfig.get(key));
        }
    }
}

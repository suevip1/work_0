package com.dtstack.engine.master;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


@RunWith(DAGSpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EngineApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Ignore
public class BaseTest {

    static final List<Long> LONG_LIST = new ArrayList<>();
    static final List<String> STRING_LIST = new ArrayList<>();
    static final List<Integer> INTEGER_LIST = new ArrayList<>();
    static final Long DEFAULT_LONG = 1L;
    static final String DEFAULT_STRING = "1";


    public static List<Long> getLongList() {
        return LONG_LIST;
    }

    public static List<String> getStringList() {
        return STRING_LIST;
    }

    public static List<Integer> getIntegerList() {
        return INTEGER_LIST;
    }

    public static Long getDefaultLong() {
        return DEFAULT_LONG;
    }

    public static String getDefaultString() {
        return DEFAULT_STRING;
    }

    @BeforeClass
    public static void setUp1() {
        LONG_LIST.add(1L);
        STRING_LIST.add("1");
        INTEGER_LIST.add(1);
    }

    public <T> T getProxy(Object targetObject, Class<T> classToMock) {
        return getProxy(targetObject, classToMock, null);
    }

    public <T> T getProxy(Object targetObject, Class<T> classToMock, String name) {
        if (StringUtils.isBlank(name)) {
            name = toLowerCaseFirstOne(classToMock.getSimpleName());
        }
        T mock = Mockito.mock(classToMock);
        ReflectionTestUtils.setField(targetObject, name, mock);
        return mock;
    }


    public static <T> T assignment(Class<T> tClass) {
        Field[] fields = getAllDeclaredFields(tClass);
        T t = null;
        try {
            t = tClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < fields.length; i++) {
            // 跳过静态属性
            String mod = Modifier.toString(fields[i].getModifiers());
            if (mod.indexOf("static") != -1)
                continue;
            // 得到属性的类名
            String className = fields[i].getType().getSimpleName();
            // 得到属性值
            if (className.equalsIgnoreCase("String")) {
                ReflectionTestUtils.setField(t, fields[i].getName(), "test");
            } else if (className.equalsIgnoreCase("boolean")) {
                ReflectionTestUtils.setField(t, fields[i].getName(), true);
            } else if (className.equalsIgnoreCase("int") || className.equalsIgnoreCase("Integer")) {
                ReflectionTestUtils.setField(t, fields[i].getName(), 0);
            } else if (className.equalsIgnoreCase("long")) {
                ReflectionTestUtils.setField(t, fields[i].getName(), 0L);
            } else if (className.equalsIgnoreCase("list")) {
                String typeName = fields[i].getGenericType().getTypeName();
                if (typeName.contains("Integer")) {
                    List<Integer> list = new ArrayList();
                    list.add(1);
                    ReflectionTestUtils.setField(t, fields[i].getName(), list);
                } else if (typeName.contains("String")) {
                    List<String> list = new ArrayList();
                    list.add("1");
                    ReflectionTestUtils.setField(t, fields[i].getName(), list);
                } else if (typeName.contains("Long")) {
                    List<Long> list = new ArrayList();
                    list.add(1L);
                    ReflectionTestUtils.setField(t, fields[i].getName(), list);
                }
            }
        }
        return t;
    }


    /**
     * 获取类的所有字段
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Field[] getAllDeclaredFields(Class<T> clazz) {

        List<Field[]> fieldArrayList = new ArrayList<Field[]>();

        while (clazz != null) {

            fieldArrayList.add(clazz.getDeclaredFields());

            clazz = (Class<T>) clazz.getSuperclass();
        }

        int fieldCount = 0;
        int fieldIndex = 0;
        for (Field[] fieldArray : fieldArrayList) {

            fieldCount = fieldCount + fieldArray.length;
        }

        Field[] allFields = new Field[fieldCount];

        for (Field[] fieldArray : fieldArrayList) {

            for (Field field : fieldArray) {

                allFields[fieldIndex++] = field;
            }
        }
        return allFields;
    }

    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}

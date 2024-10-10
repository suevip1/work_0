package com.dtstack.engine.master.vo;

/**
 * @author yuebai
 * @date 2022/7/18
 */

import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;


public class AllVOTest {

    List<String> voPackage = Lists.newArrayList("com.dtstack.engine.master.vo",
            "com.dtstack.engine.master.dto",
            "com.dtstack.engine.master.bo",
            "com.dtstack.engine.master.queue",
            "com.dtstack.engine.master.scheduler",
            "com.dtstack.engine.dao",
            "com.dtstack.engine.po",
            "com.dtstack.engine.common",
            "com.dtstack.schedule.common",
            "com.dtstack.engine.api"
    );

    @Test
    public void vo() throws Exception {
        String property = System.getProperty("user.dir");
        Set<Class<?>> classes = loadClasses(property + "/target/classes");
        classes.forEach(classe -> {
            try {
                assignment(classe);
            } catch (Exception e) {
            }
        });
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

    public static <T> T assignment(Class<T> tClass) throws Exception {
        Field[] fields = getAllDeclaredFields(tClass);
        T t = tClass.newInstance();
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
            ReflectionTestUtils.getField(t, fields[i].getName());
        }
        return t;
    }

    public static Set<Class<?>> loadClasses(String rootClassPath) throws Exception {
        Set<Class<?>> classSet = Sets.newHashSet();
        // 设置class文件所在根路径
        File clazzPath = new File(rootClassPath);

        // 记录加载.class文件的数量
        int clazzCount = 0;

        if (clazzPath.exists() && clazzPath.isDirectory()) {
            // 获取路径长度
            int clazzPathLen = clazzPath.getAbsolutePath().length() + 1;

            Stack<File> stack = new Stack<>();
            stack.push(clazzPath);

            // 遍历类路径
            while (!stack.isEmpty()) {
                File path = stack.pop();
                File[] classFiles = path.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        //只加载class文件
                        return pathname.isDirectory() || pathname.getName().endsWith(".class");
                    }
                });
                if (classFiles == null) {
                    break;
                }
                for (File subFile : classFiles) {
                    if (subFile.isDirectory()) {
                        stack.push(subFile);
                    } else {
                        if (clazzCount++ == 0) {
                            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                            boolean accessible = method.isAccessible();
                            try {
                                if (!accessible) {
                                    method.setAccessible(true);
                                }
                                // 设置类加载器
                                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                                // 将当前类路径加入到类加载器中
                                method.invoke(classLoader, clazzPath.toURI().toURL());
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                method.setAccessible(accessible);
                            }
                        }
                        // 文件名称
                        String className = subFile.getAbsolutePath();
                        className = className.substring(clazzPathLen, className.length() - 6);
                        //将/替换成. 得到全路径类名
                        className = className.replace(File.separatorChar, '.');
                        // 加载Class类
                        Class<?> aClass = Class.forName(className);
                        classSet.add(aClass);
                    }
                }
            }
        }
        return classSet;
    }
}


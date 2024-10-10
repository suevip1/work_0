package com.dtstack.engine.enums;

/**
 * @author yuebai
 * @date 2022/7/18
 */

import com.google.common.collect.Sets;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;


public class EnumsTest {

    @Test
    public void enums() throws Exception {
        String property = System.getProperty("user.dir");
        Set<Class<?>> classes = loadClasses(property + "/target/classes");
        classes.forEach(classe -> {
            if (classe.getPackage().getName().equals("com.dtstack.engine.common.enums") ||
                    classe.getPackage().getName().equals("com.dtstack.schedule.common.enums") ||
                    classe.getPackage().getName().equals("com.dtstack.engine.master.enums")
            ) {
                try {
                    assignment(classe);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static <T> void assignment(Class<T> tClass) throws Exception {
        Method[] methods = tClass.getMethods();
        List<Method> getMethod = Arrays.stream(methods).filter(m -> m.getName().startsWith("get")).collect(Collectors.toList());
        for (Method method : methods) {
            if (method.getName().equals("values")) {
                Object[] value = (Object[]) method.invoke(null);
                for (Object o : value) {
                    for (Method getM : getMethod) {
                        Parameter[] parameters = getM.getParameters();
                        Object[] args = Arrays.stream(parameters).map(parameter -> {
                            try {
                                return parameter.getType().newInstance();
                            } catch (Exception e) {
                            }
                            return null;
                        }).toArray();
                        try {
                            getM.invoke(o, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
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


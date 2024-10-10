package com.dtstack.engine.api;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * po test
 *
 * @author ：wangchuan
 * date：Created in 15:50 2022/7/29
 * company: www.dtstack.com
 */
public class POTest {

    /**
     * 要测试的包
     */
    private static final String DTO_PACKAGE_NAME = "com.dtstack.engine.api";

    @Test
    public void poTest() {

        List<Class<?>> allClass = getClasses(DTO_PACKAGE_NAME);
        for (Class<?> classes : allClass) {
            if (StringUtils.containsIgnoreCase(classes.getName(), POTest.class.getSimpleName())) {
                continue;
            }
            // 循环反射执行所有类
            try {
                boolean isAbstract = Modifier.isAbstract(classes.getModifiers());
                if (classes.isInterface() || isAbstract) {
                    // 如果是接口或抽象类,跳过
                    continue;
                }
                Constructor<?>[] constructorArr = classes.getConstructors();
                Object clazzObj = newConstructor(constructorArr, classes);
                fieldTest(classes, clazzObj);
                methodInvoke(classes, clazzObj);
            } catch (Exception e) {
                // ignore error
            }
        }
    }

    private void fieldTest(Class<?> classes, Object clazzObj)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == clazzObj) {
            return;
        }

        Field[] fields = classes.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object fieldGetObj = field.get(clazzObj);
                if (!Modifier.isFinal(field.getModifiers()) || null == fieldGetObj) {
                    field.set(clazzObj, adaptorGenObj(field.getType()));
                }
            }
        }
    }

    private void methodInvoke(Class<?> classes, Object clazzObj)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        String clazzName = classes.getName();
        Method[] methods = classes.getDeclaredMethods();
        if (methods.length > 0) {
            for (Method method : methods) {
                String methodName = method.getName();
                String clazzMethodName = clazzName + "." + methodName;

                // 无论如何，先把权限放开
                method.setAccessible(true);
                Class<?>[] paramClassArrs = method.getParameterTypes();

                // 执行get、set方法
                if (methodName.startsWith("set") && null != clazzObj) {
                    methodInvokeGetSet(classes, clazzObj, method, paramClassArrs, methodName);
                    continue;
                }
                // 如果是静态方法
                if (Modifier.isStatic(method.getModifiers()) && !classes.isEnum()) {
                    if (paramClassArrs.length == 0) {
                        method.invoke(null);
                    } else if (paramClassArrs.length == 1) {
                        System.out.println("clazzMethodName:" + clazzMethodName + "," + classes.isEnum());
                        method.invoke(null, adaptorGenObj(paramClassArrs[0]));
                    } else if (paramClassArrs.length == 2) {
                        method.invoke(null, adaptorGenObj(paramClassArrs[0]), adaptorGenObj(paramClassArrs[1]));
                    } else if (paramClassArrs.length == 3) {
                        method.invoke(null, adaptorGenObj(paramClassArrs[0]), adaptorGenObj(paramClassArrs[1]),
                                adaptorGenObj(paramClassArrs[2]));
                    } else if (paramClassArrs.length == 4) {
                        method.invoke(null, adaptorGenObj(paramClassArrs[0]), adaptorGenObj(paramClassArrs[1]),
                                adaptorGenObj(paramClassArrs[2]), adaptorGenObj(paramClassArrs[3]));
                    }
                    continue;
                }
                if (null == clazzObj) {
                    continue;
                }
                // 如果方法是toString,直接执行
                if ("toString".equals(methodName)) {
                    try {
                        Method toStringMethod = classes.getDeclaredMethod(methodName);
                        toStringMethod.invoke(clazzObj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                // 其他方法
                if (paramClassArrs.length == 0) {
                    method.invoke(clazzObj);
                } else if (paramClassArrs.length == 1) {
                    method.invoke(clazzObj, adaptorGenObj(paramClassArrs[0]));
                } else if (paramClassArrs.length == 2) {
                    method.invoke(clazzObj, adaptorGenObj(paramClassArrs[0]), adaptorGenObj(paramClassArrs[1]));
                } else if (paramClassArrs.length == 3) {
                    method.invoke(clazzObj, adaptorGenObj(paramClassArrs[0]), adaptorGenObj(paramClassArrs[1]),
                            adaptorGenObj(paramClassArrs[2]));
                } else if (paramClassArrs.length == 4) {
                    method.invoke(clazzObj, adaptorGenObj(paramClassArrs[0]), adaptorGenObj(paramClassArrs[1]),
                            adaptorGenObj(paramClassArrs[2]), adaptorGenObj(paramClassArrs[3]));
                }
            }
        }
    }

    private void methodInvokeGetSet(Class<?> classes, Object clazzObj, Method method, Class<?>[] paramClassArrs,
                                    String methodName)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object getObj;
        String methodNameSuffix = methodName.substring(3);
        Method getMethod;
        try {
            getMethod = classes.getDeclaredMethod("get" + methodNameSuffix);
        } catch (NoSuchMethodException e) {
            // 如果对应的get方法找不到,会有is开头的属性名,其get方法就是其属性名称
            char firstChar = methodNameSuffix.charAt(0);// 取出第一个字符转小写
            String firstLowerStr = Character.toString(firstChar).toLowerCase();
            try {
                getMethod = classes.getDeclaredMethod(
                        firstLowerStr + methodNameSuffix.substring(1));
            } catch (NoSuchMethodException e2) {
                try {
                    getMethod = classes.getDeclaredMethod("is" + methodNameSuffix);
                } catch (NoSuchMethodException ex) {
                    // 如果还是空的,就跳过吧
                    return;
                }
            }
        }
        // 如果get返回结果和set参数结果一样,才可以执行,否则不可以执行
        Class<?> returnClass = getMethod.getReturnType();
        if (paramClassArrs.length == 1 && paramClassArrs[0].toString().equals(returnClass.toString())) {
            getObj = getMethod.invoke(clazzObj);
            method.invoke(clazzObj, getObj);
        }

    }

    @SuppressWarnings("rawtypes")
    private Object newConstructor(Constructor[] constructorArr, Class<?> classes)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (null == constructorArr || constructorArr.length < 1) {
            return null;
        }
        Object clazzObj = null;
        boolean isExitNoParamConstruct = false;
        for (Constructor constructor : constructorArr) {
            Class[] constructParamClazzArr = constructor.getParameterTypes();
            if (constructParamClazzArr.length == 0) {
                constructor.setAccessible(true);
                clazzObj = classes.newInstance();
                isExitNoParamConstruct = true;
                break;
            }
        }
        // 没有无参构造取第一个
        if (!isExitNoParamConstruct) {
            boolean isContinueFor = false;
            Class[] constructParamClazzArr = constructorArr[0].getParameterTypes();
            Object[] construParamObjArr = new Object[constructParamClazzArr.length];
            for (int i = 0; i < constructParamClazzArr.length; i++) {
                Class constructParamClazz = constructParamClazzArr[i];
                construParamObjArr[i] = adaptorGenObj(constructParamClazz);
                if (null == construParamObjArr[i]) {
                    isContinueFor = true;
                }
            }
            if (!isContinueFor) {
                clazzObj = constructorArr[0].newInstance(construParamObjArr);
            }
        }
        return clazzObj;
    }

    private Object adaptorGenObj(Class<?> clazz)
            throws IllegalArgumentException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (null == clazz) {
            return null;
        }
        switch (clazz.getName()) {
            case "int":
                return 1;
            case "char":
                return 'x';
            case "boolean":
                return true;
            case "double":
                return 1.0;
            case "float":
                return 1.0f;
            case "long":
                return 1L;
            case "byte":
                return 0xFFFFFFFF;
            case "java.lang.Class":
                return this.getClass();
            case "java.math.BigDecimal":
                return new BigDecimal(1);
            case "java.lang.String":
                return "333";
            case "java.util.Hashtable":
                return new Hashtable<>();
            case "java.util.HashMap":
                return new HashMap<>();
            case "java.util.List":
                return new ArrayList<>();
            default:
                // 如果是接口或抽象类,直接跳过
                boolean paramIsAbstract = Modifier.isAbstract(clazz.getModifiers());
                boolean paramIsInterface = Modifier.isInterface(clazz.getModifiers());
                if (paramIsInterface || paramIsAbstract) {
                    return null;
                }
                Constructor<?>[] constructorArrs = clazz.getConstructors();
                return newConstructor(constructorArrs, clazz);
        }
    }

    private List<Class<?>> getClasses(String packageName) {
        // 第一个class类的集合
        List<Class<?>> classes = new ArrayList<>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
                                                  List<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirFiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // 循环所有文件
        assert dirFiles != null;
        for (File file : dirFiles) {
            // 如果是目录 则递归继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                String pakClazzName = packageName + '.' + className;
                try {
                    // 添加到集合中去
                    classes.add(Class.forName(pakClazzName));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

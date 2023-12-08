package com.xiaohe.common.scanner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-03 22:47
 */
public class ClassScanner {
    private static final String PROTOCOL_FILE = "file";

    private static final String PROTOCOL_JAR = "jar";

    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * 返回指定路径下的所有类全限定名称
     * @param packageName
     * @return
     * @throws Exception
     */
    public static List<String> getClassNameList(String packageName) throws Exception {
        List<String> classNameList = new ArrayList<>();
        // 是否迭代循环
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            // 协议名称，其实就是 file、jar类型
            String protocol = url.getProtocol();
            if (PROTOCOL_FILE.equals(protocol)) {
                // 得到包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // 以文件的形式扫描整个包下的文件，并添加到集合中
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNameList);
            } else if (PROTOCOL_JAR.equals(protocol)) {
                packageName = findAndAddClassesInPackageByJar(packageName, classNameList, recursive, packageDirName, url);

            }
        }
        return classNameList;
    }
    /**
     * 扫描Jar文件中指定包下的所有类信息
     * @param packageName 扫描的包名
     * @param classNameList 完成类名存放的List集合
     * @param recursive 是否递归调用
     * @param packageDirName 当前包名的前面部分的名称
     * @param url 包的url地址
     * @return 处理后的包名，以供下次调用使用
     * @throws IOException
     */
    private static String findAndAddClassesInPackageByJar(String packageName, List<String> classNameList, boolean recursive, String packageDirName, URL url) throws IOException {
        //如果是jar包文件
        //定义一个JarFile
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        //从此jar包 得到一个枚举类
        Enumeration<JarEntry> entries = jar.entries();
        //同样的进行循环迭代
        while (entries.hasMoreElements()) {
            //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            //如果是以/开头的
            if (name.charAt(0) == '/') {
                //获取后面的字符串
                name = name.substring(1);
            }
            //如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                //如果以"/"结尾 是一个包
                if (idx != -1) {
                    //获取包名 把"/"替换成"."
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                //如果可以迭代下去 并且是一个包
                if ((idx != -1) || recursive){
                    //如果是一个.class文件 而且不是目录
                    if (name.endsWith(CLASS_FILE_SUFFIX) && !entry.isDirectory()) {
                        //去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        classNameList.add(packageName + '.' + className);
                    }
                }
            }
        }
        return packageName;
    }

    /**
     * 扫描当前工程中指定包下的所有类信息
     * @param packageName 扫描的包名
     * @param packagePath 包在磁盘上的完整路径
     * @param recursive 是否递归调用
     * @param classNameList 类名称的集合
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<String> classNameList){
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 自定义过滤规则，如果可以循环并且是文件夹，或者以.class结尾的文件就拿出来
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(
                        packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classNameList
                );
            } else {
                // 将.class后缀去掉
                String className = file.getName().substring(0, file.getName().length() - 6);
                classNameList.add(packageName + "." + className);
            }
        }
    }
}

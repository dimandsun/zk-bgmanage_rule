package cn.lxt6.util;

import cn.lxt6.model.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by czy on 2019/5/15.
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger("sys_error");
    private static FileUtil fileUtil = new FileUtil();

    public static FileUtil getInstance() {
        return fileUtil;
    }

    private FileUtil() {
    }

    private static void copy(File file) {
        String url2 = "C:\\Users\\Administrator\\Desktop\\新建文件夹2\\";
        // 获取源文件夹当前下的文件或目录
        File[] files = (file).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                // 复制文件
                System.out.println("复制:" + files[i].getName());
                copyFile(files[i], new File(url2 + files[i].getName()));
            } else {
                copy(files[i]);
            }
        }
    }

    /**
     * 复制文件
     *
     * @param sourceFile
     * @param targetFile
     */
    public static void copyFile(File sourceFile, File targetFile) {
        // 新建文件输入流并对它进行缓冲
        try {
            FileInputStream input = new FileInputStream(sourceFile);
            BufferedInputStream inBuff = new BufferedInputStream(input);
            // 新建文件输出流并对它进行缓冲
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff = new BufferedOutputStream(output);
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
            //关闭流
            inBuff.close();
            outBuff.close();
            output.close();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用文件通道的方式来进行文件复制
     *
     * @param sourceFile 原文件
     * @param targetFile 新文件
     */
    public static void fileChannelCopy(File sourceFile, File targetFile) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(sourceFile);
            fo = new FileOutputStream(targetFile);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据绝对路径(可能是文件夹，可能是文件)，得到当前路径下所有指定后缀文件
     *
     * @param filePath
     * @return
     */
    public static List<File> getFileList(String filePath, String suffix) {
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        List<File> files = new ArrayList();
        if (childFiles != null) {
            for (File childFile : childFiles) {
                if (childFile.isDirectory()) {
                    files.addAll(getFileList(childFile.getPath(), suffix));
                } else {
                    String childFilePath = childFile.getPath();
                    String fileSuffix = childFilePath.substring(childFilePath.lastIndexOf(".") + 1);
                    if (suffix == null || fileSuffix.equals(suffix)) {
                        files.add(childFile);
                    }
                }
            }
        }
        return files;
    }


    public static List<Class> getClassList(String filePath, String groupId) {
        List<Class> classList = new ArrayList();
        String className = null;
        try {
            File[] childFiles = new File(filePath).listFiles();
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    if (childFile.isDirectory()) {
                        classList.addAll(getClassList(childFile.getPath(), groupId));
                    } else {
                        className = childFile.getPath().replace(File.separator, ".");
                        if (className.endsWith(".class")) {
                            className = className.substring(className.indexOf(groupId), className.lastIndexOf(".class"));
                            Class c = Class.forName(className);
                            classList.add(c);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.error("加载类{}错误", className);
        }
        return classList;
    }


    /**
     * 批量重命名
     *
     * @param filePath
     * @param newName
     */
    public static void reNameJavaList(String filePath, String newName) {
        String oldName = null;
        try {
            File[] childFiles = new File(filePath).listFiles();
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    if (childFile.isDirectory()) {
                        reNameJavaList(childFile.getPath(), newName);
                    } else {
                        oldName = childFile.getName();
                        String newFileName = newName.replace("oldName"
                                , oldName.substring(0, oldName.indexOf(".java"))) + ".java";
//                        newFileName=newFileName.substring(0,newFileName.indexOf(".java"))+".java";
                        childFile.renameTo(new File(childFile.getParentFile() + "/" + newFileName));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("重命名{}错误", oldName);
        }
    }

    public static File getFile(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if (url != null) {
            /*资源文件存在时，直接返回文件*/
            return new File(url.getPath());
        }
        return null;
    }
    public static <T> StringMap<T> readConfigFileByProperty(File file) {
        try {
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream(file);
            prop.load(in);
            StringMap<T> proMap = new StringMap();
            for (String key : prop.stringPropertyNames()) {
                proMap.put(key, (T) prop.getProperty(key));
            }
            in.close();
            return proMap;
        } catch (IOException e) {
            logger.error("加载配置文件{}失败。", file.getPath());
            return null;
        }
    }

    public static <T> StringMap<T> readConfigFileByYML(String filePath) {
        try(InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(filePath)) {
            StringMap<T> proMap = new Yaml().loadAs(in, StringMap.class);
            return proMap;
        } catch (IOException e) {
            logger.error("加载配置文件{}失败。", filePath);
            return null;
        }
    }
    public static <T> StringMap<T> readConfigFileByYML(String filePath,String... keys) {
        if (keys==null||keys.length==0){
            return readConfigFileByYML(filePath);
        }
        try(InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(filePath)){
            StringMap<T> proMap = new Yaml().loadAs(in, StringMap.class);
            StringMap<T> reesult = new StringMap<>(keys.length);
            for (String key:keys){
                reesult.add(key,proMap.get(key));
            }
            return reesult;
        } catch (IOException e) {
            logger.error("加载配置文件{}失败。", filePath);
            return null;
        }
    }
    public static void main(String[] args) {

    }


}

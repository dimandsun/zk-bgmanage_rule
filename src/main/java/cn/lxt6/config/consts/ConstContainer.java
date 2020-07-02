package cn.lxt6.config.consts;

import cn.lxt6.config.core.model.ProjectInfo;
import cn.lxt6.model.util.StringMap;
import cn.lxt6.util.FileUtil;
import cn.lxt6.util.StringUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author chenzy
 * 把业务相关配置文件映射成常量及map
 * @since 2020-04-08
 */
public class ConstContainer {
    private static StringMap<String> urlMap = new StringMap<>();

    private static ConstContainer instance = new ConstContainer();

    private ConstContainer() {
        reloadConfigFile();
    }

    public static ConstContainer getInstance() {
        return instance;
    }

    public void reloadConfigFile() {
        reloadURLConst();
    }



    public String getURL(String urlName) {
        if (urlMap.size() < 1) {
            reloadURLConst();
        }
        return urlMap.get(urlName);
    }


    public void reloadURLConst() {
        if (urlMap != null && !urlMap.isEmpty()) {
            urlMap.clear();
        }
        File file =FileUtil.getFile("config/url-" + ProjectInfo.getInstance().getActive().getMsg() + ".properties");
        if (file==null){
            return;
        }
        urlMap = FileUtil.readConfigFileByProperty(file);
        setStaticField(URLConst.class, urlMap, null);
    }

    private void setStaticField(Class c, StringMap parMap, String namePrefix) {
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            for (Field field : c.getDeclaredFields()) {
                /*将字段的访问权限设为true：即去除private修饰符的影响*/
                field.setAccessible(true);
                /*去除final修饰符的影响，将字段设为可修改的*/
                modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
                /*赋值*/
                String temp = StringUtil.isBlank(namePrefix) ? field.getName() : field.getName().substring(namePrefix.length());
                field.set(null, parMap.get(temp));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
      /*  ConstContainer.getInstance().reloadConfigFile();
        ConstContainer.getInstance().getURL("server_adress_A");*/
        System.out.println(123);
    }
    public String getURL(Long schoolId) {
        return null;
    }
}

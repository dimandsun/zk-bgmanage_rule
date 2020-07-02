package cn.lxt6.config.listener;

import cn.lxt6.config.core.CoreContainer;
import cn.lxt6.config.core.model.ProjectInfo;
import cn.lxt6.config.db.DataSourceEnum;
import cn.lxt6.config.db.DataSourceHolder;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author chenzy
 * @since 2020-05-25
 *
 */
public class CZYServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext application=sce.getServletContext();
        System.out.println("------------------------------容器正在初始化-----------------------------");
        CoreContainer container =CoreContainer.getInstance();
        container.initProject();
        /*项目信息放入ServletContext*/
        application.setAttribute("projectName", ProjectInfo.getInstance().getProjectName());//项目名称
        application.setAttribute("routeModelMap", container.getRouteMap());//存储路由与业务方法的map
        application.setAttribute("charEncoding", "UTF-8");
        System.out.println("------------------------------容器初始化完成------------------------------");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("------------------------------容器销毁开始------------------------------");
        DataSourceHolder.getInstance().clear();
        DataSourceEnum.clear();
        CoreContainer.getInstance().close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("------------------------------容器销毁结束------------------------------");
    }


}

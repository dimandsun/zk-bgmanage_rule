package cn.lxt6.config.db;

import cn.lxt6.config.core.CoreContainer;
import cn.lxt6.config.core.annotation.bean.Dao;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author chenzy
 *
 * @since 2020-04-08
 */
public class DaoUtil {
    private DaoUtil() {
    }
    public static Object exeSql(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Class targetClass = method.getDeclaringClass();
        SqlSession sqlSession = null;
        try {
            if (targetClass.isAnnotationPresent(Dao.class)) {
                sqlSession = CoreContainer.getInstance().getDataSourceMap().get(DataSourceHolder.getInstance().get())
                        .openSession();
                Object mapper = sqlSession.getMapper(targetClass);
                Method mapperMethod = mapper.getClass().getMethod(method.getName(), method.getParameterTypes());
                Object result = mapperMethod.invoke(mapper, args);
                sqlSession.commit();
                return result;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw e;//sql错误，需要抛出到业务代码做处理
        }finally {
            if (sqlSession!=null){
                sqlSession.close();
            }
            //防止内存泄漏
            DataSourceHolder.getInstance().clear();
        }
        return null;
    }
}

package cn.lxt6.config.db;


/**
 * 设置当前线程的变量的工具类，用于设置对应的数据源名称
 * @author chenzy
 * @date 2019.12.25
 */
public class DataSourceHolder {
    private DataSourceEnum defaultDataSource;
    private final ThreadLocal<DataSourceEnum> curDataSource = new ThreadLocal<>();
    private static DataSourceHolder instance=new DataSourceHolder();
    public static DataSourceHolder getInstance() {
        return instance;
    }
    protected DataSourceHolder() {
        this.defaultDataSource = DataSourceEnum.DEFAULT;
    }
    /**
     *  设置数据源
     */
    public void set(DataSourceEnum dataSourceKey) {
        curDataSource.set(dataSourceKey);
    }
    public void setDefault() {
        curDataSource.set(defaultDataSource);
    }
    /**
     *  获取数据源
     */
    public DataSourceEnum get() {
        if (curDataSource.get()==null){
            set(defaultDataSource);
        }
        return curDataSource.get();
    }
    /**
     *  清除数据源
     */
    public void clear() {
        curDataSource.remove();
    }
}

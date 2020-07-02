package cn.lxt6.config.enums;

/**
 * 枚举都要继承此接口，
 * @author chenzy
 * @date 2019.12.23
 * @param <Key> 枚举实际值的数据类型
 */
public interface IEnum<Key> {
    //枚举实际值
    Key getValue();

}

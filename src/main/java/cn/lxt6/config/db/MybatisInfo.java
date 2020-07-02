package cn.lxt6.config.db;

/**
 * @author chenzy
 *
 * @since 2020-04-14
 */
public class MybatisInfo {
    private TypeAliases typeAliases;

    public TypeAliases getTypeAliases() {
        return typeAliases;
    }

    public void setTypeAliases(TypeAliases typeAliases) {
        this.typeAliases = typeAliases;
    }
    public MybatisInfo(TypeAliases typeAliases) {
        this.typeAliases = typeAliases;
    }
}

package cn.lxt6.config.consts;

/**
 * @author chenzy
 * @since 2020-06-29
 * 服务器分组
 */
public class ServerGroup {
    private Long minSchoolId;
    private Long maxSchoolId;
    /*学生id前缀*/
    private String studentIdPrefix;
    private String groupName;
    //不是真正的url，是ConstContainer.getInstance().getURL()的参数
    private String serverAdress;

    public String getStudentIdPrefix() {
        return studentIdPrefix;
    }

    public void setStudentIdPrefix(String studentIdPrefix) {
        this.studentIdPrefix = studentIdPrefix;
    }

    public Long getMinSchoolId() {
        return minSchoolId;
    }
    public void setMinSchoolId(Long minSchoolId) {
        this.minSchoolId = minSchoolId;
    }
    public Long getMaxSchoolId() {
        return maxSchoolId;
    }

    public void setMaxSchoolId(Long maxSchoolId) {
        this.maxSchoolId = maxSchoolId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServerAdress() {
        return serverAdress;
    }
    public void setServerAdress(String serverAdress) {
        this.serverAdress = serverAdress;
    }
}

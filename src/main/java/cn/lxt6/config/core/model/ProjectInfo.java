package cn.lxt6.config.core.model;


import cn.lxt6.config.core.enums.ActiveEnum;

/**
 * @author chenzy
 * @since 2020-04-07
 *  项目信息，在项目启动时实例化
 */
public class ProjectInfo {
    private static ProjectInfo instance=new ProjectInfo();
    public static ProjectInfo getInstance(){
        return instance;
    }
    protected ProjectInfo(){

    }
    private String projectName;
    private String projectGroupId;

    /**/
    private ActiveEnum active;


    public ActiveEnum getActive() {
        return active==null?ActiveEnum.Default:active;
    }

    public String getProjectGroupId() {
        return projectGroupId;
    }

    public void setProjectGroupId(String projectGroupId) {
        this.projectGroupId = projectGroupId;
    }

    public void setActive(ActiveEnum active) {
        this.active = active;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}

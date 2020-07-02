package cn.lxt6.security.model;


import cn.lxt6.util.TimeUtil;

import java.time.LocalDateTime;

/**
 * @author chenzy
 *
 * @since 2020-03-27
 */
public class SecurityInfo {
    private static SecurityInfo instance = new SecurityInfo();
    private SecurityInfo() {
    }
    public static SecurityInfo getInstance() {
        return instance;
    }

    /*维护开始时间*/
    private LocalDateTime beginTime;
    /*维护结束时间*/
    private LocalDateTime endTime;
    //维护时长分钟数
    private String useTime;

    //是否处于维护状态
    private Boolean maintenance=false;

    public String getUseTime() {
        return useTime;
    }
    /*设置维护时长，设定开始与结束时间*/
    public SecurityInfo setUseTime(String useTime) {
        setMaintenance(true);
        this.useTime = useTime;
        beginTime = TimeUtil.now();
        endTime = beginTime.plusMinutes(Long.valueOf(useTime));
        return this;
    }
    //当前是否处于维护状态
    public Boolean getMaintenance() {
        return maintenance && TimeUtil.isInTime(this.getBeginTime(), this.getEndTime());
    }

    public SecurityInfo setMaintenance(Boolean maintenance) {
        this.maintenance = maintenance;
        return this;
    }

    public static void setInstance(SecurityInfo instance) {
        SecurityInfo.instance = instance;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

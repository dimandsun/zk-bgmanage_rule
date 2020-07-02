package cn.lxt6.security.service.impl;


import cn.lxt6.config.core.annotation.bean.Service;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.model.ResultVO;
import cn.lxt6.model.util.StringMap;
import cn.lxt6.security.model.SecurityInfo;
import cn.lxt6.security.model.SecurityQO;
import cn.lxt6.security.service.ISecurityService;

/**
 * @author chenzy
 *
 * @since 2020-03-27
 */
@Service
public class SecurityServiceImpl implements ISecurityService {

    @Override
    public ResultVO getHeartbeat(SecurityQO securityQO) {
        return new ResultVO(ResCodeEnum.Normal,"系统正常");
    }

    @Override
    public ResultVO setMaintenance(SecurityQO securityQO) {
        final String useTime = securityQO.getUseTime();
        final String random = securityQO.getRandom();
        SecurityInfo securityInfo = SecurityInfo.getInstance();
        if (securityInfo.getMaintenance()){
            return new ResultVO(ResCodeEnum.Normal,"已经是维护状态，无需重新设置");
        }
        /*设置维护时长，设定开始与结束时间*/
        securityInfo.setUseTime(useTime);
        return new ResultVO(new StringMap<>(2, "usetime", useTime).add("random", random));
    }

    @Override
    public ResultVO troubleShooting(SecurityQO securityQO) {
        final String random = securityQO.getRandom();
        String msg = null;
        if (SecurityInfo.getInstance().getMaintenance()) {
            SecurityInfo.getInstance().setMaintenance(false);
            msg = "设置成功";
        } else {
            msg = "已经解除维护状态，不需要重试！";
        }
        return new ResultVO(new StringMap<>(1, "random", random), msg);
    }
}

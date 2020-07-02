package cn.lxt6.security;


import cn.lxt6.config.core.annotation.Auto;
import cn.lxt6.config.core.annotation.bean.Controller;
import cn.lxt6.config.core.annotation.mapping.GetMapping;
import cn.lxt6.config.core.annotation.mapping.Mapping;
import cn.lxt6.model.ResultVO;
import cn.lxt6.security.model.SecurityQO;
import cn.lxt6.security.service.ISecurityService;
import cn.lxt6.util.DataUtil;

/**
 * @author chenzy
 *  文档：http://api.lxt6.cn:8080/api/OPS.html
 * @since 2020-03-27
 */
@Controller
@Mapping("/heartbeat")
public class SecurityController {
    @Auto
    private ISecurityService securityService;

    /**
     * 心跳探测接口
     * @return
     */
    @GetMapping("/GetHeartbeat")
    public ResultVO getHeartbeat(SecurityQO securityQO) {
        //业务数据简单校验
        ResultVO validResult = DataUtil.validateField(securityQO, "random");
        if (validResult.isNotNormal()) {
            return validResult;
        }
        return securityService.getHeartbeat(securityQO);
    }

    /**
     * 统故障切换接口：开始维护，
     * @param securityQO
     * @return
     */
    @GetMapping("/SetMaintenance")
    public ResultVO setMaintenance(SecurityQO securityQO) {
        //业务数据简单校验
        ResultVO validResult= DataUtil.validateField(securityQO, "useTime", "random");
        if (validResult.isNotNormal()) {
            return validResult;
        }
        return securityService.setMaintenance(securityQO);
    }

    /**
     * 统故障切换接口,维护完成，切换回正常状态
     * @param securityQO
     * @return
     */
    @GetMapping("/TroubleShooting")
    public ResultVO troubleShooting(SecurityQO securityQO) {
        //业务数据简单校验
        ResultVO validResult = DataUtil.validateField(securityQO,  "random");
        if (validResult.isNotNormal()) {
            return validResult;
        }
        return securityService.troubleShooting(securityQO);
    }

}

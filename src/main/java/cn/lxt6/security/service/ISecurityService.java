package cn.lxt6.security.service;

import cn.lxt6.model.ResultVO;
import cn.lxt6.security.model.SecurityQO;

/**
 * @author chenzy
 * @since 2020-03-27
 *
 */
public interface ISecurityService {
    ResultVO getHeartbeat(SecurityQO securityQO);

    ResultVO setMaintenance(SecurityQO securityQO);

    ResultVO troubleShooting(SecurityQO securityQO);
}

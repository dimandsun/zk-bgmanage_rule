package cn.lxt6.config.controller;


import cn.lxt6.config.consts.ConstContainer;
import cn.lxt6.config.core.CoreContainer;
import cn.lxt6.config.core.annotation.Par;
import cn.lxt6.config.core.annotation.bean.Controller;
import cn.lxt6.config.core.annotation.mapping.GetMapping;
import cn.lxt6.config.core.annotation.mapping.Mapping;
import cn.lxt6.config.enums.ResCodeEnum;
import cn.lxt6.model.ResultVO;
import cn.lxt6.secret.SecretContainer;
import cn.lxt6.util.StringUtil;

/**
 * @author chenzy
 *  用于处理重新加截配置文件或者处理缓存重新加载功能，不用重起容器
 * @date 2020.04.21
 */
@Controller
@Mapping("/loadinfo")
public class ConfigInfoController {

    /**
     * loadinfo/reloadConfig
     * 重新加载配置文件
     * configs="basic,secret,school,url,mybatis"
     * basic：基础配置文件为application-xx.yml 信息包括：
     *      projectName、specialSchoolPayApps
     *      数据源配置信息、redis配置、memcache配置
     * secret：config/secret-xx.yml
     * school：config/school-xx.yml
     * url：config/url-xx.properties
     * log：log4j2-xx.xml
     * mybatis：mybatis-config.xml
     * @return
     */
    @GetMapping("/reloadConfig")
    public ResultVO loadConfig(@Par("configs") String configs){
        if (StringUtil.isBlank(configs)){
            return new ResultVO(ResCodeEnum.ArgEmpty,"configs不能为空");
        }
        if (configs.contains("basic")){
            CoreContainer.getInstance().reloadBasicCofig();
//            APPEnum appEnum =LXTProjectInfo.getInstance().getSpecialSchoolPayAppMap().get(1);
        }
        if (configs.contains("log")){
            CoreContainer.getInstance().reloadLogCofig();
        }
        if (configs.contains("mybatis")){
            CoreContainer.getInstance().reloadMybatis();
        }
        if (configs.contains("secret")){
            SecretContainer.getInstance().reloadSecretConst();
        }
        if (configs.contains("url")){
            ConstContainer.getInstance().reloadURLConst();
        }
        return new ResultVO();
    }

}

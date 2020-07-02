package cn.lxt6.config;

import cn.lxt6.config.core.annotation.bean.Bean;
import cn.lxt6.config.core.annotation.bean.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * springboot 日志配置类
 * @author chenzy
 * @date 2019.12.17
 */
@Config
public class LogConfig {
    @Bean
    public Logger sysErrorLog() {
        return LoggerFactory.getLogger("sys_error");
    }
    @Bean
    public Logger userManagerLog() {
        return LoggerFactory.getLogger("userManager");
    }
    @Bean
    public Logger errMachineLog() {
        return LoggerFactory.getLogger("err_machine");
    }
    /*NB蓝牙设备访问设备管理中心日志*/
    @Bean
    public Logger machineLog() {
        return LoggerFactory.getLogger("machineManager");
    }
    @Bean
    public Logger deviceClientNetLog(){
        return LoggerFactory.getLogger("deviceClientNet");
    }
    @Bean
    public Logger deviceClientAuthLog(){
        return LoggerFactory.getLogger("deviceClientAuth");
    }
    @Bean
    public Logger deviceClientDBLog(){
        return LoggerFactory.getLogger("deviceClientDB");
    }
    @Bean
    public Logger deviceClientLog(){
        return LoggerFactory.getLogger("deviceClient");
    }
    @Bean
    public Logger nbDeviceClientDBLog(){
        return LoggerFactory.getLogger("nbDeviceClientDB");
    }
    @Bean
    public Logger nbDeviceClientAuthLog(){
        return LoggerFactory.getLogger("nbDeviceClientAuth");
    }
    @Bean
    public Logger nbDeviceClientLog(){
        return LoggerFactory.getLogger("nbDeviceClient");
    }
    @Bean
    public Logger cardClientDBLog(){
        return LoggerFactory.getLogger("cardClientDB");
    }
    @Bean
    public Logger cardClientAuthLog(){
        return LoggerFactory.getLogger("cardClientAuth");
    }
    @Bean
    public Logger cardClientLog(){
        return LoggerFactory.getLogger("cardClient");
    }
    /*集中器访问日志*/
    @Bean
    public Logger concentratorLog(){
        return LoggerFactory.getLogger("concentrator");
    }
    /*客户端访问日志*/
    @Bean
    public Logger clientLog(){
        return LoggerFactory.getLogger("client");
    }

    @Bean
    public Logger dbInfoLog(){
        return LoggerFactory.getLogger("dbinfo");
    }
    /*net访问日志日志*/
    @Bean
    public Logger netServerLog(){
        return LoggerFactory.getLogger("netServer");
    }
    /*NB蓝牙设备授权访问日志日志*/
    @Bean
    public Logger nbBatheAuthLog(){
        return LoggerFactory.getLogger("nbBatheAuth");
    }
    /**
     * 数据库错误日志
     */
    @Bean
    public Logger nbBatheDBErrorLog() {
        return LoggerFactory.getLogger("nbBatheDBError");
    }










}

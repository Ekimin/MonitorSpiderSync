package com.amarsoft.app.job;

/**监控程序接口
 * Created by ryang on 2017/1/9.
 */
public interface MonitorJob {

    /**
     * 监控逻辑
     * 1.根据flowID获得相对应的企业名单
     * 2.将企业名单存入本地监控表
     * 3。监控这些名单是否爬取完成、是否已经同步
     * 4.修改状态
     * @param flowId
     * @param bankId
     */
    public void monitorSpiderSync(String flowId,String modelId,String bankId);

    /**程序入口
     *@param flowId
     */
    public void run(String flowId);

}

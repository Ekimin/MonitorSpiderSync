package com.amarsoft.app.dao.LawData;

import com.amarsoft.app.common.DateManager;
import com.amarsoft.app.common.MonitorSpiderSync;
import com.amarsoft.app.dao.common.MonitorDao;
import com.amarsoft.app.model.MonitorModel;
import com.amarsoft.app.spider.LawCrawler.ExtractJSON;
import com.amarsoft.app.spider.LawCrawler.SpiderMethod;
import com.amarsoft.are.ARE;
import com.amarsoft.are.lang.DateX;
import com.amarsoft.are.lang.StringX;
import com.amarsoft.are.util.StringFunction;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ymhe on 2017/1/10.
 * MonitorSpiderSync
 */
public class LawDataDBManager implements MonitorDao, MonitorSpiderSync {
    String sourceUrl;
    String contentUrl;
    private String monitorTable;
    private String spiderTable;
    private String taskTable;
    private String ARE_PATH = "etc/are_Law.xml";
    private String LAW_DATABASE;
    private String LAW_DATABASE_TEST;

    public LawDataDBManager() {
        if (!ARE.isInitOk()) {
            ARE.init(ARE_PATH);
        }
        monitorTable = ARE.getProperty("MONITOR_TABLE", "monitor_lawcrawler_org");
        spiderTable = ARE.getProperty("SPIDER_TABLE", "COURTBULLETIN_QY");
        taskTable = ARE.getProperty("TASK_TABLE", "QY_CRAWLER_TASK");
        sourceUrl = ARE.getProperty("source_url", "http://wenshu.court.gov.cn/List/List?sorttype=1");
        contentUrl = ARE.getProperty("content_url", "http://wenshu.court.gov.cn/List/ListContent");
        LAW_DATABASE = ARE.getProperty("LAW_DATABASE", "bddata");
        LAW_DATABASE_TEST = ARE.getProperty("LAW_DATABASE_TEST", "bdsyn");
    }

    /**
     * 初始化监控任务
     *
     * @param batchId
     */
    public void initMonitor(String batchId) {
        Connection conn = null;
        PreparedStatement ps = null;
        String insertMonitorSql = "insert into " + monitorTable + "(batchId,spiderstatus,inputtime) values (?,?,?)";

        try {
            conn = ARE.getDBConnection(LAW_DATABASE);
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(insertMonitorSql);

            ps.setString(1, batchId);
            ps.setString(2, "init");//初始化状态，等待爬虫任务生成
            ps.setString(3, DateManager.getCurrentDate());
            ARE.getLog().info("开始往监控表里面插入该批次的信息");
            ps.execute();
            conn.commit();//TODO:for test
            ARE.getLog().info("插入批次信息完成");

        } catch (SQLException e) {
            ARE.getLog().info("插入批次信息出错", e);
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改监控表状态
     * @param batchId Az编号
     * @param status running，success
     */
    public void updateMonitorStatus(String batchId, String status){
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update " + monitorTable + " set SPIDERSTATUS=? where BATCHID=?";

        try{
            conn = ARE.getDBConnection(LAW_DATABASE);
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, batchId);
            ps.execute();
        }catch(SQLException e){
            ARE.getLog().info("修改监控表状态为出错", e);
            e.printStackTrace();
        }finally {
            try{
                if(ps != null){
                    ps.close();
                }
                if(conn != null){
                    conn.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化爬虫任务（生成任务）
     *
     * @param monitorModelList 监控名单
     * @param batchId
     */
    public void initSpiderTask(List<MonitorModel> monitorModelList, String batchId) {
        Connection connTask = null;
        PreparedStatement ps_Task = null;
        Statement st = null;
        ResultSet rs = null;


        String sqlMaxId = "select MAX(SERIALNO) from " + taskTable; //
        String sqlTask = "insert into " + taskTable + " (SERIALNO, QUERYPARAM, STATUS, CREATETIME, PRIORITY, FLOWID) values " +
                "(?,?,?,?,?,?)";//这里flowid就是batchid
        int batch = 500;
        long currentSerialNo = 0;
        try {
            connTask = ARE.getDBConnection(LAW_DATABASE_TEST); //78_bdsyn
            st = connTask.createStatement();
            rs = st.executeQuery(sqlMaxId);
            connTask.setAutoCommit(false);

            if (rs.next()) {
                String str = rs.getString(1);
                if(str == null || str.equals("")){
                    currentSerialNo = 201701010000000001L;//无数据,默认值
                }else{
                    currentSerialNo = Long.parseLong(rs.getString(1));
                    ARE.getLog().info("任务表目前最大ID=" + currentSerialNo);
                }

            }else{
                currentSerialNo = 201701010000000001L;//无数据,默认值
            }

            ps_Task = connTask.prepareStatement(sqlTask);
            String currentDate = DateManager.getCurrentDate("yyyyMMdd");
            ARE.getLog().info("currentDate = " + currentDate);
            int batchCount = 0;
            for (MonitorModel monitorModel : monitorModelList) {
                currentSerialNo++;
                ps_Task.setString(1, String.valueOf(currentSerialNo)); //serialNo
                ps_Task.setString(6, batchId); //batchId，批次号=flowid
                String entName = monitorModel.getEntName();
                String monitorReg = monitorModel.getStockBlock(); //监控时间区间
                //TODO:爬虫区间合并，搜索条件
                String queryStr = null;
                if (entName != null && !entName.equals("")) {
                    if (monitorReg != null && monitorReg.length() > 20) {
                        String minDate = monitorReg.substring(1, 11).replace("/", "-");
                        String maxDate = monitorReg.substring(12, 22);
                        if (minDate.equals("0001-01-01")) {
                            queryStr = "全文检索:" + entName;
                        } else {
                            queryStr = "全文检索:" + entName + ",上传日期:" + minDate + " TO " + maxDate;
                        }
                    } else {
//                        String minDate = monitorReg.substring(0, 10).replace("/", "-");
//                        String maxDate = DateManager.getDate(minDate, -1);
                        //TODO:时间区间判断
                        queryStr = "全文检索:" + entName;
                    }
                }
                //处理优先级：1,2==》high 3=》low
                String piority = monitorModel.getInspectLevel();
                piority = (piority.equals("1") || piority.equals("2")) ? "high" : "low";
                int num = getReturnNum(queryStr);
                if (num == 0) {
                    //没有数据，直接生成任务标记完成
                    ps_Task.setString(2, queryStr); //queryStr
                    ps_Task.setString(3, "2"); //status：2=完成
                    ps_Task.setString(4, StringFunction.getTodayNow()); //inputtime
                    ps_Task.setString(5, piority);//优先级

                    ps_Task.addBatch();
                    batchCount++;
                    if (batchCount >= batch) {
                        ps_Task.executeBatch();
                        batchCount = 0;
                    }
                } else if (num > 100) {
                    String date = DateManager.getCurrentDate("yyyy-MM-dd");
                    List<String> judgmentConditions = createJudgmentCondition(date);
                    for (String judgmentCondition : judgmentConditions) {
                        String newQuery = queryStr + "," + judgmentCondition;
                        ps_Task.setString(2, newQuery); //newQuery
                        ps_Task.setString(3, "0"); //status：0=等待爬取状态
                        ps_Task.setString(4, StringFunction.getTodayNow()); //inputtime
                        ps_Task.setString(5, piority);//优先级

                        ps_Task.addBatch();
                        batchCount++;
                        if (batchCount >= batch) {
                            ps_Task.executeBatch();
                            batchCount = 0;
                        }
                    }
                } else {
                    ps_Task.setString(2, queryStr); //queryStr
                    ps_Task.setString(3, "0"); //status：0=等待爬取状态
                    ps_Task.setString(4, StringFunction.getTodayNow()); //inputtime
                    ps_Task.setString(5, piority);//优先级

                    ps_Task.addBatch();
                    batchCount++;
                    if (batchCount >= batch) {
                        ps_Task.executeBatch();
                        batchCount = 0;
                    }
                }
                monitorModel.setTaskStage("TaskGenerated");
            }
            ps_Task.executeBatch();
            connTask.commit();
        } catch (SQLException e) {
            ARE.getLog().error("生成爬虫任务时出错", e);
            e.printStackTrace();
        } finally {
            try {
                if (ps_Task != null) {
                    ps_Task.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (connTask != null) {
                    connTask.close();
                }
            } catch (SQLException e) {
                ARE.getLog().error("生成爬虫任务时，关闭数据库连接出错", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 监控爬虫任务是否完成
     * <li>企业名单中优先级1和2的任务完成就算完成，即优先级为high的任务状态为2</li>
     *
     * @param monitorModelList
     * @param flowId
     */
    public boolean monitorSpiderTask(List<MonitorModel> monitorModelList, String flowId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select count(*) from " + taskTable + " where FLOWID=? and STATUS!=? and PRIORITY=?";
        boolean isSpidered = false;

        try {
            conn = ARE.getDBConnection(LAW_DATABASE_TEST);
            ps = conn.prepareStatement(sql);
            ps.setString(1, flowId);
            ps.setString(2, "2"); //2=抓取完成。
            ps.setString(3, "high");
            rs = ps.executeQuery();

            int num = -1;
            if (rs.next()) {
                num = Integer.parseInt(rs.getString(1));
                if (num > 0) {
                    ARE.getLog().info("爬虫任务尚未完成====继续监控====");
                    return isSpidered;
                } else if (num == 0) {
                    ARE.getLog().info("爬虫爬取完成了，开始监控同步");
                    isSpidered = true;
                    return isSpidered; //TODO:ceshi
                }
            }

        } catch (SQLException e) {
            ARE.getLog().error("监控爬虫任务是否完成出错了", e);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                ARE.getLog().error("监控爬虫任务是否完成关闭数据库连接出错了", e);
                e.printStackTrace();
            }
        }
        return isSpidered;
    }

    /**
     * 监控同步任务是否完成
     *
     * @param monitorModelList
     * @param batchId
     */
    public boolean monitorSyncTask(List<MonitorModel> monitorModelList, String batchId) {

        String sql = "select count(*) from " + spiderTable + " where (STATUS='waiting' OR STATUS='running') and COURTROOM=?";

        boolean isSynchronized = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            conn = ARE.getDBConnection(LAW_DATABASE_TEST);
            ps = conn.prepareStatement(sql);

            String entName;
            int unsyncCount;
            for (MonitorModel monitorModel : monitorModelList){
                entName = monitorModel.getEntName();
                if(StringX.isEmpty(entName)){
                    continue;
                }
                ps.setString(1, entName);
                rs = ps.executeQuery();
                if (rs.next()){
                    unsyncCount = rs.getInt(1);
                    if (unsyncCount > 0){
                        ARE.getLog().info("企业" + entName + "同步尚未完成...");
                        isSynchronized = false;
                        return isSynchronized;
                    }
                }
            }
            isSynchronized = true;
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            try{
                if (rs!=null){
                    rs.close();
                }
                if(ps != null){
                    ps.close();
                }
                if (conn != null){
                    conn.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }

        }


        return isSynchronized;
    }

    /**
     * 返回企业网页的总页数
     *
     * @return
     */
    public int getReturnNum(String paramStr) {
        int pageSum = 0;
        try {
            ARE.getLog().info("[" + paramStr + "]");
            Map<String, String> params = LawDataDBManager.createParams();
            params.put("Param", paramStr);
            SpiderMethod spiderMethod = new SpiderMethod();
            String jsonStr = spiderMethod.getJSON(sourceUrl, contentUrl, params, "utf-8");
            pageSum = ExtractJSON.getSumPage(jsonStr, paramStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageSum;
    }

    public static Map<String, String> createParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Param", "");
        params.put("Index", "1");
        params.put("Page", "20");
        params.put("Order", "裁判日期");
        params.put("Direction", "desc");
        return params;
    }

    /**
     * 构造裁判日期查询条件
     */
    private static List<String> createJudgmentCondition(String curDate) {
        DateX datex = new DateX(curDate);
        String template = "裁判日期:preDate TO curDate";
        String condition = "";
        String yearNum = ARE.getProperty("yearNum", "10");
        int num = 0;
        try {
            num = Integer.parseInt(yearNum);
        } catch (Exception e) {
            ARE.getLog().error("配置文件中设置的抓取的年份计数不是整数，将其默认设置为10");
            num = 10;
        }
        List<String> list = new ArrayList<String>();
        String endDate = DateX.format(datex.getAbsoluteRelativeDate(0, -num), "yyyy-MM-dd");
        String preDate = "";
        while (true) {
            // 2010之后的的裁判日期区间设为10天，2010之前的裁判区间为一个季度
            if (curDate.compareTo("2010-01-01") > 0) {
                preDate = DateX.format(datex.getAbsoluteRelativeDate(1, -3), "yyyy-MM-dd");
            } else {
                preDate = DateX.format(datex.getAbsoluteRelativeDate(1, -12), "yyyy-MM-dd");
            }
            condition = template.replace("preDate", preDate).replace("curDate", curDate);
            list.add(condition);
            curDate = preDate;
            datex = new DateX(curDate);
            if (curDate.compareTo(endDate) <= 0) {
                break;
            }
        }
        return list;
    }

    public void generateTask(List<MonitorModel> entMonitor, String flowID) {

    }

    public boolean isSpidered(String flowID) {
        return false;
    }

    public boolean isSynchronized(List<MonitorModel> entList) {
        return false;
    }
}

package com.amarsoft.app.model;

/**
 * Created by ryang on 2017/1/5.
 */

//监控表对象
public class MonitorModel {
    String serialno;
    String orgname;
    String enterprisename;
    String idno;
    String monitorurl;
    String stockblock;
    String inspectlevel;
    String inspectstate;
    String inputtime;
    String updatetime;
    String datasourcetype;
    String modelid;
    String taskstage;

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getEnterprisename() {
        return enterprisename;
    }

    public void setEnterprisename(String enterprisename) {
        this.enterprisename = enterprisename;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getMonitorurl() {
        return monitorurl;
    }

    public void setMonitorurl(String monitorurl) {
        this.monitorurl = monitorurl;
    }

    public String getStockblock() {
        return stockblock;
    }

    public void setStockblock(String stockblock) {
        this.stockblock = stockblock;
    }

    public String getInspectlevel() {
        return inspectlevel;
    }

    public void setInspectlevel(String inspectlevel) {
        this.inspectlevel = inspectlevel;
    }

    public String getInspectstate() {
        return inspectstate;
    }

    public void setInspectstate(String inspectstate) {
        this.inspectstate = inspectstate;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getDatasourcetype() {
        return datasourcetype;
    }

    public void setDatasourcetype(String datasourcetype) {
        this.datasourcetype = datasourcetype;
    }

    public String getModelid() {
        return modelid;
    }

    public void setModelid(String modelid) {
        this.modelid = modelid;
    }

    public String getTaskstage() {
        return taskstage;
    }

    public void setTaskstage(String taskstage) {
        this.taskstage = taskstage;
    }
}

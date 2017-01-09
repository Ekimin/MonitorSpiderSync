package com.amarsoft.app.model;

/**
 * Created by ryang on 2017/1/5.
 */

//监控表对象
public class MonitorModel {
    String serialNo;
    String orgName;
    String entName;
    String idNo;
    String monitorUrl;
    String stockBlock;
    String inspectLevel;
    String inspectState;
    String inputTime;
    String updateTime;
    String datasourceType;
    String modelId;
    String taskStage;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getMonitorUrl() {
        return monitorUrl;
    }

    public void setMonitorUrl(String monitorUrl) {
        this.monitorUrl = monitorUrl;
    }

    public String getStockBlock() {
        return stockBlock;
    }

    public void setStockBlock(String stockBlock) {
        this.stockBlock = stockBlock;
    }

    public String getInspectLevel() {
        return inspectLevel;
    }

    public void setInspectLevel(String inspectLevel) {
        this.inspectLevel = inspectLevel;
    }

    public String getInspectState() {
        return inspectState;
    }

    public void setInspectState(String inspectState) {
        this.inspectState = inspectState;
    }

    public String getInputTime() {
        return inputTime;
    }

    public void setInputTime(String inputTime) {
        this.inputTime = inputTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getTaskStage() {
        return taskStage;
    }

    public void setTaskStage(String taskStage) {
        this.taskStage = taskStage;
    }
}

package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.SlaveModel;

public class Slave {
    private long id;
    private String machineName;
    private int status;
    private int concurrency;
    private boolean isListening;

    public static Slave create(SlaveModel m) {
        Slave slave = new Slave();
        slave.setId(m.getLong(SlaveModel.Fields.ID));
        slave.setMachineName(m.getStr(SlaveModel.Fields.MACHINE_NAME));
        slave.setStatus(m.getInt(SlaveModel.Fields.STATUS));
        slave.setConcurrency(m.getInt(SlaveModel.Fields.CONCURRENCY));
        slave.setIsListening(m.getBoolean(SlaveModel.Fields.IS_LISTENING));
        return slave;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public boolean getIsListening() {
        return isListening;
    }

    public void setIsListening(boolean isListening) {
        this.isListening = isListening;
    }
}

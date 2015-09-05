package net.nitrogen.ates.core.model.slave;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

public class SlaveModel extends Model<SlaveModel> {
    public static final String TABLE = "slave";

    public class Fields {
        public static final String ID = "id";
        public static final String MACHINE_NAME = "machine_name";
        public static final String STATUS = "status";
        public static final String CONCURRENCY = "concurrency";
        public static final String IS_LISTENING = "is_listening";
    }

    public static final SlaveModel me = new SlaveModel();

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getMachineName() {
        return this.getStr(Fields.MACHINE_NAME);
    }

    public void setMachineName(String machineName) {
        this.set(Fields.MACHINE_NAME, machineName);
    }

    public int getStatus() {
        return this.getInt(Fields.STATUS);
    }

    public void setStatus(int status) {
        this.set(Fields.STATUS, status);
    }

    public int getConcurrency() {
        return this.getInt(Fields.CONCURRENCY);
    }

    public void setConcurrency(int concurrency) {
        this.set(Fields.CONCURRENCY, concurrency);
    }

    public boolean getIsListening() {
        return this.getBoolean(Fields.IS_LISTENING);
    }

    public void setIsListening(boolean isListening) {
        this.set(Fields.IS_LISTENING, isListening);
    }

    public List<SlaveModel> findAllSlaves() {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s` FROM `%s`",
                Fields.ID,
                Fields.MACHINE_NAME,
                Fields.STATUS,
                Fields.CONCURRENCY,
                Fields.IS_LISTENING,
                TABLE);

        return find(sql);
    }

    public boolean slaveExists(String slaveName) {
        return SlaveModel.me.findFirst(String.format("SELECT `%s` FROM `%s` WHERE `%s`=? LIMIT 1", Fields.ID, TABLE, Fields.MACHINE_NAME), slaveName) != null;
    }

    public void insertSlave(String slaveName) {
        SlaveModel m = new SlaveModel();
        m.setMachineName(slaveName);
        m.setStatus(0);
        m.setConcurrency(1);
        m.setIsListening(true);
        m.save();
    }
}

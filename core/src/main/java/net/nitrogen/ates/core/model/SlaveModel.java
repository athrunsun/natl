package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.entity.Slave;

import java.util.ArrayList;
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

    public List<Slave> findAllSlaves() {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s` FROM `%s`",
                Fields.ID,
                Fields.MACHINE_NAME,
                Fields.STATUS,
                Fields.CONCURRENCY,
                Fields.IS_LISTENING,
                TABLE);

        List<Slave> slaves = new ArrayList<>();

        for (SlaveModel m : find(sql)) {
            slaves.add(Slave.create(m));
        }

        return slaves;
    }

    public boolean slaveExists(String slaveName) {
        return SlaveModel.me.findFirst(String.format("SELECT `%s` FROM `%s` WHERE `%s`=? LIMIT 1", Fields.ID, TABLE, Fields.MACHINE_NAME), slaveName) != null;
    }

    public void insertSlave(String slaveName) {
        SlaveModel m = new SlaveModel();
        m.set(Fields.MACHINE_NAME, slaveName).set(Fields.STATUS, 0).set(Fields.CONCURRENCY, 1).set(Fields.IS_LISTENING, true).save();
    }
}

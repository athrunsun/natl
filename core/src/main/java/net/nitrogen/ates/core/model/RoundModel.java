package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.enumeration.QueueEntryStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoundModel extends Model<RoundModel> {
    public static final String TABLE = "round";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }

    public static final RoundModel me = new RoundModel();

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getName() {
        return this.getStr(Fields.NAME);
    }

    public void setName(String name) {
        this.set(Fields.NAME, name);
    }

    public long getProjectId() {
        return this.getLong(Fields.PROJECT_ID);
    }

    public void setProjectId(long projectId) {
        this.set(Fields.PROJECT_ID, projectId);
    }

    public List<RoundModel> findRounds(long projectId) {
        return find(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.PROJECT_ID, Fields.ID),
                projectId);
    }

    public long createRoundByTestGroup(long projectId, String roundName, String env, String jvmOptions, String params, List<Long> testGroupIds) {
        RoundModel newRound = new RoundModel();
        newRound.set(Fields.NAME, roundName).set(Fields.PROJECT_ID, projectId).save();
        long roundId = newRound.get(Fields.ID);
        Set<String> uniqueTestNames = new HashSet<String>();

        for(Long testGroupId : testGroupIds) {
            for(TestGroupTestCaseModel tg_tc : TestGroupTestCaseModel.me.findTestGroupTestCases(testGroupId.longValue())) {
                uniqueTestNames.add(tg_tc.getTestName());
            }
        }

        List<QueueEntryModel> entries = new ArrayList<QueueEntryModel>();

        for(String testName : uniqueTestNames) {
            QueueEntryModel entry = new QueueEntryModel();
            entry.setStatus(QueueEntryStatus.WAITING.getStatus());
            entry.setName(testName);
            entry.setSlaveName("");
            entry.setRoundId(roundId);
            entry.setProjectId(projectId);
            entry.setEnv(env);
            entry.setJvmOptions(jvmOptions);
            entry.setParams(params);
            entries.add(entry);
        }

        QueueEntryModel.me.insertEntries(entries);
        return roundId;
    }
}

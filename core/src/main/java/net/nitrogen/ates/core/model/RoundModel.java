package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.entity.QueueEntry;
import net.nitrogen.ates.core.entity.Round;
import net.nitrogen.ates.core.entity.TestGroupTestCase;
import net.nitrogen.ates.core.enumeration.QueueEntryStatus;

import java.util.*;

public class RoundModel extends Model<RoundModel> {
    public static final String TABLE = "round";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }

    public static final RoundModel me = new RoundModel();

    public List<Round> findRounds(long projectId) {
        List<RoundModel> mList = find(
                String.format("SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=? ORDER BY `%s` DESC", Fields.ID, Fields.NAME, Fields.PROJECT_ID, TABLE, Fields.PROJECT_ID, Fields.ID),
                projectId);

        List<Round> rounds = new ArrayList<Round>();

        for(RoundModel m : mList) {
            rounds.add(Round.create(m));
        }

        return rounds;
    }

    public long createRoundByTestGroup(long projectId, String roundName, String env, String jvmOptions, String params, List<Long> testGroupIds) {
        RoundModel newRound = new RoundModel();
        newRound.set(Fields.NAME, roundName).set(Fields.PROJECT_ID, projectId).save();
        long roundId = newRound.get(Fields.ID);
        Set<String> uniqueTestNames = new HashSet<String>();

        for(Long testGroupId : testGroupIds) {
            for(TestGroupTestCase tg_tc : TestGroupTestCaseModel.me.findTestGroupTestCases(testGroupId.longValue())) {
                uniqueTestNames.add(tg_tc.getTestName());
            }
        }

        List<QueueEntry> entries = new ArrayList<QueueEntry>();

        for(String testName : uniqueTestNames) {
            QueueEntry entry = new QueueEntry();
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

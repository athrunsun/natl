package net.nitrogen.ates.core.model.test_group;

public class TestGroupWithAdditionalInfo {
    private TestGroupModel testGroup;
    private int testCaseCount;

    public class Fields {
        public static final String TEST_CASE_COUNT = "test_case_count";
    }

    public TestGroupModel getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(TestGroupModel tg) {
        this.testGroup = tg;
    }

    public int getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }
}

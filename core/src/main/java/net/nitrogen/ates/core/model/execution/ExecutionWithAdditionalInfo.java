package net.nitrogen.ates.core.model.execution;

public class ExecutionWithAdditionalInfo {
    public class Fields {
        public static final String QUEUE_ENTRY_COUNT = "queue_entry_count";
    }

    private ExecutionModel execution;
    private int queueEntryCount;

    public ExecutionModel getExecution() {
        return this.execution;
    }

    public void setExecution(ExecutionModel e) {
        this.execution = e;
    }

    public int getQueueEntryCount() {
        return this.queueEntryCount;
    }

    public void setQueueEntryCount(int count) {
        this.queueEntryCount = count;
    }
}

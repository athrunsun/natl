package net.nitrogen.ates.core.exec;

import net.nitrogen.ates.core.model.QueueEntryModel;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;

public class QueueEntryExecResultHandler extends DefaultExecuteResultHandler {
    private long queueEntryId;

    public QueueEntryExecResultHandler(long entryId) {
        this.queueEntryId = entryId;
    }

    @Override
    public void onProcessComplete(final int exitValue) {
        super.onProcessComplete(exitValue);
        QueueEntryModel.me.markEntryAsFinished(this.queueEntryId);
    }

    @Override
    public void onProcessFailed(final ExecuteException e) {
        super.onProcessFailed(e);
        QueueEntryModel.me.markEntryAsFinished(this.queueEntryId);
    }
}

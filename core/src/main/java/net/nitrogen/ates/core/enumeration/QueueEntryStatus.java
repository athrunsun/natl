package net.nitrogen.ates.core.enumeration;

public enum QueueEntryStatus {
    WAITING(0),
    RUNNING(1),
    FINISHED(2),
    STOPPED(3);

    final int status;

    QueueEntryStatus(int s) {
        this.status = s;
    }

    public int getStatus() {
        return this.status;
    }
}

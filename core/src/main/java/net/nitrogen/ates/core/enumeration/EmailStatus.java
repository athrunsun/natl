package net.nitrogen.ates.core.enumeration;

public enum EmailStatus {
    STARTED(0), READY(1), SENT(2);

    final int value;

    EmailStatus(int s) {
        this.value = s;
    }

    public int getValue() {
        return this.value;
    }
}

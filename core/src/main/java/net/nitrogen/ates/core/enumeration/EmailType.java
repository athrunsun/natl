package net.nitrogen.ates.core.enumeration;

public enum EmailType {
    EXECUTION(0), SUITE(1), CASE_RELOAD(2);

    final int value;

    EmailType(int s) {
        this.value = s;
    }

    public int getValue() {
        return this.value;
    }
}

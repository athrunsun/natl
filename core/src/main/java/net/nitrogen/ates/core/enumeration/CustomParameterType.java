package net.nitrogen.ates.core.enumeration;

public enum CustomParameterType {
    JVM(0), TESTNG(1), EMAIL(2);

    final int value;

    CustomParameterType(int s) {
        this.value = s;
    }

    public int getValue() {
        return this.value;
    }
}

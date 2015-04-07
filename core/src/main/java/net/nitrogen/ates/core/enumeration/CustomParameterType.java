package net.nitrogen.ates.core.enumeration;

public enum CustomParameterType {
    JVM(0), TESTNG(1);

    final int value;

    CustomParameterType(int s) {
        this.value = s;
    }

    public int getValue() {
        return this.value;
    }
}

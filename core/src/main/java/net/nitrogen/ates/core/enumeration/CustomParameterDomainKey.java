package net.nitrogen.ates.core.enumeration;


public enum CustomParameterDomainKey {
    EXECUTION(0), TEST_SUITE(1), PROJECT(2);

    final int value;

    CustomParameterDomainKey(int v) {
        this.value = v;
    }

    public int getValue() {
        return this.value;
    }
}

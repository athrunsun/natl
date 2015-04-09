package net.nitrogen.ates.core.enumeration;

public enum FeedbackType {
    COMMENT(0), BUG(1), SUGGESTION(2), QUESTION(3);

    final int value;

    FeedbackType(int s) {
        this.value = s;
    }

    public int getValue() {
        return this.value;
    }
}

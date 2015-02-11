package net.nitrogen.ates.core.enumeration;

import java.util.HashMap;
import java.util.Map;

public enum ExecResult {
    UNKNOWN(0),
    SKIPPED(1),
    PASSED(2),
    FAILED(3);

    final int value;

    ExecResult(int v) {
        this.value = v;
    }

    public int getValue()
    {
        return this.value;
    }

    private static final Map<Integer, ExecResult> intToTypeMap = new HashMap<Integer, ExecResult>();
    static {
        for (ExecResult type : ExecResult.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    public static ExecResult fromInt(int i) {
        ExecResult type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return ExecResult.UNKNOWN;
        return type;
    }
}

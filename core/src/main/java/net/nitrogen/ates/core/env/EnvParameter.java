package net.nitrogen.ates.core.env;

import net.nitrogen.ates.core.exec.ExecManager;
import net.nitrogen.ates.util.StringUtil;

public class EnvParameter {
    public static String machineName() {
        return System.getenv("COMPUTERNAME");
    }

    public static boolean isDebug() {
        String debug = System.getProperty(ExecManager.EXEC_PARAM_KEY_DEBUG);
        if (StringUtil.isNullOrWhiteSpace(debug)) {
            return true;
        } else {
            return Integer.parseInt(debug) > 0;
        }
    }

    public static long entryId() {
        return Long.parseLong(System.getProperty(ExecManager.EXEC_PARAM_KEY_ENTRY_ID));
    }

    public static int entryIndex() {
        return Integer.parseInt(System.getProperty(ExecManager.EXEC_PARAM_KEY_ENTRY_INDEX));
    }

    public static long executionId() {
        return Long.parseLong(System.getProperty(ExecManager.EXEC_PARAM_KEY_EXECUTION_ID));
    }

    public static long projectId() {
        return Long.parseLong(System.getProperty(ExecManager.EXEC_PARAM_KEY_PROJECT_ID));
    }
}

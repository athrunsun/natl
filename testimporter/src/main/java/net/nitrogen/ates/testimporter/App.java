package net.nitrogen.ates.testimporter;

import net.nitrogen.ates.core.entity.TestCase;
import net.nitrogen.ates.core.entity.TestGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        long projectId = Long.parseLong(args[0]);
        String jarFileWithoutDependenciesPath = args[1];
        String jarFileWithDependenciesPath = args[2];

        List<TestCase> testCasesToReload = new ArrayList<TestCase>();
        List<TestGroup> testGroupsToReload = new ArrayList<TestGroup>();
        Map<String, List<String>> rawTestGroupTestCasesToReload = new HashMap<String, List<String>>();

        new JarScanner(projectId, jarFileWithoutDependenciesPath, jarFileWithDependenciesPath).scan(testCasesToReload, testGroupsToReload, rawTestGroupTestCasesToReload);
        new Importer(projectId).doImport(testCasesToReload, testGroupsToReload, rawTestGroupTestCasesToReload);
    }
}

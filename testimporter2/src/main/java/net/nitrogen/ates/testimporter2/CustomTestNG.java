package net.nitrogen.ates.testimporter2;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.testng.*;
import org.testng.annotations.ITestAnnotation;
import org.testng.collections.Lists;
import org.testng.collections.Maps;
import org.testng.internal.*;
import org.testng.internal.annotations.DefaultAnnotationTransformer;
import org.testng.internal.annotations.IAnnotationFinder;
import org.testng.internal.annotations.JDK15AnnotationFinder;
import org.testng.junit.JUnitTestFinder;
import org.testng.log4testng.Logger;
import org.testng.remote.SuiteDispatcher;
import org.testng.remote.SuiteSlave;
import org.testng.reporters.*;
import org.testng.xml.Parser;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Bruce 2015-2-15
 * This is a modified version of TestNG borrowed from TestNG 6.8.5.
 * This class is the main entry point for scanning tests and import them into ATES database.
 * Users can create their own TestNG object and invoke it in many different
 * ways:
 * <ul>
 * <li>On an existing testng.xml
 * <li>On a synthetic testng.xml, created entirely from Java
 * <li>By directly setting the test classes
 * </ul>
 * You can also define which groups to include or exclude, assign parameters, etc...
 * <p/>
 * The command line parameters are:
 * <UL>
 * <LI>-d <TT>outputdir</TT>: specify the output directory</LI>
 * <LI>-testclass <TT>class_name</TT>: specifies one or several class names </li>
 * <LI>-testjar <TT>jar_name</TT>: specifies the jar containing the tests</LI>
 * <LI>-sourcedir <TT>src1;src2</TT>: ; separated list of source directories
 * (used only when javadoc annotations are used)</LI>
 * <LI>-target</LI>
 * <LI>-groups</LI>
 * <LI>-testrunfactory</LI>
 * <LI>-listener</LI>
 * </UL>
 * <p/>
 * Please consult documentation for more details.
 * <p/>
 *
 * @author Bruce Sun
 * @see #usage()
 */
public class CustomTestNG extends TestNG {

    /**
     * This class' log4testng Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CustomTestNG.class);

    /**
     * The default name for a suite launched from the command line
     */
    public static final String DEFAULT_COMMAND_LINE_SUITE_NAME = "Command line suite";

    /**
     * The default name for a test launched from the command line
     */
    public static final String DEFAULT_COMMAND_LINE_TEST_NAME = "Command line test";

    /**
     * The default name of the result's output directory (keep public, used by Eclipse).
     */
    public static final String DEFAULT_OUTPUTDIR = "test-output";

    /**
     * System properties
     */
    public static final String SHOW_TESTNG_STACK_FRAMES = "testng.show.stack.frames";
    public static final String TEST_CLASSPATH = "testng.test.classpath";

    private static CustomTestNG m_instance;

    private static JCommander m_jCommander;

    private List<String> m_commandLineMethods;
    protected List<org.testng.xml.XmlSuite> m_suites = Lists.newArrayList();
    private List<org.testng.xml.XmlSuite> m_cmdlineSuites;
    private String m_outputDir = DEFAULT_OUTPUTDIR;

    private String[] m_includedGroups;
    private String[] m_excludedGroups;

    private Boolean m_isJUnit = org.testng.xml.XmlSuite.DEFAULT_JUNIT;
    private Boolean m_isMixed = org.testng.xml.XmlSuite.DEFAULT_MIXED;
    protected boolean m_useDefaultListeners = true;

    private ITestRunnerFactory m_testRunnerFactory;

    // These listeners can be overridden from the command line
    private List<org.testng.ITestListener> m_testListeners = Lists.newArrayList();
    private List<org.testng.ISuiteListener> m_suiteListeners = Lists.newArrayList();
    private Set<IReporter> m_reporters = org.testng.internal.annotations.Sets.newHashSet();

    protected static final int HAS_FAILURE = 1;
    protected static final int HAS_SKIPPED = 2;
    protected static final int HAS_FSP = 4;
    protected static final int HAS_NO_TEST = 8;

    public static final Integer DEFAULT_VERBOSE = 1;

    private int m_status;
    private boolean m_hasTests = false;

    private String m_slavefileName = null;
    private String m_masterfileName = null;

    // Command line suite parameters
    private int m_threadCount;
    private boolean m_useThreadCount;
    private String m_parallelMode;
    private boolean m_useParallelMode;
    private String m_configFailurePolicy;
    private Class[] m_commandLineTestClasses;

    private String m_defaultSuiteName = DEFAULT_COMMAND_LINE_SUITE_NAME;
    private String m_defaultTestName = DEFAULT_COMMAND_LINE_TEST_NAME;

    private Map<String, Integer> m_methodDescriptors = Maps.newHashMap();

    private org.testng.ITestObjectFactory m_objectFactory;

    private List<IInvokedMethodListener> m_invokedMethodListeners = Lists.newArrayList();

    private Integer m_dataProviderThreadCount = null;

    private String m_jarPath;
    /**
     * The path of the testng.xml file inside the jar file
     */
    private String m_xmlPathInJar = CommandLineArgs.XML_PATH_IN_JAR_DEFAULT;

    private List<String> m_stringSuites = Lists.newArrayList();

    private IHookable m_hookable;
    private IConfigurable m_configurable;

    protected long m_end;
    protected long m_start;

    private List<org.testng.IExecutionListener> m_executionListeners = Lists.newArrayList();

    private boolean m_isInitialized = false;

    /**
     * Default constructor. Setting also usage of default listeners/reporters.
     */
    public CustomTestNG() {
        init(true);
    }

    /**
     * Used by maven2 to have 0 output of any kind come out
     * of testng.
     *
     * @param useDefaultListeners Whether or not any default reports
     *                            should be added to tests.
     */
    public CustomTestNG(boolean useDefaultListeners) {
        init(useDefaultListeners);
    }

    private void init(boolean useDefaultListeners) {
        m_instance = this;

        m_useDefaultListeners = useDefaultListeners;
        m_configuration = new Configuration();
    }

    public int getStatus() {
        return m_status;
    }

    private void setStatus(int status) {
        m_status |= status;
    }

    /**
     * Sets the output directory where the reports will be created.
     *
     * @param outputdir The directory.
     */
    public void setOutputDirectory(final String outputdir) {
        if (org.testng.internal.Utils.isStringNotEmpty(outputdir)) {
            m_outputDir = outputdir;
        }
    }

    /**
     * If this method is passed true before run(), the default listeners
     * will not be used.
     * <ul>
     * <li>TestHTMLReporter
     * <li>JUnitXMLReporter
     * <li>XMLReporter
     * </ul>
     *
     * @see org.testng.reporters.TestHTMLReporter
     * @see org.testng.reporters.JUnitXMLReporter
     * @see org.testng.reporters.XMLReporter
     */
    public void setUseDefaultListeners(boolean useDefaultListeners) {
        m_useDefaultListeners = useDefaultListeners;
    }

    /**
     * Sets a jar containing a testng.xml file.
     *
     * @param jarPath
     */
    public void setTestJar(String jarPath) {
        m_jarPath = jarPath;
    }

    /**
     * Sets the path to the XML file in the test jar file.
     */
    public void setXmlPathInJar(String xmlPathInJar) {
        m_xmlPathInJar = xmlPathInJar;
    }

    public void initializeSuitesAndJarFile() {
        // The Eclipse plug-in (RemoteTestNG) might have invoked this method already
        // so don't initialize suites twice.
        if (m_isInitialized) {
            return;
        }

        m_isInitialized = true;
        if (m_suites.size() > 0) {
            //to parse the suite files (<suite-file>), if any
            for (org.testng.xml.XmlSuite s : m_suites) {
                List<String> suiteFiles = s.getSuiteFiles();
                for (int i = 0; i < suiteFiles.size(); i++) {
                    try {
                        Collection<XmlSuite> childSuites = getParser(suiteFiles.get(i)).parse();
                        for (org.testng.xml.XmlSuite cSuite : childSuites) {
                            cSuite.setParentSuite(s);
                            s.getChildSuites().add(cSuite);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace(System.out);
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace(System.out);
                    } catch (SAXException e) {
                        e.printStackTrace(System.out);
                    } catch (IOException e) {
                        e.printStackTrace(System.out);
                    }
                }

            }
            return;
        }

        //
        // Parse the suites that were passed on the command line
        //
        for (String suitePath : m_stringSuites) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("suiteXmlPath: \"" + suitePath + "\"");
            }
            try {
                Collection<org.testng.xml.XmlSuite> allSuites = getParser(suitePath).parse();

                for (org.testng.xml.XmlSuite s : allSuites) {
                    // If test names were specified, only run these test names
                    if (m_testNames != null) {
                        m_suites.add(extractTestNames(s, m_testNames));
                    } else {
                        m_suites.add(s);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace(System.out);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } catch (ParserConfigurationException e) {
                e.printStackTrace(System.out);
            } catch (SAXException e) {
                e.printStackTrace(System.out);
            } catch (Exception ex) {
                // Probably a Yaml exception, unnest it
                Throwable t = ex;
                while (t.getCause() != null) t = t.getCause();
//        t.printStackTrace();
                if (t instanceof org.testng.TestNGException) throw (org.testng.TestNGException) t;
                else throw new org.testng.TestNGException(t);
            }
        }

        //
        // jar path
        //
        // If suites were passed on the command line, they take precedence over the suite file
        // inside that jar path
        if (m_jarPath != null && m_stringSuites.size() > 0) {
            StringBuilder suites = new StringBuilder();
            for (String s : m_stringSuites) {
                suites.append(s);
            }
            org.testng.internal.Utils.log("TestNG", 2, "Ignoring the XML file inside " + m_jarPath + " and using "
                    + suites + " instead");
            return;
        }
        if (org.testng.internal.Utils.isStringEmpty(m_jarPath)) {
            return;
        }

        // We have a jar file and no XML file was specified: try to find an XML file inside the jar
        File jarFile = new File(m_jarPath);

        try {

            org.testng.internal.Utils.log("TestNG", 2, "Trying to open jar file:" + jarFile);

            JarFile jf = new JarFile(jarFile);
//      System.out.println("   result: " + jf);
            Enumeration<JarEntry> entries = jf.entries();
            List<String> classes = Lists.newArrayList();
            boolean foundTestngXml = false;
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (je.getName().equals(m_xmlPathInJar)) {
                    Parser parser = getParser(jf.getInputStream(je));
                    m_suites.addAll(parser.parse());
                    foundTestngXml = true;
                    break;
                } else if (je.getName().endsWith(".class")) {
                    int n = je.getName().length() - ".class".length();
                    classes.add(je.getName().replace("/", ".").substring(0, n));
                }
            }
            if (!foundTestngXml) {
                org.testng.internal.Utils.log("TestNG", 1,
                        "Couldn't find the " + m_xmlPathInJar + " in the jar file, running all the classes");
                org.testng.xml.XmlSuite xmlSuite = new org.testng.xml.XmlSuite();
                xmlSuite.setVerbose(0);
                xmlSuite.setName("Jar suite");
                XmlTest xmlTest = new XmlTest(xmlSuite);
                List<XmlClass> xmlClasses = Lists.newArrayList();
                for (String cls : classes) {
                    XmlClass xmlClass = new XmlClass(cls);
                    xmlClasses.add(xmlClass);
                }
                xmlTest.setXmlClasses(xmlClasses);
                m_suites.add(xmlSuite);
            }
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Parser getParser(String path) {
        Parser result = new Parser(path);
        initProcessor(result);
        return result;
    }

    private Parser getParser(InputStream is) {
        Parser result = new Parser(is);
        initProcessor(result);
        return result;
    }

    private void initProcessor(Parser result) {
        result.setPostProcessor(new OverrideProcessor(m_includedGroups, m_excludedGroups));
    }

    /**
     * If the XmlSuite contains at least one test named as testNames, return
     * an XmlSuite that's made only of these tests, otherwise, return the
     * original suite.
     */
    private static org.testng.xml.XmlSuite extractTestNames(org.testng.xml.XmlSuite s, List<String> testNames) {
        List<XmlTest> tests = Lists.newArrayList();
        for (XmlTest xt : s.getTests()) {
            for (String tn : testNames) {
                if (xt.getName().equals(tn)) {
                    tests.add(xt);
                }
            }
        }

        if (tests.size() == 0) {
            return s;
        } else {
            org.testng.xml.XmlSuite result = (org.testng.xml.XmlSuite) s.clone();
            result.getTests().clear();
            result.getTests().addAll(tests);
            return result;
        }
    }

    /**
     * Define the number of threads in the thread pool.
     */
    public void setThreadCount(int threadCount) {
        if (threadCount < 1) {
            exitWithError("Cannot use a threadCount parameter less than 1; 1 > " + threadCount);
        }

        m_threadCount = threadCount;
        m_useThreadCount = true;
    }

    /**
     * Define whether this run will be run in parallel mode.
     */
    public void setParallel(String parallel) {
        m_parallelMode = parallel;
        m_useParallelMode = true;
    }

    public void setCommandLineSuite(org.testng.xml.XmlSuite suite) {
        m_cmdlineSuites = Lists.newArrayList();
        m_cmdlineSuites.add(suite);
        m_suites.add(suite);
    }

    /**
     * Set the test classes to be run by this TestNG object.  This method
     * will create a dummy suite that will wrap these classes called
     * "Command Line Test".
     * <p/>
     * If used together with threadCount, parallel, groups, excludedGroups than this one must be set first.
     *
     * @param classes An array of classes that contain TestNG annotations.
     */
    public void setTestClasses(Class[] classes) {
        m_suites.clear();
        m_commandLineTestClasses = classes;
    }

    /**
     * Given a string com.example.Foo.f1, return an array where [0] is the class and [1]
     * is the method.
     */
    private String[] splitMethod(String m) {
        int index = m.lastIndexOf(".");
        if (index < 0) {
            throw new org.testng.TestNGException("Bad format for command line method:" + m
                    + ", expected <class>.<method>");
        }

        return new String[]{m.substring(0, index), m.substring(index + 1).replaceAll("\\*", "\\.\\*")};
    }

    /**
     * @param commandLineMethods a string with the form "com.example.Foo.f1,com.example.Bar.f2"
     * @return a list of XmlSuite objects that represent the list of classes and methods passed
     * in parameter.
     */
    private List<org.testng.xml.XmlSuite> createCommandLineSuitesForMethods(List<String> commandLineMethods) {
        //
        // Create the <classes> tag
        //
        Set<Class> classes = org.testng.internal.annotations.Sets.newHashSet();
        for (String m : commandLineMethods) {
            Class c = ClassHelper.forName(splitMethod(m)[0]);
            if (c != null) {
                classes.add(c);
            }
        }

        List<org.testng.xml.XmlSuite> result = createCommandLineSuitesForClasses(classes.toArray(new Class[0]));

        //
        // Add the method tags
        //
        List<XmlClass> xmlClasses = Lists.newArrayList();
        for (org.testng.xml.XmlSuite s : result) {
            for (XmlTest t : s.getTests()) {
                xmlClasses.addAll(t.getClasses());
            }
        }

        for (XmlClass xc : xmlClasses) {
            for (String m : commandLineMethods) {
                String[] split = splitMethod(m);
                String className = split[0];
                if (xc.getName().equals(className)) {
                    org.testng.xml.XmlInclude includedMethod = new org.testng.xml.XmlInclude(split[1]);
                    xc.getIncludedMethods().add(includedMethod);
                }
            }
        }

        return result;
    }

    private List<org.testng.xml.XmlSuite> createCommandLineSuitesForClasses(Class[] classes) {
        //
        // See if any of the classes has an xmlSuite or xmlTest attribute.
        // If it does, create the appropriate XmlSuite, otherwise, create
        // the default one
        //
        XmlClass[] xmlClasses = org.testng.internal.Utils.classesToXmlClasses(classes);
        Map<String, org.testng.xml.XmlSuite> suites = Maps.newHashMap();
        IAnnotationFinder finder = m_configuration.getAnnotationFinder();

        for (int i = 0; i < classes.length; i++) {
            Class c = classes[i];
            ITestAnnotation test = finder.findAnnotation(c, ITestAnnotation.class);
            String suiteName = getDefaultSuiteName();
            String testName = getDefaultTestName();
            boolean isJUnit = false;
            if (test != null) {
                suiteName = org.testng.internal.Utils.defaultIfStringEmpty(test.getSuiteName(), suiteName);
                testName = org.testng.internal.Utils.defaultIfStringEmpty(test.getTestName(), testName);
            } else {
                if (m_isMixed && JUnitTestFinder.isJUnitTest(c)) {
                    isJUnit = true;
                    testName = c.getName();
                }
            }
            org.testng.xml.XmlSuite xmlSuite = suites.get(suiteName);
            if (xmlSuite == null) {
                xmlSuite = new org.testng.xml.XmlSuite();
                xmlSuite.setName(suiteName);
                suites.put(suiteName, xmlSuite);
            }

            if (m_dataProviderThreadCount != null) {
                xmlSuite.setDataProviderThreadCount(m_dataProviderThreadCount);
            }
            XmlTest xmlTest = null;
            for (XmlTest xt : xmlSuite.getTests()) {
                if (xt.getName().equals(testName)) {
                    xmlTest = xt;
                    break;
                }
            }

            if (xmlTest == null) {
                xmlTest = new XmlTest(xmlSuite);
                xmlTest.setName(testName);
                xmlTest.setJUnit(isJUnit);
            }

            xmlTest.getXmlClasses().add(xmlClasses[i]);
        }

        return new ArrayList<org.testng.xml.XmlSuite>(suites.values());
    }

    public void addMethodSelector(String className, int priority) {
        m_methodDescriptors.put(className, priority);
    }

    /**
     * Set the suites file names to be run by this TestNG object. This method tries to load and
     * parse the specified TestNG suite xml files. If a file is missing, it is ignored.
     *
     * @param suites A list of paths to one more XML files defining the tests.  For example:
     *               <p/>
     *               <pre>
     *               TestNG tng = new TestNG();
     *               List<String> suites = Lists.newArrayList();
     *               suites.add("c:/tests/testng.xml");
     *               suites.add("c:/tests/testng2.xml");
     *               tng.setTestSuites(suites);
     *               tng.run();
     *               </pre>
     */
    public void setTestSuites(List<String> suites) {
        m_stringSuites = suites;
    }

    /**
     * Specifies the XmlSuite objects to run.
     *
     * @param suites
     * @see org.testng.xml.XmlSuite
     */
    public void setXmlSuites(List<org.testng.xml.XmlSuite> suites) {
        m_suites = suites;
    }

    /**
     * Define which groups will be excluded from this run.
     *
     * @param groups A list of group names separated by a comma.
     */
    public void setExcludedGroups(String groups) {
        m_excludedGroups = org.testng.internal.Utils.split(groups, ",");
    }


    /**
     * Define which groups will be included from this run.
     *
     * @param groups A list of group names separated by a comma.
     */
    public void setGroups(String groups) {
        m_includedGroups = org.testng.internal.Utils.split(groups, ",");
    }


    private void setTestRunnerFactoryClass(Class testRunnerFactoryClass) {
        setTestRunnerFactory((ITestRunnerFactory) ClassHelper.newInstance(testRunnerFactoryClass));
    }


    protected void setTestRunnerFactory(ITestRunnerFactory itrf) {
        m_testRunnerFactory = itrf;
    }

    public void setObjectFactory(Class c) {
        m_objectFactory = (org.testng.ITestObjectFactory) ClassHelper.newInstance(c);
    }

    public void setObjectFactory(org.testng.ITestObjectFactory factory) {
        m_objectFactory = factory;
    }

    /**
     * Define which listeners to user for this run.
     *
     * @param classes A list of classes, which must be either ISuiteListener,
     *                ITestListener or IReporter
     */
    public void setListenerClasses(List<Class> classes) {
        for (Class cls : classes) {
            addListener(ClassHelper.newInstance(cls));
        }
    }

    public void addListener(Object listener) {
        if (!(listener instanceof org.testng.ITestNGListener)) {
            exitWithError("Listener " + listener
                    + " must be one of ITestListener, ISuiteListener, IReporter, "
                    + " IAnnotationTransformer, IMethodInterceptor or IInvokedMethodListener");
        } else {
            if (listener instanceof org.testng.ISuiteListener) {
                addListener((org.testng.ISuiteListener) listener);
            }
            if (listener instanceof org.testng.ITestListener) {
                addListener((org.testng.ITestListener) listener);
            }
            if (listener instanceof org.testng.IReporter) {
                addListener((org.testng.IReporter) listener);
            }
            if (listener instanceof org.testng.IAnnotationTransformer) {
                setAnnotationTransformer((org.testng.IAnnotationTransformer) listener);
            }
            if (listener instanceof IMethodInterceptor) {
                setMethodInterceptor((IMethodInterceptor) listener);
            }
            if (listener instanceof IInvokedMethodListener) {
                addInvokedMethodListener((IInvokedMethodListener) listener);
            }
            if (listener instanceof IHookable) {
                setHookable((IHookable) listener);
            }
            if (listener instanceof IConfigurable) {
                setConfigurable((IConfigurable) listener);
            }
            if (listener instanceof org.testng.IExecutionListener) {
                addExecutionListener((org.testng.IExecutionListener) listener);
            }
            if (listener instanceof IConfigurationListener) {
                getConfiguration().addConfigurationListener((IConfigurationListener) listener);
            }
        }
    }

    public void addListener(IInvokedMethodListener listener) {
        m_invokedMethodListeners.add(listener);
    }

    public void addListener(org.testng.ISuiteListener listener) {
        if (null != listener) {
            m_suiteListeners.add(listener);
        }
    }

    public void addListener(org.testng.ITestListener listener) {
        if (null != listener) {
            m_testListeners.add(listener);
        }
    }

    public void addListener(org.testng.IReporter listener) {
        if (null != listener) {
            m_reporters.add(listener);
        }
    }

    public void addInvokedMethodListener(IInvokedMethodListener listener) {
        m_invokedMethodListeners.add(listener);
    }

    public Set<org.testng.IReporter> getReporters() {
        return m_reporters;
    }

    public List<org.testng.ITestListener> getTestListeners() {
        return m_testListeners;
    }

    public List<org.testng.ISuiteListener> getSuiteListeners() {
        return m_suiteListeners;
    }

    /**
     * If m_verbose gets set, it will override the verbose setting in testng.xml
     */
    private Integer m_verbose = null;

    private final org.testng.IAnnotationTransformer m_defaultAnnoProcessor = new DefaultAnnotationTransformer();
    private org.testng.IAnnotationTransformer m_annotationTransformer = m_defaultAnnoProcessor;

    private Boolean m_skipFailedInvocationCounts = false;

    private IMethodInterceptor m_methodInterceptor = null;

    /**
     * The list of test names to run from the given suite
     */
    private List<String> m_testNames;

    private Integer m_suiteThreadPoolSize = CommandLineArgs.SUITE_THREAD_POOL_SIZE_DEFAULT;

    private boolean m_randomizeSuites = Boolean.FALSE;

    private boolean m_preserveOrder = false;
    private Boolean m_groupByInstances;

    private IConfiguration m_configuration;

    /**
     * Sets the level of verbosity. This value will override the value specified
     * in the test suites.
     *
     * @param verbose the verbosity level (0 to 10 where 10 is most detailed)
     *                Actually, this is a lie:  you can specify -1 and this will put TestNG
     *                in debug mode (no longer slicing off stack traces and all).
     */
    public void setVerbose(int verbose) {
        m_verbose = verbose;
    }

    private void initializeCommandLineSuites() {
        if (m_commandLineTestClasses != null || m_commandLineMethods != null) {
            if (null != m_commandLineMethods) {
                m_cmdlineSuites = createCommandLineSuitesForMethods(m_commandLineMethods);
            } else {
                m_cmdlineSuites = createCommandLineSuitesForClasses(m_commandLineTestClasses);
            }

            for (org.testng.xml.XmlSuite s : m_cmdlineSuites) {
                for (XmlTest t : s.getTests()) {
                    t.setPreserveOrder(m_preserveOrder ? "true " : "false");
                }
                m_suites.add(s);
                if (m_groupByInstances != null) {
                    s.setGroupByInstances(m_groupByInstances);
                }
            }
        }
    }

    private void initializeCommandLineSuitesParams() {
        if (null == m_cmdlineSuites) {
            return;
        }

        for (org.testng.xml.XmlSuite s : m_cmdlineSuites) {
            if (m_useThreadCount) {
                s.setThreadCount(m_threadCount);
            }
            if (m_useParallelMode) {
                s.setParallel(m_parallelMode);
            }
            if (m_configFailurePolicy != null) {
                s.setConfigFailurePolicy(m_configFailurePolicy.toString());
            }
        }

    }

    private void initializeCommandLineSuitesGroups() {
        // If groups were specified on the command line, they should override groups
        // specified in the XML file
        boolean hasIncludedGroups = null != m_includedGroups && m_includedGroups.length > 0;
        boolean hasExcludedGroups = null != m_excludedGroups && m_excludedGroups.length > 0;
        List<org.testng.xml.XmlSuite> suites = m_cmdlineSuites != null ? m_cmdlineSuites : m_suites;
        if (hasIncludedGroups || hasExcludedGroups) {
            for (org.testng.xml.XmlSuite s : suites) {
                //set on each test, instead of just the first one of the suite
                for (XmlTest t : s.getTests()) {
                    if (hasIncludedGroups) {
                        t.setIncludedGroups(Arrays.asList(m_includedGroups));
                    }
                    if (hasExcludedGroups) {
                        t.setExcludedGroups(Arrays.asList(m_excludedGroups));
                    }
                }
            }
        }
    }

    private void addReporter(Class<? extends org.testng.IReporter> r) {
        if (!m_reporters.contains(r)) {
            m_reporters.add(ClassHelper.newInstance(r));
        }
    }

    private void initializeDefaultListeners() {
        m_testListeners.add(new ExitCodeListener(this));

        if (m_useDefaultListeners) {
            addReporter(SuiteHTMLReporter.class);
            addReporter(org.testng.reporters.jq.Main.class);
            addReporter(FailedReporter.class);
            addReporter(org.testng.reporters.XMLReporter.class);
            if (System.getProperty("oldDestngEmailableReporter") != null) {
                addReporter(EmailableReporter.class);
            } else {
                addReporter(EmailableReporter2.class);
            }
            addReporter(JUnitReportReporter.class);
            if (m_verbose != null && m_verbose > 4) {
                addListener(new org.testng.reporters.VerboseReporter("[TestNG] "));
            }
        }
    }

    private void initializeConfiguration() {
        org.testng.ITestObjectFactory factory = m_objectFactory;
        //
        // Install the listeners found in ServiceLoader (or use the class
        // loader for tests, if specified).
        //
        addServiceLoaderListeners();

        //
        // Install the listeners found in the suites
        //
        for (org.testng.xml.XmlSuite s : m_suites) {
            for (String listenerName : s.getListeners()) {
                Class<?> listenerClass = ClassHelper.forName(listenerName);

                // If specified listener does not exist, a TestNGException will be thrown
                if (listenerClass == null) {
                    throw new org.testng.TestNGException("Listener " + listenerName
                            + " was not found in project's classpath");
                }

                Object listener = ClassHelper.newInstance(listenerClass);
                addListener(listener);
            }

            //
            // Install the method selectors
            //
            for (org.testng.xml.XmlMethodSelector methodSelector : s.getMethodSelectors()) {
                addMethodSelector(methodSelector.getClassName(), methodSelector.getPriority());
            }

            //
            // Find if we have an object factory
            //
            if (s.getObjectFactory() != null) {
                if (factory == null) {
                    factory = s.getObjectFactory();
                } else {
                    throw new org.testng.TestNGException("Found more than one object-factory tag in your suites");
                }
            }
        }

        m_configuration.setAnnotationFinder(new JDK15AnnotationFinder(getAnnotationTransformer()));
        m_configuration.setHookable(m_hookable);
        m_configuration.setConfigurable(m_configurable);
        m_configuration.setObjectFactory(factory);
    }

    /**
     * Using reflection to remain Java 5 compliant.
     */
    private void addServiceLoaderListeners() {
        try {
            Class c = Class.forName("java.util.ServiceLoader");
            List<Object> parameters = Lists.newArrayList();
            parameters.add(org.testng.ITestNGListener.class);
            Method loadMethod;
            if (m_serviceLoaderClassLoader != null) {
                parameters.add(m_serviceLoaderClassLoader);
                loadMethod = c.getMethod("load", Class.class, ClassLoader.class);
            } else {
                loadMethod = c.getMethod("load", Class.class);
            }
            Iterable<org.testng.ITestNGListener> loader =
                    (Iterable<org.testng.ITestNGListener>) loadMethod.invoke(c, parameters.toArray());
//      Object loader = c.
//      ServiceLoader<ITestNGListener> loader = m_serviceLoaderClassLoader != null
//      ? ServiceLoader.load(ITestNGListener.class, m_serviceLoaderClassLoader)
//          : ServiceLoader.load(ITestNGListener.class);
            for (org.testng.ITestNGListener l : loader) {
                org.testng.internal.Utils.log("[TestNG]", 2, "Adding ServiceLoader listener:" + l);
                addListener(l);
                addServiceLoaderListener(l);
            }
        } catch (ClassNotFoundException ex) {
            // Ignore
        } catch (NoSuchMethodException ex) {
            // Ignore
        } catch (IllegalAccessException ex) {
            // Ignore
        } catch (InvocationTargetException ex) {
            // Ignore
        }
    }

    /**
     * Before suites are executed, do a sanity check to ensure all required
     * conditions are met. If not, throw an exception to stop test execution
     *
     * @throws org.testng.TestNGException if the sanity check fails
     */
    private void sanityCheck() {
        checkTestNames(m_suites);
        checkSuiteNames(m_suites);
    }

    /**
     * Ensure that two XmlTest within the same XmlSuite don't have the same name
     */
    private void checkTestNames(List<org.testng.xml.XmlSuite> suites) {
        for (org.testng.xml.XmlSuite suite : suites) {
            Set<String> testNames = org.testng.internal.annotations.Sets.newHashSet();
            for (XmlTest test : suite.getTests()) {
                if (testNames.contains(test.getName())) {
                    throw new org.testng.TestNGException("Two tests in the same suite "
                            + "cannot have the same name: " + test.getName());
                } else {
                    testNames.add(test.getName());
                }
            }
            checkTestNames(suite.getChildSuites());
        }
    }

    /**
     * Ensure that two XmlSuite don't have the same name
     * Otherwise will be clash in SuiteRunnerMap
     * See issue #302
     */
    private void checkSuiteNames(List<org.testng.xml.XmlSuite> suites) {
        checkSuiteNamesInternal(suites, org.testng.internal.annotations.Sets.<String>newHashSet());
    }

    private void checkSuiteNamesInternal(List<org.testng.xml.XmlSuite> suites, Set<String> names) {
        for (org.testng.xml.XmlSuite suite : suites) {
            final String name = suite.getName();
            if (names.contains(name)) {
                throw new org.testng.TestNGException("Two suites cannot have the same name: " + name);
            }
            names.add(name);
            checkSuiteNamesInternal(suite.getChildSuites(), names);
        }
    }

    /**
     * Import tests.
     */
    public void run() {
        initializeSuitesAndJarFile();
        initializeConfiguration();
        initializeDefaultListeners();
        initializeCommandLineSuites();
        initializeCommandLineSuitesParams();
        initializeCommandLineSuitesGroups();

        sanityCheck();

        List<org.testng.ISuite> suiteRunners = null;

        runExecutionListeners(true /* start */);

        m_start = System.currentTimeMillis();

        //
        // Slave mode
        //
        if (m_slavefileName != null) {
            SuiteSlave slave = new SuiteSlave(m_slavefileName, this);
            slave.waitForSuites();
        }

        //
        // Regular mode
        //
        else if (m_masterfileName == null) {
            suiteRunners = runSuitesLocally();

            // Bruce 2015-2-15
            // We only define one suite in ATESTestImport.xml
            new TestImporter().composeAndImport(suiteRunners.get(0).getAllMethods());
        }

        //
        // Master mode
        //
        else {
            SuiteDispatcher dispatcher = new SuiteDispatcher(m_masterfileName);
            suiteRunners = dispatcher.dispatch(getConfiguration(),
                    m_suites, getOutputDirectory(),
                    getTestListeners());
        }

        m_end = System.currentTimeMillis();
        runExecutionListeners(false /* finish */);

        if (null != suiteRunners) {
            generateReports(suiteRunners);
        }

        if (!m_hasTests) {
            setStatus(HAS_NO_TEST);
            if (org.testng.TestRunner.getVerbose() > 1) {
                System.err.println("[TestNG] No tests found. Nothing was run");
                usage();
            }
        }
    }

    private void p(String string) {
        System.out.println("[TestNG] " + string);
    }

    private void runExecutionListeners(boolean start) {
        for (List<org.testng.IExecutionListener> listeners
                : Arrays.asList(m_executionListeners, m_configuration.getExecutionListeners())) {
            for (org.testng.IExecutionListener l : listeners) {
                if (start) l.onExecutionStart();
                else l.onExecutionFinish();
            }
        }
    }

    public void addExecutionListener(org.testng.IExecutionListener l) {
        m_executionListeners.add(l);
    }

    private static void usage() {
        if (m_jCommander == null) {
            m_jCommander = new JCommander(new CommandLineArgs());
        }
        m_jCommander.usage();
    }

    private void generateReports(List<org.testng.ISuite> suiteRunners) {
        for (org.testng.IReporter reporter : m_reporters) {
            try {
                long start = System.currentTimeMillis();
                reporter.generateReport(m_suites, suiteRunners, m_outputDir);
                org.testng.internal.Utils.log("TestNG", 2, "Time taken by " + reporter + ": "
                        + (System.currentTimeMillis() - start) + " ms");
            } catch (Exception ex) {
                System.err.println("[TestNG] Reporter " + reporter + " failed");
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     * This needs to be public for maven2, for now..At least
     * until an alternative mechanism is found.
     */
    public List<org.testng.ISuite> runSuitesLocally() {
        SuiteRunnerMap suiteRunnerMap = new SuiteRunnerMap();
        if (m_suites.size() > 0) {
            if (m_suites.get(0).getVerbose() >= 2) {
                Version.displayBanner();
            }

            // First initialize the suite runners to ensure there are no configuration issues.
            // Create a map with XmlSuite as key and corresponding SuiteRunner as value
            for (org.testng.xml.XmlSuite xmlSuite : m_suites) {
                createSuiteRunners(suiteRunnerMap, xmlSuite);
            }

            // Bruce 2015-2-15
            // Comment the execution part.
            //
            // Run suites
            //
//      if (m_suiteThreadPoolSize == 1 && !m_randomizeSuites) {
//        // Single threaded and not randomized: run the suites in order
//        for (XmlSuite xmlSuite : m_suites) {
//          runSuitesSequentially(xmlSuite, suiteRunnerMap, getVerbose(xmlSuite),
//              getDefaultSuiteName());
//        }
//      } else {
//        // Multithreaded: generate a dynamic graph that stores the suite hierarchy. This is then
//        // used to run related suites in specific order. Parent suites are run only
//        // once all the child suites have completed execution
//        DynamicGraph<ISuite> suiteGraph = new DynamicGraph<ISuite>();
//        for (XmlSuite xmlSuite : m_suites) {
//          populateSuiteGraph(suiteGraph, suiteRunnerMap, xmlSuite);
//        }
//
//        IThreadWorkerFactory<ISuite> factory = new SuiteWorkerFactory(suiteRunnerMap,
//          0 /* verbose hasn't been set yet */, getDefaultSuiteName());
//        GraphThreadPoolExecutor<ISuite> pooledExecutor =
//          new GraphThreadPoolExecutor<ISuite>(suiteGraph, factory, m_suiteThreadPoolSize,
//          m_suiteThreadPoolSize, Integer.MAX_VALUE, TimeUnit.MILLISECONDS,
//          new LinkedBlockingQueue<Runnable>());
//
//        Utils.log("TestNG", 2, "Starting executor for all suites");
//        // Run all suites in parallel
//        pooledExecutor.run();
//        try {
//          pooledExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
//          pooledExecutor.shutdownNow();
//        }
//        catch (InterruptedException handled) {
//          Thread.currentThread().interrupt();
//          error("Error waiting for concurrent executors to finish " + handled.getMessage());
//        }
//      }
        } else {
            setStatus(HAS_NO_TEST);
            error("No test suite found. Nothing to run");
            usage();
        }

        //
        // Generate the suites report
        //
        return Lists.newArrayList(suiteRunnerMap.values());
    }

    private static void error(String s) {
        LOGGER.error(s);
    }

    /**
     * @return the verbose level, checking in order: the verbose level on
     * the suite, the verbose level on the TestNG object, or 1.
     */
    private int getVerbose(org.testng.xml.XmlSuite xmlSuite) {
        int result = xmlSuite.getVerbose() != null ? xmlSuite.getVerbose()
                : (m_verbose != null ? m_verbose : DEFAULT_VERBOSE);
        return result;
    }

    /**
     * Recursively runs suites. Runs the children suites before running the parent
     * suite. This is done so that the results for parent suite can reflect the
     * combined results of the children suites.
     *
     * @param xmlSuite         XML Suite to be executed
     * @param suiteRunnerMap   Maps {@code XmlSuite}s to respective {@code ISuite}
     * @param verbose          verbose level
     * @param defaultSuiteName default suite name
     */
    private void runSuitesSequentially(org.testng.xml.XmlSuite xmlSuite,
                                       SuiteRunnerMap suiteRunnerMap, int verbose, String defaultSuiteName) {
        for (org.testng.xml.XmlSuite childSuite : xmlSuite.getChildSuites()) {
            runSuitesSequentially(childSuite, suiteRunnerMap, verbose, defaultSuiteName);
        }
        SuiteRunnerWorker srw = new SuiteRunnerWorker(suiteRunnerMap.get(xmlSuite), suiteRunnerMap,
                verbose, defaultSuiteName);
        srw.run();
    }

    /**
     * Populates the dynamic graph with the reverse hierarchy of suites. Edges are
     * added pointing from child suite runners to parent suite runners, hence making
     * parent suite runners dependent on all the child suite runners
     *
     * @param suiteGraph     dynamic graph representing the reverse hierarchy of SuiteRunners
     * @param suiteRunnerMap Map with XMLSuite as key and its respective SuiteRunner as value
     * @param xmlSuite       XML Suite
     */
    private void populateSuiteGraph(DynamicGraph<org.testng.ISuite> suiteGraph /* OUT */,
                                    SuiteRunnerMap suiteRunnerMap, org.testng.xml.XmlSuite xmlSuite) {
        org.testng.ISuite parentSuiteRunner = suiteRunnerMap.get(xmlSuite);
        if (xmlSuite.getChildSuites().isEmpty()) {
            suiteGraph.addNode(parentSuiteRunner);
        } else {
            for (org.testng.xml.XmlSuite childSuite : xmlSuite.getChildSuites()) {
                suiteGraph.addEdge(parentSuiteRunner, suiteRunnerMap.get(childSuite));
                populateSuiteGraph(suiteGraph, suiteRunnerMap, childSuite);
            }
        }
    }

    /**
     * Creates the {@code SuiteRunner}s and populates the suite runner map with
     * this information
     *
     * @param suiteRunnerMap Map with XMLSuite as key and it's respective
     *                       SuiteRunner as value. This is updated as part of this method call
     * @param xmlSuite       Xml Suite (and its children) for which {@code SuiteRunner}s are created
     */
    private void createSuiteRunners(SuiteRunnerMap suiteRunnerMap /* OUT */, org.testng.xml.XmlSuite xmlSuite) {
        if (null != m_isJUnit && !m_isJUnit.equals(org.testng.xml.XmlSuite.DEFAULT_JUNIT)) {
            xmlSuite.setJUnit(m_isJUnit);
        }

        // If the skip flag was invoked on the command line, it
        // takes precedence
        if (null != m_skipFailedInvocationCounts) {
            xmlSuite.setSkipFailedInvocationCounts(m_skipFailedInvocationCounts);
        }

        // Override the XmlSuite verbose value with the one from TestNG
        if (m_verbose != null) {
            xmlSuite.setVerbose(m_verbose);
        }

        if (null != m_configFailurePolicy) {
            xmlSuite.setConfigFailurePolicy(m_configFailurePolicy);
        }

        for (XmlTest t : xmlSuite.getTests()) {
            for (Map.Entry<String, Integer> ms : m_methodDescriptors.entrySet()) {
                org.testng.xml.XmlMethodSelector xms = new org.testng.xml.XmlMethodSelector();
                xms.setName(ms.getKey());
                xms.setPriority(ms.getValue());
                t.getMethodSelectors().add(xms);
            }
        }

        suiteRunnerMap.put(xmlSuite, createSuiteRunner(xmlSuite));

        for (org.testng.xml.XmlSuite childSuite : xmlSuite.getChildSuites()) {
            createSuiteRunners(suiteRunnerMap, childSuite);
        }
    }

    /**
     * Creates a suite runner and configures its initial state
     *
     * @param xmlSuite
     * @return returns the newly created suite runner
     */
    private org.testng.SuiteRunner createSuiteRunner(org.testng.xml.XmlSuite xmlSuite) {
        org.testng.SuiteRunner result = new org.testng.SuiteRunner(getConfiguration(), xmlSuite,
                m_outputDir,
                m_testRunnerFactory,
                m_useDefaultListeners);//,
                //m_methodInterceptor,
                //m_invokedMethodListeners,
                //m_testListeners);

        for (org.testng.ISuiteListener isl : m_suiteListeners) {
            result.addListener(isl);
        }

        for (org.testng.IReporter r : result.getReporters()) {
            addListener(r);
        }

        for (IConfigurationListener cl : m_configuration.getConfigurationListeners()) {
            result.addListener(cl);
        }

        return result;
    }

    protected IConfiguration getConfiguration() {
        return m_configuration;
    }

    /**
     * The TestNG entry point for command line execution.
     *
     * @param argv the TestNG command line parameters.
     * @throws FileNotFoundException
     */
    public static void main(String[] argv) {
        CustomTestNG testng = privateMain(argv, null);
		System.exit(0);
        // System.exit(testng.getStatus()); // Allen Liu: Comment this, so that it won't fail Jenkins job.
    }

    /**
     * <B>Note</B>: this method is not part of the public API and is meant for internal usage only.
     */
    public static CustomTestNG privateMain(String[] argv, org.testng.ITestListener listener) {
        CustomTestNG result = new CustomTestNG();

        if (null != listener) {
            result.addListener(listener);
        }

        //
        // Parse the arguments
        //
        try {
            CommandLineArgs cla = new CommandLineArgs();
            m_jCommander = new JCommander(cla, argv);
            validateCommandLineParameters(cla);
            result.configure(cla);
        } catch (ParameterException ex) {
            exitWithError(ex.getMessage());
        }

        //
        // Run
        //
        try {
            result.run();
        } catch (org.testng.TestNGException ex) {
            if (org.testng.TestRunner.getVerbose() > 1) {
                ex.printStackTrace(System.out);
            } else {
                error(ex.getMessage());
            }
            result.setStatus(HAS_FAILURE);
        }

        return result;
    }

    /**
     * Configure the TestNG instance based on the command line parameters.
     */
    protected void configure(CommandLineArgs cla) {
        if (cla.verbose != null) {
            setVerbose(cla.verbose);
        }
        setOutputDirectory(cla.outputDirectory);

        String testClasses = cla.testClass;
        if (null != testClasses) {
            String[] strClasses = testClasses.split(",");
            List<Class> classes = Lists.newArrayList();
            for (String c : strClasses) {
                classes.add(ClassHelper.fileToClass(c));
            }

            setTestClasses(classes.toArray(new Class[classes.size()]));
        }

        setOutputDirectory(cla.outputDirectory);

        if (cla.testNames != null) {
            setTestNames(Arrays.asList(cla.testNames.split(",")));
        }

//    List<String> testNgXml = (List<String>) cmdLineArgs.get(CommandLineArgs.SUITE_DEF);
//    if (null != testNgXml) {
//      setTestSuites(testNgXml);
//    }

        // Note: can't use a Boolean field here because we are allowing a boolean
        // parameter with an arity of 1 ("-usedefaultlisteners false")
        if (cla.useDefaultListeners != null) {
            setUseDefaultListeners("true".equalsIgnoreCase(cla.useDefaultListeners));
        }

        setGroups(cla.groups);
        setExcludedGroups(cla.excludedGroups);
        setTestJar(cla.testJar);
        setXmlPathInJar(cla.xmlPathInJar);
        setJUnit(cla.junit);
        setMixed(cla.mixed);
        setMaster(cla.master);
        setSlave(cla.slave);
        setSkipFailedInvocationCounts(cla.skipFailedInvocationCounts);
        if (cla.parallelMode != null) {
            setParallel(cla.parallelMode);
        }
        if (cla.configFailurePolicy != null) {
            setConfigFailurePolicy(cla.configFailurePolicy);
        }
        if (cla.threadCount != null) {
            setThreadCount(cla.threadCount);
        }
        if (cla.dataProviderThreadCount != null) {
            setDataProviderThreadCount(cla.dataProviderThreadCount);
        }
        if (cla.suiteName != null) {
            setDefaultSuiteName(cla.suiteName);
        }
        if (cla.testName != null) {
            setDefaultTestName(cla.testName);
        }
        if (cla.listener != null) {
            String sep = ";";
            if (cla.listener.indexOf(",") >= 0) {
                sep = ",";
            }
            String[] strs = org.testng.internal.Utils.split(cla.listener, sep);
            List<Class> classes = Lists.newArrayList();

            for (String cls : strs) {
                classes.add(ClassHelper.fileToClass(cls));
            }

            setListenerClasses(classes);
        }

        if (null != cla.methodSelectors) {
            String[] strs = org.testng.internal.Utils.split(cla.methodSelectors, ",");
            for (String cls : strs) {
                String[] sel = org.testng.internal.Utils.split(cls, ":");
                try {
                    if (sel.length == 2) {
                        addMethodSelector(sel[0], Integer.valueOf(sel[1]));
                    } else {
                        error("Method selector value was not in the format org.example.Selector:4");
                    }
                } catch (NumberFormatException nfe) {
                    error("Method selector value was not in the format org.example.Selector:4");
                }
            }
        }

        if (cla.objectFactory != null) {
            setObjectFactory(ClassHelper.fileToClass(cla.objectFactory));
        }
        if (cla.testRunnerFactory != null) {
            setTestRunnerFactoryClass(
                    ClassHelper.fileToClass(cla.testRunnerFactory));
        }

        if (cla.reporter != null) {
            ReporterConfig reporterConfig = ReporterConfig.deserialize(cla.reporter);
            addReporter(reporterConfig);
        }

        if (cla.commandLineMethods.size() > 0) {
            m_commandLineMethods = cla.commandLineMethods;
        }

        if (cla.suiteFiles != null) {
            setTestSuites(cla.suiteFiles);
        }

        setSuiteThreadPoolSize(cla.suiteThreadPoolSize);
        setRandomizeSuites(cla.randomizeSuites);
    }

    public void setSuiteThreadPoolSize(Integer suiteThreadPoolSize) {
        m_suiteThreadPoolSize = suiteThreadPoolSize;
    }

    public Integer getSuiteThreadPoolSize() {
        return m_suiteThreadPoolSize;
    }

    public void setRandomizeSuites(boolean randomizeSuites) {
        m_randomizeSuites = randomizeSuites;
    }

    /**
     * This method is invoked by Maven's Surefire, only remove it once
     * Surefire has been modified to no longer call it.
     */
    public void setSourcePath(String path) {
        // nop
    }

    /**
     * This method is invoked by Maven's Surefire to configure the runner,
     * do not remove unless you know for sure that Surefire has been updated
     * to use the new configure(CommandLineArgs) method.
     *
     * @deprecated use new configure(CommandLineArgs) method
     */
    @SuppressWarnings({"unchecked"})
    @Deprecated
    public void configure(Map cmdLineArgs) {
        CommandLineArgs result = new CommandLineArgs();

        Integer verbose = (Integer) cmdLineArgs.get(CommandLineArgs.LOG);
        if (null != verbose) {
            result.verbose = verbose;
        }
        result.outputDirectory = (String) cmdLineArgs.get(CommandLineArgs.OUTPUT_DIRECTORY);

        String testClasses = (String) cmdLineArgs.get(CommandLineArgs.TEST_CLASS);
        if (null != testClasses) {
            result.testClass = testClasses;
        }

        String testNames = (String) cmdLineArgs.get(CommandLineArgs.TEST_NAMES);
        if (testNames != null) {
            result.testNames = testNames;
        }

        String useDefaultListeners = (String) cmdLineArgs.get(CommandLineArgs.USE_DEFAULT_LISTENERS);
        if (null != useDefaultListeners) {
            result.useDefaultListeners = useDefaultListeners;
        }

        result.groups = (String) cmdLineArgs.get(CommandLineArgs.GROUPS);
        result.excludedGroups = (String) cmdLineArgs.get(CommandLineArgs.EXCLUDED_GROUPS);
        result.testJar = (String) cmdLineArgs.get(CommandLineArgs.TEST_JAR);
        result.xmlPathInJar = (String) cmdLineArgs.get(CommandLineArgs.XML_PATH_IN_JAR);
        result.junit = (Boolean) cmdLineArgs.get(CommandLineArgs.JUNIT);
        result.mixed = (Boolean) cmdLineArgs.get(CommandLineArgs.MIXED);
        result.master = (String) cmdLineArgs.get(CommandLineArgs.MASTER);
        result.slave = (String) cmdLineArgs.get(CommandLineArgs.SLAVE);
        result.skipFailedInvocationCounts = (Boolean) cmdLineArgs.get(
                CommandLineArgs.SKIP_FAILED_INVOCATION_COUNTS);
        String parallelMode = (String) cmdLineArgs.get(CommandLineArgs.PARALLEL);
        if (parallelMode != null) {
            result.parallelMode = parallelMode;
        }

        String threadCount = (String) cmdLineArgs.get(CommandLineArgs.THREAD_COUNT);
        if (threadCount != null) {
            result.threadCount = Integer.parseInt(threadCount);
        }

        // Not supported by Surefire yet
        Integer dptc = (Integer) cmdLineArgs.get(CommandLineArgs.DATA_PROVIDER_THREAD_COUNT);
        if (dptc != null) {
            result.dataProviderThreadCount = dptc;
        }
        String defaultSuiteName = (String) cmdLineArgs.get(CommandLineArgs.SUITE_NAME);
        if (defaultSuiteName != null) {
            result.suiteName = defaultSuiteName;
        }

        String defaultTestName = (String) cmdLineArgs.get(CommandLineArgs.TEST_NAME);
        if (defaultTestName != null) {
            result.testName = defaultTestName;
        }

        Object listeners = cmdLineArgs.get(CommandLineArgs.LISTENER);
        if (listeners instanceof List) {
            result.listener = org.testng.internal.Utils.join((List<?>) listeners, ",");
        } else {
            result.listener = (String) listeners;
        }

        String ms = (String) cmdLineArgs.get(CommandLineArgs.METHOD_SELECTORS);
        if (null != ms) {
            result.methodSelectors = ms;
        }

        String objectFactory = (String) cmdLineArgs.get(CommandLineArgs.OBJECT_FACTORY);
        if (null != objectFactory) {
            result.objectFactory = objectFactory;
        }

        String runnerFactory = (String) cmdLineArgs.get(CommandLineArgs.TEST_RUNNER_FACTORY);
        if (null != runnerFactory) {
            result.testRunnerFactory = runnerFactory;
        }

        String reporterConfigs = (String) cmdLineArgs.get(CommandLineArgs.REPORTER);
        if (reporterConfigs != null) {
            result.reporter = reporterConfigs;
        }

        String failurePolicy = (String) cmdLineArgs.get(CommandLineArgs.CONFIG_FAILURE_POLICY);
        if (failurePolicy != null) {
            result.configFailurePolicy = failurePolicy;
        }

        configure(result);
    }

    /**
     * Only run the specified tests from the suite.
     */
    public void setTestNames(List<String> testNames) {
        m_testNames = testNames;
    }

    public void setSkipFailedInvocationCounts(Boolean skip) {
        m_skipFailedInvocationCounts = skip;
    }

    private void addReporter(ReporterConfig reporterConfig) {
        Object instance = reporterConfig.newReporterInstance();
        if (instance != null) {
            addListener(instance);
        } else {
            LOGGER.warn("Could not find reporte class : " + reporterConfig.getClassName());
        }
    }

    /**
     * Specify if this run should be in Master-Slave mode as Master
     *
     * @param fileName remote.properties path
     */
    public void setMaster(String fileName) {
        m_masterfileName = fileName;
    }

    /**
     * Specify if this run should be in Master-Slave mode as slave
     *
     * @param fileName remote.properties path
     */
    public void setSlave(String fileName) {
        m_slavefileName = fileName;
    }

    /**
     * Specify if this run should be made in JUnit mode
     *
     * @param isJUnit
     */
    public void setJUnit(Boolean isJUnit) {
        m_isJUnit = isJUnit;
    }

    /**
     * Specify if this run should be made in mixed mode
     */
    public void setMixed(Boolean isMixed) {
        if (isMixed == null) {
            return;
        }
        m_isMixed = isMixed;
    }

    /**
     * @deprecated The TestNG version is now established at load time. This
     * method is not required anymore and is now a no-op.
     */
    @Deprecated
    public static void setTestNGVersion() {
        LOGGER.info("setTestNGVersion has been deprecated.");
    }

    /**
     * Returns true if this is the JDK 1.4 JAR version of TestNG, false otherwise.
     *
     * @return true if this is the JDK 1.4 JAR version of TestNG, false otherwise.
     */
    @Deprecated
    public static boolean isJdk14() {
        return false;
    }

    /**
     * Double check that the command line parameters are valid.
     */
    protected static void validateCommandLineParameters(CommandLineArgs args) {
        String testClasses = args.testClass;
        List<String> testNgXml = args.suiteFiles;
        String testJar = args.testJar;
        String slave = args.slave;
        List<String> methods = args.commandLineMethods;

        if (testClasses == null && slave == null && testJar == null
                && (testNgXml == null || testNgXml.isEmpty())
                && (methods == null || methods.isEmpty())) {
            throw new ParameterException("You need to specify at least one testng.xml, one class"
                    + " or one method");
        }

        String groups = args.groups;
        String excludedGroups = args.excludedGroups;

        if (testJar == null &&
                (null != groups || null != excludedGroups) && testClasses == null
                && (testNgXml == null || testNgXml.isEmpty())) {
            throw new ParameterException("Groups option should be used with testclass option");
        }

        if (args.slave != null && args.master != null) {
            throw new ParameterException(CommandLineArgs.SLAVE + " can't be combined with "
                    + CommandLineArgs.MASTER);
        }

        Boolean junit = args.junit;
        Boolean mixed = args.mixed;
        if (junit && mixed) {
            throw new ParameterException(CommandLineArgs.MIXED + " can't be combined with "
                    + CommandLineArgs.JUNIT);
        }
    }

    /**
     * @return true if at least one test failed.
     */
    public boolean hasFailure() {
        return (getStatus() & HAS_FAILURE) == HAS_FAILURE;
    }

    /**
     * @return true if at least one test failed within success percentage.
     */
    public boolean hasFailureWithinSuccessPercentage() {
        return (getStatus() & HAS_FSP) == HAS_FSP;
    }

    /**
     * @return true if at least one test was skipped.
     */
    public boolean hasSkip() {
        return (getStatus() & HAS_SKIPPED) == HAS_SKIPPED;
    }

    static void exitWithError(String msg) {
        System.err.println(msg);
        usage();
        System.exit(1);
    }

    public String getOutputDirectory() {
        return m_outputDir;
    }

    public org.testng.IAnnotationTransformer getAnnotationTransformer() {
        return m_annotationTransformer;
    }

    public void setAnnotationTransformer(org.testng.IAnnotationTransformer t) {
        // compare by reference!
        if (m_annotationTransformer != m_defaultAnnoProcessor && m_annotationTransformer != t) {
            LOGGER.warn("AnnotationTransformer already set");
        }
        m_annotationTransformer = t;
    }

    /**
     * @return the defaultSuiteName
     */
    public String getDefaultSuiteName() {
        return m_defaultSuiteName;
    }

    /**
     * @param defaultSuiteName the defaultSuiteName to set
     */
    public void setDefaultSuiteName(String defaultSuiteName) {
        m_defaultSuiteName = defaultSuiteName;
    }

    /**
     * @return the defaultTestName
     */
    public String getDefaultTestName() {
        return m_defaultTestName;
    }

    /**
     * @param defaultTestName the defaultTestName to set
     */
    public void setDefaultTestName(String defaultTestName) {
        m_defaultTestName = defaultTestName;
    }

    /**
     * Sets the policy for whether or not to ever invoke a configuration method again after
     * it has failed once. Possible values are defined in {@link org.testng.xml.XmlSuite}.  The default
     * value is {@link org.testng.xml.XmlSuite#SKIP}.
     *
     * @param failurePolicy the configuration failure policy
     */
    public void setConfigFailurePolicy(String failurePolicy) {
        m_configFailurePolicy = failurePolicy;
    }

    /**
     * Returns the configuration failure policy.
     *
     * @return config failure policy
     */
    public String getConfigFailurePolicy() {
        return m_configFailurePolicy;
    }

    // DEPRECATED: to be removed after a major version change

    /**
     * @deprecated since 5.1
     */
    @Deprecated
    public static CustomTestNG getDefault() {
        return m_instance;
    }

    /**
     * @deprecated since 5.1
     */
    @Deprecated
    public void setHasFailure(boolean hasFailure) {
        m_status |= HAS_FAILURE;
    }

    /**
     * @deprecated since 5.1
     */
    @Deprecated
    public void setHasFailureWithinSuccessPercentage(boolean hasFailureWithinSuccessPercentage) {
        m_status |= HAS_FSP;
    }

    /**
     * @deprecated since 5.1
     */
    @Deprecated
    public void setHasSkip(boolean hasSkip) {
        m_status |= HAS_SKIPPED;
    }

    public static class ExitCodeListener implements IResultListener2 {
        private CustomTestNG m_mainRunner;

        public ExitCodeListener() {
            m_mainRunner = CustomTestNG.m_instance;
        }

        public ExitCodeListener(CustomTestNG runner) {
            m_mainRunner = runner;
        }

        @Override
        public void beforeConfiguration(org.testng.ITestResult tr) {
        }

        @Override
        public void onTestFailure(org.testng.ITestResult result) {
            setHasRunTests();
            m_mainRunner.setStatus(HAS_FAILURE);
        }

        @Override
        public void onTestSkipped(org.testng.ITestResult result) {
            setHasRunTests();
            m_mainRunner.setStatus(HAS_SKIPPED);
        }

        @Override
        public void onTestFailedButWithinSuccessPercentage(org.testng.ITestResult result) {
            setHasRunTests();
            m_mainRunner.setStatus(HAS_FSP);
        }

        @Override
        public void onTestSuccess(org.testng.ITestResult result) {
            setHasRunTests();
        }

        @Override
        public void onStart(org.testng.ITestContext context) {
            setHasRunTests();
        }

        @Override
        public void onFinish(org.testng.ITestContext context) {
        }

        @Override
        public void onTestStart(org.testng.ITestResult result) {
            setHasRunTests();
        }

        private void setHasRunTests() {
            m_mainRunner.m_hasTests = true;
        }

        /**
         * @see IConfigurationListener#onConfigurationFailure(org.testng.ITestResult)
         */
        @Override
        public void onConfigurationFailure(org.testng.ITestResult itr) {
            m_mainRunner.setStatus(HAS_FAILURE);
        }

        /**
         * @see IConfigurationListener#onConfigurationSkip(org.testng.ITestResult)
         */
        @Override
        public void onConfigurationSkip(org.testng.ITestResult itr) {
            m_mainRunner.setStatus(HAS_SKIPPED);
        }

        /**
         * @see IConfigurationListener#onConfigurationSuccess(org.testng.ITestResult)
         */
        @Override
        public void onConfigurationSuccess(org.testng.ITestResult itr) {
        }
    }

    private void setConfigurable(IConfigurable c) {
        // compare by reference!
        if (m_configurable != null && m_configurable != c) {
            LOGGER.warn("Configurable already set");
        }
        m_configurable = c;
    }

    private void setHookable(IHookable h) {
        // compare by reference!
        if (m_hookable != null && m_hookable != h) {
            LOGGER.warn("Hookable already set");
        }
        m_hookable = h;
    }

    public void setMethodInterceptor(IMethodInterceptor i) {
        // compare by reference!
        if (m_methodInterceptor != null && m_methodInterceptor != i) {
            LOGGER.warn("MethodInterceptor already set");
        }
        m_methodInterceptor = i;
    }

    public void setDataProviderThreadCount(int count) {
        m_dataProviderThreadCount = count;
    }

    /**
     * Add a class loader to the searchable loaders.
     */
    public void addClassLoader(final ClassLoader loader) {
        if (loader != null) {
            ClassHelper.addClassLoader(loader);
        }
    }

    public void setPreserveOrder(boolean b) {
        m_preserveOrder = b;
    }

    protected long getStart() {
        return m_start;
    }

    protected long getEnd() {
        return m_end;
    }

    public void setGroupByInstances(boolean b) {
        m_groupByInstances = b;
    }

    /////
    // ServiceLoader testing
    //

    private URLClassLoader m_serviceLoaderClassLoader;
    private List<org.testng.ITestNGListener> m_serviceLoaderListeners = Lists.newArrayList();

    /*
     * Used to test ServiceClassLoader
     */
    public void setServiceLoaderClassLoader(URLClassLoader ucl) {
        m_serviceLoaderClassLoader = ucl;
    }

    /*
     * Used to test ServiceClassLoader
     */
    private void addServiceLoaderListener(org.testng.ITestNGListener l) {
        m_serviceLoaderListeners.add(l);
    }

    /*
     * Used to test ServiceClassLoader
     */
    public List<org.testng.ITestNGListener> getServiceLoaderListeners() {
        return m_serviceLoaderListeners;
    }

    //
    // ServiceLoader testing
    /////
}


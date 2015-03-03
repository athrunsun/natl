SET project_path_db_config=D:\Git\nitrogen-ates-misc\dbconfig
SET project_root_path=D:\Git\nitrogen-ates
SET project_path_util=%project_root_path%\util
SET project_path_core=%project_root_path%\core
SET project_path_daemon=%project_root_path%\daemon
SET project_path_dashboard=%project_root_path%\dashboard
SET project_path_testimporter=%project_root_path%\testimporter2
SET project_path_testpartner=%project_root_path%\testpartner
SET project_path_testresultreporter=%project_root_path%\testresultreporter
SET ates_path_root=C:\ates
SET ates_path_lib=%ates_path_root%\lib

MKDIR %project_path_daemon%\src\main\resources
MKDIR %project_path_dashboard%\src\main\resources
MKDIR %project_path_testimporter%\src\main\resources
MKDIR %project_path_testresultreporter%\src\main\resources

COPY %project_path_db_config%\configLocal.txt %project_path_daemon%\src\main\resources\config.txt /y
COPY %project_path_db_config%\configLocal.txt %project_path_dashboard%\src\main\resources\config.txt /y
COPY %project_path_db_config%\configLocal.txt %project_path_testimporter%\src\main\resources\config.txt /y
COPY %project_path_db_config%\configLocal.txt %project_path_testresultreporter%\src\main\resources\config.txt /y

CD /D %project_path_util%
CALL mvn clean compile package install

CD /D %project_path_core%
CALL mvn clean compile package install

CD /D %project_path_daemon%
CALL mvn clean compile package assembly:single
XCOPY %project_path_daemon%\target\*.jar %ates_path_lib%\ /h /i /r /c /y /d
COPY %project_path_daemon%\launchDaemon.bat %ates_path_lib%\ /y

REM CD /D %project_path_dashboard%
REM CALL mvn clean compile package tomcat7:redeploy

CD /D %project_path_testimporter%
CALL mvn clean compile package assembly:single
CALL mvn install
XCOPY %project_path_testimporter%\target\*.jar %ates_path_lib%\ /h /i /r /c /y /d

CD /D %project_path_testpartner%
CALL mvn clean compile package install

CD /D %project_path_testresultreporter%
CALL mvn clean compile package assembly:single
XCOPY %project_path_testresultreporter%\target\*.jar %ates_path_lib%\ /h /i /r /c /y /d

PAUSE
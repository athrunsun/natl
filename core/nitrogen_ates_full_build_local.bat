SET project_path_util=D:\Git\nitrogen-ates-util
SET project_path_core=D:\Git\nitrogen-ates-core
SET project_path_daemon=D:\Git\nitrogen-ates-daemon
SET project_path_dashboard=D:\Git\nitrogen-ates-dashboard
SET project_path_testimporter=D:\Git\nitrogen-ates-testimporter
SET project_path_testpartner=D:\Git\nitrogen-ates-testpartner
SET project_path_testresultreporter=D:\Git\nitrogen-ates-testresultreporter
SET ates_path_root=C:\ates
SET ates_path_lib=%ates_path_root%\lib

CD /D %project_path_util%
CALL mvn clean compile package install

CD /D %project_path_core%
CALL mvn clean compile package install

CD /D %project_path_daemon%
CALL mvn clean compile package assembly:single
xcopy %project_path_daemon%\target\*.jar %ates_path_lib%\ /h /i /r /c /y /d

REM CD /D %project_path_dashboard%
REM CALL mvn clean compile package tomcat7:redeploy

CD /D %project_path_testimporter%
CALL mvn clean compile package assembly:single
xcopy %project_path_testimporter%\target\*.jar %ates_path_lib%\ /h /i /r /c /y /d

CD /D %project_path_testpartner%
CALL mvn clean compile package install

CD /D %project_path_testresultreporter%
CALL mvn clean compile package assembly:single
xcopy %project_path_testresultreporter%\target\*.jar %ates_path_lib%\ /h /i /r /c /y /d

PAUSE
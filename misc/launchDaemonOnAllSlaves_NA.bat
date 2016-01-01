SET psexec_path=D:\GreenApp\Utilities\SysinternalsSuite
REM FOR %%G IN (10.119.33.69,10.119.33.66,10.119.35.102,10.119.35.103,10.119.35.104,10.119.35.105) DO (%psexec_path%\psexec.exe \\%%G -u .\auto -p @ctive123 -i 2 -d C:\ates\lib\launchDaemon.bat)

START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.33.69 -u .\auto -p @ctive123 -i 2 -d C:\ates\lib\launchDaemon.bat &
START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.33.66 -u .\auto -p @ctive123 -i 2 -d C:\ates\lib\launchDaemon.bat &
START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.35.102 -u .\auto -p @ctive123 -i 2 -d C:\ates\lib\launchDaemon.bat &
START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.35.103 -u .\auto -p @ctive123 -i 2 -d C:\ates\lib\launchDaemon.bat &
START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.35.104 -u .\auto -p @ctive123 -i 2 -d C:\ates\lib\launchDaemon.bat &
START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.35.105 -u .\auto -p @ctive123 -i 2 -d C:\ates\lib\launchDaemon.bat

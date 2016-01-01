SET psexec_path=D:\GreenApp\Utilities\SysinternalsSuite

START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.136.4.133 -u svc.shaqa -p sqP@ssw0rd -i 2 -d C:\ates\lib\launchDaemon.bat &
START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.136.4.47 -u svc.shaqa -p P@ssword2 -i 2 -d C:\ates\lib\launchDaemon.bat &
START "launchDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.136.4.247 -u svc.shaqa -p P@ssword2 -i 2 -d C:\ates\lib\launchDaemon.bat

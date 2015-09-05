SET psexec_path=D:\GreenApp\Utilities\SysinternalsSuite

START "killDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.33.69 -u .\auto -p @ctive123 -i 2 -d taskkill /fi "WINDOWTITLE eq NATLDaemon" /f /t &
START "killDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.33.66 -u .\auto -p @ctive123 -i 2 -d taskkill /fi "WINDOWTITLE eq NATLDaemon" /f /t &
START "killDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.35.102 -u .\auto -p @ctive123 -i 2 -d taskkill /fi "WINDOWTITLE eq NATLDaemon" /f /t &
START "killDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.35.103 -u .\auto -p @ctive123 -i 2 -d taskkill /fi "WINDOWTITLE eq NATLDaemon" /f /t &
START "killDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.35.104 -u .\auto -p @ctive123 -i 2 -d taskkill /fi "WINDOWTITLE eq NATLDaemon" /f /t &
START "killDaemonOnAllSlaves" %psexec_path%\psexec.exe \\10.119.35.105 -u .\auto -p @ctive123 -i 2 -d taskkill /fi "WINDOWTITLE eq NATLDaemon" /f /t

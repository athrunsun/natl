# ATES (Automation Test Eco System)
<br/>

## TODO
### Dashboard
* ~~`P1`Charts~~
  * ~~Pass rate of last 5 execution~~
  * ~~Coverage of all test cases(add manual test cases field to project table)~~
<br/>

* Navigation
  * Replace current project preference `<select>` with a metro dropdown menu

* Execution
  * Add/remove entries
  * ~~`P1`Display result~~
  * ~~`P1`Rerun all/failed, will create a new execution~~
  * Rerun selected
  * Automatically rerun(replace former entries on webpage, but keep data)
  * ~~`P1`Pie chart to display pass rate~~
  * Append to current execution?
  * `P1`Queue entry count, execution's created date on execution list page
<br/>

* TestCase
  * `P1`Display latest result
  * ~~`P1`Run selected(in a new execution or append to existing ones)~~
  * Filter(testGroup/execResult/execution)
  * Historical results, and filter(env/browser/slave)
  * Group test cases by failure message
  * `P2`Treeview to display test cases: left-right panel, left panel to display treeview, right to display test case detail (result history, linked defects)
    `http://codepen.io/fsbdev/embed/nILhu?height=600&user=fsbdev&type=result&slug-hash=nILhu&safe=true&default-tab=result&animations=run#js-box`
  * Gridview to display test cases: add/remove columns, filter(with condition AND/OR), group by column
<br/>

* TestGroup
  * Historical pass rate
<br/>

* Project
  * Project concurrency
  * Reload: reload all TestCase/TestGroup/TestSuite, call Jenkins job to do this, add a process bar on the webpage
<br/>

* TestResultDetail
  * Failure reason(regression/environment/scriptError)
  * Comments
  * Log(provided by TestPartner)(redirect TestNG commandline output to a file, then save to DB)
<br/>

* TestSuite
  * Single/multiple TestGroup/TestCase with default paramters, user can use default parameter or define their own when creating executions
  * Reload automatically when full-building project
  * Historical pass rate (need to create relationship with execution)
  * Define test suite based on certain condition: 1) TestGroup.name == "ABC" OR "DEF"
<br/>

* Slave
  * `P2`Project priority
<br/>

* `P2`Schedule
<br/>

### Daemon
* ~~`P1`Deployment(stop/deploy/start)~~
* Run TestNG programatically inside `net.nitrogen.ates.TestRunner`, to fit `@Parameters` requirement
* Timeout for queue entry (to avoid 'dead' queue entry to block slave)
<br/>

### Miscellaneous
* ~~Renaming:Round -> Execution~~
* ~~Move all ates projects into one git repo~~
* ~~Deploy ATES on VMs in Shanghai~~
* Multiple browser support(IE10/Firefox/Chrome)
* Ant support
* Test method dependency
* ~~`P2`Email notification against execution~~
* ~~`P1`Hide TestResult page~~
* Test case mapping with test case management system
* ~~`P1`Migration manual~~
* ~~`P1`Modify Jenkins configuration to: copy maven resources to project/{projectId}/main/resources (clean before copying)~~
* ~~`P1`Let users define Java packages that contain valid tests they want to import,
  then run TestNG programatically to import tests with a virtual TestNG suite.
  This way users don't have to add `ATESTestImporter.xml` to their project.~~
* Add DB build
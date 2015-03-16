# Nitrogen ATES (Automation Test Eco System)
<br/>

## TODO
### Dashboard
* `P1`Charts
  * ~~Pass rate of last 5 execution~~
  * ~~Coverage of all test cases(add manual test cases field to project table)~~
<br/>

* Execution
  * Create new execution based on current execution
  * Add/remove entries
  * `P1`Display result
  * `P1`Rerun all/failed
  * Rerun selected
  * Automatically rerun(replace former entries on webpage, but keep data)
  * `P1`Pie chart to display pass rate
<br/>

* Favorite Execution(single/multiple TestGroup with paramters, reload automatically when full-building project)
  * (TBD)Historical pass rate
<br/>

* TestCase
  * Display latest result
  * `P1`Run selected(in a new execution or append to existing ones)
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
<br/>

* TestResultDetail
  * Failure reason(regression/environment/scriptError)
  * Comments
  * Log(provided by TestPartner)
<br/>

* (TBD)TestSuite
  * Think it over before implementing
<br/>

* ENV
  * Customize env parameter name
<br/>

* Slave
  * `P2`Project priority
<br/>

* `P2`Schedule
<br/>

### Daemon
* ~~`P1`Deployment(stop/deploy/start)~~
* Run TestNG programatically inside `net.nitrogen.ates.TestRunner`, to fit `@Parameters` requirement
<br/>

### Miscellaneous
* ~~Renaming:Round -> Execution~~
* ~~Move all ates projects into one git repo~~
* ~~Deploy ATES on VMs in Shanghai~~
* Multiple browser support(IE10/Firefox/Chrome)
* Ant support
* Test method dependency
* `P2`Email notification against execution
* `P1`Remove TestResult page
* Test case mapping with test case management system
* `P1`Migration manual
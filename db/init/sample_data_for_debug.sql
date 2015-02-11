# Project
INSERT INTO `nitrogenates`.`project` (`name`, `jar_name`, `jar_with_dependency_name`, `git_url`) VALUES ('testProject', 'D:/git/test1.jar', 'D:/git/test2.jar', 'git://git_url');
INSERT INTO `nitrogenates`.`project` (`name`, `jar_name`, `jar_with_dependency_name`, `git_url`) VALUES ('demo', 'D:/git/test3.jar', 'D:/git/test4.jar', 'git://git_url');
SELECT * FROM `nitrogenates`.`project`;
# Custome environment
INSERT INTO `nitrogenates`.`custom_env` (`name`, `project_id`) VALUES ('qa', 1);
INSERT INTO `nitrogenates`.`custom_env` (`name`, `project_id`) VALUES ('stable', 1);
INSERT INTO `nitrogenates`.`custom_env` (`name`, `project_id`) VALUES ('qa', 2);
INSERT INTO `nitrogenates`.`custom_env` (`name`, `project_id`) VALUES ('stable', 2);
INSERT INTO `nitrogenates`.`custom_env` (`name`, `project_id`) VALUES ('stage', 2);
SELECT * FROM `nitrogenates`.`custom_env`;
# Slaves
INSERT INTO `nitrogenates`.`slave` (`machine_name`, `status`, `concurrency`, `is_listening`) VALUES ('127.0.0.1', 1, 1, 1);
INSERT INTO `nitrogenates`.`slave` (`machine_name`, `status`, `concurrency`, `is_listening`) VALUES ('127.0.0.1', 1, 1, 1);
SELECT * FROM `nitrogenates`.`slave`;
# Test case
INSERT INTO `nitrogenates`.`test_case` (`project_id`, `name`, `mapping_id`) VALUES (1, 'testcase1', 'mapping1');
INSERT INTO `nitrogenates`.`test_case` (`project_id`, `name`, `mapping_id`) VALUES (1, 'testcase2', 'mapping2');
INSERT INTO `nitrogenates`.`test_case` (`project_id`, `name`, `mapping_id`) VALUES (1, 'testcase3', 'mapping3');
INSERT INTO `nitrogenates`.`test_case` (`project_id`, `name`, `mapping_id`) VALUES (1, 'testcase4', 'mapping4');
INSERT INTO `nitrogenates`.`test_case` (`project_id`, `name`, `mapping_id`) VALUES (1, 'testcase5', 'mapping5');
INSERT INTO `nitrogenates`.`test_case` (`project_id`, `name`, `mapping_id`) VALUES (1, 'testcase6', 'mapping6');
INSERT INTO `nitrogenates`.`test_case` (`project_id`, `name`, `mapping_id`) VALUES (1, 'testcase7', 'mapping7');
SELECT * FROM `nitrogenates`.`test_case`;
# Test Suite
INSERT INTO `nitrogenates`.`test_suite` (`name`, `project_id`) VALUES ('testsuite1', 1);
INSERT INTO `nitrogenates`.`test_suite` (`name`, `project_id`) VALUES ('testsuite2', 1);
SELECT * FROM `nitrogenates`.`test_suite`;
# Test suite & test case
INSERT INTO `nitrogenates`.`test_suite-test_case` (`test_suite_id`, `test_name`) VALUES (1, 'testcase1');
INSERT INTO `nitrogenates`.`test_suite-test_case` (`test_suite_id`, `test_name`) VALUES (1, 'testcase2');
INSERT INTO `nitrogenates`.`test_suite-test_case` (`test_suite_id`, `test_name`) VALUES (1, 'testcase3');
INSERT INTO `nitrogenates`.`test_suite-test_case` (`test_suite_id`, `test_name`) VALUES (1, 'testcase4');
INSERT INTO `nitrogenates`.`test_suite-test_case` (`test_suite_id`, `test_name`) VALUES (2, 'testcase1');
INSERT INTO `nitrogenates`.`test_suite-test_case` (`test_suite_id`, `test_name`) VALUES (2, 'testcase2');
INSERT INTO `nitrogenates`.`test_suite-test_case` (`test_suite_id`, `test_name`) VALUES (2, 'testcase5');
INSERT INTO `nitrogenates`.`test_suite-test_case` (`test_suite_id`, `test_name`) VALUES (2, 'testcase6');
SELECT * FROM `nitrogenates`.`test_suite-test_case`;
# Test group
INSERT INTO `nitrogenates`.`test_group` (`name`, `project_id`) VALUES ('testgroup1', 1);
INSERT INTO `nitrogenates`.`test_group` (`name`, `project_id`) VALUES ('testgroup2', 1);
SELECT * FROM `nitrogenates`.`test_group`;
# Test Group & Test case
INSERT INTO `nitrogenates`.`test_group-test_case` (`test_group_id`, `test_name`) VALUES (1, 'testcase1');
INSERT INTO `nitrogenates`.`test_group-test_case` (`test_group_id`, `test_name`) VALUES (1, 'testcase3');
INSERT INTO `nitrogenates`.`test_group-test_case` (`test_group_id`, `test_name`) VALUES (1, 'testcase5');
INSERT INTO `nitrogenates`.`test_group-test_case` (`test_group_id`, `test_name`) VALUES (1, 'testcase7');
INSERT INTO `nitrogenates`.`test_group-test_case` (`test_group_id`, `test_name`) VALUES (2, 'testcase1');
INSERT INTO `nitrogenates`.`test_group-test_case` (`test_group_id`, `test_name`) VALUES (2, 'testcase3');
INSERT INTO `nitrogenates`.`test_group-test_case` (`test_group_id`, `test_name`) VALUES (2, 'testcase4');
INSERT INTO `nitrogenates`.`test_group-test_case` (`test_group_id`, `test_name`) VALUES (2, 'testcase6');
SELECT * FROM `nitrogenates`.`test_group-test_case`;
# Round
INSERT INTO `nitrogenates`.`round` (`name`, `project_id`) VALUES ('round1', 1);
INSERT INTO `nitrogenates`.`round` (`name`, `project_id`) VALUES ('round2', 1);
INSERT INTO `nitrogenates`.`round` (`name`, `project_id`) VALUES ('round3', 1);
SELECT * FROM `nitrogenates`.`round`;
# Queue Entry
INSERT INTO `nitrogenates`.`queue_entry` (`status`, `name`, `slave_name`, `index`, `start_time`, `end_time`, `round_id`, `project_id`, `env`, `jvm_options`, `params`)
	VALUES (0, 'testcase1', '', 1, '2015-01-01 00:00:00', '2015-01-01 00:10:00', 1, 1, 'qa', '', '');
INSERT INTO `nitrogenates`.`queue_entry` (`status`, `name`, `slave_name`, `index`, `start_time`, `end_time`, `round_id`, `project_id`, `env`, `jvm_options`, `params`)
	VALUES (0, 'testcase2', '', 1, '2015-01-01 00:10:00', '2015-01-01 00:20:00', 1, 1, 'qa', '', '');
INSERT INTO `nitrogenates`.`queue_entry` (`status`, `name`, `slave_name`, `index`, `start_time`, `end_time`, `round_id`, `project_id`, `env`, `jvm_options`, `params`)
	VALUES (0, 'testcase1', '', 1, '2015-01-01 00:00:00', '2015-01-01 00:10:00', 2, 1, 'qa', '', '');
INSERT INTO `nitrogenates`.`queue_entry` (`status`, `name`, `slave_name`, `index`, `start_time`, `end_time`, `round_id`, `project_id`, `env`, `jvm_options`, `params`)
	VALUES (0, 'testcase2', '', 1, '2015-01-01 00:10:00', '2015-01-01 00:20:00', 2, 1, 'qa', '', '');
SELECT * FROM `nitrogenates`.`queue_entry`;
# Test Result
INSERT INTO `nitrogenates`.`test_result` (`entry_id`, `test_name`, `slave_name`, `start_time`, `end_time`, `exec_result`, `message`, `stack_trace`, `screenshot_url`, `round_id`, `project_id`, `env`)
	VALUES (1, 'testcase1', '127.0.0.1', '2015-01-01 00:00:00', '2015-01-01 00:10:00', 1, 'message1', 'stacktrace1', 'screenurl1', 1, 1, 'qa');
INSERT INTO `nitrogenates`.`test_result` (`entry_id`, `test_name`, `slave_name`, `start_time`, `end_time`, `exec_result`, `message`, `stack_trace`, `screenshot_url`, `round_id`, `project_id`, `env`)
	VALUES (2, 'testcase2', '127.0.0.1', '2015-01-01 00:10:00', '2015-01-01 00:20:00', 2, 'message2', 'stacktrace2', 'screenurl2', 1, 1, 'qa');
INSERT INTO `nitrogenates`.`test_result` (`entry_id`, `test_name`, `slave_name`, `start_time`, `end_time`, `exec_result`, `message`, `stack_trace`, `screenshot_url`, `round_id`, `project_id`, `env`)
	VALUES (3, 'testcase1', '127.0.0.1', '2015-01-01 00:00:00', '2015-01-01 00:10:00', 1, 'message3', 'stacktrace3', 'screenurl3', 2, 1, 'qa');
INSERT INTO `nitrogenates`.`test_result` (`entry_id`, `test_name`, `slave_name`, `start_time`, `end_time`, `exec_result`, `message`, `stack_trace`, `screenshot_url`, `round_id`, `project_id`, `env`)
	VALUES (4, 'testcase2', '127.0.0.1', '2015-01-01 00:10:00', '2015-01-01 00:20:00', 2, 'message4', 'stacktrace4', 'screenurl4', 2, 1, 'qa');
SELECT * FROM `nitrogenates`.`test_result`;


################################## Stored Procedure Testings ############################################
SET @du = 0;
	CALL `nitrogenates`.`CalculateRoundDuration`(1, 1, @du);
	select @du as duration;

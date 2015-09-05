USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindTestCasesForTestSuiteWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `FindTestCasesForTestSuiteWithAdditionalInfo`(
  IN SuiteId INT UNSIGNED)
BEGIN
  DECLARE LatestExecutionId INT UNSIGNED;

  SELECT `id` FROM `execution` WHERE `test_suite_id` = SuiteId ORDER BY `id` DESC LIMIT 1 INTO LatestExecutionId;

  SELECT 
    `ts-tc`.`test_case_id` AS `id`,
    `tc`.`name`,
    `tc`.`project_id`,
    `tc`.`mapping_id`,
    `tc`.`version`,
    `tr`.`id` AS `test_result_id`,
    `tr`.`exec_result`,
    `tr`.`start_time` 
  FROM 
    `test_suite-test_case` AS `ts-tc`
    JOIN `test_case` AS `tc`  ON `tc`.`id` = `ts-tc`.`test_case_id`
    LEFT JOIN (SELECT * FROM `test_result` WHERE `execution_id` = LatestExecutionId) AS `tr` ON `tc`.`id` = `tr`.`test_case_id`
  WHERE
    `ts-tc`.`test_suite_id` = SuiteId;
END $$

DELIMITER ;
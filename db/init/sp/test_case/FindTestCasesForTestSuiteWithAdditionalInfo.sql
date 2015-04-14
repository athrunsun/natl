USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindTestCasesForTestSuiteWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `FindTestCasesForTestSuiteWithAdditionalInfo`(
  IN SuiteId INT UNSIGNED)
BEGIN
  DECLARE LatestExecutionId INT UNSIGNED;

  SELECT `id` FROM `execution` WHERE `test_suite_id` = SuiteId ORDER BY `id` DESC LIMIT 1 INTO LatestExecutionId;

  SELECT `ts`.`test_case_id` AS `id`,`tc`.`name`,`tc`.`project_id`,`tc`.`mapping_id`,
    `tc`.`version`,`tr`.`id` AS `test_result_id`,`tr`.`exec_result` 
  FROM 
    # Join 4 tables may be another choice (including `queue_entry`)
    `test_suite-test_case` AS `ts`
    JOIN `test_case` AS `tc`  ON `tc`.`id` = `ts`.`test_case_id`
    LEFT JOIN `test_result` AS `tr` ON `tc`.`id` = `tr`.`test_case_id`
  WHERE
    `test_suite_id` = SuiteId AND
    (`tr`.`execution_id` is null OR `tr`.`execution_id` = LatestExecutionId) AND
    NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_case_id` = `tc`.`id` AND `id` > `tr`.`id` LIMIT 1);
END $$

DELIMITER ;
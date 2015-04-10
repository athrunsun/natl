USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetTestCasesForTestSuiteWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `GetTestCasesForTestSuiteWithAdditionalInfo`(
  IN SuiteId INT UNSIGNED)
BEGIN
  DECLARE LatestExecutionId INT UNSIGNED;

  SELECT `id` FROM `execution` WHERE `test_suite_id` = SuiteId ORDER BY `id` DESC LIMIT 1 INTO LatestExecutionId;

  SELECT `tc`.`id`,`tc`.`name`,`tc`.`project_id`,`tc`.`mapping_id`,`tr`.`id` AS `test_result_id`,`tr`.`exec_result` 
  FROM 
    # Join 4 tables may be another choice (including `queue_entry`)
    `test_suite-test_case` AS `ts`
    JOIN `test_case` AS `tc`  ON `tc`.`id` = `ts`.`test_case_id`
    LEFT JOIN `test_result` AS `tr` ON `tc`.`id` = `tr`.`test_case_id`
  WHERE
    `tr`.`execution_id` = LatestExecutionId AND
    NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_case_id` = `tc`.`id` AND `id` > `tr`.`id` LIMIT 1);
END $$

DELIMITER ;
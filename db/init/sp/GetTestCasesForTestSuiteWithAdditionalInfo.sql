USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetTestCasesForTestSuiteWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `GetTestCasesForTestSuiteWithAdditionalInfo`(
  IN SuiteId INT UNSIGNED)
BEGIN
  SELECT `tc`.`project_id`,`tc`.`name`,`tc`.`mapping_id`,`tr`.`id`,`tr`.`exec_result` 
  FROM 
    `test_suite-test_case` AS `ts`
    LEFT JOIN `test_case` AS `tc`  ON `tc`.`name` = `ts`.`test_name`
    LEFT JOIN `test_result` AS `tr` ON `tc`.`name` = `tr`.`test_name`
  WHERE
    `ts`.`test_suite_id` = SuiteId AND
    NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_name` = `tc`.`name` AND `id` > `tr`.`id` LIMIT 1);
END $$

DELIMITER ;
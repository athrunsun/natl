USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetTestCasesForProjectWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `GetTestCasesForProjectWithAdditionalInfo`(
  IN ProjectId INT UNSIGNED)
BEGIN
  SELECT `tc`.`id`,`tc`.`name`,`tc`.`project_id`,`tc`.`mapping_id`,`tr`.`id` AS `test_result_id`,`tr`.`exec_result` 
  FROM 
    `test_case` AS `tc` LEFT JOIN `test_result` AS `tr` ON `tc`.`id` = `tr`.`test_case_id`
  WHERE
    `tc`.`project_id` = ProjectId AND
    NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_case_id` = `tc`.`id` AND `id` > `tr`.`id` LIMIT 1);
END $$

DELIMITER ;
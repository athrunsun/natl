USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetTestCasesForProjectWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `GetTestCasesForProjectWithAdditionalInfo`(
  IN ProjectId INT UNSIGNED)
BEGIN
  SELECT `tc`.`project_id`,`tc`.`name`,`tc`.`mapping_id`,`tr`.`id`,`tr`.`exec_result` 
  FROM 
    `test_case` AS `tc` LEFT JOIN `test_result` AS `tr` ON `tc`.`name` = `tr`.`test_name`
  WHERE
    `tc`.`project_id` = ProjectId AND
    NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_name` = `tc`.`name` AND `id` > `tr`.`id` LIMIT 1);
END $$

DELIMITER ;
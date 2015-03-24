USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetAllTestCasesButPassedOrFailed`;

DELIMITER $$

# Used for filtering test cases by status
CREATE PROCEDURE `GetAllTestCasesButPassedOrFailed`(
  IN ProjectId INT)
BEGIN
  SELECT `r`.`id`, `r`.`name` 
  FROM (
    SELECT `tc`.`mapping_id`, `tc`.`name` 
    FROM 
      (SELECT `mapping_id`, `name` FROM `test_case` WHERE `project_id` = ProjectId) AS `tc` 
      JOIN 
      (SELECT `test_name`, `exec_result` FROM `test_result` WHERE `project_id` = ProjectId) AS `tr` 
      ON `tc`.`name` = `tr`.`test_name`
    WHERE 
      NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_name` = `tr`.`test_name` AND `id` > `tr`.`id` LIMIT 1)
      AND `tr`.`exec_result` <> 1 AND `tr`.`exec_result` <> 2
    UNION ALL
    SELECT `tc`.`mapping_id`, `tc`.`name` 
    FROM (SELECT `mapping_id`, `name` FROM `test_case` WHERE `project_id` = ProjectId) AS `tc`
    WHERE 
      NOT EXISTS(SELECT `tre`.`id` FROM `test_result` AS `tre` WHERE `tre`.`test_name` = `tc`.`name`)
  ) AS `r`;
END $$

DELIMITER ;
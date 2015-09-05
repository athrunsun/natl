USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetTestCasesByExecResult`;

DELIMITER $$

# Used for filtering test cases by result
CREATE PROCEDURE `GetTestCasesByExecResult`(
  IN ProjectId INT,
  IN ExecResult INT)
BEGIN
  SELECT `tc`.`mapping_id`, `tr`.`test_name` 
  FROM 
    (SELECT `mapping_id`, `name` FROM `test_case` WHERE `project_id` = ProjectId) AS `tc` 
    JOIN 
    (SELECT `test_name`, `exec_result` FROM `test_result` WHERE `project_id` = ProjectId AND `exec_result` = ExecResult) AS `tr` 
    ON `tc`.`name` = `tr`.`test_name` 
  WHERE 
    NOT EXISTS(SELECT 1 FROM `test_result` WHERE `test_name` = `tr`.`test_name` AND `id` > `tr`.`id`);
END $$

DELIMITER ;
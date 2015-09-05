USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetLatestTestResultsByGroup`;

DELIMITER $$

CREATE PROCEDURE `GetLatestTestResultsByGroup`(
  IN ProjectId INT UNSIGNED,
  IN TestGroupId INT UNSIGNED)
BEGIN
  SELECT `tcase`.`name`, `tr`.`exec_result`, `tr`.`env` 
  FROM 
    (SELECT `tc`.`name` 
    FROM 
      (SELECT `name` FROM `test_case` WHERE `project_id` = ProjectId) AS `tc` 
      JOIN (SELECT `test_name` FROM `test_group-test_case` WHERE `test_group_id` = TestGroupId) AS `tg-tc` 
      ON `tc`.`name` = `tg-tc`.`test_name`) AS `tcase` 
    JOIN `test_result` AS `tr` ON `tcase`.`name` = `tr`.`test_name` 
  WHERE 
    `tr`.`id` = (SELECT `id` FROM `test_result` WHERE `test_name` = `tcase`.`name` ORDER BY `id` DESC LIMIT 1);
END $$

DELIMITER ;
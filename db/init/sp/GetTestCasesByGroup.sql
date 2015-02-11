USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetTestCasesByGroup`;

DELIMITER $$

CREATE PROCEDURE `GetTestCasesByGroup`(
  IN ProjectId INT UNSIGNED,
  IN TestGroupId INT UNSIGNED)
BEGIN
  SELECT `tc`.`mapping_id`, `tc`.`name` 
  FROM 
    (SELECT `mapping_id`, `name` FROM `test_case` WHERE `project_id` = ProjectId) AS `tc` 
    JOIN (SELECT `test_case_id` FROM `test_group-test_case` WHERE `test_group_id` = TestGroupId) AS `tg-tc` 
    ON `tc`.`name` = `tg-tc`.`test_name`;
END $$

DELIMITER ;
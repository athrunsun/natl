USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetTestCasesForTestGroupWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `GetTestCasesForTestGroupWithAdditionalInfo`(
  IN GroupId INT UNSIGNED)
BEGIN
  SELECT `tc`.`project_id`,`tc`.`name`,`tc`.`mapping_id`,`tr`.`id`,`tr`.`exec_result` 
  FROM 
    `test_group-test_case` AS `tg`
    LEFT JOIN `test_case` AS `tc`  ON `tc`.`name` = `tg`.`test_name`
    LEFT JOIN `test_result` AS `tr` ON `tc`.`name` = `tr`.`test_name`
  WHERE
    `tg`.`test_group_id` = GroupId AND
    NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_name` = `tc`.`name` AND `id` > `tr`.`id` LIMIT 1);
END $$

DELIMITER ;
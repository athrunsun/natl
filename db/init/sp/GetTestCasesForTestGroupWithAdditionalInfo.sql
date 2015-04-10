USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetTestCasesForTestGroupWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `GetTestCasesForTestGroupWithAdditionalInfo`(
  IN GroupId INT UNSIGNED)
BEGIN
  SELECT `tc`.`id`,`tc`.`name`,`tc`.`project_id`,`tc`.`mapping_id`,`tr`.`id` AS `test_result_id`,`tr`.`exec_result` 
  FROM 
    `test_group-test_case` AS `tg`
    JOIN `test_case` AS `tc`  ON `tc`.`id` = `tg`.`test_case_id`
    LEFT JOIN `test_result` AS `tr` ON `tc`.`id` = `tr`.`test_case_id`
  WHERE
    `tg`.`test_group_id` = GroupId AND
    NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_case_id` = `tc`.`id` AND `id` > `tr`.`id` LIMIT 1);
END $$

DELIMITER ;
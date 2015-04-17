USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindTestGroupsWithAdditionalInfoForProject`;

DELIMITER $$

CREATE PROCEDURE `FindTestGroupsWithAdditionalInfoForProject`(
  IN ProjectId INT UNSIGNED)
BEGIN
  SELECT 
    `tg`.`id`,
    `tg`.`name`,
    `tg`.`project_id`,
    COUNT(`tg_tc`.`id`) AS `test_case_count` 
  FROM 
    `test_group` AS `tg` 
  JOIN 
    `test_group-test_case` AS `tg_tc` 
  ON 
    `tg`.`id` = `tg_tc`.`test_group_id`
  WHERE
    `tg`.`project_id` = ProjectId
  GROUP BY
    `tg`.`id`;
END $$

DELIMITER ;
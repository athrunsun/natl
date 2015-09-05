USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindTestSuitesWithAdditionalInfoForProject`;

DELIMITER $$

CREATE PROCEDURE `FindTestSuitesWithAdditionalInfoForProject`(
  IN ProjectId INT UNSIGNED)
BEGIN
  SELECT 
    `ts`.`id`,
    `ts`.`name`,
    `ts`.`project_id`,
    COUNT(`ts_tc`.`id`) AS `test_case_count` 
  FROM 
    `test_suite` AS `ts` 
  LEFT JOIN 
    `test_suite-test_case` AS `ts_tc` 
  ON 
    `ts`.`id` = `ts_tc`.`test_suite_id`
  WHERE
    `ts`.`project_id` = ProjectId
  GROUP BY
    `ts`.`id`;
END $$

DELIMITER ;
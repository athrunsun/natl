USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindTestCasesForProjectWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `FindTestCasesForProjectWithAdditionalInfo`(
  IN ProjectId INT UNSIGNED)
BEGIN
  DECLARE LatestVersion INT UNSIGNED;

  SELECT `latest_test_case_version` FROM `project` WHERE `id` = ProjectId INTO LatestVersion;

  SELECT 
    `tc`.`id`,
    `tc`.`name`,
    `tc`.`project_id`,
    `tc`.`mapping_id`,
    `tr`.`id` AS `test_result_id`,
    `tr`.`exec_result` 
  FROM 
    `test_case` AS `tc` 
  LEFT JOIN 
    `test_result` AS `tr` 
  ON 
    `tc`.`id` = `tr`.`test_case_id`
  WHERE
    `tc`.`project_id` = ProjectId 
  AND
    `tc`.`version` = LatestVersion
  AND
    NOT EXISTS(SELECT `id` FROM `test_result` WHERE `test_case_id` = `tc`.`id` AND `id` > `tr`.`id` LIMIT 1);
END $$

DELIMITER ;
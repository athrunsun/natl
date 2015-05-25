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
    `tc`.`version`,
    `tb`.`test_result_id`,
    `tr`.`exec_result`,
    `tr`.`start_time` 
  FROM `test_case` AS `tc` 
  LEFT JOIN
    (SELECT `test_case_id`, MAX(`id`)  AS `test_result_id` FROM `test_result` WHERE `project_id` = ProjectId GROUP BY `test_case_id`) AS `tb`
    ON `tc`.`id` = `tb`.`test_case_id`
  LEFT JOIN `test_result` AS `tr` 
    ON `tr`.`id` = `tb`.`test_result_id`
  WHERE
    `tc`.`project_id` = ProjectId 
  AND
    `tc`.`version` = LatestVersion;
END $$

DELIMITER ;
USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindTestCasesForTestGroupWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `FindTestCasesForTestGroupWithAdditionalInfo`(
  IN GroupId INT UNSIGNED)
BEGIN
  DECLARE ProjectId INT UNSIGNED;
  DECLARE LatestVersion INT UNSIGNED;

  SELECT `project_id` FROM `test_group` WHERE `id` = GroupId INTO ProjectId;
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
  FROM 
    `test_group-test_case` AS `tg`
  JOIN 
    `test_case` AS `tc`  
  ON 
    `tc`.`id` = `tg`.`test_case_id`
  LEFT JOIN
    (SELECT `test_case_id`, MAX(`id`)  AS `test_result_id` FROM `test_result` WHERE `project_id` = ProjectId GROUP BY `test_case_id`) AS `tb`
    ON `tc`.`id` = `tb`.`test_case_id`
  LEFT JOIN `test_result` AS `tr` 
    ON `tr`.`id` = `tb`.`test_result_id`
  WHERE
    `tg`.`test_group_id` = GroupId 
  AND
    `tc`.`version` = LatestVersion;
END $$

DELIMITER ;
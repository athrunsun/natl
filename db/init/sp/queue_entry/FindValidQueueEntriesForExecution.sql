# This procedure is used to find valid queue entries for execution,
# "valid" means their versions match current project's latest test case version.
# It is not used for displaying, so we don't need to paginate within this procedure.

USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindValidQueueEntriesForExecution`;

DELIMITER $$

CREATE PROCEDURE `FindValidQueueEntriesForExecution`(
  IN ExecutionId INT UNSIGNED)
BEGIN
  DECLARE ProjectId INT UNSIGNED;
  DECLARE LatestVersion INT UNSIGNED;
  
  SELECT `project_id` FROM `execution` WHERE `id` = ExecutionId INTO ProjectId;
  SELECT `latest_test_case_version` FROM `project` WHERE `id` = ProjectId INTO LatestVersion;
  
  SELECT 
    `q`.`id`,
    `q`.`status`,
    `q`.`test_case_id`,
    `tc`.`version`,
    `q`.`slave_name`,
    `q`.`index`,
    `q`.`start_time`,
    `q`.`end_time`,
    `q`.`execution_id`,
    `q`.`project_id`
  FROM 
    `queue_entry` AS `q` 
    JOIN `test_case` AS `tc` ON `q`.`test_case_id` = `tc`.`id`
  WHERE `q`.`execution_id` = ExecutionId AND `tc`.`version` = LatestVersion ORDER BY `q`.`id` DESC;
END $$

DELIMITER ;
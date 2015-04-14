# This procedure is used to filter queue entries by specific execution result out of an existing execution
# to create new execution (rerun all/failed/...),
# but not used for displaying, so we don't need to paginate within this procedure.

USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindValidQueueEntriesForExecution_ExecResultFiltering`;

DELIMITER $$

CREATE PROCEDURE `FindValidQueueEntriesForExecution_ExecResultFiltering`(
  IN ExecutionId INT UNSIGNED,
  IN ExecResult INT)
BEGIN
  DECLARE ProjectId INT UNSIGNED;
  DECLARE LatestVersion INT UNSIGNED;
  
  SELECT `project_id` FROM `execution` WHERE `id` = ExecutionId INTO ProjectId;
  SELECT `latest_test_case_version` FROM `project` WHERE `id` = ProjectId INTO LatestVersion;

  IF ExecResult = 0
  THEN
    SELECT 
      `q`.`id`,
      `q`.`status`,
      `q`.`test_case_id`,
      `q`.`slave_name`,
      `q`.`index`,
      `q`.`start_time`,
      `q`.`end_time`,
      `q`.`execution_id`,
      `q`.`project_id`
    FROM 
      `queue_entry` AS `q` 
      JOIN `test_case` AS `tc` ON `q`.`test_case_id` = `tc`.`id`
      LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id`
    WHERE 
      `q`.`execution_id`=ExecutionId AND `tc`.`version` = LatestVersion AND `tr`.`id` IS NULL 
    ORDER BY 
      `q`.`id` DESC;
  ELSE
    SELECT 
      `q`.`id`,
      `q`.`status`,
      `q`.`test_case_id`,
      `q`.`slave_name`,
      `q`.`index`,
      `q`.`start_time`,
      `q`.`end_time`,
      `q`.`execution_id`,
      `q`.`project_id`
    FROM 
      `queue_entry` AS `q` 
      JOIN `test_case` AS `tc` ON `q`.`test_case_id` = `tc`.`id`
      LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id`
    WHERE 
      `q`.`execution_id`=ExecutionId AND `tc`.`version` = LatestVersion AND `tr`.`exec_result`=ExecResult 
    ORDER BY 
      `q`.`id` DESC;
  END IF;
END $$

DELIMITER ;
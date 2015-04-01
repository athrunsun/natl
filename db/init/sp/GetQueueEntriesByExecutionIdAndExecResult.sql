# This procedure is used to filter queue entries by specific execution result out of an existing execution
# to create new execution (rerun all/failed/...),
# but not used for displaying, so we don't need to paginate within this procedure.

USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetQueueEntriesByExecutionIdAndExecResult`;

DELIMITER $$

CREATE PROCEDURE `GetQueueEntriesByExecutionIdAndExecResult`(
  IN ExecutionId INT UNSIGNED,
  IN ExecResult INT)
BEGIN
  IF ExecResult = 0
  THEN
    SELECT `q`.`id`,`q`.`status`,`q`.`name`,`q`.`slave_name`,`q`.`index`,`q`.`start_time`,`q`.`end_time`,`q`.`execution_id`,`q`.`project_id`,`q`.`env`,`q`.`jvm_options`,`q`.`params`,
      `tr`.`id` AS `test_result_id`,`tr`.`exec_result`
    FROM 
      `queue_entry` AS `q` LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id`
    WHERE `q`.`execution_id`=ExecutionId AND `tr`.`id` IS NULL ORDER BY `q`.`id` DESC;
  ELSE
    SELECT `q`.`id`,`q`.`status`,`q`.`name`,`q`.`slave_name`,`q`.`index`,`q`.`start_time`,`q`.`end_time`,`q`.`execution_id`,`q`.`project_id`,`q`.`env`,`q`.`jvm_options`,`q`.`params`,
    `tr`.`id`,`tr`.`exec_result`
    FROM 
      `queue_entry` AS `q` LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id`
    WHERE `q`.`execution_id`=ExecutionId AND `tr`.`exec_result`=ExecResult ORDER BY `q`.`id` DESC;
  END IF;
END $$

DELIMITER ;
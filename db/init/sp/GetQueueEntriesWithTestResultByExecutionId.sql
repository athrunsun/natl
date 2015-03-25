USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetQueueEntriesWithTestResultByExecutionId`;

DELIMITER $$

CREATE PROCEDURE `GetQueueEntriesWithTestResultByExecutionId`(
  IN ExecutionId INT UNSIGNED)
BEGIN
  SELECT `q`.`id`,`q`.`status`,`q`.`name`,`q`.`slave_name`,`q`.`index`,`q`.`start_time`,`q`.`end_time`,`q`.`execution_id`,`q`.`project_id`,`q`.`env`,`q`.`jvm_options`,`q`.`params`,
  `tr`.`id`,`tr`.`exec_result` 
  FROM 
    `queue_entry` AS `q` LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id` 
  WHERE 
    `q`.`execution_id`=ExecutionId
  ORDER BY `q`.`id` DESC;
END $$

DELIMITER ;
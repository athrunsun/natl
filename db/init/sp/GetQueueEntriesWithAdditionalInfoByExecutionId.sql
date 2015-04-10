USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetQueueEntriesWithAdditionalInfoByExecutionId`;

DELIMITER $$

CREATE PROCEDURE `GetQueueEntriesWithAdditionalInfoByExecutionId`(
  IN ExecutionId INT UNSIGNED)
BEGIN
  SELECT `q`.`id`,`q`.`status`,`q`.`test_case_id`,`tc`.`name` AS `test_case_name`,`q`.`slave_name`,`q`.`index`,`q`.`start_time`,`q`.`end_time`,`q`.`execution_id`,`q`.`project_id`,
    `tr`.`id` AS `test_result_id`,`tr`.`exec_result` 
  FROM 
    `queue_entry` AS `q` 
    JOIN `test_case` AS `tc` ON `q`.`test_case_id` = `tc`.`id`
    LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id` 
  WHERE 
    `q`.`execution_id`=ExecutionId
  ORDER BY 
    `q`.`id` DESC;
END $$

DELIMITER ;
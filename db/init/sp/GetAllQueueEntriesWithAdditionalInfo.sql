USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetAllQueueEntriesWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `GetAllQueueEntriesWithAdditionalInfo`()
BEGIN
  SELECT `q`.`id`,`q`.`status`,`q`.`name`,`q`.`slave_name`,`q`.`index`,`q`.`start_time`,`q`.`end_time`,`q`.`execution_id`,`q`.`project_id`,`q`.`env`,`q`.`jvm_options`,`q`.`params`,
    `tr`.`id` AS `test_result_id`,`tr`.`exec_result`
  FROM 
    `queue_entry` AS `q` LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id`
  ORDER BY `q`.`id` DESC;
END $$

DELIMITER ;
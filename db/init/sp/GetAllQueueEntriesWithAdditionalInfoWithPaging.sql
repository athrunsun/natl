USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetAllQueueEntriesWithAdditionalInfoWithPaging`;

DELIMITER $$

CREATE PROCEDURE `GetAllQueueEntriesWithAdditionalInfoWithPaging`(
  IN PageNumber INT,
  IN PageSize INT)
BEGIN
  DECLARE Offset INT;
  DECLARE Total INT;
  SET Offset := PageSize * (PageNumber - 1);
  SELECT COUNT(`id`) FROM `queue_entry` INTO Total;

  IF Offset > Total - 1
  THEN SET Offset := Total - 1;
  END IF;

  SELECT `q`.`id`,`q`.`status`,`q`.`name`,`q`.`slave_name`,`q`.`index`,`q`.`start_time`,`q`.`end_time`,`q`.`execution_id`,`q`.`project_id`,
    `tr`.`id` AS `test_result_id`,`tr`.`exec_result`
  FROM 
    `queue_entry` AS `q` LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id`
  ORDER BY 
    `q`.`id` DESC
  LIMIT
    Offset, PageSize;
END $$

DELIMITER ;
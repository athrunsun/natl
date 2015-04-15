USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindAllQueueEntriesWithAdditionalInfo_Paging`;

DELIMITER $$

CREATE PROCEDURE `FindAllQueueEntriesWithAdditionalInfo_Paging`(
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

  SELECT 
    `q`.`id`,
    `q`.`status`,
    `q`.`test_case_id`,
    `tc`.`name` AS `test_case_name`,
    `tc`.`version`,
    `q`.`slave_name`,
    `q`.`index`,
    `q`.`start_time`,
    `q`.`end_time`,
    `q`.`execution_id`,
    `q`.`project_id`,
    `tr`.`id` AS `test_result_id`,
    `tr`.`exec_result`
  FROM     
    `queue_entry` AS `q`
    JOIN `test_case` AS `tc` ON `q`.`test_case_id` = `tc`.`id`
    LEFT JOIN `test_result` AS `tr` ON `q`.`id` = `tr`.`entry_id`
  ORDER BY 
    `q`.`id` DESC
  LIMIT
    Offset, PageSize;
END $$

DELIMITER ;
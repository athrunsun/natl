USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetAllExecutionsWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `GetAllExecutionsWithAdditionalInfo`(
  IN ExecutionId INT UNSIGNED)
BEGIN
  SELECT 
    `e`.`id`,`e`.`name`,`e`.`project_id`,COUNT(`q`.`execution_id`) AS `queue_entry_count` 
  FROM 
    `execution` AS `e` JOIN `queue_entry` AS `q` ON `e`.`id` = `q`.`execution_id`
  GROUP BY
    `e`.`id`;
END $$

DELIMITER ;
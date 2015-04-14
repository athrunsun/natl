USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `FindExecutionsWithAdditionalInfo`;

DELIMITER $$

CREATE PROCEDURE `FindExecutionsWithAdditionalInfo`(
  IN ProjectId INT UNSIGNED)
BEGIN
  SELECT 
    `e`.`id`,`e`.`name`,`e`.`project_id`,`e`.`created_time`,COUNT(`q`.`execution_id`) AS `queue_entry_count` 
  FROM 
    `execution` AS `e` JOIN `queue_entry` AS `q` ON `e`.`id` = `q`.`execution_id`
  WHERE
    `e`.`project_id` = ProjectId
  GROUP BY
    `e`.`id`
  ORDER BY
    `e`.`id` DESC;
END $$

DELIMITER ;
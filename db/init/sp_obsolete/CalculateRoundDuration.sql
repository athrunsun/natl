USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `CalculateRoundDuration`;

DELIMITER $$

CREATE PROCEDURE `CalculateRoundDuration`(
  IN ProjectId INT,
  IN ExecutionId INT,
  OUT Duration INT)
BEGIN
  DECLARE StartTime DATETIME;
  DECLARE EndTime DATETIME;

  SET StartTime = (SELECT `q`.`start_time` FROM `queue_entry` AS `q` 
	WHERE `q`.`project_id` = ProjectId AND `q`.`execution_id` = ExecutionId AND `q`.`status` = 3 
    ORDER BY `q`.`end_time` ASC LIMIT 1);
  
  SET EndTime = (SELECT `q`.`end_time` FROM `queue_entry` AS `q` 
	WHERE `q`.`project_id` = ProjectId AND `q`.`execution_id` = ExecutionId AND `q`.`status` = 3 
    ORDER BY `q`.`end_time` ASC LIMIT 1);
  SET Duration = TIMESTAMPDIFF(SECOND, StartTime, EndTime);
END $$

DELIMITER ;
USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `GetLatestTestResultsByRound`;

DELIMITER $$

CREATE PROCEDURE `GetLatestTestResultsByRound`(
  IN ProjectId INT UNSIGNED,
  IN RoundId INT UNSIGNED)
BEGIN
  SELECT `tcase`.`name`, `tr`.`exec_result`, `tr`.`env` 
  FROM 
    (SELECT `tc`.`name` FROM `test_case` WHERE `project_id` = ProjectId) AS `tcase` 
    JOIN (SELECT `id`, `test_name`, `start_time`, `end_time`, `exec_result`, `message`, `stack_trace`, `screenshot_url`, `round_id`, `project_id`, `env`, `params`, `slave_name` FROM `test_result` WHERE `round_id` = RoundId) AS `tr` 
    ON `tcase`.`name` = `tr`.`test_name` 
  WHERE 
    `tr`.`id` = (SELECT `id` FROM `test_result` WHERE `test_name` = `tcase`.`name` ORDER BY `id` DESC LIMIT 1);
END $$

DELIMITER ;
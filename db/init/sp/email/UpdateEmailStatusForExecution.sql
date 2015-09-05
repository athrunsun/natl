USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `UpdateEmailStatusForExecution`;

DELIMITER $$

CREATE PROCEDURE `UpdateEmailStatusForExecution`(
  IN ExecutionId INT UNSIGNED)
BEGIN
  
	DECLARE result_num INT UNSIGNED;
	DECLARE expected_num INT UNSIGNED;
	DECLARE EntryId INT UNSIGNED;
	SELECT COUNT(*) FROM `test_result` WHERE `execution_id`=ExecutionId INTO result_num;
	SELECT COUNT(*) FROM `queue_entry` WHERE `execution_id`=ExecutionId INTO expected_num;

	IF result_num>=expected_num
    THEN
		SELECT `id` INTO EntryId FROM `email` WHERE `TYPE`=0 AND `value`=ExecutionId AND `status`=0 ORDER BY 1 LIMIT 1;
		UPDATE `nitrogenates`.`email` SET `status` = 1, `updated_date` = NOW() WHERE `id`=EntryId;
	END IF;
  
END $$

DELIMITER ;
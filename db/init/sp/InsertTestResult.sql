# Deprecated!!
USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `InsertTestResult`;

DELIMITER $$

CREATE PROCEDURE `InsertTestResult`(
  IN EntryId INT UNSIGNED,
  IN TestName VARCHAR(500),
  IN SlaveName VARCHAR(100),
  IN StartTime VARCHAR(15),
  IN EndTime VARCHAR(15),
  IN ExecResult INT,
  IN Message VARCHAR(2000),
  IN StackTrace VARCHAR(5000),
  IN ScreenshotUrl VARCHAR(500),
  IN RoundId INT,
  IN ProjectId INT,
  IN ENV VARCHAR(200))
BEGIN
  INSERT `test_result`
  (
    `entry_id`,
    `test_name`,
    `slave_name`,
    `start_time`,
    `end_time`,
    `exec_result`,
    `message`,
    `stack_trace`,
    `screenshot_url`,
    `round_id`,
    `project_id`,
    `env`
  )
  VALUES
  (
    EntryId,
    TestName,
    SlaveName,
    StartTime,
    EndTime,
    ExecResult,
    Message,
    StackTrace,
    ScreenshotUrl,
    RoundId,
    ProjectId,
    ENV
  );
END $$

DELIMITER ;
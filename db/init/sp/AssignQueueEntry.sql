USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `AssignQueueEntry`;

DELIMITER $$

# `status` enum:
# 0 - waiting
# 1 - running
# 2 - finished
# 3 - stopped
CREATE PROCEDURE `AssignQueueEntry`(
  IN SlaveName VARCHAR(100),
  OUT EntryId INT UNSIGNED)
this_proc:BEGIN
  DECLARE ProjectId INT UNSIGNED;
  DECLARE EntryName VARCHAR(500);
  DECLARE IsSlaveListening BIT;
  DECLARE SlaveConcurrency INT UNSIGNED;
  DECLARE CurrentConcurrency INT UNSIGNED;
  DECLARE ConcurrencySum INT UNSIGNED;
  DECLARE UniqueIndex INT UNSIGNED;
  
  #SET autocommit := 0;
  #LOCK TABLES `queue_entry` WRITE;
  #START TRANSACTION;
  
  #SET EntryId := 0;
  SELECT `is_listening` INTO IsSlaveListening FROM `slave` WHERE `machine_name` = SlaveName LIMIT 1;

  IF IsSlaveListening = 0
  THEN LEAVE this_proc;
  END IF;

  SET ProjectId := 0;
  SELECT `concurrency` INTO SlaveConcurrency FROM `slave` WHERE `machine_name` = SlaveName LIMIT 1;
  SELECT SUM(`concurrency`) INTO ConcurrencySum FROM `slave`;
  SELECT COUNT(`id`) INTO CurrentConcurrency FROM `queue_entry` WHERE `slave_name` = SlaveName AND `status` = 1;
  
  IF CurrentConcurrency < SlaveConcurrency 
  THEN
    # ProjectId=0 means assigning a queue entry regardless of project priority
    IF ProjectId = 0
    THEN
      # Queue entries are inserted into `queue_entry` with `status` set to 0
      SELECT `project_id` INTO ProjectId FROM `queue_entry` WHERE `status` = 0 ORDER BY `id` ASC LIMIT 1;
    END IF;

    # If ProjectId is still 0, there's no entries to execute, exit.
    IF ProjectId = 0 OR ProjectId = NULL
    THEN LEAVE this_proc;
    END IF;

    # Generate an unique index
    SET UniqueIndex := 1;

    # Search current queue to find an index unique for current project.
    # Only search for queue entry in 1 (running) status.
    search_index_loop:WHILE UniqueIndex <= ConcurrencySum DO
      IF NOT EXISTS (SELECT `index` FROM `queue_entry` WHERE `project_id` = ProjectId AND `status` = 1 AND `index` = UniqueIndex ORDER BY `id` ASC)
      THEN 
        # Only search for queue entry in waiting status.
        SELECT `id` INTO EntryId FROM `queue_entry` WHERE `project_id` = ProjectId AND `status` = 0 ORDER BY `id` ASC LIMIT 1;

        UPDATE `queue_entry` SET `slave_name` = SlaveName, `index` = UniqueIndex, `status` = 1, `start_time` = NOW() WHERE `id` = EntryId;
        LEAVE search_index_loop;
      END IF;

      SET UniqueIndex := UniqueIndex + 1;
    END WHILE;
  END IF;

  #SELECT `id`, `status`, `name`, `slave_name`, `index`, `start_time`, `end_time`, `round_id`, `project_id`, `env`, `jvm_options`, `params` FROM `queue_entry` WHERE `id` = EntryId;
  #COMMIT;
  #UNLOCK TABLES;
END $$

DELIMITER ;
USE `nitrogenates`;

DROP PROCEDURE IF EXISTS `DeleteProject`;

DELIMITER $$

# `domain_key` enum:
# 0 - execution
# 1 - testSuite
# 2 - project
CREATE PROCEDURE `DeleteProject`(
    IN ProjectId INT UNSIGNED)
BEGIN
    DELETE `cp` FROM `custom_parameter` AS `cp` JOIN `execution` AS `e` ON `cp`.`domain_value`=`e`.`id` WHERE `cp`.`domain_key`=0 AND `e`.`project_id`=ProjectId;
    DELETE `cp` FROM `custom_parameter` AS `cp` JOIN `test_suite` AS `ts` ON `cp`.`domain_value`=`ts`.`id` WHERE `cp`.`domain_key`=1 AND `ts`.`project_id`=ProjectId;
    DELETE FROM `custom_parameter` AS `cp` WHERE `cp`.`domain_key`=2 AND `cp`.`domain_value`=ProjectId;

    DELETE FROM `queue_entry` WHERE `project_id`=projectId;

    DELETE FROM `execution` WHERE `project_id`=projectId;

    DELETE `tg-tc` FROM `test_group-test_case` AS `tg-tc` JOIN `test_group` AS `tg` ON `tg-tc`.`test_group_id`=`tg`.`id` WHERE `tg`.`project_id`=ProjectId;

    DELETE FROM `test_group` WHERE `project_id`=projectId;

    DELETE `ts-tc` FROM `test_suite-test_case` AS `ts-tc` JOIN `test_suite` AS `ts` ON `ts-tc`.`test_suite_id`=`ts`.`id` WHERE `ts`.`project_id`=ProjectId;

    DELETE FROM `test_suite` WHERE `project_id`=projectId;

    DELETE FROM `test_result` WHERE `project_id`=projectId;

    DELETE FROM `test_case` WHERE `project_id`=projectId;

    DELETE FROM `project` WHERE `id`=projectId;
END $$

DELIMITER ;
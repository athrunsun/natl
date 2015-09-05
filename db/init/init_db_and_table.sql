CREATE DATABASE IF NOT EXISTS `nitrogenates`
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_unicode_ci;

USE `nitrogenates`;

# `status` enum:
# 0 - WAITING
# 1 - RUNNING
# 2 - FINISHED
# 3 - STOPPED
CREATE TABLE IF NOT EXISTS `queue_entry` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `status` INT NOT NULL,
  `test_case_id` INT UNSIGNED NOT NULL,
  `slave_name` VARCHAR(100) NOT NULL,
  `index` INT,
  `start_time` DATETIME,
  `end_time` DATETIME,
  `execution_id` INT UNSIGNED NOT NULL,
  `project_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `execution` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `project_id` INT UNSIGNED NOT NULL,
  `test_suite_id` INT,
  `created_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

# `type` enum:
# 0 - JVM
# 1 - TESTNG
#
# `domain_key` enum:
# 0 - execution
# 1 - testsuite
# 2 - project
CREATE TABLE IF NOT EXISTS `custom_parameter` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `key` VARCHAR(100) NOT NULL,
  `value` VARCHAR(500) NOT NULL,
  `domain_key` INT NOT NULL,
  `domain_value` INT NOT NULL,
  `type` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `test_case` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(500) NOT NULL,
  `version` INT UNSIGNED NOT NULL,
  `project_id` INT UNSIGNED NOT NULL,
  `mapping_id` VARCHAR(100),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `test_group` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `project_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `test_group-test_case` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `test_group_id` INT UNSIGNED NOT NULL,
  `test_case_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `test_suite` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `project_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `test_suite-test_case` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `test_suite_id` INT UNSIGNED NOT NULL,
  `test_case_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

# `exec_result` enum:
# 0 - UNKNOWN
# 1 - SKIPPED
# 2 - PASSED
# 3 - FAILED
CREATE TABLE IF NOT EXISTS `test_result` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `entry_id` INT UNSIGNED NOT NULL,
  `test_case_id` INT UNSIGNED NOT NULL,
  `slave_name` VARCHAR(100) NOT NULL,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `exec_result` INT NOT NULL,
  `message` VARCHAR(2000) NOT NULL,
  `stack_trace` TEXT(20000) NOT NULL,
  `screenshot_url` VARCHAR(500) NOT NULL,
  `execution_id` INT UNSIGNED NOT NULL,
  `project_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `slave` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `machine_name` VARCHAR(100) NOT NULL,
  `status` INT,
  `concurrency` INT NOT NULL,
  `is_listening` BIT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `project` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  `jar_with_dependency_name` VARCHAR(500) NOT NULL,
  `git_url` VARCHAR(500) NOT NULL,
  `total_test_case_count` INT NOT NULL,
  `latest_test_case_version` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

# `type` enum:
# 0 - Comment
# 1 - Bug
# 2 - Suggestion
# 3 - Question
CREATE TABLE IF NOT EXISTS `feedback` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `message` VARCHAR(12000) NOT NULL,
  `contact_info` VARCHAR(100),
  `create_date` DATETIME NOT NULL,
  `type` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

# `status` enum:
# 0 - STARTED
# 1 - READY
# 2 - SENT
#
# `type` enum:
# 0 - Execution
# 1 - Suite
# 2 - CaseReload
CREATE TABLE IF NOT EXISTS `email` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `from` VARCHAR(100),
  `to` VARCHAR(100) NOT NULL,
  `cc` VARCHAR(100),
  `subject` VARCHAR(1000),
  `message` TEXT(65000),
  `status` INT NOT NULL,
  `updated_date` DATETIME NOT NULL,
  `type` INT NOT NULL,
  `value` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
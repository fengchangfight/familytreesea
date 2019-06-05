CREATE TABLE IF NOT EXISTS `ft_user` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `username` varchar(200) DEFAULT NULL,
  `password` varchar(200) NOT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `nickname` varchar(200) NOT NULL,
  `salt` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `license_code` text,
  `register_date` timestamp NULL DEFAULT NULL,
  UNIQUE(`phone`) ON CONFLICT ABORT,
  UNIQUE(`username`) ON CONFLICT ABORT,
  UNIQUE(`email`) ON CONFLICT ABORT
);

CREATE TABLE IF NOT EXISTS `ft_tree` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `name` varchar(200) NOT NULL,
  `description` text,
  `created_by` integer NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(`name`,`created_by`) ON CONFLICT ABORT,
  FOREIGN KEY (`created_by`) REFERENCES `ft_user` (`id`) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `ft_person` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `name` varchar(200) NOT NULL,
  `sex` integer NOT NULL DEFAULT '1',
  `family_tree_id` integer NOT NULL,
  `life_description` mediumtext,
  `born` varchar(45) DEFAULT NULL,
  `death` varchar(45) DEFAULT NULL,
  `profile_img_path` varchar(1000) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `short_description` text,
  `zibei` varchar(45) DEFAULT NULL,
  `rank` integer DEFAULT NULL,
  FOREIGN KEY (`family_tree_id`) REFERENCES `ft_tree` (`id`) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `ft_img_upload_history` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `upload_by` integer NOT NULL,
  `person_id` integer NOT NULL,
  `obj_key` varchar(100) DEFAULT NULL,
  `upload_time` timestamp NULL DEFAULT NULL,
  FOREIGN KEY (`person_id`) REFERENCES `ft_person` (`id`),
  FOREIGN KEY (`upload_by`) REFERENCES `ft_user` (`id`)
);

CREATE TABLE IF NOT EXISTS `ft_relationship` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `family_tree_id` integer NOT NULL,
  `from_person_id` integer NOT NULL,
  `to_person_id` integer NOT NULL,
  `type` varchar(100) NOT NULL,
  `higher` integer NOT NULL,
  UNIQUE(`from_person_id`,`to_person_id`) ON CONFLICT ABORT,
  FOREIGN KEY (`from_person_id`) REFERENCES `ft_person` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`to_person_id`) REFERENCES `ft_person` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`family_tree_id`) REFERENCES `ft_tree` (`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `ft_role` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `name` varchar(200) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS `ft_snapshot` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `origin_ft_name` varchar(50) DEFAULT NULL,
  `created_by` integer NOT NULL,
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `origin_ft_id` integer DEFAULT NULL,
  UNIQUE(`name`,`created_by`) ON CONFLICT ABORT,
  FOREIGN KEY (`created_by`) REFERENCES `ft_user` (`id`)
);

CREATE TABLE IF NOT EXISTS `ft_snapshot_image_upload` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `snapshot_id` integer NOT NULL,
  `upload_by` integer DEFAULT NULL,
  `person_id` integer DEFAULT NULL,
  `obj_key` varchar(100) DEFAULT NULL,
  `upload_time` timestamp NULL DEFAULT NULL,
  FOREIGN KEY (`snapshot_id`) REFERENCES `ft_snapshot` (`id`)
);

CREATE TABLE IF NOT EXISTS `ft_snapshot_person` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `snapshot_id` integer NOT NULL,
  `person_id` integer DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `sex` integer DEFAULT NULL,
  `life_description` mediumtext,
  `born` varchar(45) DEFAULT NULL,
  `death` varchar(45) DEFAULT NULL,
  `profile_img_path` varchar(1000) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `short_description` text,
  `zibei` varchar(45) DEFAULT NULL,
  `rank` integer DEFAULT NULL,
  FOREIGN KEY (`snapshot_id`) REFERENCES `ft_snapshot` (`id`)
);

CREATE TABLE IF NOT EXISTS `ft_snapshot_relation` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `snapshot_id` integer NOT NULL,
  `from_person_id` integer DEFAULT NULL,
  `to_person_id` integer DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `higher` integer DEFAULT NULL,
  FOREIGN KEY (`snapshot_id`) REFERENCES `ft_snapshot` (`id`)
);

CREATE TABLE IF NOT EXISTS `ft_user_role` (
  `id` integer PRIMARY KEY AUTOINCREMENT,
  `user_id` integer DEFAULT NULL,
  `role_id` integer DEFAULT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `ft_user` (`id`) ON UPDATE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `ft_role` (`id`) ON UPDATE CASCADE
);

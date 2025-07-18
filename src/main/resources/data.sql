-- INITIALIZE ADMIN
INSERT IGNORE INTO USERS(id, email, password, username, first_name, last_name, status, created_at, password_expiration, position) VALUES(1, 'admin@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'ADM', 'Administrator', 'X', 'ACT', CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 50 YEAR), "CEO");

-- INITIALIZE ROOT DIRECTORY
INSERT IGNORE INTO DIRECTORY(id, name, description, date_created, date_modified, created_by_id) VALUES(1, 'YONDU Wiki', 'root', CURRENT_DATE, CURRENT_DATE, 1);

-- INITIALIZE ROLE
INSERT IGNORE INTO ROLE(id, role_name) VALUES(1, 'Administrator'), (2, 'Moderator'), (3, 'Content Creator'), (4, 'User');
--INSERT IGNORE INTO ROLE(id, role_name) VALUES(2, 'test');

-- INITIALIZE USER-PERMISSIONS
INSERT IGNORE INTO PERMISSION(id, name, description, category)
VALUES
-- USERS MODULE
(1, 'CREATE_USERS', 'Create users', 'User'),
(2, 'UPDATE_USERS', 'Update users', 'User'),
(3, 'DEACTIVATE_USERS', 'Deactivate users', 'User'),
(4, 'VIEW_USERS', 'View users', 'User'),
-- CONTENT MODULE
(5, 'CREATE_CONTENT', 'Allows user to create content', 'Content'),
(6, 'UPDATE_CONTENT', 'Allows user to update created content', 'Content'),
(7, 'DELETE_CONTENT', 'Allows user to delete created content', 'Content'),
(8, 'READ_CONTENT', 'Allows user to read created contents', 'Content'),
(26, 'MANAGE_PAGE_PERMISSIONS', 'Allow user to manage the page permissions', 'Content'),
-- CONTENT MODERATION MODULE
(9, 'CONTENT_APPROVAL', 'Allows user to approve created content', 'Content Moderation'),
-- PAGE COLLABORATION & DISCUSSION
-- PAGE COMMENTS
(10, 'COMMENT_AVAILABILITY', 'Allows user to enable or disable content', 'Comment'),
(11, 'CREATE_COMMENT', 'Allows user to create comment in a page', 'Comment'),
(12, 'UPDATE_COMMENT', 'Allows user to update comment in a page', 'Comment'),
(13, 'DELETE_COMMENT', 'Allows user to delete their own comment', 'Comment'),
(14, 'VIEW_COMMENTS', 'Allows user to view comments in the page', 'Comment'),
-- PAGE EDITORS
-- (15, 'UPDATE_PAGE_EDITOR', 'Allow user to add/remove page editors in a page', 'Page Editor'),
-- DIRECTORY ACCESS
(16, 'CREATE_DIRECTORY', 'Allow user to create directory', 'Directory'),
(17, 'UPDATE_DIRECTORY', 'Allow user to update directory', 'Directory'),
(18, 'DELETE_DIRECTORY', 'Allow user to delete directory', 'Directory'),
(19, 'VIEW_DIRECTORY', 'Allow user to view directory', 'Directory'),
(25, 'MANAGE_DIRECTORY_PERMISSIONS', 'Allow user to manage the directory permissions', 'Directory'),
-- ROLES
(20, 'CREATE_ROLES', 'Allow user to create new roles', 'Roles'),
(21, 'UPDATE_ROLES', 'Allow user to update roles', 'Roles'),
(22, 'DELETE_ROLES', 'Allow user to delete roles', 'Roles'),
-- (23, 'MANAGE_ROLES', 'Allow user to manage roles', 'Roles'),
(24, 'READ_ROLES', 'Allow user to view roles', 'Roles');

-- INITIALIZE ROLE PERMISSION
 INSERT IGNORE INTO role_permission(role_id, permission_id)
 -- Administrator: 1
 -- Moderator: 2
 -- Content Creator: 3
 -- User: 4
 -- Administrator
 VALUES
 (1, 1),
 (1, 2),
 (1, 3),
 (1, 4),
 (1, 5),
 (1, 6),
 (1, 7),
 (1, 8),
 (1, 9),
 (1, 10),
 (1, 11),
 (1, 12),
 (1, 13),
 (1, 14),
--  (1, 15),
 (1, 16),
 (1, 17),
 (1, 18),
 (1, 19),
 (1, 20),
 (1, 21),
 (1, 22),
--  (1, 23),
 (1, 24),
 (1, 25),
 (1, 26),
 -- Moderator
 (2, 2),
 (2, 3),
 (2, 4),
 (2, 8),
 (2, 9),
 (2, 11),
 (2, 12),
 (2, 13),
 (2, 14),
 (2, 19),
 (2, 24),
 (2, 26),
 -- Content Creator
 (3, 4),
 (3, 5),
 (3, 6),
 (3, 7),
 (3, 8),
 (3, 10),
 (3, 11),
 (3, 12),
 (3, 13),
 (3, 14),
--  (3, 15),
 (3, 16),
 (3, 17),
 (3, 18),
 (3, 19),
 (3, 24),
 (3, 25),
 (3, 26),
 -- Users (Readers)
 (4, 4),
 (4, 8),
 (4, 11),
 (4, 12),
 (4, 13),
 (4, 14),
 (4, 19),
 (4, 24);

-- INITIALIZE USER ROLE
INSERT IGNORE INTO USER_ROLE(user_id, role_id) VALUES(1, 1);
INSERT IGNORE INTO directory_user_access VALUES
(1,1,5,1),
(2,1,6,1),
(3,1,7,1),
(4,1,8,1),
(5,1,9,1);

-- INITIALIZE USER RIGHTS
-- INSERT IGNORE INTO USER_RIGHTS(user_id, rights_id) VALUES(1, 1), (1, 2), (1, 3), (1, 4);

INSERT IGNORE INTO CLUSTER(id, name, description, is_active, date_created) VALUES (1, "Yondu", "Group for all users", true, CURRENT_DATE);
INSERT IGNORE INTO GROUP_PERMISSIONS VALUES 
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 19);

INSERT IGNORE INTO DIRECTORY_GROUP_ACCESS(id, directory_id, group_id, permission_id) VALUES
(1, 1, 1, 19);


-- INITIALIZE CATEGORIES
INSERT IGNORE INTO category(id, is_deleted, name) VALUES
(1, 0, 'Technology'),
(2, 0, 'Work Life Balance'),
(3, 0, 'Career');

---- Add FULLTEXT index to the `first_name` and `last_name` columns in the `users` table
ALTER TABLE users ADD FULLTEXT INDEX idx_users_name (first_name, last_name);
--
---- Add FULLTEXT index to the `title` and `content` column in the `page_version` table
ALTER TABLE page_version ADD FULLTEXT INDEX idx_page_version_title_content (title, content);

-- Populate Users to assign as Workflow Approvers pass: adm1n1strat0r

INSERT IGNORE INTO USERS (id, email, password, username, first_name, last_name, status, created_at, password_expiration, position)
VALUES
    (2, 'john@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user1', 'John', 'Doe', 'ACT', CURRENT_DATE, CURRENT_DATE, "Mid Level Full Stack Engineer"),
    (3, 'jane@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user2', 'Jane', 'Smith', 'ACT', CURRENT_DATE, CURRENT_DATE, "Jr. Software Engineer"),
    (4, 'michael@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user3', 'Michael', 'Johnson', 'ACT', CURRENT_DATE, CURRENT_DATE, "Team Lead"),
    (5, 'emily@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user4', 'Emily', 'Williams', 'ACT', CURRENT_DATE, CURRENT_DATE, "Sr. Software Engineer"),
    (6, 'david@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user5', 'David', 'Brown', 'ACT', CURRENT_DATE, CURRENT_DATE, "Manager");

INSERT IGNORE INTO USER_ROLE (user_id, role_id)
VALUES
    (2, 2),
    (3, 2),
    (4, 2),
    (5, 2),
    (6, 2);

-- Populate workflow Entity
INSERT IGNORE INTO workflow (id, directory_id)
VALUES (1, 1);

-- Populate workflow Step Entity
INSERT IGNORE INTO workflow_step (id, step, name , workflow_id)
VALUES
    (1, 1,'MODERATOR', 1);

-- Populate workflow Step Approver
INSERT IGNORE INTO workflow_step_approver (id, workflow_step_id, approver_id)
VALUES
    (1, 1, 1),
    (1, 1, 2),
    (2, 1, 3),
    (3, 1, 4),
    (4, 1, 5),
    (5, 1, 6);

UPDATE directory SET workflow_id = 1 WHERE id = 1;

---- Add FULLTEXT index to the `title` and `modified_content` column in the `post` table
ALTER TABLE post ADD FULLTEXT INDEX idx_post_title_content (title, modified_content);

INSERT IGNORE INTO group_users(group_id, user_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6);
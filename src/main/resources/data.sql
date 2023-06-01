-- INITIALIZE ADMIN
INSERT IGNORE INTO USERS(id, email, password, username, first_name, last_name, status, created_at) VALUES(1, 'admin@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'ADM', 'Administrator', '', 'ACT', CURRENT_DATE);

-- INITIALIZE ROOT DIRECTORY
INSERT IGNORE INTO DIRECTORY(id, name, description, date_created, date_modified, created_by_id) VALUES(1, 'YONDU Wiki', 'root directory', CURRENT_DATE, CURRENT_DATE, 1);

-- INITIALIZE ROLE
INSERT IGNORE INTO ROLE(id, role_name) VALUES(1, 'admin'), (2, 'Moderator'), (3, 'Content Creator'), (4, 'User');
--INSERT IGNORE INTO ROLE(id, role_name) VALUES(2, 'test');

-- INITIALIZE USER-PERMISSIONS
INSERT INTO PERMISSION(id, name, description, category)
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
(15, 'UPDATE_PAGE_EDITOR', 'Allow user to add/remove page editors in a page', 'Page Editor'),
-- DIRECTORY ACCESS
(16, 'CREATE_DIRECTORY', 'Allow user to create directory', 'Directory'),
(17, 'UPDATE_DIRECTORY', 'Allow user to create directory', 'Directory'),
(18, 'DELETE_DIRECTORY', 'Allow user to create directory', 'Directory'),
(19, 'VIEW_DIRECTORY', 'Allow user to create directory', 'Directory'),
(25, 'MANAGE_DIRECTOR_PERMISSIONS', 'Allow user to manage the directory permissions', 'Directory'),
-- ROLES
(20, 'CREATE_ROLES', 'Allow user to create new roles', 'Roles'),
(21, 'UPDATE_ROLES', 'Allow user to update roles', 'Roles'),
(22, 'DELETE_ROLES', 'Allow user to delete roles', 'Roles'),
(23, 'MANAGE_ROLES', 'Allow user to manage roles', 'Roles'),
(24, 'READ_ROLES', 'Allow user to view roles', 'Roles');

-- INITIALIZE ROLE PERMISSION
 INSERT INTO role_permission(role_id, permission_id)
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
 (1, 15),
 (1, 16),
 (1, 17),
 (1, 18),
 (1, 19),
 (1, 20),
 (1, 21),
 (1, 22),
 (1, 23),
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
 (3, 15),
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
--INSERT IGNORE INTO USER_ROLE(user_id, role_id) VALUES(1, 2);

-- INITIALIZE DIRECTORY USER ACCESS
INSERT IGNORE INTO DIRECTORY_USER_ACCESS(id, directory_id, user_id, permission_id) VALUES(1, 1, 1, 16), (2, 1, 1, 17), (3, 1, 1, 18), (4, 1, 1, 19);

-- INITIALIZE DIRECTORY RIGHTS FOR ROOT
INSERT IGNORE INTO RIGHTS(id) VALUES(1), (2), (3);
INSERT IGNORE INTO DIRECTORY_RIGHTS(id, directory_id, permission_id) VALUES(1, 1, 16), (2, 1, 19), (3, 1, 25);

-- INITIALIZE USER RIGHTS
INSERT IGNORE INTO USER_RIGHTS(user_id, rights_id) VALUES(1, 1), (1, 2), (1, 3);

-- Create a new page
INSERT INTO page (date_created, author, is_active, is_deleted, lock_start, lock_end, locked_by, directory_id)
VALUES (CURRENT_TIMESTAMP, 1, true, false, null, null, null, 1);

-- Create a new page version
INSERT INTO page_version (title, content, date_modified, modified_by, page_id)
VALUES ('Page Version Title', 'Page Version Content', CURRENT_TIMESTAMP, 1, (SELECT id FROM page ORDER BY id DESC LIMIT 1));

INSERT INTO CLUSTER(id, name, description) VALUES (1, "HI", "HELLO");

-- Create user page permission
INSERT INTO user_page_permission (date_created, last_modified, is_active, page_id, user_id, permission_id) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 5), (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 6),(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 7), (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 8),(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 9),(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 10),(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 11),(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 12),(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 13),(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 14),(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 1, 1, 15);

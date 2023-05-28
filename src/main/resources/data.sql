INSERT IGNORE INTO USERS(email, password, username, first_name, last_name, status, created_at) VALUES('admin@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'ADM', 'Administrator', '', 'ACT', CURRENT_DATE);

-- INITIALIZE ROOT DIRECTORY
INSERT IGNORE INTO DIRECTORY(id, name, description, date_created, date_modified) VALUES(1, 'YONDU Wiki', 'root directory', CURRENT_DATE, CURRENT_DATE);

-- DIRECTORY PERMISSIONS
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description, is_deleted) VALUES(1, 'Create Directory', 'Allows users to create new directories within the specified directory', 0);
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description, is_deleted) VALUES(2, 'Edit Directory', 'Allows users to edit the directory properties of the specified directory', 0);
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description, is_deleted) VALUES(3, 'View Directory', 'Allows users to view the content and metadata of a directory', 0);
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description, is_deleted) VALUES(4, 'Delete Directory', 'Allows users to delete the directory and its contents', 0);
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description, is_deleted) VALUES(5, 'Move Directory', 'Allows users to move directory to different locations within the directory structure', 0);
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description, is_deleted) VALUES(6, 'Manage Permissions', 'Allows users to manage permissions for a directory, including assigning roles and users', 0);
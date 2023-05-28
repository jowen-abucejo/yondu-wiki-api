INSERT IGNORE INTO USERS(email, password, username, first_name, last_name, status, created_at) VALUES('admin@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'ADM', 'Administrator', '', 'ACT', CURRENT_DATE);
INSERT IGNORE INTO DIRECTORY(id, name) VALUES(1, 'root');

-- DIRECTORY PERMISSIONS
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description) VALUES(1, 'Create Directory', 'Allows users to create new directories within the specified directory')
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description) VALUES(2, 'Edit Directory', 'Allows users to edit the directory properties of the specified directory')
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description) VALUES(3, 'View Directory', 'Allows users to view the content and metadata of a directory')
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description) VALUES(4, 'Delete Directory', 'Allows users to delete the directory and its contents')
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description) VALUES(5, 'Move Directory', 'Allows users to move directory to different locations within the directory structure')
INSERT IGNORE INTO DIRECTORY_PERMISSION(id, name, description) VALUES(6, 'Manage Permissions', 'Allows users to manage permissions for a directory, including assigning roles and users')
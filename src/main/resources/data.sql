-- INITIALIZE ADMIN
INSERT IGNORE INTO USERS(id, email, password, username, first_name, last_name, status, created_at, password_expiration) VALUES(1, 'admin@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'ADM', 'Administrator', 'X', 'ACT', CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 50 YEAR));

-- INITIALIZE ROOT DIRECTORY
INSERT IGNORE INTO DIRECTORY(id, name, description, date_created, date_modified, created_by_id) VALUES(1, 'YONDU Wiki', 'root', CURRENT_DATE, CURRENT_DATE, 1);

-- INITIALIZE ROLE
INSERT IGNORE INTO ROLE(id, role_name) VALUES(1, 'Administrator'), (2, 'Moderator'), (3, 'Content Creator'), (4, 'User');
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
(17, 'UPDATE_DIRECTORY', 'Allow user to update directory', 'Directory'),
(18, 'DELETE_DIRECTORY', 'Allow user to delete directory', 'Directory'),
(19, 'VIEW_DIRECTORY', 'Allow user to view directory', 'Directory'),
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
INSERT IGNORE INTO USER_ROLE(user_id, role_id) VALUES(2, 2);
--INSERT IGNORE INTO USER_ROLE(user_id, role_id) VALUES(1, 2);

-- INITIALIZE DIRECTORY RIGHTS FOR ROOT
INSERT IGNORE INTO RIGHTS(id) VALUES(1), (2), (3), (4);
INSERT IGNORE INTO DIRECTORY_RIGHTS(id, directory_id, permission_id) VALUES(1, 1, 16), (2, 1, 19), (3, 1, 25), (4, 1, 5);
INSERT IGNORE INTO directory_user_access VALUES
(1,1,5,1),
(2,1,6,1),
(3,1,7,1),
(4,1,8,1),
(5,1,9,1);

-- INITIALIZE USER RIGHTS
INSERT IGNORE INTO USER_RIGHTS(user_id, rights_id) VALUES(1, 1), (1, 2), (1, 3), (1, 4);

-- Create a new page
INSERT INTO page (date_created, author, is_active, is_deleted, lock_start, lock_end, locked_by, directory_id, page_type)
VALUES 
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'ANNOUNCEMENT'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'ANNOUNCEMENT'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'WIKI'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'WIKI'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'WIKI'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'WIKI'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'WIKI'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'WIKI'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'WIKI'),
    (CURRENT_TIMESTAMP, 1, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, 'WIKI');

-- Create a new page versionS
INSERT INTO page_version (title, content, original_content, date_modified, modified_by, page_id)
VALUES
    -- Page 1
    ('Jumping Cat', 'The cat jumped over the fence.', '<div>The cat jumped over the fence.</div>', '2023-03-01 09:15:00', 1, 1),
    ('Delicious Ice Cream', 'I love ice cream.', '<div>I love ice cream.</div>', '2023-04-05 14:30:00', 1, 1),
    ('Beautiful Song', 'She sang her favorite song.', '<div>She sang her favorite song.</div>', '2023-05-10 18:45:00', 1, 1),
    ('Playful Dog', 'The dog chased its tail.', '<div>The dog chased its tail.</div>', '2023-06-15 12:00:00', 1, 1),
    ('Tasty Pizza', 'He ate a delicious pizza.', '<div>He ate a delicious pizza.</div>', '2023-07-20 16:30:00', 1, 1),
    
    -- Page 2
    ('Joyful Smile', 'She smiled at the camera.', '<div>She smiled at the camera.</div>', '2023-03-02 10:00:00', 1, 2),
    ('Flying Bird', 'The bird flew high in the sky.', '<div>The bird flew high in the sky.</div>', '2023-04-06 15:15:00', 1, 2),
    ('Dancing All Night', 'They danced all night long.', '<div>They danced all night long.</div>', '2023-05-11 20:30:00', 1, 2),
    ('Fast Car', 'The car raced down the track.', '<div>The car raced down the track.</div>', '2023-06-16 14:45:00', 1, 2),
    ('Laughing at Joke', 'She laughed at the funny joke.', '<div>She laughed at the funny joke.</div>', '2023-07-21 18:00:00', 1, 2),
    
    -- Page 3
    ('Favorite Song on Guitar', 'He played his favorite song on the guitar.', '<div>He played his favorite song on the guitar.</div>', '2023-03-03 11:30:00', 1, 3),
    ('Interesting Book', 'The book contained many interesting stories.', '<div>The book contained many interesting stories.</div>', '2023-04-07 16:45:00', 1, 3),
    ('Steep Mountain Hike', 'They hiked up the steep mountain.', '<div>They hiked up the steep mountain.</div>', '2023-05-12 21:00:00', 1, 3),
    ('Heartfelt Letter', 'She wrote a heartfelt letter to her friend.', '<div>She wrote a heartfelt letter to her friend.</div>', '2023-06-17 15:15:00', 1, 3),
    ('Bright Sunny Day', 'The sun shone brightly in the clear sky.', '<div>The sun shone brightly in the clear sky.</div>', '2023-07-22 19:30:00', 1, 3),
    
    -- Page 4
    ('Running Fast', 'He ran as fast as he could.', '<div>He ran as fast as he could.</div>', '2023-03-04 12:45:00', 1, 4),
    ('Shady Tree', 'The tree provided shade on a hot day.', '<div>The tree provided shade on a hot day.</div>', '2023-04-08 18:00:00', 1, 4),
    ('Endless Conversation', 'They talked for hours about their dreams.', '<div>They talked for hours about their dreams.</div>', '2023-05-13 22:15:00', 1, 4),
    ('Heavy Rainfall', 'The rain poured down in heavy drops.', '<div>The rain poured down in heavy drops.</div>', '2023-06-18 16:30:00', 1, 4),
    ('Artistic Painting', 'She painted a beautiful landscape.', '<div>She painted a beautiful landscape.</div>', '2023-07-23 20:45:00', 1, 4),
    
    -- Page 5
    ('Morning Coffee', 'He enjoyed a cup of coffee in the morning.', '<div>He enjoyed a cup of coffee in the morning.</div>', '2023-08-01 08:00:00', 1, 5),
    ('Quiet Library', 'The library provided a peaceful atmosphere for studying.', '<div>The library provided a peaceful atmosphere for studying.</div>', '2023-09-05 10:30:00', 1, 5),
    ('Gardening Hobby', 'She spent her weekends tending to her garden.', '<div>She spent her weekends tending to her garden.</div>', '2023-10-10 14:45:00', 1, 5),
    ('Camping Adventure', 'They went camping in the wilderness and explored nature.', '<div>They went camping in the wilderness and explored nature.</div>', '2023-11-15 17:00:00', 1, 5),
    ('Home Cooking', 'He cooked a delicious meal for his family at home.', '<div>He cooked a delicious meal for his family at home.</div>', '2023-12-20 19:30:00', 1, 5),
    
    -- Page 6
    ('Summer Beach Trip', 'They enjoyed a sunny day at the beach with friends.', '<div>They enjoyed a sunny day at the beach with friends.</div>', '2023-08-02 09:30:00', 1, 6),
    ('Exploring Nature', 'They hiked through the forest and discovered hidden trails.', '<div>They hiked through the forest and discovered hidden trails.</div>', '2023-09-06 11:45:00', 1, 6),
    ('Music Concert', 'They attended a lively concert and danced to the rhythm.', '<div>They attended a lively concert and danced to the rhythm.</div>', '2023-10-11 15:00:00', 1, 6),
    ('Starlit Sky', 'They gazed at the stars in the clear night sky.', '<div>They gazed at the stars in the clear night sky.</div>', '2023-11-16 18:15:00', 1, 6),
    ('Homemade Cookies', 'She baked delicious cookies from scratch in her kitchen.', '<div>She baked delicious cookies from scratch in her kitchen.</div>', '2023-12-21 20:30:00', 1, 6),
    
    -- Page 7
    ('Family Reunion', 'They gathered for a joyful family reunion.', '<div>They gathered for a joyful family reunion.</div>', '2023-08-03 11:00:00', 1, 7),
    ('City Exploration', 'They wandered through the city streets and admired the architecture.', '<div>They wandered through the city streets and admired the architecture.</div>', '2023-09-07 13:15:00', 1, 7),
    ('Book Club Discussion', 'They engaged in a lively discussion about the latest book.', '<div>They engaged in a lively discussion about the latest book.</div>', '2023-10-12 16:30:00', 1, 7),
    ('Winter Wonderland', 'They played in the snow and built a snowman.', '<div>They played in the snow and built a snowman.</div>', '2023-11-17 19:45:00', 1, 7),
    ('Festive Decorations', 'She decorated her home with lights and ornaments for the holidays.', '<div>She decorated her home with lights and ornaments for the holidays.</div>', '2023-12-22 22:00:00', 1, 7),
    
    -- Page 8
    ('Morning Jog', 'He went for a refreshing jog in the park.', '<div>He went for a refreshing jog in the park.</div>', '2023-08-04 12:15:00', 1, 8),
    ('Picnic in the Park', 'They enjoyed a picnic on a sunny day.', '<div>They enjoyed a picnic on a sunny day.</div>', '2023-09-08 14:30:00', 1, 8),
    ('Writing Poetry', 'He expressed his thoughts and emotions through poetic verses.', '<div>He expressed his thoughts and emotions through poetic verses.</div>', '2023-10-13 17:45:00', 1, 8),
    ('Autumn Colors', 'They marveled at the vibrant colors of the fall foliage.', '<div>They marveled at the vibrant colors of the fall foliage.</div>', '2023-11-18 21:00:00', 1, 8),
    ('Cozy Fireplace', 'She snuggled up by the fireplace with a good book.', '<div>She snuggled up by the fireplace with a good book.</div>', '2023-12-23 23:15:00', 1, 8),
    
    -- Page 9
    ('Road Trip Adventure', 'They embarked on an exciting road trip across the country.', '<div>They embarked on an exciting road trip across the country.</div>', '2023-08-05 13:30:00', 1, 9),
    ('Photography Passion', 'He captured stunning images with his camera.', '<div>He captured stunning images with his camera.</div>', '2023-09-09 15:45:00', 1, 9),
    ('Art Exhibition', 'They admired the unique artwork at the gallery.', '<div>They admired the unique artwork at the gallery.</div>', '2023-10-14 19:00:00', 1, 9),
    ('Star Gazing', 'They observed the constellations in the night sky.', '<div>They observed the constellations in the night sky.</div>', '2023-11-19 22:15:00', 1, 9),
    ('Holiday Festivities', 'She celebrated with family and friends during the festive season.', '<div>She celebrated with family and friends during the festive season.</div>', '2023-12-24 00:30:00', 1, 9),
    
    -- Page 10
    ('Morning Sunshine', 'The sun rose above the horizon, filling the sky with golden light.', '<div>The sun rose above the horizon, filling the sky with golden light.</div>', '2023-08-01 08:45:00', 1, 10),
    ('Peaceful Meditation', 'He found inner peace through daily meditation.', '<div>He found inner peace through daily meditation.</div>', '2023-09-05 10:00:00', 1, 10),
    ('Colorful Flower Garden', 'She admired the vibrant colors of the blooming flowers in her garden.', '<div>She admired the vibrant colors of the blooming flowers in her garden.</div>', '2023-10-10 12:15:00', 1, 10),
    ('Hiking Adventure', 'They explored a scenic trail and reached the summit of a mountain.', '<div>They explored a scenic trail and reached the summit of a mountain.</div>', '2023-11-15 14:30:00', 1, 10),
    ('Cozy Winter Evening', 'He snuggled up by the fireplace with a cup of hot cocoa.', '<div>He snuggled up by the fireplace with a cup of hot cocoa.</div>', '2023-12-20 16:45:00', 1, 10),

    -- Additional rows (Page 1)
    ('Serenade under the Moonlight', 'They enjoyed a romantic evening with soft music under the moonlit sky.', '<div>They enjoyed a romantic evening with soft music under the moonlit sky.</div>', '2023-08-02 09:00:00', 1, 1),
    ('Nature Photography', 'He captured the beauty of nature through his lens.', '<div>He captured the beauty of nature through his lens.</div>', '2023-09-06 11:15:00', 1, 1),
    ('Live Concert Experience', 'They attended an energetic live concert and danced the night away.', '<div>They attended an energetic live concert and danced the night away.</div>', '2023-10-11 13:30:00', 1, 1),
    ('Stargazing Adventure', 'They marveled at the wonders of the universe while stargazing on a clear night.', '<div>They marveled at the wonders of the universe while stargazing on a clear night.</div>', '2023-11-16 15:45:00', 1, 1),
    ('Homemade Pie', 'She baked a delicious pie from scratch using fresh ingredients.', '<div>She baked a delicious pie from scratch using fresh ingredients.</div>', '2023-12-21 18:00:00', 1, 1),

    -- Additional rows (Page 2)
    ('Candlelit Dinner', 'They enjoyed a romantic candlelit dinner at a cozy restaurant.', '<div>They enjoyed a romantic candlelit dinner at a cozy restaurant.</div>', '2023-08-03 10:15:00', 1, 2),
    ('Exploring Local Cuisine', 'They indulged in a variety of local dishes during their culinary adventure.', '<div>They indulged in a variety of local dishes during their culinary adventure.</div>', '2023-09-07 12:30:00', 1, 2),
    ('Bookworm\'s Paradise', 'They spent hours immersed in the captivating world of books at the bookstore.', '<div>They spent hours immersed in the captivating world of books at the bookstore.</div>', '2023-10-12 14:45:00', 1, 2),
    ('Sledding Fun', 'They raced down the snowy hill on sleds, filled with laughter and joy.', '<div>They raced down the snowy hill on sleds, filled with laughter and joy.</div>', '2023-11-17 17:00:00', 1, 2),
    ('Festive Baking', 'She decorated cookies and baked treats to share with loved ones during the holiday season.', '<div>She decorated cookies and baked treats to share with loved ones during the holiday season.</div>', '2023-12-22 19:15:00', 1, 2),

    -- Additional rows (Page 3)
    ('Morning Yoga Routine', 'She started her day with a calming yoga session to rejuvenate her mind and body.', '<div>She started her day with a calming yoga session to rejuvenate her mind and body.</div>', '2023-08-04 11:30:00', 1, 3),
    ('Nature Picnic', 'They enjoyed a delightful picnic surrounded by the beauty of nature.', '<div>They enjoyed a delightful picnic surrounded by the beauty of nature.</div>', '2023-09-08 13:45:00', 1, 3),
    ('Creative Writing', 'He let his imagination soar as he penned captivating stories and poems.', '<div>He let his imagination soar as he penned captivating stories and poems.</div>', '2023-10-13 16:00:00', 1, 3),
    ('Autumn Hiking Trail', 'They embarked on a scenic hiking trail, witnessing the breathtaking colors of autumn.', '<div>They embarked on a scenic hiking trail, witnessing the breathtaking colors of autumn.</div>', '2023-11-18 18:15:00', 1, 3),
    ('Reading by the Fire', 'She curled up with a good book, basking in the warm glow of the fireplace.', '<div>She curled up with a good book, basking in the warm glow of the fireplace.</div>', '2023-12-23 20:30:00', 1, 3),

    -- Additional rows (Page 4)
    ('Cross-Country Road Trip', 'They embarked on an epic cross-country road trip, exploring diverse landscapes and cultures.', '<div>They embarked on an epic cross-country road trip, exploring diverse landscapes and cultures.</div>', '2023-08-05 12:45:00', 1, 4),
    ('Capturing Moments', 'He captured precious moments with his camera, preserving memories for a lifetime.', '<div>He captured precious moments with his camera, preserving memories for a lifetime.</div>', '2023-09-09 15:00:00', 1, 4),
    ('Artistic Expression', 'They immersed themselves in the world of art, expressing their creativity through various mediums.', '<div>They immersed themselves in the world of art, expressing their creativity through various mediums.</div>', '2023-10-14 17:15:00', 1, 4),
    ('Starry Night', 'They marveled at the mesmerizing beauty of the starry night sky.', '<div>They marveled at the mesmerizing beauty of the starry night sky.</div>', '2023-11-19 19:30:00', 1, 4),
    ('New Year\'s Celebration', 'She welcomed the new year with joy and excitement, celebrating with loved ones.', '<div>She welcomed the new year with joy and excitement, celebrating with loved ones.</div>', '2023-12-24 21:45:00', 1, 4),

    -- Additional rows (Page 5)
    ('Lazy Weekend Morning', 'They enjoyed a slow and relaxing morning, sipping coffee and reading the newspaper.', '<div>They enjoyed a slow and relaxing morning, sipping coffee and reading the newspaper.</div>', '2023-08-01 10:00:00', 1, 5),
    ('Refreshing Swim', 'They cooled off from the summer heat with a refreshing swim in the pool.', '<div>They cooled off from the summer heat with a refreshing swim in the pool.</div>', '2023-09-05 12:15:00', 1, 5),
    ('Artistic Inspiration', 'She found inspiration for her paintings in the beauty of nature and everyday life.', '<div>She found inspiration for her paintings in the beauty of nature and everyday life.</div>', '2023-10-10 14:30:00', 1, 5),
    ('Mountain Biking Adventure', 'They navigated thrilling trails and conquered challenging terrains on their mountain bikes.', '<div>They navigated thrilling trails and conquered challenging terrains on their mountain bikes.</div>', '2023-11-15 16:45:00', 1, 5),
    ('Cozy Evening with Friends', 'They gathered around the fireplace, sharing laughter and stories with dear friends.', '<div>They gathered around the fireplace, sharing laughter and stories with dear friends.</div>', '2023-12-20 19:00:00', 1, 5),

    -- Additional rows (Page 6)
    ('Music Festival Experience', 'They immersed themselves in the vibrant atmosphere of a music festival, dancing to their favorite tunes.', '<div>They immersed themselves in the vibrant atmosphere of a music festival, dancing to their favorite tunes.</div>', '2023-08-02 11:15:00', 1, 6),
    ('Exploring Ancient Ruins', 'They uncovered the secrets of ancient civilizations as they explored fascinating ruins.', '<div>They uncovered the secrets of ancient civilizations as they explored fascinating ruins.</div>', '2023-09-06 13:30:00', 1, 6),
    ('Artistic Expression', 'He poured his emotions onto the canvas, creating artwork that spoke to the soul.', '<div>He poured his emotions onto the canvas, creating artwork that spoke to the soul.</div>', '2023-10-11 15:45:00', 1, 6),
    ('Nighttime Adventure', 'They embarked on an exhilarating adventure under the starry night sky.', '<div>They embarked on an exhilarating adventure under the starry night sky.</div>', '2023-11-16 18:00:00', 1, 6),
    ('Winter Cozy Reading', 'She curled up with a blanket and a captivating book, escaping into imaginary worlds.', '<div>She curled up with a blanket and a captivating book, escaping into imaginary worlds.</div>', '2023-12-21 20:15:00', 1, 6),

    -- Additional rows (Page 7)
    ('Exploring Hidden Gems', 'They discovered hidden gems in their city, from quaint cafes to hidden parks.', '<div>They discovered hidden gems in their city, from quaint cafes to hidden parks.</div>', '2023-08-03 12:30:00', 1, 7),
    ('Culinary Delights', 'They indulged in a culinary journey, savoring unique flavors and gourmet dishes.', '<div>They indulged in a culinary journey, savoring unique flavors and gourmet dishes.</div>', '2023-09-07 14:45:00', 1, 7),
    ('Writing Inspiration', 'He found inspiration for his writing in the beauty of nature and the intricacies of human emotions.', '<div>He found inspiration for his writing in the beauty of nature and the intricacies of human emotions.</div>', '2023-10-12 17:00:00', 1, 7),
    ('Autumn Scenic Drive', 'They embarked on a picturesque drive, admiring the breathtaking autumn scenery.', '<div>They embarked on a picturesque drive, admiring the breathtaking autumn scenery.</div>', '2023-11-17 19:15:00', 1, 7),
    ('Holiday Traditions', 'She carried out cherished holiday traditions, from decorating the tree to baking gingerbread cookies.', '<div>She carried out cherished holiday traditions, from decorating the tree to baking gingerbread cookies.</div>', '2023-12-22 21:30:00', 1, 7),

    -- Additional rows (Page 8)
    ('Morning Jog', 'She started her day with an invigorating jog, feeling the cool breeze against her skin.', '<div>She started her day with an invigorating jog, feeling the cool breeze against her skin.</div>', '2023-08-04 13:45:00', 1, 8),
    ('Beach Getaway', 'They soaked up the sun and enjoyed the sound of crashing waves on a relaxing beach getaway.', '<div>They soaked up the sun and enjoyed the sound of crashing waves on a relaxing beach getaway.</div>', '2023-09-08 16:00:00', 1, 8),
    ('Poetry and Reflection', 'He delved into the world of poetry, finding solace and self-expression through thoughtful verses.', '<div>He delved into the world of poetry, finding solace and self-expression through thoughtful verses.</div>', '2023-10-13 18:15:00', 1, 8),
    ('Foliage Hiking Trail', 'They explored a scenic hiking trail, surrounded by the vibrant colors of autumn foliage.', '<div>They explored a scenic hiking trail, surrounded by the vibrant colors of autumn foliage.</div>', '2023-11-18 20:30:00', 1, 8),
    ('Cozy Winter Night', 'She snuggled up in a warm blanket and enjoyed a movie marathon on a cozy winter night.', '<div>She snuggled up in a warm blanket and enjoyed a movie marathon on a cozy winter night.</div>', '2023-12-23 22:45:00', 1, 8),

    -- Additional rows (Page 9)
    ('Adventure in the Wilderness', 'They embarked on an exciting adventure in the wilderness, exploring untamed landscapes.', '<div>They embarked on an exciting adventure in the wilderness, exploring untamed landscapes.</div>', '2023-08-05 15:00:00', 1, 9),
    ('Creative Photography', 'He unleashed his creativity through photography, capturing unique perspectives and stunning visuals.', '<div>He unleashed his creativity through photography, capturing unique perspectives and stunning visuals.</div>', '2023-09-09 17:15:00', 1, 9),
    ('Expressive Art', 'They unleashed their creativity through various art forms, expressing their innermost thoughts and emotions.', '<div>They unleashed their creativity through various art forms, expressing their innermost thoughts and emotions.</div>', '2023-10-14 19:30:00', 1, 9),
    ('Moonlit Beach Walk', 'They took a romantic stroll along the moonlit beach, listening to the soothing sound of crashing waves.', '<div>They took a romantic stroll along the moonlit beach, listening to the soothing sound of crashing waves.</div>', '2023-11-19 21:45:00', 1, 9),
    ('New Year\'s Resolutions', 'She reflected on the past year and set meaningful resolutions for the upcoming year.', '<div>She reflected on the past year and set meaningful resolutions for the upcoming year.</div>', '2023-12-24 00:00:00', 1, 9);

INSERT INTO CLUSTER(id, name, description, is_active) VALUES (1, "HI", "HELLO", true);

-- INITIALIZE CATEGORIES
INSERT IGNORE INTO category(id, is_deleted, name) VALUES
(1, 0, 'Technology'),
(2, 0, 'Work Life Balance'),
(3, 0, 'Career');

-- Add FULLTEXT index to the `first_name` and `last_name` columns in the `users` table
ALTER TABLE users ADD FULLTEXT INDEX idx_users_name (first_name, last_name);

-- Add FULLTEXT index to the `title` column in the `page_version` table
ALTER TABLE page_version ADD FULLTEXT INDEX idx_page_version_title_content (title, content);

-- Add FULLTEXT index to the `name` column in the `category` table
ALTER TABLE category ADD FULLTEXT INDEX idx_category_name (name);

-- Add FULLTEXT index to the `name` column in the `tag` table
ALTER TABLE tag ADD FULLTEXT INDEX idx_tag_name (name);

-- Populate Users to test Workflows pass: adm1n1strat0r

INSERT IGNORE INTO USERS (id, email, password, username, first_name, last_name, status, created_at, password_expiration)
VALUES
    (2, 'john@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user1', 'John', 'Doe', 'ACT', CURRENT_DATE, CURRENT_DATE),
    (3, 'jane@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user2', 'Jane', 'Smith', 'ACT', CURRENT_DATE, CURRENT_DATE),
    (4, 'michael@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user3', 'Michael', 'Johnson', 'ACT', CURRENT_DATE, CURRENT_DATE),
    (5, 'emily@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user4', 'Emily', 'Williams', 'ACT', CURRENT_DATE, CURRENT_DATE),
    (6, 'david@yondu.com', '$2a$12$Rr07POwHbDDdbO4gMrbAEuCWOIPvNn/U6CQFDjnrGcLh.G.6T7nj.', 'user5', 'David', 'Brown', 'ACT', CURRENT_DATE, CURRENT_DATE);

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
    (1, 1,'ADMIN', 1),
    (2, 2,'MODERATOR', 1),
    (3, 3,"SME", 1);

-- Populate workflow Step Approver
INSERT IGNORE INTO workflow_step_approver (id, workflow_step_id, approver_id)
VALUES
    (1, 1, 2),
    (2, 1, 3),
    (3, 2, 4),
    (4, 2, 5),
    (5, 3, 6);

UPDATE directory SET workflow_id = 1 WHERE id = 1;

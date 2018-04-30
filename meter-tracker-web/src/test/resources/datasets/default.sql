INSERT INTO image_info(id, url, storage_id, hash, uploaded_at, created_at, file_name)
  VALUES (1, 'files?id=dfc751b80bec8aa64c41e512745c1e64', 'dfc751b80bec8aa64c41e512745c1e64', 'dfc751b80bec8aa64c41e512745c1e64', '2015-02-20 10:12:00', '2015-02-20 09:12:34', '00000_087.jpg');

INSERT INTO image_info(id, url, storage_id, hash, uploaded_at, created_at, file_name)
  VALUES (2, 'files?id=127a2ab163a0673859e1c14f0d12e5a5', '127a2ab163a0673859e1c14f0d12e5a5', null, '2015-02-20 10:12:00', null, 'i_00000_087.jpg');

INSERT INTO image_info(id, url, storage_id, hash, uploaded_at, created_at, file_name)
  VALUES (3, 'files?id=2d320c230cb9e294aa9d78cb6b1af147', '2d320c230cb9e294aa9d78cb6b1af147', '2d320c230cb9e294aa9d78cb6b1af147', '2015-02-20 10:12:34', '2015-02-20 09:13:21', '00000_102.jpg');

INSERT INTO image_info(id, url, storage_id, hash, uploaded_at, created_at, file_name)
  VALUES (4, 'files?id=566d9d151013bb21f9b77feb58741c98', '566d9d151013bb21f9b77feb58741c98', null, '2015-02-20 10:12:34', null, 'i_00000_102.jpg');

INSERT INTO image_info(id, url, storage_id, hash, uploaded_at, created_at, file_name)
  VALUES (5, 'files?id=a39442561f89a80761f42703d9a14534', 'a39442561f89a80761f42703d9a14534', 'a39442561f89a80761f42703d9a14534', '2015-03-20 11:23:42', '2015-03-20 08:12:52', '00001_701.jpg');

INSERT INTO image_info(id, url, storage_id, hash, uploaded_at, created_at, file_name)
  VALUES (6, 'files?id=59da3ab2ff216d80a1115fa20a63303c', '59da3ab2ff216d80a1115fa20a63303c', null, '2015-03-20 11:23:42', null, 'i_00001_701.jpg');

INSERT INTO image_info(id, url, storage_id, hash, uploaded_at, created_at, file_name)
  VALUES (7, 'files?id=5b691838047e3ebb8734e03406acfe16', '5b691838047e3ebb8734e03406acfe16', '5b691838047e3ebb8734e03406acfe16', '2015-03-20 11:24:22', '2015-03-20 08:13:32', '00002_084.jpg');

INSERT INTO image_info(id, url, storage_id, hash, uploaded_at, created_at, file_name)
  VALUES (8, 'files?id=48a353ac6e0fd41da118777a73eeb99c', '48a353ac6e0fd41da118777a73eeb99c', null, '2015-03-20 11:24:22', null, 'i_00002_084.jpg');



INSERT INTO indication(id, original_image_info_id, indication_image_info_id, created_at, meter_id, value, consumption)
  VALUES (1, 1, 2, '2015-02-20 09:12:34', 2, '0.087', 0);

INSERT INTO indication(id, original_image_info_id, indication_image_info_id, created_at, meter_id, value, consumption)
  VALUES (2, 3, 4, '2015-02-20 09:13:21', 3, '0.102', 0);

INSERT INTO indication(id, original_image_info_id, indication_image_info_id, created_at, meter_id, value, consumption)
  VALUES (3, 5, 6, '2015-03-20 08:12:52', 3, null, null);

INSERT INTO indication(id, original_image_info_id, indication_image_info_id, created_at, meter_id, value, consumption)
  VALUES (4, 7, 8, '2015-03-20 08:13:32', 2, '2.084', 2);

-- Adds the teacher homework menu beside the existing exam/training menus.
-- Run once after deploying the teacher frontend. Access is copied only to roles
-- that already have either sibling menu.
START TRANSACTION;

SET @homework_sibling_parent := (
  SELECT COALESCE(MIN(parent_id), 0)
  FROM crazy_menu
  WHERE name IN ('考试管理', '实训管理') AND category = 1 AND is_deleted = 0
);

INSERT INTO crazy_menu
  (parent_id, code, name, alias, path, source, sort, hidden, category, action, is_open, remark, is_deleted)
SELECT @homework_sibling_parent, 'homework_manage', '作业管理', '作业管理', '/labour/homework',
       'el-icon-edit-outline', 0, 0, 1, 0, 0, '教师作业管理菜单', 0
WHERE NOT EXISTS (
  SELECT 1 FROM crazy_menu WHERE code = 'homework_manage' AND is_deleted = 0
);

SET @homework_menu_id := (
  SELECT id FROM crazy_menu WHERE code = 'homework_manage' AND is_deleted = 0 LIMIT 1
);

INSERT INTO crazy_menu
  (parent_id, code, name, alias, path, source, sort, hidden, category, action, is_open, remark, is_deleted)
SELECT @homework_menu_id, 'homework_grade', '作业批改', '作业批改', '/labour/homeworkGrade',
       'el-icon-edit', 2, 0, 1, 0, 0, '按课程查看并批改章节作业', 0
WHERE NOT EXISTS (
  SELECT 1 FROM crazy_menu WHERE code = 'homework_grade' AND is_deleted = 0
);

INSERT INTO crazy_menu
  (parent_id, code, name, alias, path, source, sort, hidden, category, action, is_open, remark, is_deleted)
SELECT @homework_menu_id, 'homework_library', '作业库', '作业库', '/labour/homeworkLibrary',
       'el-icon-collection', 1, 0, 1, 0, 0, '查看课程章节作业库', 0
WHERE NOT EXISTS (
  SELECT 1 FROM crazy_menu WHERE code = 'homework_library' AND is_deleted = 0
);

INSERT INTO crazy_role_menu (role_id, menu_id)
SELECT DISTINCT source_roles.role_id, menu.id
FROM (
  SELECT role_menu.role_id
  FROM crazy_role_menu role_menu
  INNER JOIN crazy_menu sibling ON sibling.id = role_menu.menu_id
  WHERE sibling.name IN ('考试管理', '实训管理') AND sibling.category = 1 AND sibling.is_deleted = 0
) source_roles
INNER JOIN crazy_menu menu ON menu.code IN ('homework_manage', 'homework_grade', 'homework_library')
  AND menu.is_deleted = 0
LEFT JOIN crazy_role_menu existing ON existing.role_id = source_roles.role_id AND existing.menu_id = menu.id
WHERE existing.id IS NULL;

COMMIT;

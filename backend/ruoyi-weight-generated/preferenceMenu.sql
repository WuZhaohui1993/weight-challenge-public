-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置', '3', '1', 'preference', 'ruoyi-weight/preference/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:preference:list', '#', 'admin', sysdate(), '', null, '同步偏好设置菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('同步偏好设置导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:preference:export',       '#', 'admin', sysdate(), '', null, '');
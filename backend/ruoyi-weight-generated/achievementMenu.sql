-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章', '3', '1', 'achievement', 'ruoyi-weight/achievement/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:achievement:list', '#', 'admin', sysdate(), '', null, '成就徽章菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成就徽章导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:achievement:export',       '#', 'admin', sysdate(), '', null, '');
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主', '3', '1', 'circle', 'ruoyi-weight/circle/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:circle:list', '#', 'admin', sysdate(), '', null, '圈子主菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子主导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:circle:export',       '#', 'admin', sysdate(), '', null, '');
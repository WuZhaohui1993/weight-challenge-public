-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态', '3', '1', 'feed', 'ruoyi-weight/feed/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:feed:list', '#', 'admin', sysdate(), '', null, '圈子动态菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('圈子动态导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:feed:export',       '#', 'admin', sysdate(), '', null, '');
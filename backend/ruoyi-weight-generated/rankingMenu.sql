-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存', '3', '1', 'ranking', 'ruoyi-weight/ranking/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:ranking:list', '#', 'admin', sysdate(), '', null, '排行榜缓存菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('排行榜缓存导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:ranking:export',       '#', 'admin', sysdate(), '', null, '');
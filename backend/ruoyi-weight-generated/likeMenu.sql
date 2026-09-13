-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞', '3', '1', 'like', 'ruoyi-weight/like/index', 1, 0, 'C', '0', '0', 'ruoyi-weight:like:list', '#', 'admin', sysdate(), '', null, '评论点赞菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('评论点赞导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'ruoyi-weight:like:export',       '#', 'admin', sysdate(), '', null, '');
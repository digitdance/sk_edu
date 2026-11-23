-- =============================================================================
-- NovaCloudEdu 数据库完整初始化脚本（合并版）
-- 由 backend/sql/*.sql 按依赖顺序合并，供 Docker 首次初始化 PostgreSQL 使用
-- 已有数据库的结构升级请使用 Flyway: backend/src/main/resources/db/migration/
-- =============================================================================


-- >>> BEGIN: 00_init_extensions.sql <<<

-- 数据库初始化脚本
-- 注意：数据库 novacloudedu 由 Docker 环境变量 POSTGRES_DB 自动创建
-- 此脚本在 novacloudedu 数据库中自动执行

-- 启用 pgvector 扩展
CREATE EXTENSION IF NOT EXISTS vector;
-- >>> END: 00_init_extensions.sql <<<

-- >>> BEGIN: 10_user.sql <<<

-- 用户领域相关表

-- 用户表
CREATE TABLE IF NOT EXISTS "user"
(
    id            BIGSERIAL PRIMARY KEY,
    user_account  VARCHAR(256)                           NOT NULL,
    user_password VARCHAR(512)                           NOT NULL,
    user_gender   INT                                    NULL,
    user_phone    VARCHAR(256)                           NULL,
    level         INT                                    NOT NULL,
    user_name     VARCHAR(256)                           NULL,
    user_avatar   VARCHAR(1024)                          NULL,
    user_profile  VARCHAR(1024)                          NULL,
    user_role     VARCHAR(128) DEFAULT 'student'         NOT NULL,
    user_address  VARCHAR(1024)                          NULL,
    user_email    VARCHAR(512)                           NULL,
    birthday      TIMESTAMP                              NULL,
    create_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_ban        SMALLINT     DEFAULT 0                 NOT NULL,
    is_delete     SMALLINT     DEFAULT 0                 NOT NULL,
    CONSTRAINT uk_user_account UNIQUE (user_account)
);
CREATE INDEX IF NOT EXISTS idx_user_phone ON "user"(user_phone);
CREATE INDEX IF NOT EXISTS idx_user_email ON "user"(user_email);
CREATE INDEX IF NOT EXISTS idx_user_role ON "user"(user_role);
CREATE INDEX IF NOT EXISTS idx_user_display ON "user"(user_name, user_avatar, user_role);
COMMENT ON TABLE "user" IS '用户';
COMMENT ON COLUMN "user".id IS 'id';
COMMENT ON COLUMN "user".user_account IS '账号';
COMMENT ON COLUMN "user".user_password IS '密码';
COMMENT ON COLUMN "user".user_gender IS '性别 0-男 1-女 2-保密';
COMMENT ON COLUMN "user".user_phone IS '手机号';
COMMENT ON COLUMN "user".level IS '等级数值';
COMMENT ON COLUMN "user".user_name IS '用户昵称';
COMMENT ON COLUMN "user".user_avatar IS '用户头像';
COMMENT ON COLUMN "user".user_profile IS '用户简介';
COMMENT ON COLUMN "user".user_role IS '用户角色：student/teacher/admin';
COMMENT ON COLUMN "user".user_address IS '地址，JSON格式：{province,city,district}';
COMMENT ON COLUMN "user".user_email IS '邮箱';
COMMENT ON COLUMN "user".birthday IS '生日';
COMMENT ON COLUMN "user".create_time IS '创建时间';
COMMENT ON COLUMN "user".update_time IS '更新时间';
COMMENT ON COLUMN "user".is_ban IS '是否封禁,0-否,1-是';
COMMENT ON COLUMN "user".is_delete IS '是否删除,0-否,1-是';

-- 等级特权表
CREATE TABLE IF NOT EXISTS level_privilege
(
    id          BIGSERIAL PRIMARY KEY,
    level       INT                                NOT NULL,
    description VARCHAR(512)                       NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_lp_level ON level_privilege(level);
COMMENT ON TABLE level_privilege IS '等级特权';
COMMENT ON COLUMN level_privilege.id IS 'id';
COMMENT ON COLUMN level_privilege.level IS '等级';
COMMENT ON COLUMN level_privilege.description IS '特权描述';
COMMENT ON COLUMN level_privilege.create_time IS '创建时间';
COMMENT ON COLUMN level_privilege.update_time IS '更新时间';

-- 用户学习统计表（天）
CREATE TABLE IF NOT EXISTS user_learning_stats
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT                             NOT NULL,
    experience  INT      DEFAULT 0                 NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_uls_user_id ON user_learning_stats(user_id);
CREATE INDEX IF NOT EXISTS idx_uls_experience ON user_learning_stats(experience);
COMMENT ON TABLE user_learning_stats IS '用户学习统计';
COMMENT ON COLUMN user_learning_stats.id IS 'id';
COMMENT ON COLUMN user_learning_stats.user_id IS '用户id';
COMMENT ON COLUMN user_learning_stats.experience IS '当日经验值';
COMMENT ON COLUMN user_learning_stats.create_time IS '创建时间';
COMMENT ON COLUMN user_learning_stats.update_time IS '更新时间';

-- 用户每日学习目标表
CREATE TABLE IF NOT EXISTS user_daily_goal
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    content         TEXT                               NULL,
    is_completed    SMALLINT  DEFAULT 0                NOT NULL,
    completed_time  TIMESTAMP                          NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_udg_user_id ON user_daily_goal(user_id);
COMMENT ON TABLE user_daily_goal IS '用户每日学习目标';
COMMENT ON COLUMN user_daily_goal.id IS 'id';
COMMENT ON COLUMN user_daily_goal.user_id IS '用户id';
COMMENT ON COLUMN user_daily_goal.content IS '目标内容';
COMMENT ON COLUMN user_daily_goal.is_completed IS '是否全部完成：0-否，1-是';
COMMENT ON COLUMN user_daily_goal.completed_time IS '全部完成时间';
COMMENT ON COLUMN user_daily_goal.create_time IS '创建时间';
COMMENT ON COLUMN user_daily_goal.update_time IS '更新时间';

-- 用户课程表（学期配置）
CREATE TABLE IF NOT EXISTS user_course_table
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    table_name      VARCHAR(128)                       NOT NULL,
    semester        VARCHAR(64)                        NULL,
    start_date      DATE                               NOT NULL,
    total_weeks     INT         DEFAULT 20             NOT NULL,
    days_per_week   INT         DEFAULT 7              NOT NULL,
    sections_per_day INT        DEFAULT 12             NOT NULL,
    time_config     JSONB                              NULL,
    background_img  VARCHAR(1024)                      NULL,
    is_default      SMALLINT    DEFAULT 0              NOT NULL,
    create_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT    DEFAULT 0              NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_uct_user_id ON user_course_table(user_id);
CREATE INDEX IF NOT EXISTS idx_uct_is_default ON user_course_table(is_default);
COMMENT ON TABLE user_course_table IS '用户课程表（学期配置）';
COMMENT ON COLUMN user_course_table.id IS 'id';
COMMENT ON COLUMN user_course_table.user_id IS '用户id';
COMMENT ON COLUMN user_course_table.table_name IS '课程表名称';
COMMENT ON COLUMN user_course_table.semester IS '学期，如：2024-2025-1';
COMMENT ON COLUMN user_course_table.start_date IS '学期开始日期（第一周周一）';
COMMENT ON COLUMN user_course_table.total_weeks IS '总周数';
COMMENT ON COLUMN user_course_table.days_per_week IS '每周天数，默认7';
COMMENT ON COLUMN user_course_table.sections_per_day IS '每天节数';
COMMENT ON COLUMN user_course_table.time_config IS '时间配置JSON，如：[{"section":1,"start":"08:00","end":"08:45"},{"section":2,"start":"08:55","end":"09:40"}]';
COMMENT ON COLUMN user_course_table.background_img IS '背景图片URL';
COMMENT ON COLUMN user_course_table.is_default IS '是否默认课程表：0-否，1-是';
COMMENT ON COLUMN user_course_table.create_time IS '创建时间';
COMMENT ON COLUMN user_course_table.update_time IS '更新时间';
COMMENT ON COLUMN user_course_table.is_delete IS '是否删除：0-否，1-是';

-- 用户课程表项目（具体课程）
CREATE TABLE IF NOT EXISTS user_course_table_item
(
    id              BIGSERIAL PRIMARY KEY,
    table_id        BIGINT                             NOT NULL,
    course_name     VARCHAR(128)                       NOT NULL,
    teacher_name    VARCHAR(64)                        NULL,
    location        VARCHAR(128)                       NULL,
    day_of_week     SMALLINT                           NOT NULL,
    start_section   SMALLINT                           NOT NULL,
    end_section     SMALLINT                           NOT NULL,
    start_week      SMALLINT    DEFAULT 1              NOT NULL,
    end_week        SMALLINT    DEFAULT 20             NOT NULL,
    week_type       SMALLINT    DEFAULT 0              NOT NULL,
    color           VARCHAR(32)                        NULL,
    remark          VARCHAR(512)                       NULL,
    create_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT    DEFAULT 0              NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ucti_table_id ON user_course_table_item(table_id);
CREATE INDEX IF NOT EXISTS idx_ucti_day_section ON user_course_table_item(day_of_week, start_section);
COMMENT ON TABLE user_course_table_item IS '用户课程表项目（具体课程）';
COMMENT ON COLUMN user_course_table_item.id IS 'id';
COMMENT ON COLUMN user_course_table_item.table_id IS '课程表id';
COMMENT ON COLUMN user_course_table_item.course_name IS '课程名称';
COMMENT ON COLUMN user_course_table_item.teacher_name IS '教师姓名';
COMMENT ON COLUMN user_course_table_item.location IS '上课地点';
COMMENT ON COLUMN user_course_table_item.day_of_week IS '星期几：1-7对应周一到周日';
COMMENT ON COLUMN user_course_table_item.start_section IS '开始节次';
COMMENT ON COLUMN user_course_table_item.end_section IS '结束节次';
COMMENT ON COLUMN user_course_table_item.start_week IS '开始周';
COMMENT ON COLUMN user_course_table_item.end_week IS '结束周';
COMMENT ON COLUMN user_course_table_item.week_type IS '周类型：0-每周，1-单周，2-双周';
COMMENT ON COLUMN user_course_table_item.color IS '显示颜色，如：#FF5733';
COMMENT ON COLUMN user_course_table_item.remark IS '备注';
COMMENT ON COLUMN user_course_table_item.create_time IS '创建时间';
COMMENT ON COLUMN user_course_table_item.update_time IS '更新时间';
COMMENT ON COLUMN user_course_table_item.is_delete IS '是否删除：0-否，1-是';
-- >>> END: 10_user.sql <<<

-- >>> BEGIN: 11_user_follow.sql <<<

-- 用户关注表
CREATE TABLE IF NOT EXISTS user_follow (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_follower_following UNIQUE (follower_id, following_id)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_user_follow_follower_id ON user_follow (follower_id);
CREATE INDEX IF NOT EXISTS idx_user_follow_following_id ON user_follow (following_id);
CREATE INDEX IF NOT EXISTS idx_user_follow_create_time ON user_follow (create_time);

-- 注释
COMMENT ON TABLE user_follow IS '用户关注表';
COMMENT ON COLUMN user_follow.id IS '关注ID';
COMMENT ON COLUMN user_follow.follower_id IS '关注者ID（粉丝）';
COMMENT ON COLUMN user_follow.following_id IS '被关注者ID';
COMMENT ON COLUMN user_follow.create_time IS '关注时间';
-- >>> END: 11_user_follow.sql <<<

-- >>> BEGIN: 12_user_preference.sql <<<

-- 用户喜好分析相关表

-- 用户喜好表（记录用户对各类别/难度的偏好权重）
CREATE TABLE IF NOT EXISTS user_preference
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    preference_type VARCHAR(32)                        NOT NULL,
    preference_key  VARCHAR(128)                       NOT NULL,
    preference_value DECIMAL(10, 4) DEFAULT 0          NOT NULL,
    interaction_count INT DEFAULT 0                    NOT NULL,
    last_interaction_time TIMESTAMP                    NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_up_user_type_key ON user_preference(user_id, preference_type, preference_key);
CREATE INDEX IF NOT EXISTS idx_up_user_id ON user_preference(user_id);
CREATE INDEX IF NOT EXISTS idx_up_preference_type ON user_preference(preference_type);
COMMENT ON TABLE user_preference IS '用户喜好表';
COMMENT ON COLUMN user_preference.id IS 'id';
COMMENT ON COLUMN user_preference.user_id IS '用户id';
COMMENT ON COLUMN user_preference.preference_type IS '喜好类型：WORD_CATEGORY-单词分类，WORD_DIFFICULTY-单词难度，ARTICLE_CATEGORY-文章分类，ARTICLE_DIFFICULTY-文章难度，ARTICLE_TAG-文章标签';
COMMENT ON COLUMN user_preference.preference_key IS '喜好键（如分类名称、难度等级、标签名称）';
COMMENT ON COLUMN user_preference.preference_value IS '喜好权重值（0-100，值越大表示越喜欢）';
COMMENT ON COLUMN user_preference.interaction_count IS '交互次数（学习/阅读/收藏/点赞等行为的累计次数）';
COMMENT ON COLUMN user_preference.last_interaction_time IS '最后交互时间';
COMMENT ON COLUMN user_preference.create_time IS '创建时间';
COMMENT ON COLUMN user_preference.update_time IS '更新时间';

-- 用户行为日志表（记录用户的学习行为，用于喜好分析）
CREATE TABLE IF NOT EXISTS user_behavior_log
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    behavior_type   VARCHAR(32)                        NOT NULL,
    target_type     VARCHAR(32)                        NOT NULL,
    target_id       BIGINT                             NOT NULL,
    behavior_data   TEXT                               NULL,
    duration        INT DEFAULT 0                      NOT NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ubl_user_id ON user_behavior_log(user_id);
CREATE INDEX IF NOT EXISTS idx_ubl_behavior_type ON user_behavior_log(behavior_type);
CREATE INDEX IF NOT EXISTS idx_ubl_target ON user_behavior_log(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_ubl_create_time ON user_behavior_log(create_time);
COMMENT ON TABLE user_behavior_log IS '用户行为日志表';
COMMENT ON COLUMN user_behavior_log.id IS 'id';
COMMENT ON COLUMN user_behavior_log.user_id IS '用户id';
COMMENT ON COLUMN user_behavior_log.behavior_type IS '行为类型：STUDY-学习，READ-阅读，COLLECT-收藏，LIKE-点赞，SEARCH-搜索';
COMMENT ON COLUMN user_behavior_log.target_type IS '目标类型：WORD-单词，ARTICLE-文章';
COMMENT ON COLUMN user_behavior_log.target_id IS '目标id';
COMMENT ON COLUMN user_behavior_log.behavior_data IS '行为数据（JSON格式，存储额外信息如搜索关键词等）';
COMMENT ON COLUMN user_behavior_log.duration IS '持续时长（秒），如阅读时长';
COMMENT ON COLUMN user_behavior_log.create_time IS '创建时间';

-- 推荐记录表（记录推荐历史，避免重复推荐）
CREATE TABLE IF NOT EXISTS recommendation_history
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    target_type     VARCHAR(32)                        NOT NULL,
    target_id       BIGINT                             NOT NULL,
    recommendation_score DECIMAL(10, 4) DEFAULT 0      NOT NULL,
    recommendation_reason VARCHAR(256)                 NULL,
    is_clicked      SMALLINT DEFAULT 0                 NOT NULL,
    is_interacted   SMALLINT DEFAULT 0                 NOT NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_rh_user_id ON recommendation_history(user_id);
CREATE INDEX IF NOT EXISTS idx_rh_target ON recommendation_history(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_rh_create_time ON recommendation_history(create_time);
COMMENT ON TABLE recommendation_history IS '推荐记录表';
COMMENT ON COLUMN recommendation_history.id IS 'id';
COMMENT ON COLUMN recommendation_history.user_id IS '用户id';
COMMENT ON COLUMN recommendation_history.target_type IS '目标类型：WORD-单词，ARTICLE-文章';
COMMENT ON COLUMN recommendation_history.target_id IS '目标id';
COMMENT ON COLUMN recommendation_history.recommendation_score IS '推荐分数';
COMMENT ON COLUMN recommendation_history.recommendation_reason IS '推荐原因';
COMMENT ON COLUMN recommendation_history.is_clicked IS '是否点击：0-否，1-是';
COMMENT ON COLUMN recommendation_history.is_interacted IS '是否产生交互（学习/阅读完成）：0-否，1-是';
COMMENT ON COLUMN recommendation_history.create_time IS '创建时间';
COMMENT ON COLUMN recommendation_history.update_time IS '更新时间';
-- >>> END: 12_user_preference.sql <<<

-- >>> BEGIN: 20_social.sql <<<

-- 社交/私聊领域相关表

-- 私聊消息表
CREATE TABLE IF NOT EXISTS private_message
(
    id          BIGSERIAL PRIMARY KEY,
    sender_id   BIGINT                             NOT NULL,
    receiver_id BIGINT                             NOT NULL,
    content     TEXT                               NOT NULL,
    type        VARCHAR(50)                        NOT NULL,
    reply_to    BIGINT                             NULL,
    is_read     SMALLINT DEFAULT 0                 NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL
);
ALTER TABLE private_message ADD COLUMN IF NOT EXISTS reply_to BIGINT NULL;
CREATE INDEX IF NOT EXISTS idx_pm_sender_id ON private_message(sender_id);
CREATE INDEX IF NOT EXISTS idx_pm_receiver_id ON private_message(receiver_id);
CREATE INDEX IF NOT EXISTS idx_pm_reply_to ON private_message(reply_to);
CREATE INDEX IF NOT EXISTS idx_pm_create_time ON private_message(create_time);
COMMENT ON TABLE private_message IS '私聊消息';
COMMENT ON COLUMN private_message.id IS '消息ID';
COMMENT ON COLUMN private_message.sender_id IS '发送者ID，关联到user表';
COMMENT ON COLUMN private_message.receiver_id IS '接收者ID，关联到user表';
COMMENT ON COLUMN private_message.content IS '消息内容';
COMMENT ON COLUMN private_message.type IS '消息类型';
COMMENT ON COLUMN private_message.reply_to IS '回复的消息ID';
COMMENT ON COLUMN private_message.is_read IS '是否已读：0-否，1-是';
COMMENT ON COLUMN private_message.create_time IS '发送时间';
COMMENT ON COLUMN private_message.is_delete IS '是否删除：0-否，1-是';

-- 私聊会话表
CREATE TABLE IF NOT EXISTS private_chat_session
(
    id                BIGSERIAL PRIMARY KEY,
    user_id1          BIGINT                             NOT NULL,
    user_id2          BIGINT                             NOT NULL,
    last_message_time TIMESTAMP                          NULL,
    create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete         SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_pcs_user_id1 ON private_chat_session(user_id1);
CREATE INDEX IF NOT EXISTS idx_pcs_user_id2 ON private_chat_session(user_id2);
COMMENT ON TABLE private_chat_session IS '私聊会话';
COMMENT ON COLUMN private_chat_session.id IS '会话ID';
COMMENT ON COLUMN private_chat_session.user_id1 IS '用户1 ID，关联到user表';
COMMENT ON COLUMN private_chat_session.user_id2 IS '用户2 ID，关联到user表';
COMMENT ON COLUMN private_chat_session.last_message_time IS '最后一条消息时间';
COMMENT ON COLUMN private_chat_session.create_time IS '创建时间';
COMMENT ON COLUMN private_chat_session.update_time IS '更新时间';
COMMENT ON COLUMN private_chat_session.is_delete IS '是否删除：0-否，1-是';

-- 好友关系表
CREATE TABLE IF NOT EXISTS friend_relationship
(
    id          BIGSERIAL PRIMARY KEY,
    user_id1    BIGINT                             NOT NULL,
    user_id2    BIGINT                             NOT NULL,
    status      VARCHAR(20)                        NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_fr_user_id1 ON friend_relationship(user_id1);
CREATE INDEX IF NOT EXISTS idx_fr_user_id2 ON friend_relationship(user_id2);
COMMENT ON TABLE friend_relationship IS '好友关系表';
COMMENT ON COLUMN friend_relationship.id IS '主键';
COMMENT ON COLUMN friend_relationship.user_id1 IS '用户1 ID，关联到user表';
COMMENT ON COLUMN friend_relationship.user_id2 IS '用户2 ID，关联到user表';
COMMENT ON COLUMN friend_relationship.status IS '关系状态：pending/accepted/blocked';
COMMENT ON COLUMN friend_relationship.create_time IS '创建时间';
COMMENT ON COLUMN friend_relationship.update_time IS '更新时间';

-- 好友申请表
CREATE TABLE IF NOT EXISTS friend_request
(
    id          BIGSERIAL PRIMARY KEY,
    sender_id   BIGINT                             NOT NULL,
    receiver_id BIGINT                             NOT NULL,
    status      VARCHAR(20)                        NOT NULL,
    message     VARCHAR(512)                       NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_freq_sender_id ON friend_request(sender_id);
CREATE INDEX IF NOT EXISTS idx_freq_receiver_id ON friend_request(receiver_id);
COMMENT ON TABLE friend_request IS '好友申请表';
COMMENT ON COLUMN friend_request.id IS '主键';
COMMENT ON COLUMN friend_request.sender_id IS '发送者 ID，关联到user表';
COMMENT ON COLUMN friend_request.receiver_id IS '接收者 ID，关联到user表';
COMMENT ON COLUMN friend_request.status IS '申请状态：pending/accepted/rejected';
COMMENT ON COLUMN friend_request.message IS '申请消息';
COMMENT ON COLUMN friend_request.create_time IS '创建时间';
COMMENT ON COLUMN friend_request.update_time IS '更新时间';

-- ===================== 群聊相关表 =====================

-- 群聊信息表
CREATE TABLE IF NOT EXISTS chat_group
(
    id                BIGSERIAL PRIMARY KEY,
    group_number      VARCHAR(20)                        NULL,
    group_name        VARCHAR(128)                       NOT NULL,
    avatar            VARCHAR(1024)                      NULL,
    description       VARCHAR(512)                       NULL,
    owner_id          BIGINT                             NOT NULL,
    class_id          BIGINT                             NULL,
    max_members       INT         DEFAULT 200            NOT NULL,
    member_count      INT         DEFAULT 1              NOT NULL,
    invite_mode       SMALLINT    DEFAULT 0              NOT NULL,
    join_mode         SMALLINT    DEFAULT 0              NOT NULL,
    is_mute           SMALLINT    DEFAULT 0              NOT NULL,
    announcement      TEXT                               NULL,
    announcement_time TIMESTAMP                          NULL,
    create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete         SMALLINT  DEFAULT 0                NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_cg_owner_id ON chat_group(owner_id);
CREATE INDEX IF NOT EXISTS idx_cg_class_id ON chat_group(class_id);
CREATE INDEX IF NOT EXISTS idx_cg_create_time ON chat_group(create_time);
CREATE UNIQUE INDEX IF NOT EXISTS idx_cg_group_number ON chat_group(group_number);
COMMENT ON TABLE chat_group IS '群聊信息';
COMMENT ON COLUMN chat_group.id IS '群ID';
COMMENT ON COLUMN chat_group.group_number IS '群号（唯一标识，用于搜索和分享）';
COMMENT ON COLUMN chat_group.group_name IS '群名称';
COMMENT ON COLUMN chat_group.avatar IS '群头像URL';
COMMENT ON COLUMN chat_group.description IS '群描述';
COMMENT ON COLUMN chat_group.owner_id IS '群主ID，关联到user表';
COMMENT ON COLUMN chat_group.class_id IS '关联班级ID，关联到class_info表，可为空';
COMMENT ON COLUMN chat_group.max_members IS '最大成员数';
COMMENT ON COLUMN chat_group.member_count IS '当前成员数';
COMMENT ON COLUMN chat_group.invite_mode IS '邀请模式：0-所有人可邀请，1-仅管理员可邀请';
COMMENT ON COLUMN chat_group.join_mode IS '加入模式：0-自由加入，1-需审批，2-禁止加入';
COMMENT ON COLUMN chat_group.is_mute IS '是否全员禁言：0-否，1-是';
COMMENT ON COLUMN chat_group.announcement IS '群公告';
COMMENT ON COLUMN chat_group.announcement_time IS '公告发布时间';
COMMENT ON COLUMN chat_group.create_time IS '创建时间';
COMMENT ON COLUMN chat_group.update_time IS '更新时间';
COMMENT ON COLUMN chat_group.is_delete IS '是否删除：0-否，1-是';

-- 群成员表
CREATE TABLE IF NOT EXISTS chat_group_member
(
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT                             NOT NULL,
    member_type SMALLINT   DEFAULT 0               NOT NULL,
    user_id     BIGINT                             NULL,
    ai_role_id  BIGINT                             NULL,
    role        SMALLINT   DEFAULT 0               NOT NULL,
    nickname    VARCHAR(64)                        NULL,
    is_mute     SMALLINT   DEFAULT 0               NOT NULL,
    mute_until  TIMESTAMP                          NULL,
    join_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT  DEFAULT 0                NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_cgm_group_id ON chat_group_member(group_id);
CREATE INDEX IF NOT EXISTS idx_cgm_user_id ON chat_group_member(user_id);
CREATE INDEX IF NOT EXISTS idx_cgm_ai_role_id ON chat_group_member(ai_role_id);
COMMENT ON TABLE chat_group_member IS '群成员';
COMMENT ON COLUMN chat_group_member.id IS '主键';
COMMENT ON COLUMN chat_group_member.group_id IS '群ID，关联到chat_group表';
COMMENT ON COLUMN chat_group_member.member_type IS '成员类型：0-普通用户，1-AI角色';
COMMENT ON COLUMN chat_group_member.user_id IS '用户ID，关联到user表，member_type=0时有值';
COMMENT ON COLUMN chat_group_member.ai_role_id IS 'AI角色ID，关联到ai_role表，member_type=1时有值';
COMMENT ON COLUMN chat_group_member.role IS '角色：0-普通成员，1-管理员，2-群主';
COMMENT ON COLUMN chat_group_member.nickname IS '群昵称';
COMMENT ON COLUMN chat_group_member.is_mute IS '是否被禁言：0-否，1-是';
COMMENT ON COLUMN chat_group_member.mute_until IS '禁言截止时间';
COMMENT ON COLUMN chat_group_member.join_time IS '加入时间';
COMMENT ON COLUMN chat_group_member.update_time IS '更新时间';
COMMENT ON COLUMN chat_group_member.is_delete IS '是否删除（退出群）：0-否，1-是';

-- 群消息表
CREATE TABLE IF NOT EXISTS group_message
(
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT                             NOT NULL,
    sender_type SMALLINT   DEFAULT 0               NOT NULL,
    sender_id   BIGINT                             NULL,
    ai_role_id  BIGINT                             NULL,
    content     TEXT                               NOT NULL,
    type        VARCHAR(50)                        NOT NULL,
    reply_to    BIGINT                             NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT  DEFAULT 0                NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_gm_group_id ON group_message(group_id);
CREATE INDEX IF NOT EXISTS idx_gm_sender_id ON group_message(sender_id);
CREATE INDEX IF NOT EXISTS idx_gm_ai_role_id ON group_message(ai_role_id);
CREATE INDEX IF NOT EXISTS idx_gm_create_time ON group_message(create_time);
COMMENT ON TABLE group_message IS '群消息';
COMMENT ON COLUMN group_message.id IS '消息ID';
COMMENT ON COLUMN group_message.group_id IS '群ID，关联到chat_group表';
COMMENT ON COLUMN group_message.sender_type IS '发送者类型：0-普通用户，1-AI角色';
COMMENT ON COLUMN group_message.sender_id IS '发送者ID，关联到user表，sender_type=0时有值';
COMMENT ON COLUMN group_message.ai_role_id IS 'AI角色ID，关联到ai_role表，sender_type=1时有值';
COMMENT ON COLUMN group_message.content IS '消息内容';
COMMENT ON COLUMN group_message.type IS '消息类型：text/image/file/audio/video/system';
COMMENT ON COLUMN group_message.reply_to IS '回复的消息ID';
COMMENT ON COLUMN group_message.create_time IS '发送时间';
COMMENT ON COLUMN group_message.is_delete IS '是否删除：0-否，1-是';

-- 群消息已读记录表
CREATE TABLE IF NOT EXISTS group_message_read
(
    id          BIGSERIAL PRIMARY KEY,
    message_id  BIGINT                             NOT NULL,
    user_id     BIGINT                             NOT NULL,
    read_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_gmr_message_id ON group_message_read(message_id);
CREATE INDEX IF NOT EXISTS idx_gmr_user_id ON group_message_read(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_gmr_message_user ON group_message_read(message_id, user_id);
COMMENT ON TABLE group_message_read IS '群消息已读记录';
COMMENT ON COLUMN group_message_read.id IS '主键';
COMMENT ON COLUMN group_message_read.message_id IS '消息ID，关联到group_message表';
COMMENT ON COLUMN group_message_read.user_id IS '用户ID，关联到user表';
COMMENT ON COLUMN group_message_read.read_time IS '阅读时间';

-- 群申请表
CREATE TABLE IF NOT EXISTS group_join_request
(
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT                             NOT NULL,
    user_id     BIGINT                             NOT NULL,
    message     VARCHAR(512)                       NULL,
    status      SMALLINT   DEFAULT 0               NOT NULL,
    handler_id  BIGINT                             NULL,
    handle_time TIMESTAMP                          NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_gjr_group_id ON group_join_request(group_id);
CREATE INDEX IF NOT EXISTS idx_gjr_user_id ON group_join_request(user_id);
CREATE INDEX IF NOT EXISTS idx_gjr_status ON group_join_request(status);
COMMENT ON TABLE group_join_request IS '群申请';
COMMENT ON COLUMN group_join_request.id IS '主键';
COMMENT ON COLUMN group_join_request.group_id IS '群ID，关联到chat_group表';
COMMENT ON COLUMN group_join_request.user_id IS '申请人ID，关联到user表';
COMMENT ON COLUMN group_join_request.message IS '申请消息';
COMMENT ON COLUMN group_join_request.status IS '状态：0-待审批，1-已通过，2-已拒绝';
COMMENT ON COLUMN group_join_request.handler_id IS '处理人ID';
COMMENT ON COLUMN group_join_request.handle_time IS '处理时间';
COMMENT ON COLUMN group_join_request.create_time IS '申请时间';
COMMENT ON COLUMN group_join_request.update_time IS '更新时间';
-- >>> END: 20_social.sql <<<

-- >>> BEGIN: 30_course.sql <<<

-- 课程领域相关表

-- 课程表
CREATE TABLE IF NOT EXISTS course
(
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(256)                             NOT NULL,
    subtitle       VARCHAR(512)                             NULL,
    description    TEXT                                     NULL,
    cover_image    VARCHAR(1024)                            NULL,
    price          DECIMAL(10, 2) DEFAULT 0.00              NOT NULL,
    course_type    SMALLINT       DEFAULT 0                 NOT NULL,
    difficulty     SMALLINT       DEFAULT 1                 NOT NULL,
    status         SMALLINT       DEFAULT 0                 NOT NULL,
    teacher_id     BIGINT                                   NOT NULL,
    total_duration INT            DEFAULT 0                 NOT NULL,
    total_chapters INT            DEFAULT 0                 NOT NULL,
    total_sections INT            DEFAULT 0                 NOT NULL,
    student_count  INT            DEFAULT 0                 NOT NULL,
    rating_score   DECIMAL(2, 1)  DEFAULT 0.0               NOT NULL,
    tags           VARCHAR(512)                             NULL,
    admin_id       BIGINT                                   NOT NULL,
    create_time    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete      SMALLINT       DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_course_teacher_id ON course(teacher_id);
CREATE INDEX IF NOT EXISTS idx_course_type ON course(course_type);
CREATE INDEX IF NOT EXISTS idx_course_status ON course(status);
CREATE INDEX IF NOT EXISTS idx_course_difficulty ON course(difficulty);
COMMENT ON TABLE course IS '课程';
COMMENT ON COLUMN course.id IS 'id';
COMMENT ON COLUMN course.title IS '课程标题';
COMMENT ON COLUMN course.subtitle IS '课程副标题';
COMMENT ON COLUMN course.description IS '课程描述';
COMMENT ON COLUMN course.cover_image IS '封面图片URL';
COMMENT ON COLUMN course.price IS '课程价格';
COMMENT ON COLUMN course.course_type IS '课程类型：0-公开课，1-付费课，2-会员课';
COMMENT ON COLUMN course.difficulty IS '难度等级：1-入门，2-初级，3-中级，4-高级，5-专家';
COMMENT ON COLUMN course.status IS '状态：0-未发布，1-已发布，2-已下架';
COMMENT ON COLUMN course.teacher_id IS '讲师id';
COMMENT ON COLUMN course.total_duration IS '总时长(分钟)';
COMMENT ON COLUMN course.total_chapters IS '总章节数';
COMMENT ON COLUMN course.total_sections IS '总小节数';
COMMENT ON COLUMN course.student_count IS '学习人数';
COMMENT ON COLUMN course.rating_score IS '评分，1-5分';
COMMENT ON COLUMN course.tags IS '标签，JSON数组格式';
COMMENT ON COLUMN course.admin_id IS '创建管理员id';
COMMENT ON COLUMN course.create_time IS '创建时间';
COMMENT ON COLUMN course.update_time IS '更新时间';
COMMENT ON COLUMN course.is_delete IS '是否删除';

-- 讲师表
CREATE TABLE IF NOT EXISTS teacher
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(128)                       NOT NULL,
    introduction TEXT                               NULL,
    expertise    VARCHAR(512)                       NULL,
    user_id      BIGINT                             NULL,
    admin_id     BIGINT                             NOT NULL,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete    SMALLINT  DEFAULT 0                NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_teacher_user_id ON teacher(user_id);
COMMENT ON TABLE teacher IS '讲师';
COMMENT ON COLUMN teacher.id IS 'id';
COMMENT ON COLUMN teacher.name IS '讲师姓名';
COMMENT ON COLUMN teacher.introduction IS '讲师简介';
COMMENT ON COLUMN teacher.expertise IS '专业领域，JSON数组格式';
COMMENT ON COLUMN teacher.user_id IS '关联的用户id，讲师也是平台用户';
COMMENT ON COLUMN teacher.admin_id IS '创建管理员id';
COMMENT ON COLUMN teacher.create_time IS '创建时间';
COMMENT ON COLUMN teacher.update_time IS '更新时间';
COMMENT ON COLUMN teacher.is_delete IS '是否删除';

-- 讲师申请表
CREATE TABLE IF NOT EXISTS teacher_application
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    name            VARCHAR(128)                       NOT NULL,
    introduction    TEXT                               NULL,
    expertise       VARCHAR(512)                       NULL,
    certificate_url VARCHAR(1024)                      NULL,
    status          SMALLINT  DEFAULT 0                NOT NULL,
    reject_reason   TEXT                               NULL,
    reviewer_id     BIGINT                             NULL,
    review_time     TIMESTAMP                          NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT  DEFAULT 0                NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ta_user_id ON teacher_application(user_id);
CREATE INDEX IF NOT EXISTS idx_ta_status ON teacher_application(status);
COMMENT ON TABLE teacher_application IS '讲师申请';
COMMENT ON COLUMN teacher_application.id IS 'id';
COMMENT ON COLUMN teacher_application.user_id IS '申请用户id';
COMMENT ON COLUMN teacher_application.name IS '讲师姓名';
COMMENT ON COLUMN teacher_application.introduction IS '讲师简介';
COMMENT ON COLUMN teacher_application.expertise IS '专业领域，JSON数组格式';
COMMENT ON COLUMN teacher_application.certificate_url IS '资质证书URL';
COMMENT ON COLUMN teacher_application.status IS '状态：0-待审核，1-已通过，2-已拒绝';
COMMENT ON COLUMN teacher_application.reject_reason IS '拒绝原因';
COMMENT ON COLUMN teacher_application.reviewer_id IS '审核人id';
COMMENT ON COLUMN teacher_application.review_time IS '审核时间';
COMMENT ON COLUMN teacher_application.create_time IS '创建时间';
COMMENT ON COLUMN teacher_application.update_time IS '更新时间';
COMMENT ON COLUMN teacher_application.is_delete IS '是否删除';

-- 课程章节表
CREATE TABLE IF NOT EXISTS course_chapter
(
    id          BIGSERIAL PRIMARY KEY,
    course_id   BIGINT                             NOT NULL,
    title       VARCHAR(256)                       NOT NULL,
    description TEXT                               NULL,
    sort        INT      DEFAULT 0                 NOT NULL,
    admin_id    BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_cchap_course_id ON course_chapter(course_id);
CREATE INDEX IF NOT EXISTS idx_cchap_sort ON course_chapter(sort);
COMMENT ON TABLE course_chapter IS '课程章节';
COMMENT ON COLUMN course_chapter.id IS 'id';
COMMENT ON COLUMN course_chapter.course_id IS '课程id';
COMMENT ON COLUMN course_chapter.title IS '章节标题';
COMMENT ON COLUMN course_chapter.description IS '章节描述';
COMMENT ON COLUMN course_chapter.sort IS '排序，数字越小排序越靠前';
COMMENT ON COLUMN course_chapter.admin_id IS '创建管理员id';
COMMENT ON COLUMN course_chapter.create_time IS '创建时间';
COMMENT ON COLUMN course_chapter.update_time IS '更新时间';
COMMENT ON COLUMN course_chapter.is_delete IS '是否删除';

-- 课程小节表
CREATE TABLE IF NOT EXISTS course_section
(
    id           BIGSERIAL PRIMARY KEY,
    course_id    BIGINT                             NOT NULL,
    chapter_id   BIGINT                             NOT NULL,
    title        VARCHAR(256)                       NOT NULL,
    description  TEXT                               NULL,
    video_url    VARCHAR(1024)                      NULL,
    hls_url      VARCHAR(1024)                      NULL,
    encryption_key_id VARCHAR(64)                   NULL,
    transcode_status SMALLINT DEFAULT 0             NOT NULL,
    duration     INT      DEFAULT 0                 NOT NULL,
    sort         INT      DEFAULT 0                 NOT NULL,
    is_free      SMALLINT DEFAULT 0                 NOT NULL,
    resource_url VARCHAR(1024)                      NULL,
    thumbnail_url VARCHAR(1024)                     NULL,
    thumbnail_count INTEGER DEFAULT 0               NULL,
    admin_id     BIGINT                             NOT NULL,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete    SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_csec_course_id ON course_section(course_id);
CREATE INDEX IF NOT EXISTS idx_csec_chapter_id ON course_section(chapter_id);
CREATE INDEX IF NOT EXISTS idx_csec_sort ON course_section(sort);
COMMENT ON TABLE course_section IS '课程小节';
COMMENT ON COLUMN course_section.id IS 'id';
COMMENT ON COLUMN course_section.course_id IS '课程id';
COMMENT ON COLUMN course_section.chapter_id IS '章节id';
COMMENT ON COLUMN course_section.title IS '小节标题';
COMMENT ON COLUMN course_section.description IS '小节描述';
COMMENT ON COLUMN course_section.video_url IS '视频URL';
COMMENT ON COLUMN course_section.hls_url IS 'HLS播放地址(m3u8)';
COMMENT ON COLUMN course_section.encryption_key_id IS 'AES-128加密密钥ID';
COMMENT ON COLUMN course_section.transcode_status IS '转码状态: 0-未转码, 1-转码中, 2-已完成, 3-失败';
COMMENT ON COLUMN course_section.duration IS '时长(秒)';
COMMENT ON COLUMN course_section.sort IS '排序，数字越小排序越靠前';
COMMENT ON COLUMN course_section.is_free IS '是否免费：0-否，1-是';
COMMENT ON COLUMN course_section.resource_url IS '资源URL';
COMMENT ON COLUMN course_section.thumbnail_url IS '视频缩略图雪碧图URL';
COMMENT ON COLUMN course_section.thumbnail_count IS '缩略图数量';
COMMENT ON COLUMN course_section.admin_id IS '创建管理员id';
COMMENT ON COLUMN course_section.create_time IS '创建时间';
COMMENT ON COLUMN course_section.update_time IS '更新时间';
COMMENT ON COLUMN course_section.is_delete IS '是否删除';

-- 用户课程购买记录表
CREATE TABLE IF NOT EXISTS user_course
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT                             NOT NULL,
    course_id      BIGINT                             NOT NULL,
    order_no       VARCHAR(64)                        NULL,
    price          DECIMAL(10, 2)                     NOT NULL,
    payment_method VARCHAR(32)                        NULL,
    payment_time   TIMESTAMP                          NULL,
    expire_time    TIMESTAMP                          NULL,
    status         SMALLINT DEFAULT 1                 NOT NULL,
    create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete      SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_uc_user_id ON user_course(user_id);
CREATE INDEX IF NOT EXISTS idx_uc_course_id ON user_course(course_id);
CREATE INDEX IF NOT EXISTS idx_uc_order_no ON user_course(order_no);
COMMENT ON TABLE user_course IS '用户课程购买记录';
COMMENT ON COLUMN user_course.id IS 'id';
COMMENT ON COLUMN user_course.user_id IS '用户id';
COMMENT ON COLUMN user_course.course_id IS '课程id';
COMMENT ON COLUMN user_course.order_no IS '订单编号';
COMMENT ON COLUMN user_course.price IS '购买价格';
COMMENT ON COLUMN user_course.payment_method IS '支付方式';
COMMENT ON COLUMN user_course.payment_time IS '支付时间';
COMMENT ON COLUMN user_course.expire_time IS '过期时间，null表示永久有效';
COMMENT ON COLUMN user_course.status IS '状态：0-未支付，1-已支付，2-已过期，3-已退款';
COMMENT ON COLUMN user_course.create_time IS '创建时间';
COMMENT ON COLUMN user_course.update_time IS '更新时间';
COMMENT ON COLUMN user_course.is_delete IS '是否删除';

-- 用户学习进度表
CREATE TABLE IF NOT EXISTS user_course_progress
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT                             NOT NULL,
    course_id      BIGINT                             NOT NULL,
    section_id     BIGINT                             NOT NULL,
    progress       INT      DEFAULT 0                 NOT NULL,
    watch_duration INT      DEFAULT 0                 NOT NULL,
    last_position  INT      DEFAULT 0                 NOT NULL,
    is_completed   SMALLINT DEFAULT 0                 NOT NULL,
    completed_time TIMESTAMP                          NULL,
    create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ucp_user_id ON user_course_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_ucp_course_id ON user_course_progress(course_id);
CREATE INDEX IF NOT EXISTS idx_ucp_section_id ON user_course_progress(section_id);
COMMENT ON TABLE user_course_progress IS '用户学习进度';
COMMENT ON COLUMN user_course_progress.id IS 'id';
COMMENT ON COLUMN user_course_progress.user_id IS '用户id';
COMMENT ON COLUMN user_course_progress.course_id IS '课程id';
COMMENT ON COLUMN user_course_progress.section_id IS '小节id';
COMMENT ON COLUMN user_course_progress.progress IS '学习进度(百分比)';
COMMENT ON COLUMN user_course_progress.watch_duration IS '观看时长(秒)';
COMMENT ON COLUMN user_course_progress.last_position IS '上次观看位置(秒)';
COMMENT ON COLUMN user_course_progress.is_completed IS '是否完成：0-否，1-是';
COMMENT ON COLUMN user_course_progress.completed_time IS '完成时间';
COMMENT ON COLUMN user_course_progress.create_time IS '创建时间';
COMMENT ON COLUMN user_course_progress.update_time IS '更新时间';

-- 课程评价表
CREATE TABLE IF NOT EXISTS course_review
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT                             NOT NULL,
    course_id   BIGINT                             NOT NULL,
    rating      SMALLINT DEFAULT 5                 NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_cr_user_id ON course_review(user_id);
CREATE INDEX IF NOT EXISTS idx_cr_course_id ON course_review(course_id);
CREATE INDEX IF NOT EXISTS idx_cr_rating ON course_review(rating);
COMMENT ON TABLE course_review IS '课程评价';
COMMENT ON COLUMN course_review.id IS 'id';
COMMENT ON COLUMN course_review.user_id IS '用户id';
COMMENT ON COLUMN course_review.course_id IS '课程id';
COMMENT ON COLUMN course_review.rating IS '评分(1-5分)';
COMMENT ON COLUMN course_review.create_time IS '创建时间';
COMMENT ON COLUMN course_review.update_time IS '更新时间';
COMMENT ON COLUMN course_review.is_delete IS '是否删除';

-- 课程收藏表
CREATE TABLE IF NOT EXISTS course_favourite
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT                             NOT NULL,
    course_id   BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_cf_user_id ON course_favourite(user_id);
CREATE INDEX IF NOT EXISTS idx_cf_course_id ON course_favourite(course_id);
COMMENT ON TABLE course_favourite IS '课程收藏';
COMMENT ON COLUMN course_favourite.id IS 'id';
COMMENT ON COLUMN course_favourite.user_id IS '用户id';
COMMENT ON COLUMN course_favourite.course_id IS '课程id';
COMMENT ON COLUMN course_favourite.create_time IS '创建时间';
COMMENT ON COLUMN course_favourite.update_time IS '更新时间';
-- >>> END: 30_course.sql <<<

-- >>> BEGIN: 31_class.sql <<<

-- 班级领域相关表

-- 班级信息表
CREATE TABLE IF NOT EXISTS class_info
(
    id          BIGSERIAL PRIMARY KEY,
    class_name  VARCHAR(256)                       NOT NULL,
    description TEXT                               NULL,
    creator_id  BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ci_creator_id ON class_info(creator_id);
COMMENT ON TABLE class_info IS '班级信息';
COMMENT ON COLUMN class_info.id IS '班级ID';
COMMENT ON COLUMN class_info.class_name IS '班级名称';
COMMENT ON COLUMN class_info.description IS '班级描述';
COMMENT ON COLUMN class_info.creator_id IS '创建者ID，关联到user表';
COMMENT ON COLUMN class_info.create_time IS '创建时间';
COMMENT ON COLUMN class_info.update_time IS '更新时间';
COMMENT ON COLUMN class_info.is_delete IS '是否删除：0-否，1-是';

-- 班级成员表
CREATE TABLE IF NOT EXISTS class_member
(
    id        BIGSERIAL PRIMARY KEY,
    class_id  BIGINT                             NOT NULL,
    user_id   BIGINT                             NOT NULL,
    role      VARCHAR(32)                        NOT NULL,
    join_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_cm_class_id ON class_member(class_id);
CREATE INDEX IF NOT EXISTS idx_cm_user_id ON class_member(user_id);
COMMENT ON TABLE class_member IS '班级成员';
COMMENT ON COLUMN class_member.id IS '成员ID';
COMMENT ON COLUMN class_member.class_id IS '班级ID，关联到class_info表';
COMMENT ON COLUMN class_member.user_id IS '用户ID，关联到user表';
COMMENT ON COLUMN class_member.role IS '角色，如teacher/student';
COMMENT ON COLUMN class_member.join_time IS '加入时间';
COMMENT ON COLUMN class_member.is_delete IS '是否删除：0-否，1-是';

-- 班级与课程关联表
CREATE TABLE IF NOT EXISTS class_course
(
    id          BIGSERIAL PRIMARY KEY,
    class_id    BIGINT                             NOT NULL,
    course_id   BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_cc_class_id ON class_course(class_id);
CREATE INDEX IF NOT EXISTS idx_cc_course_id ON class_course(course_id);
COMMENT ON TABLE class_course IS '班级与课程关联';
COMMENT ON COLUMN class_course.id IS '关联ID';
COMMENT ON COLUMN class_course.class_id IS '班级ID，关联到class_info表';
COMMENT ON COLUMN class_course.course_id IS '课程ID，关联到course表';
COMMENT ON COLUMN class_course.create_time IS '创建时间';
COMMENT ON COLUMN class_course.update_time IS '更新时间';
COMMENT ON COLUMN class_course.is_delete IS '是否删除：0-否，1-是';
-- >>> END: 31_class.sql <<<

-- >>> BEGIN: 40_book.sql <<<

-- 电子阅览系统相关表

-- 书籍表
CREATE TABLE IF NOT EXISTS book
(
    id                BIGSERIAL PRIMARY KEY,
    title             VARCHAR(255)                             NOT NULL,
    author            VARCHAR(100)                             NULL,
    cover_url         VARCHAR(500)                             NULL,
    origin_file_url   VARCHAR(500)                             NOT NULL,
    file_type         VARCHAR(20)                              NOT NULL,
    status            SMALLINT       DEFAULT 0                 NOT NULL,
    total_chapters    INT            DEFAULT 0                 NOT NULL,
    word_count        INT            DEFAULT 0                 NOT NULL,
    file_size         BIGINT         DEFAULT 0                 NOT NULL,
    search_vector     tsvector                                NULL,
    admin_id          BIGINT                                   NOT NULL,
    create_time       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete         SMALLINT       DEFAULT 0                 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_book_status ON book(status);
CREATE INDEX IF NOT EXISTS idx_book_file_type ON book(file_type);
CREATE INDEX IF NOT EXISTS idx_book_admin_id ON book(admin_id);
CREATE INDEX IF NOT EXISTS idx_book_create_time ON book(create_time);

COMMENT ON TABLE book IS '书籍表';
COMMENT ON COLUMN book.id IS '书籍ID';
COMMENT ON COLUMN book.title IS '书名';
COMMENT ON COLUMN book.author IS '作者';
COMMENT ON COLUMN book.cover_url IS '封面图片URL';
COMMENT ON COLUMN book.origin_file_url IS '原始文件URL（OSS地址）';
COMMENT ON COLUMN book.file_type IS '文件类型：EPUB, DOCX, TXT, PDF';
COMMENT ON COLUMN book.status IS '状态：0-已上传，1-解析中，2-就绪，3-解析失败';
COMMENT ON COLUMN book.total_chapters IS '总章节数';
COMMENT ON COLUMN book.word_count IS '总字数';
COMMENT ON COLUMN book.file_size IS '文件大小（字节）';
COMMENT ON COLUMN book.admin_id IS '上传管理员ID';
COMMENT ON COLUMN book.create_time IS '创建时间';
COMMENT ON COLUMN book.update_time IS '更新时间';
COMMENT ON COLUMN book.is_delete IS '是否删除：0-否，1-是';


-- 章节表
CREATE TABLE IF NOT EXISTS chapter
(
    id              BIGSERIAL PRIMARY KEY,
    book_id         BIGINT                                   NOT NULL,
    title           VARCHAR(255)                             NOT NULL,
    chapter_index   INT                                      NOT NULL,
    word_count      INT            DEFAULT 0                 NOT NULL,
    content         TEXT                                     NOT NULL,
    content_hash    VARCHAR(64)                              NULL,
    encryption_iv   VARCHAR(64)                              NULL,
    content_vector  vector(1536)                             NULL,
    vector_model    VARCHAR(100)                             NULL,
    vectorized_at   TIMESTAMP                                NULL,
    content_search_vector tsvector                           NULL,
    create_time     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT       DEFAULT 0                 NOT NULL,
    CONSTRAINT uk_chapter_book_index UNIQUE (book_id, chapter_index)
);

CREATE INDEX IF NOT EXISTS idx_chapter_book_id ON chapter(book_id);
CREATE INDEX IF NOT EXISTS idx_chapter_book_index ON chapter(book_id, chapter_index);

COMMENT ON TABLE chapter IS '章节表';
COMMENT ON COLUMN chapter.id IS '章节ID';
COMMENT ON COLUMN chapter.book_id IS '所属书籍ID';
COMMENT ON COLUMN chapter.title IS '章节标题';
COMMENT ON COLUMN chapter.chapter_index IS '章节序号（从0开始）';
COMMENT ON COLUMN chapter.word_count IS '章节字数';
COMMENT ON COLUMN chapter.content IS '章节内容（清洗后的HTML）';
COMMENT ON COLUMN chapter.content_hash IS '内容哈希值（用于去重和版本控制）';
COMMENT ON COLUMN chapter.encryption_iv IS '加密IV（Base64编码，用于AES解密）';
COMMENT ON COLUMN chapter.create_time IS '创建时间';
COMMENT ON COLUMN chapter.update_time IS '更新时间';
COMMENT ON COLUMN chapter.is_delete IS '是否删除：0-否，1-是';


-- 用户书架表
CREATE TABLE IF NOT EXISTS user_book_shelf
(
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT                                   NOT NULL,
    book_id             BIGINT                                   NOT NULL,
    last_chapter_index  INT            DEFAULT 0                 NOT NULL,
    last_position       INT            DEFAULT 0                 NOT NULL,
    reading_progress    DECIMAL(5, 2)  DEFAULT 0.00              NOT NULL,
    added_time          TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_read_time      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete           SMALLINT       DEFAULT 0                 NOT NULL,
    CONSTRAINT uk_user_book UNIQUE (user_id, book_id)
);

CREATE INDEX IF NOT EXISTS idx_shelf_user_id ON user_book_shelf(user_id);
CREATE INDEX IF NOT EXISTS idx_shelf_book_id ON user_book_shelf(book_id);
CREATE INDEX IF NOT EXISTS idx_shelf_last_read ON user_book_shelf(user_id, last_read_time);

COMMENT ON TABLE user_book_shelf IS '用户书架表';
COMMENT ON COLUMN user_book_shelf.id IS '书架记录ID';
COMMENT ON COLUMN user_book_shelf.user_id IS '用户ID';
COMMENT ON COLUMN user_book_shelf.book_id IS '书籍ID';
COMMENT ON COLUMN user_book_shelf.last_chapter_index IS '最后阅读章节序号';
COMMENT ON COLUMN user_book_shelf.last_position IS '章节内阅读位置（字符偏移量）';
COMMENT ON COLUMN user_book_shelf.reading_progress IS '阅读进度百分比（0.00-100.00）';
COMMENT ON COLUMN user_book_shelf.added_time IS '添加到书架时间';
COMMENT ON COLUMN user_book_shelf.last_read_time IS '最后阅读时间';
COMMENT ON COLUMN user_book_shelf.is_delete IS '是否删除：0-否，1-是';


-- 书籍标签表
CREATE TABLE IF NOT EXISTS book_tag
(
    id          BIGSERIAL PRIMARY KEY,
    book_id     BIGINT                                   NOT NULL,
    tag_name    VARCHAR(50)                              NOT NULL,
    create_time TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_book_tag_book_id ON book_tag(book_id);
CREATE INDEX IF NOT EXISTS idx_book_tag_name ON book_tag(tag_name);

COMMENT ON TABLE book_tag IS '书籍标签表';
COMMENT ON COLUMN book_tag.id IS '标签ID';
COMMENT ON COLUMN book_tag.book_id IS '书籍ID';
COMMENT ON COLUMN book_tag.tag_name IS '标签名称';
COMMENT ON COLUMN book_tag.create_time IS '创建时间';


-- 阅读笔记表
CREATE TABLE IF NOT EXISTS reading_note
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                                   NOT NULL,
    book_id         BIGINT                                   NOT NULL,
    chapter_id      BIGINT                                   NOT NULL,
    chapter_index   INT                                      NULL,
    note_content    TEXT                                     NOT NULL,
    selected_text   TEXT                                     NULL,
    position_start  INT                                      NULL,
    position_end    INT                                      NULL,
    note_color      VARCHAR(20)   DEFAULT '#FFEB3B'          NULL,
    create_time     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT       DEFAULT 0                 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_note_user_book ON reading_note(user_id, book_id);
CREATE INDEX IF NOT EXISTS idx_note_chapter ON reading_note(chapter_id);

COMMENT ON TABLE reading_note IS '阅读笔记表';
COMMENT ON COLUMN reading_note.id IS '笔记ID';
COMMENT ON COLUMN reading_note.user_id IS '用户ID';
COMMENT ON COLUMN reading_note.book_id IS '书籍ID';
COMMENT ON COLUMN reading_note.chapter_id IS '章节ID';
COMMENT ON COLUMN reading_note.note_content IS '笔记内容';
COMMENT ON COLUMN reading_note.selected_text IS '选中的文本';
COMMENT ON COLUMN reading_note.position_start IS '选中文本起始位置';
COMMENT ON COLUMN reading_note.position_end IS '选中文本结束位置';
COMMENT ON COLUMN reading_note.create_time IS '创建时间';
COMMENT ON COLUMN reading_note.update_time IS '更新时间';
COMMENT ON COLUMN reading_note.is_delete IS '是否删除：0-否，1-是';


-- 书签表
CREATE TABLE IF NOT EXISTS reading_bookmark
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                                   NOT NULL,
    book_id         BIGINT                                   NOT NULL,
    chapter_id      BIGINT                                   NOT NULL,
    chapter_index   INT                                      NOT NULL,
    position        INT            DEFAULT 0                 NOT NULL,
    bookmark_name   VARCHAR(100)                             NULL,
    note            TEXT                                     NULL,
    create_time     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT       DEFAULT 0                 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_bookmark_user_book ON reading_bookmark(user_id, book_id);
CREATE INDEX IF NOT EXISTS idx_bookmark_chapter ON reading_bookmark(chapter_id);

COMMENT ON TABLE reading_bookmark IS '阅读书签表';
COMMENT ON COLUMN reading_bookmark.id IS '书签ID';
COMMENT ON COLUMN reading_bookmark.user_id IS '用户ID';
COMMENT ON COLUMN reading_bookmark.book_id IS '书籍ID';
COMMENT ON COLUMN reading_bookmark.chapter_id IS '章节ID';
COMMENT ON COLUMN reading_bookmark.chapter_index IS '章节序号';
COMMENT ON COLUMN reading_bookmark.position IS '书签位置（字符偏移量）';
COMMENT ON COLUMN reading_bookmark.bookmark_name IS '书签名称';
COMMENT ON COLUMN reading_bookmark.create_time IS '创建时间';
COMMENT ON COLUMN reading_bookmark.is_delete IS '是否删除：0-否，1-是';


-- ========================================
-- 向量化和全文搜索支持
-- ========================================

-- 添加 pgvector 扩展(用于向量相似度搜索)
CREATE EXTENSION IF NOT EXISTS vector;

-- 创建向量索引(使用 HNSW 算法,适合大规模向量检索)
CREATE INDEX IF NOT EXISTS idx_chapter_content_vector 
ON chapter USING hnsw (content_vector vector_cosine_ops);

-- 创建全文搜索索引
CREATE INDEX IF NOT EXISTS idx_book_search_vector 
ON book USING gin(search_vector);

-- 创建触发器函数,自动更新全文搜索向量
CREATE OR REPLACE FUNCTION update_book_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector := 
        setweight(to_tsvector('simple', COALESCE(NEW.title, '')), 'A') ||
        setweight(to_tsvector('simple', COALESCE(NEW.author, '')), 'B');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器
DROP TRIGGER IF EXISTS trigger_update_book_search_vector ON book;
CREATE TRIGGER trigger_update_book_search_vector
    BEFORE INSERT OR UPDATE ON book
    FOR EACH ROW
    EXECUTE FUNCTION update_book_search_vector();

-- 为现有数据更新全文搜索向量
UPDATE book SET search_vector = 
    setweight(to_tsvector('simple', COALESCE(title, '')), 'A') ||
    setweight(to_tsvector('simple', COALESCE(author, '')), 'B')
WHERE search_vector IS NULL;

-- 创建章节内容全文搜索索引
CREATE INDEX IF NOT EXISTS idx_chapter_content_search_vector 
ON chapter USING gin(content_search_vector);

-- 为超长章节内容构建可安全写入的全文搜索向量
CREATE OR REPLACE FUNCTION build_safe_chapter_search_vector(
    chapter_title TEXT,
    chapter_content TEXT
)
RETURNS tsvector AS $$
DECLARE
    normalized_content TEXT := trim(regexp_replace(
        regexp_replace(COALESCE(chapter_content, ''), '<[^>]+>', ' ', 'g'),
        '\s+',
        ' ',
        'g'
    ));
    title_vector tsvector := setweight(to_tsvector('simple', COALESCE(chapter_title, '')), 'A');
    content_length INT := char_length(normalized_content);
BEGIN
    IF normalized_content = '' THEN
        RETURN title_vector;
    END IF;

    LOOP
        BEGIN
            RETURN title_vector ||
                setweight(strip(to_tsvector('simple', left(normalized_content, content_length))), 'B');
        EXCEPTION
            WHEN SQLSTATE '54000' THEN
                IF content_length <= 1024 THEN
                    RETURN title_vector;
                END IF;
                content_length := GREATEST(content_length / 2, 1024);
        END;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- 创建章节全文搜索触发器函数
CREATE OR REPLACE FUNCTION update_chapter_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    -- 只索引纯文本内容,去除HTML标签
    NEW.content_search_vector := build_safe_chapter_search_vector(NEW.title, NEW.content);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建章节触发器
DROP TRIGGER IF EXISTS trigger_update_chapter_search_vector ON chapter;
CREATE TRIGGER trigger_update_chapter_search_vector
    BEFORE INSERT OR UPDATE ON chapter
    FOR EACH ROW
    EXECUTE FUNCTION update_chapter_search_vector();

-- 添加向量搜索辅助函数
CREATE OR REPLACE FUNCTION search_similar_chapters(
    query_vector vector(1536),
    similarity_threshold FLOAT DEFAULT 0.7,
    result_limit INT DEFAULT 10
)
RETURNS TABLE (
    chapter_id BIGINT,
    book_id BIGINT,
    title VARCHAR,
    similarity FLOAT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        c.id,
        c.book_id,
        c.title,
        1 - (c.content_vector <=> query_vector) as similarity
    FROM chapter c
    WHERE c.content_vector IS NOT NULL
        AND 1 - (c.content_vector <=> query_vector) >= similarity_threshold
        AND c.is_delete = 0
    ORDER BY c.content_vector <=> query_vector
    LIMIT result_limit;
END;
$$ LANGUAGE plpgsql;

-- 添加全文搜索辅助函数
CREATE OR REPLACE FUNCTION search_books_fulltext(
    search_query TEXT,
    result_limit INT DEFAULT 20
)
RETURNS TABLE (
    book_id BIGINT,
    title VARCHAR,
    author VARCHAR,
    rank FLOAT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        b.id,
        b.title,
        b.author,
        ts_rank(b.search_vector, to_tsquery('simple', search_query)) as rank
    FROM book b
    WHERE b.search_vector @@ to_tsquery('simple', search_query)
        AND b.is_delete = 0
    ORDER BY rank DESC
    LIMIT result_limit;
END;
$$ LANGUAGE plpgsql;

-- 添加章节内容全文搜索函数
CREATE OR REPLACE FUNCTION search_chapters_fulltext(
    search_query TEXT,
    book_id_filter BIGINT DEFAULT NULL,
    result_limit INT DEFAULT 50
)
RETURNS TABLE (
    chapter_id BIGINT,
    book_id BIGINT,
    title VARCHAR,
    chapter_index INT,
    rank FLOAT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        c.id,
        c.book_id,
        c.title,
        c.chapter_index,
        ts_rank(c.content_search_vector, to_tsquery('simple', search_query)) as rank
    FROM chapter c
    WHERE c.content_search_vector @@ to_tsquery('simple', search_query)
        AND c.is_delete = 0
        AND (book_id_filter IS NULL OR c.book_id = book_id_filter)
    ORDER BY rank DESC
    LIMIT result_limit;
END;
$$ LANGUAGE plpgsql;

-- 添加列注释
COMMENT ON COLUMN chapter.content_vector IS '章节内容的向量表示,用于语义相似度搜索';
COMMENT ON COLUMN chapter.vector_model IS '生成向量使用的模型名称';
COMMENT ON COLUMN chapter.vectorized_at IS '向量化时间';
COMMENT ON COLUMN book.search_vector IS '书籍全文搜索向量';
COMMENT ON COLUMN chapter.content_search_vector IS '章节内容全文搜索向量';
COMMENT ON COLUMN reading_note.chapter_index IS '章节序号';
COMMENT ON COLUMN reading_note.note_color IS '笔记颜色标记';
COMMENT ON COLUMN reading_bookmark.note IS '书签备注';


-- ========================================
-- AI功能相关表
-- ========================================

-- 19. AI对话表
CREATE TABLE IF NOT EXISTS ai_conversation (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    chapter_id BIGINT,
    conversation_type VARCHAR(20) NOT NULL,
    messages JSONB NOT NULL DEFAULT '[]'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ai_conversation_user ON ai_conversation(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_conversation_book ON ai_conversation(book_id);
CREATE INDEX IF NOT EXISTS idx_ai_conversation_type ON ai_conversation(conversation_type);

COMMENT ON TABLE ai_conversation IS 'AI对话记录表';
COMMENT ON COLUMN ai_conversation.id IS '对话ID';
COMMENT ON COLUMN ai_conversation.user_id IS '用户ID';
COMMENT ON COLUMN ai_conversation.book_id IS '书籍ID';
COMMENT ON COLUMN ai_conversation.chapter_id IS '章节ID（可选）';
COMMENT ON COLUMN ai_conversation.conversation_type IS '对话类型：SUMMARY-总结, QA-问答, KNOWLEDGE-知识点, QUIZ-测试';
COMMENT ON COLUMN ai_conversation.messages IS '对话消息列表（JSON格式）';
COMMENT ON COLUMN ai_conversation.create_time IS '创建时间';
COMMENT ON COLUMN ai_conversation.update_time IS '更新时间';
COMMENT ON COLUMN ai_conversation.is_delete IS '是否删除：0-否，1-是';

-- 20. 章节总结表
CREATE TABLE IF NOT EXISTS chapter_summary (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    summary_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    key_points JSONB DEFAULT '[]'::jsonb,
    ai_model VARCHAR(50) NOT NULL,
    is_cached BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_chapter_summary_chapter ON chapter_summary(chapter_id);
CREATE INDEX IF NOT EXISTS idx_chapter_summary_type ON chapter_summary(summary_type);
CREATE UNIQUE INDEX IF NOT EXISTS idx_chapter_summary_unique ON chapter_summary(chapter_id, summary_type) WHERE is_delete = 0;

COMMENT ON TABLE chapter_summary IS '章节总结表';
COMMENT ON COLUMN chapter_summary.id IS '总结ID';
COMMENT ON COLUMN chapter_summary.chapter_id IS '章节ID';
COMMENT ON COLUMN chapter_summary.summary_type IS '总结类型：BRIEF-简短, DETAILED-详细, KEYPOINTS-要点';
COMMENT ON COLUMN chapter_summary.content IS '总结内容';
COMMENT ON COLUMN chapter_summary.key_points IS '关键要点列表（JSON格式）';
COMMENT ON COLUMN chapter_summary.ai_model IS '使用的AI模型';
COMMENT ON COLUMN chapter_summary.is_cached IS '是否已缓存';
COMMENT ON COLUMN chapter_summary.create_time IS '创建时间';
COMMENT ON COLUMN chapter_summary.is_delete IS '是否删除：0-否，1-是';

-- 21. 知识点表
CREATE TABLE IF NOT EXISTS knowledge_point (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    point_type VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    position INT,
    related_chapter_ids JSONB DEFAULT '[]'::jsonb,
    related_point_ids JSONB DEFAULT '[]'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_knowledge_point_chapter ON knowledge_point(chapter_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_point_type ON knowledge_point(point_type);
CREATE INDEX IF NOT EXISTS idx_knowledge_point_name ON knowledge_point(name);

COMMENT ON TABLE knowledge_point IS '知识点表';
COMMENT ON COLUMN knowledge_point.id IS '知识点ID';
COMMENT ON COLUMN knowledge_point.chapter_id IS '章节ID';
COMMENT ON COLUMN knowledge_point.point_type IS '知识点类型：CONCEPT-概念, TERM-术语, FORMULA-公式, PRINCIPLE-原理, METHOD-方法';
COMMENT ON COLUMN knowledge_point.name IS '知识点名称';
COMMENT ON COLUMN knowledge_point.description IS '知识点描述';
COMMENT ON COLUMN knowledge_point.position IS '在章节中的位置（字符偏移量）';
COMMENT ON COLUMN knowledge_point.related_chapter_ids IS '关联章节ID列表（JSON格式）';
COMMENT ON COLUMN knowledge_point.related_point_ids IS '关联知识点ID列表（JSON格式）';
COMMENT ON COLUMN knowledge_point.create_time IS '创建时间';
COMMENT ON COLUMN knowledge_point.is_delete IS '是否删除：0-否，1-是';

-- 22. 阅读测试表
CREATE TABLE IF NOT EXISTS reading_quiz (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    questions JSONB NOT NULL DEFAULT '[]'::jsonb,
    ai_model VARCHAR(50) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reading_quiz_chapter ON reading_quiz(chapter_id);

COMMENT ON TABLE reading_quiz IS '阅读测试表';
COMMENT ON COLUMN reading_quiz.id IS '测试ID';
COMMENT ON COLUMN reading_quiz.chapter_id IS '章节ID';
COMMENT ON COLUMN reading_quiz.questions IS '题目列表（JSON格式）';
COMMENT ON COLUMN reading_quiz.ai_model IS '使用的AI模型';
COMMENT ON COLUMN reading_quiz.create_time IS '创建时间';
COMMENT ON COLUMN reading_quiz.is_delete IS '是否删除：0-否，1-是';
-- >>> END: 40_book.sql <<<

-- >>> BEGIN: 41_post.sql <<<

-- 帖子/社区领域相关表

-- 帖子表
CREATE TABLE IF NOT EXISTS post
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(512)                       NULL,
    content     TEXT                               NULL,
    tags        VARCHAR(1024)                      NULL,
    thumb_num   INT      DEFAULT 0                 NOT NULL,
    favour_num  INT      DEFAULT 0                 NOT NULL,
    comment_num INT      DEFAULT 0                 NOT NULL,
    user_id     BIGINT                             NOT NULL,
    ip_address  VARCHAR(100)                       NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL,
    post_type   VARCHAR(50)                        NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_post_user_id ON post(user_id);
CREATE INDEX IF NOT EXISTS idx_post_list ON post(is_delete, post_type, create_time);
COMMENT ON TABLE post IS '帖子';
COMMENT ON COLUMN post.id IS 'id';
COMMENT ON COLUMN post.title IS '标题';
COMMENT ON COLUMN post.content IS '内容';
COMMENT ON COLUMN post.tags IS '标签列表（json 数组）';
COMMENT ON COLUMN post.thumb_num IS '点赞数';
COMMENT ON COLUMN post.favour_num IS '收藏数';
COMMENT ON COLUMN post.comment_num IS '评论数';
COMMENT ON COLUMN post.user_id IS '创建用户 id';
COMMENT ON COLUMN post.ip_address IS 'ip地址区域';
COMMENT ON COLUMN post.create_time IS '创建时间';
COMMENT ON COLUMN post.update_time IS '更新时间';
COMMENT ON COLUMN post.is_delete IS '是否删除';
COMMENT ON COLUMN post.post_type IS '帖子类型：discussion-讨论交流，question-提问求助，share-资料分享，experience-学习心得，homework-作业答疑，exam-考试升学，course-课程讨论，announcement-活动公告，life-闲聊生活，tool-工具技巧，other-其他';

-- 帖子点赞表（硬删除）
CREATE TABLE IF NOT EXISTS post_thumb
(
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT                             NOT NULL,
    user_id     BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_pt_post_id ON post_thumb(post_id);
CREATE INDEX IF NOT EXISTS idx_pt_user_id ON post_thumb(user_id);
COMMENT ON TABLE post_thumb IS '帖子点赞';
COMMENT ON COLUMN post_thumb.id IS 'id';
COMMENT ON COLUMN post_thumb.post_id IS '帖子 id';
COMMENT ON COLUMN post_thumb.user_id IS '点赞用户 id';
COMMENT ON COLUMN post_thumb.create_time IS '创建时间';
COMMENT ON COLUMN post_thumb.update_time IS '更新时间';

-- 帖子收藏表（硬删除）
CREATE TABLE IF NOT EXISTS post_favour
(
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT                             NOT NULL,
    user_id     BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_pf_post_id ON post_favour(post_id);
CREATE INDEX IF NOT EXISTS idx_pf_user_id ON post_favour(user_id);
COMMENT ON TABLE post_favour IS '帖子收藏';
COMMENT ON COLUMN post_favour.id IS 'id';
COMMENT ON COLUMN post_favour.post_id IS '帖子 id';
COMMENT ON COLUMN post_favour.user_id IS '收藏用户 id';
COMMENT ON COLUMN post_favour.create_time IS '创建时间';
COMMENT ON COLUMN post_favour.update_time IS '更新时间';

-- 帖子评论表（硬删除)
CREATE TABLE IF NOT EXISTS post_comment
(
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT                             NOT NULL,
    user_id     BIGINT                             NOT NULL,
    content     TEXT                               NOT NULL,
    ip_address  VARCHAR(100)                       NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_pc_post_user ON post_comment(post_id, user_id);
CREATE INDEX IF NOT EXISTS idx_pc_post_time ON post_comment(post_id, create_time);
COMMENT ON TABLE post_comment IS '帖子评论';
COMMENT ON COLUMN post_comment.id IS '评论ID';
COMMENT ON COLUMN post_comment.post_id IS '帖子ID，关联到post表';
COMMENT ON COLUMN post_comment.user_id IS '评论者ID，关联到user表';
COMMENT ON COLUMN post_comment.content IS '评论内容';
COMMENT ON COLUMN post_comment.ip_address IS 'ip地址区域';
COMMENT ON COLUMN post_comment.create_time IS '创建时间';
COMMENT ON COLUMN post_comment.update_time IS '更新时间';

-- 帖子评论回复表（硬删除）
CREATE TABLE IF NOT EXISTS post_comment_reply
(
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT                             NOT NULL,
    comment_id  BIGINT                             NOT NULL,
    user_id     BIGINT                             NOT NULL,
    content     TEXT                               NOT NULL,
    ip_address  VARCHAR(100)                       NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_pcr_post_comment ON post_comment_reply(post_id, comment_id);
CREATE INDEX IF NOT EXISTS idx_pcr_comment_user ON post_comment_reply(comment_id, user_id);
CREATE INDEX IF NOT EXISTS idx_pcr_user_time ON post_comment_reply(user_id, create_time);
COMMENT ON TABLE post_comment_reply IS '帖子评论回复';
COMMENT ON COLUMN post_comment_reply.id IS '回复ID';
COMMENT ON COLUMN post_comment_reply.post_id IS '帖子ID，关联到post表';
COMMENT ON COLUMN post_comment_reply.comment_id IS '评论ID，关联到post_comment表';
COMMENT ON COLUMN post_comment_reply.user_id IS '回复者ID，关联到user表';
COMMENT ON COLUMN post_comment_reply.content IS '回复内容';
COMMENT ON COLUMN post_comment_reply.ip_address IS 'ip地址区域';
COMMENT ON COLUMN post_comment_reply.create_time IS '创建时间';
COMMENT ON COLUMN post_comment_reply.update_time IS '更新时间';
-- >>> END: 41_post.sql <<<

-- >>> BEGIN: 42_ai_book_tables.sql <<<

-- AI电子书功能相关表（从40_book.sql提取，用于补建缺失的表）

-- AI对话表
CREATE TABLE IF NOT EXISTS ai_conversation (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    chapter_id BIGINT,
    conversation_type VARCHAR(20) NOT NULL,
    messages JSONB NOT NULL DEFAULT '[]'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ai_conversation_user ON ai_conversation(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_conversation_book ON ai_conversation(book_id);
CREATE INDEX IF NOT EXISTS idx_ai_conversation_type ON ai_conversation(conversation_type);

-- 章节总结表
CREATE TABLE IF NOT EXISTS chapter_summary (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    summary_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    key_points JSONB DEFAULT '[]'::jsonb,
    ai_model VARCHAR(50) NOT NULL,
    is_cached BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_chapter_summary_chapter ON chapter_summary(chapter_id);
CREATE INDEX IF NOT EXISTS idx_chapter_summary_type ON chapter_summary(summary_type);
CREATE UNIQUE INDEX IF NOT EXISTS idx_chapter_summary_unique ON chapter_summary(chapter_id, summary_type) WHERE is_delete = 0;

-- 知识点表
CREATE TABLE IF NOT EXISTS knowledge_point (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    point_type VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    position INT,
    related_chapter_ids JSONB DEFAULT '[]'::jsonb,
    related_point_ids JSONB DEFAULT '[]'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_knowledge_point_chapter ON knowledge_point(chapter_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_point_type ON knowledge_point(point_type);
CREATE INDEX IF NOT EXISTS idx_knowledge_point_name ON knowledge_point(name);

-- 阅读测试表
CREATE TABLE IF NOT EXISTS reading_quiz (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    questions JSONB NOT NULL DEFAULT '[]'::jsonb,
    ai_model VARCHAR(50) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reading_quiz_chapter ON reading_quiz(chapter_id);
-- >>> END: 42_ai_book_tables.sql <<<

-- >>> BEGIN: 50_ai_chat.sql <<<

-- AI聊天领域相关表（重构版）
-- 包含：AI助手、知识库、工作流、会话消息等

-- =====================================================
-- AI助手表（替代原ai_role表，增强功能）
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_assistant
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(128)                            NOT NULL,
    description     TEXT                                    NULL,
    avatar_url      VARCHAR(1024)                           NULL,
    tags            JSONB           DEFAULT '[]'            NOT NULL,
    category        VARCHAR(64)                             NULL,
    
    -- 提示词配置
    system_prompt   TEXT                                    NULL,
    opening_message TEXT                                    NULL,
    suggested_questions JSONB       DEFAULT '[]'            NOT NULL,
    
    -- 模型配置
    model_name      VARCHAR(256)    DEFAULT 'dashscope/qwen-plus' NOT NULL,
    temperature     DECIMAL(3,2)    DEFAULT 0.7             NOT NULL,
    top_p           DECIMAL(3,2)    DEFAULT 0.8             NOT NULL,
    max_tokens      INT             DEFAULT 2000            NOT NULL,
    api_key         VARCHAR(512)                            NULL,
    model_base_url  VARCHAR(512)                            NULL,
    
    -- MCP 服务器绑定
    mcp_server_ids  JSONB           DEFAULT '[]'            NOT NULL,
    
    -- 状态与版本
    status          VARCHAR(32)     DEFAULT 'DRAFT'         NOT NULL,
    version         INT             DEFAULT 1               NOT NULL,
    published_version INT           DEFAULT 0               NOT NULL,
    
    -- 统计
    is_public       SMALLINT        DEFAULT 0               NOT NULL,
    usage_count     INT             DEFAULT 0               NOT NULL,
    rating          DECIMAL(2,1)    DEFAULT 0.0             NOT NULL,
    
    -- 审计
    creator_id      BIGINT                                  NOT NULL,
    sort            INT             DEFAULT 0               NOT NULL,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_aa_creator_id ON ai_assistant(creator_id);
CREATE INDEX IF NOT EXISTS idx_aa_status ON ai_assistant(status);
CREATE INDEX IF NOT EXISTS idx_aa_is_public ON ai_assistant(is_public);
CREATE INDEX IF NOT EXISTS idx_aa_category ON ai_assistant(category);
CREATE INDEX IF NOT EXISTS idx_aa_sort ON ai_assistant(sort);

COMMENT ON TABLE ai_assistant IS 'AI助手';
COMMENT ON COLUMN ai_assistant.id IS 'id';
COMMENT ON COLUMN ai_assistant.name IS 'AI助手名称';
COMMENT ON COLUMN ai_assistant.description IS 'AI助手描述';
COMMENT ON COLUMN ai_assistant.avatar_url IS '头像URL';
COMMENT ON COLUMN ai_assistant.tags IS '标签，JSON数组格式';
COMMENT ON COLUMN ai_assistant.category IS '分类';
COMMENT ON COLUMN ai_assistant.system_prompt IS '系统提示词';
COMMENT ON COLUMN ai_assistant.opening_message IS '开场白';
COMMENT ON COLUMN ai_assistant.suggested_questions IS '推荐问题，JSON数组格式';
COMMENT ON COLUMN ai_assistant.model_name IS '模型名称（格式 provider/model）';
COMMENT ON COLUMN ai_assistant.api_key IS '模型API密钥（智慧体独立配置，留空则使用系统全局配置）';
COMMENT ON COLUMN ai_assistant.model_base_url IS '模型 API Base URL（智慧体独立配置，留空则使用系统默认）';
COMMENT ON COLUMN ai_assistant.temperature IS '温度参数';
COMMENT ON COLUMN ai_assistant.top_p IS 'Top-P参数';
COMMENT ON COLUMN ai_assistant.max_tokens IS '最大Token数';
COMMENT ON COLUMN ai_assistant.mcp_server_ids IS '绑定的MCP服务器ID列表，JSON数组格式';
COMMENT ON COLUMN ai_assistant.status IS '状态：DRAFT-草稿，PUBLISHED-已发布，ARCHIVED-已归档';
COMMENT ON COLUMN ai_assistant.version IS '当前版本号';
COMMENT ON COLUMN ai_assistant.published_version IS '已发布版本号';
COMMENT ON COLUMN ai_assistant.is_public IS '是否公开：0-否，1-是';
COMMENT ON COLUMN ai_assistant.usage_count IS '使用次数';
COMMENT ON COLUMN ai_assistant.rating IS '评分';
COMMENT ON COLUMN ai_assistant.creator_id IS '创建者id';
COMMENT ON COLUMN ai_assistant.sort IS '排序';
COMMENT ON COLUMN ai_assistant.create_time IS '创建时间';
COMMENT ON COLUMN ai_assistant.update_time IS '更新时间';
COMMENT ON COLUMN ai_assistant.is_delete IS '是否删除';

-- =====================================================
-- AI助手版本历史表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_assistant_version
(
    id              BIGSERIAL PRIMARY KEY,
    assistant_id    BIGINT                                  NOT NULL,
    version         INT                                     NOT NULL,
    
    -- 快照数据
    name            VARCHAR(128)                            NOT NULL,
    description     TEXT                                    NULL,
    system_prompt   TEXT                                    NULL,
    opening_message TEXT                                    NULL,
    suggested_questions JSONB       DEFAULT '[]'            NOT NULL,
    model_name      VARCHAR(64)                             NOT NULL,
    temperature     DECIMAL(3,2)                            NOT NULL,
    top_p           DECIMAL(3,2)                            NOT NULL,
    max_tokens      INT                                     NOT NULL,
    
    -- 关联的知识库快照
    knowledge_base_ids JSONB       DEFAULT '[]'             NOT NULL,
    
    -- 发布信息
    publish_note    TEXT                                    NULL,
    published_by    BIGINT                                  NOT NULL,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_aav_assistant_id ON ai_assistant_version(assistant_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_aav_assistant_version ON ai_assistant_version(assistant_id, version);

COMMENT ON TABLE ai_assistant_version IS 'AI助手版本历史';
COMMENT ON COLUMN ai_assistant_version.id IS 'id';
COMMENT ON COLUMN ai_assistant_version.assistant_id IS 'AI助手id';
COMMENT ON COLUMN ai_assistant_version.version IS '版本号';
COMMENT ON COLUMN ai_assistant_version.name IS '名称快照';
COMMENT ON COLUMN ai_assistant_version.description IS '描述快照';
COMMENT ON COLUMN ai_assistant_version.system_prompt IS '系统提示词快照';
COMMENT ON COLUMN ai_assistant_version.opening_message IS '开场白快照';
COMMENT ON COLUMN ai_assistant_version.suggested_questions IS '推荐问题快照';
COMMENT ON COLUMN ai_assistant_version.model_name IS '模型名称快照';
COMMENT ON COLUMN ai_assistant_version.temperature IS '温度参数快照';
COMMENT ON COLUMN ai_assistant_version.top_p IS 'Top-P参数快照';
COMMENT ON COLUMN ai_assistant_version.max_tokens IS '最大Token数快照';
COMMENT ON COLUMN ai_assistant_version.knowledge_base_ids IS '关联知识库ID列表快照';
COMMENT ON COLUMN ai_assistant_version.publish_note IS '发布说明';
COMMENT ON COLUMN ai_assistant_version.published_by IS '发布者id';
COMMENT ON COLUMN ai_assistant_version.create_time IS '创建时间';

-- AI工作流表
CREATE TABLE IF NOT EXISTS ai_workflow
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(128)                            NOT NULL,
    description  TEXT                                    NULL,
    flow_data    JSONB                                   NOT NULL,
    status       VARCHAR(32)   DEFAULT 'DRAFT'           NOT NULL,
    version      INT           DEFAULT 1                 NOT NULL,
    is_public    SMALLINT      DEFAULT 0                 NOT NULL,
    creator_id   BIGINT                                  NOT NULL,
    create_time  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete    SMALLINT      DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_aw_creator_id ON ai_workflow(creator_id);
CREATE INDEX IF NOT EXISTS idx_aw_is_public ON ai_workflow(is_public);
COMMENT ON TABLE ai_workflow IS 'AI工作流';
COMMENT ON COLUMN ai_workflow.id IS 'id';
COMMENT ON COLUMN ai_workflow.name IS '工作流名称';
COMMENT ON COLUMN ai_workflow.description IS '工作流描述';
COMMENT ON COLUMN ai_workflow.flow_data IS '流程定义JSON，包含nodes和edges，如：{"nodes":[{"id":"1","type":"start","data":{}}],"edges":[{"source":"1","target":"2"}]}';
COMMENT ON COLUMN ai_workflow.status IS '工作流状态：DRAFT/PUBLISHED/ARCHIVED';
COMMENT ON COLUMN ai_workflow.creator_id IS '创建者id';
COMMENT ON COLUMN ai_workflow.create_time IS '创建时间';
COMMENT ON COLUMN ai_workflow.update_time IS '更新时间';
COMMENT ON COLUMN ai_workflow.is_delete IS '是否删除';

-- =====================================================
-- AI助手工作流关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_assistant_workflow
(
    id              BIGSERIAL PRIMARY KEY,
    assistant_id    BIGINT                             NOT NULL,
    workflow_id     BIGINT                             NOT NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_aaw_assistant_id ON ai_assistant_workflow(assistant_id);
CREATE INDEX IF NOT EXISTS idx_aaw_workflow_id ON ai_assistant_workflow(workflow_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_aaw_unique ON ai_assistant_workflow(assistant_id, workflow_id);
COMMENT ON TABLE ai_assistant_workflow IS 'AI助手工作流关联';
COMMENT ON COLUMN ai_assistant_workflow.id IS 'id';
COMMENT ON COLUMN ai_assistant_workflow.assistant_id IS 'AI助手id';
COMMENT ON COLUMN ai_assistant_workflow.workflow_id IS '工作流id';
COMMENT ON COLUMN ai_assistant_workflow.create_time IS '创建时间';
COMMENT ON COLUMN ai_assistant_workflow.update_time IS '更新时间';

-- =====================================================
-- 知识库表
-- =====================================================
CREATE TABLE IF NOT EXISTS knowledge_base
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(128)                            NOT NULL,
    description     TEXT                                    NULL,
    
    -- 向量化配置
    embedding_model VARCHAR(64)     DEFAULT 'text-embedding-v2' NOT NULL,
    embedding_dimension INT         DEFAULT 1536            NOT NULL,
    chunk_size      INT             DEFAULT 500             NOT NULL,
    chunk_overlap   INT             DEFAULT 50              NOT NULL,
    
    -- 统计
    document_count  INT             DEFAULT 0               NOT NULL,
    chunk_count     INT             DEFAULT 0               NOT NULL,
    
    -- 状态
    status          VARCHAR(32)     DEFAULT 'ACTIVE'        NOT NULL,
    
    -- 审计
    creator_id      BIGINT                                  NOT NULL,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_kb_creator_id ON knowledge_base(creator_id);
CREATE INDEX IF NOT EXISTS idx_kb_status ON knowledge_base(status);

COMMENT ON TABLE knowledge_base IS '知识库';
COMMENT ON COLUMN knowledge_base.id IS 'id';
COMMENT ON COLUMN knowledge_base.name IS '知识库名称';
COMMENT ON COLUMN knowledge_base.description IS '知识库描述';
COMMENT ON COLUMN knowledge_base.embedding_model IS '向量化模型';
COMMENT ON COLUMN knowledge_base.embedding_dimension IS '向量维度';
COMMENT ON COLUMN knowledge_base.chunk_size IS '分块大小';
COMMENT ON COLUMN knowledge_base.chunk_overlap IS '分块重叠';
COMMENT ON COLUMN knowledge_base.document_count IS '文档数量';
COMMENT ON COLUMN knowledge_base.chunk_count IS '分块数量';
COMMENT ON COLUMN knowledge_base.status IS '状态：ACTIVE-活跃，ARCHIVED-已归档';
COMMENT ON COLUMN knowledge_base.creator_id IS '创建者id';
COMMENT ON COLUMN knowledge_base.create_time IS '创建时间';
COMMENT ON COLUMN knowledge_base.update_time IS '更新时间';
COMMENT ON COLUMN knowledge_base.is_delete IS '是否删除';

-- =====================================================
-- 知识库文档表
-- =====================================================
CREATE TABLE IF NOT EXISTS knowledge_document
(
    id                  BIGSERIAL PRIMARY KEY,
    knowledge_base_id   BIGINT                                  NOT NULL,
    
    -- 文档信息
    name                VARCHAR(256)                            NOT NULL,
    file_type           VARCHAR(32)                             NOT NULL,
    file_url            VARCHAR(1024)                           NULL,
    file_size           BIGINT          DEFAULT 0               NOT NULL,
    
    -- 内容
    content             TEXT                                    NULL,
    content_hash        VARCHAR(64)                             NULL,
    
    -- 处理状态
    chunk_count         INT             DEFAULT 0               NOT NULL,
    status              VARCHAR(32)     DEFAULT 'PENDING'       NOT NULL,
    error_message       TEXT                                    NULL,
    
    -- 审计
    creator_id          BIGINT                                  NOT NULL,
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete           SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_kd_knowledge_base_id ON knowledge_document(knowledge_base_id);
CREATE INDEX IF NOT EXISTS idx_kd_status ON knowledge_document(status);
CREATE INDEX IF NOT EXISTS idx_kd_file_type ON knowledge_document(file_type);

COMMENT ON TABLE knowledge_document IS '知识库文档';
COMMENT ON COLUMN knowledge_document.id IS 'id';
COMMENT ON COLUMN knowledge_document.knowledge_base_id IS '知识库id';
COMMENT ON COLUMN knowledge_document.name IS '文档名称';
COMMENT ON COLUMN knowledge_document.file_type IS '文件类型：PDF/DOCX/TXT/MD/HTML/URL';
COMMENT ON COLUMN knowledge_document.file_url IS '文件URL';
COMMENT ON COLUMN knowledge_document.file_size IS '文件大小（字节）';
COMMENT ON COLUMN knowledge_document.content IS '文档内容（提取后的纯文本）';
COMMENT ON COLUMN knowledge_document.content_hash IS '内容哈希（用于去重）';
COMMENT ON COLUMN knowledge_document.chunk_count IS '分块数量';
COMMENT ON COLUMN knowledge_document.status IS '状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-已完成，FAILED-失败';
COMMENT ON COLUMN knowledge_document.error_message IS '错误信息';
COMMENT ON COLUMN knowledge_document.creator_id IS '创建者id';
COMMENT ON COLUMN knowledge_document.create_time IS '创建时间';
COMMENT ON COLUMN knowledge_document.update_time IS '更新时间';
COMMENT ON COLUMN knowledge_document.is_delete IS '是否删除';

-- =====================================================
-- 知识库向量分块表
-- =====================================================
CREATE TABLE IF NOT EXISTS knowledge_chunk
(
    id                  BIGSERIAL PRIMARY KEY,
    knowledge_base_id   BIGINT                                  NOT NULL,
    document_id         BIGINT                                  NOT NULL,
    
    -- 分块内容
    content             TEXT                                    NOT NULL,
    chunk_index         INT                                     NOT NULL,
    
    -- 向量
    embedding           VECTOR(1536)                            NOT NULL,
    
    -- 元数据
    metadata            JSONB           DEFAULT '{}'            NOT NULL,
    
    -- 审计
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete           SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_kc_knowledge_base_id ON knowledge_chunk(knowledge_base_id);
CREATE INDEX IF NOT EXISTS idx_kc_document_id ON knowledge_chunk(document_id);
CREATE INDEX IF NOT EXISTS idx_kc_embedding ON knowledge_chunk USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

COMMENT ON TABLE knowledge_chunk IS '知识库向量分块';
COMMENT ON COLUMN knowledge_chunk.id IS 'id';
COMMENT ON COLUMN knowledge_chunk.knowledge_base_id IS '知识库id';
COMMENT ON COLUMN knowledge_chunk.document_id IS '文档id';
COMMENT ON COLUMN knowledge_chunk.content IS '分块内容';
COMMENT ON COLUMN knowledge_chunk.chunk_index IS '分块索引';
COMMENT ON COLUMN knowledge_chunk.embedding IS '向量嵌入';
COMMENT ON COLUMN knowledge_chunk.metadata IS '元数据JSON';
COMMENT ON COLUMN knowledge_chunk.create_time IS '创建时间';
COMMENT ON COLUMN knowledge_chunk.is_delete IS '是否删除';

-- =====================================================
-- AI助手与知识库关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_assistant_knowledge
(
    id                  BIGSERIAL PRIMARY KEY,
    assistant_id        BIGINT                                  NOT NULL,
    knowledge_base_id   BIGINT                                  NOT NULL,
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_aak_assistant_id ON ai_assistant_knowledge(assistant_id);
CREATE INDEX IF NOT EXISTS idx_aak_knowledge_base_id ON ai_assistant_knowledge(knowledge_base_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_aak_unique ON ai_assistant_knowledge(assistant_id, knowledge_base_id);

COMMENT ON TABLE ai_assistant_knowledge IS 'AI助手与知识库关联';
COMMENT ON COLUMN ai_assistant_knowledge.id IS 'id';
COMMENT ON COLUMN ai_assistant_knowledge.assistant_id IS 'AI助手id';
COMMENT ON COLUMN ai_assistant_knowledge.knowledge_base_id IS '知识库id';
COMMENT ON COLUMN ai_assistant_knowledge.create_time IS '创建时间';

-- =====================================================
-- 用户AI助手收藏表
-- =====================================================
CREATE TABLE IF NOT EXISTS user_ai_assistant
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                                  NOT NULL,
    assistant_id    BIGINT                                  NOT NULL,
    
    -- 用户偏好
    is_favorite     SMALLINT        DEFAULT 0               NOT NULL,
    use_count       INT             DEFAULT 0               NOT NULL,
    last_use_time   TIMESTAMP                               NULL,
    rating          DECIMAL(2,1)    DEFAULT 0.0             NOT NULL,
    
    -- 审计
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_uaa_user_id ON user_ai_assistant(user_id);
CREATE INDEX IF NOT EXISTS idx_uaa_assistant_id ON user_ai_assistant(assistant_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_uaa_unique ON user_ai_assistant(user_id, assistant_id);

COMMENT ON TABLE user_ai_assistant IS '用户AI助手收藏';
COMMENT ON COLUMN user_ai_assistant.id IS 'id';
COMMENT ON COLUMN user_ai_assistant.user_id IS '用户id';
COMMENT ON COLUMN user_ai_assistant.assistant_id IS 'AI助手id';
COMMENT ON COLUMN user_ai_assistant.is_favorite IS '是否收藏';
COMMENT ON COLUMN user_ai_assistant.use_count IS '使用次数';
COMMENT ON COLUMN user_ai_assistant.last_use_time IS '最后使用时间';
COMMENT ON COLUMN user_ai_assistant.rating IS '评分';
COMMENT ON COLUMN user_ai_assistant.create_time IS '创建时间';
COMMENT ON COLUMN user_ai_assistant.update_time IS '更新时间';

-- =====================================================
-- AI助手会话表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_assistant_session
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                                  NOT NULL,
    assistant_id    BIGINT                                  NOT NULL,
    
    -- 会话信息
    title           VARCHAR(256)                            NULL,
    
    -- 审计
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_aas_user_id ON ai_assistant_session(user_id);
CREATE INDEX IF NOT EXISTS idx_aas_assistant_id ON ai_assistant_session(assistant_id);

COMMENT ON TABLE ai_assistant_session IS 'AI助手会话';
COMMENT ON COLUMN ai_assistant_session.id IS 'id';
COMMENT ON COLUMN ai_assistant_session.user_id IS '用户id';
COMMENT ON COLUMN ai_assistant_session.assistant_id IS 'AI助手id';
COMMENT ON COLUMN ai_assistant_session.title IS '会话标题';
COMMENT ON COLUMN ai_assistant_session.create_time IS '创建时间';
COMMENT ON COLUMN ai_assistant_session.update_time IS '更新时间';
COMMENT ON COLUMN ai_assistant_session.is_delete IS '是否删除';

-- =====================================================
-- AI助手会话消息表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_assistant_message
(
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT                                  NOT NULL,
    user_id         BIGINT                                  NOT NULL,
    
    -- 消息内容
    role            VARCHAR(32)                             NOT NULL,
    content         TEXT                                    NOT NULL,
    content_type    VARCHAR(32)     DEFAULT 'TEXT'          NOT NULL,
    
    -- RAG来源
    sources         JSONB           DEFAULT '[]'            NOT NULL,
    
    -- 审计
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_aam_session_id ON ai_assistant_message(session_id);
CREATE INDEX IF NOT EXISTS idx_aam_user_id ON ai_assistant_message(user_id);

COMMENT ON TABLE ai_assistant_message IS 'AI助手会话消息';
COMMENT ON COLUMN ai_assistant_message.id IS 'id';
COMMENT ON COLUMN ai_assistant_message.session_id IS '会话id';
COMMENT ON COLUMN ai_assistant_message.user_id IS '用户id';
COMMENT ON COLUMN ai_assistant_message.role IS '角色：USER/ASSISTANT/SYSTEM';
COMMENT ON COLUMN ai_assistant_message.content IS '消息内容';
COMMENT ON COLUMN ai_assistant_message.content_type IS '内容类型：TEXT/IMAGE/FILE';
COMMENT ON COLUMN ai_assistant_message.sources IS 'RAG来源，JSON数组';
COMMENT ON COLUMN ai_assistant_message.create_time IS '创建时间';
COMMENT ON COLUMN ai_assistant_message.is_delete IS '是否删除';

-- =====================================================
-- 工作流执行日志表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_workflow_execution_log
(
    id              BIGSERIAL PRIMARY KEY,
    execution_id    VARCHAR(64)                             NOT NULL,
    workflow_id     BIGINT                                  NOT NULL,
    workflow_name   VARCHAR(128)                            NOT NULL,
    node_id         VARCHAR(64)                             NULL,
    node_name       VARCHAR(128)                            NULL,
    node_type       VARCHAR(32)                             NULL,
    level           VARCHAR(16)     DEFAULT 'INFO'          NOT NULL,
    message         TEXT                                    NOT NULL,
    input           JSONB           DEFAULT '{}'            NOT NULL,
    output          JSONB           DEFAULT '{}'            NOT NULL,
    error_stack     TEXT                                    NULL,
    duration_ms     BIGINT          DEFAULT 0               NOT NULL,
    trace_id        VARCHAR(64)                             NULL,
    user_id         BIGINT                                  NOT NULL,
    timestamp       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_awel_execution_id ON ai_workflow_execution_log(execution_id);
CREATE INDEX IF NOT EXISTS idx_awel_workflow_id ON ai_workflow_execution_log(workflow_id);
CREATE INDEX IF NOT EXISTS idx_awel_timestamp ON ai_workflow_execution_log(timestamp);
CREATE INDEX IF NOT EXISTS idx_awel_level ON ai_workflow_execution_log(level);

COMMENT ON TABLE ai_workflow_execution_log IS '工作流执行日志';
COMMENT ON COLUMN ai_workflow_execution_log.id IS 'id';
COMMENT ON COLUMN ai_workflow_execution_log.execution_id IS '执行ID';
COMMENT ON COLUMN ai_workflow_execution_log.workflow_id IS '工作流ID';
COMMENT ON COLUMN ai_workflow_execution_log.workflow_name IS '工作流名称';
COMMENT ON COLUMN ai_workflow_execution_log.node_id IS '节点ID';
COMMENT ON COLUMN ai_workflow_execution_log.node_name IS '节点名称';
COMMENT ON COLUMN ai_workflow_execution_log.node_type IS '节点类型';
COMMENT ON COLUMN ai_workflow_execution_log.level IS '日志级别：DEBUG/INFO/WARN/ERROR';
COMMENT ON COLUMN ai_workflow_execution_log.message IS '日志消息';
COMMENT ON COLUMN ai_workflow_execution_log.input IS '输入数据';
COMMENT ON COLUMN ai_workflow_execution_log.output IS '输出数据';
COMMENT ON COLUMN ai_workflow_execution_log.error_stack IS '错误堆栈';
COMMENT ON COLUMN ai_workflow_execution_log.duration_ms IS '执行耗时(毫秒)';
COMMENT ON COLUMN ai_workflow_execution_log.trace_id IS '追踪ID';
COMMENT ON COLUMN ai_workflow_execution_log.user_id IS '用户ID';
COMMENT ON COLUMN ai_workflow_execution_log.timestamp IS '时间戳';

-- =====================================================
-- 工作流执行记录表（主表）
-- =====================================================
CREATE TABLE IF NOT EXISTS workflow_execution
(
    id                  VARCHAR(64) PRIMARY KEY,
    workflow_id         BIGINT                                  NOT NULL,
    workflow_name       VARCHAR(255)                            NOT NULL,
    workflow_version    INT             DEFAULT 1               NOT NULL,
    status              VARCHAR(32)     DEFAULT 'PENDING'       NOT NULL,
    input               JSONB                                   NULL,
    output              JSONB                                   NULL,
    variables           JSONB                                   NULL,
    node_executions     JSONB                                   NULL,
    current_node_id     VARCHAR(64)                             NULL,
    error_message       TEXT                                    NULL,
    user_id             BIGINT                                  NOT NULL,
    start_time          TIMESTAMP                               NULL,
    end_time            TIMESTAMP                               NULL,
    duration_ms         BIGINT          DEFAULT 0               NOT NULL,
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted             INT             DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_we_workflow_id ON workflow_execution(workflow_id);
CREATE INDEX IF NOT EXISTS idx_we_user_id ON workflow_execution(user_id);
CREATE INDEX IF NOT EXISTS idx_we_status ON workflow_execution(status);
CREATE INDEX IF NOT EXISTS idx_we_create_time ON workflow_execution(create_time);

COMMENT ON TABLE workflow_execution IS '工作流执行记录';
COMMENT ON COLUMN workflow_execution.id IS '执行ID（UUID）';
COMMENT ON COLUMN workflow_execution.workflow_id IS '工作流ID';
COMMENT ON COLUMN workflow_execution.workflow_name IS '工作流名称快照';
COMMENT ON COLUMN workflow_execution.workflow_version IS '工作流版本快照';
COMMENT ON COLUMN workflow_execution.status IS '执行状态：PENDING/RUNNING/PAUSED/COMPLETED/FAILED/TIMEOUT/CANCELLED';
COMMENT ON COLUMN workflow_execution.input IS '输入参数JSON';
COMMENT ON COLUMN workflow_execution.output IS '输出结果JSON';
COMMENT ON COLUMN workflow_execution.variables IS '执行变量JSON';
COMMENT ON COLUMN workflow_execution.node_executions IS '节点执行记录JSON';
COMMENT ON COLUMN workflow_execution.current_node_id IS '当前执行节点ID';
COMMENT ON COLUMN workflow_execution.error_message IS '错误信息';
COMMENT ON COLUMN workflow_execution.user_id IS '执行用户ID';
COMMENT ON COLUMN workflow_execution.start_time IS '开始时间';
COMMENT ON COLUMN workflow_execution.end_time IS '结束时间';
COMMENT ON COLUMN workflow_execution.duration_ms IS '执行耗时（毫秒）';
COMMENT ON COLUMN workflow_execution.create_time IS '创建时间';
COMMENT ON COLUMN workflow_execution.update_time IS '更新时间';
COMMENT ON COLUMN workflow_execution.deleted IS '是否删除';

-- =====================================================
-- 工作流模板表
-- =====================================================
CREATE TABLE IF NOT EXISTS workflow_template
(
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(128)                            NOT NULL,
    description         TEXT                                    NULL,
    category            VARCHAR(64)                             NULL,
    icon                VARCHAR(256)                            NULL,
    definition          JSONB                                   NOT NULL,
    tags                JSONB           DEFAULT '[]'            NOT NULL,
    is_system           SMALLINT        DEFAULT 0               NOT NULL,
    is_public           SMALLINT        DEFAULT 0               NOT NULL,
    creator_id          BIGINT                                  NULL,
    usage_count         INT             DEFAULT 0               NOT NULL,
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted             INT             DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_wt_category ON workflow_template(category);
CREATE INDEX IF NOT EXISTS idx_wt_is_system ON workflow_template(is_system);
CREATE INDEX IF NOT EXISTS idx_wt_is_public ON workflow_template(is_public);
CREATE INDEX IF NOT EXISTS idx_wt_creator_id ON workflow_template(creator_id);

COMMENT ON TABLE workflow_template IS '工作流模板';
COMMENT ON COLUMN workflow_template.id IS 'id';
COMMENT ON COLUMN workflow_template.name IS '模板名称';
COMMENT ON COLUMN workflow_template.description IS '模板描述';
COMMENT ON COLUMN workflow_template.category IS '分类：RAG/CHATBOT/AUTOMATION/DATA_PROCESSING';
COMMENT ON COLUMN workflow_template.icon IS '图标URL';
COMMENT ON COLUMN workflow_template.definition IS '工作流定义JSON';
COMMENT ON COLUMN workflow_template.tags IS '标签JSON数组';
COMMENT ON COLUMN workflow_template.is_system IS '是否系统预置：0-否，1-是';
COMMENT ON COLUMN workflow_template.is_public IS '是否公开：0-否，1-是';
COMMENT ON COLUMN workflow_template.creator_id IS '创建者ID（系统模板为空）';
COMMENT ON COLUMN workflow_template.usage_count IS '使用次数';
COMMENT ON COLUMN workflow_template.create_time IS '创建时间';
COMMENT ON COLUMN workflow_template.update_time IS '更新时间';
COMMENT ON COLUMN workflow_template.deleted IS '是否删除';

-- =====================================================
-- 工作流触发器表
-- =====================================================
CREATE TABLE IF NOT EXISTS workflow_trigger
(
    id                  BIGSERIAL PRIMARY KEY,
    workflow_id         BIGINT                                  NOT NULL,
    type                VARCHAR(32)                             NOT NULL,
    name                VARCHAR(128)                            NOT NULL,
    enabled             SMALLINT        DEFAULT 0               NOT NULL,
    config              JSONB           DEFAULT '{}'            NOT NULL,
    last_triggered_at   TIMESTAMP                               NULL,
    trigger_count       INT             DEFAULT 0               NOT NULL,
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted             INT             DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_wtr_workflow_id ON workflow_trigger(workflow_id);
CREATE INDEX IF NOT EXISTS idx_wtr_type ON workflow_trigger(type);
CREATE INDEX IF NOT EXISTS idx_wtr_enabled ON workflow_trigger(enabled);

COMMENT ON TABLE workflow_trigger IS '工作流触发器';
COMMENT ON COLUMN workflow_trigger.id IS 'id';
COMMENT ON COLUMN workflow_trigger.workflow_id IS '工作流ID';
COMMENT ON COLUMN workflow_trigger.type IS '触发器类型：SCHEDULE/WEBHOOK/EVENT';
COMMENT ON COLUMN workflow_trigger.name IS '触发器名称';
COMMENT ON COLUMN workflow_trigger.enabled IS '是否启用：0-否，1-是';
COMMENT ON COLUMN workflow_trigger.config IS '配置JSON（cron表达式/webhook路径/事件类型等）';
COMMENT ON COLUMN workflow_trigger.last_triggered_at IS '最后触发时间';
COMMENT ON COLUMN workflow_trigger.trigger_count IS '触发次数';
COMMENT ON COLUMN workflow_trigger.create_time IS '创建时间';
COMMENT ON COLUMN workflow_trigger.update_time IS '更新时间';
COMMENT ON COLUMN workflow_trigger.deleted IS '是否删除';

-- =====================================================
-- 工作流版本历史表
-- =====================================================
CREATE TABLE IF NOT EXISTS workflow_version
(
    id                  BIGSERIAL PRIMARY KEY,
    workflow_id         BIGINT                                  NOT NULL,
    version             INT                                     NOT NULL,
    name                VARCHAR(128)                            NOT NULL,
    description         TEXT                                    NULL,
    definition          JSONB                                   NOT NULL,
    publish_note        TEXT                                    NULL,
    published_by        BIGINT                                  NOT NULL,
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_wv_workflow_id ON workflow_version(workflow_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_wv_workflow_version ON workflow_version(workflow_id, version);

COMMENT ON TABLE workflow_version IS '工作流版本历史';
COMMENT ON COLUMN workflow_version.id IS 'id';
COMMENT ON COLUMN workflow_version.workflow_id IS '工作流ID';
COMMENT ON COLUMN workflow_version.version IS '版本号';
COMMENT ON COLUMN workflow_version.name IS '名称快照';
COMMENT ON COLUMN workflow_version.description IS '描述快照';
COMMENT ON COLUMN workflow_version.definition IS '工作流定义快照';
COMMENT ON COLUMN workflow_version.publish_note IS '发布说明';
COMMENT ON COLUMN workflow_version.published_by IS '发布者ID';
COMMENT ON COLUMN workflow_version.create_time IS '创建时间';

-- =====================================================
-- MCP服务器配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS mcp_server
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(128)                            NOT NULL,
    description     VARCHAR(512)                            NULL,
    url             VARCHAR(512)                            NULL,
    config_json     TEXT                                    NULL,
    enabled         SMALLINT        DEFAULT 1               NOT NULL,
    creator_id      BIGINT                                  NOT NULL,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ms_creator_id ON mcp_server(creator_id);
CREATE INDEX IF NOT EXISTS idx_ms_enabled ON mcp_server(enabled);

COMMENT ON TABLE mcp_server IS 'MCP服务器配置';
COMMENT ON COLUMN mcp_server.id IS 'id';
COMMENT ON COLUMN mcp_server.name IS '服务器名称';
COMMENT ON COLUMN mcp_server.description IS '描述';
COMMENT ON COLUMN mcp_server.url IS 'MCP服务器URL（HTTP模式用，stdio模式为空）';
COMMENT ON COLUMN mcp_server.config_json IS 'JSON配置（stdio: command/args/env; HTTP: headers等）';
COMMENT ON COLUMN mcp_server.enabled IS '是否启用：0-否，1-是';
COMMENT ON COLUMN mcp_server.creator_id IS '创建者用户ID';
COMMENT ON COLUMN mcp_server.create_time IS '创建时间';
COMMENT ON COLUMN mcp_server.update_time IS '更新时间';
COMMENT ON COLUMN mcp_server.is_delete IS '是否删除';
-- >>> END: 50_ai_chat.sql <<<

-- >>> BEGIN: 51_ai_chat_session.sql <<<

-- AI通用聊天会话表
-- 用于管理通用AI聊天的会话和消息，支持记忆摘要

-- =====================================================
-- AI聊天会话表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_chat_session
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                                  NOT NULL,
    assistant_id    BIGINT                                  NULL,

    -- 会话信息
    title           VARCHAR(256)                            NULL,

    -- 记忆摘要（当历史消息过多时，旧消息被压缩为摘要存储在此）
    memory_summary  TEXT                                    NULL,

    -- 消息统计
    message_count   INT             DEFAULT 0               NOT NULL,

    -- 审计
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_acs_user_id ON ai_chat_session(user_id);
CREATE INDEX IF NOT EXISTS idx_acs_update_time ON ai_chat_session(update_time DESC);
CREATE INDEX IF NOT EXISTS idx_chat_session_assistant ON ai_chat_session(assistant_id) WHERE assistant_id IS NOT NULL;

COMMENT ON TABLE ai_chat_session IS 'AI通用聊天会话';
COMMENT ON COLUMN ai_chat_session.id IS 'id';
COMMENT ON COLUMN ai_chat_session.user_id IS '用户id';
COMMENT ON COLUMN ai_chat_session.assistant_id IS '关联的AI助手ID，NULL表示通用AI对话';
COMMENT ON COLUMN ai_chat_session.title IS '会话标题（首次对话后由AI自动生成）';
COMMENT ON COLUMN ai_chat_session.memory_summary IS '记忆摘要（旧消息的压缩摘要，用于长对话的上下文保持）';
COMMENT ON COLUMN ai_chat_session.message_count IS '消息总数';
COMMENT ON COLUMN ai_chat_session.create_time IS '创建时间';
COMMENT ON COLUMN ai_chat_session.update_time IS '更新时间';
COMMENT ON COLUMN ai_chat_session.is_delete IS '是否删除';

-- =====================================================
-- AI聊天消息表
-- =====================================================
CREATE TABLE IF NOT EXISTS ai_chat_message
(
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT                                  NOT NULL,
    user_id         BIGINT                                  NOT NULL,

    -- 消息内容
    role            VARCHAR(32)                             NOT NULL,
    content         TEXT                                    NOT NULL,

    -- 附件（图片URL等，JSON数组）
    attachments     JSONB           DEFAULT '[]'            NOT NULL,

    -- 是否已被摘要压缩（被压缩后的消息不再发送给LLM，仅保留用于前端展示）
    is_summarized   SMALLINT        DEFAULT 0               NOT NULL,

    -- 审计
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT        DEFAULT 0               NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_acm_session_id ON ai_chat_message(session_id);
CREATE INDEX IF NOT EXISTS idx_acm_user_id ON ai_chat_message(user_id);
CREATE INDEX IF NOT EXISTS idx_acm_create_time ON ai_chat_message(create_time);

COMMENT ON TABLE ai_chat_message IS 'AI聊天消息';
COMMENT ON COLUMN ai_chat_message.id IS 'id';
COMMENT ON COLUMN ai_chat_message.session_id IS '会话id';
COMMENT ON COLUMN ai_chat_message.user_id IS '用户id';
COMMENT ON COLUMN ai_chat_message.role IS '角色：user/assistant/system';
COMMENT ON COLUMN ai_chat_message.content IS '消息内容';
COMMENT ON COLUMN ai_chat_message.attachments IS '附件（图片URL等），JSON数组';
COMMENT ON COLUMN ai_chat_message.is_summarized IS '是否已被摘要压缩：0-否，1-是';
COMMENT ON COLUMN ai_chat_message.create_time IS '创建时间';
COMMENT ON COLUMN ai_chat_message.is_delete IS '是否删除';
-- >>> END: 51_ai_chat_session.sql <<<

-- >>> BEGIN: 52_ppt_generation_session.sql <<<

-- PPT生成会话表

CREATE TABLE IF NOT EXISTS ppt_generation_session
(
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT                              NOT NULL,
    state             VARCHAR(32)   DEFAULT 'init'        NOT NULL,
    topic             TEXT                                 NOT NULL,
    outline_markdown  TEXT                                 NULL,
    template_id       BIGINT                              NULL,
    template_url      VARCHAR(1024)                       NULL,
    template_json     TEXT                                 NULL,
    slides_json       TEXT                                 NULL,
    result_url        VARCHAR(1024)                       NULL,
    create_time       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete         SMALLINT      DEFAULT 0             NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ppt_gen_session_user ON ppt_generation_session(user_id);
CREATE INDEX IF NOT EXISTS idx_ppt_gen_session_state ON ppt_generation_session(state);
COMMENT ON TABLE ppt_generation_session IS 'PPT生成会话';
COMMENT ON COLUMN ppt_generation_session.id IS '会话ID';
COMMENT ON COLUMN ppt_generation_session.user_id IS '用户ID';
COMMENT ON COLUMN ppt_generation_session.state IS '状态: init/generating_outline/outline_ready/awaiting_template/parsing_template/template_ready/generating_slides/assembling/completed/failed';
COMMENT ON COLUMN ppt_generation_session.topic IS '用户输入的主题';
COMMENT ON COLUMN ppt_generation_session.outline_markdown IS 'AI生成的Markdown大纲';
COMMENT ON COLUMN ppt_generation_session.template_id IS '选择的PPT模板ID';
COMMENT ON COLUMN ppt_generation_session.template_url IS '模板文件URL';
COMMENT ON COLUMN ppt_generation_session.template_json IS '解析后的模板结构JSON';
COMMENT ON COLUMN ppt_generation_session.slides_json IS 'AI生成的slides填充配置JSON';
COMMENT ON COLUMN ppt_generation_session.result_url IS '最终生成的PPT文件URL';
COMMENT ON COLUMN ppt_generation_session.create_time IS '创建时间';
COMMENT ON COLUMN ppt_generation_session.update_time IS '更新时间';
COMMENT ON COLUMN ppt_generation_session.is_delete IS '是否删除';
-- >>> END: 52_ppt_generation_session.sql <<<

-- >>> BEGIN: 53_ppt_project.sql <<<

-- PPT项目表：一个项目包含多个文档，可关联多个生成会话

CREATE TABLE IF NOT EXISTS ppt_project
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT                              NOT NULL,
    name        VARCHAR(256)                        NOT NULL,
    description TEXT                                NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT  DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ppt_project_user ON ppt_project (user_id);
COMMENT ON TABLE ppt_project IS 'PPT项目';
COMMENT ON COLUMN ppt_project.name IS '项目名称';
COMMENT ON COLUMN ppt_project.description IS '项目描述';

-- PPT项目文档表：用户上传的参考文档

CREATE TABLE IF NOT EXISTS ppt_project_document
(
    id          BIGSERIAL PRIMARY KEY,
    project_id  BIGINT                              NOT NULL,
    file_name   VARCHAR(512)                        NOT NULL,
    file_url    VARCHAR(1024)                       NOT NULL,
    file_type   VARCHAR(32)                         NULL,
    file_size   BIGINT    DEFAULT 0                 NOT NULL,
    content     TEXT                                NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT  DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ppt_project_doc_project ON ppt_project_document (project_id);
COMMENT ON TABLE ppt_project_document IS 'PPT项目文档';
COMMENT ON COLUMN ppt_project_document.file_name IS '文件名';
COMMENT ON COLUMN ppt_project_document.file_url IS '文件OSS URL';
COMMENT ON COLUMN ppt_project_document.file_type IS '文件类型: pdf/docx/txt/md/pptx';
COMMENT ON COLUMN ppt_project_document.file_size IS '文件大小(字节)';
COMMENT ON COLUMN ppt_project_document.content IS '提取的文本内容(供AI使用)';
-- >>> END: 53_ppt_project.sql <<<

-- >>> BEGIN: 53_ppt_template.sql <<<

-- PPT模板领域相关表

-- PPT模板表
CREATE TABLE IF NOT EXISTS ppt_template
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(128)                        NOT NULL,
    description     VARCHAR(512)  DEFAULT ''            NOT NULL,
    cover_url       VARCHAR(1024) DEFAULT ''            NOT NULL,
    template_url    VARCHAR(1024)                       NOT NULL,
    slide_count     INT           DEFAULT 0             NOT NULL,
    structure_json  TEXT                                 NULL,
    parse_status    VARCHAR(16)   DEFAULT 'pending'     NOT NULL,
    uploader_id     BIGINT                              NULL,
    enabled         BOOLEAN       DEFAULT TRUE          NOT NULL,
    create_time     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT      DEFAULT 0             NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ppt_template_enabled ON ppt_template(enabled);
CREATE INDEX IF NOT EXISTS idx_ppt_template_uploader ON ppt_template(uploader_id);
COMMENT ON TABLE ppt_template IS 'PPT模板';
COMMENT ON COLUMN ppt_template.id IS '模板ID';
COMMENT ON COLUMN ppt_template.name IS '模板名称';
COMMENT ON COLUMN ppt_template.description IS '模板描述';
COMMENT ON COLUMN ppt_template.cover_url IS '封面预览图URL';
COMMENT ON COLUMN ppt_template.template_url IS 'PPTX模板文件OSS URL';
COMMENT ON COLUMN ppt_template.slide_count IS '幻灯片页数';
COMMENT ON COLUMN ppt_template.structure_json IS '模板结构JSON（解析后缓存）';
COMMENT ON COLUMN ppt_template.parse_status IS '解析状态: pending/parsing/ready/failed';
COMMENT ON COLUMN ppt_template.uploader_id IS '上传者用户ID';

-- 已有数据库增量迁移：添加 parse_status 列，已有行默认 ready（因为之前同步解析）
-- ALTER TABLE ppt_template ADD COLUMN IF NOT EXISTS parse_status VARCHAR(16) DEFAULT 'pending' NOT NULL;
-- UPDATE ppt_template SET parse_status = 'ready' WHERE structure_json IS NOT NULL AND structure_json != '';
COMMENT ON COLUMN ppt_template.enabled IS '是否启用';
COMMENT ON COLUMN ppt_template.create_time IS '创建时间';
COMMENT ON COLUMN ppt_template.update_time IS '更新时间';
COMMENT ON COLUMN ppt_template.is_delete IS '是否删除';
-- >>> END: 53_ppt_template.sql <<<

-- >>> BEGIN: 54_ppt_session_add_project.sql <<<

-- ppt_generation_session 增加 project_id 关联和 outline_json 字段

ALTER TABLE ppt_generation_session ADD COLUMN IF NOT EXISTS project_id BIGINT NULL;
ALTER TABLE ppt_generation_session ADD COLUMN IF NOT EXISTS outline_json TEXT NULL;

CREATE INDEX IF NOT EXISTS idx_ppt_gen_session_project ON ppt_generation_session (project_id);
COMMENT ON COLUMN ppt_generation_session.project_id IS '关联的PPT项目ID';
COMMENT ON COLUMN ppt_generation_session.outline_json IS '结构化大纲JSON(新格式)';
-- >>> END: 54_ppt_session_add_project.sql <<<

-- >>> BEGIN: 60_daily_learning.sql <<<

-- 每日学习领域相关表

-- 每日单词表
CREATE TABLE IF NOT EXISTS daily_word
(
    id                  BIGSERIAL PRIMARY KEY,
    word                VARCHAR(128) NOT NULL,
    pronunciation_us    VARCHAR(128) NULL,
    pronunciation_uk    VARCHAR(128) NULL,
    audio_url_us        VARCHAR(2048) NULL,
    audio_url_uk        VARCHAR(2048) NULL,
    translation         VARCHAR(512) NOT NULL,
    example             TEXT NULL,
    example_translation TEXT NULL,
    difficulty          SMALLINT DEFAULT 1 NOT NULL,
    category            VARCHAR(64) NULL,
    notes               TEXT NULL,
    publish_date        DATE NOT NULL,
    admin_id            BIGINT NOT NULL,
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete           SMALLINT DEFAULT 0 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_dw_publish_date ON daily_word(publish_date);
CREATE INDEX IF NOT EXISTS idx_dw_word ON daily_word(word);
CREATE INDEX IF NOT EXISTS idx_dw_category ON daily_word(category);
CREATE INDEX IF NOT EXISTS idx_dw_difficulty ON daily_word(difficulty);
CREATE UNIQUE INDEX IF NOT EXISTS uq_word_category ON daily_word(word, category);
COMMENT ON TABLE daily_word IS '每日单词';
COMMENT ON COLUMN daily_word.id IS 'ID';
COMMENT ON COLUMN daily_word.word IS '单词';
COMMENT ON COLUMN daily_word.pronunciation_us IS '美式音标';
COMMENT ON COLUMN daily_word.pronunciation_uk IS '英式音标';
COMMENT ON COLUMN daily_word.audio_url_us IS '美式发音音频URL';
COMMENT ON COLUMN daily_word.audio_url_uk IS '英式发音音频URL';
COMMENT ON COLUMN daily_word.translation IS '翻译';
COMMENT ON COLUMN daily_word.example IS '例句';
COMMENT ON COLUMN daily_word.example_translation IS '例句翻译';
COMMENT ON COLUMN daily_word.difficulty IS '难度等级：1-简单，2-中等，3-困难';
COMMENT ON COLUMN daily_word.category IS '单词分类（如小学、初中、高中、CET4、CET6、雅思、托福等）';
COMMENT ON COLUMN daily_word.notes IS '单词笔记';
COMMENT ON COLUMN daily_word.publish_date IS '发布日期';
COMMENT ON COLUMN daily_word.admin_id IS '创建管理员ID';
COMMENT ON COLUMN daily_word.create_time IS '创建时间';
COMMENT ON COLUMN daily_word.update_time IS '更新时间';
COMMENT ON COLUMN daily_word.is_delete IS '是否删除：0-未删除，1-已删除';

-- 每日文章表
CREATE TABLE IF NOT EXISTS daily_article
(
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(256)                       NOT NULL,
    content       TEXT                               NOT NULL,
    summary       VARCHAR(512)                       NULL,
    cover_image   VARCHAR(1024)                      NULL,
    author        VARCHAR(128)                       NULL,
    source        VARCHAR(256)                       NULL,
    source_url    VARCHAR(1024)                      NULL,
    category      VARCHAR(64)                        NULL,
    tags          VARCHAR(512)                       NULL,
    difficulty    SMALLINT DEFAULT 1                 NOT NULL,
    read_time     INT      DEFAULT 0                 NOT NULL,
    publish_date  DATE                               NOT NULL,
    admin_id      BIGINT                             NOT NULL,
    view_count    INT      DEFAULT 0                 NOT NULL,
    like_count    INT      DEFAULT 0                 NOT NULL,
    collect_count INT      DEFAULT 0                 NOT NULL,
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete     SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_da_publish_date ON daily_article(publish_date);
CREATE INDEX IF NOT EXISTS idx_da_category ON daily_article(category);
CREATE INDEX IF NOT EXISTS idx_da_difficulty ON daily_article(difficulty);
COMMENT ON TABLE daily_article IS '每日文章';
COMMENT ON COLUMN daily_article.id IS 'id';
COMMENT ON COLUMN daily_article.title IS '文章标题';
COMMENT ON COLUMN daily_article.content IS '文章内容';
COMMENT ON COLUMN daily_article.summary IS '文章摘要';
COMMENT ON COLUMN daily_article.cover_image IS '封面图片URL';
COMMENT ON COLUMN daily_article.author IS '作者';
COMMENT ON COLUMN daily_article.source IS '来源';
COMMENT ON COLUMN daily_article.source_url IS '原文链接';
COMMENT ON COLUMN daily_article.category IS '文章分类，技能、生活、科技、文化、艺术等';
COMMENT ON COLUMN daily_article.tags IS '标签，JSON数组格式';
COMMENT ON COLUMN daily_article.difficulty IS '难度等级：1-简单，2-中等，3-困难';
COMMENT ON COLUMN daily_article.read_time IS '预计阅读时间(分钟)';
COMMENT ON COLUMN daily_article.publish_date IS '发布日期';
COMMENT ON COLUMN daily_article.admin_id IS '创建管理员id';
COMMENT ON COLUMN daily_article.view_count IS '查看次数';
COMMENT ON COLUMN daily_article.like_count IS '点赞次数';
COMMENT ON COLUMN daily_article.collect_count IS '收藏次数';
COMMENT ON COLUMN daily_article.create_time IS '创建时间';
COMMENT ON COLUMN daily_article.update_time IS '更新时间';
COMMENT ON COLUMN daily_article.is_delete IS '是否删除';

-- 用户与每日文章关联表
CREATE TABLE IF NOT EXISTS user_daily_article
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    article_id      BIGINT                             NOT NULL,
    is_read         SMALLINT DEFAULT 0                 NOT NULL,
    is_liked        SMALLINT DEFAULT 0                 NOT NULL,
    is_collected    SMALLINT DEFAULT 0                 NOT NULL,
    comment_content TEXT                               NULL,
    comment_time    TIMESTAMP                          NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_uda_user_id ON user_daily_article(user_id);
CREATE INDEX IF NOT EXISTS idx_uda_article_id ON user_daily_article(article_id);
COMMENT ON TABLE user_daily_article IS '用户与每日文章关联';
COMMENT ON COLUMN user_daily_article.id IS 'id';
COMMENT ON COLUMN user_daily_article.user_id IS '用户id';
COMMENT ON COLUMN user_daily_article.article_id IS '文章id';
COMMENT ON COLUMN user_daily_article.is_read IS '是否阅读：0-否，1-是';
COMMENT ON COLUMN user_daily_article.is_liked IS '是否点赞：0-否，1-是';
COMMENT ON COLUMN user_daily_article.is_collected IS '是否收藏：0-否，1-是';
COMMENT ON COLUMN user_daily_article.comment_content IS '评论内容';
COMMENT ON COLUMN user_daily_article.comment_time IS '评论时间';
COMMENT ON COLUMN user_daily_article.create_time IS '创建时间';
COMMENT ON COLUMN user_daily_article.update_time IS '更新时间';

-- 用户与每日单词关联表
CREATE TABLE IF NOT EXISTS user_daily_word
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT                             NOT NULL,
    word_id       BIGINT                             NOT NULL,
    is_studied    SMALLINT DEFAULT 0                 NOT NULL,
    is_collected  SMALLINT DEFAULT 0                 NOT NULL,
    mastery_level SMALLINT DEFAULT 0                 NOT NULL,
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_udw_user_id ON user_daily_word(user_id);
CREATE INDEX IF NOT EXISTS idx_udw_word_id ON user_daily_word(word_id);
COMMENT ON TABLE user_daily_word IS '用户与每日单词关联';
COMMENT ON COLUMN user_daily_word.id IS 'id';
COMMENT ON COLUMN user_daily_word.user_id IS '用户id';
COMMENT ON COLUMN user_daily_word.word_id IS '单词id';
COMMENT ON COLUMN user_daily_word.is_studied IS '是否学习：0-否，1-是';
COMMENT ON COLUMN user_daily_word.is_collected IS '是否收藏：0-否，1-是';
COMMENT ON COLUMN user_daily_word.mastery_level IS '掌握程度：0-未知，1-生词，2-熟悉，3-掌握';
COMMENT ON COLUMN user_daily_word.create_time IS '创建时间';
COMMENT ON COLUMN user_daily_word.update_time IS '更新时间';

-- 用户生词本表
CREATE TABLE IF NOT EXISTS user_word_book
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    word_id         BIGINT                             NOT NULL,
    learning_status SMALLINT DEFAULT 0                 NOT NULL,
    collected_time  TIMESTAMP                          NULL,
    is_deleted      SMALLINT DEFAULT 0                 NOT NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_uwb_user_id ON user_word_book(user_id);
CREATE INDEX IF NOT EXISTS idx_uwb_word_id ON user_word_book(word_id);
CREATE INDEX IF NOT EXISTS idx_uwb_learning_status ON user_word_book(learning_status);
CREATE INDEX IF NOT EXISTS idx_uwb_collected_time ON user_word_book(collected_time);
CREATE INDEX IF NOT EXISTS idx_uwb_is_deleted ON user_word_book(is_deleted);
COMMENT ON TABLE user_word_book IS '用户生词本';
COMMENT ON COLUMN user_word_book.id IS 'id';
COMMENT ON COLUMN user_word_book.user_id IS '用户id，关联到user表';
COMMENT ON COLUMN user_word_book.word_id IS '单词id，关联到daily_word表';
COMMENT ON COLUMN user_word_book.learning_status IS '学习状态：0-未学习，1-已学习，2-已掌握';
COMMENT ON COLUMN user_word_book.collected_time IS '收藏时间';
COMMENT ON COLUMN user_word_book.is_deleted IS '是否删除：0-否，1-是';
COMMENT ON COLUMN user_word_book.create_time IS '创建时间';
COMMENT ON COLUMN user_word_book.update_time IS '更新时间';
-- >>> END: 60_daily_learning.sql <<<

-- >>> BEGIN: 61_schedule.sql <<<

-- 班级课程表领域相关表

-- 班级课程表配置（学期/作息配置）
CREATE TABLE IF NOT EXISTS class_schedule_setting
(
    id              BIGSERIAL PRIMARY KEY,
    class_id        BIGINT                             NOT NULL,
    semester        VARCHAR(64)                        NOT NULL, -- 学期，如：2024-2025-1
    start_date      DATE                               NOT NULL, -- 学期开始日期
    total_weeks     INT         DEFAULT 20             NOT NULL, -- 总周数
    days_per_week   INT         DEFAULT 7              NOT NULL, -- 每周天数
    sections_per_day INT        DEFAULT 12             NOT NULL, -- 每天节数
    time_config     JSONB                              NULL,     -- 节次时间配置 [{"section":1,"start":"08:00","end":"08:45"}]
    is_active       SMALLINT    DEFAULT 0              NOT NULL, -- 是否当前激活学期
    create_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT    DEFAULT 0              NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_css_class_id ON class_schedule_setting (class_id);
CREATE INDEX IF NOT EXISTS idx_css_active ON class_schedule_setting (class_id, is_active);

COMMENT ON TABLE class_schedule_setting IS '班级课程表配置';
COMMENT ON COLUMN class_schedule_setting.id IS '配置ID';
COMMENT ON COLUMN class_schedule_setting.class_id IS '班级ID';
COMMENT ON COLUMN class_schedule_setting.semester IS '学期名称';
COMMENT ON COLUMN class_schedule_setting.start_date IS '开学日期(第一周周一)';
COMMENT ON COLUMN class_schedule_setting.total_weeks IS '学期总周数';
COMMENT ON COLUMN class_schedule_setting.time_config IS '具体节次时间安排';
COMMENT ON COLUMN class_schedule_setting.is_active IS '是否为当前生效学期：0-否，1-是';


-- 班级课程表项（具体排课）
CREATE TABLE IF NOT EXISTS class_schedule_item
(
    id              BIGSERIAL PRIMARY KEY,
    setting_id      BIGINT                             NULL,     -- 关联配置 (个人日程可能不关联班级配置)
    class_id        BIGINT                             NULL,     -- 关联班级ID
    user_id         BIGINT                             NULL,     -- 关联用户ID (个人日程)
    
    -- 核心类型区分
    course_type     SMALLINT    DEFAULT 1              NOT NULL, -- 1-现实课程(自定义), 2-虚拟课程(平台课程)
    
    -- 现实课程信息 (当 type=1 时使用)
    course_name     VARCHAR(128)                       NULL,     -- 自定义课程名
    teacher_name    VARCHAR(64)                        NULL,     -- 自定义教师名
    location        VARCHAR(128)                       NULL,     -- 上课地点/教室
    
    -- 虚拟课程信息 (当 type=2 时使用)
    course_id       BIGINT                             NULL,     -- 关联平台课程ID
    teacher_id      BIGINT                             NULL,     -- 关联平台讲师ID
    
    -- 排课时间规则
    day_of_week     SMALLINT                           NOT NULL, -- 星期几(1-7)
    start_section   SMALLINT                           NOT NULL, -- 开始节次
    end_section     SMALLINT                           NOT NULL, -- 结束节次
    start_week      SMALLINT    DEFAULT 1              NOT NULL, -- 开始周
    end_week        SMALLINT    DEFAULT 20             NOT NULL, -- 结束周
    week_type       SMALLINT    DEFAULT 0              NOT NULL, -- 周类型: 0-每周, 1-单周, 2-双周
    
    -- 其他展示信息
    color           VARCHAR(32)                        NULL,     -- 课程卡片颜色
    remark          VARCHAR(512)                       NULL,     -- 备注
    
    create_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT    DEFAULT 0              NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_csi_setting_id ON class_schedule_item (setting_id);
CREATE INDEX IF NOT EXISTS idx_csi_class_date ON class_schedule_item (class_id, day_of_week);
CREATE INDEX IF NOT EXISTS idx_csi_course_id ON class_schedule_item (course_id);

COMMENT ON TABLE class_schedule_item IS '班级课程表项';
COMMENT ON COLUMN class_schedule_item.course_type IS '课程类型：1-现实课程(手动录入)，2-虚拟课程(关联平台)';
COMMENT ON COLUMN class_schedule_item.course_id IS '关联平台课程ID';
COMMENT ON COLUMN class_schedule_item.week_type IS '周类型：0-全周，1-单周，2-双周';
-- >>> END: 61_schedule.sql <<<

-- >>> BEGIN: 70_announcement.sql <<<

-- 公告领域相关表

-- 公告表
CREATE TABLE IF NOT EXISTS announcement
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(256)                       NOT NULL,
    content     TEXT                               NOT NULL,
    sort        INT      DEFAULT 0                 NOT NULL,
    status      SMALLINT DEFAULT 1                 NOT NULL,
    start_time  TIMESTAMP                          NULL,
    end_time    TIMESTAMP                          NULL,
    cover_image VARCHAR(1024)                      NULL,
    admin_id    BIGINT                             NOT NULL,
    view_count  INT      DEFAULT 0                 NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ann_admin_id ON announcement(admin_id);
CREATE INDEX IF NOT EXISTS idx_ann_status ON announcement(status);
CREATE INDEX IF NOT EXISTS idx_ann_sort ON announcement(sort);
COMMENT ON TABLE announcement IS '系统公告';
COMMENT ON COLUMN announcement.id IS 'id';
COMMENT ON COLUMN announcement.title IS '公告标题';
COMMENT ON COLUMN announcement.content IS '公告内容';
COMMENT ON COLUMN announcement.sort IS '优先级，数字越大优先级越高';
COMMENT ON COLUMN announcement.status IS '状态：0-草稿，1-已发布，2-已下线';
COMMENT ON COLUMN announcement.start_time IS '公告开始展示时间';
COMMENT ON COLUMN announcement.end_time IS '公告结束展示时间';
COMMENT ON COLUMN announcement.cover_image IS '封面图片URL';
COMMENT ON COLUMN announcement.admin_id IS '发布管理员id';
COMMENT ON COLUMN announcement.view_count IS '查看次数';
COMMENT ON COLUMN announcement.create_time IS '创建时间';
COMMENT ON COLUMN announcement.update_time IS '更新时间';
COMMENT ON COLUMN announcement.is_delete IS '是否删除';

-- 公告已读表
CREATE TABLE IF NOT EXISTS announcement_read
(
    id              BIGSERIAL PRIMARY KEY,
    announcement_id BIGINT                             NOT NULL,
    user_id         BIGINT                             NOT NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ar_announcement_id ON announcement_read(announcement_id);
CREATE INDEX IF NOT EXISTS idx_ar_user_id ON announcement_read(user_id);
COMMENT ON TABLE announcement_read IS '公告阅读记录';
COMMENT ON COLUMN announcement_read.id IS 'id';
COMMENT ON COLUMN announcement_read.announcement_id IS '公告id';
COMMENT ON COLUMN announcement_read.user_id IS '用户id';
COMMENT ON COLUMN announcement_read.create_time IS '阅读时间';
-- >>> END: 70_announcement.sql <<<

-- >>> BEGIN: 71_banner.sql <<<

-- 轮播图领域相关表

-- 轮播图表
CREATE TABLE IF NOT EXISTS banner
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(128)                       NOT NULL,
    image_url   VARCHAR(1024)                      NOT NULL,
    link_type   SMALLINT DEFAULT 0                 NOT NULL,
    link_url    VARCHAR(1024)                      NULL,
    sort        INT      DEFAULT 0                 NOT NULL,
    status      SMALLINT DEFAULT 0                 NOT NULL,
    start_time  TIMESTAMP                          NULL,
    end_time    TIMESTAMP                          NULL,
    admin_id    BIGINT                             NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_banner_admin_id ON banner(admin_id);
CREATE INDEX IF NOT EXISTS idx_banner_status ON banner(status);
CREATE INDEX IF NOT EXISTS idx_banner_sort ON banner(sort);
COMMENT ON TABLE banner IS '首页轮播图';
COMMENT ON COLUMN banner.id IS 'id';
COMMENT ON COLUMN banner.title IS '轮播图标题';
COMMENT ON COLUMN banner.image_url IS '图片URL';
COMMENT ON COLUMN banner.link_type IS '跳转类型：0-无跳转，1-内部路由，2-外部链接';
COMMENT ON COLUMN banner.link_url IS '跳转URL/路由';
COMMENT ON COLUMN banner.sort IS '排序权重，数字越大越靠前';
COMMENT ON COLUMN banner.status IS '状态：0-草稿，1-已发布，2-已下线';
COMMENT ON COLUMN banner.start_time IS '开始展示时间';
COMMENT ON COLUMN banner.end_time IS '结束展示时间';
COMMENT ON COLUMN banner.admin_id IS '创建管理员id';
COMMENT ON COLUMN banner.create_time IS '创建时间';
COMMENT ON COLUMN banner.update_time IS '更新时间';
COMMENT ON COLUMN banner.is_delete IS '是否删除';
-- >>> END: 71_banner.sql <<<

-- >>> BEGIN: 80_feedback.sql <<<

-- 反馈领域相关表

-- 用户反馈表
CREATE TABLE IF NOT EXISTS user_feedback
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT                             NOT NULL,
    feedback_type VARCHAR(64)                       NOT NULL,
    title        VARCHAR(256)                       NULL,
    content      TEXT                               NOT NULL,
    attachment   VARCHAR(1024)                      NULL,
    status       SMALLINT DEFAULT 0                 NOT NULL,
    admin_id     BIGINT                             NULL,
    process_time TIMESTAMP                          NULL,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete    SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_uf_user_id ON user_feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_uf_status ON user_feedback(status);
COMMENT ON TABLE user_feedback IS '用户反馈';
COMMENT ON COLUMN user_feedback.id IS 'id';
COMMENT ON COLUMN user_feedback.user_id IS '用户ID';
COMMENT ON COLUMN user_feedback.feedback_type IS '反馈类型';
COMMENT ON COLUMN user_feedback.title IS '反馈标题';
COMMENT ON COLUMN user_feedback.content IS '反馈内容';
COMMENT ON COLUMN user_feedback.attachment IS '附件URL';
COMMENT ON COLUMN user_feedback.status IS '处理状态：0-待处理，1-处理中，2-已处理';
COMMENT ON COLUMN user_feedback.admin_id IS '处理管理员ID';
COMMENT ON COLUMN user_feedback.process_time IS '处理时间';
COMMENT ON COLUMN user_feedback.create_time IS '创建时间';
COMMENT ON COLUMN user_feedback.update_time IS '更新时间';
COMMENT ON COLUMN user_feedback.is_delete IS '是否删除';

-- 用户反馈回复表
CREATE TABLE IF NOT EXISTS user_feedback_reply
(
    id          BIGSERIAL PRIMARY KEY,
    feedback_id BIGINT        NOT NULL,
    sender_id   BIGINT        NOT NULL,
    sender_role SMALLINT      NOT NULL,
    content     VARCHAR(1000) NOT NULL,
    attachment  VARCHAR(1024) NULL,
    is_read     SMALLINT      DEFAULT 0 NOT NULL,
    create_time TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT      DEFAULT 0 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_ufr_feedback_id ON user_feedback_reply(feedback_id);
CREATE INDEX IF NOT EXISTS idx_ufr_sender_id ON user_feedback_reply(sender_id);
CREATE INDEX IF NOT EXISTS idx_ufr_create_time ON user_feedback_reply(create_time);
COMMENT ON TABLE user_feedback_reply IS '反馈回复';
COMMENT ON COLUMN user_feedback_reply.id IS 'id';
COMMENT ON COLUMN user_feedback_reply.feedback_id IS '关联的反馈ID';
COMMENT ON COLUMN user_feedback_reply.sender_id IS '发送者ID';
COMMENT ON COLUMN user_feedback_reply.sender_role IS '发送者角色：0-用户，1-管理员';
COMMENT ON COLUMN user_feedback_reply.content IS '回复内容';
COMMENT ON COLUMN user_feedback_reply.attachment IS '附件URL';
COMMENT ON COLUMN user_feedback_reply.is_read IS '是否已读：0-未读，1-已读';
COMMENT ON COLUMN user_feedback_reply.create_time IS '创建时间';
COMMENT ON COLUMN user_feedback_reply.update_time IS '更新时间';
COMMENT ON COLUMN user_feedback_reply.is_delete IS '是否删除';
-- >>> END: 80_feedback.sql <<<

-- >>> BEGIN: 81_checkin.sql <<<

-- 用户打卡表
CREATE TABLE IF NOT EXISTS user_checkin (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL,
    checkin_time TIMESTAMP NOT NULL,
    streak_days INT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_date UNIQUE (user_id, checkin_date)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_user_checkin_user_id ON user_checkin(user_id);
CREATE INDEX IF NOT EXISTS idx_user_checkin_date ON user_checkin(checkin_date);
CREATE INDEX IF NOT EXISTS idx_user_checkin_streak ON user_checkin(streak_days);

-- 添加注释
COMMENT ON TABLE user_checkin IS '用户打卡表';
COMMENT ON COLUMN user_checkin.id IS '打卡记录ID';
COMMENT ON COLUMN user_checkin.user_id IS '用户ID';
COMMENT ON COLUMN user_checkin.checkin_date IS '打卡日期';
COMMENT ON COLUMN user_checkin.checkin_time IS '打卡时间';
COMMENT ON COLUMN user_checkin.streak_days IS '连续打卡天数';
COMMENT ON COLUMN user_checkin.create_time IS '创建时间';

-- 用户打卡统计表（汇总表，用于排行榜）
CREATE TABLE IF NOT EXISTS user_checkin_stats (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_checkin_days INT NOT NULL DEFAULT 0,
    current_streak INT NOT NULL DEFAULT 0,
    max_streak INT NOT NULL DEFAULT 0,
    last_checkin_date DATE DEFAULT NULL,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_checkin_stats_user_id UNIQUE (user_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_user_checkin_stats_total ON user_checkin_stats(total_checkin_days DESC);
CREATE INDEX IF NOT EXISTS idx_user_checkin_stats_streak ON user_checkin_stats(current_streak DESC);

-- 添加注释
COMMENT ON TABLE user_checkin_stats IS '用户打卡统计表';
COMMENT ON COLUMN user_checkin_stats.id IS '统计ID';
COMMENT ON COLUMN user_checkin_stats.user_id IS '用户ID';
COMMENT ON COLUMN user_checkin_stats.total_checkin_days IS '累计打卡天数';
COMMENT ON COLUMN user_checkin_stats.current_streak IS '当前连续打卡天数';
COMMENT ON COLUMN user_checkin_stats.max_streak IS '最长连续打卡天数';
COMMENT ON COLUMN user_checkin_stats.last_checkin_date IS '最后打卡日期';
COMMENT ON COLUMN user_checkin_stats.update_time IS '更新时间';

-- 创建触发器函数，用于自动更新 update_time
CREATE OR REPLACE FUNCTION update_user_checkin_stats_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器
DROP TRIGGER IF EXISTS trigger_update_user_checkin_stats_timestamp ON user_checkin_stats;
CREATE TRIGGER trigger_update_user_checkin_stats_timestamp
    BEFORE UPDATE ON user_checkin_stats
    FOR EACH ROW
    EXECUTE FUNCTION update_user_checkin_stats_timestamp();
-- >>> END: 81_checkin.sql <<<

-- >>> BEGIN: 82_file_upload.sql <<<

-- 文件上传表
CREATE TABLE IF NOT EXISTS file_upload
(
    id            BIGSERIAL PRIMARY KEY,
    file_name     VARCHAR(256)                       NOT NULL,
    original_name VARCHAR(512)                       NOT NULL,
    file_url      VARCHAR(1024)                      NOT NULL,
    file_size     BIGINT                             NOT NULL,
    content_type  VARCHAR(128)                       NULL,
    business_type VARCHAR(64)                        NOT NULL,
    uploader_id   BIGINT                             NOT NULL,
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete     SMALLINT DEFAULT 0                 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_fu_uploader_id ON file_upload(uploader_id);
CREATE INDEX IF NOT EXISTS idx_fu_business_type ON file_upload(business_type);
CREATE INDEX IF NOT EXISTS idx_fu_create_time ON file_upload(create_time);

COMMENT ON TABLE file_upload IS '文件上传记录';
COMMENT ON COLUMN file_upload.id IS 'id';
COMMENT ON COLUMN file_upload.file_name IS '文件名（UUID）';
COMMENT ON COLUMN file_upload.original_name IS '原始文件名';
COMMENT ON COLUMN file_upload.file_url IS '文件访问URL';
COMMENT ON COLUMN file_upload.file_size IS '文件大小（字节）';
COMMENT ON COLUMN file_upload.content_type IS '文件类型';
COMMENT ON COLUMN file_upload.business_type IS '业务类型（决定存储文件夹）';
COMMENT ON COLUMN file_upload.uploader_id IS '上传者ID';
COMMENT ON COLUMN file_upload.create_time IS '上传时间';
COMMENT ON COLUMN file_upload.is_delete IS '是否删除';
-- >>> END: 82_file_upload.sql <<<

-- >>> BEGIN: 90_create_scraper_tables.sql <<<

-- PostgreSQL 抓取配置管理表
-- 创建时间: 2026-02-05

-- 抓取源配置表
CREATE TABLE IF NOT EXISTS scraper_source_config (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    source_code VARCHAR(50) NOT NULL UNIQUE,
    base_url VARCHAR(500) NOT NULL,
    description TEXT,
    
    -- 选择器配置
    title_selector VARCHAR(500),
    author_selector VARCHAR(500),
    source_selector VARCHAR(500),
    content_selector VARCHAR(500),
    date_selector VARCHAR(500),
    image_selector VARCHAR(500),
    link_selector VARCHAR(500),
    
    -- 抓取配置
    max_depth INTEGER DEFAULT 2,
    max_pages INTEGER DEFAULT 10,
    delay_ms BIGINT DEFAULT 1500,
    use_dynamic BOOLEAN DEFAULT FALSE,
    wait_for_js_ms INTEGER DEFAULT 3000,
    
    -- 调度配置
    cron_expression VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    default_max_articles INTEGER DEFAULT 5,
    default_category VARCHAR(50),
    default_difficulty INTEGER DEFAULT 2,
    
    creator_id BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    is_delete INTEGER DEFAULT 0
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_scraper_config_source_code ON scraper_source_config(source_code);
CREATE INDEX IF NOT EXISTS idx_scraper_config_enabled ON scraper_source_config(enabled);
CREATE INDEX IF NOT EXISTS idx_scraper_config_create_time ON scraper_source_config(create_time);

-- 抓取任务表
CREATE TABLE IF NOT EXISTS scraper_task (
    id BIGINT PRIMARY KEY,
    config_id BIGINT NOT NULL,
    config_name VARCHAR(100),
    status INTEGER DEFAULT 0,
    total_articles INTEGER DEFAULT 0,
    success_count INTEGER DEFAULT 0,
    fail_count INTEGER DEFAULT 0,
    created_article_ids TEXT,
    error_message TEXT,
    start_time TIMESTAMPTZ,
    end_time TIMESTAMPTZ,
    duration_ms BIGINT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    is_delete INTEGER DEFAULT 0
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_scraper_task_config_id ON scraper_task(config_id);
CREATE INDEX IF NOT EXISTS idx_scraper_task_status ON scraper_task(status);
CREATE INDEX IF NOT EXISTS idx_scraper_task_create_time ON scraper_task(create_time);

-- 添加表注释
COMMENT ON TABLE scraper_source_config IS '抓取源配置表';
COMMENT ON TABLE scraper_task IS '抓取任务表';

-- scraper_source_config 字段注释
COMMENT ON COLUMN scraper_source_config.id IS '主键ID';
COMMENT ON COLUMN scraper_source_config.name IS '配置名称';
COMMENT ON COLUMN scraper_source_config.source_code IS '数据源编码，唯一标识';
COMMENT ON COLUMN scraper_source_config.base_url IS '抓取的基础URL';
COMMENT ON COLUMN scraper_source_config.description IS '配置描述';
COMMENT ON COLUMN scraper_source_config.title_selector IS '标题CSS选择器';
COMMENT ON COLUMN scraper_source_config.author_selector IS '作者CSS选择器';
COMMENT ON COLUMN scraper_source_config.source_selector IS '来源CSS选择器';
COMMENT ON COLUMN scraper_source_config.content_selector IS '内容CSS选择器';
COMMENT ON COLUMN scraper_source_config.date_selector IS '日期CSS选择器';
COMMENT ON COLUMN scraper_source_config.image_selector IS '图片CSS选择器';
COMMENT ON COLUMN scraper_source_config.link_selector IS '链接CSS选择器';
COMMENT ON COLUMN scraper_source_config.max_depth IS '最大抓取深度';
COMMENT ON COLUMN scraper_source_config.max_pages IS '最大抓取页面数';
COMMENT ON COLUMN scraper_source_config.delay_ms IS '请求间隔(毫秒)';
COMMENT ON COLUMN scraper_source_config.use_dynamic IS '是否使用动态抓取(Selenium)';
COMMENT ON COLUMN scraper_source_config.wait_for_js_ms IS 'JS渲染等待时间(毫秒)';
COMMENT ON COLUMN scraper_source_config.cron_expression IS '定时任务Cron表达式';
COMMENT ON COLUMN scraper_source_config.enabled IS '是否启用';
COMMENT ON COLUMN scraper_source_config.default_max_articles IS '默认最大文章数';
COMMENT ON COLUMN scraper_source_config.default_category IS '默认文章分类';
COMMENT ON COLUMN scraper_source_config.default_difficulty IS '默认难度(1-5)';
COMMENT ON COLUMN scraper_source_config.creator_id IS '创建者ID';
COMMENT ON COLUMN scraper_source_config.create_time IS '创建时间';
COMMENT ON COLUMN scraper_source_config.update_time IS '更新时间';
COMMENT ON COLUMN scraper_source_config.is_delete IS '逻辑删除(0-未删除,1-已删除)';

-- scraper_task 字段注释
COMMENT ON COLUMN scraper_task.id IS '主键ID';
COMMENT ON COLUMN scraper_task.config_id IS '关联的配置ID';
COMMENT ON COLUMN scraper_task.config_name IS '配置名称(冗余)';
COMMENT ON COLUMN scraper_task.status IS '任务状态(0-待执行,1-执行中,2-成功,3-失败,4-部分成功)';
COMMENT ON COLUMN scraper_task.total_articles IS '抓取到的文章总数';
COMMENT ON COLUMN scraper_task.success_count IS '成功保存的文章数';
COMMENT ON COLUMN scraper_task.fail_count IS '保存失败的文章数';
COMMENT ON COLUMN scraper_task.created_article_ids IS '创建的文章ID列表(JSON数组)';
COMMENT ON COLUMN scraper_task.error_message IS '错误信息';
COMMENT ON COLUMN scraper_task.start_time IS '任务开始时间';
COMMENT ON COLUMN scraper_task.end_time IS '任务结束时间';
COMMENT ON COLUMN scraper_task.duration_ms IS '执行耗时(毫秒)';
COMMENT ON COLUMN scraper_task.create_time IS '创建时间';
COMMENT ON COLUMN scraper_task.is_delete IS '逻辑删除(0-未删除,1-已删除)';
-- >>> END: 90_create_scraper_tables.sql <<<

-- >>> BEGIN: 91_exam.sql <<<

-- 试卷编辑系统相关表

-- 题库表
CREATE TABLE IF NOT EXISTS question
(
    id              BIGSERIAL PRIMARY KEY,
    type            VARCHAR(20)                            NOT NULL,
    subject         VARCHAR(20)                            NOT NULL,
    grade           VARCHAR(20)                            NULL,
    difficulty      SMALLINT     DEFAULT 3                 NOT NULL,
    content         TEXT                                   NOT NULL,
    options         JSONB                                  NULL,
    answer          TEXT                                   NOT NULL,
    explanation     TEXT                                   NULL,
    knowledge_tags  TEXT[]                                 NULL,
    image_url       VARCHAR(500)                           NULL,
    source          VARCHAR(100) DEFAULT 'MANUAL'          NOT NULL,
    creator_id      BIGINT                                 NOT NULL,
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT     DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_question_type ON question(type);
CREATE INDEX IF NOT EXISTS idx_question_subject ON question(subject);
CREATE INDEX IF NOT EXISTS idx_question_grade ON question(grade);
CREATE INDEX IF NOT EXISTS idx_question_difficulty ON question(difficulty);
CREATE INDEX IF NOT EXISTS idx_question_creator ON question(creator_id);
CREATE INDEX IF NOT EXISTS idx_question_knowledge_tags ON question USING GIN(knowledge_tags);
COMMENT ON TABLE question IS '题库';
COMMENT ON COLUMN question.id IS '题目ID';
COMMENT ON COLUMN question.type IS '题型: SINGLE_CHOICE/MULTI_CHOICE/FILL_BLANK/TRUE_FALSE/SHORT_ANSWER/CALCULATION/ESSAY';
COMMENT ON COLUMN question.subject IS '学科: MATH/CHINESE/ENGLISH/PHYSICS/CHEMISTRY/BIOLOGY/HISTORY/GEOGRAPHY/POLITICS';
COMMENT ON COLUMN question.grade IS '年级: GRADE_7~GRADE_12 / COLLEGE';
COMMENT ON COLUMN question.difficulty IS '难度: 1-5';
COMMENT ON COLUMN question.content IS '题干内容(支持KaTeX公式)';
COMMENT ON COLUMN question.options IS '选项JSON数组 [{"label":"A","text":"..."}]';
COMMENT ON COLUMN question.answer IS '标准答案';
COMMENT ON COLUMN question.explanation IS '解析';
COMMENT ON COLUMN question.knowledge_tags IS '知识点标签数组';
COMMENT ON COLUMN question.source IS '来源: MANUAL/AI/IMPORT';

-- 试卷表
CREATE TABLE IF NOT EXISTS exam_paper
(
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(200)                           NOT NULL,
    subtitle        VARCHAR(500)                           NULL,
    subject         VARCHAR(20)                            NOT NULL,
    grade           VARCHAR(20)                            NULL,
    total_score     INT          DEFAULT 0                 NOT NULL,
    duration_min    INT                                    NULL,
    layout          JSONB        DEFAULT '{}'::jsonb       NOT NULL,
    template_id     BIGINT                                 NULL,
    status          VARCHAR(20)  DEFAULT 'DRAFT'           NOT NULL,
    creator_id      BIGINT                                 NOT NULL,
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT     DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_exam_paper_subject ON exam_paper(subject);
CREATE INDEX IF NOT EXISTS idx_exam_paper_creator ON exam_paper(creator_id);
CREATE INDEX IF NOT EXISTS idx_exam_paper_status ON exam_paper(status);
COMMENT ON TABLE exam_paper IS '试卷';
COMMENT ON COLUMN exam_paper.id IS '试卷ID';
COMMENT ON COLUMN exam_paper.title IS '试卷标题';
COMMENT ON COLUMN exam_paper.subtitle IS '副标题';
COMMENT ON COLUMN exam_paper.subject IS '学科';
COMMENT ON COLUMN exam_paper.total_score IS '总分';
COMMENT ON COLUMN exam_paper.duration_min IS '考试时长(分钟)';
COMMENT ON COLUMN exam_paper.layout IS '排版配置JSON {paperSize,columns,fontSize}';
COMMENT ON COLUMN exam_paper.template_id IS '关联的试卷模板ID，NULL使用系统默认模板';
COMMENT ON COLUMN exam_paper.status IS '状态: DRAFT/PUBLISHED';

-- 试卷大题表
CREATE TABLE IF NOT EXISTS paper_section
(
    id              BIGSERIAL PRIMARY KEY,
    paper_id        BIGINT                                 NOT NULL REFERENCES exam_paper(id) ON DELETE CASCADE,
    title           VARCHAR(100)                           NOT NULL,
    description     VARCHAR(500)                           NULL,
    question_type   VARCHAR(20)                            NULL,
    sort_order      INT          DEFAULT 0                 NOT NULL,
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_paper_section_paper ON paper_section(paper_id);
COMMENT ON TABLE paper_section IS '试卷大题';
COMMENT ON COLUMN paper_section.title IS '大题标题(如"一、选择题")';
COMMENT ON COLUMN paper_section.description IS '大题描述(如"每小题5分，共60分")';
COMMENT ON COLUMN paper_section.question_type IS '该大题的题型';
COMMENT ON COLUMN paper_section.sort_order IS '排序';

-- 试卷题目关联表
CREATE TABLE IF NOT EXISTS paper_question
(
    id              BIGSERIAL PRIMARY KEY,
    section_id      BIGINT                                 NOT NULL REFERENCES paper_section(id) ON DELETE CASCADE,
    question_id     BIGINT                                 NOT NULL REFERENCES question(id),
    score           INT          DEFAULT 0                 NOT NULL,
    sort_order      INT          DEFAULT 0                 NOT NULL,
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_paper_question_section ON paper_question(section_id);
CREATE INDEX IF NOT EXISTS idx_paper_question_question ON paper_question(question_id);
COMMENT ON TABLE paper_question IS '试卷题目关联';
COMMENT ON COLUMN paper_question.section_id IS '所属大题ID';
COMMENT ON COLUMN paper_question.question_id IS '题目ID';
COMMENT ON COLUMN paper_question.score IS '该题分值';
COMMENT ON COLUMN paper_question.sort_order IS '排序';

-- 试卷模板表
CREATE TABLE IF NOT EXISTS exam_template
(
    id           BIGINT                                 PRIMARY KEY,
    name         VARCHAR(200)                           NOT NULL,
    description  TEXT                                   NULL,
    template_url VARCHAR(500)                           NULL,
    cover_url    VARCHAR(500)                           NULL,
    creator_id   BIGINT                                 NULL,
    is_system    BOOLEAN      DEFAULT FALSE             NOT NULL,
    is_enabled   BOOLEAN      DEFAULT TRUE              NOT NULL,
    create_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete    SMALLINT     DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_exam_template_creator ON exam_template(creator_id);
CREATE INDEX IF NOT EXISTS idx_exam_template_system ON exam_template(is_system);
COMMENT ON TABLE exam_template IS '试卷模板';
COMMENT ON COLUMN exam_template.id IS '模板ID（Snowflake）';
COMMENT ON COLUMN exam_template.name IS '模板名称';
COMMENT ON COLUMN exam_template.description IS '模板描述';
COMMENT ON COLUMN exam_template.template_url IS '模板文件URL';
COMMENT ON COLUMN exam_template.cover_url IS '封面图URL';
COMMENT ON COLUMN exam_template.creator_id IS '创建者用户ID';
COMMENT ON COLUMN exam_template.is_system IS '是否系统内置模板';
COMMENT ON COLUMN exam_template.is_enabled IS '是否启用';
COMMENT ON COLUMN exam_template.is_delete IS '是否删除';
-- >>> END: 91_exam.sql <<<

-- >>> BEGIN: 92_membership.sql <<<

-- 会员系统相关表

-- 会员计划表（管理员配置）
CREATE TABLE IF NOT EXISTS membership_plan
(
    id                    BIGSERIAL PRIMARY KEY,
    name                  VARCHAR(128)                           NOT NULL,
    code                  VARCHAR(64)                            NOT NULL,
    description           TEXT                                   NULL,
    price                 DECIMAL(10, 2) DEFAULT 0.00            NOT NULL,
    duration_days         INT            DEFAULT 30              NOT NULL,
    ai_chat_daily_limit   INT            DEFAULT -1              NOT NULL,
    ai_chat_monthly_limit INT            DEFAULT -1              NOT NULL,
    ai_ppt_daily_limit    INT            DEFAULT -1              NOT NULL,
    ai_ppt_monthly_limit  INT            DEFAULT -1              NOT NULL,
    ai_exam_daily_limit   INT            DEFAULT -1              NOT NULL,
    ai_exam_monthly_limit INT            DEFAULT -1              NOT NULL,
    ai_book_daily_limit   INT            DEFAULT -1              NOT NULL,
    ai_book_monthly_limit INT            DEFAULT -1              NOT NULL,
    ai_grading_daily_limit INT           DEFAULT -1              NOT NULL,
    ai_grading_monthly_limit INT         DEFAULT -1              NOT NULL,
    course_member_access  SMALLINT       DEFAULT 0               NOT NULL,
    is_default            SMALLINT       DEFAULT 0               NOT NULL,
    sort_order            INT            DEFAULT 0               NOT NULL,
    create_time           TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time           TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete             SMALLINT       DEFAULT 0               NOT NULL,
    CONSTRAINT uk_membership_plan_code UNIQUE (code)
);
COMMENT ON TABLE membership_plan IS '会员计划';
COMMENT ON COLUMN membership_plan.id IS 'id';
COMMENT ON COLUMN membership_plan.name IS '计划名称，如：免费版、基础版、专业版';
COMMENT ON COLUMN membership_plan.code IS '计划编码：FREE/BASIC/PRO/TEACHER';
COMMENT ON COLUMN membership_plan.description IS '计划描述';
COMMENT ON COLUMN membership_plan.price IS '价格（元）';
COMMENT ON COLUMN membership_plan.duration_days IS '有效期天数，0表示永久';
COMMENT ON COLUMN membership_plan.ai_chat_daily_limit IS 'AI对话每日限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_chat_monthly_limit IS 'AI对话每月限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_ppt_daily_limit IS 'PPT生成每日限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_ppt_monthly_limit IS 'PPT生成每月限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_exam_daily_limit IS 'AI出题每日限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_exam_monthly_limit IS 'AI出题每月限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_book_daily_limit IS '电子书AI每日限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_book_monthly_limit IS '电子书AI每月限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_grading_daily_limit IS '智能批改每日限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.ai_grading_monthly_limit IS '智能批改每月限额，-1表示无限制';
COMMENT ON COLUMN membership_plan.course_member_access IS '是否可访问会员课：0-否，1-是';
COMMENT ON COLUMN membership_plan.is_default IS '是否默认计划（未开通会员的用户使用）：0-否，1-是';
COMMENT ON COLUMN membership_plan.sort_order IS '排序，越小越靠前';
COMMENT ON COLUMN membership_plan.create_time IS '创建时间';
COMMENT ON COLUMN membership_plan.update_time IS '更新时间';
COMMENT ON COLUMN membership_plan.is_delete IS '是否删除';

-- 用户会员表
CREATE TABLE IF NOT EXISTS user_membership
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT                             NOT NULL,
    plan_id     BIGINT                             NOT NULL,
    order_no    VARCHAR(64)                        NULL,
    start_time  TIMESTAMP                          NOT NULL,
    expire_time TIMESTAMP                          NULL,
    status      SMALLINT DEFAULT 0                 NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete   SMALLINT DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_um_user_id ON user_membership(user_id);
CREATE INDEX IF NOT EXISTS idx_um_plan_id ON user_membership(plan_id);
CREATE INDEX IF NOT EXISTS idx_um_status ON user_membership(status);
CREATE INDEX IF NOT EXISTS idx_um_order_no ON user_membership(order_no);
CREATE INDEX IF NOT EXISTS idx_um_expire_time ON user_membership(expire_time);
COMMENT ON TABLE user_membership IS '用户会员';
COMMENT ON COLUMN user_membership.id IS 'id';
COMMENT ON COLUMN user_membership.user_id IS '用户id';
COMMENT ON COLUMN user_membership.plan_id IS '会员计划id';
COMMENT ON COLUMN user_membership.order_no IS '关联订单号';
COMMENT ON COLUMN user_membership.start_time IS '生效时间';
COMMENT ON COLUMN user_membership.expire_time IS '到期时间，null表示永久';
COMMENT ON COLUMN user_membership.status IS '状态：0-待支付，1-生效中，2-已过期，3-已取消';
COMMENT ON COLUMN user_membership.create_time IS '创建时间';
COMMENT ON COLUMN user_membership.update_time IS '更新时间';
COMMENT ON COLUMN user_membership.is_delete IS '是否删除';

-- AI使用记录表（按天/月聚合计数）
CREATE TABLE IF NOT EXISTS ai_usage_record
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT                             NOT NULL,
    feature_type VARCHAR(32)                        NOT NULL,
    usage_date   DATE                               NOT NULL,
    usage_count  INT      DEFAULT 0                 NOT NULL,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_ai_usage_record UNIQUE (user_id, feature_type, usage_date)
);
CREATE INDEX IF NOT EXISTS idx_aur_user_id ON ai_usage_record(user_id);
CREATE INDEX IF NOT EXISTS idx_aur_feature_type ON ai_usage_record(feature_type);
CREATE INDEX IF NOT EXISTS idx_aur_usage_date ON ai_usage_record(usage_date);
COMMENT ON TABLE ai_usage_record IS 'AI功能使用记录';
COMMENT ON COLUMN ai_usage_record.id IS 'id';
COMMENT ON COLUMN ai_usage_record.user_id IS '用户id';
COMMENT ON COLUMN ai_usage_record.feature_type IS '功能类型：AI_CHAT/AI_PPT/AI_EXAM/AI_BOOK/AI_GRADING';
COMMENT ON COLUMN ai_usage_record.usage_date IS '使用日期';
COMMENT ON COLUMN ai_usage_record.usage_count IS '当日使用次数';
COMMENT ON COLUMN ai_usage_record.create_time IS '创建时间';
COMMENT ON COLUMN ai_usage_record.update_time IS '更新时间';

-- 种子数据：默认会员计划
INSERT INTO membership_plan (name, code, description, price, duration_days,
    ai_chat_daily_limit, ai_chat_monthly_limit,
    ai_ppt_daily_limit, ai_ppt_monthly_limit,
    ai_exam_daily_limit, ai_exam_monthly_limit,
    ai_book_daily_limit, ai_book_monthly_limit,
    ai_grading_daily_limit, ai_grading_monthly_limit,
    course_member_access, is_default, sort_order)
VALUES
    ('免费版', 'FREE', '注册即享的基础权益', 0.00, 0,
     10, 200, 1, 20, 3, 60, 5, 100, 3, 60,
     0, 1, 0),
    ('基础版', 'BASIC', '适合日常学习的进阶套餐', 29.90, 30,
     50, 1000, 5, 100, 15, 300, 30, 600, 15, 300,
     1, 0, 1),
    ('专业版', 'PRO', '全功能无限制的专业套餐', 59.90, 30,
     -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
     1, 0, 2),
    ('教师版', 'TEACHER', '教师专属权益计划', 0.00, 0,
     100, 2000, 20, 400, 50, 1000, 50, 1000, 50, 1000,
     1, 0, 3)
ON CONFLICT (code) DO NOTHING;
-- >>> END: 92_membership.sql <<<

-- >>> BEGIN: 93_grading.sql <<<

-- 智能批改系统相关表

-- 作业提交表
CREATE TABLE IF NOT EXISTS homework_submission
(
    id              BIGSERIAL PRIMARY KEY,
    student_id      BIGINT                                 NOT NULL,
    class_id        BIGINT                                 NULL,
    grading_mode    VARCHAR(20)  DEFAULT 'GENERAL'         NOT NULL,
    title           VARCHAR(200)                           NULL,
    subject         VARCHAR(20)                            NULL,
    grade           VARCHAR(20)                            NULL,
    image_urls      TEXT[]                                 NOT NULL,
    ocr_raw_text    TEXT                                   NULL,
    structured_data JSONB                                  NULL,
    status          VARCHAR(20)  DEFAULT 'PENDING'         NOT NULL,
    exam_paper_id   BIGINT                                 NULL,
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT     DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_hw_submission_student ON homework_submission(student_id);
CREATE INDEX IF NOT EXISTS idx_hw_submission_class ON homework_submission(class_id);
CREATE INDEX IF NOT EXISTS idx_hw_submission_subject ON homework_submission(subject);
CREATE INDEX IF NOT EXISTS idx_hw_submission_status ON homework_submission(status);
CREATE INDEX IF NOT EXISTS idx_hw_submission_exam_paper ON homework_submission(exam_paper_id);
COMMENT ON TABLE homework_submission IS '作业提交';
COMMENT ON COLUMN homework_submission.id IS '提交ID';
COMMENT ON COLUMN homework_submission.student_id IS '学生ID';
COMMENT ON COLUMN homework_submission.class_id IS '班级ID(可选)';
COMMENT ON COLUMN homework_submission.grading_mode IS '批改模式: EXAM_PAPER(试卷批改)/GENERAL(通用作业助手)';
COMMENT ON COLUMN homework_submission.title IS '作业标题(通用模式可自定义)';
COMMENT ON COLUMN homework_submission.subject IS '学科(可空，通用模式下AI自动推断): MATH/CHINESE/ENGLISH/PHYSICS/CHEMISTRY/BIOLOGY/HISTORY/GEOGRAPHY/POLITICS';
COMMENT ON COLUMN homework_submission.grade IS '年级';
COMMENT ON COLUMN homework_submission.image_urls IS '作业图片OSS URL数组';
COMMENT ON COLUMN homework_submission.ocr_raw_text IS 'OCR原始识别文本';
COMMENT ON COLUMN homework_submission.structured_data IS 'OCR结构化数据JSON';
COMMENT ON COLUMN homework_submission.status IS '状态: PENDING/OCR_PROCESSING/GRADING/COMPLETED/FAILED';
COMMENT ON COLUMN homework_submission.exam_paper_id IS '关联试卷ID(可选,有标准答案时)';

-- 批改结果表
CREATE TABLE IF NOT EXISTS grading_result
(
    id              BIGSERIAL PRIMARY KEY,
    submission_id   BIGINT                                 NOT NULL REFERENCES homework_submission(id),
    total_score     INT                                    NULL,
    max_score       INT                                    NULL,
    overall_comment TEXT                                   NULL,
    model_id        VARCHAR(100)                           NULL,
    grading_time    TIMESTAMP                              NULL,
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_delete       SMALLINT     DEFAULT 0                 NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_grading_result_submission ON grading_result(submission_id);
COMMENT ON TABLE grading_result IS '批改结果';
COMMENT ON COLUMN grading_result.id IS '批改结果ID';
COMMENT ON COLUMN grading_result.submission_id IS '关联作业提交ID';
COMMENT ON COLUMN grading_result.total_score IS '总得分';
COMMENT ON COLUMN grading_result.max_score IS '满分';
COMMENT ON COLUMN grading_result.overall_comment IS '总评语';
COMMENT ON COLUMN grading_result.model_id IS '使用的AI模型ID';
COMMENT ON COLUMN grading_result.grading_time IS '批改完成时间';

-- 单题批改详情表
CREATE TABLE IF NOT EXISTS question_grading
(
    id                  BIGSERIAL PRIMARY KEY,
    grading_result_id   BIGINT                                 NOT NULL REFERENCES grading_result(id) ON DELETE CASCADE,
    question_index      INT                                    NOT NULL,
    question_content    TEXT                                   NULL,
    question_type       VARCHAR(20)                            NULL,
    student_answer      TEXT                                   NULL,
    standard_answer     TEXT                                   NULL,
    score               INT          DEFAULT 0                 NOT NULL,
    max_score           INT          DEFAULT 0                 NOT NULL,
    error_categories    TEXT[]                                 NULL,
    error_detail        TEXT                                   NULL,
    knowledge_points    TEXT[]                                 NULL,
    comment             TEXT                                   NULL,
    similar_question_id BIGINT                                 NULL,
    create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_question_grading_result ON question_grading(grading_result_id);
COMMENT ON TABLE question_grading IS '单题批改详情';
COMMENT ON COLUMN question_grading.grading_result_id IS '关联批改结果ID';
COMMENT ON COLUMN question_grading.question_index IS '题号';
COMMENT ON COLUMN question_grading.question_content IS '题干内容';
COMMENT ON COLUMN question_grading.question_type IS '题型';
COMMENT ON COLUMN question_grading.student_answer IS '学生答案(OCR识别)';
COMMENT ON COLUMN question_grading.standard_answer IS '标准答案';
COMMENT ON COLUMN question_grading.score IS '得分';
COMMENT ON COLUMN question_grading.max_score IS '满分';
COMMENT ON COLUMN question_grading.error_categories IS '错误分类: CONCEPT_ERROR/CALCULATION_ERROR/READING_ERROR/UNIT_ERROR/STEP_MISSING/LOGIC_INCOMPLETE/EXPRESSION_UNCLEAR/GRAMMAR_ERROR/SPELLING_ERROR/FORMAT_ERROR/KNOWLEDGE_GAP/CARELESS_MISTAKE';
COMMENT ON COLUMN question_grading.error_detail IS '错误详情说明';
COMMENT ON COLUMN question_grading.knowledge_points IS '关联知识点';
COMMENT ON COLUMN question_grading.comment IS '单题评语';
COMMENT ON COLUMN question_grading.similar_question_id IS '推荐的同类题ID';

-- 学生知识画像表
CREATE TABLE IF NOT EXISTS student_knowledge_profile
(
    id                      BIGSERIAL PRIMARY KEY,
    student_id              BIGINT                                 NOT NULL,
    subject                 VARCHAR(20)                            NOT NULL,
    knowledge_point         VARCHAR(200)                           NOT NULL,
    mastery_level           DOUBLE PRECISION DEFAULT 0.5           NOT NULL,
    total_attempts          INT              DEFAULT 0             NOT NULL,
    correct_count           INT              DEFAULT 0             NOT NULL,
    recent_error_categories TEXT[]                                 NULL,
    last_updated            TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE(student_id, subject, knowledge_point)
);
CREATE INDEX IF NOT EXISTS idx_skp_student ON student_knowledge_profile(student_id);
CREATE INDEX IF NOT EXISTS idx_skp_subject ON student_knowledge_profile(subject);
CREATE INDEX IF NOT EXISTS idx_skp_mastery ON student_knowledge_profile(mastery_level);
COMMENT ON TABLE student_knowledge_profile IS '学生知识画像';
COMMENT ON COLUMN student_knowledge_profile.student_id IS '学生ID';
COMMENT ON COLUMN student_knowledge_profile.subject IS '学科';
COMMENT ON COLUMN student_knowledge_profile.knowledge_point IS '知识点名称';
COMMENT ON COLUMN student_knowledge_profile.mastery_level IS '掌握度 0.0~1.0';
COMMENT ON COLUMN student_knowledge_profile.total_attempts IS '总答题次数';
COMMENT ON COLUMN student_knowledge_profile.correct_count IS '正确次数';
COMMENT ON COLUMN student_knowledge_profile.recent_error_categories IS '近期错误类型';
COMMENT ON COLUMN student_knowledge_profile.last_updated IS '最近更新时间';
-- >>> END: 93_grading.sql <<<

-- >>> BEGIN: 94_learning_analytics.sql <<<

-- 学情分析 - 学习活动流水表
CREATE TABLE IF NOT EXISTS learning_activity
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    activity_type VARCHAR(32)  NOT NULL,
    reference_id  BIGINT       NULL,
    subject       VARCHAR(20)  NULL,
    class_id      BIGINT       NULL,
    duration_sec  INT          DEFAULT 0  NOT NULL,
    score         INT          NULL,
    max_score     INT          NULL,
    detail        JSONB        NULL,
    activity_date DATE         NOT NULL,
    create_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_la_user_id ON learning_activity(user_id);
CREATE INDEX IF NOT EXISTS idx_la_activity_type ON learning_activity(activity_type);
CREATE INDEX IF NOT EXISTS idx_la_activity_date ON learning_activity(activity_date);
CREATE INDEX IF NOT EXISTS idx_la_class_id ON learning_activity(class_id);
CREATE INDEX IF NOT EXISTS idx_la_user_date ON learning_activity(user_id, activity_date);
CREATE INDEX IF NOT EXISTS idx_la_class_date ON learning_activity(class_id, activity_date);

COMMENT ON TABLE learning_activity IS '学习活动流水表';
COMMENT ON COLUMN learning_activity.id IS '活动ID';
COMMENT ON COLUMN learning_activity.user_id IS '用户ID';
COMMENT ON COLUMN learning_activity.activity_type IS '活动类型: COURSE_WATCH/WORD_STUDY/ARTICLE_READ/HOMEWORK_SUBMIT/CHECKIN/EXAM_PRACTICE';
COMMENT ON COLUMN learning_activity.reference_id IS '关联业务ID（课程ID/单词ID/提交ID等）';
COMMENT ON COLUMN learning_activity.subject IS '关联学科（可选）';
COMMENT ON COLUMN learning_activity.class_id IS '关联班级ID（可选）';
COMMENT ON COLUMN learning_activity.duration_sec IS '活动时长（秒）';
COMMENT ON COLUMN learning_activity.score IS '得分（做题类活动）';
COMMENT ON COLUMN learning_activity.max_score IS '满分';
COMMENT ON COLUMN learning_activity.detail IS '扩展详情JSON';
COMMENT ON COLUMN learning_activity.activity_date IS '活动日期';
COMMENT ON COLUMN learning_activity.create_time IS '创建时间';
-- >>> END: 94_learning_analytics.sql <<<

-- >>> BEGIN: 95_livestream.sql <<<

-- 直播系统表
-- 包含直播间和直播间聊天消息

-- 直播间
CREATE TABLE IF NOT EXISTS live_room (
    id            BIGINT PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    cover_url     VARCHAR(500),
    host_user_id  BIGINT NOT NULL REFERENCES "user"(id),
    class_id      BIGINT REFERENCES class_info(id),
    stream_key    VARCHAR(64) NOT NULL UNIQUE,
    status        VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    visibility    VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    viewer_count  INT NOT NULL DEFAULT 0,
    peak_viewers  INT NOT NULL DEFAULT 0,
    started_at    TIMESTAMP,
    ended_at      TIMESTAMP,
    duration      INT,
    is_recording  BOOLEAN DEFAULT false,
    playback_url  VARCHAR(500),
    create_time   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete     SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE live_room IS '直播间';
COMMENT ON COLUMN live_room.id IS '雪花ID';
COMMENT ON COLUMN live_room.title IS '直播间标题';
COMMENT ON COLUMN live_room.description IS '直播间描述';
COMMENT ON COLUMN live_room.cover_url IS '封面图URL';
COMMENT ON COLUMN live_room.host_user_id IS '主播用户ID';
COMMENT ON COLUMN live_room.class_id IS '关联班级ID（NULL=公开直播）';
COMMENT ON COLUMN live_room.stream_key IS 'OBS推流密钥';
COMMENT ON COLUMN live_room.status IS '状态: CREATED/LIVE/ENDED';
COMMENT ON COLUMN live_room.visibility IS '可见性: PUBLIC/CLASS_ONLY';
COMMENT ON COLUMN live_room.viewer_count IS '当前观看人数';
COMMENT ON COLUMN live_room.peak_viewers IS '峰值观看人数';
COMMENT ON COLUMN live_room.started_at IS '开播时间';
COMMENT ON COLUMN live_room.ended_at IS '结束时间';
COMMENT ON COLUMN live_room.duration IS '直播时长(秒)';
COMMENT ON COLUMN live_room.is_recording IS '是否录制';
COMMENT ON COLUMN live_room.playback_url IS '回放地址';

CREATE INDEX IF NOT EXISTS idx_live_room_host ON live_room(host_user_id);
CREATE INDEX IF NOT EXISTS idx_live_room_status ON live_room(status);
CREATE INDEX IF NOT EXISTS idx_live_room_class ON live_room(class_id);

-- 直播间聊天消息（持久化）
CREATE TABLE IF NOT EXISTS live_room_message (
    id            BIGINT PRIMARY KEY,
    room_id       BIGINT NOT NULL REFERENCES live_room(id),
    sender_id     BIGINT NOT NULL REFERENCES "user"(id),
    content       TEXT NOT NULL,
    message_type  VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    create_time   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete     SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE live_room_message IS '直播间聊天消息';
COMMENT ON COLUMN live_room_message.room_id IS '所属直播间ID';
COMMENT ON COLUMN live_room_message.sender_id IS '发送者用户ID';
COMMENT ON COLUMN live_room_message.content IS '消息内容';
COMMENT ON COLUMN live_room_message.message_type IS '消息类型: TEXT/SYSTEM/GIFT';

CREATE INDEX IF NOT EXISTS idx_live_msg_room ON live_room_message(room_id, create_time);
CREATE INDEX IF NOT EXISTS idx_live_msg_sender ON live_room_message(sender_id);
-- >>> END: 95_livestream.sql <<<

-- >>> BEGIN: 96_call_record.sql <<<

-- 音视频通话记录表

CREATE TABLE IF NOT EXISTS call_record (
    id            BIGINT PRIMARY KEY,
    call_id       VARCHAR(64) NOT NULL UNIQUE,
    caller_id     BIGINT NOT NULL REFERENCES "user"(id),
    callee_id     BIGINT NOT NULL REFERENCES "user"(id),
    media_type    VARCHAR(10) NOT NULL,
    status        VARCHAR(20) NOT NULL,
    mode          VARCHAR(10) NOT NULL DEFAULT 'p2p',
    started_at    TIMESTAMP,
    ended_at      TIMESTAMP,
    duration      INT,
    create_time   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE call_record IS '音视频通话记录';
COMMENT ON COLUMN call_record.call_id IS '通话UUID（Go侧生成）';
COMMENT ON COLUMN call_record.caller_id IS '发起者用户ID';
COMMENT ON COLUMN call_record.callee_id IS '接收者用户ID';
COMMENT ON COLUMN call_record.media_type IS '媒体类型: audio/video';
COMMENT ON COLUMN call_record.status IS '状态: completed/missed/rejected/busy/failed';
COMMENT ON COLUMN call_record.mode IS '通话模式: p2p/turn/sfu';
COMMENT ON COLUMN call_record.started_at IS '通话开始时间';
COMMENT ON COLUMN call_record.ended_at IS '通话结束时间';
COMMENT ON COLUMN call_record.duration IS '通话时长(秒)';

CREATE INDEX IF NOT EXISTS idx_call_record_caller ON call_record(caller_id, create_time DESC);
CREATE INDEX IF NOT EXISTS idx_call_record_callee ON call_record(callee_id, create_time DESC);
-- >>> END: 96_call_record.sql <<<

-- >>> BEGIN: 97_knowledge_base_chunk_config.sql <<<

-- 知识库切分配置增强：新增切分策略、父子chunk模式、元数据保留等字段
-- 执行前请确保 knowledge_base 和 knowledge_chunk 表已存在

-- ===================== knowledge_base 表新增列 =====================

ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS chunk_strategy VARCHAR(32) DEFAULT 'SEMANTIC';
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS parent_child_mode BOOLEAN DEFAULT FALSE;
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS parent_chunk_size INTEGER DEFAULT 1500;
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS preserve_metadata BOOLEAN DEFAULT TRUE;
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS semantic_threshold DOUBLE PRECISION DEFAULT 0.5;

COMMENT ON COLUMN knowledge_base.chunk_strategy IS '切分策略: FIXED/PARAGRAPH/TITLE/SENTENCE/SEMANTIC';
COMMENT ON COLUMN knowledge_base.parent_child_mode IS '是否启用父子chunk模式';
COMMENT ON COLUMN knowledge_base.parent_chunk_size IS '父chunk大小（字符数）';
COMMENT ON COLUMN knowledge_base.preserve_metadata IS '是否保留元数据';
COMMENT ON COLUMN knowledge_base.semantic_threshold IS '语义切分相似度阈值(0~1)';

-- ===================== knowledge_chunk 表新增列 =====================

ALTER TABLE knowledge_chunk ADD COLUMN IF NOT EXISTS parent_chunk_id BIGINT DEFAULT NULL;
ALTER TABLE knowledge_chunk ADD COLUMN IF NOT EXISTS is_parent_chunk BOOLEAN DEFAULT FALSE;
ALTER TABLE knowledge_chunk ADD COLUMN IF NOT EXISTS section_title VARCHAR(512) DEFAULT NULL;

COMMENT ON COLUMN knowledge_chunk.parent_chunk_id IS '父chunk的ID（父子模式下子chunk指向父chunk）';
COMMENT ON COLUMN knowledge_chunk.is_parent_chunk IS '是否为父chunk';
COMMENT ON COLUMN knowledge_chunk.section_title IS '所属章节标题（标题切分/语义切分时提取）';

-- 父chunk索引
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_parent ON knowledge_chunk (parent_chunk_id) WHERE parent_chunk_id IS NOT NULL;
-- 父子模式过滤索引
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_is_parent ON knowledge_chunk (knowledge_base_id, is_parent_chunk) WHERE is_delete = 0;
-- >>> END: 97_knowledge_base_chunk_config.sql <<<

-- >>> BEGIN: 98_knowledge_base_rag_config.sql <<<

-- ============================================================
-- 知识库 RAG 检索配置扩展
-- 新增字段: retrieval_mode, enable_query_rewrite, use_dynamic_top_k
-- 全文检索支持: pg_trgm 扩展 + GIN 索引
-- ============================================================

-- 1. 添加 RAG 检索配置字段到 knowledge_base 表
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS retrieval_mode VARCHAR(32) DEFAULT 'HYBRID_RERANK';
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS enable_query_rewrite BOOLEAN DEFAULT FALSE;
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS use_dynamic_top_k BOOLEAN DEFAULT TRUE;
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS default_top_k INTEGER DEFAULT 5;
ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS query_rewrite_model_id VARCHAR(64) DEFAULT 'dashscope/qwen-turbo';

COMMENT ON COLUMN knowledge_base.retrieval_mode IS '检索模式: VECTOR_ONLY/HYBRID/HYBRID_RERANK';
COMMENT ON COLUMN knowledge_base.enable_query_rewrite IS '是否启用LLM查询改写';
COMMENT ON COLUMN knowledge_base.use_dynamic_top_k IS '是否启用动态topK';
COMMENT ON COLUMN knowledge_base.query_rewrite_model_id IS 'Query改写使用的LLM模型ID，如 dashscope/qwen-turbo';

-- 2. 启用 pg_trgm 扩展（支持 trigram 模糊匹配，用于 BM25 降级）
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 3. 为 knowledge_chunk.content 创建全文检索 GIN 索引
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_content_tsvector
    ON knowledge_chunk USING GIN (to_tsvector('simple', content))
    WHERE is_delete = 0;

-- 4. 为 knowledge_chunk.content 创建 trigram GIN 索引（模糊匹配降级）
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_content_trigram
    ON knowledge_chunk USING GIN (content gin_trgm_ops)
    WHERE is_delete = 0;

-- 5. 更新现有知识库的默认 RAG 配置
UPDATE knowledge_base
SET retrieval_mode = 'HYBRID_RERANK',
    enable_query_rewrite = FALSE,
    use_dynamic_top_k = TRUE
WHERE retrieval_mode IS NULL;
-- >>> END: 98_knowledge_base_rag_config.sql <<<

-- >>> BEGIN: 99_knowledge_base_rerank_model.sql <<<

-- ============================================================
-- 知识库 Rerank 模型配置扩展
-- 新增字段: rerank_model (支持用户选择 Rerank 模型)
-- ============================================================

ALTER TABLE knowledge_base ADD COLUMN IF NOT EXISTS rerank_model VARCHAR(64) DEFAULT 'qwen3-rerank';

COMMENT ON COLUMN knowledge_base.rerank_model IS 'Rerank精排模型: qwen3-rerank / gte-rerank-v2 / qwen3-vl-rerank';

-- 更新现有知识库的默认 Rerank 模型
UPDATE knowledge_base
SET rerank_model = 'qwen3-rerank'
WHERE rerank_model IS NULL;
-- >>> END: 99_knowledge_base_rerank_model.sql <<<

-- >>> BEGIN: 99_seed_data.sql <<<

-- 默认数据种子
-- 此文件应在所有表创建后执行

-- 默认管理员账号
-- 账号: admin
-- 密码: 123456
-- BCrypt hash for "admin123": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO "user" (user_account, user_password, user_name, user_role, level)
VALUES ('admin', '$2a$10$FVszfHXWI0REOV5cB9tvguaQ55ROaklP/OUbb61m0BrWCxuN4Am8a', '系统管理员', 'admin', 99)
ON CONFLICT (user_account) DO NOTHING;

-- 默认测试老师账号
-- 账号: teacher
-- 密码: 123456
INSERT INTO "user" (user_account, user_password, user_name, user_role, level)
VALUES ('teacher', '$2a$10$FVszfHXWI0REOV5cB9tvguaQ55ROaklP/OUbb61m0BrWCxuN4Am8a', '测试讲师', 'teacher', 10)
ON CONFLICT (user_account) DO NOTHING;

-- 默认测试学生账号
-- 账号: student
-- 密码: 123456
INSERT INTO "user" (user_account, user_password, user_name, user_role, level)
VALUES ('student', '$2a$10$FVszfHXWI0REOV5cB9tvguaQ55ROaklP/OUbb61m0BrWCxuN4Am8a', '测试学生', 'student', 1)
ON CONFLICT (user_account) DO NOTHING;

-- teacher 账号 → TEACHER 会员计划（永久有效，状态=生效中）
INSERT INTO user_membership (user_id, plan_id, start_time, expire_time, status)
SELECT u.id, p.id, NOW(), NULL, 1
FROM "user" u, membership_plan p
WHERE u.user_account = 'teacher' AND p.code = 'TEACHER'
ON CONFLICT DO NOTHING;

-- admin 账号 → TEACHER 会员计划（永久有效，状态=生效中）
INSERT INTO user_membership (user_id, plan_id, start_time, expire_time, status)
SELECT u.id, p.id, NOW(), NULL, 1
FROM "user" u, membership_plan p
WHERE u.user_account = 'admin' AND p.code = 'TEACHER'
ON CONFLICT DO NOTHING;

-- teacher 账号 → teacher 表（讲师管理，admin 为创建者）
INSERT INTO teacher (name, introduction, expertise, user_id, admin_id)
SELECT u.user_name, '测试讲师简介', '[]', u.id,
       (SELECT id FROM "user" WHERE user_account = 'admin' LIMIT 1)
FROM "user" u
WHERE u.user_account = 'teacher'
AND NOT EXISTS (SELECT 1 FROM teacher WHERE teacher.user_id = u.id);
-- >>> END: 99_seed_data.sql <<<

-- >>> BEGIN: 100_demo_data.sql <<<

-- =================================================================
-- 平台演示数据（每模块 20 条）
-- 含：仪表盘 30 天趋势、10 名演示讲师（teacher01–teacher10）
-- 幂等：主块若已存在「趣味数学启蒙」课程则跳过；
--       仪表盘/讲师块各自独立幂等，可重复执行
-- =================================================================

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM course WHERE title = '趣味数学启蒙' LIMIT 1) THEN
        RAISE NOTICE 'Demo data already seeded, skipping.';
        RETURN;
    END IF;

    -- =============================================================
    -- 1. 学生用户（20 名）
    -- =============================================================
    INSERT INTO "user" (user_account, user_password, user_name, user_role, level)
    SELECT
        'student' || LPAD(i::text, 2, '0'),
        '$2a$10$FVszfHXWI0REOV5cB9tvguaQ55ROaklP/OUbb61m0BrWCxuN4Am8a',
        names.user_name,
        'student',
        1
    FROM generate_series(1, 20) AS i
    JOIN (VALUES
        (1, '陈思远'), (2, '林雨萱'), (3, '王子涵'), (4, '刘佳怡'), (5, '张子轩'),
        (6, '杨若曦'), (7, '赵一鸣'), (8, '黄诗涵'), (9, '周浩然'), (10, '吴欣妍'),
        (11, '徐天宇'), (12, '孙雨桐'), (13, '马俊杰'), (14, '朱梓涵'), (15, '胡嘉怡'),
        (16, '郭宇航'), (17, '何思琪'), (18, '高子墨'), (19, '罗心怡'), (20, '梁逸飞')
    ) AS names(n, user_name) ON names.n = i
    ON CONFLICT (user_account) DO NOTHING;

    -- =============================================================
    -- 2. 课程（20 门，已发布）
    -- =============================================================
    WITH new_courses AS (
        INSERT INTO course (
            title, subtitle, description, cover_image, price, course_type, difficulty,
            status, teacher_id, total_duration, total_chapters, total_sections,
            student_count, rating_score, tags, admin_id
        )
        SELECT
            titles.title,
            titles.subtitle,
            titles.description,
            'https://picsum.photos/seed/ncourse' || i || '/640/360',
            CASE WHEN i % 3 = 0 THEN 0 ELSE (i % 5 + 1) * 19.9 END,
            CASE WHEN i % 3 = 0 THEN 0 WHEN i % 3 = 1 THEN 1 ELSE 2 END,
            (i % 5) + 1,
            1,
            1,
            45 + i * 3,
            3,
            8,
            50 + i * 15,
            4.0 + (i % 5) * 0.2,
            '["热门","推荐"]',
            1
        FROM generate_series(1, 20) AS i
        JOIN (VALUES
            (1, '趣味数学启蒙', '从生活场景出发学数学', '通过游戏化练习和情景题，帮助孩子建立数感与空间想象能力。'),
            (2, '少儿编程入门', 'Scratch 创意编程', '零基础学习图形化编程，完成动画与小游戏作品。'),
            (3, '英语口语突破', '日常会话实战', '围绕校园与旅行场景，训练听说表达与语音语调。'),
            (4, '科学实验探索', '动手做科学', '覆盖力学、光学与化学小实验，培养观察与记录能力。'),
            (5, '语文阅读写作', '读写结合提升', '精选短文精读，配套仿写训练与作文批改要点。'),
            (6, '物理力学基础', '力学核心概念', '系统讲解力、运动与压强，配合典型例题演练。'),
            (7, '化学元素探秘', '走进微观世界', '认识常见元素与反应规律，建立化学方程式基础。'),
            (8, '历史文明之旅', '纵览华夏文明', '按时间线梳理重要朝代与人物，培养史料分析能力。'),
            (9, '地理世界漫游', '认识我们的地球', '地图判读、气候与地形专题，联系生活实际理解。'),
            (10, '生物生命奥秘', '探索生命现象', '细胞、遗传与生态系统入门，注重实验与图示。'),
            (11, '音乐艺术欣赏', '聆听经典之声', '了解中外名曲与乐器，提升审美与乐理基础。'),
            (12, '美术创意绘画', '色彩与构图', '从素描到水彩，分步骤完成主题创作。'),
            (13, '信息技术应用', '数字素养提升', '办公软件、信息检索与网络安全实用技能。'),
            (14, '逻辑思维训练', '推理与建模', '通过数独、逻辑题与思维导图训练结构化思考。'),
            (15, '英语语法精讲', '语法体系梳理', '时态、从句与非谓语动词系统讲解与练习。'),
            (16, '古诗词鉴赏', '品读诗词之美', '名家名篇赏析，掌握意象、手法与背诵技巧。'),
            (17, '奥数思维拓展', '竞赛思维入门', '数论、几何与组合经典题型思路拆解。'),
            (18, '机器人制作', '创客实践课', '从结构设计到简单编程，完成可动机器人模型。'),
            (19, '演讲与口才', '自信表达训练', '演讲结构、肢体语言与即兴发言专项训练。'),
            (20, '学习方法指导', '高效学习策略', '时间管理、笔记方法与复习规划一站式指导。')
        ) AS titles(n, title, subtitle, description) ON titles.n = i
        RETURNING id
    ),
    new_chapters AS (
        INSERT INTO course_chapter (course_id, title, description, sort, admin_id)
        SELECT id, '第一章 基础入门', '本章介绍课程核心概念与学习方法', 1, 1
        FROM new_courses
        RETURNING id, course_id
    )
    INSERT INTO course_section (
        course_id, chapter_id, title, description, duration, sort, is_free, admin_id
    )
    SELECT nc.course_id, nc.id, '第1节 课程导学', '了解课程目标与学习路径', 600, 1, 1, 1
    FROM new_chapters nc;

    -- =============================================================
    -- 3. 每日单词（20 条）
    -- =============================================================
    INSERT INTO daily_word (
        word, pronunciation_us, pronunciation_uk, translation,
        example, example_translation, difficulty, category, publish_date, admin_id
    )
    SELECT * FROM (VALUES
        ('abandon',  '/əˈbændən/', '/əˈbændən/', '放弃；遗弃', 'Never abandon your dreams.', '永远不要放弃梦想。', 2, '四级', CURRENT_DATE, 1),
        ('benefit',  '/ˈbenɪfɪt/', '/ˈbenɪfɪt/', '利益；好处', 'Exercise benefits your health.', '锻炼有益健康。', 1, '四级', CURRENT_DATE, 1),
        ('challenge','/ˈtʃælɪndʒ/', '/ˈtʃælɪndʒ/', '挑战', 'Face every challenge bravely.', '勇敢面对每一个挑战。', 2, '四级', CURRENT_DATE, 1),
        ('diligent', '/ˈdɪlɪdʒənt/', '/ˈdɪlɪdʒənt/', '勤奋的', 'She is a diligent student.', '她是一个勤奋的学生。', 2, '四级', CURRENT_DATE, 1),
        ('efficient','/ɪˈfɪʃnt/', '/ɪˈfɪʃnt/', '高效的', 'Use time efficiently.', '高效利用时间。', 2, '四级', CURRENT_DATE, 1),
        ('foundation','/faʊnˈdeɪʃn/', '/faʊnˈdeɪʃn/', '基础', 'Build a solid foundation.', '打下坚实基础。', 3, '四级', CURRENT_DATE, 1),
        ('gratitude','/ˈɡrætɪtjuːd/', '/ˈɡrætɪtjuːd/', '感激', 'Express gratitude daily.', '每天表达感激。', 3, '四级', CURRENT_DATE, 1),
        ('harmony',  '/ˈhɑːməni/', '/ˈhɑːməni/', '和谐', 'Live in harmony with nature.', '与自然和谐相处。', 2, '四级', CURRENT_DATE, 1),
        ('inspire',  '/ɪnˈspaɪə/', '/ɪnˈspaɪə/', '激励', 'Teachers inspire students.', '老师激励学生。', 2, '四级', CURRENT_DATE, 1),
        ('journey',  '/ˈdʒɜːni/', '/ˈdʒɜːni/', '旅程', 'Learning is a lifelong journey.', '学习是一生的旅程。', 1, '四级', CURRENT_DATE, 1),
        ('knowledge','/ˈnɒlɪdʒ/', '/ˈnɒlɪdʒ/', '知识', 'Knowledge is power.', '知识就是力量。', 1, '四级', CURRENT_DATE, 1),
        ('logical',  '/ˈlɒdʒɪkl/', '/ˈlɒdʒɪkl/', '逻辑的', 'Think in a logical way.', '用逻辑方式思考。', 2, '四级', CURRENT_DATE, 1),
        ('motivate', '/ˈməʊtɪveɪt/', '/ˈməʊtɪveɪt/', '激励', 'Goals motivate us to work hard.', '目标激励我们努力。', 2, '四级', CURRENT_DATE, 1),
        ('nurture',  '/ˈnɜːtʃə/', '/ˈnɜːtʃə/', '培养', 'Nurture good habits early.', '尽早培养好习惯。', 3, '四级', CURRENT_DATE, 1),
        ('optimistic','/ˌɒptɪˈmɪstɪk/', '/ˌɒptɪˈmɪstɪk/', '乐观的', 'Stay optimistic about the future.', '对未来保持乐观。', 3, '四级', CURRENT_DATE, 1),
        ('persevere','/ˌpɜːsɪˈvɪə/', '/ˌpɜːsɪˈvɪə/', '坚持', 'Persevere through difficulties.', '在困难中坚持。', 3, '四级', CURRENT_DATE, 1),
        ('quality',  '/ˈkwɒləti/', '/ˈkwɒləti/', '质量', 'Quality matters more than speed.', '质量比速度更重要。', 1, '四级', CURRENT_DATE, 1),
        ('resilient','/rɪˈzɪliənt/', '/rɪˈzɪliənt/', '有韧性的', 'Be resilient after failure.', '失败后要有韧性。', 3, '四级', CURRENT_DATE, 1),
        ('strategy', '/ˈstrætədʒi/', '/ˈstrætədʒi/', '策略', 'Plan your study strategy.', '规划你的学习策略。', 2, '四级', CURRENT_DATE, 1),
        ('wisdom',   '/ˈwɪzdəm/', '/ˈwɪzdəm/', '智慧', 'Wisdom comes from experience.', '智慧来自经验。', 2, '四级', CURRENT_DATE, 1)
    ) AS w(word, pronunciation_us, pronunciation_uk, translation, example, example_translation, difficulty, category, publish_date, admin_id)
    ON CONFLICT DO NOTHING;

    -- =============================================================
    -- 4. 每日文章（20 篇）
    -- =============================================================
    INSERT INTO daily_article (
        title, content, summary, cover_image, author, source, category,
        tags, difficulty, read_time, publish_date, admin_id, view_count, like_count
    )
    SELECT
        t.title,
        t.content,
        t.summary,
        'https://picsum.photos/seed/narticle' || i || '/640/360',
        '鳗螈.星云课堂',
        '鳗螈.星云课堂',
        t.category,
        '["学习","阅读"]',
        (i % 3) + 1,
        5 + (i % 8),
        CURRENT_DATE - ((i - 1) % 7),
        1,
        100 + i * 20,
        10 + i * 3
    FROM generate_series(1, 20) AS i
    JOIN (VALUES
        (1, '春天的校园', '生活', '三月校园里樱花开满枝头，同学们在操场上奔跑欢笑。', '描写春日校园景象，感受季节更替与自然之美。'),
        (2, '科技改变生活', '科技', '智能手机、在线支付与远程会议，让我们的生活更加便捷。', '从日常场景看科技如何重塑学习与生活节奏。'),
        (3, '传统文化之美', '文化', '书法、国画与戏曲，承载着中华民族千年的审美与智慧。', '走近非遗与传统艺术，理解文化自信的来源。'),
        (4, '高效学习的方法', '技能', '番茄钟、费曼技巧与间隔复习，是被验证有效的学习策略。', '分享可立即实践的时间管理与复习方法。'),
        (5, '人工智能与教育', '科技', 'AI 助教、智能批改与个性化推荐，正在重塑课堂形态。', '探讨人工智能如何辅助而非替代教师。'),
        (6, '阅读的力量', '技能', '一本好书能打开一扇窗，让我们在文字里遇见更广阔的世界。', '阅读如何拓展词汇量、提升写作与思辨能力。'),
        (7, '运动与健康', '生活', '每天三十分钟中等强度运动，有助于提升专注力与睡眠质量。', '科学运动习惯对青少年身心发展的益处。'),
        (8, '环保从我做起', '生活', '垃圾分类、节约用水与绿色出行，每个人都能为地球尽一份力。', '校园与家庭可参与的低碳环保行动。'),
        (9, '数学之美', '技能', '从黄金分割到对称图形，数学藏在建筑、音乐与自然之中。', '发现数学在艺术与生活中的优雅表达。'),
        (10, '英语学习的乐趣', '技能', '看英文动画、听英文歌曲，让语言学习不再枯燥。', '趣味输入与输出结合，提升语感与自信。'),
        (11, '历史上的今天', '文化', '回望重要历史事件，理解时代背景与人物选择。', '以史为鉴，培养客观理性的历史观。'),
        (12, '艺术与生活', '艺术', '一幅画、一段旋律，都能为平凡日子增添色彩与温度。', '艺术欣赏入门，提升审美与感受力。'),
        (13, '探索宇宙奥秘', '科技', '从月球到火星，人类对宇宙的探索从未停止。', '航天成就与基础天文知识科普。'),
        (14, '友谊的真谛', '生活', '真正的朋友会在你困难时伸出援手，也会分享你的喜悦。', '如何建立真诚、互相尊重的同伴关系。'),
        (15, '时间管理技巧', '技能', '制定优先级清单，把精力留给最重要的事。', '学生适用的时间规划与拖延应对技巧。'),
        (16, '中华诗词欣赏', '文化', '床前明月光，疑是地上霜——经典诗句穿越千年仍动人。', '品读唐诗宋词，体会意象与韵律之美。'),
        (17, '科学小实验', '科技', '用硬币与玻璃杯也能做有趣实验，观察表面张力现象。', '家庭可完成的简单科学实验与观察记录。'),
        (18, '成长的故事', '生活', '每一次失败都是成长的台阶，关键在于复盘与坚持。', '青春期面对挫折时的心态调整与自我激励。'),
        (19, '创新思维培养', '技能', '多问「为什么」与「还能怎样」，打破思维定式。', '课堂与项目中的创造性问题解决方法。'),
        (20, '梦想与坚持', '生活', '梦想需要行动浇灌，把大目标拆成每天可完成的小步。', '设定学习目标并持续追踪进度的实用建议。')
    ) AS t(n, title, category, content, summary) ON t.n = i;

    -- =============================================================
    -- 5. 公告（20 条，已发布）
    -- =============================================================
    INSERT INTO announcement (title, content, sort, status, admin_id, view_count)
    SELECT
        t.title,
        t.content,
        20 - i,
        1,
        1,
        i * 10
    FROM generate_series(1, 20) AS i
    JOIN (VALUES
        (1, '平台升级通知', '为提升使用体验，平台将于本周日凌晨 2:00-4:00 进行系统升级，期间部分功能可能短暂不可用。'),
        (2, '新学期课程上线', '春季学期精品课程已上架，涵盖语数外及科学素养专题，欢迎选课学习。'),
        (3, '会员优惠活动', '即日起至月底，年度会员限时八折，开通即享专属课程与学习报告。'),
        (4, 'AI功能更新说明', '智慧体中心新增多学科助教，支持知识库检索与连续对话记忆。'),
        (5, '寒假学习计划发布', '各学科寒假复习清单已发布，可按年级下载打印执行。'),
        (6, '教师培训通知', '本周六举办「AI 辅助备课」线上培训，请任课教师准时参加。'),
        (7, '系统维护公告', '数据库例行维护已完成，如遇异常请及时通过反馈中心联系我们。'),
        (8, '安全使用提醒', '请勿向他人泄露账号密码，警惕仿冒链接与诈骗信息。'),
        (9, '优质课程推荐', '编辑部精选本周热门课程，欢迎同学们按需选学。'),
        (10, '学习打卡活动', '连续打卡 21 天可领取学习勋章，活动详情见学习圈置顶帖。'),
        (11, '电子书库上新', '经典名著专区新增 20 本电子书，支持在线阅读与笔记。'),
        (12, '社群活动预告', '下周六晚 8 点举办线上读书分享会，欢迎报名参与。'),
        (13, '考试备考指南', '期中备考专题已上线，含各科复习要点与模拟练习。'),
        (14, '编程大赛通知', '校园编程挑战赛报名开启，初赛采用线上提交作品形式。'),
        (15, '英语口语角开放', '每周三晚英语口语角开放，外教与学长学姐在线陪练。'),
        (16, '作业提交须知', '智能批改支持 JPG/PNG 格式，单张图片不超过 10MB。'),
        (17, '隐私政策更新', '我们更新了隐私政策，请家长与学生查阅并知悉相关条款。'),
        (18, '用户反馈回复', '上月反馈的问题已处理完毕，处理结果可在消息中心查看。'),
        (19, '端午节放假安排', '端午节期间客服响应时间调整为 10:00-18:00，祝节日快乐。'),
        (20, '暑期课程预告', '暑期特训营课程表即将发布，敬请关注首页轮播与公告。')
    ) AS t(n, title, content) ON t.n = i;

    -- =============================================================
    -- 6. 轮播图（20 条，已发布）
    -- =============================================================
    INSERT INTO banner (title, image_url, link_type, link_url, sort, status, admin_id)
    SELECT
        t.title,
        'https://picsum.photos/seed/nbanner' || i || '/1200/400',
        1,
        '/courses',
        20 - i,
        1,
        1
    FROM generate_series(1, 20) AS i
    JOIN (VALUES
        (1, 'AI学习新天地'), (2, '热门课程推荐'), (3, '每日单词打卡'),
        (4, '精品电子书'), (5, '智能批改上线'), (6, '学习圈子开放'),
        (7, '暑期特训营'), (8, '编程启蒙课'), (9, '英语口语突破'),
        (10, '数学思维训练'), (11, '科学实验课'), (12, '会员专享福利'),
        (13, '每日美文阅读'), (14, '考试备考专区'), (15, '名师直播课'),
        (16, '学习工具推荐'), (17, '新生入学指南'), (18, '教师入驻招募'),
        (19, '社区活动精选'), (20, '年终学习报告')
    ) AS t(n, title) ON t.n = i;

    -- =============================================================
    -- 7. 学习圈帖子（20 条）
    -- =============================================================
    INSERT INTO post (title, content, tags, thumb_num, favour_num, comment_num, user_id, post_type)
    SELECT
        t.title,
        t.content,
        '["学习","交流"]',
        i * 3,
        i * 2,
        i,
        ((i - 1) % 20) + 4,
        t.post_type
    FROM generate_series(1, 20) AS i
    JOIN (VALUES
        (1, '数学难题求助', '三角形全等这题卡了半天，辅助线应该怎么作？有同学能讲讲思路吗。', 'question'),
        (2, '分享学习笔记', '把本周英语 Unit 3 的时态对比整理成表格了，需要的同学可以收藏。', 'share'),
        (3, '编程学习心得', '学完 Python 列表和字典，做小项目比刷题更有成就感。', 'experience'),
        (4, '推荐一本好书', '《如何阅读一本书》对做读书笔记很有帮助，推荐给爱阅读的同学。', 'share'),
        (5, '期末考试复习', '分享我的期末复习时间表：上午主攻理科，下午语文英语交替。', 'exam'),
        (6, '英语口语练习', '想组个每晚 20 分钟的口语小组，互相纠正发音，有兴趣的留言。', 'discussion'),
        (7, '物理实验记录', '今天做了光的折射实验，入射角增大时折射角也增大，记录附上图。', 'experience'),
        (8, '作文素材积累', '整理了 20 个描写秋天的句子，写记叙文可以直接套用。', 'share'),
        (9, '学习方法讨论', '番茄工作法用了两周，专注力确实有提升，大家还有什么好方法？', 'discussion'),
        (10, '课程推荐', '《趣味数学启蒙》讲得很清楚，适合基础薄弱的同学查漏补缺。', 'course'),
        (11, '每日打卡', '连续学习打卡第 30 天，小目标达成，继续加油。', 'experience'),
        (12, '历史知识问答', '唐朝除了李白杜甫，还有哪些值得背的代表诗人？', 'question'),
        (13, '化学方程式', '总结了氧化还原反应配平的四步法，附三道典型例题。', 'share'),
        (14, '课间闲聊', '周末打算去图书馆自习，有一起的同学吗？', 'life'),
        (15, '工具推荐', '推荐三款做思维导图和背单词的 App，亲测好用。', 'tool'),
        (16, '生物实验报告', '显微镜下观察洋葱表皮细胞，细胞壁和细胞核看得很清楚。', 'homework'),
        (17, '升学咨询', '高二选科在物化生与史地政之间犹豫，学长学姐能给点建议吗？', 'exam'),
        (18, '音乐学习分享', '学钢琴三个月，终于能完整弹一首《致爱丽丝》了。', 'experience'),
        (19, '活动通知', '下周六晚 8 点线上读书会，共读《小王子》，欢迎参加。', 'announcement'),
        (20, '综合讨论', '大家觉得 AI 批改作文靠谱吗？哪些科目最适合用 AI 辅助？', 'discussion')
    ) AS t(n, title, content, post_type) ON t.n = i;

    -- =============================================================
    -- 8. 电子书（20 本）
    -- =============================================================
    WITH new_books AS (
        INSERT INTO book (title, author, cover_url, origin_file_url, file_type, status, total_chapters, word_count, admin_id)
        SELECT
            t.title,
            t.author,
            'https://picsum.photos/seed/nbook' || i || '/300/400',
            'https://cdn.example.com/ebooks/' || i || '.epub',
            'EPUB',
            2,
            5,
            50000 + i * 1000,
            1
        FROM generate_series(1, 20) AS i
        JOIN (VALUES
            (1, '小王子', '圣埃克苏佩里'), (2, '西游记', '吴承恩'), (3, '三国演义', '罗贯中'),
            (4, '水浒传', '施耐庵'), (5, '红楼梦', '曹雪芹'), (6, '格林童话', '格林兄弟'),
            (7, '安徒生童话', '安徒生'), (8, '伊索寓言', '伊索'), (9, '海底两万里', '凡尔纳'),
            (10, '鲁滨逊漂流记', '笛福'), (11, '汤姆索亚历险记', '马克吐温'), (12, '昆虫记', '法布尔'),
            (13, '钢铁是怎样炼成的', '奥斯特洛夫斯基'), (14, '简爱', '夏洛蒂'), (15, '傲慢与偏见', '奥斯汀'),
            (16, '老人与海', '海明威'), (17, '朝花夕拾', '鲁迅'), (18, '骆驼祥子', '老舍'),
            (19, '围城', '钱钟书'), (20, '平凡的世界', '路遥')
        ) AS t(n, title, author) ON t.n = i
        RETURNING id
    )
    INSERT INTO chapter (book_id, title, chapter_index, word_count, content)
    SELECT id, '第一章', 0, 5000,
        '<p>清晨的阳光透过窗棂洒在书页上，故事从这里缓缓展开。请继续阅读，感受文字带来的想象与力量。</p>'
    FROM new_books;

    -- =============================================================
    -- 9. 班级（20 个）
    -- =============================================================
    WITH new_classes AS (
        INSERT INTO class_info (class_name, description, creator_id)
        SELECT
            t.class_name,
            t.description,
            1
        FROM (VALUES
            (1, '初一(1)班', '初一常规教学班，每周发布周练与阅读任务。'),
            (2, '初一(2)班', '注重小组合作学习，定期开展主题讨论。'),
            (3, '初二(1)班', '初二理科强化班，配套实验视频与习题讲解。'),
            (4, '初二(2)班', '读写并重，每月一次作文面批。'),
            (5, '初三(1)班', '中考备考班，按专题推进一轮复习。'),
            (6, '高一(1)班', '高一文理基础课程班级，学业规划指导。'),
            (7, '高一(2)班', '英语提升重点班，增设口语训练环节。'),
            (8, '高二(1)班', '高二选科教学班，物理方向。'),
            (9, '高二(2)班', '高二选科教学班，历史方向。'),
            (10, '高三(1)班', '高考冲刺班，周测与错题复盘。'),
            (11, '奥数提高班', '竞赛思维训练，每周两次专题课。'),
            (12, '英语提高班', '词汇、阅读与写作专项提升。'),
            (13, '编程兴趣班', 'Python 项目实战，每学期完成一个作品。'),
            (14, '科学实验班', '动手实验与探究报告并重。'),
            (15, '阅读写作班', '名著精读与记叙文写作训练。'),
            (16, '数学竞赛班', '数论与几何竞赛题型训练。'),
            (17, '物理竞赛班', '力学与电磁学竞赛进阶。'),
            (18, '化学实验班', '实验操作规范与方程式专题。'),
            (19, '历史研讨班', '史料阅读与论述题训练。'),
            (20, '艺术特长班', '音乐美术跨学科素养拓展。')
        ) AS t(n, class_name, description)
        RETURNING id
    )
    INSERT INTO class_member (class_id, user_id, role)
    SELECT nc.id, 3, 'student'
    FROM new_classes nc;

    -- =============================================================
    -- 10. 题库（20 道）
    -- =============================================================
    INSERT INTO question (type, subject, grade, difficulty, content, options, answer, explanation, creator_id)
    SELECT
        'SINGLE_CHOICE',
        subjects.subject,
        'GRADE_8',
        (i % 5) + 1,
        subjects.content,
        '[{"label":"A","text":"选项A"},{"label":"B","text":"选项B"},{"label":"C","text":"选项C"},{"label":"D","text":"选项D"}]'::jsonb,
        'A',
        subjects.explanation,
        1
    FROM generate_series(1, 20) AS i
    JOIN (VALUES
        (1, 'MATH', '下列哪个数是质数？', '质数只有 1 和自身两个正因数，注意与合数区分。'),
        (2, 'MATH', '一元一次方程 2x+3=7 的解是？', '移项后得 2x=4，因此 x=2。'),
        (3, 'CHINESE', '下列词语中加点字读音正确的是？', '多音字需结合语境判断，注意声调和轻声。'),
        (4, 'ENGLISH', '选择正确的时态填空。', '根据时间状语判断一般现在时、过去时或完成时。'),
        (5, 'PHYSICS', '物体做匀速直线运动的条件是？', '合外力为零或合力与运动方向共线且大小恒定。'),
        (6, 'CHEMISTRY', '下列物质属于纯净物的是？', '纯净物由一种物质组成，混合物由多种物质组成。'),
        (7, 'BIOLOGY', '细胞的基本结构不包括？', '细胞壁并非所有细胞都具有，动物细胞无细胞壁。'),
        (8, 'HISTORY', '秦始皇统一六国的时间是？', '公元前 221 年秦灭六国，建立中央集权制度。'),
        (9, 'GEOGRAPHY', '我国最长的河流是？', '长江全长约 6300 千米，为我国第一长河。'),
        (10, 'MATH', '三角形内角和等于？', '平面三角形内角和恒为 180°。'),
        (11, 'CHINESE', '下列句子没有语病的是？', '检查搭配不当、成分残缺与语序问题。'),
        (12, 'ENGLISH', '选择正确的介词。', '介词搭配需记忆常见固定用法。'),
        (13, 'PHYSICS', '牛顿第一定律又称为？', '也称惯性定律，描述力与运动状态改变的关系。'),
        (14, 'CHEMISTRY', '水的化学式是？', '一个水分子由两个氢原子和一个氧原子构成。'),
        (15, 'BIOLOGY', '光合作用的场所是？', '叶绿体中含有叶绿素，是光合作用主要场所。'),
        (16, 'HISTORY', '鸦片战争爆发的年份是？', '1840 年英国发动侵华战争，中国开始沦为半殖民地半封建社会。'),
        (17, 'GEOGRAPHY', '我国的首都是？', '北京是中华人民共和国首都。'),
        (18, 'MATH', '勾股定理的表达式是？', '直角三角形两直角边的平方和等于斜边的平方。'),
        (19, 'CHINESE', '下列成语使用正确的是？', '成语使用需符合对象、程度与感情色彩。'),
        (20, 'ENGLISH', '选择正确的单词填空。', '根据句意与语法形式选择恰当词汇。')
    ) AS subjects(n, subject, content, explanation) ON subjects.n = i;

    -- =============================================================
    -- 11. 试卷（20 份）
    -- =============================================================
    INSERT INTO exam_paper (title, subtitle, subject, grade, total_score, duration_min, status, creator_id)
    SELECT
        t.title,
        '满分100分 · 考试时间90分钟',
        t.subject,
        'GRADE_8',
        100,
        90,
        'PUBLISHED',
        1
    FROM (VALUES
        (1, '八年级数学期中试卷', 'MATH'), (2, '八年级语文期中试卷', 'CHINESE'),
        (3, '八年级英语期中试卷', 'ENGLISH'), (4, '八年级物理期中试卷', 'PHYSICS'),
        (5, '八年级化学期中试卷', 'CHEMISTRY'), (6, '八年级生物期中试卷', 'BIOLOGY'),
        (7, '八年级历史期中试卷', 'HISTORY'), (8, '八年级地理期中试卷', 'GEOGRAPHY'),
        (9, '八年级数学期末试卷', 'MATH'), (10, '八年级语文期末试卷', 'CHINESE'),
        (11, '八年级英语期末试卷', 'ENGLISH'), (12, '八年级物理期末试卷', 'PHYSICS'),
        (13, '八年级化学期末试卷', 'CHEMISTRY'), (14, '八年级生物期末试卷', 'BIOLOGY'),
        (15, '八年级历史期末试卷', 'HISTORY'), (16, '八年级地理期末试卷', 'GEOGRAPHY'),
        (17, '八年级综合测试卷一', 'MATH'), (18, '八年级综合测试卷二', 'CHINESE'),
        (19, '八年级月考试卷', 'ENGLISH'), (20, '八年级模拟考试卷', 'MATH')
    ) AS t(n, title, subject);

    -- =============================================================
    -- 12. AI 助手（20 个，已发布）
    -- =============================================================
    INSERT INTO ai_assistant (
        name, description, system_prompt, opening_message, model_name, model_base_url,
        status, is_public, creator_id, category, tags
    )
    SELECT
        t.name,
        t.description,
        '你是一位专业的' || t.name || '，请用简洁易懂的语言回答学生的问题。',
        '你好！我是' || t.name || '，有什么可以帮你的吗？',
        'deepseek/deepseek-chat',
        'https://api.deepseek.com/v1',
        'PUBLISHED',
        1,
        1,
        t.category,
        '["学习","AI"]'::jsonb
    FROM (VALUES
        (1, '数学小助手', '帮你解答数学难题', '数学'),
        (2, '英语陪练', '英语口语与语法辅导', '英语'),
        (3, '作文批改师', '语文作文润色与点评', '语文'),
        (4, '物理实验顾问', '物理概念与实验指导', '物理'),
        (5, '化学方程式助手', '化学方程式配平与解析', '化学'),
        (6, '历史知识百科', '历史事件与人物解读', '历史'),
        (7, '地理探索向导', '地理知识与地图解读', '地理'),
        (8, '生物学习伙伴', '生物概念与实验辅导', '生物'),
        (9, '编程入门导师', 'Python/JavaScript入门指导', '编程'),
        (10, '学习方法教练', '科学学习方法指导', '综合'),
        (11, '考试备考顾问', '考试策略与复习规划', '综合'),
        (12, '古诗词鉴赏师', '古诗词解读与赏析', '语文'),
        (13, '阅读理解助手', '文章分析与理解训练', '语文'),
        (14, '单词记忆教练', '高效单词记忆方法', '英语'),
        (15, '科学实验助手', '科学实验设计与分析', '科学'),
        (16, '逻辑思维训练师', '逻辑思维与推理训练', '综合'),
        (17, '演讲口才教练', '演讲技巧与表达训练', '综合'),
        (18, '时间管理顾问', '学习计划与时间管理', '综合'),
        (19, '心理辅导伙伴', '学习压力疏导与激励', '综合'),
        (20, '全能学习助手', '多学科综合学习辅导', '综合')
    ) AS t(n, name, description, category);

    -- =============================================================
    -- 13. 知识库（20 个）
    -- =============================================================
    INSERT INTO knowledge_base (name, description, creator_id, status)
    SELECT
        t.name,
        t.description,
        1,
        'ACTIVE'
    FROM (VALUES
        (1, '数学知识库', '收录代数、几何与竞赛专题讲义。'),
        (2, '英语知识库', '词汇、语法与阅读理解训练资料。'),
        (3, '语文知识库', '作文素材、古诗词与名著导读。'),
        (4, '物理知识库', '力学、电学与实验操作要点。'),
        (5, '化学知识库', '元素周期表、方程式与实验安全须知。'),
        (6, '生物知识库', '细胞、遗传与生态系统专题。'),
        (7, '历史知识库', '中外通史时间轴与史料分析范例。'),
        (8, '地理知识库', '地形气候、区域地理与地图技能。'),
        (9, '编程知识库', 'Python 语法、项目案例与调试技巧。'),
        (10, '学习方法库', '时间管理、笔记方法与复习策略。'),
        (11, '考试真题库', '近年期中、期末与模拟试题汇编。'),
        (12, '古诗词库', '必背篇目注释、赏析与默写训练。'),
        (13, '科学实验库', '家庭与课堂可做的实验方案。'),
        (14, '阅读理解库', '记叙文、说明文阅读技巧与真题。'),
        (15, '写作素材库', '名言、事例与优美段落分类整理。'),
        (16, '逻辑思维库', '推理题、数独与思维训练题集。'),
        (17, '演讲技巧库', '演讲稿结构、范例与点评要点。'),
        (18, '时间管理库', '日程模板、番茄钟与目标拆解方法。'),
        (19, '心理辅导库', '压力管理、情绪调节与积极暗示。'),
        (20, '综合教学库', '跨学科教案与拓展阅读推荐。')
    ) AS t(n, name, description);

    -- =============================================================
    -- 14. 聊天群组（20 个）
    -- =============================================================
    WITH new_groups AS (
        INSERT INTO chat_group (group_number, group_name, description, owner_id, member_count)
        SELECT
            '10000' || LPAD(i::text, 2, '0'),
            t.name,
            t.description,
            1,
            2
        FROM generate_series(1, 20) AS i
        JOIN (VALUES
            (1, '数学交流群', '讨论难题与分享解题思路。'),
            (2, '英语学习群', '每日一句、口语打卡与作文互评。'),
            (3, '编程学习群', '项目答疑与代码评审。'),
            (4, '物理讨论群', '力学电学专题交流与实验分享。'),
            (5, '化学实验群', '方程式配平与实验现象讨论。'),
            (6, '历史爱好者群', '史料共读与专题研讨。'),
            (7, '地理探索群', '地图判读与区域地理讨论。'),
            (8, '生物学习群', '细胞遗传与实验报告交流。'),
            (9, '语文阅读群', '名著共读与写作素材分享。'),
            (10, '考试备考群', '复习计划、真题讨论与心态调整。'),
            (11, '学习方法群', '效率工具与习惯养成交流。'),
            (12, '科学实验群', '家庭小实验与安全注意事项。'),
            (13, '作文写作群', '周更练笔与互改反馈。'),
            (14, '奥数竞赛群', '竞赛题讨论与备赛经验。'),
            (15, '英语口语群', '每晚口语打卡与纠音。'),
            (16, '编程Python群', 'Python 作业与项目协作。'),
            (17, '高考交流群', '高三学长学姐经验分享。'),
            (18, '中考交流群', '初三备考资料与志愿讨论。'),
            (19, '每日打卡群', '学习打卡监督与互相鼓励。'),
            (20, '综合学习群', '各学科综合交流与资源分享。')
        ) AS t(n, name, description) ON t.n = i
        RETURNING id, owner_id
    )
    INSERT INTO chat_group_member (group_id, user_id, role)
    SELECT ng.id, ng.owner_id, 2 FROM new_groups ng
    UNION ALL
    SELECT ng.id, 3, 0 FROM new_groups ng;

    -- =============================================================
    -- 15. 用户课表（管理员个人课表 20 节课）
    -- =============================================================
    INSERT INTO class_schedule_item (
        user_id, course_type, course_name, teacher_name, location,
        day_of_week, start_section, end_section, color
    )
    SELECT
        1,
        1,
        course_name,
        teacher_name,
        location,
        day_of_week,
        start_section,
        end_section,
        color
    FROM (VALUES
        ('语文', '王老师', '教学楼201', 1, 1, 2, '#3B82F6'),
        ('数学', '李老师', '教学楼202', 1, 3, 4, '#10B981'),
        ('英语', '张老师', '教学楼203', 1, 5, 6, '#8B5CF6'),
        ('物理', '刘老师', '实验楼101', 2, 1, 2, '#F59E0B'),
        ('化学', '陈老师', '实验楼102', 2, 3, 4, '#EF4444'),
        ('生物', '赵老师', '实验楼103', 2, 5, 6, '#06B6D4'),
        ('历史', '孙老师', '教学楼301', 3, 1, 2, '#EC4899'),
        ('地理', '周老师', '教学楼302', 3, 3, 4, '#14B8A6'),
        ('政治', '吴老师', '教学楼303', 3, 5, 6, '#F97316'),
        ('体育', '郑老师', '体育馆', 4, 1, 2, '#22C55E'),
        ('音乐', '钱老师', '艺术楼101', 4, 3, 4, '#A855F7'),
        ('美术', '冯老师', '艺术楼102', 4, 5, 6, '#EAB308'),
        ('信息技术', '褚老师', '机房201', 5, 1, 2, '#0EA5E9'),
        ('班会', '班主任', '教学楼201', 5, 3, 3, '#64748B'),
        ('语文', '王老师', '教学楼201', 5, 5, 6, '#3B82F6'),
        ('数学', '李老师', '教学楼202', 1, 7, 8, '#10B981'),
        ('英语', '张老师', '教学楼203', 2, 7, 8, '#8B5CF6'),
        ('物理', '刘老师', '实验楼101', 3, 7, 8, '#F59E0B'),
        ('化学', '陈老师', '实验楼102', 4, 7, 8, '#EF4444'),
        ('自习', '值班老师', '教学楼201', 5, 7, 8, '#94A3B8')
    ) AS s(course_name, teacher_name, location, day_of_week, start_section, end_section, color);

    -- =============================================================
    -- 16. 用户反馈（20 条）
    -- =============================================================
    INSERT INTO user_feedback (user_id, feedback_type, title, content, status)
    SELECT
        ((i - 1) % 20) + 4,
        t.feedback_type,
        t.title,
        t.content,
        i % 3
    FROM generate_series(1, 20) AS i
    JOIN (VALUES
        (1, '功能建议', '希望增加深色模式', '夜间学习时希望有深色主题，并支持跟随系统自动切换。'),
        (2, 'Bug反馈', '视频播放卡顿', '部分课程视频在 Wi-Fi 环境下仍会缓冲较久，建议优化 CDN。'),
        (3, '功能建议', '增加离线下载', '通勤时网络不稳定，希望支持课程视频离线下载。'),
        (4, '内容建议', '增加更多编程课程', '对 Python 和 Web 开发很感兴趣，希望增加项目实战课。'),
        (5, 'Bug反馈', '打卡功能异常', '昨天打卡成功但统计页未更新，今天恢复正常了。'),
        (6, '功能建议', '优化搜索功能', '搜索课程时希望支持按年级和学科组合筛选。'),
        (7, '内容建议', '增加高考真题', '希望有按省份分类的高考真题与详解。'),
        (8, 'Bug反馈', '消息通知延迟', '群聊消息偶尔延迟一两分钟才收到推送。'),
        (9, '功能建议', '增加学习报告', '希望每周生成学习时长与完成课程的可视化报告。'),
        (10, '内容建议', '增加日语课程', '计划自学日语，希望平台能提供入门课程。'),
        (11, 'Bug反馈', '作业上传失败', '上传 8MB 作业图片时提示失败，压缩后可以上传。'),
        (12, '功能建议', '增加家长端', '希望家长能查看学习进度但不干扰孩子账号。'),
        (13, '内容建议', '增加物理实验视频', '力学实验如果能配慢动作演示会更好理解。'),
        (14, 'Bug反馈', '页面加载慢', '首页在高峰时段偶尔需要 5 秒以上才能完全加载。'),
        (15, '功能建议', '增加学习提醒', '希望可自定义每日学习提醒时间与重复规则。'),
        (16, '内容建议', '增加古诗词朗诵', '背诵古诗词时希望有标准朗诵音频对照。'),
        (17, 'Bug反馈', '电子书翻页问题', '部分 EPUB 在移动端翻页有轻微卡顿。'),
        (18, '功能建议', '增加小组学习', '希望班级内可创建小组共同完成作业与讨论。'),
        (19, '内容建议', '增加思维导图', '长章节课程如果能附思维导图总结会更高效。'),
        (20, '其他', '总体评价', '平台功能全面，界面清晰，继续优化视频与搜索体验会更好。')
    ) AS t(n, feedback_type, title, content) ON t.n = i;

    -- =============================================================
    -- 17. 智能批改记录（20 条，已完成）
    -- =============================================================
    WITH new_submissions AS (
        INSERT INTO homework_submission (
            student_id, title, subject, grade, image_urls, status
        )
        SELECT
            3,
            subjects.title,
            subjects.subject,
            'GRADE_8',
            ARRAY['https://picsum.photos/seed/hw' || i || '/800/600'],
            'COMPLETED'
        FROM generate_series(1, 20) AS i
        JOIN (VALUES
            (1, 'MATH', '八年级数学 · 第三单元巩固练习'),
            (2, 'CHINESE', '八年级语文 · 阅读理解作业'),
            (3, 'ENGLISH', '八年级英语 · 语法专项训练'),
            (4, 'PHYSICS', '八年级物理 · 力学计算题'),
            (5, 'CHEMISTRY', '八年级化学 · 方程式练习'),
            (6, 'BIOLOGY', '八年级生物 · 细胞结构识图'),
            (7, 'HISTORY', '八年级历史 · 材料分析题'),
            (8, 'GEOGRAPHY', '八年级地理 · 地图判读练习'),
            (9, 'MATH', '八年级数学 · 期中复习卷'),
            (10, 'CHINESE', '八年级语文 · 文言文翻译'),
            (11, 'ENGLISH', '八年级英语 · 完形填空练习'),
            (12, 'PHYSICS', '八年级物理 · 压强专题'),
            (13, 'CHEMISTRY', '八年级化学 · 实验设计题'),
            (14, 'BIOLOGY', '八年级生物 · 生态系统习题'),
            (15, 'HISTORY', '八年级历史 · 近代史专题'),
            (16, 'GEOGRAPHY', '八年级地理 · 气候类型判断'),
            (17, 'MATH', '八年级数学 · 几何证明题'),
            (18, 'CHINESE', '八年级语文 · 记叙文写作'),
            (19, 'ENGLISH', '八年级英语 · 书面表达练习'),
            (20, 'PHYSICS', '八年级物理 · 综合测评')
        ) AS subjects(n, subject, title) ON subjects.n = i
        RETURNING id
    )
    INSERT INTO grading_result (submission_id, total_score, max_score, overall_comment, grading_time)
    SELECT
        ns.id,
        70 + (ns.id % 30),
        100,
        '整体表现良好，继续保持！注意细节和书写规范。',
        NOW()
    FROM new_submissions ns;

    -- =============================================================
    -- 18. 打卡记录（管理员近 20 天）
    -- =============================================================
    INSERT INTO user_checkin (user_id, checkin_date, checkin_time, streak_days)
    SELECT
        1,
        CURRENT_DATE - (20 - i),
        (CURRENT_DATE - (20 - i))::timestamp + TIME '08:30:00',
        i
    FROM generate_series(1, 20) AS i
    ON CONFLICT (user_id, checkin_date) DO NOTHING;

    INSERT INTO user_checkin_stats (user_id, total_checkin_days, current_streak, max_streak, last_checkin_date)
    VALUES (1, 20, 20, 20, CURRENT_DATE)
    ON CONFLICT (user_id) DO UPDATE SET
        total_checkin_days = 20,
        current_streak = 20,
        max_streak = 20,
        last_checkin_date = CURRENT_DATE;

    RAISE NOTICE 'Demo data seeded successfully!';
END $$;

-- =============================================================
-- 19. 仪表盘分析演示数据（30 天 DAU / 用户增长 / 收入趋势）
-- 幂等：近 30 天 learning_activity >= 200 条则跳过
-- =============================================================
DO $$
DECLARE
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM learning_activity
    WHERE activity_date >= CURRENT_DATE - INTERVAL '30 days';

    IF v_count >= 200 THEN
        RAISE NOTICE 'Dashboard analytics already seeded (% rows in last 30 days)', v_count;
        RETURN;
    END IF;

    DELETE FROM learning_activity;
    DELETE FROM user_course WHERE order_no LIKE 'DEMO-DASH-%';

    INSERT INTO class_member (class_id, user_id, role)
    SELECT
        ci.id,
        u.id,
        'student'
    FROM "user" u
    CROSS JOIN LATERAL (
        SELECT id FROM class_info
        WHERE is_delete = 0
        ORDER BY id
        OFFSET ((u.id - 1) % GREATEST((SELECT COUNT(*) FROM class_info WHERE is_delete = 0), 1)) LIMIT 1
    ) ci
    WHERE u.user_role = 'student' AND u.is_delete = 0
      AND NOT EXISTS (SELECT 1 FROM class_member cm WHERE cm.user_id = u.id AND cm.is_delete = 0);

    UPDATE "user" u
    SET create_time = NOW() - (s.rn || ' days')::INTERVAL
    FROM (
        SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
        FROM "user"
        WHERE user_role = 'student' AND is_delete = 0
    ) s
    WHERE u.id = s.id;

    INSERT INTO learning_activity (
        user_id, activity_type, class_id, duration_sec, score, max_score, activity_date, create_time
    )
    SELECT
        u.id,
        (ARRAY['COURSE_WATCH', 'COURSE_WATCH', 'ARTICLE_READ', 'WORD_STUDY', 'HOMEWORK_SUBMIT', 'CHECKIN'])
            [1 + ((d + u.id + i) % 6)],
        cm.class_id,
        180 + ((d * 17 + u.id * 13 + i * 7) % 2700),
        CASE WHEN (d + u.id + i) % 6 = 4 THEN 68 + ((d + i) % 32) ELSE NULL END,
        CASE WHEN (d + u.id + i) % 6 = 4 THEN 100 ELSE NULL END,
        CURRENT_DATE - d,
        (CURRENT_DATE - d)::TIMESTAMP + TIME '09:00:00' + ((i * 37) % 480) * INTERVAL '1 minute'
    FROM generate_series(0, 29) AS d
    CROSS JOIN generate_series(1, 6) AS i
    CROSS JOIN (
        SELECT id FROM "user" WHERE user_role = 'student' AND is_delete = 0
    ) u(id)
    LEFT JOIN LATERAL (
        SELECT class_id FROM class_member WHERE user_id = u.id LIMIT 1
    ) cm ON TRUE
    WHERE (u.id + d + i) % 4 <> 0;

    INSERT INTO user_course (
        user_id, course_id, order_no, price, payment_method, payment_time, status, create_time
    )
    SELECT
        u.id,
        c.id,
        'DEMO-DASH-' || d || '-' || u.id || '-' || c.id,
        GREATEST(c.price, 19.9),
        CASE (d % 3) WHEN 0 THEN 'wechat' WHEN 1 THEN 'alipay' ELSE 'balance' END,
        (CURRENT_DATE - d)::TIMESTAMP + TIME '15:20:00' + (seq * 17) * INTERVAL '1 minute',
        1,
        (CURRENT_DATE - d)::TIMESTAMP + TIME '15:18:00'
    FROM generate_series(0, 29) AS d
    CROSS JOIN generate_series(1, 2) AS seq
    CROSS JOIN LATERAL (
        SELECT id FROM "user"
        WHERE user_role = 'student' AND is_delete = 0
        ORDER BY id
        OFFSET ((d + seq) % 15) LIMIT 1
    ) u(id)
    CROSS JOIN LATERAL (
        SELECT id, price FROM course
        WHERE is_delete = 0 AND status = 1
        ORDER BY id
        OFFSET (d % 10) LIMIT 1
    ) c(id, price)
    WHERE (d + seq) % 2 = 0 OR d <= 3;

    INSERT INTO user_course (
        user_id, course_id, order_no, price, payment_method, payment_time, status, create_time
    )
    SELECT
        u.id, c.id,
        'DEMO-DASH-TODAY-' || u.id,
        GREATEST(c.price, 29.9), 'wechat', NOW() - INTERVAL '2 hours', 1, NOW() - INTERVAL '2 hours'
    FROM (SELECT id FROM "user" WHERE user_role = 'student' AND is_delete = 0 ORDER BY id LIMIT 5) u(id)
    CROSS JOIN (SELECT id, price FROM course WHERE is_delete = 0 AND status = 1 ORDER BY id LIMIT 1) c(id, price)
    WHERE NOT EXISTS (SELECT 1 FROM user_course uc WHERE uc.order_no = 'DEMO-DASH-TODAY-' || u.id);

    INSERT INTO user_course (
        user_id, course_id, order_no, price, payment_method, payment_time, status, create_time
    )
    SELECT
        u.id, c.id,
        'DEMO-DASH-YDAY-' || u.id,
        GREATEST(c.price, 19.9), 'alipay',
        (CURRENT_DATE - 1)::TIMESTAMP + TIME '16:00:00', 1,
        (CURRENT_DATE - 1)::TIMESTAMP + TIME '15:55:00'
    FROM (SELECT id FROM "user" WHERE user_role = 'student' AND is_delete = 0 ORDER BY id OFFSET 5 LIMIT 4) u(id)
    CROSS JOIN (SELECT id, price FROM course WHERE is_delete = 0 AND status = 1 ORDER BY id OFFSET 1 LIMIT 1) c(id, price)
    WHERE NOT EXISTS (SELECT 1 FROM user_course uc WHERE uc.order_no = 'DEMO-DASH-YDAY-' || u.id);

    INSERT INTO user_membership (user_id, plan_id, start_time, expire_time, status)
    SELECT u.id, p.id, NOW() - INTERVAL '15 days', NOW() + INTERVAL '350 days', 1
    FROM (SELECT id FROM "user" WHERE user_role = 'student' AND is_delete = 0 ORDER BY id LIMIT 8) u(id)
    CROSS JOIN (SELECT id FROM membership_plan WHERE code = 'BASIC' LIMIT 1) p(id)
    WHERE NOT EXISTS (
        SELECT 1 FROM user_membership um WHERE um.user_id = u.id AND um.status = 1 AND um.is_delete = 0
    );

    INSERT INTO user_membership (user_id, plan_id, start_time, expire_time, status)
    SELECT u.id, p.id, NOW() - INTERVAL '60 days', NOW() + INTERVAL '5 days', 1
    FROM (SELECT id FROM "user" WHERE user_role = 'student' AND is_delete = 0 ORDER BY id OFFSET 8 LIMIT 2) u(id)
    CROSS JOIN (SELECT id FROM membership_plan WHERE code = 'PRO' LIMIT 1) p(id)
    WHERE NOT EXISTS (
        SELECT 1 FROM user_membership um WHERE um.user_id = u.id AND um.status = 1 AND um.is_delete = 0
    );

    INSERT INTO user_checkin (user_id, checkin_date, checkin_time, streak_days)
    SELECT
        u.id, CURRENT_DATE,
        CURRENT_DATE::TIMESTAMP + TIME '07:30:00' + (ROW_NUMBER() OVER ()) * INTERVAL '3 minutes',
        3 + (u.id % 12)
    FROM (SELECT id FROM "user" WHERE user_role = 'student' AND is_delete = 0 ORDER BY id LIMIT 12) u(id)
    ON CONFLICT (user_id, checkin_date) DO NOTHING;

    INSERT INTO ai_usage_record (user_id, feature_type, usage_date, usage_count)
    SELECT u.id, f.ft, CURRENT_DATE, 5 + (u.id % 20)
    FROM (SELECT id FROM "user" WHERE is_delete = 0 ORDER BY id LIMIT 15) u(id)
    CROSS JOIN (VALUES ('AI_CHAT'), ('AI_GRADING'), ('AI_PPT')) AS f(ft)
    ON CONFLICT (user_id, feature_type, usage_date)
    DO UPDATE SET usage_count = EXCLUDED.usage_count;

    UPDATE ai_chat_session
    SET create_time = CURRENT_DATE::TIMESTAMP + TIME '10:00:00' + (id % 120) * INTERVAL '1 minute'
    WHERE is_delete = 0 AND create_time::DATE < CURRENT_DATE - 1
    AND id IN (SELECT id FROM ai_chat_session WHERE is_delete = 0 ORDER BY id DESC LIMIT 15);

    RAISE NOTICE 'Dashboard analytics data seeded successfully.';
END $$;

-- =============================================================
-- 20. 演示讲师（teacher01–teacher10，密码 123456）
-- 幂等：按账号/teacher 记录判重
-- =============================================================
DO $$
DECLARE
    v_admin_id BIGINT;
    v_pwd TEXT := '$2a$10$FVszfHXWI0REOV5cB9tvguaQ55ROaklP/OUbb61m0BrWCxuN4Am8a';
BEGIN
    SELECT id INTO v_admin_id FROM "user" WHERE user_account = 'admin' AND is_delete = 0 LIMIT 1;
    IF v_admin_id IS NULL THEN
        RAISE NOTICE 'Admin user not found, skip teacher demo seed';
        RETURN;
    END IF;

    INSERT INTO "user" (user_account, user_password, user_name, user_role, level, create_time)
    SELECT v.account, v_pwd, v.user_name, 'teacher', 10,
           NOW() - (v.idx || ' days')::INTERVAL
    FROM (VALUES
        (1,  'teacher01', E'王明华'),
        (2,  'teacher02', E'李晓芳'),
        (3,  'teacher03', E'张建国'),
        (4,  'teacher04', E'陈思雨'),
        (5,  'teacher05', E'刘洋'),
        (6,  'teacher06', E'赵婷婷'),
        (7,  'teacher07', E'孙志强'),
        (8,  'teacher08', E'周佳宁'),
        (9,  'teacher09', E'吴昊'),
        (10, 'teacher10', E'郑雅文')
    ) AS v(idx, account, user_name)
    WHERE NOT EXISTS (SELECT 1 FROM "user" u WHERE u.user_account = v.account);

    INSERT INTO teacher (name, introduction, expertise, user_id, admin_id, create_time)
    SELECT v.name, v.intro, v.expertise, u.id, v_admin_id,
           NOW() - (v.idx || ' days')::INTERVAL
    FROM (VALUES
        (1,  'teacher01', E'王明华', E'十年一线数学教学经验，擅长竞赛思维训练与中考冲刺辅导。', '["数学","竞赛思维"]'),
        (2,  'teacher02', E'李晓芳', E'英语专业八级，专注口语表达与听力技巧，课堂互动性强。', '["英语","口语"]'),
        (3,  'teacher03', E'张建国', E'物理实验教学专家，善于用生活化案例讲解力学与电学。', '["物理","实验"]'),
        (4,  'teacher04', E'陈思雨', E'化学硕士，系统梳理初中化学知识体系，注重实验安全与规范。', '["化学"]'),
        (5,  'teacher05', E'刘洋',   E'少儿编程讲师，Scratch/Python 启蒙，培养逻辑思维与创造力。', '["编程","Scratch"]'),
        (6,  'teacher06', E'赵婷婷', E'语文教研骨干，阅读写作双线提升，古典文学讲解生动有趣。', '["语文","阅读"]'),
        (7,  'teacher07', E'孙志强', E'生物学科带头人，引导探究式学习，关注科学素养培养。', '["生物","科学"]'),
        (8,  'teacher08', E'周佳宁', E'历史人文讲师，以故事化方式串联中外历史脉络。', '["历史","人文"]'),
        (9,  'teacher09', E'吴昊',   E'地理教师，地图读图与区域分析专项训练，贴近生活案例。', '["地理"]'),
        (10, 'teacher10', E'郑雅文', E'音乐素养讲师，合唱与乐理基础教学，激发艺术感知力。', '["音乐","艺术"]')
    ) AS v(idx, account, name, intro, expertise)
    JOIN "user" u ON u.user_account = v.account AND u.is_delete = 0
    WHERE NOT EXISTS (
        SELECT 1 FROM teacher t WHERE t.user_id = u.id AND t.is_delete = 0
    );

    INSERT INTO user_membership (user_id, plan_id, start_time, expire_time, status)
    SELECT u.id, p.id, NOW() - INTERVAL '30 days', NULL, 1
    FROM "user" u
    CROSS JOIN (SELECT id FROM membership_plan WHERE code = 'TEACHER' LIMIT 1) p(id)
    WHERE u.user_account LIKE 'teacher0%' AND u.user_role = 'teacher'
      AND NOT EXISTS (
          SELECT 1 FROM user_membership um
          WHERE um.user_id = u.id AND um.status = 1 AND um.is_delete = 0
      );

    RAISE NOTICE 'Teacher demo data seeded (10 lecturers).';
END $$;
-- >>> END: 100_demo_data.sql <<<

-- >>> BEGIN: 101_realistic_data_cleanup.sql <<<

-- =================================================================
-- 去除演示数据中的「示例」前缀与模板化文案（可重复执行）
-- =================================================================

-- 学生昵称
UPDATE "user" u
SET user_name = m.name
FROM (VALUES
    ('student01', '陈思远'), ('student02', '林雨萱'), ('student03', '王子涵'),
    ('student04', '刘佳怡'), ('student05', '张子轩'), ('student06', '杨若曦'),
    ('student07', '赵一鸣'), ('student08', '黄诗涵'), ('student09', '周浩然'),
    ('student10', '吴欣妍'), ('student11', '徐天宇'), ('student12', '孙雨桐'),
    ('student13', '马俊杰'), ('student14', '朱梓涵'), ('student15', '胡嘉怡'),
    ('student16', '郭宇航'), ('student17', '何思琪'), ('student18', '高子墨'),
    ('student19', '罗心怡'), ('student20', '梁逸飞')
) AS m(account, name)
WHERE u.user_account = m.account;

UPDATE "user"
SET user_name = REPLACE(user_name, '示例学生', '同学')
WHERE user_name LIKE '示例学生%';

-- 课程
UPDATE course
SET
    title = REPLACE(title, '【示例】', ''),
    description = CASE
        WHEN description LIKE '%示例课程%' OR description LIKE '%精心设计的示例%'
            THEN '系统梳理核心知识点，配套随堂练习与阶段测评，适合课后巩固与自主提升。'
        ELSE description
    END
WHERE title LIKE '【示例】%' OR description LIKE '%示例%';

-- 每日文章
UPDATE daily_article
SET
    title = REPLACE(title, '【示例】', ''),
    summary = REPLACE(summary, '精选美文：', ''),
    content = REGEXP_REPLACE(content, '^这是示例文章内容。[^—]*——', ''),
    source = CASE WHEN source = 'NovaCloudEdu' THEN '鳗螈.星云课堂' ELSE source END,
    author = CASE WHEN author LIKE '%编辑部' THEN '鳗螈.星云课堂' ELSE author END
WHERE title LIKE '【示例】%' OR content LIKE '这是示例文章内容%';

-- 公告
UPDATE announcement
SET
    title = REPLACE(title, '【示例】', ''),
    content = REGEXP_REPLACE(content, '^这是系统公告示例内容：', '')
WHERE title LIKE '【示例】%' OR content LIKE '这是系统公告示例内容%';

-- 轮播图
UPDATE banner SET title = REPLACE(title, '【示例】', '') WHERE title LIKE '【示例】%';

-- 学习圈帖子
UPDATE post
SET
    title = REPLACE(title, '【示例】', ''),
    content = REGEXP_REPLACE(content, '^这是学习圈示例帖子：', '')
WHERE title LIKE '【示例】%' OR content LIKE '这是学习圈示例帖子%';

-- 电子书
UPDATE book SET title = REPLACE(title, '【示例】', '') WHERE title LIKE '【示例】%';

UPDATE chapter
SET content = '<p>清晨的阳光透过窗棂洒在书页上，故事从这里缓缓展开。请继续阅读，感受文字带来的想象与力量。</p>'
WHERE content LIKE '%示例书籍%' OR content LIKE '%演示阅读%';

-- 班级
UPDATE class_info
SET
    class_name = REPLACE(class_name, '【示例】', ''),
    description = CASE
        WHEN description LIKE '%示例班级%' OR description LIKE '%演示班级%'
            THEN '本班注重课堂互动与分层辅导，定期发布学习任务与阶段测评。'
        ELSE description
    END
WHERE class_name LIKE '【示例】%' OR description LIKE '%示例班级%';

-- 题库
UPDATE question
SET
    content = REGEXP_REPLACE(content, '^【示例】第[0-9]+题：', ''),
    explanation = CASE
        WHEN explanation LIKE '这是第%题的解析说明。'
            THEN '结合题干关键信息逐项排除，注意审题与单位换算。'
        ELSE explanation
    END
WHERE content LIKE '【示例】%' OR explanation LIKE '这是第%题的解析说明。';

-- 试卷
UPDATE exam_paper SET title = REPLACE(title, '【示例】', '') WHERE title LIKE '【示例】%';

-- AI 助手
UPDATE ai_assistant SET name = REPLACE(name, '【示例】', '') WHERE name LIKE '【示例】%';

-- 知识库
UPDATE knowledge_base
SET
    name = REPLACE(name, '【示例】', ''),
    description = CASE
        WHEN description LIKE '%供AI检索增强使用%'
            THEN REPLACE(description, '，供AI检索增强使用。', '。收录教案、讲义与拓展阅读材料。')
        ELSE description
    END
WHERE name LIKE '【示例】%' OR description LIKE '%供AI检索增强使用%';

-- 聊天群
UPDATE chat_group
SET
    group_name = REPLACE(group_name, '【示例】', ''),
    description = CASE
        WHEN description = '这是一个示例学习交流群。' THEN '欢迎分享学习资料、讨论作业与备考经验。'
        ELSE description
    END
WHERE group_name LIKE '【示例】%' OR description LIKE '%示例学习交流群%';

-- 用户反馈
UPDATE user_feedback
SET
    title = REPLACE(title, '【示例】', ''),
    content = REGEXP_REPLACE(content, '^反馈内容：', '')
WHERE title LIKE '【示例】%' OR content LIKE '反馈内容：%';

-- 智能批改作业
UPDATE homework_submission
SET title = CASE subject
    WHEN 'MATH' THEN '八年级数学 · 单元巩固练习'
    WHEN 'CHINESE' THEN '八年级语文 · 阅读理解作业'
    WHEN 'ENGLISH' THEN '八年级英语 · 语法专项训练'
    WHEN 'PHYSICS' THEN '八年级物理 · 力学计算题'
    WHEN 'CHEMISTRY' THEN '八年级化学 · 方程式练习'
    WHEN 'BIOLOGY' THEN '八年级生物 · 细胞结构识图'
    WHEN 'HISTORY' THEN '八年级历史 · 材料分析题'
    WHEN 'GEOGRAPHY' THEN '八年级地理 · 地图判读练习'
    ELSE REPLACE(title, '【示例】作业', '课后作业')
END
WHERE title LIKE '【示例】作业%';
-- >>> END: 101_realistic_data_cleanup.sql <<<


CREATE TABLE IF NOT EXISTS homework_assignment (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL REFERENCES "user"(id),
    course_id BIGINT NOT NULL REFERENCES course(id),
    chapter_id BIGINT,
    section_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    total_score INTEGER NOT NULL DEFAULT 100 CHECK (total_score > 0),
    pass_score INTEGER NOT NULL DEFAULT 60 CHECK (pass_score >= 0),
    deadline TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    settings_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_homework_assignment_teacher ON homework_assignment(teacher_id, update_time DESC);
CREATE INDEX IF NOT EXISTS idx_homework_assignment_course ON homework_assignment(course_id);
CREATE INDEX IF NOT EXISTS idx_homework_assignment_status ON homework_assignment(status, deadline);

CREATE TABLE IF NOT EXISTS homework_assignment_class (
    assignment_id BIGINT NOT NULL REFERENCES homework_assignment(id) ON DELETE CASCADE,
    class_id BIGINT NOT NULL REFERENCES class_info(id) ON DELETE CASCADE,
    PRIMARY KEY (assignment_id, class_id)
);
CREATE INDEX IF NOT EXISTS idx_homework_assignment_class_class ON homework_assignment_class(class_id);

CREATE TABLE IF NOT EXISTS homework_assignment_submission (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL REFERENCES homework_assignment(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL REFERENCES "user"(id),
    attempt_no INTEGER NOT NULL DEFAULT 1 CHECK (attempt_no > 0),
    answers_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    attachment_urls_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    score NUMERIC(8,2),
    teacher_comment TEXT,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    graded_at TIMESTAMP,
    UNIQUE (assignment_id, student_id, attempt_no)
);
CREATE INDEX IF NOT EXISTS idx_homework_submission_assignment ON homework_assignment_submission(assignment_id, submitted_at);
CREATE INDEX IF NOT EXISTS idx_homework_submission_student ON homework_assignment_submission(student_id, submitted_at DESC);

-- Teacher exam schedules and student answer submissions.
CREATE TABLE IF NOT EXISTS exam_schedule (
  id BIGSERIAL PRIMARY KEY,
  teacher_id BIGINT NOT NULL REFERENCES "user"(id),
  course_id BIGINT NOT NULL REFERENCES course(id),
  paper_id BIGINT REFERENCES exam_paper(id),
  title VARCHAR(255) NOT NULL,
  starts_at TIMESTAMP NOT NULL,
  ends_at TIMESTAMP NOT NULL,
  duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0),
  class_ids_json JSONB NOT NULL DEFAULT '[]'::jsonb,
  settings_json JSONB NOT NULL DEFAULT '{}'::jsonb,
  status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CHECK (ends_at > starts_at)
);
CREATE INDEX IF NOT EXISTS idx_exam_schedule_teacher ON exam_schedule(teacher_id, starts_at DESC);
CREATE INDEX IF NOT EXISTS idx_exam_schedule_course ON exam_schedule(course_id, starts_at);
CREATE TABLE IF NOT EXISTS exam_submission (
  id BIGSERIAL PRIMARY KEY,
  exam_id BIGINT NOT NULL REFERENCES exam_schedule(id) ON DELETE CASCADE,
  student_id BIGINT NOT NULL REFERENCES "user"(id),
  answers_json JSONB NOT NULL DEFAULT '{}'::jsonb,
  score NUMERIC(8,2),
  submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (exam_id, student_id)
);
CREATE INDEX IF NOT EXISTS idx_exam_submission_student ON exam_submission(student_id, submitted_at DESC);
-- ai_assistant 模型配置字段（已有库升级；新库见 backend/sql/50_ai_chat.sql）
ALTER TABLE ai_assistant ADD COLUMN IF NOT EXISTS api_key VARCHAR(512) NULL;
ALTER TABLE ai_assistant ADD COLUMN IF NOT EXISTS model_base_url VARCHAR(512) NULL;
COMMENT ON COLUMN ai_assistant.api_key IS 'Per-assistant model API key; falls back to global config when null';
COMMENT ON COLUMN ai_assistant.model_base_url IS 'Model API base URL for this assistant';

ALTER TABLE ai_assistant ALTER COLUMN model_name TYPE VARCHAR(256);

-- 修正旧版 model_name 格式，并将无独立密钥的助手默认切到 DeepSeek
UPDATE ai_assistant
SET model_name = 'dashscope/' || model_name
WHERE model_name IS NOT NULL
  AND model_name <> ''
  AND model_name NOT LIKE '%/%';

UPDATE ai_assistant
SET model_name = 'deepseek/deepseek-chat',
    model_base_url = 'https://api.deepseek.com/v1'
WHERE (api_key IS NULL OR api_key = '')
  AND (model_base_url IS NULL OR model_base_url = '')
  AND (model_name LIKE 'dashscope/%' OR model_name = 'qwen-plus');

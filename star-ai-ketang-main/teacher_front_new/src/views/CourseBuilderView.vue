<template>
  <div>
    <!-- ==================== 课程列表（一位教师 · 多门课程） ==================== -->
    <template v-if="!activeCourse">
      <div class="panel list-head">
        <div class="lh-info">
          <h2>我的课程</h2>
          <p class="muted">
            王琳老师，您共承担 {{ courses.length }} 门课程 ·
            {{ courses.filter(c => c.status === '已发布').length }} 门已发布 ·
            覆盖学生 {{ totalStudents }} 人
          </p>
        </div>
        <div class="list-tools">
          <el-input v-model="keyword" placeholder="搜索课程名称 / 编号" :prefix-icon="SearchIcon" clearable style="width:220px" />
          <el-select v-model="semesterFilter" style="width:150px">
            <el-option v-for="s in semesterOptions" :key="s" :label="s" :value="s" />
          </el-select>
          <el-select v-model="statusFilter" style="width:120px">
            <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
          </el-select>
          <el-button type="primary" :icon="Plus" @click="createDialog = true">新建课程</el-button>
        </div>
      </div>

      <div class="course-grid">
        <div v-for="c in filteredCourses" :key="c.id" class="course-card panel" @click="openCourse(c.id)">
          <div class="cc-cover" :style="{ background: c.cover }">
            <component :is="c.icon" :size="30" />
            <el-tag class="cc-status" size="small" effect="dark"
              :type="c.status === '已发布' ? 'success' : c.status === '建设中' ? 'warning' : 'info'">
              {{ c.status }}
            </el-tag>
          </div>
          <div class="cc-body">
            <div class="cc-name">{{ c.name }}</div>
            <div class="cc-meta">{{ c.code }} · {{ c.level }} · {{ c.hours }} 学时</div>
            <div class="cc-stats">
              <span><strong>{{ c.classes }}</strong> 班级</span>
              <span><strong>{{ c.students }}</strong> 学生</span>
              <span><strong>{{ c.chapters.length }}</strong> 章节</span>
              <span><strong>{{ chapterPoints(c) }}</strong> 任务点</span>
            </div>
            <div class="cc-progress">
              <span class="muted">建设完成度</span>
              <el-progress :percentage="c.progress" :stroke-width="6"
                :status="c.progress >= 90 ? 'success' : undefined" style="flex:1" />
            </div>
          </div>
          <div class="cc-foot" @click.stop>
            <span class="muted">{{ c.semester }} · 更新 {{ c.updated }}</span>
            <div style="display:flex; gap:6px">
              <el-button type="primary" size="small" @click="openCourse(c.id)">进入建设</el-button>
              <el-dropdown trigger="click" @command="cmd => courseCommand(cmd, c)">
                <el-button size="small" text :icon="MoreVertical" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="copy">复制课程</el-dropdown-item>
                    <el-dropdown-item command="data">课程数据</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除课程</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!filteredCourses.length" class="panel empty-box">
          <BookOpen :size="34" class="muted" />
          <p class="muted">没有符合条件的课程，调整筛选或新建一门课程</p>
          <el-button type="primary" :icon="Plus" @click="createDialog = true">新建课程</el-button>
        </div>
      </div>
    </template>

    <!-- ==================== 单课程建设 ==================== -->
    <template v-else>
      <!-- 课程信息头 -->
      <div class="panel course-head">
        <el-button text :icon="ArrowLeft" class="back-btn" @click="backToList">课程列表</el-button>
        <div class="cover" :style="{ background: activeCourse.cover }">
          <component :is="activeCourse.icon" :size="30" />
        </div>
        <div class="info">
          <div class="name-row">
            <h2>{{ activeCourse.name }}</h2>
            <el-tag size="small" effect="light"
              :type="activeCourse.status === '已发布' ? 'success' : activeCourse.status === '建设中' ? 'warning' : 'info'">
              {{ activeCourse.status }}
            </el-tag>
            <el-tag size="small" effect="plain" type="info">{{ activeCourse.level }} · {{ activeCourse.hours }} 学时</el-tag>
          </div>
          <p class="meta">
            课程编号 {{ activeCourse.code }} · 开课班级 {{ activeCourse.classes }} 个 · 学生 {{ activeCourse.students }} 人 ·
            章节 {{ activeCourse.chapters.length }} / 任务点 {{ chapterPoints(activeCourse) }}
            · 最近更新 {{ activeCourse.updated }}
          </p>
        </div>
        <!-- 多课程快速切换 -->
        <el-select v-model="activeCourseId" class="course-switcher" placeholder="切换课程">
          <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id">
            <div style="display:flex; align-items:center; justify-content:space-between; gap:14px">
              <span>{{ c.name }}</span>
              <span class="muted" style="font-size:12px">{{ c.status }}</span>
            </div>
          </el-option>
        </el-select>
        <div class="actions">
          <el-button @click="openPortal">预览课程</el-button>
          <el-button @click="saveDraft">保存草稿</el-button>
          <el-button type="primary" @click="publish">发布课程</el-button>
        </div>
      </div>

      <el-tabs v-model="tab" class="panel tabs-panel">
        <!-- ========== 课程介绍 ========== -->
        <el-tab-pane label="课程介绍" name="intro">
          <div class="intro-grid">
            <div class="editor-col">
              <div class="editor-toolbar">
                <button class="tbtn" title="加粗"><strong>B</strong></button>
                <button class="tbtn" title="斜体"><em>I</em></button>
                <button class="tbtn" title="下划线"><u>U</u></button>
                <button class="tbtn" title="删除线"><s>S</s></button>
                <span class="sep" />
                <button class="tbtn" title="标题">H1</button>
                <button class="tbtn" title="标题2">H2</button>
                <button class="tbtn" title="有序列表">1.</button>
                <button class="tbtn" title="无序列表">•</button>
                <button class="tbtn" title="引用">❝</button>
                <span class="sep" />
                <button class="tbtn" title="插入图片" @click="insertResource('图片')"><ImageIcon :size="15" /></button>
                <button class="tbtn" title="插入视频" @click="insertResource('视频')"><Film :size="15" /></button>
                <button class="tbtn" title="插入附件" @click="insertResource('附件')"><Paperclip :size="15" /></button>
                <button class="tbtn" title="插入公式" @click="insertFormula">∑</button>
                <button class="tbtn" title="插入链接" @click="insertLink"><Link2 :size="15" /></button>
                <button class="tbtn" title="AI 润色" @click="aiPolish"><Sparkles :size="15" /></button>
                <span class="paste-hint muted">支持直接粘贴网页图文，图片自动转存本地</span>
              </div>
              <div ref="editorRef" class="editor-body" contenteditable="true" @keyup="saveEditorRange" @mouseup="saveEditorRange" @paste="onEditorPaste">
                <h3>一、课程定位</h3>
                <p>{{ activeCourse.intro }}</p>
                <h3>二、先修要求</h3>
                <p>建议先修《摄影摄像基础》《数字媒体概论》。零基础学生可通过第 1 章「入门导学」补齐基础概念。</p>
                <div class="video-embed">
                  <PlayCircle :size="22" />
                  <div>
                    <div>1.1 课程导学.mp4</div>
                    <div style="opacity:.7; font-size:12px">08:24 · 已嵌入章节任务点 · 含 2 个随堂测验锚点</div>
                  </div>
                </div>
                <h3>三、考核方式</h3>
                <p>总成绩 = 作业 30% + 章节测验 20% + 期末考试 40% + 线下实践 10%。闯关模式下需完成全部任务点方可进入下一章。</p>
                <div class="callout">
                  教师提示：「{{ activeCourse.name }}」共 {{ activeCourse.classes }} 个班级修读，建议统一教学进度后再发布新章节。
                </div>
              </div>
            </div>
            <div class="side-col">
              <div class="panel-title"><h3>章节快速导航</h3></div>
              <div class="toc">
                <div v-for="ch in chapters" :key="ch.id" class="toc-item" @click="tab = 'content'">
                  <strong>{{ ch.title }}</strong>
                  <span class="muted">{{ ch.sections.length }} 小节 · {{ ch.points }} 任务点</span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- ========== 章节内容 ========== -->
        <el-tab-pane label="章节内容" name="content">
          <div class="content-toolbar">
            <el-button type="primary" @click="chapterDialog = true">新建章节</el-button>
            <el-button @click="addSection">添加小节</el-button>
            <el-button @click="insertDrawer = true">插入资源</el-button>
            <span class="muted drag-hint"><GripVertical :size="13" /> 按住手柄拖拽，可调整章节与小节顺序</span>
          </div>
          <div class="chapter-tree">
            <div v-for="(ch, ci) in chapters" :key="ch.id" class="chapter-node"
              :class="{ 'drop-into': dropTarget?.kind === 'into-chapter' && dropTarget.ci === ci }"
              @dragover.prevent="onNodeDragOver(ci)"
              @drop.prevent="commitDrop">
              <div class="chapter-row"
                :class="[rowDropClass('chapter', ci), dragState?.kind === 'chapter' && dragState.index === ci ? 'dragging' : '']"
                @dragover.prevent.stop="onRowDragOver($event, 'chapter', ci)"
                @drop.prevent.stop="commitDrop">
                <span class="grip" title="拖拽调整章节顺序" draggable="true"
                  @dragstart="onDragStart($event, { kind: 'chapter', index: ci })"
                  @dragend="onDragEnd"><GripVertical :size="15" /></span>
                <ChevronDown :size="14" />
                <span>{{ ch.title }}</span>
                <el-tag size="small" effect="plain">{{ ch.points }} 任务点</el-tag>
                <span style="flex:1" />
                <el-button link type="primary" size="small" @click="addSection">添加小节</el-button>
                <el-button link size="small">重命名</el-button>
                <el-button link type="danger" size="small">删除</el-button>
              </div>
              <div v-for="(sec, si) in ch.sections" :key="sec.id" class="section-row"
                :class="[rowDropClass('section', ci, si), dragState?.kind === 'section' && dragState.ci === ci && dragState.si === si ? 'dragging' : '']"
                @dragover.prevent.stop="onRowDragOver($event, 'section', ci, si)"
                @drop.prevent.stop="commitDrop">
                <span class="grip" title="拖拽调整小节顺序，可跨章节移动" draggable="true"
                  @dragstart="onDragStart($event, { kind: 'section', ci, si })"
                  @dragend="onDragEnd"><GripVertical :size="14" /></span>
                <component :is="sec.type === 'video' ? PlayCircle : sec.type === 'doc' ? FileText : ClipboardList" :size="14" />
                <span class="grow">{{ sec.title }}</span>
                <el-tag v-if="sec.taskPoint" size="small" type="success" effect="light">任务点</el-tag>
                <el-tag v-if="sec.quiz" size="small" type="warning" effect="light">含测验</el-tag>
                <span class="muted">{{ sec.duration }}</span>
                <el-button link type="primary" size="small" @click.stop="assignSectionHomework(ch, sec)">Homework</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- ========== 章节管理 ========== -->
        <el-tab-pane label="章节管理" name="manage">
          <div class="content-toolbar">
            <span class="muted drag-hint" style="margin-left:0">
              <GripVertical :size="13" /> 拖拽手柄调整顺序：拖动「章节」行将连同其下小节整体移动
            </span>
          </div>
          <div ref="manageTableEl" class="manage-table"
            @dragover.prevent="onManageDragOver" @drop.prevent="commitManageDrop" @dragleave="onManageDragLeave">
            <el-table :data="manageRows" border stripe row-key="rowKey">
              <el-table-column label="" width="42" align="center">
                <template #default="{ row }">
                  <span class="grip" title="拖拽排序" draggable="true"
                    @dragstart="onManageDragStart($event, row)" @dragend="onManageDragEnd">
                    <GripVertical :size="14" />
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="章节 / 小节" min-width="220" show-overflow-tooltip />
              <el-table-column prop="type" label="类型" width="90">
                <template #default="{ row }">
                  <el-tag size="small" effect="plain">{{ row.type }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="taskPoint" label="任务点" width="80" align="center">
                <template #default="{ row }">
                  <el-switch :model-value="row.taskPoint" @change="v => row.taskPoint = Boolean(v)" />
                </template>
              </el-table-column>
              <el-table-column label="开放模式" width="150">
                <template #default="{ row }">
                  <el-select v-model="row.mode" size="small">
                    <el-option v-for="m in accessModes" :key="m" :label="m" :value="m" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="finish" label="完成率" width="120">
                <template #default="{ row }">
                  <el-progress :percentage="row.finish" :stroke-width="6" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="140">
                <template #default>
                  <el-button link type="primary" size="small">编辑</el-button>
                  <el-button link size="small">上移</el-button>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- ========== 课程设置 ========== -->
        <el-tab-pane label="课程设置" name="settings">
          <div class="grid-2">
            <div>
              <div class="panel-title"><h3>学习规则</h3></div>
              <el-form label-position="top">
                <el-form-item v-for="s in settingSwitches" :key="s.key" class="switch-item">
                  <div class="switch-row">
                    <div>
                      <div class="sw-label">{{ s.label }}</div>
                      <div class="muted">{{ s.hint }}</div>
                    </div>
                    <el-switch v-model="s.on" />
                  </div>
                </el-form-item>
                <el-form-item label="结课标准（学习进度达到）">
                  <el-slider v-model="passProgress" :min="50" :max="100" :format-tooltip="v => v + '%'" />
                </el-form-item>
              </el-form>
            </div>
            <div>
              <div class="panel-title"><h3>视频播放控制</h3></div>
              <el-form label-position="top">
                <el-form-item v-for="s in videoSwitches" :key="s.key" class="switch-item">
                  <div class="switch-row">
                    <div>
                      <div class="sw-label">{{ s.label }}</div>
                      <div class="muted">{{ s.hint }}</div>
                    </div>
                    <el-switch v-model="s.on" />
                  </div>
                </el-form-item>
                <el-form-item label="允许倍速">
                  <el-checkbox-group v-model="speedOptions">
                    <el-checkbox value="1.0">1.0x</el-checkbox>
                    <el-checkbox value="1.25">1.25x</el-checkbox>
                    <el-checkbox value="1.5">1.5x</el-checkbox>
                    <el-checkbox value="2.0">2.0x</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
              </el-form>
            </div>
          </div>
          <div style="display:flex; justify-content:flex-end; gap:10px; margin-top:8px">
            <el-button @click="resetSettings">恢复默认</el-button>
            <el-button type="primary" @click="saveSettings">保存设置</el-button>
          </div>
        </el-tab-pane>

        <!-- ========== 助教与共建者 ========== -->
        <el-tab-pane label="助教与共建者" name="assistants">
          <div class="content-toolbar">
            <el-button type="primary" @click="assistantDialog = true">添加助教 / 共建者</el-button>
            <span class="muted">共 {{ assistants.length }} 人，权限仅作用于「{{ activeCourse.name }}」</span>
          </div>
          <el-table :data="assistants" border>
            <el-table-column label="成员" min-width="180">
              <template #default="{ row }">
                <div style="display:flex; align-items:center; gap:10px">
                  <div class="a-avatar">{{ row.name[0] }}</div>
                  <div>
                    <div style="font-weight:600">{{ row.name }}</div>
                    <div class="muted">{{ row.dept }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="角色" width="110">
              <template #default="{ row }">
                <el-tag :type="row.role === '共建者' ? 'warning' : 'primary'" effect="light" size="small">{{ row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column v-for="p in permCols" :key="p.key" :label="p.label" width="96" align="center">
              <template #default="{ row }">
                <el-switch :model-value="row.perms[p.key]" @change="v => row.perms[p.key] = Boolean(v)" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90" fixed="right">
              <template #default>
                <el-button link type="danger" size="small">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </template>

    <!-- 新建课程 -->
    <el-dialog v-model="createDialog" title="新建课程" width="660px">
      <div class="mode-switch">
        <el-radio-group v-model="createMode">
          <el-radio-button value="blank">空白创建</el-radio-button>
          <el-radio-button value="template">从模板库复制</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 从模板库复制 -->
      <template v-if="createMode === 'template'">
        <el-alert type="info" :closable="false" show-icon style="margin-bottom:12px"
          title="模板为系统预置的标准课程，复制后将带入完整的章节结构与任务点配置，可自由修改。" />
        <div class="tpl-grid">
          <button v-for="t in courseTemplates" :key="t.id" type="button" class="tpl-card"
            :class="{ on: tplId === t.id }" @click="pickTemplate(t)">
            <div class="tpl-cover" :style="{ background: t.cover }">
              <component :is="t.icon" :size="20" />
            </div>
            <div class="tpl-info">
              <div class="tpl-name">{{ t.name }}</div>
              <div class="tpl-meta">{{ t.tag }} · {{ t.hours }} 学时 · {{ t.chapters.length }} 章 {{ templatePoints(t) }} 任务点</div>
              <div class="tpl-desc">{{ t.desc }}</div>
            </div>
            <CheckCircle2 v-if="tplId === t.id" :size="18" class="tpl-check" />
          </button>
        </div>
        <el-form label-position="top" style="margin-top:14px">
          <div class="grid-2" style="gap:14px">
            <el-form-item label="课程名称" required>
              <el-input v-model="newCourse.name" placeholder="默认为模板名称，可修改" maxlength="30" show-word-limit />
            </el-form-item>
            <el-form-item label="开课学期">
              <el-select v-model="newCourse.semester" style="width:100%">
                <el-option v-for="s in semesterOptions.filter(x => x !== '全部学期')" :key="s" :label="s" :value="s" />
              </el-select>
            </el-form-item>
          </div>
        </el-form>
      </template>

      <!-- 空白创建 -->
      <el-form v-else label-position="top">
        <el-form-item label="课程名称" required>
          <el-input v-model="newCourse.name" placeholder="例如：短视频制作与运营" maxlength="30" show-word-limit />
        </el-form-item>
        <div class="grid-2" style="gap:14px">
          <el-form-item label="课程性质">
            <el-select v-model="newCourse.nature" style="width:100%">
              <el-option v-for="n in ['专业必修', '专业选修', '通识课']" :key="n" :label="n" :value="n" />
            </el-select>
          </el-form-item>
          <el-form-item label="总学时">
            <el-input-number v-model="newCourse.hours" :min="8" :max="128" :step="8" style="width:100%" />
          </el-form-item>
        </div>
        <el-form-item label="开课学期">
          <el-select v-model="newCourse.semester" style="width:100%">
            <el-option v-for="s in semesterOptions.filter(x => x !== '全部学期')" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程封面">
          <div class="cover-picker">
            <button v-for="(g, i) in coverPresets" :key="i" type="button" class="cp-item"
              :class="{ on: newCourse.coverIdx === i }" :style="{ background: g }"
              @click="newCourse.coverIdx = i" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmCreateCourse">
          {{ createMode === 'template' ? '复制模板并进入建设' : '创建并进入建设' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 公式编辑器（数学 / 化学） -->
    <FormulaEditorDialog v-model="formulaDialog" @insert="onFormulaInsert" />

    <!-- 新建章节 -->
    <el-dialog v-model="chapterDialog" title="新建章节" width="440px">
      <el-form label-position="top">
        <el-form-item label="所属课程">
          <el-input :model-value="activeCourse?.name" disabled />
        </el-form-item>
        <el-form-item label="章节名称">
          <el-input v-model="newChapterName" placeholder="例如：第 6 章 直播运营实战" />
        </el-form-item>
        <el-form-item label="开放模式">
          <el-radio-group v-model="newChapterMode">
            <el-radio v-for="m in accessModes" :key="m" :value="m">{{ m }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="chapterDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddChapter">创建</el-button>
      </template>
    </el-dialog>

    <!-- 添加助教 -->
    <el-dialog v-model="assistantDialog" title="添加助教 / 共建者" width="480px">
      <el-form label-position="top">
        <el-form-item label="搜索教师">
          <el-input placeholder="输入姓名或工号搜索" :prefix-icon="SearchIcon" />
        </el-form-item>
        <el-form-item label="协作角色">
          <el-radio-group v-model="assistantRole">
            <el-radio value="助教">助教</el-radio>
            <el-radio value="共建者">共建者</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-alert type="info" :closable="false" show-icon
          title="助教默认拥有批阅与成绩查看权限，共建者可编辑课程内容与资源。" />
      </el-form>
      <template #footer>
        <el-button @click="assistantDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddAssistant">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- 插入资源抽屉 -->
    <el-drawer v-model="insertDrawer" title="从资源库插入" size="420px">
      <el-input v-model="resKeyword" placeholder="搜索资源名称" :prefix-icon="SearchIcon" style="margin-bottom:12px" />
      <el-tabs v-model="resTab">
        <el-tab-pane label="视频" name="video" />
        <el-tab-pane label="文档" name="doc" />
        <el-tab-pane label="图片" name="img" />
      </el-tabs>
      <div class="res-list">
        <label v-for="r in filteredResources" :key="r.id" class="res-item">
          <el-checkbox v-model="r.checked" />
          <component :is="r.type === 'video' ? PlayCircle : r.type === 'doc' ? FileText : ImageIcon" :size="16" />
          <div class="r-meta">
            <strong>{{ r.name }}</strong>
            <span class="muted">{{ r.size }} · {{ r.time }}</span>
          </div>
        </label>
      </div>
      <template #footer>
        <el-button @click="insertDrawer = false">取消</el-button>
        <el-button type="primary" @click="confirmInsert">插入选中资源</el-button>
      </template>
    </el-drawer>

  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import katex from 'katex'
import 'katex/contrib/mhchem'
import FormulaEditorDialog from '../components/FormulaEditorDialog.vue'
import {
  Clapperboard, PlayCircle, FileText, ClipboardList, GripVertical,
  ChevronDown, Image as ImageIcon, Film, Paperclip, Link2, Sparkles,
  Search as SearchIcon, ArrowLeft, Plus, MoreVertical,
  BookOpen, Globe, Code2, Truck, Lightbulb, Languages, CheckCircle2
} from 'lucide-vue-next'
import { courses, chapterPoints, portalCourseId, type Course, type Chapter, type SectionType } from '../stores/courses'
import { openHomeworkForSection } from '../stores/homework-context'

const emit = defineEmits<{ navigate: [id: string] }>()

/* ---------- 课程列表筛选 ---------- */
const keyword = ref('')
const semesterFilter = ref('全部学期')
const statusFilter = ref('全部状态')
const semesterOptions = ['全部学期', '2026 春', '2025 秋', '2025 春']
const statusOptions = ['全部状态', '已发布', '建设中', '草稿']
const filteredCourses = computed(() => courses.value.filter(c => {
  const kw = keyword.value.trim()
  const hitKw = !kw || c.name.includes(kw) || c.code.includes(kw)
  const hitSem = semesterFilter.value === '全部学期' || c.semester === semesterFilter.value
  const hitStatus = statusFilter.value === '全部状态' || c.status === statusFilter.value
  return hitKw && hitSem && hitStatus
}))
const totalStudents = computed(() => courses.value.reduce((sum, c) => sum + c.students, 0))

const tab = ref('intro')
const chapterDialog = ref(false)
const assistantDialog = ref(false)
const insertDrawer = ref(false)
const createDialog = ref(false)
const newChapterName = ref('')
const newChapterMode = ref('开放')
const assistantRole = ref('助教')
const resKeyword = ref('')
const resTab = ref('video')
const passProgress = ref(80)
const speedOptions = ref(['1.0', '1.25', '1.5'])
const editorRef = ref<HTMLElement>()

const accessModes = ['开放', '定时开放', '闯关模式', '关闭']

/* ---------- 多课程数据：王琳老师承担的全部课程 ---------- */
/* ---------- 当前建设的课程 ---------- */
const activeCourseId = ref<number | null>(null)
const activeCourse = computed(() => courses.value.find(c => c.id === activeCourseId.value) ?? null)
const chapters = computed<Chapter[]>(() => activeCourse.value?.chapters ?? [])

function assignSectionHomework(chapter: Chapter, section: Chapter['sections'][number]) {
  if (!activeCourse.value) return
  openHomeworkForSection({ courseId: activeCourse.value.id, chapterId: chapter.id, sectionId: section.id, sectionTitle: section.title })
  emit('navigate', 'homework')
}
function openCourse(id: number) {
  activeCourseId.value = id
  tab.value = 'intro'
  window.scrollTo({ top: 0 })
}
function backToList() {
  activeCourseId.value = null
  window.scrollTo({ top: 0 })
}
/* 预览课程：跳到学生端上课页面的所见即所得编辑 */
function openPortal() {
  if (!activeCourse.value) return
  portalCourseId.value = activeCourse.value.id
  emit('navigate', 'portal')
}

/* ---------- 章节 / 小节拖拽排序 ---------- */
type DragItem = { kind: 'chapter'; index: number } | { kind: 'section'; ci: number; si: number }
type DropTarget =
  | { kind: 'chapter'; ci: number; pos: 'before' | 'after' }
  | { kind: 'section'; ci: number; si: number; pos: 'before' | 'after' }
  | { kind: 'into-chapter'; ci: number }

const dragState = ref<DragItem | null>(null)
const dropTarget = ref<DropTarget | null>(null)

function onDragStart(e: DragEvent, item: DragItem) {
  dragState.value = item
  e.dataTransfer?.setData('text/plain', item.kind)
  if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move'
}
function onDragEnd() {
  dragState.value = null
  dropTarget.value = null
}
function onRowDragOver(e: DragEvent, kind: 'chapter' | 'section', ci: number, si?: number) {
  if (!dragState.value || dragState.value.kind !== kind) return
  const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
  const pos = e.clientY < rect.top + rect.height / 2 ? 'before' : 'after'
  dropTarget.value = kind === 'chapter' ? { kind, ci, pos } : { kind, ci, si: si!, pos }
}
function onNodeDragOver(ci: number) {
  // 拖到章节空白区域：小节追加到该章末尾
  if (dragState.value?.kind === 'section') dropTarget.value = { kind: 'into-chapter', ci }
}
function rowDropClass(kind: 'chapter' | 'section', ci: number, si?: number) {
  const t = dropTarget.value
  if (!t || t.kind !== kind) return ''
  if (kind === 'chapter' && t.kind === 'chapter' && t.ci === ci) return t.pos === 'before' ? 'drop-before' : 'drop-after'
  if (kind === 'section' && t.kind === 'section' && t.ci === ci && t.si === si) return t.pos === 'before' ? 'drop-before' : 'drop-after'
  return ''
}
function commitDrop() {
  const d = dragState.value
  const t = dropTarget.value
  const list = activeCourse.value?.chapters
  if (!d || !t || !list) { onDragEnd(); return }
  if (d.kind === 'chapter') {
    if (t.kind !== 'chapter') { onDragEnd(); return }
    let to = t.ci + (t.pos === 'after' ? 1 : 0)
    if (to === d.index || to === d.index + 1) { onDragEnd(); return }
    const [moved] = list.splice(d.index, 1)
    if (d.index < to) to--
    list.splice(to, 0, moved)
    renumber()
  } else {
    if (t.kind !== 'section' && t.kind !== 'into-chapter') { onDragEnd(); return }
    if (t.kind === 'section' && t.ci === d.ci) {
      const to = t.si + (t.pos === 'after' ? 1 : 0)
      if (to === d.si || to === d.si + 1) { onDragEnd(); return }
    }
    const [moved] = list[d.ci].sections.splice(d.si, 1)
    if (t.kind === 'section') {
      let to = t.si + (t.pos === 'after' ? 1 : 0)
      if (t.ci === d.ci && d.si < to) to--
      list[t.ci].sections.splice(to, 0, moved)
    } else {
      list[t.ci].sections.push(moved)
    }
    renumber()
  }
  ElMessage.success('顺序已调整，章节编号已同步更新')
  onDragEnd()
}
/* 拖拽后自动重排「第 N 章」与「X.Y」编号 */
function renumber() {
  activeCourse.value?.chapters.forEach((ch, ci) => {
    ch.title = ch.title.replace(/^第\s*\d+\s*章/, `第 ${ci + 1} 章`)
    ch.sections.forEach((s, si) => {
      s.title = s.title.replace(/^\d+\.\d+/, `${ci + 1}.${si + 1}`)
    })
  })
}

/* ---------- 章节管理表格拖拽（章行整体携带小节） ---------- */
const manageTableEl = ref<HTMLElement>()

function manageBodyRows(): HTMLElement[] {
  return Array.from(
    manageTableEl.value?.querySelectorAll('.el-table__body-wrapper tbody tr') ?? []
  ) as HTMLElement[]
}
function clearDropIndicators() {
  manageBodyRows().forEach(r => r.classList.remove('drop-before', 'drop-after', 'drop-into'))
}
function clearManageIndicators() {
  clearDropIndicators()
  manageBodyRows().forEach(r => r.classList.remove('dragging-src'))
}
function onManageDragStart(e: DragEvent, row: { kind: 'chapter' | 'section'; ci: number; si: number }) {
  dragState.value = row.kind === 'chapter' ? { kind: 'chapter', index: row.ci } : { kind: 'section', ci: row.ci, si: row.si }
  e.dataTransfer?.setData('text/plain', row.kind)
  if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move'
  ;(e.target as HTMLElement).closest('tr')?.classList.add('dragging-src')
}
function onManageDragEnd() {
  clearManageIndicators()
  onDragEnd()
}
function onManageDragOver(e: DragEvent) {
  const d = dragState.value
  if (!d) return
  const tr = (e.target as HTMLElement).closest('tr')
  if (!tr || !manageTableEl.value?.contains(tr)) return
  const rows = manageBodyRows()
  const idx = rows.indexOf(tr as HTMLElement)
  const row = manageRows.value[idx]
  if (!row) return
  const rect = tr.getBoundingClientRect()
  const pos = e.clientY < rect.top + rect.height / 2 ? 'before' : 'after'
  clearDropIndicators()
  if (d.kind === 'chapter') {
    // 章节只能落在章节行：整章连同小节一起移动
    if (row.kind !== 'chapter') return
    dropTarget.value = { kind: 'chapter', ci: row.ci, pos }
    ;(tr as HTMLElement).classList.add(pos === 'before' ? 'drop-before' : 'drop-after')
  } else {
    // 小节：落在小节行插入其前/后；落在章节行则追加到该章末尾
    if (row.kind === 'section') {
      dropTarget.value = { kind: 'section', ci: row.ci, si: row.si, pos }
      ;(tr as HTMLElement).classList.add(pos === 'before' ? 'drop-before' : 'drop-after')
    } else {
      dropTarget.value = { kind: 'into-chapter', ci: row.ci }
      ;(tr as HTMLElement).classList.add('drop-into')
    }
  }
}
function onManageDragLeave(e: DragEvent) {
  if (!(e.relatedTarget as Node) || !manageTableEl.value?.contains(e.relatedTarget as Node)) {
    clearManageIndicators()
    dropTarget.value = null
  }
}
function commitManageDrop() {
  clearManageIndicators()
  commitDrop()
}

/* ---------- 课程卡片操作 ---------- */
function courseCommand(cmd: string, c: Course) {
  if (cmd === 'copy') {
    const copy: Course = {
      ...c, id: Date.now(), name: `${c.name}（副本）`, code: `${c.code}-C`,
      status: '草稿', classes: 0, students: 0, progress: c.progress,
      updated: new Date().toISOString().slice(0, 10),
      chapters: JSON.parse(JSON.stringify(c.chapters))
    }
    courses.value.unshift(copy)
    ElMessage.success(`已创建副本「${copy.name}」，可在列表顶部查看`)
  } else if (cmd === 'data') {
    ElMessage.info(`「${c.name}」本学期共有 ${c.students} 名学生，任务点平均完成率 ${Math.min(c.progress + 3, 100)}%`)
  } else if (cmd === 'delete') {
    ElMessageBox.confirm(`确定删除「${c.name}」吗？课程章节与资源将一并移除。`, '删除课程', {
      type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
    }).then(() => {
      courses.value = courses.value.filter(x => x.id !== c.id)
      if (activeCourseId.value === c.id) activeCourseId.value = null
      ElMessage.success('课程已删除')
    }).catch(() => undefined)
  }
}

/* ---------- 新建课程 ---------- */
const coverPresets = [
  'linear-gradient(135deg,#2f6bff,#6b4dff)',
  'linear-gradient(135deg,#0ea5a4,#22c55e)',
  'linear-gradient(135deg,#f59e0b,#ef4444)',
  'linear-gradient(135deg,#8b5cf6,#ec4899)'
]
const createMode = ref<'blank' | 'template'>('blank')
const tplId = ref<number | null>(null)
const newCourse = ref({ name: '', nature: '专业必修', hours: 32, semester: '2026-2027-1', coverIdx: 0 })

/* ---------- 系统预置课程模板库 ---------- */
interface CourseTemplate {
  id: number; name: string; tag: string; hours: number; desc: string
  cover: string; icon: unknown; chapters: Chapter[]
}
const ch = (id: number, title: string, points: number, secs: [string, SectionType, string][]): Chapter => ({
  id, title, points,
  sections: secs.map(([t, ty, d], i) => ({
    id: id * 100 + i, title: t, type: ty, taskPoint: ty !== 'doc', quiz: ty === 'quiz', duration: d
  }))
})
const courseTemplates: CourseTemplate[] = [
  {
    id: 101, name: '网页设计与前端基础', tag: '专业核心', hours: 48,
    desc: 'HTML / CSS / JavaScript 三件套入门到实战，含 3 个阶段性网页作品任务。',
    cover: 'linear-gradient(135deg,#2563eb,#06b6d4)', icon: Globe,
    chapters: [
      ch(1, '第 1 章 开发环境与 HTML 基础', 8, [['1.1 开发环境搭建', 'video', '09:12'], ['1.2 HTML 常用标签', 'video', '14:40'], ['1.3 章节测验', 'quiz', '10 题']]),
      ch(2, '第 2 章 CSS 布局与样式', 10, [['2.1 选择器与盒模型', 'video', '12:08'], ['2.2 Flex 与 Grid 布局', 'video', '16:32']]),
      ch(3, '第 3 章 JavaScript 交互入门', 9, [['3.1 语法基础', 'video', '13:55'], ['3.2 DOM 操作实战', 'video', '15:20'], ['3.3 阅读：编码规范', 'doc', 'PDF · 10 页']])
    ]
  },
  {
    id: 102, name: 'Python 程序设计', tag: '专业核心', hours: 48,
    desc: '零基础编程入门，覆盖语法、数据结构与文件处理，配套在线判题作业。',
    cover: 'linear-gradient(135deg,#0ea5a4,#84cc16)', icon: Code2,
    chapters: [
      ch(1, '第 1 章 Python 入门', 6, [['1.1 第一个 Python 程序', 'video', '08:36'], ['1.2 变量与数据类型', 'video', '12:24']]),
      ch(2, '第 2 章 流程控制', 8, [['2.1 分支与循环', 'video', '14:10'], ['2.2 章节测验', 'quiz', '12 题']]),
      ch(3, '第 3 章 函数与模块', 8, [['3.1 函数定义与调用', 'video', '11:48'], ['3.2 常用标准库', 'doc', 'PDF · 14 页']])
    ]
  },
  {
    id: 103, name: '电子商务物流管理', tag: '专业选修', hours: 32,
    desc: '仓储、配送与供应链协同，结合电商大促案例讲解物流方案设计。',
    cover: 'linear-gradient(135deg,#f97316,#eab308)', icon: Truck,
    chapters: [
      ch(1, '第 1 章 电商物流概述', 5, [['1.1 物流与供应链基础', 'video', '10:22'], ['1.2 阅读：行业报告节选', 'doc', 'PDF · 8 页']]),
      ch(2, '第 2 章 仓储与库存管理', 7, [['2.1 智能仓储系统', 'video', '13:04'], ['2.2 章节测验', 'quiz', '8 题']])
    ]
  },
  {
    id: 104, name: '大学生创新创业基础', tag: '通识课', hours: 16,
    desc: '双创通识课标准模板：商业模式、路演训练与创业计划书撰写。',
    cover: 'linear-gradient(135deg,#eab308,#ef4444)', icon: Lightbulb,
    chapters: [
      ch(1, '第 1 章 创新思维启蒙', 4, [['1.1 什么是创新', 'video', '09:30']]),
      ch(2, '第 2 章 商业模式画布', 6, [['2.1 九宫格拆解', 'video', '12:16'], ['2.2 实操：画布填写', 'doc', 'DOCX · 模板']])
    ]
  },
  {
    id: 105, name: '职场英语沟通', tag: '通识课', hours: 24,
    desc: '面向实习与求职场景，覆盖邮件写作、面试口语与会议表达。',
    cover: 'linear-gradient(135deg,#6366f1,#a855f7)', icon: Languages,
    chapters: [
      ch(1, '第 1 章 求职英语', 6, [['1.1 英文简历撰写', 'video', '11:08'], ['1.2 面试常见问答', 'video', '13:42']]),
      ch(2, '第 2 章 商务邮件写作', 6, [['2.1 邮件结构与语气', 'video', '10:26'], ['2.2 章节测验', 'quiz', '10 题']])
    ]
  },
  {
    id: 106, name: '短视频运营实战（实训）', tag: '实训课程', hours: 40,
    desc: '以真实账号为载体的实训模板：定位、起号、投放与复盘全流程。',
    cover: 'linear-gradient(135deg,#2f6bff,#6b4dff)', icon: Clapperboard,
    chapters: [
      ch(1, '第 1 章 账号定位与起号', 8, [['1.1 赛道分析', 'video', '12:30'], ['1.2 账号诊断实操', 'video', '14:56']]),
      ch(2, '第 2 章 内容投放与 DOU+', 8, [['2.1 投放策略', 'video', '13:18'], ['2.2 章节测验', 'quiz', '10 题']]),
      ch(3, '第 3 章 数据复盘', 6, [['3.1 核心指标复盘会', 'video', '15:44']])
    ]
  }
]
const templatePoints = (t: CourseTemplate) => t.chapters.reduce((s, c) => s + c.points, 0)

function pickTemplate(t: CourseTemplate) {
  tplId.value = t.id
  newCourse.value.name = t.name
}

/* 弹窗每次打开时重置为空白创建 */
watch(createDialog, v => {
  if (v) {
    createMode.value = 'blank'
    tplId.value = null
    newCourse.value.name = ''
  }
})

function confirmCreateCourse() {
  const id = Date.now()
  let course: Course
  if (createMode.value === 'template') {
    const t = courseTemplates.find(x => x.id === tplId.value)
    if (!t) { ElMessage.warning('请先从模板库选择一个课程模板'); return }
    const name = newCourse.value.name.trim() || t.name
    course = {
      id, name,
      code: `CSD-2026-${String(courses.value.length + 41).padStart(4, '0')}`,
      status: '草稿', level: t.tag === '通识课' ? '通识' : '高职',
      hours: t.hours, classes: 0, students: 0,
      semester: newCourse.value.semester, updated: new Date().toISOString().slice(0, 10),
      progress: Math.min(60 + t.chapters.length * 8, 95), icon: t.icon, cover: t.cover,
      intro: `本课程基于系统模板「${t.name}」创建。${t.desc}`,
      chapters: JSON.parse(JSON.stringify(t.chapters))
    }
  } else {
    if (!newCourse.value.name.trim()) { ElMessage.warning('请输入课程名称'); return }
    course = {
      id, name: newCourse.value.name.trim(),
      code: `CSD-2026-${String(courses.value.length + 41).padStart(4, '0')}`,
      status: '草稿', level: newCourse.value.nature === '通识课' ? '通识' : '高职',
      hours: newCourse.value.hours, classes: 0, students: 0,
      semester: newCourse.value.semester, updated: new Date().toISOString().slice(0, 10),
      progress: 5, icon: BookOpen, cover: coverPresets[newCourse.value.coverIdx],
      intro: '（请在此处填写课程定位与教学目标，支持插入视频、图片与公式。）',
      chapters: [
        {
          id: id + 1, title: '第 1 章 （待命名）', points: 0,
          sections: [{ id: id + 2, title: '1.1 新小节', type: 'video', taskPoint: false, quiz: false, duration: '00:00' }]
        }
      ]
    }
  }
  courses.value.unshift(course)
  createDialog.value = false
  createMode.value = 'blank'
  tplId.value = null
  newCourse.value = { name: '', nature: '专业必修', hours: 32, semester: '2026-2027-1', coverIdx: 0 }
  openCourse(id)
  ElMessage.success(`课程「${course.name}」已创建，开始建设章节内容吧`)
}

const manageRows = computed(() =>
  chapters.value.flatMap((ch, ci) => [
    { rowKey: `c-${ch.id}`, kind: 'chapter' as const, ci, si: -1, name: ch.title, type: '章节', taskPoint: true, mode: '开放', finish: 82 },
    ...ch.sections.map((s, si) => ({
      rowKey: `s-${s.id}`, kind: 'section' as const, ci, si,
      name: s.title,
      type: s.type === 'video' ? '视频' : s.type === 'doc' ? '文档' : '测验',
      taskPoint: s.taskPoint,
      mode: '开放',
      finish: 60 + ((s.id * 7) % 38)
    }))
  ])
)

const settingSwitches = ref([
  { key: 'download', label: '允许学生下载课件', hint: '关闭后仅支持在线预览', on: true },
  { key: 'note', label: '允许学生记笔记', hint: '笔记与视频时间点绑定', on: true },
  { key: 'discuss', label: '开放课程讨论区', hint: '教师可置顶、删除违规内容', on: true },
  { key: 'challenge', label: '闯关模式', hint: '完成上一章任务点后解锁下一章', on: false }
])

const videoSwitches = ref([
  { key: 'antiDrag', label: '防拖拽', hint: '未观看部分禁止拖动进度条跳过', on: true },
  { key: 'antiSwitch', label: '防切屏', hint: '切换窗口自动暂停并记录日志', on: true },
  { key: 'watchAll', label: '看完才计任务点', hint: '完整观看后自动标记完成', on: true },
  { key: 'quizGate', label: '随堂测验必答', hint: '答对后才可继续播放', on: false }
])

const permCols = [
  { key: 'edit', label: '课程编辑' },
  { key: 'resource', label: '资源管理' },
  { key: 'publish', label: '作业发布' },
  { key: 'grade', label: '作业批阅' },
  { key: 'score', label: '成绩查看' }
] as const
type PermKey = typeof permCols[number]['key']

const assistants = ref([
  { id: 1, name: '李思颖', dept: '数字媒体教研室', role: '助教', perms: { edit: false, resource: true, publish: true, grade: true, score: true } as Record<PermKey, boolean> },
  { id: 2, name: '周明远', dept: '电子商务教研室', role: '共建者', perms: { edit: true, resource: true, publish: false, grade: false, score: true } as Record<PermKey, boolean> },
  { id: 3, name: '陈嘉', dept: '信息工程学院', role: '助教', perms: { edit: false, resource: false, publish: true, grade: true, score: true } as Record<PermKey, boolean> }
])

const resourcePool = ref([
  { id: 1, name: '3.1 平台算法拆解.mp4', size: '86.4 MB', time: '2026-11-08', type: 'video', checked: false },
  { id: 2, name: '账号定位案例集.pdf', size: '4.2 MB', time: '2026-11-05', type: 'doc', checked: false },
  { id: 3, name: '运镜示范-推拉摇移.mp4', size: '122 MB', time: '2026-10-30', type: 'video', checked: false },
  { id: 4, name: '脚本模板（三幕结构）.docx', size: '68 KB', time: '2026-10-26', type: 'doc', checked: false },
  { id: 5, name: '优秀作业截图示例.png', size: '1.8 MB', time: '2026-10-22', type: 'img', checked: false }
])

const filteredResources = computed(() =>
  resourcePool.value.filter(r => r.type === resTab.value && r.name.includes(resKeyword.value))
)

function addSection() { ElMessage.info('请先在左侧选中章节，再添加小节') }
function insertResource(kind: string) {
  resTab.value = kind === '视频' ? 'video' : kind === '图片' ? 'img' : 'doc'
  insertDrawer.value = true
}
/* ---------- 公式编辑器（数学 / 化学） ---------- */
const formulaDialog = ref(false)
let savedRange: Range | null = null
function saveEditorRange() {
  const sel = window.getSelection()
  if (sel && sel.rangeCount > 0 && editorRef.value?.contains(sel.anchorNode)) {
    savedRange = sel.getRangeAt(0).cloneRange()
  }
}
function insertFormula() {
  saveEditorRange()
  formulaDialog.value = true
}
function onFormulaInsert(latex: string) {
  const html = katex.renderToString(latex, { throwOnError: false, displayMode: false, strict: false, trust: true })
  const editor = editorRef.value
  if (!editor) return
  const wrapper = document.createElement('span')
  wrapper.className = 'formula-embed'
  wrapper.setAttribute('contenteditable', 'false')
  wrapper.dataset.latex = latex
  wrapper.title = latex
  wrapper.innerHTML = html
  const sel = window.getSelection()
  if (savedRange && editor.contains(savedRange.commonAncestorContainer)) {
    sel?.removeAllRanges()
    sel?.addRange(savedRange)
    savedRange.deleteContents()
    savedRange.insertNode(wrapper)
    savedRange.setStartAfter(wrapper)
    savedRange.collapse(true)
  } else {
    editor.appendChild(wrapper)
  }
  editor.appendChild(document.createTextNode(' '))
  ElMessage.success('公式已插入课程介绍')
}
function insertLink() { ElMessage.success('已插入链接占位符') }
function aiPolish() { ElMessage.success('AI 已润色当前段落：语句更通顺，专业术语已统一') }

/* ---------- 富媒体粘贴：保留格式，图片转存本地并替换线上路径 ---------- */
const PASTE_ALLOWED_TAGS = new Set([
  'P', 'BR', 'H1', 'H2', 'H3', 'H4', 'H5', 'H6', 'STRONG', 'B', 'EM', 'I', 'U', 'S', 'STRIKE',
  'UL', 'OL', 'LI', 'BLOCKQUOTE', 'PRE', 'CODE', 'TABLE', 'THEAD', 'TBODY', 'TR', 'TD', 'TH',
  'A', 'IMG', 'FIGURE', 'FIGCAPTION', 'HR', 'SPAN', 'DIV', 'VIDEO', 'AUDIO', 'SOURCE'
])
const PASTE_ALLOWED_ATTRS: Record<string, string[]> = {
  A: ['href', 'target'], IMG: ['src', 'alt'], TD: ['colspan', 'rowspan'], TH: ['colspan', 'rowspan'],
  VIDEO: ['src', 'controls'], AUDIO: ['src', 'controls'], SOURCE: ['src', 'type']
}
const PASTE_VOID_TAGS = new Set(['BR', 'HR', 'IMG', 'SOURCE'])

function escapeHtml(t: string) {
  return t.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}
/** 白名单清洗：保留格式标签，剥离脚本 / 样式 / 事件属性 */
function sanitizePasteNode(node: Node): string {
  if (node.nodeType === Node.TEXT_NODE) return escapeHtml(node.textContent ?? '')
  if (node.nodeType !== Node.ELEMENT_NODE) return ''
  const el = node as HTMLElement
  const tag = el.tagName
  if (['SCRIPT', 'STYLE', 'IFRAME', 'META', 'LINK', 'OBJECT', 'EMBED'].includes(tag)) return ''
  const inner = [...el.childNodes].map(sanitizePasteNode).join('')
  if (!PASTE_ALLOWED_TAGS.has(tag)) return inner
  if (tag === 'DIV') return inner.trim() ? `<p>${inner}</p>` : ''
  if (tag === 'SPAN') return inner
  const attrs = (PASTE_ALLOWED_ATTRS[tag] ?? [])
    .map(a => {
      const v = el.getAttribute(a)
      return v ? ` ${a.toLowerCase()}="${v.replace(/"/g, '&quot;')}"` : ''
    })
    .join('')
  const t = tag.toLowerCase()
  return PASTE_VOID_TAGS.has(tag) ? `<${t}${attrs}>` : `<${t}${attrs}>${inner}</${t}>`
}

function blobToDataUrl(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const r = new FileReader()
    r.onload = () => resolve(r.result as string)
    r.onerror = reject
    r.readAsDataURL(blob)
  })
}
/** 抓取线上图片并转存为本地资源（dataURL 内嵌，等同本地上传后的资源内容） */
async function localizeImage(src: string): Promise<string | null> {
  try {
    const resp = await fetch(src)
    if (!resp.ok) return null
    const blob = await resp.blob()
    if (!blob.type.startsWith('image/')) return null
    if (blob.size > 8 * 1024 * 1024) return null // 超过 8MB 不内嵌
    return await blobToDataUrl(blob)
  } catch {
    return null // 跨域 / 网络失败
  }
}

async function onEditorPaste(e: ClipboardEvent) {
  const cd = e.clipboardData
  if (!cd) return
  const html = cd.getData('text/html')
  const imgFiles = [...(cd.files ?? [])].filter(f => f.type.startsWith('image/'))
  if (!html && !imgFiles.length) return // 纯文本走浏览器默认粘贴
  e.preventDefault()

  let pasted = 0, localized = 0, kept = 0
  let fragHtml = ''

  if (html) {
    const doc = new DOMParser().parseFromString(html, 'text/html')
    const imgs = [...doc.querySelectorAll('img')]
    pasted += imgs.length
    await Promise.all(imgs.map(async img => {
      const src = img.getAttribute('src') ?? ''
      if (!src || src.startsWith('data:') || src.startsWith('blob:')) return
      const local = await localizeImage(src)
      if (local) { img.setAttribute('src', local); localized++ } else kept++
    }))
    fragHtml += [...doc.body.childNodes].map(sanitizePasteNode).join('')
  }
  // 截图 / 复制的本地图片文件：直接读取并转存
  for (const f of imgFiles) {
    fragHtml += `<img src="${await blobToDataUrl(f)}" alt="粘贴图片">`
    pasted++; localized++
  }
  if (!fragHtml.trim()) return

  const editor = editorRef.value
  editor?.focus()
  document.execCommand('insertHTML', false, fragHtml)

  if (!pasted) ElMessage.success('已按原格式粘贴（标题 / 列表 / 表格 / 加粗等样式已保留）')
  else if (!kept) ElMessage.success(`已粘贴 ${pasted} 张图片，全部转存本地并替换线上路径`)
  else ElMessage.warning(`已粘贴 ${pasted} 张图片：${localized} 张转存本地，${kept} 张受跨域限制保留原路径`)
}
function confirmInsert() {
  const n = resourcePool.value.filter(r => r.checked).length
  insertDrawer.value = false
  ElMessage.success(n ? `已插入 ${n} 个资源到当前章节` : '未选择资源')
  resourcePool.value.forEach(r => (r.checked = false))
}
function confirmAddChapter() {
  if (!newChapterName.value.trim()) { ElMessage.warning('请输入章节名称'); return }
  activeCourse.value?.chapters.push({
    id: Date.now(), title: newChapterName.value.trim(), points: 0,
    sections: [{ id: Date.now() + 1, title: '新小节', type: 'video', taskPoint: false, quiz: false, duration: '00:00' }]
  })
  chapterDialog.value = false
  newChapterName.value = ''
  ElMessage.success(`章节已创建到「${activeCourse.value?.name}」`)
}
function confirmAddAssistant() {
  assistantDialog.value = false
  ElMessage.success(`已发送${assistantRole.value}邀请，待对方确认后生效`)
}
function saveDraft() { ElMessage.success(`「${activeCourse.value?.name}」草稿已保存 · ` + new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })) }
function publish() { ElMessage.success(`「${activeCourse.value?.name}」已发布，学生端立即同步更新`) }
function saveSettings() { ElMessage.success('课程设置已保存并应用到该课程全部班级') }
function resetSettings() { ElMessage.info('已恢复为系统默认设置') }
</script>

<style scoped>
/* ---------- 课程列表 ---------- */
.list-head { display: flex; align-items: center; justify-content: space-between; gap: 14px; flex-wrap: wrap; }
.lh-info h2 { margin: 0; font-size: 18px; }
.lh-info p { margin: 6px 0 0; font-size: 12.5px; }
.list-tools { display: flex; gap: 10px; flex-wrap: wrap; }
.course-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(330px, 1fr)); gap: 16px; }
.course-card { padding: 0; overflow: hidden; cursor: pointer; display: flex; flex-direction: column; transition: transform .15s ease, box-shadow .15s ease; }
.course-card:hover { transform: translateY(-2px); box-shadow: 0 10px 28px rgba(31, 52, 110, .12); }
.cc-cover { height: 96px; color: #fff; display: flex; align-items: center; justify-content: center; position: relative; }
.cc-status { position: absolute; top: 10px; right: 10px; border: none; }
.cc-body { padding: 14px 16px 10px; display: flex; flex-direction: column; gap: 8px; }
.cc-name { font-size: 15.5px; font-weight: 700; }
.cc-meta { font-size: 12px; color: var(--app-ink-3); }
.cc-stats { display: flex; gap: 14px; font-size: 12px; color: var(--app-ink-3); }
.cc-stats strong { color: var(--app-ink-1); font-size: 13.5px; margin-right: 2px; }
.cc-progress { display: flex; align-items: center; gap: 10px; font-size: 12px; }
.cc-progress .muted { white-space: nowrap; }
.cc-foot { display: flex; align-items: center; justify-content: space-between; gap: 10px; padding: 10px 16px; border-top: 1px solid var(--app-line); font-size: 12px; margin-top: auto; }
.empty-box { grid-column: 1 / -1; display: flex; flex-direction: column; align-items: center; gap: 10px; padding: 46px 0; }
.cover-picker { display: flex; gap: 10px; }
.cp-item { width: 64px; height: 40px; border-radius: 8px; border: 2px solid transparent; cursor: pointer; }
.cp-item.on { border-color: var(--el-color-primary); box-shadow: 0 0 0 2px var(--el-color-primary-light-8); }
.mode-switch { display: flex; justify-content: center; margin-bottom: 16px; }
.tpl-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; max-height: 300px; overflow-y: auto; padding-right: 4px; }
.tpl-card {
  position: relative; display: flex; gap: 10px; align-items: flex-start; text-align: left;
  border: 1px solid var(--app-line); border-radius: 10px; padding: 10px; background: #fff; cursor: pointer;
  transition: border-color .15s ease, box-shadow .15s ease;
}
.tpl-card:hover { border-color: var(--el-color-primary); }
.tpl-card.on { border-color: var(--el-color-primary); box-shadow: 0 0 0 2px var(--el-color-primary-light-8); }
.tpl-cover { width: 46px; height: 46px; border-radius: 9px; color: #fff; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.tpl-info { min-width: 0; display: flex; flex-direction: column; gap: 3px; }
.tpl-name { font-size: 13.5px; font-weight: 700; color: var(--app-ink-1); }
.tpl-meta { font-size: 11.5px; color: var(--el-color-primary); }
.tpl-desc { font-size: 11.5px; color: var(--app-ink-3); line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.tpl-check { position: absolute; top: 8px; right: 8px; color: var(--el-color-primary); }
.editor-body :deep(.formula-embed) {
  padding: 1px 5px; border-radius: 5px; background: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-7); cursor: default; white-space: nowrap;
}
@media (max-width: 700px) { .tpl-grid { grid-template-columns: 1fr; } }

/* ---------- 单课程建设 ---------- */
.back-btn { margin-right: 2px; }
.course-head { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
.cover {
  width: 64px; height: 64px; border-radius: 12px; flex-shrink: 0; color: #fff;
  display: flex; align-items: center; justify-content: center;
}
.info { flex: 1; min-width: 240px; }
.name-row { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.name-row h2 { margin: 0; font-size: 18px; }
.meta { margin: 6px 0 0; font-size: 12.5px; color: var(--app-ink-3); }
.course-switcher { width: 190px; }
.actions { display: flex; gap: 10px; }
.tabs-panel :deep(.el-tabs__header) { margin-bottom: 18px; }
.intro-grid { display: grid; grid-template-columns: 1fr 280px; gap: 20px; }
.toc { display: flex; flex-direction: column; gap: 8px; }
.toc-item { border: 1px solid var(--app-line); border-radius: 8px; padding: 10px 12px; cursor: pointer; display: flex; flex-direction: column; gap: 3px; transition: border-color .15s ease; }
.toc-item:hover { border-color: var(--el-color-primary); }
.toc-item strong { font-size: 13px; }
.content-toolbar { display: flex; gap: 10px; align-items: center; margin-bottom: 14px; flex-wrap: wrap; }
.drag-hint { display: inline-flex; align-items: center; gap: 4px; margin-left: auto; }
.grip { display: inline-flex; align-items: center; color: var(--app-ink-3); cursor: grab; border-radius: 4px; padding: 2px; }
.grip:hover { background: var(--el-color-primary-light-9); color: var(--el-color-primary); }
.grip:active { cursor: grabbing; }
.chapter-row.dragging, .section-row.dragging { opacity: .45; }
.chapter-row.drop-before, .section-row.drop-before { box-shadow: inset 0 2px 0 var(--el-color-primary); }
.chapter-row.drop-after, .section-row.drop-after { box-shadow: inset 0 -2px 0 var(--el-color-primary); }
.chapter-node.drop-into { outline: 1.5px dashed var(--el-color-primary); outline-offset: 3px; border-radius: 8px; background: var(--el-color-primary-light-9); }
.manage-table :deep(tr.drop-before td) { box-shadow: inset 0 2px 0 var(--el-color-primary); }
.manage-table :deep(tr.drop-after td) { box-shadow: inset 0 -2px 0 var(--el-color-primary); }
.manage-table :deep(tr.drop-into td) { background: var(--el-color-primary-light-9); }
.manage-table :deep(tr.dragging-src td) { opacity: .45; }
.switch-item { margin-bottom: 6px; }
.switch-row { display: flex; align-items: center; justify-content: space-between; width: 100%; gap: 16px; }
.sw-label { font-size: 14px; font-weight: 600; }
.a-avatar {
  width: 32px; height: 32px; border-radius: 50%; background: var(--el-color-primary-light-9);
  color: var(--el-color-primary); display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 13px;
}
.res-list { display: flex; flex-direction: column; gap: 6px; }
.res-item { display: flex; align-items: center; gap: 10px; padding: 9px 10px; border: 1px solid var(--app-line); border-radius: 8px; cursor: pointer; }
.res-item:hover { border-color: var(--el-color-primary); }
.r-meta { display: flex; flex-direction: column; min-width: 0; }
.r-meta strong { font-size: 13px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
@media (max-width: 960px) {
  .intro-grid { grid-template-columns: 1fr; }
  .course-switcher { width: 100%; }
}
</style>

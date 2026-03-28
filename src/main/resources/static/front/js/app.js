// API 基础地址
const API_BASE = 'http://localhost:8080';

// 当前选中的警报筛选
let currentAlertFilter = 'all';

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    loadStations();
    loadAlerts();
    setupForms();
    setupAlertFilters();
});

// 标签页切换
function initTabs() {
    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const target = tab.dataset.tab;
            
            // 切换按钮状态
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            
            // 切换内容
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            document.getElementById(target).classList.add('active');
            
            // 加载对应数据
            if (target === 'stations') loadStations();
            if (target === 'report') loadStationsForSelect();
            if (target === 'records') {
                loadStationsForRecord();
                loadRecords();
            }
            if (target === 'alerts') loadAlerts();
        });
    });
}

// ==================== 站点管理 ====================

// 加载站点列表
async function loadStations() {
    const container = document.getElementById('stations-list');
    container.innerHTML = '<div class="loading"><div class="spinner"></div>加载中...</div>';
    
    try {
        const res = await fetch(`${API_BASE}/stations`);
        const data = await res.json();
        
        if (data.code !== 200 || !data.data || data.data.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="grid-column: 1/-1;">
                    <div class="empty-state-icon">📍</div>
                    <div class="empty-state-text">暂无监测站点</div>
                </div>
            `;
            return;
        }
        
        container.innerHTML = data.data.map(station => `
            <div class="station-card" data-id="${station.id}">
                <div class="station-header">
                    <span class="station-name">${station.name}</span>
                    <span class="station-status ${station.status === 1 ? 'status-normal' : 'status-disabled'}">
                        ${station.status === 1 ? '正常' : '停用'}
                    </span>
                </div>
                <div class="station-location">📍 ${station.location || '暂无位置信息'}</div>
                <div class="water-levels">
                    <div class="level-item warn">
                        <div class="level-value">${station.warnLevel}m</div>
                        <div class="level-label">警戒水位</div>
                    </div>
                    <div class="level-item danger">
                        <div class="level-value">${station.dangerLevel}m</div>
                        <div class="level-label">危险水位</div>
                    </div>
                </div>
                <div class="station-actions">
                    <button class="btn btn-sm btn-secondary" onclick="viewStationDetail(${station.id})">查看</button>
                    <button class="btn btn-sm btn-danger" onclick="disableStation(${station.id})">停用</button>
                </div>
            </div>
        `).join('');
        
    } catch (error) {
        container.innerHTML = `<div class="empty-state" style="grid-column: 1/-1;">加载失败: ${error.message}</div>`;
    }
}

// 加载站点到选择框
async function loadStationsForSelect() {
    const select = document.getElementById('report-station');
    const recordSelect = document.getElementById('record-station');
    
    try {
        const res = await fetch(`${API_BASE}/stations`);
        const data = await res.json();
        
        if (data.code === 200 && data.data) {
            const options = data.data.map(s => `<option value="${s.id}">${s.name}</option>`).join('');
            select.innerHTML = '<option value="">请选择站点</option>' + options;
            recordSelect.innerHTML = '<option value="">全部站点</option>' + options;
        }
    } catch (error) {
        console.error('加载站点失败:', error);
    }
}

// 加载站点到记录筛选
async function loadStationsForRecord() {
    await loadStationsForSelect();
}

// 打开新增站点弹窗
function openAddStationModal() {
    document.getElementById('station-modal').classList.add('show');
}

// 关闭弹窗
function closeModal() {
    document.getElementById('station-modal').classList.remove('show');
    document.getElementById('station-form').reset();
}

// 查看站点详情
async function viewStationDetail(id) {
    try {
        const res = await fetch(`${API_BASE}/stations/${id}`);
        const data = await res.json();
        
        if (data.code === 200 && data.data) {
            const s = data.data;
            alert(`站点详情:\n名称: ${s.name}\n位置: ${s.location || '暂无'}\n警戒水位: ${s.warnLevel}m\n危险水位: ${s.dangerLevel}m`);
        }
    } catch (error) {
        showToast('获取站点详情失败', 'error');
    }
}

// 停用站点
async function disableStation(id) {
    if (!confirm('确定要停用该站点吗？')) return;
    
    try {
        const res = await fetch(`${API_BASE}/stations/${id}`, { method: 'DELETE' });
        const data = await res.json();
        
        if (data.code === 200) {
            showToast('站点已停用', 'success');
            loadStations();
        } else {
            showToast(data.message || '停用失败', 'error');
        }
    } catch (error) {
        showToast('停用失败: ' + error.message, 'error');
    }
}

// ==================== 表单提交 ====================

function setupForms() {
    // 新增站点表单
    document.getElementById('station-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const station = {
            name: document.getElementById('station-name').value,
            location: document.getElementById('station-location').value,
            warnLevel: parseFloat(document.getElementById('station-warn').value),
            dangerLevel: parseFloat(document.getElementById('station-danger').value)
        };
        
        try {
            const res = await fetch(`${API_BASE}/stations`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(station)
            });
            
            const data = await res.json();
            if (data.code === 200) {
                showToast('站点添加成功', 'success');
                closeModal();
                loadStations();
            } else {
                showToast(data.message || '添加失败', 'error');
            }
        } catch (error) {
            showToast('添加失败: ' + error.message, 'error');
        }
    });
    
    // 水位上报表单
    document.getElementById('report-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const report = {
            stationId: parseInt(document.getElementById('report-station').value),
            waterLevel: parseFloat(document.getElementById('report-level').value),
            recordedAt: document.getElementById('report-time').value || null
        };
        
        try {
            const res = await fetch(`${API_BASE}/water/report`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(report)
            });
            
            const data = await res.json();
            if (data.code === 200) {
                showToast('水位上报成功', 'success');
                document.getElementById('report-form').reset();
                document.getElementById('alert-preview').style.display = 'none';
                loadAlerts();
            } else {
                showToast(data.message || '上报失败', 'error');
            }
        } catch (error) {
            showToast('上报失败: ' + error.message, 'error');
        }
    });
    
    // 水位输入实时预览警报
    document.getElementById('report-level').addEventListener('input', checkAlertLevel);
    document.getElementById('report-station').addEventListener('change', checkAlertLevel);
}

// 检查警报级别
async function checkAlertLevel() {
    const stationId = document.getElementById('report-station').value;
    const waterLevel = parseFloat(document.getElementById('report-level').value);
    const preview = document.getElementById('alert-preview');
    const text = document.getElementById('alert-text');
    
    if (!stationId || isNaN(waterLevel)) {
        preview.style.display = 'none';
        return;
    }
    
    try {
        const res = await fetch(`${API_BASE}/stations/${stationId}`);
        const data = await res.json();
        
        if (data.code === 200 && data.data) {
            const station = data.data;
            preview.style.display = 'block';
            
            if (waterLevel >= station.dangerLevel) {
                preview.querySelector('.alert-box').className = 'alert-box danger';
                text.textContent = `⚠️ 危险！当前水位 ${waterLevel}m 已超过危险水位 ${station.dangerLevel}m`;
            } else if (waterLevel >= station.warnLevel) {
                preview.querySelector('.alert-box').className = 'alert-box warning';
                text.textContent = `⚡ 警戒！当前水位 ${waterLevel}m 已超过警戒水位 ${station.warnLevel}m`;
            } else {
                preview.querySelector('.alert-box').className = 'alert-box';
                preview.querySelector('.alert-box').style.background = '#d4edda';
                preview.querySelector('.alert-box').style.borderColor = '#28a745';
                preview.querySelector('.alert-box').style.color = '#155724';
                text.textContent = `✅ 正常，当前水位 ${waterLevel}m 处于安全范围`;
            }
        }
    } catch (error) {
        console.error('检查水位失败:', error);
    }
}

// ==================== 历史记录 ====================

async function loadRecords() {
    const tbody = document.getElementById('records-list');
    tbody.innerHTML = '<tr><td colspan="4" class="loading"><div class="spinner"></div>加载中...</td></tr>';
    
    const stationId = document.getElementById('record-station').value;
    const startTime = document.getElementById('record-start').value;
    const endTime = document.getElementById('record-end').value;
    
    let url = `${API_BASE}/water/records?`;
    if (stationId) url += `stationId=${stationId}&`;
    if (startTime) url += `startTime=${startTime}&`;
    if (endTime) url += `endTime=${endTime}&`;
    
    try {
        const res = await fetch(url);
        const data = await res.json();
        
        if (data.code !== 200 || !data.data || data.data.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="empty-state">
                        <div class="empty-state-icon">📊</div>
                        <div class="empty-state-text">暂无记录</div>
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = data.data.map(record => {
            const levelClass = getLevelClass(record.waterLevel, record.stationId);
            return `
                <tr>
                    <td>${record.stationName || '未知站点'}</td>
                    <td><span class="level-badge ${levelClass}">${record.waterLevel}m</span></td>
                    <td>${formatDateTime(record.recordedAt)}</td>
                    <td>${levelClass === 'normal' ? '正常' : levelClass === 'warning' ? '警戒' : '危险'}</td>
                </tr>
            `;
        }).join('');
        
    } catch (error) {
        tbody.innerHTML = `<tr><td colspan="4">加载失败: ${error.message}</td></tr>`;
    }
}

// 获取水位级别样式
function getLevelClass(waterLevel, stationId) {
    // 简化处理，实际应该根据站点阈值判断
    return 'normal';
}

// ==================== 警报管理 ====================

function setupAlertFilters() {
    document.querySelectorAll('.filter-btns .btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.filter-btns .btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentAlertFilter = btn.dataset.filter;
            loadAlerts();
        });
    });
}

async function loadAlerts() {
    const container = document.getElementById('alerts-list');
    container.innerHTML = '<div class="loading"><div class="spinner"></div>加载中...</div>';
    
    let url = `${API_BASE}/alerts`;
    if (currentAlertFilter !== 'all') {
        url += `?status=${currentAlertFilter}`;
    }
    
    try {
        const res = await fetch(url);
        const data = await res.json();
        
        // 更新未处理警报数量
        updateAlertBadge(data.data);
        
        if (data.code !== 200 || !data.data || data.data.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">🎉</div>
                    <div class="empty-state-text">暂无警报</div>
                </div>
            `;
            return;
        }
        
        container.innerHTML = data.data.map(alert => `
            <div class="alert-card ${alert.alertType === 2 ? 'danger' : 'warning'} ${alert.status === 1 ? 'handled' : ''}">
                <div class="alert-icon">${alert.alertType === 2 ? '🔴' : '🟡'}</div>
                <div class="alert-info">
                    <div class="alert-title">
                        ${alert.stationName} - ${alert.alertTypeDesc}警报
                        <span class="level-badge ${alert.alertType === 2 ? 'danger' : 'warning'}">${alert.waterLevel}m</span>
                    </div>
                    <div class="alert-detail">
                        ${alert.status === 0 ? '待处理' : '已处理'} · 
                        ${formatDateTime(alert.createdAt)}
                    </div>
                </div>
                <div class="alert-actions">
                    ${alert.status === 0 ? `
                        <button class="btn btn-success btn-sm" onclick="handleAlert(${alert.id})">标记已处理</button>
                    ` : `<span class="btn btn-sm" disabled>已处理</span>`}
                </div>
            </div>
        `).join('');
        
    } catch (error) {
        container.innerHTML = `<div class="empty-state">加载失败: ${error.message}</div>`;
    }
}

// 更新警报徽章
function updateAlertBadge(alerts) {
    const badge = document.getElementById('alert-badge');
    if (!alerts) {
        badge.style.display = 'none';
        return;
    }
    const unhandled = alerts.filter(a => a.status === 0).length;
    badge.textContent = unhandled;
    badge.style.display = unhandled > 0 ? 'inline-block' : 'none';
}

// 处理警报
async function handleAlert(id) {
    try {
        const res = await fetch(`${API_BASE}/alerts/${id}/handle`, {
            method: 'PUT'
        });
        
        const data = await res.json();
        if (data.code === 200) {
            showToast('警报已处理', 'success');
            loadAlerts();
        } else {
            showToast(data.message || '处理失败', 'error');
        }
    } catch (error) {
        showToast('处理失败: ' + error.message, 'error');
    }
}

// ==================== 工具函数 ====================

// 格式化日期时间
function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '-';
    const date = new Date(dateTimeStr);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Toast 提示
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100px)';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

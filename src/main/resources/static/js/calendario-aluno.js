'use strict';
 
// ========== ESTADO DA APLICAÇÃO ==========
const estado = {
    dataAtual: new Date(),
    mesAtual: new Date().getMonth(),
    anoAtual: new Date().getFullYear(),
    diaSelecionado: null,
    avisos: {},
    anotacoes: {},
    termoBusca: ''
};
 
// ========== INICIALIZAÇÃO ==========
document.addEventListener('DOMContentLoaded', () => {
    carregarDados();
    inicializarAplicacao();
    configurarEventos();
});
 
/**
 * Inicializa a aplicação com dados do backend
 */
function inicializarAplicacao() {
    // Nome do aluno vindo do backend via Thymeleaf
    const nomeEl = document.getElementById('userName');
    if (nomeEl && typeof alunoNome !== 'undefined') {
        nomeEl.textContent = alunoNome;
    }
 
    renderizarCalendario();
    atualizarDisplayMes();
    carregarAvisosDoBackend();
}
 
/**
 * Configura todos os event listeners
 */
function configurarEventos() {
    document.getElementById('prevMonth')?.addEventListener('click', () => navegarMes(-1));
    document.getElementById('nextMonth')?.addEventListener('click', () => navegarMes(1));
 
    document.getElementById('scrollUp')?.addEventListener('click', () => rolarSidebar(-100));
    document.getElementById('scrollDown')?.addEventListener('click', () => rolarSidebar(100));
 
    document.getElementById('searchInput')?.addEventListener('input', (e) => {
        estado.termoBusca = e.target.value.toLowerCase();
        renderizarCalendario();
    });
 
    document.querySelector('.close')?.addEventListener('click', fecharModal);
    document.getElementById('dayModal')?.addEventListener('click', (e) => {
        if (e.target === document.getElementById('dayModal')) fecharModal();
    });
 
    document.getElementById('btnAddEvent')?.addEventListener('click', adicionarAnotacao);
 
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') fecharModal();
    });
}
 
/**
 * Carrega anotações do localStorage
 */
function carregarDados() {
    const anotacoesSalvas = localStorage.getItem('aev_anotacoes_aluno');
    estado.anotacoes = anotacoesSalvas ? JSON.parse(anotacoesSalvas) : {};
}
 
/**
 * Salva anotações no localStorage
 */
function salvarDados() {
    localStorage.setItem('aev_anotacoes_aluno', JSON.stringify(estado.anotacoes));
}
 
/**
 * Busca avisos do professor para todas as turmas do aluno
 */
async function carregarAvisosDoBackend() {
    if (typeof alunoTurmas === 'undefined' || alunoTurmas.length === 0) return;
 
    const lista = document.getElementById('activitiesList');
    if (lista) lista.innerHTML = '<p style="padding:1rem;text-align:center;">Carregando avisos...</p>';
 
    const todosAvisos = [];
 
    for (const turma of alunoTurmas) {
        try {
            const response = await fetch(`/agendaescolar/aluno/avisos?turmaId=${turma.id}`);
            if (response.ok) {
                const avisos = await response.json();
                avisos.forEach(aviso => {
                    todosAvisos.push({ ...aviso, turmaNome: turma.nome });
                });
            }
        } catch (error) {
            console.error('Erro ao buscar avisos da turma:', turma.nome);
        }
    }
 
    // Organiza avisos por data
    todosAvisos.forEach(aviso => {
        const data = aviso.dataEvento ? aviso.dataEvento.substring(0, 10) : (aviso.createdAt ? aviso.createdAt.substring(0, 10) : new Date().toISOString().substring(0, 10));
        if (!estado.avisos[data]) estado.avisos[data] = [];
        estado.avisos[data].push(aviso);
    });
 
    renderizarCalendario();
    renderizarAvisosSidebar(todosAvisos);
}
 
/**
 * Renderiza avisos na sidebar
 */
function renderizarAvisosSidebar(avisos) {
    const lista = document.getElementById('activitiesList');
    if (!lista) return;
 
    lista.innerHTML = '';
 
    if (avisos.length === 0) {
        lista.innerHTML = '<p style="padding:1rem;text-align:center;">Nenhum aviso encontrado.</p>';
        return;
    }
 
    avisos.forEach(aviso => {
        const item = document.createElement('div');
        item.className = 'activity-item';
        item.setAttribute('role', 'listitem');
        item.innerHTML = `
            <div class="activity-icon" aria-hidden="true">📢</div>
            <div class="activity-info">
                <span class="activity-name">${aviso.titulo}</span>
                <span class="activity-date">${aviso.turmaNome}</span>
            </div>
        `;
        lista.appendChild(item);
    });
}
 
// ========== CALENDÁRIO ==========
 
/**
 * Renderiza o calendário do mês atual
 */
function renderizarCalendario() {
    const grid = document.getElementById('calendarGrid');
    if (!grid) return;
 
    grid.innerHTML = '';
 
    const diasSemana = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
    grid.appendChild(criarCelulaVazia());
 
    diasSemana.forEach(dia => {
        const header = document.createElement('div');
        header.className = 'day-header';
        header.setAttribute('role', 'columnheader');
        header.textContent = dia;
        grid.appendChild(header);
    });
 
    const primeiroDia = new Date(estado.anoAtual, estado.mesAtual, 1);
    const ultimoDia = new Date(estado.anoAtual, estado.mesAtual + 1, 0);
    const totalDias = ultimoDia.getDate();
    const diaSemanaInicio = primeiroDia.getDay();
 
    let diaAtual = 1;
    let semanaAtual = 1;
 
    for (let semana = 0; semana < 6; semana++) {
        const weekNum = document.createElement('div');
        weekNum.className = 'week-number';
        weekNum.textContent = semanaAtual++;
        grid.appendChild(weekNum);
 
        for (let dia = 0; dia < 7; dia++) {
            const celula = document.createElement('div');
            celula.className = 'calendar-cell';
            celula.setAttribute('role', 'gridcell');
 
            if (semana === 0 && dia < diaSemanaInicio) {
                celula.classList.add('empty');
            } else if (diaAtual <= totalDias) {
                const dataStr = formatarData(estado.anoAtual, estado.mesAtual, diaAtual);
                const dataObj = new Date(estado.anoAtual, estado.mesAtual, diaAtual);
                const ehHoje = isDataHoje(dataObj);
                const ehFimDeSemana = dia === 0 || dia === 6;
 
                celula.dataset.data = dataStr;
 
                const dayNum = document.createElement('span');
                dayNum.className = 'day-number';
                dayNum.textContent = diaAtual;
                celula.appendChild(dayNum);
 
                if (ehHoje) celula.classList.add('today');
                if (ehFimDeSemana) celula.classList.add('weekend');
 
                const avisosDoDia = estado.avisos[dataStr] || [];
                const anotacoesDoDia = estado.anotacoes[dataStr] || [];
 
                if (avisosDoDia.length > 0 || anotacoesDoDia.length > 0) {
                    celula.classList.add('has-event');
                    const indicators = document.createElement('div');
                    indicators.className = 'day-events';
 
                    if (avisosDoDia.length > 0) {
                        const dot = document.createElement('span');
                        dot.className = 'day-event-dot event-type-2';
                        indicators.appendChild(dot);
                    }
                    if (anotacoesDoDia.length > 0) {
                        const dot = document.createElement('span');
                        dot.className = 'day-event-dot event-type-1';
                        indicators.appendChild(dot);
                    }
 
                    celula.appendChild(indicators);
                }
 
                celula.addEventListener('click', () => abrirModalDia(dataStr, dataObj));
                diaAtual++;
            } else {
                celula.classList.add('empty');
            }
 
            grid.appendChild(celula);
        }
    }
}
 
function criarCelulaVazia() {
    const celula = document.createElement('div');
    celula.className = 'day-header';
    celula.textContent = 'Sem';
    return celula;
}
 
function navegarMes(direcao) {
    estado.mesAtual += direcao;
 
    if (estado.mesAtual > 11) {
        estado.mesAtual = 0;
        estado.anoAtual++;
    } else if (estado.mesAtual < 0) {
        estado.mesAtual = 11;
        estado.anoAtual--;
    }
 
    renderizarCalendario();
    atualizarDisplayMes();
}
 
function atualizarDisplayMes() {
    const display = document.getElementById('currentYearMonth');
    if (display) {
        const meses = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
            'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];
        display.textContent = `${meses[estado.mesAtual]} ${estado.anoAtual}`;
    }
}
 
function formatarData(ano, mes, dia) {
    const m = String(mes + 1).padStart(2, '0');
    const d = String(dia).padStart(2, '0');
    return `${ano}-${m}-${d}`;
}
 
function isDataHoje(data) {
    const hoje = new Date();
    return data.getDate() === hoje.getDate() &&
        data.getMonth() === hoje.getMonth() &&
        data.getFullYear() === hoje.getFullYear();
}
 
function rolarSidebar(pixels) {
    const lista = document.getElementById('activitiesList');
    if (lista) lista.scrollBy({ top: pixels, behavior: 'smooth' });
}
 
// ========== MODAL ==========
 
function abrirModalDia(dataStr, dataObj) {
    estado.diaSelecionado = dataStr;
 
    const modalTitle = document.getElementById('modalDate');
    if (modalTitle) {
        const dias = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];
        const meses = ['jan', 'fev', 'mar', 'abr', 'mai', 'jun', 'jul', 'ago', 'set', 'out', 'nov', 'dez'];
        modalTitle.textContent = `${dias[dataObj.getDay()]}, ${dataObj.getDate()} de ${meses[dataObj.getMonth()]} de ${dataObj.getFullYear()}`;
    }
 
    renderizarAvisosDoDia(dataStr);
    renderizarAnotacoesDoDia(dataStr);
 
    const input = document.getElementById('newEventInput');
    if (input) input.value = '';
 
    const modal = document.getElementById('dayModal');
    if (modal) {
        modal.hidden = false;
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }
}
 
/**
 * Renderiza avisos do professor no modal
 */
function renderizarAvisosDoDia(dataStr) {
    const container = document.getElementById('eventsList');
    if (!container) return;
 
    const avisos = estado.avisos[dataStr] || [];
 
    if (avisos.length === 0) {
        container.innerHTML = '<p class="no-events">Nenhum aviso para este dia.</p>';
        return;
    }
 
    container.innerHTML = avisos.map(aviso => `
        <div class="event-item" role="listitem">
            <div>
                <strong>📢 ${aviso.titulo}</strong>
                <br><small>${aviso.conteudo || ''}</small>
            </div>
        </div>
    `).join('');
}
 
/**
 * Renderiza anotações pessoais do aluno no modal
 */
function renderizarAnotacoesDoDia(dataStr) {
    const container = document.getElementById('activitiesDayList');
    if (!container) return;
 
    const anotacoes = estado.anotacoes[dataStr] || [];
 
    if (anotacoes.length === 0) {
        container.innerHTML = '<p class="no-events">Nenhuma anotação para este dia.</p>';
        return;
    }
 
    container.innerHTML = anotacoes.map((anotacao, index) => `
        <div class="event-item" role="listitem">
            <div><strong>${anotacao.titulo}</strong></div>
            <button class="delete-event" onclick="removerAnotacao('${dataStr}', ${index})"
                    aria-label="Remover anotação" title="Remover">🗑️</button>
        </div>
    `).join('');
}
 
function fecharModal() {
    const modal = document.getElementById('dayModal');
    if (modal) {
        modal.hidden = true;
        modal.style.display = 'none';
        document.body.style.overflow = '';
    }
    estado.diaSelecionado = null;
}
 
// ========== ANOTAÇÕES ==========
 
/**
 * Adiciona anotação pessoal do aluno
 */
async function adicionarAnotacao() {
    const input = document.getElementById('newEventInput');
    const titulo = input?.value.trim();
 
    if (!titulo || !estado.diaSelecionado) {
        exibirToast('⚠️ Digite uma descrição para a anotação', 'warning');
        return;
    }
 
    try {
        const response = await fetch('/agendaescolar/aluno/anotacao', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `titulo=${encodeURIComponent(titulo)}&dataEvento=${estado.diaSelecionado}`
        });
 
        if (response.ok) {
            // Salva localmente também para exibição imediata
            if (!estado.anotacoes[estado.diaSelecionado]) {
                estado.anotacoes[estado.diaSelecionado] = [];
            }
            estado.anotacoes[estado.diaSelecionado].push({ titulo });
            salvarDados();
            renderizarAnotacoesDoDia(estado.diaSelecionado);
            renderizarCalendario();
            if (input) input.value = '';
            exibirToast('✅ Anotação salva!', 'success');
        } else {
            exibirToast('❌ Erro ao salvar anotação.', 'error');
        }
    } catch (error) {
        exibirToast('❌ Erro ao salvar anotação.', 'error');
    }
}
 
/**
 * Remove anotação do localStorage
 */
function removerAnotacao(dataStr, index) {
    if (!estado.anotacoes[dataStr]) return;
    estado.anotacoes[dataStr].splice(index, 1);
    if (estado.anotacoes[dataStr].length === 0) delete estado.anotacoes[dataStr];
    salvarDados();
    renderizarAnotacoesDoDia(dataStr);
    renderizarCalendario();
    exibirToast('🗑️ Anotação removida', 'info');
}
 
// ========== UTILITÁRIOS ==========
 
function exibirToast(mensagem, tipo = 'info') {
    const toast = document.getElementById('toast');
    if (!toast) return;
 
    toast.textContent = mensagem;
    toast.className = `toast ${tipo} show`;
 
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3500);
}
 
// ========== FUNÇÕES GLOBAIS ==========
window.fecharModal = fecharModal;
window.adicionarAnotacao = adicionarAnotacao;
window.removerAnotacao = removerAnotacao;
 
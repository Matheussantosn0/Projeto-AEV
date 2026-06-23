/**
 * 👨‍👩‍👧‍👦 Agenda do Responsável - Agenda Escolar Virtual
 * Módulo: agenda-responsavel.js
 * Perfil: Responsável
 * 
 * Funcionalidades (VISUALIZAÇÃO APENAS):
 * - Calendário dinâmico com navegação mensal
 * - Lista de desempenho/atividades dos filhos
 * - Modal de detalhes do dia (somente leitura)
 * - Busca em tempo real
 * - Sem edição de eventos (apenas visualização)
 */

'use strict';

// ========== CONFIGURAÇÃO INICIAL ==========
const CONFIG = {
    tiposItem: {
        evento: { cor: '#841633', icone: '📌', label: 'Evento' },
        atividade: { cor: '#17a2b8', icone: '📚', label: 'Atividade' },
        prova: { cor: '#dc3545', icone: '📝', label: 'Prova' },
        entrega: { cor: '#fd7e14', icone: '⏰', label: 'Prazo' },
        aviso: { cor: '#6f42c1', icone: '📢', label: 'Aviso' }
    }
};

const dadosIniciais = {
    filhos: [],
    desempenho: [],
    eventos: {}
};
// ========== ESTADO DA APLICAÇÃO ==========
const estado = {
    dataAtual: new Date(),
    mesAtual: new Date().getMonth(),
    anoAtual: new Date().getFullYear(),
    diaSelecionado: null,
    filhos: [],
    desempenho: [],
    eventos: {},
    termoBusca: ''
};

// ========== DADOS SIMULADOS (Substituir por API) ==========


// ========== INICIALIZAÇÃO ==========
document.addEventListener('DOMContentLoaded', () => {
    configurarEventos();
    carregarDados();
});

/**
 * Inicializa a aplicação
 */
function inicializarAplicacao() {

    // Carregar dados
    carregarDados();

    // Renderizar interface
    renderizarCalendario();
    renderizarDesempenho();
    atualizarDisplayMes();
}

/**
 * Configura todos os event listeners
 */
function configurarEventos() {
    // Navegação do calendário
    document.getElementById('prevMonth')?.addEventListener('click', () => navegarMes(-1));
    document.getElementById('nextMonth')?.addEventListener('click', () => navegarMes(1));

    // Scroll da sidebar
    document.getElementById('scrollUp')?.addEventListener('click', () => rolarSidebar(-100));
    document.getElementById('scrollDown')?.addEventListener('click', () => rolarSidebar(100));

    // Busca
    document.getElementById('searchInput')?.addEventListener('input', (e) => {
        estado.termoBusca = e.target.value.toLowerCase();
        renderizarDesempenho();
        renderizarCalendario();
    });

    // Modal
    document.querySelector('.close')?.addEventListener('click', fecharModal);
    document.getElementById('dayModal')?.addEventListener('click', (e) => {
        if (e.target === document.getElementById('dayModal')) fecharModal();
    });

    // Fechar modal com ESC
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') fecharModal();
    });
}

/**
 * Carrega dados do localStorage ou usa dados iniciais
 */
async function carregarDados() {
    // turma do filho — você precisa ter isso disponível via Thymeleaf
    // Ex: const turmaFilhoId = /*[[${turmaFilho}]]*/ null;
    // Por ora usa localStorage como fallback
    const filhosSalvos = localStorage.getItem('aev_resp_filhos');
    estado.filhos = filhosSalvos ? JSON.parse(filhosSalvos) : [...dadosIniciais.filhos];

    const desempenhoSalvo = localStorage.getItem('aev_resp_desempenho');
    estado.desempenho = desempenhoSalvo ? JSON.parse(desempenhoSalvo) : [...dadosIniciais.desempenho];

    // Busca avisos reais do backend se souber o código da turma
    if (typeof turmaFilhoCodigo !== 'undefined' && turmaFilhoCodigo) {
        try {
            const res = await fetch(`/agendaescolar/responsavel/avisos-filho?turmaId=${turmaFilhoCodigo}`);
            if (res.ok) {
                const avisos = await res.json();
                avisos.forEach(aviso => {
                    const dataStr = aviso.dataEvento;
                    if (!estado.eventos[dataStr]) estado.eventos[dataStr] = [];
                    estado.eventos[dataStr].push({
                        id: aviso.id,
                        tipo: 'aviso',
                        titulo: `📢 ${aviso.titulo}`,
                        hora: '08:00',
                        filho: null,
                        turma: null,
                        conteudo: aviso.conteudo
                    });
                });
                renderizarCalendario();
            }
        } catch (e) {
            console.warn('Não foi possível carregar avisos do backend:', e);
        }
    } else {
        const eventosSalvos = localStorage.getItem('aev_resp_eventos');
        estado.eventos = eventosSalvos ? JSON.parse(eventosSalvos) : { ...dadosIniciais.eventos };
    }
}

// ========== CALENDÁRIO (Visualização) ==========

/**
 * Renderiza o calendário do mês atual
 */
function renderizarCalendario() {
    const grid = document.getElementById('calendarGrid');
    if (!grid) return;

    grid.innerHTML = '';

    // Cabeçalho: Dias da semana
    const diasSemana = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
    grid.appendChild(criarCelulaVazia());

    diasSemana.forEach(dia => {
        const header = document.createElement('div');
        header.className = 'day-header';
        header.setAttribute('role', 'columnheader');
        header.textContent = dia;
        grid.appendChild(header);
    });

    // Calcular dias do mês
    const primeiroDia = new Date(estado.anoAtual, estado.mesAtual, 1);
    const ultimoDia = new Date(estado.anoAtual, estado.mesAtual + 1, 0);
    const totalDias = ultimoDia.getDate();
    const diaSemanaInicio = primeiroDia.getDay();

    // Renderizar semanas
    let diaAtual = 1;
    let semanaAtual = 1;

    for (let semana = 0; semana < 6; semana++) {
        // Número da semana
        const weekNum = document.createElement('div');
        weekNum.className = 'week-number';
        weekNum.textContent = semanaAtual++;
        grid.appendChild(weekNum);

        // Dias da semana
        for (let dia = 0; dia < 7; dia++) {
            const celula = document.createElement('div');
            celula.className = 'calendar-cell';
            celula.setAttribute('role', 'gridcell');

            if (semana === 0 && dia < diaSemanaInicio) {
                celula.classList.add('empty');
            }
            else if (diaAtual <= totalDias) {
                const dataStr = formatarData(estado.anoAtual, estado.mesAtual, diaAtual);
                const dataObj = new Date(estado.anoAtual, estado.mesAtual, diaAtual);
                const ehHoje = isDataHoje(dataObj);
                const ehFimDeSemana = dia === 0 || dia === 6;

                celula.dataset.data = dataStr;

                // Número do dia
                const dayNum = document.createElement('span');
                dayNum.className = 'day-number';
                dayNum.textContent = diaAtual;
                celula.appendChild(dayNum);

                // Classes especiais
                if (ehHoje) celula.classList.add('today');
                if (ehFimDeSemana) celula.classList.add('weekend');

                // Verificar eventos/atividades (apenas visualização)
                const eventosDoDia = estado.eventos[dataStr] || [];
                const desempenhoDoDia = filtrarDesempenhoPorData(dataStr);
                const itensFiltrados = filtrarItensPorBusca([...eventosDoDia, ...desempenhoDoDia]);

                if (itensFiltrados.length > 0) {
                    celula.classList.add('has-event');

                    // Indicadores visuais por tipo
                    const indicators = document.createElement('div');
                    indicators.className = 'day-events';

                    const tiposVistos = new Set();
                    itensFiltrados.forEach(item => {
                        const tipo = item.tipo || 'evento';
                        if (!tiposVistos.has(tipo)) {
                            const dot = document.createElement('span');
                            dot.className = `day-event-dot event-type-${tipo}`;
                            indicators.appendChild(dot);
                            tiposVistos.add(tipo);
                        }
                    });

                    celula.appendChild(indicators);
                }

                // Clique para ver detalhes (apenas visualização)
                celula.addEventListener('click', () => abrirModalDia(dataStr, dataObj));
            }
            else {
                celula.classList.add('empty');
            }

            grid.appendChild(celula);
            if (diaAtual <= totalDias) diaAtual++;
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

function filtrarItensPorBusca(itens) {
    if (!estado.termoBusca) return itens;

    return itens.filter(item =>
        item.titulo?.toLowerCase().includes(estado.termoBusca) ||
        item.filho?.toLowerCase().includes(estado.termoBusca) ||
        item.turma?.toLowerCase().includes(estado.termoBusca)
    );
}

function filtrarDesempenhoPorData(dataStr) {
    return estado.desempenho.filter(d => d.data === dataStr);
}

// ========== DESEMPENHO DOS FILHOS (Sidebar - Visualização) ==========

/**
 * Renderiza a lista de desempenho na sidebar
 */
function renderizarDesempenho() {
    const lista = document.getElementById('activitiesList');
    if (!lista) return;

    lista.innerHTML = '';

    // Filtrar desempenho
    let desempenhoFiltrado = estado.desempenho;

    if (estado.termoBusca) {
        desempenhoFiltrado = desempenhoFiltrado.filter(d =>
            d.titulo.toLowerCase().includes(estado.termoBusca) ||
            d.filhoNome.toLowerCase().includes(estado.termoBusca) ||
            d.turma.toLowerCase().includes(estado.termoBusca)
        );
    }

    // Ordenar por data (mais recente primeiro)
    desempenhoFiltrado.sort((a, b) => new Date(b.data) - new Date(a.data));

    if (desempenhoFiltrado.length === 0) {
        lista.innerHTML = '<p class="no-events" style="padding: 1rem; text-align: center;">Nenhuma atividade encontrada.</p>';
        return;
    }

    desempenhoFiltrado.forEach(item => {
        const el = document.createElement('div');
        el.className = `activity-item status-${item.status}`;
        el.setAttribute('role', 'listitem');

        // Ícone e classe baseado no status
        const icones = { ok: '✅', alerta: '⚠️', atrasado: '❌' };
        const classes = { ok: 'ok', alerta: 'alerta', atrasado: 'atrasado' };

        // Formatar data
        const dataObj = new Date(item.data);
        const dataFormatada = dataObj.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });

        // Nota ou status
        const notaDisplay = item.nota !== null ? `Nota: ${item.nota.toFixed(1)}` : 'Pendente';

        el.innerHTML = `
            <div class="activity-icon ${classes[item.status]}" aria-hidden="true">${icones[item.status]}</div>
            <div class="activity-info">
                <span class="activity-name">${item.filhoNome}</span>
                <span class="activity-detail">${item.titulo} • ${item.turma}</span>
                <span class="activity-detail">📅 ${dataFormatada} • ${notaDisplay}</span>
            </div>
            <span class="activity-status">${item.status === 'ok' ? '✓ Entregue' : item.status === 'alerta' ? '⚠️ Atenção' : '❌ Atrasado'}</span>
        `;

        // Clique apenas para destacar (sem ação)
        el.addEventListener('click', () => {
            // Remover seleção anterior
            document.querySelectorAll('.activity-item.selected').forEach(el => el.classList.remove('selected'));
            // Adicionar seleção atual
            el.classList.add('selected');
            exibirToast(`📋 Visualizando: ${item.titulo}`, 'info');
        });

        lista.appendChild(el);
    });
}

function rolarSidebar(pixels) {
    const lista = document.getElementById('activitiesList');
    if (lista) {
        lista.scrollBy({ top: pixels, behavior: 'smooth' });
    }
}

// ========== MODAL (Visualização Apenas) ==========

function abrirModalDia(dataStr, dataObj) {
    estado.diaSelecionado = dataStr;

    // Atualizar título
    const modalTitle = document.getElementById('modalDate');
    if (modalTitle) {
        const dias = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];
        const meses = ['jan', 'fev', 'mar', 'abr', 'mai', 'jun', 'jul', 'ago', 'set', 'out', 'nov', 'dez'];
        modalTitle.textContent = `${dias[dataObj.getDay()]}, ${dataObj.getDate()} de ${meses[dataObj.getMonth()]} de ${dataObj.getFullYear()}`;
    }

    // Renderizar conteúdo (apenas visualização)
    renderizarEventosDoDia(dataStr);
    renderizarAtividadesDoDia(dataStr);

    // Mostrar modal
    const modal = document.getElementById('dayModal');
    if (modal) {
        modal.hidden = false;
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }
}

function renderizarEventosDoDia(dataStr) {
    const container = document.getElementById('eventsList');
    if (!container) return;

    const eventos = estado.eventos[dataStr] || [];
    const itensFiltrados = filtrarItensPorBusca(eventos);

    if (itensFiltrados.length === 0) {
        container.innerHTML = '<p class="no-events">Nenhum evento escolar para este dia.</p>';
        return;
    }

    container.innerHTML = itensFiltrados.map(evento => {
        const tipo = CONFIG.tiposItem[evento.tipo] || CONFIG.tiposItem.evento;
        return `
            <div class="event-item tipo-${evento.tipo}" role="listitem">
                <div class="event-content">
                    <span class="event-time">${evento.hora}</span>
                    <span class="event-titulo">${tipo.icone} ${evento.titulo}</span>
                    ${evento.filho ? `<div class="event-filho">👤 ${evento.filho}</div>` : ''}
                    ${evento.turma ? `<span class="event-turma">${evento.turma}</span>` : ''}
                </div>
            </div>
        `;
    }).join('');
}

function renderizarAtividadesDoDia(dataStr) {
    const container = document.getElementById('atividadesList');
    if (!container) return;

    const atividades = filtrarDesempenhoPorData(dataStr);
    const itensFiltrados = filtrarItensPorBusca(atividades);

    if (itensFiltrados.length === 0) {
        container.innerHTML = '<p class="no-events">Nenhuma atividade dos filhos para este dia.</p>';
        return;
    }

    container.innerHTML = itensFiltrados.map(atv => {
        const tipo = CONFIG.tiposItem[atv.tipo] || CONFIG.tiposItem.atividade;
        const notaDisplay = atv.nota !== null ? `• Nota: ${atv.nota.toFixed(1)}` : '';
        const statusIcon = atv.status === 'ok' ? '✓' : atv.status === 'alerta' ? '⚠️' : '❌';

        return `
            <div class="event-item tipo-atividade" role="listitem">
                <div class="event-content">
                    <span class="event-time">${atv.data.split('-')[2].padStart(2, '0')}:00</span>
                    <span class="event-titulo">${tipo.icone} ${atv.titulo}</span>
                    <div class="event-filho">👤 ${atv.filhoNome} • ${atv.turma} ${notaDisplay}</div>
                    <span class="event-turma">${statusIcon} ${atv.status === 'ok' ? 'Entregue' : atv.status === 'alerta' ? 'Atenção' : 'Atrasado'}</span>
                </div>
            </div>
        `;
    }).join('');
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

// ========== FUNÇÕES GLOBAIS (para onclick no HTML) ==========
window.fecharModal = fecharModal;
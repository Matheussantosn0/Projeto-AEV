/**
 * 👨‍🏫 Agenda do Professor - Agenda Escolar Virtual
 * Módulo: calendario-professor.js
 * Perfil: Professor
 */

'use strict';

// ========== CONFIGURAÇÃO INICIAL ==========
const CONFIG = {
    tiposEvento: {
        evento: { cor: '#16844c', icone: '📌', label: 'Evento' },
        aula: { cor: '#17a2b8', icone: '🎓', label: 'Aula' },
        prova: { cor: '#dc3545', icone: '📝', label: 'Prova' },
        entrega: { cor: '#fd7e14', icone: '⏰', label: 'Prazo' },
        aviso: { cor: '#6f42c1', icone: '📢', label: 'Aviso' }
    }
};

// ========== ESTADO DA APLICAÇÃO ==========
const estado = {
    dataAtual: new Date(),
    mesAtual: new Date().getMonth(),
    anoAtual: new Date().getFullYear(),
    diaSelecionado: null,
    turmas: [],
    eventos: {},
    aulas: {},
    termoBusca: '',
    turmaFiltro: 'all',
    turmaSelecionada: null  // ← turma clicada na sidebar
};

// ========== DADOS INICIAIS ==========
const dadosIniciais = {
    eventos: {},
    aulas: {}
};

// ========== INICIALIZAÇÃO ==========
document.addEventListener('DOMContentLoaded', () => {
    carregarDados();
    inicializarAplicacao();
    configurarEventos();
});

function inicializarAplicacao() {
    const nomeEl = document.getElementById('professorName');
    if (nomeEl) nomeEl.textContent = professorNome;

    renderizarCalendario();
    renderizarTurmas();
    atualizarDisplayMes();
    preencherSelectTurmas();
}

function configurarEventos() {
    document.getElementById('prevMonth')?.addEventListener('click', () => navegarMes(-1));
    document.getElementById('nextMonth')?.addEventListener('click', () => navegarMes(1));

    document.getElementById('scrollUp')?.addEventListener('click', () => rolarSidebar(-100));
    document.getElementById('scrollDown')?.addEventListener('click', () => rolarSidebar(100));

    document.getElementById('filtroTurma')?.addEventListener('change', (e) => {
        estado.turmaFiltro = e.target.value;
        renderizarTurmas();
    });

    document.getElementById('searchInput')?.addEventListener('input', (e) => {
        estado.termoBusca = e.target.value.toLowerCase();
        renderizarTurmas();
        renderizarCalendario();
    });

    document.querySelector('.close')?.addEventListener('click', fecharModal);
    document.getElementById('dayModal')?.addEventListener('click', (e) => {
        if (e.target === document.getElementById('dayModal')) fecharModal();
    });

    document.getElementById('btnAddEvent')?.addEventListener('click', adicionarItemAgenda);

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') fecharModal();
    });

    // Mostrar/esconder campo de conteúdo do aviso
    document.addEventListener('change', (e) => {
        if (e.target.name === 'eventType') {
            const avisoSection = document.getElementById('aviso-section');
            if (avisoSection) {
                avisoSection.style.display = e.target.value === 'aviso' ? 'block' : 'none';
            }
        }
    });
}

function carregarDados() {
    if (typeof turmasBackend !== 'undefined' && turmasBackend.length > 0) {
        estado.turmas = turmasBackend.map(t => ({
            id: t.codigoAcesso,
            nome: t.nome,
            ano: t.anoLetivo
        }));
    }

    const eventosSalvos = localStorage.getItem('aev_prof_eventos');
    estado.eventos = eventosSalvos ? JSON.parse(eventosSalvos) : { ...dadosIniciais.eventos };

    const aulasSalvas = localStorage.getItem('aev_prof_aulas');
    estado.aulas = aulasSalvas ? JSON.parse(aulasSalvas) : { ...dadosIniciais.aulas };
}

function salvarDados() {
    localStorage.setItem('aev_prof_eventos', JSON.stringify(estado.eventos));
    localStorage.setItem('aev_prof_aulas', JSON.stringify(estado.aulas));
}

// ========== CALENDÁRIO ==========

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

                const eventosDoDia = estado.eventos[dataStr] || [];
                const aulasDoDia = estado.aulas[dataStr] || [];
                const itensFiltrados = filtrarItensPorBusca([...eventosDoDia, ...aulasDoDia]);

                if (itensFiltrados.length > 0) {
                    celula.classList.add('has-event');

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

                celula.addEventListener('click', () => abrirModalDia(dataStr, dataObj));
            } else {
                celula.classList.add('empty');
            }

            grid.appendChild(celula);
            if (semana === 0 && dia < diaSemanaInicio) {
                // não incrementa
            } else if (diaAtual <= totalDias) {
                diaAtual++;
            }
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
        item.titulo.toLowerCase().includes(estado.termoBusca) ||
        (item.turma && item.turma.toLowerCase().includes(estado.termoBusca))
    );
}

// ========== SIDEBAR: TURMAS ==========

function renderizarTurmas() {
    estado.turmaSelecionada = null; // ← limpa turma selecionada ao voltar

    const lista = document.getElementById('studentsList');
    if (!lista) return;

    lista.innerHTML = '';

    if (estado.turmas.length === 0) {
        lista.innerHTML = '<p style="padding:1rem;text-align:center;">Nenhuma turma encontrada.</p>';
        return;
    }

    estado.turmas.forEach(turma => {
        const item = document.createElement('div');
        item.className = 'student-item';
        item.setAttribute('role', 'listitem');
        item.style.cursor = 'pointer';
        item.innerHTML = `
            <div class="student-avatar" aria-hidden="true">📚</div>
            <div class="student-info">
                <span class="student-name">${turma.nome}</span>
                <span class="student-turma">${turma.ano}</span>
            </div>
        `;
        item.addEventListener('click', () => buscarAlunosDaTurma(turma.id, turma.nome));
        lista.appendChild(item);
    });
}

async function buscarAlunosDaTurma(codigo, nomeTurma) {
    const lista = document.getElementById('studentsList');
    lista.innerHTML = '<p style="padding:1rem;text-align:center;">Carregando...</p>';

    try {
        const [resAlunos, resAvisos] = await Promise.all([
            fetch(`/agendaescolar/professor/alunos-turma?codigo=${codigo}`),
            fetch(`/agendaescolar/professor/avisos-turma?turmaId=${codigo}`)
        ]);

        const alunos = await resAlunos.json();
        const avisos = resAvisos.ok ? await resAvisos.json() : [];

        estado.turmaSelecionada = { id: codigo, nome: nomeTurma };

        // Injeta avisos do banco no calendário
        avisos.forEach(aviso => {
            const dataStr = aviso.dataEvento; // formato YYYY-MM-DD
            if (!estado.eventos[dataStr]) estado.eventos[dataStr] = [];
            // evita duplicata
            if (!estado.eventos[dataStr].find(e => e.id === aviso.id)) {
                estado.eventos[dataStr].push({
                    id: aviso.id,
                    tipo: 'aviso',
                    titulo: aviso.titulo,
                    hora: '08:00',
                    conteudo: aviso.conteudo,
                    turma: nomeTurma,
                    _fromBackend: true
                });
            }
        });

        renderizarCalendario();
        renderizarAlunos(alunos, nomeTurma);

    } catch (error) {
        lista.innerHTML = '<p style="padding:1rem;text-align:center;">Erro ao carregar dados.</p>';
    }
}

function renderizarAlunos(alunos, nomeTurma) {
    const lista = document.getElementById('studentsList');
    lista.innerHTML = '';

    const voltar = document.createElement('div');
    voltar.className = 'student-item';
    voltar.style.cursor = 'pointer';
    voltar.innerHTML = `
        <div class="student-avatar">◀</div>
        <div class="student-info">
            <span class="student-name">Voltar</span>
            <span class="student-turma">${nomeTurma}</span>
        </div>
    `;
    voltar.addEventListener('click', renderizarTurmas);
    lista.appendChild(voltar);

    if (alunos.length === 0) {
        lista.innerHTML += '<p style="padding:1rem;text-align:center;">Nenhum aluno matriculado.</p>';
        return;
    }

    alunos.forEach(aluno => {
        const item = document.createElement('div');
        item.className = 'student-item';
        item.setAttribute('role', 'listitem');
        item.innerHTML = `
            <div class="student-avatar" aria-hidden="true">👤</div>
            <div class="student-info">
                <span class="student-name">${aluno.nomeCompleto}</span>
                <span class="student-turma">${aluno.email}</span>
            </div>
        `;
        lista.appendChild(item);
    });
}

function preencherSelectTurmas() {
    const select = document.getElementById('eventTurma');
    if (!select) return;

    const primeira = select.options[0];
    select.innerHTML = '';
    select.appendChild(primeira);

    estado.turmas.forEach(turma => {
        const option = document.createElement('option');
        option.value = turma.id;
        option.textContent = turma.nome;
        select.appendChild(option);
    });
}

function rolarSidebar(pixels) {
    const lista = document.getElementById('studentsList');
    if (lista) {
        lista.scrollBy({ top: pixels, behavior: 'smooth' });
    }
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

    renderizarEventosDoDia(dataStr);
    renderizarAulasDoDia(dataStr);
    resetarFormularioEvento();
    atualizarFormularioModal(); // ← atualiza opção de aviso conforme turma selecionada

    const modal = document.getElementById('dayModal');
    if (modal) {
        modal.hidden = false;
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }
}

/**
 * Mostra/esconde opção de Aviso e label de turma conforme turma selecionada
 */
function atualizarFormularioModal() {
    const temTurma = estado.turmaSelecionada !== null;

    const turmaSelecionadaLabel = document.getElementById('turma-selecionada-label');
    if (turmaSelecionadaLabel) {
        turmaSelecionadaLabel.textContent = temTurma
            ? `Turma selecionada: ${estado.turmaSelecionada.nome}`
            : 'Nenhuma turma selecionada — avisos não disponíveis';
    }

    const avisoRadioLabel = document.getElementById('aviso-radio-label');
    if (avisoRadioLabel) {
        avisoRadioLabel.style.display = temTurma ? 'inline-flex' : 'none';
    }

    const avisoSection = document.getElementById('aviso-section');
    if (avisoSection) {
        avisoSection.style.display = 'none'; // sempre oculto ao abrir
    }
}

function renderizarEventosDoDia(dataStr) {
    const container = document.getElementById('eventsList');
    if (!container) return;

    const eventos = estado.eventos[dataStr] || [];
    const itensFiltrados = filtrarItensPorBusca(eventos);

    if (itensFiltrados.length === 0) {
        container.innerHTML = '<p class="no-events">Nenhum evento para este dia.</p>';
        return;
    }

    container.innerHTML = itensFiltrados.map(evento => {
        const tipo = CONFIG.tiposEvento[evento.tipo] || CONFIG.tiposEvento.evento;
        return `
            <div class="event-item tipo-${evento.tipo}" role="listitem">
                <div>
                    <span class="event-time">${evento.hora}</span>
                    ${tipo.icone} ${evento.titulo}
                    ${evento.turma ? `<span class="event-turma">${evento.turma}</span>` : ''}
                </div>
                <button class="delete-event" onclick="removerEvento('${dataStr}', '${evento.id}', 'evento')"
                        aria-label="Remover evento" title="Remover">🗑️</button>
            </div>
        `;
    }).join('');
}

function renderizarAulasDoDia(dataStr) {
    const container = document.getElementById('aulasList');
    if (!container) return;

    const aulas = estado.aulas[dataStr] || [];
    const itensFiltrados = filtrarItensPorBusca(aulas);

    if (itensFiltrados.length === 0) {
        container.innerHTML = '<p class="no-events">Nenhuma aula para este dia.</p>';
        return;
    }

    container.innerHTML = itensFiltrados.map(aula => `
        <div class="event-item tipo-aula" role="listitem">
            <div>
                <span class="event-time">${aula.hora}</span>
                🎓 ${aula.titulo}
                <span class="event-turma">${aula.turma}</span>
                <br><small style="color: var(--color-muted)">${aula.conteudo || ''}</small>
            </div>
            <button class="delete-event" onclick="removerEvento('${dataStr}', '${aula.id}', 'aula')"
                    aria-label="Remover aula" title="Remover">🗑️</button>
        </div>
    `).join('');
}

function resetarFormularioEvento() {
    const input = document.getElementById('newEventInput');
    const time = document.getElementById('eventTime');
    const turma = document.getElementById('eventTurma');
    const avisoConteudo = document.getElementById('avisoConteudo');

    if (input) input.value = '';
    if (time) time.value = '08:00';
    if (turma) turma.value = '';
    if (avisoConteudo) avisoConteudo.value = '';

    document.querySelectorAll('input[name="eventType"]').forEach(radio => {
        if (radio.value === 'evento') radio.checked = true;
    });
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

// ========== ADICIONAR EVENTOS/AULAS/AVISOS ==========

async function adicionarItemAgenda() {
    const input = document.getElementById('newEventInput');
    const time = document.getElementById('eventTime');
    const turma = document.getElementById('eventTurma');
    const tipoRadio = document.querySelector('input[name="eventType"]:checked');

    const texto = input?.value.trim();
    const horario = time?.value || '08:00';
    const tipo = tipoRadio?.value || 'evento';

    if (!texto || !estado.diaSelecionado) {
        exibirToast('⚠️ Digite uma descrição para o item', 'warning');
        return;
    }

    // ── AVISO: envia para o backend ──────────────────────────────────────────
    if (tipo === 'aviso') {
        if (!estado.turmaSelecionada) {
            exibirToast('⚠️ Selecione uma turma na sidebar para enviar um aviso', 'warning');
            return;
        }

        const conteudo = document.getElementById('avisoConteudo')?.value.trim() || '';

        try {
            const params = new URLSearchParams({
                turmaId: estado.turmaSelecionada.id,
                titulo: texto,
                conteudo: conteudo,
                dataEvento: estado.diaSelecionado  // ← data do dia clicado (formato YYYY-MM-DD)
            });

            const response = await fetch('/agendaescolar/professor/aviso', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            });

            if (response.ok) {
                exibirToast('✅ Aviso enviado para a turma!', 'success');
                resetarFormularioEvento();
                atualizarFormularioModal();
            } else {
                exibirToast('❌ Erro ao enviar aviso.', 'error');
            }
        } catch (e) {
            exibirToast('❌ Erro ao enviar aviso.', 'error');
        }
        return;
    }

    // ── EVENTO ou AULA: salva no localStorage ────────────────────────────────
    const turmaSelecionada = turma?.value || null;

    const novoItem = {
        id: Date.now(),
        tipo: tipo,
        titulo: texto,
        hora: horario,
        turma: turmaSelecionada,
        dataCriacao: new Date().toISOString()
    };

    if (tipo === 'aula') {
        if (!estado.aulas[estado.diaSelecionado]) estado.aulas[estado.diaSelecionado] = [];
        estado.aulas[estado.diaSelecionado].push(novoItem);
    } else {
        if (!estado.eventos[estado.diaSelecionado]) estado.eventos[estado.diaSelecionado] = [];
        estado.eventos[estado.diaSelecionado].push(novoItem);
    }

    salvarDados();

    if (tipo === 'aula') {
        renderizarAulasDoDia(estado.diaSelecionado);
    } else {
        renderizarEventosDoDia(estado.diaSelecionado);
    }

    renderizarCalendario();
    resetarFormularioEvento();
    exibirToast(`✅ ${CONFIG.tiposEvento[tipo]?.label || 'Item'} adicionado!`, 'success');
}

async function removerEvento(dataStr, itemId, tipo) {
    const collection = tipo === 'aula' ? estado.aulas : estado.eventos;
    if (!collection[dataStr]) return;

    const item = collection[dataStr].find(i => i.id == itemId);

    // Se for aviso do backend, deleta no servidor
    if (item && item._fromBackend) {
        try {
            const res = await fetch(`/agendaescolar/professor/excluir-aviso/${itemId}`, { method: 'POST' });
            if (!res.ok) {
                exibirToast('❌ Erro ao excluir aviso.', 'error');
                return;
            }
        } catch (e) {
            exibirToast('❌ Erro ao excluir aviso.', 'error');
            return;
        }
    }

    collection[dataStr] = collection[dataStr].filter(i => i.id != itemId);
    if (collection[dataStr].length === 0) delete collection[dataStr];

    salvarDados();

    if (tipo === 'aula') renderizarAulasDoDia(dataStr);
    else renderizarEventosDoDia(dataStr);

    renderizarCalendario();
    exibirToast('🗑️ Item removido da agenda', 'info');
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
window.removerEvento = removerEvento;
window.adicionarItemAgenda = adicionarItemAgenda;

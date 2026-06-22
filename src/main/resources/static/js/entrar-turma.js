const form = document.getElementById('formBuscarTurma');
const turmaEncontrada = document.getElementById('turmaEncontrada');
const errorMsg = document.getElementById('errorMsg');
let codigoEncontrado = '';

// Busca a turma pelo código ao clicar em "Buscar Turma"
form.addEventListener('submit', async function (e) {
    e.preventDefault();

    const codigo = document.getElementById('codigoTurma').value.trim().toUpperCase();
    turmaEncontrada.classList.add('hidden');
    errorMsg.classList.add('hidden');

    try {
        const response = await fetch(`/agendaescolar/escola/buscar-turma?codigo=${codigo}`);

        if (!response.ok) {
            errorMsg.classList.remove('hidden');
            return;
        }

        const turma = await response.json();

        document.getElementById('turmaNome').textContent = turma.nome;
        document.getElementById('turmaCodigo').textContent = turma.codigoAcesso;

        codigoEncontrado = turma.codigoAcesso;
        turmaEncontrada.classList.remove('hidden');

    } catch (error) {
        errorMsg.classList.remove('hidden');
    }
});

// Entra na turma ao clicar em "Entrar na Turma"
async function entrarNaTurma() {
    try {
        const response = await fetch('/agendaescolar/aluno/entrar-turma', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `codigoAcesso=${codigoEncontrado}`
        });

        if (response.redirected) {
            window.location.href = response.url;
        } else if (!response.ok) {
            mostrarToast('Erro ao entrar na turma. Tente novamente.');
        }

    } catch (error) {
        mostrarToast('Erro ao entrar na turma. Tente novamente.');
    }
}

// Limpa a busca e volta ao estado inicial
function limparBusca() {
    turmaEncontrada.classList.add('hidden');
    errorMsg.classList.add('hidden');
    document.getElementById('codigoTurma').value = '';
    codigoEncontrado = '';
}

// Cola o código da área de transferência
async function colarCodigo(inputId) {
    try {
        const text = await navigator.clipboard.readText();
        document.getElementById(inputId).value = text;
    } catch (error) {
        mostrarToast('Não foi possível colar. Cole manualmente.');
    }
}

// Exibe um toast de notificação
function mostrarToast(mensagem) {
    const toast = document.getElementById('toast');
    toast.textContent = mensagem;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3000);
}
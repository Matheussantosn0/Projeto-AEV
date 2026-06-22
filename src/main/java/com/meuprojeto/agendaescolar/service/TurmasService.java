package com.meuprojeto.agendaescolar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.List;
import com.meuprojeto.agendaescolar.entity.MatriculasTurmas;
import com.meuprojeto.agendaescolar.entity.Turmas;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;
import java.util.stream.Collectors;
import com.meuprojeto.agendaescolar.repository.TurmasRepository;
import com.meuprojeto.agendaescolar.repository.MatriculasTurmasRepository;

@Service
public class TurmasService {

    @Autowired
    private MatriculasTurmasRepository matriculasTurmasRepository;

    @Autowired
    private TurmasRepository turmasRepository;

    // Cria uma nova turma chamando a procedure no banco
    @Transactional
    public void criarTurma(UUID professorId, String nome, Year anoLetivo) {
        turmasRepository.inserirTurma(professorId, nome, anoLetivo);
    }

    // Lista todas as turmas que o professor criou via professores_turmas
    public List<Turmas> listarTurmasPorProfessor(UUID professorId) {
        return turmasRepository.findByProfessorId(professorId);
    }

    // Exclui uma turma pelo código de acesso
    @Transactional
    public void excluirTurmaPorCodigo(String codigo) {
        turmasRepository.deleteByCodigoAcesso(codigo);
    }

    // Matricula um aluno em uma turma pelo código de acesso
    @Transactional
    public void entrarTurmaAluno(UUID alunoId, String codigoAcesso) {
        Turmas turma = turmasRepository.findByCodigoAcesso(codigoAcesso)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        MatriculasTurmas matricula = new MatriculasTurmas();
        matricula.setTurmasId(turma.getId());
        matricula.setAlunosId(alunoId);
        matricula.setDataMatricula(LocalDate.now());
        matricula.setStatus("ativo");
        matriculasTurmasRepository.save(matricula);
    }

    // Lista todas as turmas em que o aluno está matriculado
    public List<Turmas> listarTurmasPorAluno(UUID alunoId) {
        List<MatriculasTurmas> matriculas = matriculasTurmasRepository.findByAlunosId(alunoId);
        return matriculas.stream()
                .map(m -> turmasRepository.findById(m.getTurmasId()).orElse(null))
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    // Busca uma turma pelo código de acesso, retorna vazio se não encontrar
    public Optional<Turmas> buscarPorCodigo(String codigo) {
        return turmasRepository.findByCodigoAcesso(codigo);
    }
}
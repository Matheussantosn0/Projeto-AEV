package com.meuprojeto.agendaescolar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.meuprojeto.agendaescolar.entity.Turmas;

import java.time.Year;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import com.meuprojeto.agendaescolar.repository.TurmasRepository;

@Service // classe de serviço, onde ficam as regras de negócio
public class TurmasService {

    @Autowired // injeção de dependência, para usar o TurmasRepository sem precisar instanciar
    private TurmasRepository turmasRepository; // variável para acessar os métodos do Turmas

    @Transactional
    public void criarTurma(UUID professorId, String nome, Year anoLetivo) {
        turmasRepository.inserirTurma(professorId, nome, anoLetivo);
    }

    public List<Turmas> listarTurmasPorProfessor(UUID professorId) {
        return turmasRepository.findByProfessorId(professorId);
    }

    @Transactional
    public void excluirTurmaPorCodigo(String codigo){
        turmasRepository.deleteByCodigoAcesso(codigo);
    }
}

package com.meuprojeto.agendaescolar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.meuprojeto.agendaescolar.entity.Avisos;
import com.meuprojeto.agendaescolar.entity.Anotacoes;
import com.meuprojeto.agendaescolar.repository.AvisosRepository;
import com.meuprojeto.agendaescolar.repository.AnotacoesRepository;

@Service
public class AvisosService {

    @Autowired
    private AvisosRepository avisosRepository;

    @Autowired
    private AnotacoesRepository anotacoesRepository;

    // Professor cria aviso para a turma com data
    @Transactional
    public void criarAviso(UUID autorId, UUID turmasId, String titulo, String conteudo, LocalDate dataEvento) {
        Avisos aviso = new Avisos();
        aviso.setAutorId(autorId);
        aviso.setTurmasId(turmasId);
        aviso.setTitulo(titulo);
        aviso.setConteudo(conteudo);
        aviso.setDataEvento(dataEvento);
        aviso.setCreatedAt(LocalDateTime.now());
        aviso.setUpdatedAt(LocalDateTime.now());
        avisosRepository.save(aviso);
    }

    // Lista avisos de uma turma
    public List<Avisos> listarAvisosPorTurma(UUID turmasId) {
        return avisosRepository.findByTurmasId(turmasId);
    }

    // Aluno cria anotação pessoal
    @Transactional
    public void criarAnotacao(UUID criadorId, UUID turmasId, String titulo, LocalDate dataEvento) {
        Anotacoes anotacao = new Anotacoes();
        anotacao.setCriadorId(criadorId);
        anotacao.setTurmasId(turmasId);
        anotacao.setTitulo(titulo);
        anotacao.setDataEvento(dataEvento);
        anotacao.setCreatedAt(LocalDateTime.now());
        anotacao.setUpdatedAt(LocalDateTime.now());
        anotacoesRepository.save(anotacao);
    }

    // Lista anotações do aluno
    public List<Anotacoes> listarAnotacoesPorAluno(UUID criadorId) {
        return anotacoesRepository.findByCriadorId(criadorId);
    }

    @Transactional
    public void excluirAviso(UUID id) {
        avisosRepository.deleteById(id);
    }
}
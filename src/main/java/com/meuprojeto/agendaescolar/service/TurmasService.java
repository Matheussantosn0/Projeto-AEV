package com.meuprojeto.agendaescolar.service;

import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import com.meuprojeto.agendaescolar.repository.TurmasRepository;


@Service //classe de serviço, onde ficam as regras de negócio
public class TurmasService {

    @Autowired //injeção de dependência, para usar o TurmasRepository sem precisar instanciar
    private TurmasRepository turmasRepository; //variável para acessar os métodos do Turmas

    public void criarTurma(UUID professorId, String nome, Year anoLetivo) {
        turmasRepository.inserirTurma(professorId, nome, anoLetivo);
    }

}


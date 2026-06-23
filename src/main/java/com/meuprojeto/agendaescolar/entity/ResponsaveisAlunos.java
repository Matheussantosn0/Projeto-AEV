package com.meuprojeto.agendaescolar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "responsaveis_alunos")
public class ResponsaveisAlunos {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID id;

    @Column(name = "responsavel_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID responsavelId;

    @Column(name = "alunos_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    private UUID alunosId;

    public ResponsaveisAlunos() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(UUID responsavelId) {
        this.responsavelId = responsavelId;
    }

    public UUID getAlunosId() {
        return alunosId;
    }

    public void setAlunosId(UUID alunosId) {
        this.alunosId = alunosId;
    }
}

package com.meuprojeto.agendaescolar.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "usuarios")

public class Usuarios {
    
    @Id //define como chave primária
    @Column(columnDefinition = "BINARY(16)") //gera o auto incremento do id
    private UUID id;
    @Column(name = "nomecompleto", nullable = false) //define o nome da coluna e que não pode ser nulo
    private String nomeCompleto;
    @Column(name = "email", nullable = false , unique = true) //define o nome da coluna, que não pode ser nulo e que deve ser único
    private String email;
    @Enumerated(EnumType.STRING)//define que o tipo do enum será armazenado como string no banco de dados
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;
    @Column(name = "salt", nullable = false)
    private String salt;
    @Column(name = "senha", nullable = false)
    private String senha;
    @Column(name = "telefone", nullable = true)
    private String telefone;
    @Column(name = "email_verificado")
    private Boolean emailVerificado = false;


        public Usuarios() {} //construtor vazio para o JPA
            
            public Usuarios(String nomeCompleto, String email, TipoUsuario tipoUsuario, String salt, String senha, String telefone) { //construtor para criar um usuário
                this.nomeCompleto = nomeCompleto;
                this.email = email;
                this.tipoUsuario = tipoUsuario;
                this.salt = salt;
                this.senha = senha;
                this.telefone = telefone;
                this.emailVerificado = false;
            }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Boolean getEmailVerificado() {
    return emailVerificado;
    }

    public void setEmailVerificado(Boolean emailVerificado) {
    this.emailVerificado = emailVerificado;
    }
}

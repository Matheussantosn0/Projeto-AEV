package com.meuprojeto.agendaescolar.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
 
import java.util.Map;
 
@Service
public class PasswordResetService {
 
    @Autowired
    private JdbcTemplate jdbcTemplate;
 
    @Autowired
    private EmailService emailService;
 
    /**
     * Solicita recuperação de senha.
     * Chama a procedure, pega o código gerado e envia por email.
     * Retorna true se o email existe, false caso contrário.
     */
    public boolean solicitarRecuperacao(String email) {
        try {
            Map<String, Object> resultado = jdbcTemplate.queryForMap(
                "CALL gerar_codigo_recuperacao(?)", email
            );
 
            String codigoGerado = (String) resultado.get("codigo_gerado");
 
            if (codigoGerado == null) {
                // Procedure retornou USUARIO_NAO_ENCONTRADO
                return false;
            }
 
            emailService.enviarCodigoRecuperacao(email, codigoGerado);
            return true;
 
        } catch (Exception e) {
            // Não expõe se o email existe ou não (segurança)
            return false;
        }
    }
 
    /**
     * Valida o código digitado pelo usuário.
     */
    public boolean validarCodigo(String email, String codigo) {
        try {
            Map<String, Object> resultado = jdbcTemplate.queryForMap(
                "CALL validar_codigo_recuperacao(?, ?)", email, codigo
            );
            Object valido = resultado.get("valido");
            return Boolean.TRUE.equals(valido) || "1".equals(String.valueOf(valido));
        } catch (Exception e) {
            return false;
        }
    }
 
    /**
     * Atualiza a senha do usuário após validação do código.
     */
    public boolean atualizarSenha(String email, String codigo, String novaSenha) {
        try {
            Map<String, Object> resultado = jdbcTemplate.queryForMap(
                "CALL atualizar_senha(?, ?, ?)", email, codigo, novaSenha
            );
            String res = (String) resultado.get("resultado");
            return "SUCESSO".equals(res);
        } catch (Exception e) {
            return false;
        }
    }
}
package com.meuprojeto.agendaescolar.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
 
@Service
public class EmailService {
 
    @Autowired
    private JavaMailSender mailSender;
 
    @Value("${spring.mail.username}")
    private String remetente;
 
    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject("Recuperação de Senha - Agenda Escolar");
        mensagem.setText(
            "Olá!\n\n" +
            "Recebemos uma solicitação para redefinir sua senha.\n\n" +
            "Seu código de recuperação é:\n\n" +
            "    " + codigo + "\n\n" +
            "Este código é válido por 15 minutos.\n\n" +
            "Se você não solicitou a recuperação de senha, ignore este email.\n\n" +
            "Agenda Escolar"
        );
        mailSender.send(mensagem);
    }
}
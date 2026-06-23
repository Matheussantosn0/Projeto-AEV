package com.meuprojeto.agendaescolar.controller;

import com.meuprojeto.agendaescolar.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recuperar-senha")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // ── Tela 1: solicitar email ──────────────────────────────────────────────

    @GetMapping
    public String paginaSolicitar() {
        return "/recuperar-senha";
    }

    @PostMapping("/solicitar")
    public String solicitar(@RequestParam String email,
            RedirectAttributes redirectAttrs) {
        passwordResetService.solicitarRecuperacao(email);
        // Sempre mostra a mesma mensagem (não revela se email existe)
        redirectAttrs.addFlashAttribute("mensagem",
                "Se o email estiver cadastrado, você receberá o código em instantes.");
        redirectAttrs.addFlashAttribute("email", email);
        return "redirect:/recuperar-senha/enviar-codigo";
    }

    // ── Tela 2: inserir código ───────────────────────────────────────────────

    @GetMapping("/enviar-codigo")
    public String paginaVerificar(Model model) {
        return "enviar-codigo";
    }

    @PostMapping("/enviar-codigo")
    public String verificar(@RequestParam String email,
            @RequestParam String codigo,
            RedirectAttributes redirectAttrs) {
        boolean valido = passwordResetService.validarCodigo(email, codigo);
        if (!valido) {
            redirectAttrs.addFlashAttribute("erro", "Código inválido ou expirado.");
            redirectAttrs.addFlashAttribute("email", email);
            return "redirect:/recuperar-senha/enviar-codigo";
        }
        redirectAttrs.addFlashAttribute("email", email);
        redirectAttrs.addFlashAttribute("codigo", codigo);
        return "redirect:/recuperar-senha/criar-nova-senha";
    }

    // ── Tela 3: nova senha ───────────────────────────────────────────────────

    @GetMapping("/criar-nova-senha")
    public String paginaNovaSenha(Model model) {
        return "criar-nova-senha";
    }

    @PostMapping("/criar-nova-senha")
    public String novaSenha(@RequestParam String email,
            @RequestParam String codigo,
            @RequestParam String novaSenha,
            @RequestParam String confirmarSenha,
            RedirectAttributes redirectAttrs) {
        if (!novaSenha.equals(confirmarSenha)) {
            redirectAttrs.addFlashAttribute("erro", "As senhas não coincidem.");
            redirectAttrs.addFlashAttribute("email", email);
            redirectAttrs.addFlashAttribute("codigo", codigo);
            return "redirect:/recuperar-senha/criar-nova-senha";
        }

        if (novaSenha.length() < 6) {
            redirectAttrs.addFlashAttribute("erro", "A senha deve ter no mínimo 6 caracteres.");
            redirectAttrs.addFlashAttribute("email", email);
            redirectAttrs.addFlashAttribute("codigo", codigo);
            return "redirect:/recuperar-senha/criar-nova-senha";
        }

        boolean sucesso = passwordResetService.atualizarSenha(email, codigo, novaSenha);
        if (!sucesso) {
            redirectAttrs.addFlashAttribute("erro", "Código inválido ou expirado. Tente novamente.");
            return "redirect:/recuperar-senha";
        }

        redirectAttrs.addFlashAttribute("mensagem", "Senha alterada com sucesso! Faça login.");
        return "redirect:/login";
    }
}

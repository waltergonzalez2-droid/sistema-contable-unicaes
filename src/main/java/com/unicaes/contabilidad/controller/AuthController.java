package com.unicaes.contabilidad.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.unicaes.contabilidad.model.Usuario;
import com.unicaes.contabilidad.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/api/contabilidad")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) Boolean registroExitoso,
                       @RequestParam(required = false) String mensaje,
                       @RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       Model model) {
        if (Boolean.TRUE.equals(registroExitoso)) {
            model.addAttribute("success", mensaje != null ? mensaje : "Registro exitoso. Por favor inicia sesión.");
        }
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos. Por favor verifica tus credenciales.");
        }
        if (logout != null) {
            model.addAttribute("success", "Sesión cerrada exitosamente.");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registro(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "registro";
    }

    // Login form submission is handled by Spring Security's default filter
    // No need for manual login handling here

    @PostMapping("/registro")
    public String handleRegistro(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String nombre,
                               @RequestParam String email,
                               Model model) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(password);
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            
            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
            return "redirect:/login?registroExitoso=true&mensaje=Usuario " + usuarioRegistrado.getUsername() + " registrado exitosamente";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            model.addAttribute("username", username);
            return "registro";
        }
    }

    @GetMapping("/api/contabilidad/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/api/contabilidad/login";
    }
    
    @GetMapping("/test-password")
    @ResponseBody
    public String testPassword(@RequestParam String username, @RequestParam String password) {
        try {
            Usuario usuario = usuarioService.buscarPorUsername(username);
            boolean matches = passwordEncoder.matches(password, usuario.getPassword());
            
            logger.info("Testing password for user: {}", username);
            logger.info("Password from request: {}", password);
            logger.info("Password hash in DB: {}", usuario.getPassword());
            logger.info("Passwords match: {}", matches);
            
            return String.format("Username: %s<br>Password matches: %s<br>Hash in DB: %s", 
                                username, matches, usuario.getPassword());
        } catch (Exception e) {
            logger.error("Error testing password", e);
            return "Error: " + e.getMessage();
        }
    }
}

package com.unicaes.contabilidad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.unicaes.contabilidad.repository.UsuarioRepository;

@ControllerAdvice
public class GlobalControllerAdvice {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @ModelAttribute
    public void addUserAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            
            String username = authentication.getName();
            
            // Buscar el usuario en la base de datos para obtener su rol
            usuarioRepository.findByUsername(username).ifPresent(usuario -> {
                String rol = usuario.getRol() != null ? usuario.getRol() : "Cliente";
                
                // Formatear el nombre con el rol
                String nombreConRol = username + ": " + rol;
                
                model.addAttribute("nombreUsuario", username);
                model.addAttribute("rolUsuario", rol);
                model.addAttribute("nombreCompleto", nombreConRol);
            });
        }
    }
}

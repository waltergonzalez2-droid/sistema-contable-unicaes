package com.unicaes.contabilidad.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.unicaes.contabilidad.model.Usuario;
import com.unicaes.contabilidad.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Intentando cargar usuario: {}", username);
        
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.error("Usuario no encontrado: {}", username);
                return new UsernameNotFoundException("Usuario no encontrado: " + username);
            });

        logger.info("Usuario encontrado: {}, password hash: {}", usuario.getUsername(), 
                    usuario.getPassword().substring(0, Math.min(20, usuario.getPassword().length())) + "...");

        // Determinar el rol del usuario (por defecto CLIENTE si no tiene rol)
        String rol = usuario.getRol() != null ? usuario.getRol() : "CLIENTE";
        
        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .roles(rol)
            .build();
    }
}
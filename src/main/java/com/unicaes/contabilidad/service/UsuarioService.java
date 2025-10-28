package com.unicaes.contabilidad.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.unicaes.contabilidad.model.Usuario;
import com.unicaes.contabilidad.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Asignar rol CLIENTE por defecto (solo el primer usuario será ADMIN)
        if (usuarioRepository.count() == 0) {
            usuario.setRol("ADMIN"); // El primer usuario registrado es ADMIN
            logger.info("Primer usuario registrado, asignando rol ADMIN");
        } else {
            usuario.setRol("CLIENTE"); // Los demás usuarios son CLIENTES
        }
        
        String passwordOriginal = usuario.getPassword();
        String passwordEncriptada = passwordEncoder.encode(passwordOriginal);
        usuario.setPassword(passwordEncriptada);
        
        logger.info("Registrando usuario: {} con rol: {}", usuario.getUsername(), usuario.getRol());
        logger.info("Password encriptada: {}", passwordEncriptada.substring(0, Math.min(20, passwordEncriptada.length())) + "...");
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.info("Usuario guardado exitosamente con ID: {}", usuarioGuardado.getId());
        
        return usuarioGuardado;
    }

    public boolean autenticarUsuario(String username, String password) {
        return usuarioRepository.findByUsername(username)
            .map(usuario -> passwordEncoder.matches(password, usuario.getPassword()))
            .orElse(false);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
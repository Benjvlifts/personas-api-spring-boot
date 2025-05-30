package com.gateway.jwt.service;

import com.gateway.jwt.dto.*;
import com.gateway.jwt.model.Persona;
import com.gateway.jwt.model.Usuario;
import com.gateway.jwt.repository.UsuarioRepository;
import com.gateway.jwt.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUser(), request.getPass()));

        Usuario usuario = usuarioRepository.findByUser(request.getUser())
                .orElseThrow();

        String token = jwtUtil.generateToken(usuario.getUser());
        return new AuthResponse(token, usuario.getUser());
    }

    public String register(RegisterRequest request) {
        Usuario usuario = new Usuario();
        usuario.setUser(request.getUser());
        usuario.setPass(passwordEncoder.encode(request.getPass()));
        usuario.setEstado(request.getEstado());

        // Persona solo se referencia por id, para simplificar
        Persona persona = new Persona();
        persona.setId(request.getIdPersona());
        usuario.setPersona(persona);

        usuarioRepository.save(usuario);
        return "Usuario registrado con éxito";
    }
}

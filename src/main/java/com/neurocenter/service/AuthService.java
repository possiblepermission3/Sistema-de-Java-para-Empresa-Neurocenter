package com.neurocenter.service;

import com.neurocenter.exception.DatosInvalidosException;
import com.neurocenter.exception.PersistenciaException;
import com.neurocenter.model.Usuario;
import com.neurocenter.repository.UsuarioRepository;
import com.neurocenter.util.Seguridad;
import com.neurocenter.util.Validadores;

/**
 * Servicio de autenticacion de usuarios del sistema.
 *
 * @author Equipo de Desarrollo - Proyecto Final
 * @version 1.0
 */
public class AuthService {

    /** Repositorio de usuarios. */
    private final UsuarioRepository usuarioRepository;

    /** Crea el servicio. */
    public AuthService() {
        this(new UsuarioRepository());
    }

    /**
     * Crea el servicio con dependencias inyectadas.
     *
     * @param usuarioRepository repositorio de usuarios
     */
    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Autentica un usuario comparando el hash SHA-256 de la contraseña.
     *
     * @param username   nombre de usuario
     * @param contrasena contraseña en texto plano
     * @return usuario autenticado
     * @throws DatosInvalidosException si las credenciales son incorrectas
     * @throws PersistenciaException si falla la consulta
     */
    public Usuario login(String username, String contrasena)
            throws DatosInvalidosException, PersistenciaException {
        String usuario = Validadores.texto(username, "usuario", 2);
        String clave = Validadores.texto(contrasena, "contraseña", 4);
        Usuario encontrado = usuarioRepository.buscarPorUsername(usuario)
                .orElseThrow(() -> new DatosInvalidosException("Usuario o contraseña incorrectos."));
        if (!encontrado.isActivo() || !Seguridad.verificar(clave, encontrado.getPasswordHash())) {
            throw new DatosInvalidosException("Usuario o contraseña incorrectos.");
        }
        return encontrado;
    }
}

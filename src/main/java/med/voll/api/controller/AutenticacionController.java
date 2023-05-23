package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.usuario.DatosAutenticacionUsuario;
import med.voll.api.domain.usuario.Usuario;
import med.voll.api.infra.security.DatosJWTToken;
import med.voll.api.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//CARGAR USUARIO, GENERAR TOKEN, DEVOLVER OK CON EL RESPONSE ENTITY
@RestController
@RequestMapping ("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity autenticarUsuario(@RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario){

        Authentication authToken = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
                datosAutenticacionUsuario.clave());//recibe los datos y los busca en la bd
        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        //getPrincipal es el usuario ya autenticado en el sistema booleano
        var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());//generar token desde mi vollmed
        return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
    }
}

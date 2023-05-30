package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //obtener token del header
        //"Authorization": obtener token del header
        //.replace sirve para que no salga el prefijo Bearer y se reemplazo con vacio
        var authHeader = request.getHeader("Authorization");
//        if(token == null || token==""){
//            throw new RuntimeException("El token no es valido");
//        }

        if (authHeader !=null){
            var token = authHeader.replace("Bearer ", "");
            //System.out.println(token);
            //System.out.println(tokenService.getSubject(token));//este usuario tiene sesion? al ser staless no tiene guardado el usuario en memoria
            var nombreUsuario = tokenService.getSubject(token);//extract username

            //Token valido
            if (nombreUsuario != null){
                var usuario = usuarioRepository.findByLogin(nombreUsuario);
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null,
                        usuario.getAuthorities());//Forzamos un inicio de sesion
                SecurityContextHolder.getContext().setAuthentication(authentication);//con esta linea le digo a spring que mi usuario ya esta autenticado
            }
        }
        filterChain.doFilter(request, response);

    }
}

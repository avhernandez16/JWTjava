package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import med.voll.api.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    //consumida desde propreties
    @Value("${api.security.secret}")//le indico desde donde debe extraer el secret
    private String apiSecret;//variable de ambiente

    //METODO DINAMICO
    public String generarToken(Usuario usuario){
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);//variable de ambiente
            return JWT.create()
                    .withIssuer("voll med")//emitido por volmed
                    .withSubject(usuario.getLogin())//username
                    .withClaim("id", usuario.getId())//muestra el id
                    .withExpiresAt(generarFechaExpiracion())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException();
        }
    }

    private Instant generarFechaExpiracion(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-05:00"));//duracion del token 2 horas con zona horario sudamerica
    }

    //METODO HARDCODEADO

//    public String generarToken(){
//        try {
//            Algorithm algorithm = Algorithm.HMAC256("123123");//es mala practica dejar secret harcodeado
//            return JWT.create()
//                    .withIssuer("voll med")//emitido por volmed
//                    .withSubject("angie hernandez")//va dirigido
//                    .sign(algorithm);
//        } catch (JWTCreationException exception){
//            throw new RuntimeException();
//        }
//    }
}

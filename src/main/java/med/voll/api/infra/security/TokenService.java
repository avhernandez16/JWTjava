package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    //metodo para verificar si el token es valido
    public String getSubject(String token) {
        if (token == null){
            throw new RuntimeException();
        }
        DecodedJWT verifier = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret); //validando firma
            verifier = JWT.require(algorithm)
                    .withIssuer("voll med")
                    .build()
                    .verify(token);
            verifier.getSubject();
        } catch (JWTVerificationException exception) {
            System.out.println(exception.toString());
        }
       // assert verifier != null;//pruebas unitarias
        if (verifier.getSubject() == null){
            throw new RuntimeException("Verifier invalido");
        }
        return verifier.getSubject();//retorna el usuario logeado
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

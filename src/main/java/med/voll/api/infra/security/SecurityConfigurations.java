package med.voll.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//SOBRE ESCRIBIR LA CONFIGURACION DE SPRINGSECURITY POR DEFECTO
//@EnableMethodSecurity(securedEnabled = true)//se debe poner para poder unar perfiles de acceso y la anotacion @Secured("ROLE_ADMIN")
@Configuration
@EnableWebSecurity//sobreescribe el comportamiento de autenticacion
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf().disable().sessionManagement()//CSRF evita la suplantacion de identidad, como se usa token se deshabilita
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// use solo stateless que es el tipo de sesion
                .and().authorizeRequests()//autorizar los request que tengan
                .requestMatchers(HttpMethod.POST, "/login")//los siguientes mattching con un post login
                .permitAll()
                .anyRequest()//se vuelve true al decirle a spring que mi usuario ya esta autenticado
                .authenticated()//se vuelve true al decirle a spring que mi usuario ya esta autenticado
                .and()
                //debe llamar primero  mi filtro usando el filtro UsernamePasswordAuthenticationFilter.class
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)//agrega un filtro antes aquí, porque yo voy a implementar UsernamePasswordAuthenticationFilter en mi SecurityFilter
                .build();//construye el objeto
        // la política de creación es stateless y cada request que haga match que es un request de tipo post
        // y va para login permitirle a todos. Después todos los requests tienen que ser autenticados. Y bueno, construye el objeto finalmente.
    }

/*

    #-----------METODO CON EL ROL ADMIN PARA ELIMINAR MEDICOS Y PACIENTES------------------------#
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.DELETE, "/medicos").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/pacientes").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

 */




//    desde la versión final 3.0.0 de Spring Boot, el método
//    authorizeRequests() ha quedado obsoleto y debe ser reemplazado por el nuevo
//    método authorizeHttpRequests(). Asimismo, el método antMatchers() debería
//    ser reemplazado por el nuevo método requestMatchers():
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and().authorizeHttpRequests()
//                .requestMatchers(HttpMethod.POST, "/login").permitAll()
//                .anyRequest().authenticated()
//                .and().build();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder PasswordEncoder(){//indicar que mi clase usuario spring clave y usuario
        return new BCryptPasswordEncoder();
    }
}

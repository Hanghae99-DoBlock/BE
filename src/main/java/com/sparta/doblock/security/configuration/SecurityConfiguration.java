package com.sparta.doblock.security.configuration;

import com.sparta.doblock.exception.AccessDeniedHandlerException;
import com.sparta.doblock.exception.AuthenticationEntryPointException;
import com.sparta.doblock.oauth2.service.PrincipalOauth2UserService;
import com.sparta.doblock.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    private final TokenProvider tokenProvider;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web -> web.ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico")
        );
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{

        http.cors();

        http.csrf().disable()


                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/members/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .apply(new JwtSecurityConfiguration(tokenProvider));


        // for Google & Naver Login
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
//                .anyRequest().permitAll()

                .and()
                .oauth2Login() // OAuth2 Login
                .loginPage("/login")
                .defaultSuccessUrl("/") // redirect to "/" when login success
                .failureUrl("/login") // redirect when login failure
                .userInfoEndpoint() // retrieve user info when login success
                .userService(principalOauth2UserService); // service process




        return http.build();
    }
}

package com.ticketon.ticketon.domain.member.config;

import com.ticketon.ticketon.domain.member.service.MemberDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final MemberDetailService memberService;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(form -> form
//                        .loginPage("/login")
                        .defaultSuccessUrl("/success")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                )
                .csrf(csrf -> csrf.disable())
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, MemberDetailService memberDetailService)
            throws Exception{
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(memberService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    // 패스워드 인코더 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

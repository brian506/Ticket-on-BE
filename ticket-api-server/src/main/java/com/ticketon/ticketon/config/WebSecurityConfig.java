package com.ticketon.ticketon.config;

import com.ticketon.ticketon.domain.member.service.CustomUserDetailService;
import com.ticketon.ticketon.global.constants.Urls;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailService customUserDetailService;

    // !! 추후 엔드포인트 상수로 정리 !!
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(Urls.LOGIN, Urls.SIGN_UP,"/v1/api/**" ).permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults())
                .formLogin(form -> form.defaultSuccessUrl(Urls.EVENTS)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl(Urls.EVENTS)
                        .invalidateHttpSession(true)
                )
                .userDetailsService(customUserDetailService)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, CustomUserDetailService memberDetailService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

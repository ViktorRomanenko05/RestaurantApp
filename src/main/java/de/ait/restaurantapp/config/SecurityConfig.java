//package de.ait.restaurantapp.config;
//
//import org.springframework.context.annotation.*;
//import org.springframework.core.annotation.*;
//import org.springframework.security.config.annotation.web.builders.*;
//import org.springframework.security.config.annotation.web.configuration.*;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.security.provisioning.*;
//import org.springframework.security.web.*;
//import org.springframework.security.config.Customizer;
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    // 1-й фильтр — для /restaurant/admin/**
//    @Bean
//    @Order(1)
//    public SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/restaurant/admin/**")
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().hasRole("ADMIN")
//                )
//                .httpBasic(Customizer.withDefaults())
//                .csrf(csrf -> csrf.disable());
//        return http.build();
//    }
//
//    // 2-й фильтр — для всего остального
//    @Bean
//    public SecurityFilterChain publicSecurity(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/restaurant", "/restaurant/",
//                                "/restaurant/reservations/**",
//                                "/restaurant/reserve/**",
//                                "/restaurant/cancel/**"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .csrf(csrf -> csrf.disable());
//        return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withUsername("admin")
//                .password("{noop}secret")
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(admin);
//    }
//}

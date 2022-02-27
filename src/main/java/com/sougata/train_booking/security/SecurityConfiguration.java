package com.sougata.train_booking.security;

import com.sougata.train_booking.filters.JWTRequestFilter;
import com.sougata.train_booking.services.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final MyUserDetailsService myUserDetailsService;
    private final JWTRequestFilter jwtRequestFilter;

    public SecurityConfiguration(MyUserDetailsService myUserDetailsService, JWTRequestFilter jwtRequestFilter) {
        this.myUserDetailsService = myUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/authenticate", "/create_user", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // added for testing with react locally
        http.cors();
        http.csrf().disable();
        http.headers().frameOptions().disable();
        ///////////////////////////////////////

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
        //return NoOpPasswordEncoder.getInstance();
    }


}




















//    @Bean
//    public BCryptPasswordEncoder encodePassword() {
//        return new BCryptPasswordEncoder(12);
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(myUserDetailsService);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/admin").hasRole("ADMIN")
//                .antMatchers("/user").hasAnyRole("ADMIN", "USER")
//                .antMatchers("/").permitAll()
//                .and()
//                .csrf().ignoringAntMatchers(
//                        "/create_user",
//                        "/update_user",
//                        "/update_name",
//                        "/get_users",
//                        "/remove_user",
//                        "/uploadFile",
//                        "/downloadFile",
//                        "/uploadMultipleFiles",
//                        "/file_storage",
//                        "/create_notification",
//                        "/create_post",
//                        "/create_comment",
//                        "/create_reaction",
//                        "/create_request",
//                        "/destroy_request",
//                        "/accept_request",
//                        "/unfriend",
//                        "/get_my_notifications",
//                        "/get_my_posts",
//                        "/get_incoming_requests",
//                        "/get_outgoing_requests",
//                        "/get_friends",
//                        "/destroy_notification",
//                        "/destroy_post",
//                        "/create_reaction_to_a_comment",
//                        "/create_reply_to_a_comment",
//                        "/create_reaction_to_a_reply",
//                        "/get_a_particular_post",
//                        "/get_my_feed",
//                        "/get_a_friends_profile",
//                        "/get_a_persons_profile",
//                        "/destroy_reaction",
//                        "/destroy_reaction_to_a_comment",
//                        "/destroy_reply_to_a_comment",
//                        "/destroy_reaction_to_a_reply",
//                        "/edit_my_profile")
//                .and()
//                .formLogin();
//    }


package com.scheible.risingempire.web.config.web;

import com.scheible.risingempire.web.security.ConnectedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 *
 * @author sj
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/css/**", "/js/**", "/typescript/**", "/images/**", "/**/favicon.ico").permitAll().and()
                .authorizeRequests().regexMatchers("/players").permitAll().and()
                .authorizeRequests().regexMatchers("/addAi").permitAll().and()
                .authorizeRequests().regexMatchers("/csrf").permitAll().and()
                .authorizeRequests().regexMatchers("/shutdown").permitAll().and() // NOTE Not production ready! ;-)
                .authorizeRequests().antMatchers("/h2-console/**").permitAll().and() // NOTE Not production ready as well! ;-)
                .authorizeRequests().anyRequest().fullyAuthenticated().and()
                .formLogin().loginProcessingUrl("/login").loginPage("/join.html").failureUrl("/join.html?error").defaultSuccessUrl("/game.html", true).permitAll().and()
                .logout().permitAll();

        http
                .csrf()
                .requireCsrfProtectionMatcher(new AndRequestMatcher(CsrfFilter.DEFAULT_CSRF_MATCHER,
                        new RegexRequestMatcher("^(?!/h2-console/)", null)));
        
        
        http.headers().frameOptions().sameOrigin(); // // NOTE Also needed for h2-console.
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(ConnectedPlayer.PASSWORD_ENCODER);
    }

    @Bean
    public RequestDataValueProcessor requestDataValueProcessor() {
        return new CsrfRequestDataValueProcessor();
    }
}

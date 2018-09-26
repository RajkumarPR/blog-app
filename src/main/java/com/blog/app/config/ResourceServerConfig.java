package com.blog.app.config;

import com.blog.app.security.AjaxLogoutSuccessHandler;
import com.blog.app.security.AuthoritiesConstants;
import com.blog.app.security.Http401UnauthorizedEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "resource_id";

    private final TokenStore tokenStore;

    private final Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint;

    private final AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    private final CorsFilter corsFilter;

    public ResourceServerConfig(TokenStore tokenStore, Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint,
                                AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler, CorsFilter corsFilter) {

        this.tokenStore = tokenStore;
        this.http401UnauthorizedEntryPoint = http401UnauthorizedEntryPoint;
        this.ajaxLogoutSuccessHandler = ajaxLogoutSuccessHandler;
        this.corsFilter = corsFilter;
    }
 
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false).tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
       /* http.
                anonymous().disable()
                .authorizeRequests()
                .antMatchers("/v1/api/**").authenticated()
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());*/
        http
                    .exceptionHandling()
                    .authenticationEntryPoint(http401UnauthorizedEntryPoint)
                .and()
                    .logout()
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                .and()
                    .csrf()
                    .disable()
                    .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                    .headers()
                    .frameOptions().disable()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler())
                .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers("/v1/users/authenticate").permitAll()
                    .antMatchers("/v1/users/register").permitAll()
                    .antMatchers("/v1/users/profile-info").permitAll()
                    .antMatchers("/v1/**").authenticated()
                    .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .antMatchers("/v1/api-docs/**").permitAll()
                    .antMatchers("/swagger-resources/configuration/ui").permitAll()
                    .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN);
    }

}

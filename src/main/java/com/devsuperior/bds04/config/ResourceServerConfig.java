package com.devsuperior.bds04.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private Environment env; // Ambiente que roda minha aplicação
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**" };
	
	private static final String[] CLIENT = { "/events",  }; 
	
	private static final String[] ADMIN = { "/cities" };	
	
	
	// Decodifica o Token e analisa se está válido
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		// H2
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll() // Quem estiver acessando alguma rota desse perfil estão todas permitidas
		.antMatchers(HttpMethod.GET).permitAll() // Liberado para todo mundo só o GET
		.antMatchers(HttpMethod.POST,CLIENT).hasAnyRole("CLIENT", "ADMIN") // A rota "events/**" em seu método POST só podem ser acessadas  pelos perfis CLIENT ou ADMIN"
		.antMatchers(HttpMethod.POST,ADMIN).hasAnyRole("ADMIN") // A rota "/cities" em seu método POST só pode ser acessada  pelo perfil ADMIN"
		.anyRequest().authenticated(); // Somente pode acessar qualquer endpoint quem estiver logado
	}	
}

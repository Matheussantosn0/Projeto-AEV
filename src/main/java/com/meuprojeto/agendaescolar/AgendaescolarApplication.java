package com.meuprojeto.agendaescolar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication //define que é uma aplicação Spring Boot
public class AgendaescolarApplication extends SpringBootServletInitializer {	//define que essa aplicação rodara em um tomcat externo

	@Override //sobrescreve o método configure para configurar a aplicação para rodar em um ambiente de servlet
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) { //configura a aplicação para rodar em um ambiente de servlet

		return application.sources(AgendaescolarApplication.class); //define a classe principal da aplicação para o Spring Boot
	}
	public static void main(String[] args) {
		SpringApplication.run(AgendaescolarApplication.class, args); //inicia a aplicação Spring Boot
	}

}

package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;


import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserJsonTest {

		@Test
	public void deveVerificarPrimeiroNivel() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/1")
		.then()
		.statusCode(200)
		.body("id", is(1))
		.body("name", containsString("Silva"))
		.body("age", greaterThan(18));
	}
		@Test
		public void deveVerificarPrimeiroNivelOutrasFormas() {
			Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1" );
			
			//path
			System.out.println(response.path("id"));
			
			//confirma se o esperado com o caminho
			Assert.assertEquals(new Integer (1), response.path("id"));
			//validando o id via parametro
			Assert.assertEquals(new Integer (1), response.path("%s", "id"));
			
			//jsonpath
			JsonPath jpath = new JsonPath(response.asString());
			Assert.assertEquals(1, jpath.getInt("id"));
			
			//from
			int id = JsonPath.from(response.asString()).getInt("id");
			Assert.assertEquals(1, id);
					
		}
		@Test
		public void deveVerificarSegundoNivel() {
			
			given()
			.when()
				.get("http://restapi.wcaquino.me/users/2")
			.then()
			.statusCode(200)
			.body("id", is(2))
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"));
			
		}
		@Test
		public void devVerificarLista() {
			given()
			.when()
				.get("http://restapi.wcaquino.me/users/3")
			.then()
			.statusCode(200)
			.body("id", is(3))
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho"))
			.body("filhos[1].name", is("Luizinho"))
			.body("filhos.name", hasItem("Zezinho"))
			.body("filhos.name", hasItems("Zezinho", "Luizinho"))
			;
		}
		
		@Test
		public void deveRetornarErroUsuarioInexistente() {
			given()
			.when()
				.get("http://restapi.wcaquino.me/users/4")
			.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
			;
			
		}
		@Test
		public void deveVerificarListaRaiz() {
			given()
			.when()
				.get("http://restapi.wcaquino.me/users")
			.then()
			.statusCode(200)
			//usa-se o $ para indicar a raiz da lista ou pode deixar em branco ""
			.body("$", hasSize(3))
			.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
			.body("salary", contains(1234.5678f, 2500, null))
			;
		}
		@Test
		public void devoFazerVerificacoesAvancadas() {
			given()
			.when()
				.get("http://restapi.wcaquino.me/users")
			.then()
			.statusCode(200)
			.body("$", hasSize(3))
			//busca para verificar qts usuarios tem abaixo de 25 anos
			.body("age.findAll{it <= 25}.size()", is(2))
			//busca para verificar qts usuarios tem entre 20 e 25 anos
			.body("age.findAll{it <= 25 && it>20}.size()", is(1))
			//verifica o nome do usuario q possui ente 20 e 25 anos
			.body("findAll{it.age <= 25 && it.age>20}.name", hasItem("Maria Joaquina"))
			//traz o primeiro elemento da lista, transformando a lista em um objeto podendo usar o is()
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
			//traz o último elemento da lista
			.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
			//procura nome que contem N
			.body("findAll{it.name.contains('n') }.name", hasItems("Maria Joaquina", "Ana Júlia"))
			//procura nome que contem mais que 10 caracteres
			.body("findAll{it.name.length() > 10 }.name", hasItems("João da Silva", "Maria Joaquina"))
			//busca na lista, transforma o nome em uppercase e compara com um nome ja em uppercase
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			//busca na lista todo nome começado em Maria, transforma o nome em uppercase e compara com um nome ja em uppercase
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
			//busco a idade, multiplico por 2 e comparo
			.body("age.collect{it * 2}", hasItems(60, 50, 40))
			// busca pelo maior id
			.body("id.max()", is(3))
			//nusca pelo menor salario
			.body("salary.min()", is(1234.5678f))
			//somatorio dos salarios com a aproximação de casas decimais
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
			//somatorio dos salarios, comparando com um range
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
			;	
		}
		
		@Test
		public void devoUnirJsonPathcomJAVA() {
			ArrayList<String> names =
				given()
				.when()
					.get("http://restapi.wcaquino.me/users")
				.then()
				.statusCode(200)
				.extract().path("name.findAll{it.startsWith('Maria')}")
				;		
			Assert.assertEquals(1, names.size());
			Assert.assertTrue(names.get(0).equalsIgnoreCase("maRia Joaquina"));
			Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
		}
}

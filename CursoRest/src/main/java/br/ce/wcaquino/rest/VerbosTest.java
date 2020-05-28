package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.*;

import io.restassured.http.ContentType;


public class VerbosTest {
	
	@Test
	public void deveSalvarusuario() {
		given()
			.log().all()
			.contentType("application/json")
		//	.contentType(ContentType.JSON)  -> outra forma de declarar o content type
			.body("{\"name\": \"Jose\",\"age\": 50}")
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Jose"))
			.body("age", is(50))
		;
		
	}
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{\" age\": 50}")
	.when()
		.post("http://restapi.wcaquino.me/users")
	.then()
		.log().all()
		.statusCode(400)
		.body("id", is(nullValue()))
		.body("error", is("Name é um atributo obrigatório"))
		
	;
	}
	@Test
	public void deveSalvarusuarioViaXML() {
		given()
			.log().all()
			.contentType("application/xml")  
	//		.contentType(ContentType.XML)  -> outra forma de declarar o content type
			.body("<user><name>Jose</name><age>50</age></user>")
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Jose"))
			.body("user.age", is("50"))
		;
		
	}
	@Test
	public void deveAlterarusuario() {
		given()
			.log().all()
			.contentType("application/json")
		//	.contentType(ContentType.JSON)  -> outra forma de declarar o content type
			.body("{\"name\": \"Usuario Alterado\",\"age\": 80}")
		.when()
			.put("http://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
		
	}
	
	@Test
	public void deveCustomizarlURL() {
		given()
			.log().all()
			.contentType("application/json")
		//	.contentType(ContentType.JSON)  -> outra forma de declarar o content type
			.body("{\"name\": \"Usuario Alterado\",\"age\": 80}")
		.when()
			.put("http://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
		
	}
	
	@Test
	public void deveCustomizarlURLParte2() {
		given()
			.log().all()
			.contentType("application/json")
		//	.contentType(ContentType.JSON)  -> outra forma de declarar o content type
			.body("{\"name\": \"Usuario Alterado\",\"age\": 80}")
			.pathParam("entidade", "users")
			.pathParam("userId", 1)
			
		.when()
			.put("http://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
		
	}
	
	@Test
	public void devoRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("http://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
			;
		;
	}
	
	@Test
	public void naoDeveRemoverUsuarioInexistente() {
		given()
			.log().all()
		.when()
			.delete("http://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
			;
		;
	}
	
	@Test
	public void deveSalvarusuarioUsandoMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Usuario via Map");
		params.put("age", 25);
		
		given()
			.log().all()
			.contentType("application/json")
		//	.contentType(ContentType.JSON)  -> outra forma de declarar o content type
			.body(params)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via Map"))
			.body("age", is(25))
		;
		
}
	
	@Test
	public void deveSalvarusuarioUsandoObjeto() {
		User user = new User("Usuario via objeto", 35);
				
		given()
			.log().all()
			.contentType("application/json")
		//	.contentType(ContentType.JSON)  -> outra forma de declarar o content type
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via objeto"))
			.body("age", is(35))
		;
		
}
	
	@Test
	public void deveDeserializarObjetoAoSalvarUsuario() {
		User user = new User("Usuario deserializado", 35);
				
		User usuarioInserido = given()
			.log().all()
			.contentType("application/json")
		//	.contentType(ContentType.JSON)  -> outra forma de declarar o content type
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		
		System.out.println(usuarioInserido);
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
		Assert.assertEquals("Usuario deserializado", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(35));
}

	@Test
	public void deveSalvarusuarioViaXMLUsandoObjeto() {
		User user = new User("Usuario XML", 40);
		
		given()
			.log().all()
			.contentType("application/xml")  
	//		.contentType(ContentType.XML)  -> outra forma de declarar o content type
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario XML"))
			.body("user.age", is("40"))
		;
		
	}
	
	@Test
	public void deveDeserializarXMLAoSalvarusuario() {
		User user = new User("Usuario XML", 40);
		
		User usuarioInserido = given()
			.log().all()
			.contentType("application/xml")  
	//		.contentType(ContentType.XML)  -> outra forma de declarar o content type
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
		Assert.assertThat(usuarioInserido.getName(), is("Usuario XML"));
		Assert.assertThat(usuarioInserido.getAge(), is(40));
		Assert.assertThat(usuarioInserido.getSalary(), nullValue());
		
	}
}


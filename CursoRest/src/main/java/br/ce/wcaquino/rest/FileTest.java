package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;

public class FileTest {
	
	@Test
	public void deveObrigarEnvioArquivo () {
		given()
			.log().all()
		.when()
			.post("http://restapi.wcaquino.me/upload")
		.then()
		.log().all()
		.statusCode(404) //Deveria ser 400
		.body("error", is("Arquivo não enviado"))
		;
	}
	@Test
	public void deveObrigarUploadArquivo () {
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/users.pdf"))
		.when()
			.post("http://restapi.wcaquino.me/upload")
		.then()
		.log().all()
		.statusCode(200)
		.body("name", is("users.pdf"))
		;
	}
	
	@Test
	public void naoDeveFazerUploadArquivoGrande () {
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/teste_size.pdf")) //arquivo com mais de um mega
		.when()
			.post("http://restapi.wcaquino.me/upload")
		.then()
		.log().all()
		.time(lessThan(9000L))
		.statusCode(413)
		
	
		;
	}
	
	@Test
	public void deveBaixarArquivo() throws IOException {
		byte[] image = given()
		.log().all()
	.when()
		.get("http://restapi.wcaquino.me/download")
	.then()
//		.log().all()
		.statusCode(200)
		.extract().asByteArray();
	
	;
	File imagem = new File("src/main/resources/file.jpg");
	OutputStream out = new FileOutputStream(imagem);	
	out.write(image);
	out.close();
	
	System.out.println(imagem.length());
	Assert.assertThat(imagem.length(), lessThan(100000L));
		
	}
}

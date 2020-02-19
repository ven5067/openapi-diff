package com.openappi.diff;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.openappi.diff.compare.OpenAPIDiff;
import com.openappi.diff.model.ChangedOpenAPI;
import com.openappi.diff.output.HtmlRender;

@SpringBootApplication
public class OpenapiDiffApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenapiDiffApplication.class, args);
		
		ChangedOpenAPI diff = OpenAPIDiff.compare("openapi3.0.json", "openapi3.0_modified.json");

		String html = new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css").render(diff);

		try {
			FileWriter fw = new FileWriter("testNewApi.html");
			fw.write(html);
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

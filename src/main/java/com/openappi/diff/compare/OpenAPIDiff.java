package com.openappi.diff.compare;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openappi.diff.model.ChangedOpenAPI;

import io.swagger.models.auth.AuthorizationValue;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class OpenAPIDiff {

	public static final String SWAGGER_VERSION_V3 = "3.0";

	private static Logger logger = LoggerFactory.getLogger(OpenAPIDiff.class);

	private OpenAPI oldSpecification;
	private OpenAPI newSpecification;

	public static ChangedOpenAPI compare(String oldSpec, String newSpec) {
		return new OpenAPIDiff(oldSpec, newSpec, null, SWAGGER_VERSION_V3).compare();
	}

	private OpenAPIDiff(String oldSpec, String newSpec, List<AuthorizationValue> auths, String version) {

		OpenAPIV3Parser openAPIV3Parser = new OpenAPIV3Parser();
		oldSpecification = openAPIV3Parser.read(oldSpec, null, null);
		newSpecification = openAPIV3Parser.read(newSpec, null, null);

		if (null == oldSpec || null == newSpec) {
			throw new RuntimeException("cannot read api-doc from spec.");
		}

	}

	private ChangedOpenAPI compare() {
		return SpecificationDiff.diff(oldSpecification, newSpecification);
	}

	public String getOldVersion() {
		return oldSpecification.getInfo().getVersion();
	}

	public String getNewVersion() {
		return newSpecification.getInfo().getVersion();
	}
}

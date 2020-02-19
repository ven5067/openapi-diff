package com.openappi.diff.model;

import com.openappi.diff.compare.PathsDiff;

import io.swagger.v3.oas.models.OpenAPI;

public class ChangedOpenAPI extends OpenAPI {

	private PathsDiff pathsDiff;

	public PathsDiff getPathsDiff() {
		return pathsDiff;
	}

	public void setPathsDiff(PathsDiff pathsDiff) {
		this.pathsDiff = pathsDiff;
	}

}

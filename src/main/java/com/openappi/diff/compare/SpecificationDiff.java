package com.openappi.diff.compare;

import java.util.Objects;

import com.openappi.diff.model.ChangedOpenAPI;
import com.openappi.diff.utils.ComparisonUtils;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class SpecificationDiff {

	public static ChangedOpenAPI diff(OpenAPI oldSpec, OpenAPI newSpec) {

		if (null == oldSpec || null == newSpec) {
			throw new IllegalArgumentException("cannot diff null spec.");
		}

		ChangedOpenAPI changedOpenAPI = new ChangedOpenAPI();
		changedOpenAPI.setOpenapi(null);

		// OpeanAPI 
		if (ComparisonUtils.isDiff(oldSpec.getOpenapi(), newSpec.getOpenapi())) {
			changedOpenAPI.setOpenapi(newSpec.getOpenapi());
		}

		// Info
		Info info = InfoDiff.diff(oldSpec.getInfo(), newSpec.getInfo());
		if (Objects.nonNull(info)) {
			changedOpenAPI.setInfo(info);
		}

		// Paths
		PathsDiff pathsDiff = PathsDiff.diff(oldSpec.getPaths(), newSpec.getPaths());
		changedOpenAPI.setPathsDiff(pathsDiff);

		// ExternalDocs
		ExternalDocumentation changedExternalDocumentation = ExternalDocumentationDiff.diff(oldSpec.getExternalDocs(),
				newSpec.getExternalDocs());
		if (Objects.nonNull(changedExternalDocumentation)) {
			changedOpenAPI.setExternalDocs(changedExternalDocumentation);
		}
		
		

		return changedOpenAPI;
	}

}

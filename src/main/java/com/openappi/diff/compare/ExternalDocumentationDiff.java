package com.openappi.diff.compare;

import com.openappi.diff.utils.ComparisonUtils;

import io.swagger.v3.oas.models.ExternalDocumentation;

public class ExternalDocumentationDiff {
	
	public static ExternalDocumentation diff(ExternalDocumentation oldDoc, ExternalDocumentation newDoc) {
		
		ExternalDocumentation changedExternalDocumentation = null;
		
		if(ComparisonUtils.isDiff(oldDoc.toString(), newDoc.toString())) {
			changedExternalDocumentation =  new ExternalDocumentation();
			
			if(ComparisonUtils.isDiff(oldDoc.getDescription(), newDoc.getDescription())) {
				changedExternalDocumentation.setDescription(newDoc.getDescription());
			}
			
			if(ComparisonUtils.isDiff(oldDoc.getUrl(), newDoc.getUrl())) {
				changedExternalDocumentation.setUrl(newDoc.getUrl());
			}
			
			// TODO: extensions - later
		}
		
		return changedExternalDocumentation;
	}
	
}

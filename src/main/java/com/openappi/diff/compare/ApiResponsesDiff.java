package com.openappi.diff.compare;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.openappi.diff.model.ChangedApiResponse;
import com.openappi.diff.model.ChangedHeader;
import com.openappi.diff.utils.ComparisonUtils;

import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class ApiResponsesDiff {
	
	LinkedHashMap<String, ApiResponse> increased; 	
	LinkedHashMap<String, ApiResponse> missing; 	
	LinkedHashMap<String, ApiResponse> changed;
	
	public ApiResponsesDiff() {
		this.increased = new LinkedHashMap<String, ApiResponse>();
		this.missing = new LinkedHashMap<String, ApiResponse>();
		this.changed = new LinkedHashMap<String, ApiResponse>();
	}

	public ApiResponsesDiff diff(LinkedHashMap<String, ApiResponse> oldResponses, LinkedHashMap<String, ApiResponse> newResponses) {
		if (null == oldResponses || newResponses == null) return null;
		
		Map<String, Collection<String>> keyDiffMap = ComparisonUtils.findDiff(oldResponses.keySet(), newResponses.keySet());
		List<String> added = (List<String>) keyDiffMap.get("added");
		List<String> removed = (List<String>) keyDiffMap.get("removed");
		
		added.stream().forEach(k -> this.increased.put(k,newResponses.get(k)));
		removed.stream().forEach(k -> this.missing.put(k,oldResponses.get(k)));
		
		ChangedApiResponse changedAPIResponse = new ChangedApiResponse();
		
		oldResponses.forEach((k, v) -> {
			ApiResponse oldApiResponse = oldResponses.get(k);
			ApiResponse newApiResponse = newResponses.get(k);
			
			if(!(added.contains(k) || removed.contains(k))) {
				if(oldApiResponse.getDescription() != newApiResponse.getDescription()) {
					changedAPIResponse.setDescription(newApiResponse.getDescription());
				}
				
				if(oldApiResponse.get$ref() != newApiResponse.get$ref()) {
					changedAPIResponse.set$ref(newApiResponse.getDescription());
				}
				
				Map<String, Header> oldHeaders = oldApiResponse.getHeaders();
				Map<String, Header> newHeaders = newApiResponse.getHeaders();
				
				if(oldHeaders.toString() != newHeaders.toString()) {
					oldHeaders.forEach((key,oldHeader) -> {
						Header newHeader = newHeaders.get(key);
						ChangedHeader changedHeader = new ChangedHeader();

						if(oldHeader.getDescription() != newHeader.getDescription()) {
							changedHeader.setDescription(newHeader.getDescription());
						}
						
						if(oldHeader.get$ref() != newHeader.get$ref()) {
							changedHeader.set$ref(newHeader.get$ref());
						}
						
						if(oldHeader.getDeprecated() != newHeader.getDeprecated()) {
							changedHeader.setDeprecated(newHeader.getDeprecated());
						}
						
						if(oldHeader.getRequired() != newHeader.getRequired()) {
							changedHeader.setRequired(newHeader.getRequired());
						}
						
						if(oldHeader.getStyle() != newHeader.getStyle()) {
							changedHeader.setStyle(newHeader.getStyle());
						}
						
						if(oldHeader.getExplode() != newHeader.getExplode()) {
							changedHeader.setExplode(newHeader.getExplode());
						}
						
						// TODO: Schema
						
						// TODO: Map<String, Example>
						
						// TODO: example
						
						// TODO: Content
						
						// TODO: java.util.Map<String, Object> extensions
					});
				}
			}
		});
		
		
		return null;
	}
}

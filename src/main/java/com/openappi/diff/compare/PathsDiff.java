package com.openappi.diff.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.openappi.diff.model.ChangedEndpoint;
import com.openappi.diff.model.ChangedOperation;
import com.openappi.diff.model.Endpoint;
import com.openappi.diff.utils.ComparisonUtils;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;

public class PathsDiff {

	private List<Endpoint> added = new ArrayList<>();
	private List<Endpoint> missing = new ArrayList<>();
	private List<ChangedEndpoint> changed = new ArrayList<>();

	public static PathsDiff diff(Paths oldPaths, Paths newPaths) {

		PathsDiff pathDiff = new PathsDiff();

		if (!oldPaths.toString().equals(newPaths.toString())) {
			MapKeyDiff<String, PathItem> pathDiffMap = MapKeyDiff.diff(oldPaths, newPaths);
			pathDiff.added = convert2EndpointList(pathDiffMap.getIncreased());
			pathDiff.missing = convert2EndpointList(pathDiffMap.getMissing());
			pathDiff.changed = new ArrayList<>();

			List<String> sharedKey = pathDiffMap.getSharedKey();
			sharedKey.stream().forEach((pathUrl) -> {
				ChangedEndpoint changedEndpoint = new ChangedEndpoint();
				changedEndpoint.setPathUrl(pathUrl);
				PathItem oldPath = oldPaths.get(pathUrl);
				PathItem newPath = newPaths.get(pathUrl);

				Map<HttpMethod, Operation> oldOperationMap = oldPath.readOperationsMap();
				Map<HttpMethod, Operation> newOperationMap = newPath.readOperationsMap();

				MapKeyDiff<HttpMethod, Operation> operationDiff = MapKeyDiff.diff(oldOperationMap, newOperationMap);

				Map<HttpMethod, Operation> addedOpearations = operationDiff.getIncreased();
				Map<HttpMethod, Operation> missingOpearations = operationDiff.getMissing();
				changedEndpoint.setNewOperations(addedOpearations);
				changedEndpoint.setMissingOperations(missingOpearations);

				List<HttpMethod> sharedMethods = operationDiff.getSharedKey();
				Map<HttpMethod, ChangedOperation> operationsMap = new HashMap<>();

				sharedMethods.stream().forEach((method) -> {
					ChangedOperation changedOperation = new ChangedOperation();
					Operation oldOperation = oldOperationMap.get(method);
					Operation newOperation = newOperationMap.get(method);

					// Tags comparison
					if (!oldOperation.getTags().equals(newOperation.getTags())) {
						Map<String, List<String>> tagDiff = findTagDiff(oldOperation.getTags(), newOperation.getTags());
						changedOperation.setTagDiff(tagDiff);
					}

					// Summary comparison
					String oldSusmmary = oldOperation.getSummary();
					String newSummary = newOperation.getSummary();

					if (ComparisonUtils.isDiff(oldSusmmary, newSummary)) {
						changedOperation.setSummary(newSummary == null ? "" : newSummary);
					}

					// Description comparison
					String oldDesc = oldOperation.getDescription();
					String newDesc = newOperation.getDescription();

					if (ComparisonUtils.isDiff(oldDesc, newDesc)) {
						changedOperation.setDescription(newDesc == null ? "" : newDesc);
					}

					// TODO: externalDocs - later

					String oldOpearationId = oldOperation.getOperationId();
					String newOperationId = newOperation.getOperationId();

					if (ComparisonUtils.isDiff(oldOpearationId, newOperationId)) {
						changedOperation.setOperationId(newOperationId == null ? "" : newOperationId);
					}

					// TODO: Parameters
					List<Parameter> oldParameters = oldOperation.getParameters();
					List<Parameter> newParameters = newOperation.getParameters();

					ParameterDiff paramDiff = new ParameterDiff().diff(oldParameters, newParameters);
					changedOperation.setAddParameters(paramDiff.getIncreased());
					changedOperation.setMissingParameters(paramDiff.getMissing());
					changedOperation.setChangedParameter(paramDiff.getChanged());

					// TODO: RequestBody - later

					// TODO: API Responses - later
//					ApiResponses oldResponses = oldOperation.getResponses();
//					ApiResponses newResponses = newOperation.getResponses();
//					ApiResponsesDiff resposeDiff = new ApiResponsesDiff().diff(oldResponses, newResponses);

					// TODO: callbacks - later

					changedOperation.setDeprecated(newOperation.getDeprecated());

					// TODO: Security - later

					// TODO: Servers - later

					// TODO: extensions
					if (changedOperation.isDiff()) {
						operationsMap.put(method, changedOperation);
					}

				});

				changedEndpoint.setChangedOperations(operationsMap);

				pathDiff.added
						.addAll(convert2EndpointList(changedEndpoint.getPathUrl(), changedEndpoint.getNewOperations()));
				pathDiff.missing.addAll(
						convert2EndpointList(changedEndpoint.getPathUrl(), changedEndpoint.getMissingOperations()));
				if (changedEndpoint.isDiff()) {
					pathDiff.changed.add(changedEndpoint);
				}
			});
		}

		return pathDiff;
	}

	public List<Endpoint> getAdded() {
		return added;
	}

	public void setAdded(List<Endpoint> added) {
		this.added = added;
	}

	public List<Endpoint> getMissing() {
		return missing;
	}

	public void setMissing(List<Endpoint> missing) {
		this.missing = missing;
	}

	public List<ChangedEndpoint> getChanged() {
		return changed;
	}

	public void setChanged(List<ChangedEndpoint> changed) {
		this.changed = changed;
	}

	private static List<Endpoint> convert2EndpointList(Map<String, PathItem> map) {
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		if (null == map)
			return endpoints;
		map.forEach((url, path) -> {
			Map<HttpMethod, Operation> operationMap = path.readOperationsMap();
			operationMap.forEach((httpMethod, operation) -> {
				Endpoint endpoint = new Endpoint();
				endpoint.setPathUrl(url);
				endpoint.setMethod(httpMethod);
				endpoint.setSummary(operation.getSummary());
				endpoint.setPath(path);
				endpoint.setOperation(operation);
				endpoints.add(endpoint);
			});
		});

		return endpoints;
	}

	private static Collection<? extends Endpoint> convert2EndpointList(String pathUrl, Map<HttpMethod, Operation> map) {
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		if (null == map)
			return endpoints;
		map.forEach((httpMethod, operation) -> {
			Endpoint endpoint = new Endpoint();
			endpoint.setPathUrl(pathUrl);
			endpoint.setMethod(httpMethod);
			endpoint.setSummary(operation.getSummary());
			endpoint.setOperation(operation);
			endpoints.add(endpoint);
		});
		return endpoints;
	}

	private static Map<String, List<String>> findTagDiff(List<String> oldTags, List<String> newTags) {
		List<String> removed = oldTags.stream().filter(aObject -> {
			return !newTags.contains(aObject);
		}).collect(Collectors.toList());

		List<String> added = newTags.stream().filter(aObject -> !oldTags.contains(aObject))
				.collect(Collectors.toList());

		HashMap<String, List<String>> tagDiff = new HashMap<>();
		tagDiff.put("removed", removed);
		tagDiff.put("added", added);

		return tagDiff;
	}

}

package com.openappi.diff.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.openappi.diff.utils.ComparisonUtils;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;

public class ServerDiff {

	List<Server> added = new ArrayList<Server>();
	List<Server> missing = new ArrayList<Server>();
	List<Server> removed = new ArrayList<Server>();

	public List<Server> diff(List<Server> oldServers, List<Server> newServers) {

		ListDiff<Server> serverDiff = ListDiff.diff(oldServers, newServers, (t, param) -> {
			for (Server server : t) {
				if (server.getUrl().equals(param.getUrl())) {
					return server;
				}
			}
			return null;
		});

		this.added.addAll(serverDiff.getIncreased());
		this.missing.addAll(serverDiff.getMissing());
		Map<Server, Server> shared = serverDiff.getShared();

		shared.forEach((leftServer, rightServer) -> {
			if(ComparisonUtils.isDiff(leftServer.toString(), rightServer.toString())) {
				Server changedServer = new Server();
				
				String leftUrl = leftServer.getUrl(); 
				String rightUrl = rightServer.getUrl();
				if(ComparisonUtils.isDiff(leftUrl, rightUrl)) {
					changedServer.setUrl(rightUrl);
				}
				
				String leftDesc = leftServer.getDescription();
				String rightDesc = rightServer.getDescription();
				if(ComparisonUtils.isDiff(leftDesc, rightDesc)) {
					changedServer.setUrl(rightDesc);
				}
				
				ServerVariables leftVariables = leftServer.getVariables();
				ServerVariables rightVariables = rightServer.getVariables();
				if(ComparisonUtils.isDiff(leftVariables.toString(), rightVariables.toString())) {
					MapKeyDiff<String, ServerVariable> serverVariableDiffMap = MapKeyDiff.diff(leftVariables, rightVariables);
					// TODO: Server variables comparison
				}
				
				
				// TODO: extensions - later
			}
		});

		return null;
	}

	public List<Server> getAdded() {
		return added;
	}

	public void setAdded(List<Server> added) {
		this.added = added;
	}

	public List<Server> getMissing() {
		return missing;
	}

	public void setMissing(List<Server> missing) {
		this.missing = missing;
	}

	public List<Server> getRemoved() {
		return removed;
	}

	public void setRemoved(List<Server> removed) {
		this.removed = removed;
	}

}

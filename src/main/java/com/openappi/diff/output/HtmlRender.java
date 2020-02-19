package com.openappi.diff.output;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.del;
import static j2html.TagCreator.div;
import static j2html.TagCreator.document;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.header;
import static j2html.TagCreator.hr;
import static j2html.TagCreator.html;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.ol;
import static j2html.TagCreator.p;
import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.script;
import static j2html.TagCreator.span;
import static j2html.TagCreator.title;
import static j2html.TagCreator.ul;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.openappi.diff.model.ChangedEndpoint;
import com.openappi.diff.model.ChangedOpenAPI;
import com.openappi.diff.model.ChangedOperation;
import com.openappi.diff.model.ChangedParameter;
import com.openappi.diff.model.ElProperty;
import com.openappi.diff.model.Endpoint;

import io.swagger.models.properties.Property;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import j2html.tags.ContainerTag;

public class HtmlRender implements Render {

	private String title;
	private String linkCss;

	public HtmlRender() {
		this("Api Change Log", "demo.css");
	}

	public HtmlRender(String title, String linkCss) {
		this.title = title;
		this.linkCss = linkCss;
	}

	public String render(ChangedOpenAPI diff) {
		List<Endpoint> newEndpoints = diff.getPathsDiff().getAdded();
		ContainerTag ol_newEndpoint = ol_newEndpoint(newEndpoints);

		List<Endpoint> missingEndpoints = diff.getPathsDiff().getMissing();
		ContainerTag ol_missingEndpoint = ol_missingEndpoint(missingEndpoints);

		List<ChangedEndpoint> changedEndpoints = diff.getPathsDiff().getChanged();
		ContainerTag ol_changed = ol_changed(changedEndpoints);

		ContainerTag p_versions = p_versions(diff.getOpenapi());

		ContainerTag p_info = p_info(diff.getInfo());
		
		ContainerTag p_external_docs = p_external_docs(diff.getExternalDocs());

		return renderHtml(ol_newEndpoint, ol_missingEndpoint, ol_changed, p_versions, p_info, p_external_docs);
	}

	public String renderHtml(ContainerTag ol_new, ContainerTag ol_miss, ContainerTag ol_changed,
			ContainerTag p_versions, ContainerTag p_info, ContainerTag p_external_docs) {
		ContainerTag html = html().attr("lang", "en").with(
				head().with(meta().withCharset("utf-8"), title(title), script(rawHtml(
						"function showHide(id){if(document.getElementById(id).style.display==\'none\'){document.getElementById(id).style.display=\'block\';document.getElementById(\'btn_\'+id).innerHTML=\'&uArr;\';}else{document.getElementById(id).style.display=\'none\';document.getElementById(\'btn_\'+id).innerHTML=\'&dArr;\';}return true;}"))
								.withType("text/javascript"),
						link().withRel("stylesheet").withHref(linkCss)),
				body().with(header().with(h1(title)),
						div().withClass("article").with(div_headArticle("OpenAPI", "openapi", p_versions),
								div_headArticle("Info", "info", p_info), 
								div_headArticle("External Docs", "external_docs", p_external_docs),
								div_headArticle("What's New", "new", ol_new),
								div_headArticle("What's Deprecated", "deprecated", ol_miss),
								div_headArticle("What's Changed", "changed", ol_changed))));

		return document().render() + html.render();
	}

	private ContainerTag div_headArticle(final String title, final String type, final ContainerTag ol) {
		return div().with(h2(title).with(a(rawHtml("&uArr;")).withId("btn_" + type).withClass("showhide").withHref("#")
				.attr("onClick", "javascript:showHide('" + type + "');")), hr(), ol);
	}

	private ContainerTag p_versions(String version) {
		ContainerTag p = p().withStyle("margin: 10px 20px;").withId("openapi");
		if(Objects.nonNull(version)) {
			p.withText("Changed to " + version);
		}
		return p;
	}

	private ContainerTag p_external_docs(ExternalDocumentation externalDocs) {
		ContainerTag ul_detail = ul().withClass("detail").withId("external_docs");
		ContainerTag ul = ul().withClass("change param");
		
		if (Objects.nonNull(externalDocs)) {
			String description = externalDocs.getDescription();
			String url = externalDocs.getUrl();
			
			if(Objects.nonNull(description)) {
				ul.with(li().withText("Description changed to: " + description));
			}
			
			if(Objects.nonNull(url)) {
				ul.with(li().withText("Url changed to: " + url));
			}
		}
		
		return ul_detail.with(ul);
	}
	
	private ContainerTag p_info(Info info) {
		ContainerTag ul_detail = ul().withClass("detail").withId("info");
		ContainerTag ul = ul().withClass("change param");
		if (Objects.nonNull(info)) {
			if (Objects.nonNull(info.getTitle())) {
				ul.with(li().withText("Title changed to: " + info.getTitle()));
			}

			if (Objects.nonNull(info.getDescription())) {
				ul.with(li().withText("Description changed to: " + info.getDescription()));
			}

			if (Objects.nonNull(info.getTermsOfService())) {
				ul.with(li().withText("TermsOfService changed to: " + info.getTermsOfService()));
			}

			if (Objects.nonNull(info.getContact())) {
				if (Objects.nonNull(info.getContact().getName())) {
					ul.with(li().withText("Contact name changed to: " + info.getContact().getName()));
				}

				if (Objects.nonNull(info.getContact().getEmail())) {
					ul.with(li().withText("Contact email changed to: " + info.getContact().getEmail()));
				}

				if (Objects.nonNull(info.getContact().getUrl())) {
					ul.with(li().withText("Contact url changed to: " + info.getContact().getUrl()));
				}
			}

			if (Objects.nonNull(info.getLicense())) {
				if (Objects.nonNull(info.getLicense().getName())) {
					ul.with(li().withText("License name changed to: " + info.getLicense().getName()));
				}

				if (Objects.nonNull(info.getLicense().getUrl())) {
					ul.with(li().withText("License url changed to: " + info.getLicense().getUrl()));
				}
			}

			if (Objects.nonNull(info.getVersion())) {
				ul.with(li().withText("Version changed to: " + info.getVersion()));
			}
		}
		return ul_detail.with(ul);
	}

	private ContainerTag ol_newEndpoint(List<Endpoint> endpoints) {
		if (null == endpoints)
			return ol().withId("new");
		ContainerTag ol = ol().withId("new");
		for (Endpoint endpoint : endpoints) {
			ol.with(li_newEndpoint(endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
		}
		return ol;
	}

	private ContainerTag li_newEndpoint(String method, String path, String desc) {
		return li().with(span(method).withClass(method)).withText(path + " ").with(span(null == desc ? "" : desc));
	}

	private ContainerTag ol_missingEndpoint(List<Endpoint> endpoints) {
		if (null == endpoints)
			return ol().withId("deprecated");
		ContainerTag ol = ol().withId("deprecated");
		for (Endpoint endpoint : endpoints) {
			ol.with(li_missingEndpoint(endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
		}
		return ol;
	}

	private ContainerTag li_missingEndpoint(String method, String path, String desc) {
		return li().with(span(method).withClass(method), del().withText(path))
				.with(span(null == desc ? "" : " " + desc));
	}

	private ContainerTag ol_changed(List<ChangedEndpoint> changedEndpoints) {
		if (null == changedEndpoints)
			return ol().withId("changed");
		ContainerTag ol = ol().withId("changed");
		for (ChangedEndpoint changedEndpoint : changedEndpoints) {
			String pathUrl = changedEndpoint.getPathUrl();
			Map<HttpMethod, ChangedOperation> changedOperations = changedEndpoint.getChangedOperations();
			for (Entry<HttpMethod, ChangedOperation> entry : changedOperations.entrySet()) {
				String method = entry.getKey().toString();
				ChangedOperation changedOperation = entry.getValue();
				String desc = changedOperation.getSummary();

				ContainerTag ul_detail = ul().withClass("detail");

				Map<String, List<String>> tagDiff = changedOperation.getTagDiff();

				if (!tagDiff.isEmpty()) {
					ul_detail.with(li().with(h3("Tags")).with(ul_tags(changedOperation)));
				}

				if (Objects.nonNull(changedOperation.getSummary())) {
					if (StringUtils.isEmpty(changedOperation.getSummary())) {
						ul_detail.with(li().with(h3().with(del().withText("Summary"))));
					} else {
						ul_detail.with(li().with(h3("Summary")).with(ul().withClass("change param")
								.with(li().withStyle("color: green").withText(changedOperation.getSummary()))));
					}
				}

				if (Objects.nonNull(changedOperation.getDescription())) {
					if (StringUtils.isEmpty(changedOperation.getDescription())) {
						ul_detail.with(li().with(h3().with(del().withText("Description"))));
					} else {
						ul_detail.with(li().with(h3("Description")).with(ul().withClass("change param")
								.with(li().withStyle("color: green").withText(changedOperation.getDescription()))));
					}
				}

				if (Objects.nonNull(changedOperation.getOperationId())) {
					if (StringUtils.isEmpty(changedOperation.getOperationId())) {
						ul_detail.with(li().with(h3().with(del().withText("OpearationId"))));
					} else {
						ul_detail.with(li().with(h3("OpearationId")).with(ul().withClass("change param")
								.with(li().withStyle("color: green").withText(changedOperation.getOperationId()))));
					}
				}

				if (changedOperation.isDiffParam()) {
					ul_detail.with(li().with(h3("Parameter")).with(ul_param(changedOperation)));
				}
				if (changedOperation.isDiffProp()) {
					ul_detail.with(li().with(h3("Return Type")).with(ul_response(changedOperation)));
				}
				ol.with(li().with(span(method).withClass(method)).withText(pathUrl + " ")
						.with(span(null == desc ? "" : desc)).with(ul_detail));
			}
		}
		return ol;
	}

	private ContainerTag ul_response(ChangedOperation changedOperation) {
		List<ElProperty> addProps = changedOperation.getAddProps();
		List<ElProperty> delProps = changedOperation.getMissingProps();
		ContainerTag ul = ul().withClass("change response");
		for (ElProperty prop : addProps) {
			ul.with(li_addProp(prop));
		}
		for (ElProperty prop : delProps) {
			ul.with(li_missingProp(prop));
		}
		return ul;
	}

	private ContainerTag li_missingProp(ElProperty prop) {
		Property property = prop.getProperty();
		return li().withClass("missing").withText("Delete").with(del(prop.getEl())).with(
				span(null == property.getDescription() ? "" : ("//" + property.getDescription())).withClass("comment"));
	}

	private ContainerTag li_addProp(ElProperty prop) {
		Property property = prop.getProperty();
		return li().withText("Add " + prop.getEl()).with(
				span(null == property.getDescription() ? "" : ("//" + property.getDescription())).withClass("comment"));
	}

	private ContainerTag ul_tags(ChangedOperation changedOperation) {
		List<String> removed = changedOperation.getTagDiff().get("removed");
		List<String> added = changedOperation.getTagDiff().get("added");

		ContainerTag ul = ul().withClass("change param");
		for (String tag : added) {
			ul.with(li_add(tag));
		}

		for (String tag : removed) {
			ul.with(li_missing(tag));
		}
		return ul;
	}

	private ContainerTag ul_param(ChangedOperation changedOperation) {
		List<Parameter> addParameters = changedOperation.getAddParameters();
		List<Parameter> delParameters = changedOperation.getMissingParameters();
		List<ChangedParameter> changedParameters = changedOperation.getChangedParameter();
		ContainerTag ul = ul().withClass("change param").withStyle("color: green");
		for (Parameter param : addParameters) {
			ul.with(li_addParam(param));
		}
		for (ChangedParameter param : changedParameters) {
			List<ElProperty> increased = param.getIncreased();
			for (ElProperty prop : increased) {
				ul.with(li_addProp(prop));
			}
		}
		for (ChangedParameter param : changedParameters) {
			boolean changeRequired = param.isChangeRequired();
			boolean changeDescription = param.isChangeDescription();
			if (changeRequired || changeDescription)
				ul.with(li_changedParam(param));
		}
		for (ChangedParameter param : changedParameters) {
			List<ElProperty> missing = param.getMissing();
			for (ElProperty prop : missing) {
				ul.with(li_missingProp(prop));
			}
		}
		for (Parameter param : delParameters) {
			ul.with(li_missingParam(param));
		}
		return ul;
	}

	private ContainerTag li_add(String s) {
		return li().withStyle("color: green").withText(s);
	}

	private ContainerTag li_missing(String s) {
		return li().withClass("missing").with(del(s));
	}

	private ContainerTag li_addParam(Parameter param) {
		return li().withText("Add " + param.getName())
				.with(span(null == param.getDescription() ? "" : ("//" + param.getDescription())).withClass("comment"));
	}

	private ContainerTag li_missingParam(Parameter param) {
		return li().withClass("missing").with(span("Delete")).with(del(param.getName()))
				.with(span(null == param.getDescription() ? "" : ("//" + param.getDescription())).withClass("comment"));
	}

	private ContainerTag li_changedParam(ChangedParameter changeParam) {
		boolean changeRequired = changeParam.isChangeRequired();
		boolean changeDescription = changeParam.isChangeDescription();
		Parameter rightParam = changeParam.getRightParameter();
		Parameter leftParam = changeParam.getLeftParameter();
		ContainerTag li = li().withText(rightParam.getName());
		if (changeRequired) {
			li.withText(" change into " + (rightParam.getRequired() ? "required" : "not required"));
		}
		if (changeDescription) {
			li.withText(" Notes ").with(del(leftParam.getDescription()).withClass("comment")).withText(" change into ")
					.with(span(span(null == rightParam.getDescription() ? "" : rightParam.getDescription())
							.withClass("comment")));
		}
		return li;
	}

}

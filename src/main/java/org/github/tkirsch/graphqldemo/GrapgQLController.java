package org.github.tkirsch.graphqldemo;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class GrapgQLController {

	public GrapgQLController(final Router router) {
		var graphQLHandler = new GraphQLHandler();
		router.route("/graphql").handler(rc -> {
			rc.request().bodyHandler(rh -> {
				var json = rh.toString();
				var queryJson = new JsonObject(json);
				var query = queryJson.getString("query");
				var variables = defaultIfNull(queryJson.getJsonObject("variables"), new JsonObject()).getMap();
				var operationName = queryJson.getString("operationName");
				var result = graphQLHandler.execToSpecification(query, operationName, queryJson, null, variables);
				var response = new JsonObject(result);
				if (result.containsKey("errors")) {
					rc.response().setStatusCode(BAD_REQUEST.code());
				} else {
					rc.response().setStatusCode(OK.code());
				}
				rc.response().putHeader("Content-Type", "application/json");
				rc.response().end(response.encode());
			});
		});
	}
}

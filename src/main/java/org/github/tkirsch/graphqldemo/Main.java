package org.github.tkirsch.graphqldemo;

import static io.vertx.core.http.HttpMethod.GET;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class Main extends AbstractVerticle {

	private static final int PORT = 8181;

	@Override
	public void start(final Future<Void> startFuture) throws Exception {
		var router = Router.router(vertx);
		new GrapgQLController(router);
		var staticHandler = StaticHandler.create("graphiql");
		staticHandler.setDirectoryListing(false);
		staticHandler.setCachingEnabled(false);
		staticHandler.setIndexPage("index.html");
		router.route("/").method(GET).handler(staticHandler);
		vertx.createHttpServer().requestHandler(router).listen(PORT, http -> {
			if (http.succeeded()) {
				startFuture.complete();
				System.out.println("HTTP server started on port " + PORT);
			} else {
				startFuture.fail(http.cause());
			}
		});
	}

	public static void main(final String[] args) {
		var vertx = Vertx.vertx();
		vertx.deployVerticle(new Main());
	}
}
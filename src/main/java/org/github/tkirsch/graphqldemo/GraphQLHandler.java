package org.github.tkirsch.graphqldemo;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;

public class GraphQLHandler {

	public static class PersonDTO {

		private long id;
		private String name;
		private Integer age;

		public PersonDTO() {
		}

		public PersonDTO(final Map<String, Object> map) {
			this((String) map.get("name"), (Integer) map.get("age"));
		}

		public PersonDTO(final String name, final Integer age) {
			this.name = name;
			this.age = age;
		}

		public PersonDTO(final long id, final String name, final Integer age) {
			this.id = id;
			this.name = name;
			this.age = age;
		}

		public long getId() {
			return id;
		}

		public void setId(final long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(final Integer age) {
			this.age = age;
		}
	}

	private static final PersonDTO ME = new PersonDTO(1L, "Thomas Kirsch", 39);
	private static final List<PersonDTO> PERSONS = new ArrayList<>(List.of(ME, new PersonDTO(2L, "Peter P.", 12)));
	private final GraphQL graphQL;

	public GraphQLHandler() {
		graphQL = getGraphQL();
	}

	private GraphQL getGraphQL() {
		var runtimeWiring = RuntimeWiring.newRuntimeWiring() //
				.type("QueryType", typeWiring -> typeWiring //
						.dataFetcher("me", new StaticDataFetcher(ME)) //
						.dataFetcher("person", env -> {
							var name = env.getArgument("name");
							return PERSONS.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
						}) //
						.dataFetcher("persons", new StaticDataFetcher(PERSONS)) //
				) //
				.type("MutationType", typeWiring -> typeWiring //
						.dataFetcher("create", env -> {
							var personInput = new PersonDTO(env.getArgument("person"));
							var maxId = PERSONS.stream().map(PersonDTO::getId).max(Long::compare).orElse(0L);
							personInput.id = maxId + 1L;
							PERSONS.add(personInput);
							return personInput;
						}) //
						.dataFetcher("update", env -> {
							var id = toLong(env.getArgument("id"));
							var personInput = new PersonDTO(env.getArgument("person"));
							return PERSONS.stream().filter(p -> p.getId() == id).findFirst().map(p -> {
								p.name = defaultIfBlank(personInput.name, p.name);
								p.age = defaultIfNull(personInput.age, p.age);
								return p;
							}).orElse(null);
						}) //
				) //
				.build();
		var reader = new InputStreamReader(this.getClass().getResourceAsStream("/demoSchema.graphqls"), Charset.forName("utf8"));
		var typeRegistry = new SchemaParser().parse(reader);
		var schemaGenerator = new SchemaGenerator();
		var graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
		var graphQL = GraphQL.newGraphQL(graphQLSchema).build();
		return graphQL;
	}

	public Map<String, Object> execToSpecification(final String query, final String operationName, final Object context, final Object root,
			final Map<String, Object> variables) {
		var input = new ExecutionInput(query, operationName, context, root, variables);
		var result = graphQL.execute(input);
		return result.toSpecification();
	}
}

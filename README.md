# Vert.x + GraphQL Demo

- clone repo
- run `docker-compose up --build`
- browser to http://localhost:8181/

**Examples**

```
query q1 {
  persons {
    id
    name
    age
  }
}

mutation m1 {
  create(person: {name: "a b", age: 43}) {
    id
    name
    age
  }
}

mutation m2 {
  update(id: 3, person: {name: "Mr X"}) {
    id
    name
    age
  }
}
```

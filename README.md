
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

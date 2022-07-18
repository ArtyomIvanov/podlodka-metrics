import http from "k6/http";

let body = open("./loadRequest.json");

export let options = {
  vus: 5,
  stages: [
    { duration: "1800s", target: 5 }
  ]
};

export default function() {
  let res = http.get(
      "http://localhost:8080/metrics/task",
      body,
      { headers: { "Content-Type": "application/json" }}
  );
};

import http from 'k6/http';
import { check } from 'k6';


export default function () {
    let endpoints = [
        "/sortedByName",
        "/sortedById",
        "/sortedBySalary",
        "/sortedByDesignation",
        "/orderByName",
        "/orderBySalary"
    ];

    for (let i = 0; i < endpoints.length; i++) {
        let res = http.get("https://10.50.16.16:8444/api/employees" + endpoints[i]);
         let checkResult = check(res, {
            'status is 200': (r) => r.status === 200,
            'protocol is HTTP/2': (r) => r.proto === 'HTTP/2.0'

        });

        if (checkResult) {
            let responseBody = JSON.parse(res.body);
            check(responseBody, {
                'response is an array of 1000 items': (body) => Array.isArray(body) && body.length === 1000
            });
        }
    }
}

#!/usr/bin/env bash
set -e

echo "Testing secure endpoints without authentication..."
seq 1 3 | xargs -n1 -P3 -I{} curl -i http://localhost:8080/secure/1
curl -i http://localhost:8080/secure/2

echo -e "\nTesting secure endpoints with incorrect basic authentication..."
curl -i -u chester:incorrect-password http://localhost:8080/secure/1
seq 1 2 | xargs -n1 -P2 -I{} curl -i -u harold:incorrect-password http://localhost:8080/secure/1
seq 1 4 | xargs -n1 -P2 -I{} curl -i -u chester:incorrect-password http://localhost:8080/secure/2

echo -e "\nTesting secure endpoints with basic authentication..."
seq 1 2 | xargs -n1 -P2 -I{} curl -i -u chester:hardcoded-insecure-password http://localhost:8080/secure/1
curl -i -u harold:hardcoded-insecure-password2 http://localhost:8080/secure/1
curl -i -u chester:hardcoded-insecure-password http://localhost:8080/secure/2

echo "Testing insecure endpoint without authentication..."
seq 1 7 | xargs -n1 -P2 -I{} curl -i http://localhost:8080/insecure
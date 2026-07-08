#!/bin/bash
# Keycloak 25 exposes management/health on port 9000 (not 8080).
# The stock image has bash but no curl/wget, so we use a TCP HTTP probe.
exec 3<>/dev/tcp/127.0.0.1/9000 || exit 1
printf 'GET /health/ready HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n' >&3
response=$(cat <&3)
echo "$response" | grep -qE '"status"[[:space:]]*:[[:space:]]*"UP"' && exit 0
echo "$response" | grep -q '200 OK' && exit 0
exit 1

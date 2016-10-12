curl -X POST -H "Content-Type: application/json" -H "Cache-Control: no-cache"  -d '{
  "isBlackoutEnabled": true
}' "http://localhost:8080/rest/apmax/blackoutConfig"
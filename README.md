# Apps

* Keycloak http://localhost:9990/auth
* PostgreSQL - running on port 5432 (but you don't have to do anything with it)
* Backend
  * first-service - http://localhost:8081/** 
  * second-service - http://localhost:8082/**
* Frontend
    * http://localhost:4200
    * http://localhost:4201

# Keycloak setup

1. Go to `./docker` directory
2. Use docker-compose to start Keycloak&PostgreSQL , e.g. `docker-compose up`
3. Schema in the database will be created automatically
4. Keycloak will be available under http://localhost:9990/auth
5. Sign in using admin/admin credentials
6. Create a new realm
   1. name - altimeet
   2. resource file - select a file from `./keycloak/realm-altimeet.json`
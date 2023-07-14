# An Auth API with Spring Security and JWT

This project is a REST API for user authentication and authorization that provides a set of endpoints to manage user registration, login, and user-related operations. It was built following SOLID principles and Clean Architecture, and using Java, Spring Web and Spring Security.

## Endpoints

| Endpoint              | Method | Protected | Description                              |
|-----------------------| ------ |-----------|------------------------------------------|
| `/api/v1/users/new`   | POST   | NO        | Create an user                           |
| `/api/v1/users/login` | POST   | NO        | Authenticate user and return a JWT token |
| `/api/v1/users`       | GET    | YES       | Return user data                         |
| `/api/v1/users`       | PUT    | YES       | Update user data                         |
| `/api/v1/users`       | DELETE | YES       | Delete user                              |
| `/api/v1/swagger-ui/` | GET    | NO        | API Documentation                        |

## Security

- After login, the user authentication and authorization is handled using JWT (JSON Web Token).
- The user's ID is embedded within the JWT token, allowing identification of the user accessing the API.
- RSA encryption is used for JWT token generation, this enables other systems to verify the authenticity and integrity of the JWT tokens only by using a public key.

## Requirements

To run this program, you will need:

- Docker
- Docker Compose

## Installation

The docker images of this project can be found on [Docker Hub](https://hub.docker.com/repository/docker/sesaquecruz/java-auth-api).

1. Clone this repository:

```
git clone https://github.com/sesaquecruz/java-auth-api
```

2. Enter the project directory:

```
cd java-auth-api
```

3. Start the MySQL container:

```
docker compose --profile db up -d
```

4. Run the migrations:

```
./gradlew flywayMigrate
```

5. Build the API jar:

```
./gradlew bootJar
```

6. Start the API container:

```
docker compose --profile app up -d --build
```

7. To stop all containers, use:

```
docker compose --profile db --profile app down
```

The MySQL container can take some seconds to start up.

## Usage

### API Documentation

1. Access the Swagger UI:

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

To access protected endpoints, a valid JWT token is required, which can be obtained upon successful user login.

## Troubleshooting

See [docker-compose.yml](./docker-compose.yml) to verify or change services, ports, and environment variables.

## Contributing

Contributions are welcome! If you find a bug or would like to suggest an enhancement, please make a fork, create a new branch with the bugfix or feature, and submit a pull request.

This project follows the [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) and adheres to [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). For CI/CD, it has [GitHub Actions](https://github.com/features/actions) to run [tests](.github/workflows/ci.yml), determine the version, and [deploy](.github/workflows/ci-cd.yml) a docker image before merging into some branches.

## License

This project is licensed under the MIT License. See [LICENSE](./LICENSE) file for more information.
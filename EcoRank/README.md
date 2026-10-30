# EcoRank – API Restful (Spring Boot)

API de ranking de ações sustentáveis. Projeto da entrega final de Domain Driven Design using Java (FIAP).

## Tecnologias
Java 21, Spring Boot 4.1.0, Maven, JDBC (DataSource), Oracle, Docker, Render.

## Estrutura (pacote `br.com.ecorank.ecorank`)
- `model` – entidades (Usuario, AcaoSustentavel, Missao, Conquista, Nivel, Status)
- `repository` – DAO com JDBC/DataSource
- `service` – BO: regras de negócio e validações
- `resource` – endpoints REST (`@RestController`)
- `dto` / `mapper` – records DTO e conversão DTO ↔ entidade
- `exception` – exceções do sistema
- `config` – `CorsFilter`

## Como rodar
1. Execute `script_banco.sql` no Oracle (SQL Developer).
2. Confira `src/main/resources/application.properties` (ou use as variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`).
3. Rode a classe `EcorankApplication` (IntelliJ) ou:
   ```
   mvn clean package -DskipTests
   java -jar target/ecorank-0.0.1-SNAPSHOT.jar
   ```
4. Teste: `http://localhost:8080/api/v1/ranking`

## Endpoints
`/api/v1/usuarios`, `/api/v1/acoes`, `/api/v1/missoes`, `/api/v1/conquistas`, `/api/v1/niveis` (GET, GET /{id}, POST, PUT /{id}, DELETE /{id};
acoes, missoes e conquistas também têm GET /usuario/{idUsuario}) e `GET /api/v1/ranking`.
A tabela completa está na documentação PDF.

## Deploy no Render
Web Service > Language **Docker** > Dockerfile Path `./Dockerfile` > variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`.

# API REST - Gestão de Correntistas

## 1. Visão Geral

### Objetivo
API REST para gerenciamento (CRUD) de dados de correntistas de uma instituição financeira, desenvolvida como parte do desafio técnico BV.

### Funcionalidades Implementadas
- **Correntistas**: CRUD completo (listar, buscar, cadastrar, atualizar, excluir)
- **Contas**: CRUD completo (cadastrar, atualizar dados, encerrar conta com soft delete)
- **Relacionamento bidirecional**: Correntista ↔ Conta com cascade delete

### Regras de Negócio
1. O identificador único (CPF/CNPJ/PASSAPORTE/RG) não pode ser duplicado
2. O campo "Data de Cadastro" é imutável após criação
3. Todos os dados obrigatórios devem ser preenchidos
4. Validação de campos nulos, vazios e formatos inválidos

---

## 2. Requisitos e Execução

### Versões Necessárias
- Java 21 (JDK)
- Maven 3.9.5+
- Spring Boot 3.5.4

### Comandos

```bash
# Compilar o projeto
./mvnw clean compile

# Executar testes
./mvnw clean test

# Compilar e executar testes
./mvnw clean verify

# Executar a aplicação
./mvnw spring-boot:run
```

### Executar com Docker

#### Pré-requisitos
- Docker 20.10+
- Docker Compose v2+

#### Comandos Docker

```bash
# Build e executar
docker compose up --build

# Executar em background
docker compose up -d

# Parar containers
docker compose down

# Ver logs
docker compose logs -f correntista-api
```

A API ficará disponível em: `http://localhost:8080`

### Exemplos de Chamadas

#### Listar todos os correntistas
```bash
GET http://localhost:8080/api/correntistas
```

#### Buscar correntista por identificador (CPF)
```bash
GET http://localhost:8080/api/correntistas/12345678900
```

#### Cadastrar novo correntista
```bash
POST http://localhost:8080/api/correntistas
Content-Type: application/json

{
    "nomeCompleto": "Maria Silva",
    "endereco": {
        "logradouro": "Rua das Flores",
        "numero": "123",
        "bairro": "Centro",
        "cidade": "São Paulo",
        "estado": "SP",
        "cep": "01234567"
    },
    "tipoIdentificador": "CPF",
    "numeroIdentificador": "12345678900"
}
```

#### Atualizar correntista
```bash
PUT http://localhost:8080/api/correntistas/1
Content-Type: application/json

{
    "nomeCompleto": "Maria Silva Santos",
    "endereco": {
        "logradouro": "Av. Paulista",
        "numero": "1000",
        "bairro": "Bela Vista",
        "cidade": "São Paulo",
        "estado": "SP",
        "cep": "01310100"
    },
    "tipoIdentificador": "CPF",
    "numeroIdentificador": "12345678900"
}
```

#### Excluir correntista
```bash
DELETE http://localhost:8080/api/correntistas/1
```

### Endpoints de Contas

#### Cadastrar nova conta
```bash
POST http://localhost:8080/api/contas
Content-Type: application/json

{
    "correntistaId": 1,
    "numero": "456789",
    "agencia": 1234,
    "tipo": "CORRENTE",
    "saldo": 5000.00,
    "status": "ATIVA"
}
```

#### Atualizar dados da conta
```bash
PUT http://localhost:8080/api/contas/1
Content-Type: application/json

{
    "saldo": 7500.00,
    "agencia": 5678
}
```

#### Encerrar conta (soft delete)
```bash
DELETE http://localhost:8080/api/contas/1
```
> O status da conta é alterado para ENCERRADA. A exclusão física ocorre apenas quando o correntista é removido.

### Documentação Swagger/OpenAPI
Acesse: http://localhost:8080/swagger-ui.html

---

## 3. Decisões Técnicas

### Organização do Projeto
```
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
src/main/java/com/bv/geciara/
├── config/          # Configurações globais
│   ├── ApiExceptionHandler.java
│   ├── JpaConfig.java
│   ├── OpenApiConfig.java
│   └── TimeZoneConfig.java
├── controller/      # Endpoints REST
│   ├── ContaController.java
│   └── CorrentistaController.java
├── dto/             # Data Transfer Objects
│   ├── request/
│   │   ├── ContaAtualizacaoRequest.java
│   │   ├── ContaRequest.java
│   │   ├── CorrentistaAtualizacaoRequest.java
│   │   └── CorrentistaRequest.java
│   └── response/
│       ├── ContaResponse.java
│       ├── CorrentistaResumoResponse.java
│       └── CorrentistaResponse.java
├── exception/       # Exceções customizadas
│   ├── ContaNaoEncontradaException.java
│   ├── CorrentistaNaoEncontradoException.java
│   └── IdentificadorDuplicadoException.java
├── mapper/          # Mappers entre DTOs e Entidades
│   ├── ContaMapper.java
│   └── CorrentistaMapper.java
│   ├── ContaMapper.java
│   └── CorrentistaMapper.java
├── model/           # Modelo de domínio
│   ├── EntidadeAuditavel.java
│   ├── entities/
│   │   ├── Conta.java
│   │   ├── Correntista.java
│   │   └── Endereco.java
│   └── enums/
│       ├── EStatusConta.java
│       ├── ETipoConta.java
│       └── ETipoIdentificador.java
├── repository/      # Acesso a dados
│   ├── ContaRepository.java
│   └── CorrentistaRepository.java
├── service/         # Lógica de negócio
│   ├── ContaService.java
│   └── CorrentistaService.java
└── util/            # Utilitários
    └── SanitizacaoUtil.java
src/main/resources/
├── application.properties
├── application-docker.properties
└── teste-tecnico-BV.md
```

### Responsabilidade das Principais Classes

| Classe | Responsabilidade |
|--------|-----------------|
| **CorrentistaController** | Expõe endpoints REST de correntistas, valida entrada e delega para o Service |
| **ContaController** | Expõe endpoints REST de contas vinculadas a correntistas |
| **CorrentistaService** | Contém regras de negócio, orquestra operações de CRUD de correntistas |
| **ContaService** | Contém regras de negócio para criação, atualização e encerramento de contas |
| **CorrentistaRepository** | Acesso a dados de correntistas via Spring Data JPA |
| **ContaRepository** | Acesso a dados de contas via Spring Data JPA |
| **CorrentistaMapper** | Converte entre DTOs e entidades de correntista |
| **ContaMapper** | Converte entre DTOs e entidades de conta |
| **SanitizacaoUtil** | Remove caracteres especiais de documentos e CEP |
| **ApiExceptionHandler** | Trata exceções globalmente e retorna respostas padronizadas |

### Estratégia de Persistência
- **Banco**: H2 (em memória) - ideal para demonstração e testes
- **ORM**: Spring Data JPA com Hibernate
- **Auditoria**: Campo `dataCadastro` (imutável) e `dataAtualizacao` via `@CreatedDate` e `@LastModifiedDate`
- **Docker**: Perfil `docker` configura H2 em memória com console desabilitado

### Estratégia de Validação e Tratamento de Erros
- **Bean Validation**: `@NotBlank`, `@NotNull`, `@Size` nos DTOs
- **ControllerAdvice**: `ApiExceptionHandler` centraliza tratamento de exceções
- **Exceções customizadas**: `CorrentistaNaoEncontradoException`, `ContaNaoEncontradaException`, `IdentificadorDuplicadoException`
- **Docker**: Perfil isolado com configurações otimizadas para container

---

## 4. Testes

### Cenários Cobertos

#### Testes Unitários (CorrentistaServiceTest)
1. Inserção de correntista com dados válidos
2. Rejeição de cadastro sem dados obrigatórios
3. Tratamento de identificador duplicado
4. Edição de dados com sucesso
5. Bloqueio ao tentar alterar "Data de Cadastro"
6. Consulta de correntista inexistente
7. Exclusão de correntista

#### Testes de Controller (ContaControllerTest)
1. Cadastrar conta com sucesso (201)
2. Cadastrar conta com correntista inexistente (404)
3. Cadastrar conta com dados inválidos (400)
4. Atualizar conta com sucesso (200)
5. Atualizar conta inexistente (404)
6. Encerrar conta com sucesso (204)
7. Encerrar conta inexistente (404)

#### Testes Unitários (ContaServiceTest)
1. Cadastrar conta com dados válidos
2. Status padrão ATIVA quando não informado
3. Exceção quando correntista não encontrado
4. Atualizar conta com sucesso
5. Exceção quando conta não encontrada na atualização
6. Encerrar conta (soft delete)
7. Exceção quando conta não encontrada no encerramento
8. Dados originais mantidos ao encerrar

#### Testes de Integração (CorrentistaIntegrationTest)
1. Fluxo completo CRUD
2. Rejeição com dados obrigatórios faltando
3. Rejeição com identificador duplicado
4. Busca de correntista inexistente
5. Exclusão de correntista inexistente

#### Testes de Integração (ContaIntegrationTest)
1. Cadastrar conta com sucesso
2. Status padrão ATIVA quando não informado
3. Atualizar conta com sucesso
4. Encerrar conta (soft delete)
5. Encerrar conta inexistente (404)

### Como Executar os Testes
```bash
# Executar todos os testes
./mvnw clean test

# Executar testes com relatório de cobertura
./mvnw clean verify
```

---

## 5. Uso de Ferramentas de IA

### Utilização
- Sim, utilizei IA durante o desenvolvimento, através do OpenCode, um aplicativo open source que acessa diversos tipos de agentes

### Atividades em que foi Utilizada
- Geração inicial de código das classes de configuração
- Criação de testes unitários e de integração
- Sugestões de tratamento de exceções para garantir os retornos adequados
- Verificação de atendimento às exigências do escopo
- Documentação (anotações Swagger)
- Revisão do README para cobrir todo o escopo
- Geração de fluxograma

### Validação das Sugestões
- Todas as sugestões foram revisadas e adaptadas ao contexto do projeto: api REST de cadastro de correntista
- Validei a lógica de negócio implementada, conforme escopo
- Validei a lógica e cobertura dos testes gerados
- Validei as informações e formatação da documentação gerada (Swagger)
- Testei todos os cenários manualmente (via Postman) e via testes automatizados

### Sugestões Alteradas ou Descartadas
Abaixo são descritas algumas:
- Uso de @Embedable na conta - mantida a entidade da conta para chegar mais próximo a um "caso real"
- Descartado o relacionamento "@OneToOne" entre correntista e conta, considerando a possibilidade de ter conta corrente e poupança, por exemplo
- Uso de MapStruct descartado - desnecessário para o projeto nesse momento / maior controle de dados expostos 
- Uso de Banco de dados Postgres - H2 é suficiente para este escopo
- Formatação de dados na documentação - inserido detalhamento de tipos e exemplos mais pŕoximos do real. Entendimento da necessidade devido à experiência em consumo de apis

---

## 6. Evidência da Execução dos Testes

```
[INFO] Tests run: 47, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Todos os 47 testes foram executados com sucesso:
- 1 teste de contexto (GeciaraApplicationTests)
- 11 testes unitários de service (CorrentistaServiceTest)
- 8 testes unitários de service (ContaServiceTest)
- 10 testes de controller (CorrentistaControllerTest)
- 7 testes de controller (ContaControllerTest)
- 5 testes de integração (CorrentistaIntegrationTest)
- 5 testes de integração (ContaIntegrationTest)

# API REST - Gestão de Correntistas

## 1. Visão Geral

### Objetivo
API REST para gerenciamento (CRUD) de dados de correntistas de uma instituição financeira, desenvolvida como parte do desafio técnico BV.

### Funcionalidades Implementadas
- **Correntistas**: CRUD completo (listar, buscar, cadastrar, atualizar, excluir)
- **Contas**: CRUD completo (listar, cadastrar, atualizar dados, encerrar conta com soft delete)
- **Relacionamento bidirecional**: Correntista ↔ Conta com cascade delete
- **Segurança**: HTTP Basic Auth (admin/admin123) em todos os endpoints, exceto Swagger

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

#### Buscar correntista por identificador
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
        "uf": "SP",
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
        "uf": "SP",
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

#### Listar todas as contas
```bash
GET http://localhost:8080/api/contas
```

#### Cadastrar nova conta
```bash
POST http://localhost:8080/api/contas
Content-Type: application/json

{
    "correntistaId": 1,
    "numero": "456789",
    "agencia": 1234,
    "tipo": "CORRENTE"
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

### Diagramas do Projeto

O arquivo `fluxograma.md` contém 14 diagramas Mermaid com os fluxos completos do projeto (arquitetura, ciclo de vida, cadastro, atualização, exclusão, listagem, tratamento de exceções, validação de identificadores, segurança, diagrama de classes e fluxo de DTOs).

Para visualizar os diagramas no IntelliJ IDEA, instale o plugin **Mermaid**:

```
Settings → Plugins → Marketplace → buscar "Mermaid"
→ Instalar "Mermaid" ou "Markdown Mermaid"
→ Reiniciar o IntelliJ
```

Abra `fluxograma.md` e clique no ícone de preview na lateral do editor. Alternativamente, acesse [mermaid.live](https://mermaid.live) para visualização online.

---

## 3. Decisões Técnicas

### Organização do Projeto
```
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── fluxograma.md           # Diagramas Mermaid dos fluxos do projeto
src/main/java/com/bv/geciara/
├── config/          # Configurações globais
│   ├── ApiExceptionHandler.java
│   ├── JpaConfig.java
│   ├── OpenApiConfig.java
│   ├── SecurityConfig.java
│   └── TimeZoneConfig.java
├── controller/      # Endpoints REST
│   ├── ContaController.java
│   └── CorrentistaController.java
├── dto/             # Data Transfer Objects
│   ├── request/
│   │   ├── ContaAtualizacaoRequest.java
│   │   ├── ContaRequest.java
│   │   ├── CorrentistaAtualizacaoRequest.java
│   │   ├── CorrentistaRequest.java
│   │   ├── EnderecoAtualizacaoRequest.java
│   │   └── EnderecoRequest.java
│   └── response/
│       ├── ContaResponse.java
│       ├── CorrentistaResumoResponse.java
│       └── CorrentistaResponse.java
├── exception/       # Exceções customizadas
│   ├── ContaNaoEncontradaException.java
│   ├── CorrentistaNaoEncontradoException.java
│   ├── IdentificadorDuplicadoException.java
│   └── IdentificadorInvalidoException.java
├── mapper/          # Mappers entre DTOs e Entidades
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
    ├── AtLeastOneNonNullField.java
    ├── AtLeastOneNonNullFieldValidator.java
    └── ValidacaoUtil.java
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
| **ValidacaoUtil** | Valida formato de identificadores (CPF, CNPJ, Passaporte, RG) |
| **AtLeastOneNonNullField** | Valida que ao menos um campo seja informado em requisições de atualização |
| **SecurityConfig** | Configura HTTP Basic Auth e permissões de acesso |
| **ApiExceptionHandler** | Trata exceções globalmente e retorna respostas padronizadas |

### Estratégia de Persistência
- **Banco**: H2 (em memória) - ideal para demonstração e testes
- **ORM**: Spring Data JPA com Hibernate
- **Auditoria**: Campo `dataCadastro` (imutável) e `dataAtualizacao` via `@CreatedDate` e `@LastModifiedDate`
- **Docker**: Perfil `docker` configura H2 em memória com console desabilitado

### Estratégia de Validação e Tratamento de Erros
- **Bean Validation**: `@NotBlank`, `@NotNull`, `@Size` nos DTOs
- **ControllerAdvice**: `ApiExceptionHandler` centraliza tratamento de exceções
- **Exceções customizadas**: `CorrentistaNaoEncontradoException`, `ContaNaoEncontradaException`, `IdentificadorDuplicadoException`, `IdentificadorInvalidoException`
- **Segurança**: HTTP Basic Auth configurado em `SecurityConfig`, com permissão exclusiva para Swagger

---

## 4. Testes

### Cenários Cobertos

#### Testes Unitários (CorrentistaServiceTest) — 16 testes
1. Listar correntistas com dados
2. Listar correntistas vazia
3. Listar completos com dados
4. Listar completos vazia
5. Buscar por identificador com sucesso
6. Buscar por identificador não encontrado
7. Cadastrar com dados válidos
8. Cadastrar com identificador duplicado
9. Cadastrar chama exists com identificador do request
10. Atualizar com sucesso
11. Atualizar correntista não encontrado
12. Atualizar com mesmo identificador (sem duplicidade)
13. Atualizar com novo identificador duplicado
14. Manter dataCadastro ao atualizar
15. Excluir com sucesso
16. Excluir correntista não encontrado

#### Testes Unitários (ContaServiceTest) — 14 testes
1. Listar contas com dados
2. Listar contas vazia
3. Cadastrar conta com sucesso
4. Status padrão ATIVA quando não informado
5. Manter status informado quando fornecido
6. Exceção quando correntista não encontrado
7. Atualizar com sucesso (saldo)
8. Atualizar múltiplos campos
9. Atualizar apenas um campo
10. Manter campos não informados na atualização
11. Exceção quando conta não encontrada
12. Encerrar conta (soft delete)
13. Dados originais mantidos ao encerrar
14. Exceção quando conta não encontrada no encerramento

#### Testes de Controller (CorrentistaControllerTest) — 16 testes
1. Listar correntistas (200)
2. Listar correntistas vazia (200)
3. Listar completos (200)
4. Buscar por identificador (200)
5. Buscar por identificador não encontrado (404)
6. Cadastrar com sucesso (201)
7. Cadastrar identificador duplicado (409)
8. Cadastrar dados inválidos (400) — com validação de detalhes
9. Cadastrar endereço nulo (400)
10. Cadastrar campo desconhecido (400)
11. Cadastrar tipo identificador inválido (400)
12. Atualizar com sucesso (200)
13. Atualizar não encontrado (404)
14. Atualizar body vazio (400)
15. Excluir com sucesso (204)
16. Excluir não encontrado (404)

#### Testes de Controller (ContaControllerTest) — 14 testes
1. Listar contas (200)
2. Listar contas vazia (200)
3. Cadastrar conta com sucesso (201)
4. Cadastrar com correntista inexistente (404)
5. Cadastrar com dados inválidos (400) — com validação de detalhes
6. Cadastrar com tipo inválido (400)
7. Cadastrar com campo desconhecido (400)
8. Cadastrar com body vazio (400)
9. Atualizar conta com sucesso (200)
10. Atualizar conta inexistente (404)
11. Atualizar body vazio (400)
12. Atualizar campo desconhecido (400)
13. Encerrar conta com sucesso (204)
14. Encerrar conta inexistente (404)

#### Testes de Mapper (CorrentistaMapperTest) — 12 testes
1. toEntity converte todos os campos (incluindo complemento)
2. toEntity aceita endereço nulo
3. toResumoResponse converte campos corretos
4. toResponse converte todos os campos com contas
5. toResponse retorna lista vazia quando contas null
6. toResponse retorna lista vazia quando contas vazia
7. updateEntity atualiza apenas nome
8. updateEntity atualiza endereço parcial
9. updateEntity cria endereço quando não existe
10. updateEntity atualiza identificador
11. updateEntity lança exceção para identificador inválido
12. updateEntity mantém campos não informados

#### Testes de Mapper (ContaMapperTest) — 6 testes
1. toEntity converte todos os campos
2. toEntity vincula correntista
3. toEntity define status padrão ATIVA
4. toEntity define saldo zero
5. toResponse converte todos os campos
6. toResponse extrai correntistaId corretamente

#### Testes de Validação (ValidacaoUtilTest) — 12 testes
1. CPF válido
2. CPF com tamanho inválido
3. CPF com letras
4. Null, vazio e espaços (parameterized)
5. Tipo nulo
6. CNPJ válido
7. CNPJ com tamanho inválido
8. Passaporte válido
9. RG válido

#### Testes de Segurança (SecurityIntegrationTest) — 7 testes
1. Rejeita GET sem autenticacao (401)
2. Rejeita credenciais invalidas (401)
3. Rejeita credenciais vazias (401)
4. Aceita credenciais validas (200)
5. Rejeita POST sem autenticacao (401)
6. Aceita actuator health com autenticacao (200)
7. Rejeita POST contas sem autenticacao (401)

#### Testes de Integração (CorrentistaIntegrationTest) — 9 testes
1. Fluxo completo CRUD
2. Rejeição com dados obrigatórios faltando
3. Rejeição com identificador duplicado
4. Busca de correntista inexistente
5. Exclusão de correntista inexistente
6. Rejeição com tipo identificador inválido
7. Cadastro com complemento opcional
8. Atualização de endereço parcial
9. Rejeição de body vazio

#### Testes de Integração (ContaIntegrationTest) — 15 testes
1. Listar contas com dados
2. Listar contas vazia
3. Cadastrar conta com sucesso
4. Status padrão ATIVA quando não informado
5. Saldo inicializado em zero
6. Correntista inexistente (404)
7. Dados inválidos (400)
8. Tipo inválido (400)
9. Body vazio (400)
10. Atualizar conta com sucesso
11. Rejeição de body vazio
12. Manter campos não informados na atualização
13. Conta inexistente na atualização (404)
14. Encerrar conta (soft delete)
15. Encerrar conta inexistente (404)

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
- Criação e revisão de testes unitários, de integração e de segurança
- Sugestões de tratamento de exceções para garantir os retornos adequados
- Verificação de atendimento às exigências do escopo
- Documentação (anotações Swagger)
- Revisão e atualização do README para cobrir todo o escopo
- Geração e atualização de fluxograma
- Geração e atualização de collection para Postman em json
- Correção de bugs (mapper retornava null com endereco nulo)
- Adição de testes de segurança (validação HTTP Basic Auth)
- Cobertura para dados inválidos, campos faltantes e consistência de retornos

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
[INFO] Tests run: 122, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Todos os 122 testes foram executados com sucesso:
- 1 teste de contexto (GeciaraApplicationTests)
- 16 testes unitários de service (CorrentistaServiceTest)
- 14 testes unitários de service (ContaServiceTest)
- 16 testes de controller (CorrentistaControllerTest)
- 14 testes de controller (ContaControllerTest)
- 12 testes de mapper (CorrentistaMapperTest)
- 6 testes de mapper (ContaMapperTest)
- 12 testes de validação (ValidacaoUtilTest)
- 7 testes de segurança (SecurityIntegrationTest)
- 9 testes de integração (CorrentistaIntegrationTest)
- 15 testes de integração (ContaIntegrationTest)

## 7. Implementações futuras
- Cadastro de documentação completa (RG, CPF/CNPJ, inscrições estadual/municipal)
- Cadastro de credenciais do usuário (autenticação da conta)
- Desenvolvimento de frontend
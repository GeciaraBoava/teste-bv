# Fluxogramas do Projeto

## 1. Arquitetura em Camadas

```mermaid
graph TB
    subgraph "Camada de Apresentação"
        CLIENT[Cliente HTTP<br/>Postman / Swagger UI / Frontend]
    end

    subgraph "Camada de Controller"
        CC[CorrentistaController<br/>/api/correntistas]
        CT[ContaController<br/>/api/contas]
    end

    subgraph "Camada de Validação"
        BV[Bean Validation<br/>@NotBlank, @NotNull, @Pattern]
        EXH[ApiExceptionHandler<br/>@RestControllerAdvice]
    end

    subgraph "Camada de Service"
        CS[CorrentistaService<br/>Regras de negócio]
        CTS[ContaService<br/>Regras de negócio]
    end

    subgraph "Camada de Mapper"
        CM[CorrentistaMapper<br/>DTO ↔ Entidade]
        CTM[ContaMapper<br/>DTO ↔ Entidade]
    end

    subgraph "Camada de Repository"
        CR[CorrentistaRepository<br/>Spring Data JPA]
        CTR[ContaRepository<br/>Spring Data JPA]
    end

    subgraph "Banco de Dados"
        H2[(H2 In-Memory<br/>geciaradb)]
    end

    subgraph "Utilitários"
        SU[SAnitizacaoUtil<br/>Remoção de caracteres especiais]
    end

    CLIENT --> CC
    CLIENT --> CT
    CC --> BV
    CT --> BV
    BV -->|Válido| CS
    BV -->|Válido| CTS
    BV -->|Inválido| EXH
    CS --> CM
    CTS --> CTM
    CS --> SU
    CM --> CR
    CTM --> CTR
    CR --> H2
    CTR --> H2
    CR --> CM
    CTR --> CTM
    CM --> CC
    CTM --> CT
    CC --> CLIENT
    CT --> CLIENT
    CS -->|Exceção| EXH
    CTS -->|Exceção| EXH
    EXH --> CLIENT
```

---

## 2. Ciclo de Vida de uma Requisição

```mermaid
flowchart TD
    START([Requisição HTTP]) --> VAL{Bean Validation<br/>válido?}
    VAL -->|Não| EXH[ApiExceptionHandler<br/>monta resposta 400]
    VAL -->|Sim| SVC[Service recebe Request DTO]

    SVC --> BIZ{Regra de negócio<br/>válida?}
    BIZ -->|Não| EXC[Exceção customizada<br/>404 / 409]
    BIZ -->|Sim| MAP[Mapper converte<br/>DTO → Entidade]

    MAP --> DB[Repository persiste<br/>no banco H2]
    DB --> RSP[Mapper converte<br/>Entidade → Response DTO]
    RSP --> HTTP[Resposta HTTP<br/>201 / 200 / 204]

    EXC --> EXH2[ApiExceptionHandler<br/>monta resposta de erro]
    EXH --> CLIENT[Cliente recebe JSON]
    EXH2 --> CLIENT
    HTTP --> CLIENT

    style VAL fill:#fff3cd
    style BIZ fill:#fff3cd
    style EXC fill:#f8d7da
    style EXH fill:#f8d7da
    style EXH2 fill:#f8d7da
    style HTTP fill:#d4edda
    style RSP fill:#d4edda
```

---

## 3. Cadastro de Correntista (POST /api/correntistas)

```mermaid
flowchart TD
    START([POST /api/correntistas<br/>CorrentistaRequest]) --> VALID{Validação<br/>válida?}

    VALID -->|Inválido| ERR400[400 Bad Request]
    VALID -->|Válido| SAN[Sanitiza número e CEP]

    SAN --> DUP{Identificador<br/>já existe?}

    DUP -->|Sim| ERR409[409 Conflict]
    DUP -->|Não| MAP[Mapper converte<br/>Request → Entity]

    MAP --> SAVE[Repository.save]
    SAVE --> RSP[Mapper converte<br/>Entity → Response]
    RSP --> OK([201 Created])

    ERR400 --> HANDLER[ApiExceptionHandler]
    ERR409 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR400 fill:#f8d7da
    style ERR409 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 4. Atualização de Correntista (PUT /api/correntistas/{id})

```mermaid
flowchart TD
    START([PUT /api/correntistas/{id}<br/>CorrentistaAtualizacaoRequest]) --> FIND{Correntista<br/>existe?}

    FIND -->|Não| ERR404[404 Not Found]
    FIND -->|Sim| UPD[Atualiza apenas<br/>campos enviados]

    UPD --> SAVE[Repository.save]
    SAVE --> RSP[Mapper converte<br/>Entity → Response]
    RSP --> OK([200 OK])

    ERR404 --> HANDLER[ApiExceptionHandler]
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 5. Exclusão de Correntista (DELETE /api/correntistas/{id})

```mermaid
flowchart TD
    START([DELETE /api/correntistas/{id}]) --> FIND{Correntista<br/>existe?}

    FIND -->|Não| ERR404[404 Not Found]
    FIND -->|Sim| DEL[deleteById<br/>cascade remove contas]

    DEL --> OK([204 No Content])

    ERR404 --> HANDLER[ApiExceptionHandler]
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 6. Cadastro de Conta (POST /api/contas)

```mermaid
flowchart TD
    START([POST /api/contas<br/>ContaRequest]) --> VALID{Validação<br/>válida?}

    VALID -->|Inválido| ERR400[400 Bad Request]
    VALID -->|Válido| FIND_CORR{Correntista<br/>existe?}

    FIND_CORR -->|Não| ERR404[404 Not Found]
    FIND_CORR -->|Sim| MAP[Mapper converte<br/>Request → Entity]

    MAP --> DEF[Status padrão: ATIVA<br/>se não informado]
    DEF --> SAVE[Repository.save<br/>cascade vincula ao Correntista]

    SAVE --> RSP[Mapper converte<br/>Entity → Response]
    RSP --> OK([201 Created])

    ERR400 --> HANDLER[ApiExceptionHandler]
    ERR404 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR400 fill:#f8d7da
    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 7. Atualização de Conta (PUT /api/contas/{id})

```mermaid
flowchart TD
    START([PUT /api/contas/{id}<br/>ContaAtualizacaoRequest]) --> FIND{Conta<br/>existe?}

    FIND -->|Não| ERR404[404 Not Found]
    FIND -->|Sim| UPD[Atualiza apenas<br/>campos enviados]

    UPD --> SAVE[Repository.save]
    SAVE --> RSP[Mapper converte<br/>Entity → Response]
    RSP --> OK([200 OK])

    ERR404 --> HANDLER[ApiExceptionHandler]
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 8. Encerramento de Conta (DELETE /api/contas/{id})

```mermaid
flowchart TD
    START([DELETE /api/contas/{id}]) --> FIND{Conta<br/>existe?}

    FIND -->|Não| ERR404[404 Not Found]
    FIND -->|Sim| SOFT[status = ENCERRADA<br/>soft delete]

    SOFT --> SAVE[Repository.save]
    SAVE --> OK([204 No Content])

    ERR404 --> HANDLER[ApiExceptionHandler]
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
    style SOFT fill:#fff3cd
```

---

## 9. Tratamento de Exceções (ApiExceptionHandler)

```mermaid
flowchart TD
    EXC([Exceção lançada]) --> TYPE{Tipo da exceção}

    TYPE -->|CorrentistaNaoEncontradoException| NF404[404 Not Found]
    TYPE -->|ContaNaoEncontradaException| NF404C[404 Not Found]
    TYPE -->|IdentificadorDuplicadoException| CON409[409 Conflict]
    TYPE -->|DataIntegrityViolationException| CON409D[409 Conflict]
    TYPE -->|MethodArgumentNotValidException| VAL400[400 Bad Request]
    TYPE -->|Exception genérica| ERR500[500 Internal Server Error]

    NF404 --> RESP[Response JSON:<br/>timestamp, status, erro, mensagem]
    NF404C --> RESP
    CON409 --> RESP
    CON409D --> RESP
    VAL400 --> RESP_V[Response JSON:<br/>timestamp, status, erro, detalhes]
    ERR500 --> RESP

    RESP --> CLIENT([Cliente])
    RESP_V --> CLIENT

    style NF404 fill:#fff3cd
    style NF404C fill:#fff3cd
    style CON409 fill:#f8d7da
    style CON409D fill:#f8d7da
    style VAL400 fill:#f8d7da
    style ERR500 fill:#f8d7da
```

---

## 10. Fluxo de Sanitização de Dados

```mermaid
flowchart LR
    subgraph "Entrada do Usuário"
        CPF["CPF: '123.456.789-09'"]
        CEP["CEP: '01310-100'"]
        CNPJ["CNPJ: '12.345.678/0001-90'"]
    end

    subgraph "SanitizacaoUtil"
        S1["Regex: [^a-zA-Z0-9]<br/>remove caracteres especiais"]
    end

    subgraph "Saída Limpa"
        CPF_OK["'12345678909'"]
        CEP_OK["'01310100'"]
        CNPJ_OK["'12345678000190'"]
    end

    CPF -->|sanitizarDocumento| S1
    CEP -->|sanitizarCep| S1
    CNPJ -->|sanitizarDocumento| S1
    S1 --> CPF_OK
    S1 --> CEP_OK
    S1 --> CNPJ_OK

    CPF_OK --> DB[(Banco H2)]
    CEP_OK --> DB
    CNPJ_OK --> DB

    style S1 fill:#e7f3ff
    style DB fill:#d4edda
```

---

## 11. Diagrama de Classes (Relacionamentos)

```mermaid
classDiagram
    class Correntista {
        +Long id
        +String nomeCompleto
        +Endereco endereco
        +ETipoIdentificador tipoIdentificador
        +String numeroIdentificador
        +LocalDateTime dataCadastro
        +LocalDateTime dataAtualizacao
        +adicionarConta(Conta)
        +removerConta(Conta)
    }

    class Conta {
        +Long id
        +String numero
        +Integer agencia
        +ETipoConta tipo
        +BigDecimal saldo
        +EStatusConta status
        +LocalDateTime dataCadastro
        +LocalDateTime dataAtualizacao
    }

    class Endereco {
        +String logradouro
        +String numero
        +String bairro
        +String cidade
        +String estado
        +String cep
    }

    class EntidadeAuditavel {
        <<MappedSuperclass>>
        +LocalDateTime dataCadastro
        +LocalDateTime dataAtualizacao
    }

    Correntista "1" --> "*" Conta : tem muitas
    Correntista *-- "1" Endereco : possui
    Conta --> "1" Correntista : pertence a
    Correntista --|> EntidadeAuditavel : herda
    Conta --|> EntidadeAuditavel : herda
```

---

## 12. Fluxo de DTOs (Request → Entity → Response)

```mermaid
flowchart LR
    subgraph "Correntista"
        CR1[CorrentistaRequest] --> CM[CorrentistaMapper]
        CM --> CE[Correntista Entity]
        CE --> CM2[CorrentistaMapper]
        CM2 --> CR2[CorrentistaResponse]
    end

    subgraph "Conta"
        CT1[ContaRequest] --> CTM[ContaMapper]
        CTM --> CTE[Conta Entity]
        CTE --> CTM2[ContaMapper]
        CTM2 --> CT2[ContaResponse]
    end

    style CR1 fill:#e7f3ff
    style CT1 fill:#e7f3ff
    style CE fill:#fff3cd
    style CTE fill:#fff3cd
    style CR2 fill:#d4edda
    style CT2 fill:#d4edda
```

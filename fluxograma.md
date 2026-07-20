# Fluxogramas do Projeto

## 1. Arquitetura em Camadas

```mermaid
graph TB
    subgraph "Camada de Apresentação"
        CLIENT[Cliente HTTP<br/>Postman / Swagger UI / Frontend]
    end

    subgraph "Segurança"
        SEC[SecurityConfig<br/>HTTP Basic Auth]
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
        VU[ValidacaoUtil<br/>Validação de identificadores]
    end

    CLIENT -->|HTTP Basic Auth| SEC
    SEC -->|Autenticado| CC
    SEC -->|Autenticado| CT
    CC --> BV
    CT --> BV
    BV -->|Válido| CS
    BV -->|Válido| CTS
    BV -->|Inválido| EXH
    CS --> CM
    CTS --> CTM
    CS --> VU
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
    START([Requisição HTTP]) --> SEC{Autenticação<br/>válida?}
    SEC -->|Não| ERR401[401 Unauthorized]
    SEC -->|Sim| VAL{Bean Validation<br/>válido?}
    VAL -->|Não| EXH[ApiExceptionHandler<br/>monta resposta 400]
    VAL -->|Sim| SVC[Service recebe Request DTO]

    SVC --> BIZ{Regra de negócio<br/>válida?}
    BIZ -->|Não| EXC[Exceção customizada<br/>404 / 409]
    BIZ -->|Sim| MAP[Mapper converte<br/>DTO → Entidade]

    MAP --> DB[Repository persiste<br/>no banco H2]
    DB --> RSP[Mapper converte<br/>Entidade → Response DTO]
    RSP --> HTTP[Resposta HTTP<br/>201 / 200 / 204]

    EXC --> EXH2[ApiExceptionHandler<br/>monta resposta de erro]
    ERR401 --> CLIENT[Cliente recebe JSON]
    EXH --> CLIENT
    EXH2 --> CLIENT
    HTTP --> CLIENT

    style SEC fill:#e7f3ff
    style ERR401 fill:#f8d7da
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
    START([POST /api/correntistas<br/>CorrentistaRequest]) --> AUTH{Autenticado?<br/>Basic Auth}
    AUTH -->|Não| ERR401[401 Unauthorized]
    AUTH -->|Sim| VALID{Validação<br/>Bean Validation?}

    VALID -->|Inválido| ERR400[400 Bad Request<br/>detalhes dos campos]
    VALID -->|Válido| DUP{Identificador<br/>já existe?}

    DUP -->|Sim| ERR409[409 Conflict<br/>CPF/CNPJ duplicado]
    DUP -->|Não| MAP[CorrentistaMapper.toEntity<br/>converte DTO → Entity]

    MAP --> SAVE[Repository.save]
    SAVE --> RSP[CorrentistaMapper.toResponse<br/>converte Entity → Response]
    RSP --> OK([201 Created])

    ERR401 --> HANDLER[ApiExceptionHandler]
    ERR400 --> HANDLER
    ERR409 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR401 fill:#f8d7da
    style ERR400 fill:#f8d7da
    style ERR409 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 4. Atualização de Correntista (PUT /api/correntistas/{id})

```mermaid
flowchart TD
    START([PUT /api/correntistas/{id}<br/>CorrentistaAtualizacaoRequest]) --> AUTH{Autenticado?<br/>Basic Auth}
    AUTH -->|Não| ERR401[401 Unauthorized]
    AUTH -->|Sim| FIND{Correntista<br/>existe?}

    FIND -->|Não| ERR404[404 Not Found]
    FIND -->|Sim| DUP{Novo identificador<br/>duplicado?}

    DUP -->|Sim| ERR409[409 Conflict]
    DUP -->|Não| UPD[CorrentistaMapper.updateEntity<br/>atualiza apenas campos enviados]

    UPD --> SAVE[Repository.save]
    SAVE --> RSP[CorrentistaMapper.toResponse<br/>converte Entity → Response]
    RSP --> OK([200 OK])

    ERR401 --> HANDLER[ApiExceptionHandler]
    ERR404 --> HANDLER
    ERR409 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR401 fill:#f8d7da
    style ERR404 fill:#f8d7da
    style ERR409 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 5. Exclusão de Correntista (DELETE /api/correntistas/{id})

```mermaid
flowchart TD
    START([DELETE /api/correntistas/{id}]) --> AUTH{Autenticado?<br/>Basic Auth}
    AUTH -->|Não| ERR401[401 Unauthorized]
    AUTH -->|Sim| FIND{Correntista<br/>existe?}

    FIND -->|Não| ERR404[404 Not Found]
    FIND -->|Sim| DEL[deleteById<br/>cascade remove contas]

    DEL --> OK([204 No Content])

    ERR401 --> HANDLER[ApiExceptionHandler]
    ERR404 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR401 fill:#f8d7da
    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 6. Listar Contas (GET /api/contas)

```mermaid
flowchart TD
    START([GET /api/contas<br/>page, size, sort]) --> AUTH{Autenticado?<br/>Basic Auth}
    AUTH -->|Não| ERR401[401 Unauthorized]
    AUTH -->|Sim| FIND[Repository.findAll<br/>(Pageable) busca pagina de contas]

    FIND --> MAP[ContaMapper.toResponse<br/>converte cada Entity → Response]
    MAP --> OK([200 OK<br/>Page of ContaResponse<br/>content, page, size, totalElements])

    ERR401 --> HANDLER[ApiExceptionHandler]
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR401 fill:#f8d7da
    style OK fill:#d4edda
    style FIND fill:#e7f3ff
```

---

## 7. Cadastro de Conta (POST /api/contas)

```mermaid
flowchart TD
    START([POST /api/contas<br/>ContaRequest]) --> AUTH{Autenticado?<br/>Basic Auth}
    AUTH -->|Não| ERR401[401 Unauthorized]
    AUTH -->|Sim| VALID{Validação<br/>Bean Validation?}

    VALID -->|Inválido| ERR400[400 Bad Request<br/>detalhes dos campos]
    VALID -->|Válido| FIND_CORR{Correntista<br/>existe?}

    FIND_CORR -->|Não| ERR404[404 Not Found]
    FIND_CORR -->|Sim| MAP[ContaMapper.toEntity<br/>converte DTO → Entity<br/>saldo = ZERO, status = ATIVA]

    MAP --> VINC[Correntista.adicionarConta<br/>vincula bidirecionalmente]

    VINC --> SAVE[Repository.save<br/>cascade vincula ao Correntista]
    SAVE --> RSP[ContaMapper.toResponse<br/>converte Entity → Response]
    RSP --> OK([201 Created])

    ERR401 --> HANDLER[ApiExceptionHandler]
    ERR400 --> HANDLER
    ERR404 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR401 fill:#f8d7da
    style ERR400 fill:#f8d7da
    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
    style MAP fill:#e7f3ff
```

---

## 8. Atualização de Conta (PUT /api/contas/{id})

```mermaid
flowchart TD
    START([PUT /api/contas/{id}<br/>ContaAtualizacaoRequest<br/>todos campos opcionais]) --> AUTH{Autenticado?<br/>Basic Auth}
    AUTH -->|Não| ERR401[401 Unauthorized]
    AUTH -->|Sim| FIND{Conta<br/>existe?}

    FIND -->|Não| ERR404[404 Not Found]
    FIND -->|Sim| UPD[Atualiza apenas<br/>campos enviados não nulos]

    UPD --> SAVE[Repository.save]
    SAVE --> RSP[ContaMapper.toResponse<br/>converte Entity → Response]
    RSP --> OK([200 OK])

    ERR401 --> HANDLER[ApiExceptionHandler]
    ERR404 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR401 fill:#f8d7da
    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 9. Encerramento de Conta (DELETE /api/contas/{id})

```mermaid
flowchart TD
    START([DELETE /api/contas/{id}]) --> AUTH{Autenticado?<br/>Basic Auth}
    AUTH -->|Não| ERR401[401 Unauthorized]
    AUTH -->|Sim| FIND{Conta<br/>existe?}

    FIND -->|Não| ERR404[404 Not Found]
    FIND -->|Sim| SOFT[status = ENCERRADA<br/>soft delete]

    SOFT --> SAVE[Repository.save]
    SAVE --> OK([204 No Content])

    ERR401 --> HANDLER[ApiExceptionHandler]
    ERR404 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe erro])

    style ERR401 fill:#f8d7da
    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
    style SOFT fill:#fff3cd
```

---

## 10. Tratamento de Exceções (ApiExceptionHandler)

```mermaid
flowchart TD
    EXC([Exceção lançada]) --> TYPE{Tipo da exceção}

    TYPE -->|CorrentistaNaoEncontradoException| NF404[404 Not Found]
    TYPE -->|ContaNaoEncontradaException| NF404C[404 Not Found]
    TYPE -->|IdentificadorDuplicadoException| CON409[409 Conflict]
    TYPE -->|IdentificadorInvalidoException| INV400[400 Bad Request]
    TYPE -->|DataIntegrityViolationException| CON409D[409 Conflict]
    TYPE -->|MethodArgumentNotValidException| VAL400[400 Bad Request<br/>detalhes por campo]
    TYPE -->|HttpMessageNotReadableException| MSG400[400 Bad Request<br/>campo inválido/enum]
    TYPE -->|Exception genérica| ERR500[500 Internal Server Error]

    NF404 --> RESP[Response JSON:<br/>timestamp, status, erro, mensagem]
    NF404C --> RESP
    CON409 --> RESP
    INV400 --> RESP
    CON409D --> RESP
    VAL400 --> RESP_V[Response JSON:<br/>timestamp, status, erro, detalhes]
    MSG400 --> RESP
    ERR500 --> RESP

    RESP --> CLIENT([Cliente])
    RESP_V --> CLIENT

    style NF404 fill:#fff3cd
    style NF404C fill:#fff3cd
    style CON409 fill:#f8d7da
    style INV400 fill:#f8d7da
    style CON409D fill:#f8d7da
    style VAL400 fill:#f8d7da
    style MSG400 fill:#f8d7da
    style ERR500 fill:#f8d7da
```

---

## 11. Validação de Identificadores (ValidacaoUtil)

```mermaid
flowchart LR
    subgraph "Entrada"
        CPF["CPF: '12345678909'"]
        CNPJ["CNPJ: '12345678000190'"]
        PASS["Passaporte: 'AB1234567'"]
        RG["RG: '123456789'"]
    end

    subgraph "ValidacaoUtil.isIdentificadorValid"
        V1{"Tipo = CPF?<br/>Tamanho = 11?"}
        V2{"Tipo = CNPJ?<br/>Tamanho = 14?"}
        V3{"Tipo válido?<br/>Não vazio?"}
    end

    subgraph "Resultado"
        OK["true → Prossegue<br/>com cadastro"]
        FAIL["false → IdentificadorInvalidoException<br/>400 Bad Request"]
    end

    CPF --> V1
    CNPJ --> V2
    PASS --> V3
    RG --> V3

    V1 -->|Sim| OK
    V1 -->|Não| FAIL
    V2 -->|Sim| OK
    V2 -->|Não| FAIL
    V3 -->|Sim| OK
    V3 -->|Não| FAIL

    style OK fill:#d4edda
    style FAIL fill:#f8d7da
    style V1 fill:#e7f3ff
    style V2 fill:#e7f3ff
    style V3 fill:#e7f3ff
```

---

## 12. Segurança (HTTP Basic Auth)

```mermaid
flowchart TD
    START([Requisição HTTP]) --> AUTH{Header<br/>Authorization<br/>presente?}

    AUTH -->|Não| ERR401A[401 Unauthorized<br/>WWW-Authenticate: Basic]
    AUTH -->|Sim| DECODE[Decodifica<br/>Base64: user:password]

    DECODE --> MATCH{Credenciais<br/>corretas?<br/>admin/admin123}

    MATCH -->|Não| ERR401B[401 Unauthorized]
    MATCH -->|Sim| PERMIT{Endpoint<br/>permitido sem auth?}

    PERMIT -->|Sim<br/>/swagger-ui/**| PUBLIC[Resposta 200<br/>sem autenticação]
    PERMIT -->|Não| FORWARD[Encaminha para<br/>Controller]

    FORWARD --> CONT[CorrentistaController<br/>ou ContaController]

    ERR401A --> CLIENT([Cliente recebe erro])
    ERR401B --> CLIENT
    PUBLIC --> CLIENT
    CONT --> CLIENT

    style ERR401A fill:#f8d7da
    style ERR401B fill:#f8d7da
    style PERMIT fill:#fff3cd
    style PUBLIC fill:#d4edda
    style CONT fill:#d4edda
```

---

## 13. Diagrama de Classes (Relacionamentos)

```mermaid
classDiagram
    class Correntista {
        +Long id
        +String nomeCompleto
        +Endereco endereco
        +ETipoIdentificador tipoIdentificador
        +String numeroIdentificador
        +List~Conta~ contas
        +LocalDateTime dataCadastro
        +LocalDateTime dataAtualizacao
        +adicionarConta(Conta)
        +removerConta(Conta)
    }

    class Conta {
        +Long id
        +String numero
        +Integer agencia
        +String codigoBanco
        +ETipoConta tipo
        +BigDecimal saldo
        +EStatusConta status
        +Correntista correntista
        +LocalDateTime dataCadastro
        +LocalDateTime dataAtualizacao
    }

    class Endereco {
        +String logradouro
        +String numero
        +String complemento
        +String bairro
        +String cidade
        +String uf
        +String cep
    }

    class EntidadeAuditavel {
        <<MappedSuperclass>>
        +LocalDateTime dataCadastro
        +LocalDateTime dataAtualizacao
    }

    class ETipoIdentificador {
        <<enumeration>>
        CPF
        CNPJ
        PASSAPORTE
        RG
    }

    class ETipoConta {
        <<enumeration>>
        CORRENTE
        POUPANCA
        SALARIO
    }

    class EStatusConta {
        <<enumeration>>
        ATIVA
        BLOQUEADA
        ENCERRADA
    }

    Correntista "1" --> "*" Conta : tem muitas
    Correntista *-- "1" Endereco : possui
    Conta --> "1" Correntista : pertence a
    Correntista --|> EntidadeAuditavel : herda
    Conta --|> EntidadeAuditavel : herda
    Correntista --> ETipoIdentificador : usa
    Conta --> ETipoConta : usa
    Conta --> EStatusConta : usa
```

---

## 14. Fluxo de DTOs (Request record → Entity → Response record)

```mermaid
flowchart LR
    subgraph "Correntista - Criação"
        CR1["CorrentistaRequest (record)<br/>todos campos obrigatórios"] --> CM1[CorrentistaMapper.toEntity]
        CM1 --> CE[Correntista Entity]
    end

    subgraph "Correntista - Atualização"
        CRU["CorrentistaAtualizacaoRequest (record)<br/>todos campos opcionais"] --> CMU[CorrentistaMapper.updateEntity]
        CMU --> CE
    end

    subgraph "Correntista - Resposta"
        CE --> CM2[CorrentistaMapper.toResponse]
        CM2 --> CR2["CorrentistaResponse (record)<br/>com lista de contas"]
    end

    subgraph "Conta - Criação"
        CT1["ContaRequest (record)<br/>correntistaId, numero, agencia, codigoBanco, tipo"] --> CTM1[ContaMapper.toEntity<br/>saldo=ZERO, status=ATIVA]
        CTM1 --> CTE[Conta Entity]
    end

    subgraph "Conta - Atualização"
        CTU["ContaAtualizacaoRequest (record)<br/>todos campos opcionais"] --> CTMU[Service atualiza<br/>campos não nulos]
        CTMU --> CTE
    end

    subgraph "Conta - Resposta"
        CTE --> CTM2[ContaMapper.toResponse]
        CTM2 --> CT2["ContaResponse (record)<br/>com correntistaId"]
    end

    style CR1 fill:#e7f3ff
    style CRU fill:#e7f3ff
    style CT1 fill:#e7f3ff
    style CTU fill:#e7f3ff
    style CE fill:#fff3cd
    style CTE fill:#fff3cd
    style CR2 fill:#d4edda
    style CT2 fill:#d4edda
```

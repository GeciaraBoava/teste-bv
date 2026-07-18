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
    BV -->|Inválido| EXH
    CS --> CM
    CS --> SU
    CTS --> CTM
    CM --> CR
    CTM --> CTR
    CR --> H2
    CTR --> H2
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
    START([POST /api/correntistas<br/>CorrentistaRequest]) --> VALID{Bean Validation<br/>válido?}

    VALID -->|Inválido| ERR400[400 Bad Request<br/>campos obrigatórios faltando]
    VALID -->|Válido| SAN[SanitizacaoUtil<br/>remove . - / do numeroIdentificador<br/>remove - . do cep]

    SAN --> DUP{Verifica duplicidade<br/>pelo identificador<br/>já existe?}

    DUP -->|Sim| ERR409[409 Conflict<br/>IdentificadorDuplicadoException]
    DUP -->|Não| MAP[CorrentistaMapper.toEntity<br/>Constrói entidade Correntista<br/>+ Endereco com CEP sanitizado]

    MAP --> SAVE[CorrentistaRepository.save<br/>Persists no H2 com auditoria<br/>dataCadastro + dataAtualizacao]
    SAVE --> RSP[CorrentistaMapper.toResponse<br/>Constrói CorrentistaResponse<br/>+ lista de ContaResponse]
    RSP --> OK([201 Created<br/>CorrentistaResponse])

    ERR400 --> HANDLER[ApiExceptionHandler<br/>retorna JSON com erro]
    ERR409 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe JSON de erro])

    style ERR400 fill:#f8d7da
    style ERR409 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 4. Atualização de Correntista (PUT /api/correntistas/{id})

```mermaid
flowchart TD
    START([PUT /api/correntistas/{id}<br/>CorrentistaAtualizacaoRequest]) --> FIND{findById no banco}

    FIND -->|Não existe| ERR404[404 Not Found<br/>CorrentistaNaoEncontradoException]
    FIND -->|Encontrado| NOME{nomeCompleto<br/>informado?}

    NOME -->|Sim| SET_N[correntista.setNomeCompleto]
    NOME -->|Não| END{endereco<br/>informado?}

    SET_N --> END

    END -->|Sim| SAN_CEP[SAnitizacaoUtil.sanitizarCep<br/>no CEP do endereco]
    SAN_CEP --> SET_E[correntista.setEndereco]
    END -->|Não| ID{tipoIdentificador E<br/>numeroIdentificador<br/>informados?}

    SET_E --> ID

    ID -->|Sim| SAN_DOC[SAnitizacaoUtil.sanitizarDocumento<br/>no numeroIdentificador]
    SAN_DOC --> SAME{Mesmo identificador<br/>já existente?}

    SAME -->|Sim| SET_ID[Atualiza identificador<br/>na entidade]
    SAME -->|Não| DUP{Verifica duplicidade<br/>pelo identificador<br/>já existe?}

    DUP -->|Sim| ERR409[409 Conflict<br/>IdentificadorDuplicadoException]
    DUP -->|Não| SET_ID

    SET_ID --> SAVE[Repository.save<br/>dataAtualizacao atualizada<br/>via @LastModifiedDate]
    ID -->|Não| SAVE

    SAVE --> RSP[Mapper.toResponse<br/>CorrentistaResponse]
    RSP --> OK([200 OK])

    ERR404 --> HANDLER[ApiExceptionHandler]
    ERR409 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe JSON de erro])

    style ERR404 fill:#f8d7da
    style ERR409 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 5. Exclusão de Correntista (DELETE /api/correntistas/{id})

```mermaid
flowchart TD
    START([DELETE /api/correntistas/{id}]) --> FIND{existsById<br/>no banco?}

    FIND -->|Não existe| ERR404[404 Not Found<br/>CorrentistaNaoEncontradoException]
    FIND -->|Existe| DEL[correntistaRepository.deleteById]

    DEL --> CASCADE[CascadeType.ALL<br/>+ orphanRemoval=true]
    CASCADE --> DEL_CONTAS[Hibernate DELETE FROM conta<br/>WHERE correntista_id = ?]
    DEL_CONTAS --> DEL_CORR[Hibernate DELETE FROM correntista<br/>WHERE id = ?]
    DEL_CORR --> OK([204 No Content])

    ERR404 --> HANDLER[ApiExceptionHandler]
    HANDLER --> CLIENT([Cliente recebe JSON de erro])

    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
    style DEL_CONTAS fill:#fff3cd
    style DEL_CORR fill:#fff3cd
```

---

## 6. Cadastro de Conta (POST /api/contas)

```mermaid
flowchart TD
    START([POST /api/contas<br/>ContaRequest]) --> VALID{Bean Validation<br/>válido?}

    VALID -->|Inválido| ERR400[400 Bad Request]
    VALID -->|Válido| FIND_CORR{findById<br/>correntistaId}

    FIND_CORR -->|Não existe| ERR404[404 Not Found<br/>CorrentistaNaoEncontradoException]
    FIND_CORR -->|Encontrado| MAP[ContaMapper.toEntity<br/>Constrói entidade Conta<br/>com referência ao Correntista]

    MAP --> STATUS{Status<br/>informado?}
    STATUS -->|Não| DEF[Define status = ATIVA<br/>padrão]
    STATUS -->|Sim| ADDConta

    DEF --> ADDConta[correntista.adicionarConta<br/>Vincula conta ao correntista<br/>em memória]
    ADDConta --> SAVE[contaRepository.save<br/>Cascade salva Conta<br/>+ atualiza lista do Correntista]

    SAVE --> RSP[ContaMapper.toResponse<br/>ContaResponse com correntistaId]
    RSP --> OK([201 Created])

    ERR400 --> HANDLER[ApiExceptionHandler]
    ERR404 --> HANDLER
    HANDLER --> CLIENT([Cliente recebe JSON de erro])

    style ERR400 fill:#f8d7da
    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 7. Atualização de Conta (PUT /api/contas/{id})

```mermaid
flowchart TD
    START([PUT /api/contas/{id}<br/>ContaAtualizacaoRequest]) --> FIND{findById<br/>no banco}

    FIND -->|Não existe| ERR404[404 Not Found<br/>ContaNaoEncontradaException]
    FIND -->|Encontrado| NUM{numero<br/>informado?}

    NUM -->|Sim| SET_N[conta.setNumero]
    NUM -->|Não| AGE{agencia<br/>informada?}
    SET_N --> AGE

    AGE -->|Sim| SET_A[conta.setAgencia]
    AGE -->|Não| TIP{tipo<br/>informado?}
    SET_A --> TIP

    TIP -->|Sim| SET_T[conta.setTipo]
    TIP -->|Não| SAL{saldo<br/>informado?}
    SET_T --> SAL

    SAL -->|Sim| SET_S[conta.setSaldo]
    SAL -->|Não| SAVE
    SET_S --> SAVE[Repository.save<br/>dataAtualizacao atualizada<br/>via @LastModifiedDate]

    SAVE --> RSP[ContaMapper.toResponse]
    RSP --> OK([200 OK])

    ERR404 --> HANDLER[ApiExceptionHandler]
    HANDLER --> CLIENT([Cliente recebe JSON de erro])

    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
```

---

## 8. Encerramento de Conta (DELETE /api/contas/{id})

```mermaid
flowchart TD
    START([DELETE /api/contas/{id}]) --> FIND{findById<br/>no banco}

    FIND -->|Não existe| ERR404[404 Not Found<br/>ContaNaoEncontradaException]
    FIND -->|Encontrado| SOFT[conta.setStatus<br/>= ENCERRADA]

    SOFT --> SAVE[Repository.save<br/>Apenas altera status<br/>registro NÃO é removido]
    SAVE --> OK([204 No Content<br/>Soft delete concluído])

    ERR404 --> HANDLER[ApiExceptionHandler]
    HANDLER --> CLIENT([Cliente recebe JSON de erro])

    style ERR404 fill:#f8d7da
    style OK fill:#d4edda
    style SOFT fill:#fff3cd
```

---

## 9. Tratamento de Exceções (ApiExceptionHandler)

```mermaid
flowchart TD
    EXC([Exceção lançada]) --> TYPE{Tipo da exceção}

    TYPE -->|CorrentistaNaoEncontradoException| NF404[404 Not Found<br/>"Correntista não encontrado"]
    TYPE -->|ContaNaoEncontradaException| NF404C[404 Not Found<br/>"Conta não encontrada"]
    TYPE -->|IdentificadorDuplicadoException| CON409[409 Conflict<br/>"Já existe correntista com este identificador"]
    TYPE -->|DataIntegrityViolationException| CON409D[409 Conflict<br/>"Violação de integridade"]
    TYPE -->|MethodArgumentNotValidException| VAL400[400 Bad Request<br/>campos inválidos detalhados]
    TYPE -->|Exception genérica| ERR500[500 Internal Server Error<br/>"Erro interno do servidor"]

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
        S1["sanitizar()"] --> R1["Regex: [^a-zA-Z0-9]<br/>remove tudo que NÃO é<br/>alfanumérico"]
    end

    subgraph "Saída Limpa"
        CPF_OK["'12345678909'"]
        CEP_OK["'01310100'"]
        CNPJ_OK["'12345678000190'"]
    end

    CPF -->|sanitizarDocumento| S1
    CEP -->|sanitizarCep| S1
    CNPJ -->|sanitizarDocumento| S1
    R1 --> CPF_OK
    R1 --> CEP_OK
    R1 --> CNPJ_OK

    CPF_OK --> DB[(Banco H2<br/>armazena sem<br/>formatação)]
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

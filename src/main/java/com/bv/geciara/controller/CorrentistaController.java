package com.bv.geciara.controller;

import com.bv.geciara.dto.request.CorrentistaAtualizacaoRequest;
import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.service.CorrentistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/correntistas")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Correntistas", description = "Endpoints para gerenciamento de correntistas")
public class CorrentistaController {

    private final CorrentistaService correntistaService;

    @GetMapping
    @Operation(
            summary = "Listar todos os correntistas (resumo)",
            description = "Retorna resumo paginado: id, nome, tipo e número do identificador. "
                    + "Use os parâmetros page, size e sort para controlar a paginação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            })
    public ResponseEntity<Page<CorrentistaResumoResponse>> listarTodos(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<CorrentistaResumoResponse> correntistas = correntistaService.listarTodos(pageable);
        return ResponseEntity.ok(correntistas);
    }

    @GetMapping("/completos")
    @Operation(
            summary = "Listar todos os correntistas (completo)",
            description = "Retorna todos os dados incluindo endereço e contas, com paginação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
            })
    public ResponseEntity<Page<CorrentistaResponse>> listarTodosCompletos(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<CorrentistaResponse> correntistas = correntistaService.listarTodosCompletos(pageable);
        return ResponseEntity.ok(correntistas);
    }

    @GetMapping("/{identificador}")
    @Operation(
            summary = "Buscar correntista por identificador",
            description = "Retorna todos os dados do correntista, incluindo endereço e contas",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Correntista encontrado"),
                    @ApiResponse(responseCode = "404", description = "Correntista não encontrado",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            })
    public ResponseEntity<CorrentistaResponse> buscarPorIdentificador(
            @Parameter(description = "Número do CPF/CNPJ/RG/Passaporte", example = "12345678909", required = true)
            @PathVariable String identificador) {
        CorrentistaResponse correntista = correntistaService.buscarPorIdentificador(identificador);
        return ResponseEntity.ok(correntista);
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar novo correntista",
            description = "Insere um novo correntista no sistema com todos os dados obrigatórios",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Correntista criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios faltando",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "409", description = "Identificador duplicado (CPF/CNPJ já cadastrado)",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            })
    public ResponseEntity<CorrentistaResponse> cadastrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do correntista a ser cadastrado",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Exemplo de cadastro",
                                    summary = "Cadastro de correntista com CPF",
                                    value = """
                                            {
                                                "nomeCompleto": "Maria Clara Fernandes",
                                                "endereco": {
                                                    "logradouro": "Rua Augusta",
                                                    "numero": "1578",
                                                    "bairro": "Consolação",
                                                    "cidade": "São Paulo",
                                                    "uf": "SP",
                                                    "cep": "01310100"
                                                },
                                                "tipoIdentificador": "CPF",
                                                "numeroIdentificador": "12345678909"
                                            }
                                            """)))
            @RequestBody @Valid CorrentistaRequest request) {
        CorrentistaResponse correntista = correntistaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(correntista);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar dados de um correntista",
            description = "Atualiza dados de um correntista existente. "
                    + "Todos os campos são opcionais — envie apenas o que deseja alterar. "
                    + "O campo 'dataCadastro' e o ID do banco de dados não podem ser alterados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Correntista atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "404", description = "Correntista não encontrado",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "409", description = "Identificador duplicado",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            })
    public ResponseEntity<CorrentistaResponse> atualizar(
            @Parameter(description = "ID do correntista", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados a serem atualizados (todos opcionais)",
                    required = true,
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "Atualizar apenas nome",
                                            value = """
                                                    {
                                                        "nomeCompleto": "Maria Clara Fernandes da Silva"
                                                    }
                                                    """),
                                    @ExampleObject(
                                            name = "Atualizar apenas endereço",
                                            value = """
                                                    {
                                                        "endereco": {
                                                            "logradouro": "Av. Paulista",
                                                            "numero": "1000",
                                                            "bairro": "Bela Vista",
                                                            "cidade": "São Paulo",
                                                            "uf": "SP",
                                                            "cep": "01310100"
                                                        }
                                                    }
                                                    """),
                                    @ExampleObject(
                                            name = "Atualizar identificador",
                                            value = """
                                                    {
                                                        "tipoIdentificador": "CPF",
                                                        "numeroIdentificador": "98765432100"
                                                    }
                                                    """)
                            }))
            @RequestBody @Valid CorrentistaAtualizacaoRequest request) {
        CorrentistaResponse correntista = correntistaService.atualizar(id, request);
        return ResponseEntity.ok(correntista);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir um correntista",
            description = "Remove permanentemente um correntista do sistema",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Correntista excluído com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Correntista não encontrado",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do correntista", example = "1", required = true)
            @PathVariable Long id) {
        correntistaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

package com.bv.geciara.controller;

import com.bv.geciara.dto.request.ContaAtualizacaoRequest;
import com.bv.geciara.dto.request.ContaRequest;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/contas")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Contas", description = "Gerenciamento de contas bancárias")
public class ContaController {

    private final ContaService contaService;

    @GetMapping
    @Operation(
            summary = "Listar todas as contas",
            description = "Retorna a lista de contas cadastradas com paginação. "
                    + "Use os parâmetros page, size e sort para controlar a paginação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ContaResponse.class)))
    })
    public ResponseEntity<Page<ContaResponse>> listarTodos(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ContaResponse> contas = contaService.listarTodos(pageable);
        return ResponseEntity.ok(contas);
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar nova conta",
            description = "Cria uma nova conta vinculada a um correntista existente. "
                    + "O status padrão é ATIVA caso não informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso",
                    content = @Content(schema = @Schema(implementation = ContaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Correntista não encontrado", content = @Content)
    })
    public ResponseEntity<ContaResponse> cadastrar(@Valid @RequestBody ContaRequest request) {
        ContaResponse response = contaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar dados da conta",
            description = "Atualiza dados de uma conta existente. "
                    + "Todos os campos são opcionais — envie apenas o que deseja alterar. "
                    + "Para encerrar a conta, use o endpoint DELETE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ContaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content)
    })
    public ResponseEntity<ContaResponse> atualizar(
            @Parameter(description = "ID da conta", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody @Valid ContaAtualizacaoRequest request) {
        ContaResponse response = contaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Encerrar conta (soft delete)",
            description = "Altera o status da conta para ENCERRADA. "
                    + "A conta não é removida fisicamente — apenas marcada como encerrada. "
                    + "A exclusão física ocorre apenas quando o correntista é removido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta encerrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content)
    })
    public ResponseEntity<Void> encerrar(
            @Parameter(description = "ID da conta", example = "1", required = true)
            @PathVariable Long id) {
        contaService.encerrar(id);
        return ResponseEntity.noContent().build();
    }

}

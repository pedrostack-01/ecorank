package br.com.ecorank.ecorank.resource;

import br.com.ecorank.ecorank.dto.AcaoSustentavelDto;
import br.com.ecorank.ecorank.dto.ErroDto;
import br.com.ecorank.ecorank.exception.EcoRankException;
import br.com.ecorank.ecorank.exception.RecursoNaoEncontradoException;
import br.com.ecorank.ecorank.mapper.AcaoSustentavelMapper;
import br.com.ecorank.ecorank.model.AcaoSustentavel;
import br.com.ecorank.ecorank.service.AcaoSustentavelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/acoes")
public class AcaoSustentavelResource {

    private final AcaoSustentavelService service;

    public AcaoSustentavelResource(AcaoSustentavelService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<AcaoSustentavel> acoes = service.listar();
            return ResponseEntity.ok(AcaoSustentavelMapper.toDtoList(acoes));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable long idUsuario) {
        try {
            List<AcaoSustentavel> acoes = service.listarPorUsuario(idUsuario);
            return ResponseEntity.ok(AcaoSustentavelMapper.toDtoList(acoes));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErroDto(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable long id) {
        try {
            AcaoSustentavel acao = service.buscarPorId(id);
            return ResponseEntity.ok(AcaoSustentavelMapper.toDto(acao));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErroDto(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody AcaoSustentavelDto dto) {
        try {
            AcaoSustentavel acao = AcaoSustentavelMapper.toEntity(dto);
            acao = service.salvar(acao);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AcaoSustentavelMapper.toDto(acao));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErroDto(e.getMessage()));
        } catch (EcoRankException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErroDto(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable long id,
                                       @RequestBody AcaoSustentavelDto dto) {
        try {
            AcaoSustentavel acao = AcaoSustentavelMapper.toEntity(dto);
            acao = service.atualizar(id, acao);
            return ResponseEntity.ok(AcaoSustentavelMapper.toDto(acao));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErroDto(e.getMessage()));
        } catch (EcoRankException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErroDto(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable long id) {
        try {
            service.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErroDto(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }
}

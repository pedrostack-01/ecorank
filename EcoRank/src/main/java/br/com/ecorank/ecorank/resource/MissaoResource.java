package br.com.ecorank.ecorank.resource;

import br.com.ecorank.ecorank.dto.ErroDto;
import br.com.ecorank.ecorank.dto.MissaoDto;
import br.com.ecorank.ecorank.exception.EcoRankException;
import br.com.ecorank.ecorank.exception.RecursoNaoEncontradoException;
import br.com.ecorank.ecorank.mapper.MissaoMapper;
import br.com.ecorank.ecorank.model.Missao;
import br.com.ecorank.ecorank.service.MissaoService;
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
@RequestMapping("/api/v1/missoes")
public class MissaoResource {

    private final MissaoService service;

    public MissaoResource(MissaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Missao> missoes = service.listar();
            return ResponseEntity.ok(MissaoMapper.toDtoList(missoes));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable long idUsuario) {
        try {
            List<Missao> missoes = service.listarPorUsuario(idUsuario);
            return ResponseEntity.ok(MissaoMapper.toDtoList(missoes));
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
            Missao missao = service.buscarPorId(id);
            return ResponseEntity.ok(MissaoMapper.toDto(missao));
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
    public ResponseEntity<?> salvar(@RequestBody MissaoDto dto) {
        try {
            Missao missao = MissaoMapper.toEntity(dto);
            missao = service.salvar(missao);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(MissaoMapper.toDto(missao));
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
    public ResponseEntity<?> atualizar(@PathVariable long id, @RequestBody MissaoDto dto) {
        try {
            Missao missao = MissaoMapper.toEntity(dto);
            missao = service.atualizar(id, missao);
            return ResponseEntity.ok(MissaoMapper.toDto(missao));
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

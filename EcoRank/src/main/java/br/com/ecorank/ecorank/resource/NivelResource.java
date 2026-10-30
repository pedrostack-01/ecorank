package br.com.ecorank.ecorank.resource;

import br.com.ecorank.ecorank.dto.ErroDto;
import br.com.ecorank.ecorank.dto.NivelDto;
import br.com.ecorank.ecorank.exception.EcoRankException;
import br.com.ecorank.ecorank.exception.RecursoNaoEncontradoException;
import br.com.ecorank.ecorank.mapper.NivelMapper;
import br.com.ecorank.ecorank.model.Nivel;
import br.com.ecorank.ecorank.service.NivelService;
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
@RequestMapping("/api/v1/niveis")
public class NivelResource {

    private final NivelService service;

    public NivelResource(NivelService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Nivel> niveis = service.listar();
            return ResponseEntity.ok(NivelMapper.toDtoList(niveis));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable long id) {
        try {
            Nivel nivel = service.buscarPorId(id);
            return ResponseEntity.ok(NivelMapper.toDto(nivel));
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
    public ResponseEntity<?> salvar(@RequestBody NivelDto dto) {
        try {
            Nivel nivel = NivelMapper.toEntity(dto);
            nivel = service.salvar(nivel);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(NivelMapper.toDto(nivel));
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
    public ResponseEntity<?> atualizar(@PathVariable long id, @RequestBody NivelDto dto) {
        try {
            Nivel nivel = NivelMapper.toEntity(dto);
            nivel = service.atualizar(id, nivel);
            return ResponseEntity.ok(NivelMapper.toDto(nivel));
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

package br.com.ecorank.ecorank.resource;

import br.com.ecorank.ecorank.dto.ErroDto;
import br.com.ecorank.ecorank.dto.UsuarioDto;
import br.com.ecorank.ecorank.exception.EcoRankException;
import br.com.ecorank.ecorank.exception.RecursoNaoEncontradoException;
import br.com.ecorank.ecorank.mapper.UsuarioMapper;
import br.com.ecorank.ecorank.model.Usuario;
import br.com.ecorank.ecorank.service.UsuarioService;
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
@RequestMapping("/api/v1/usuarios")
public class UsuarioResource {

    private final UsuarioService service;

    public UsuarioResource(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Usuario> usuarios = service.listar();
            return ResponseEntity.ok(UsuarioMapper.toDtoList(usuarios));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroDto("Erro interno no servidor"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable long id) {
        try {
            Usuario usuario = service.buscarPorId(id);
            return ResponseEntity.ok(UsuarioMapper.toDto(usuario));
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
    public ResponseEntity<?> salvar(@RequestBody UsuarioDto dto) {
        try {
            Usuario usuario = UsuarioMapper.toEntity(dto);
            usuario = service.salvar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UsuarioMapper.toDto(usuario));
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
    public ResponseEntity<?> atualizar(@PathVariable long id, @RequestBody UsuarioDto dto) {
        try {
            Usuario usuario = UsuarioMapper.toEntity(dto);
            usuario = service.atualizar(id, usuario);
            return ResponseEntity.ok(UsuarioMapper.toDto(usuario));
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

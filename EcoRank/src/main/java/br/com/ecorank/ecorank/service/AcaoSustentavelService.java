package br.com.ecorank.ecorank.service;

import br.com.ecorank.ecorank.exception.EcoRankException;
import br.com.ecorank.ecorank.exception.RecursoNaoEncontradoException;
import br.com.ecorank.ecorank.model.AcaoSustentavel;
import br.com.ecorank.ecorank.model.Status;
import br.com.ecorank.ecorank.model.Usuario;
import br.com.ecorank.ecorank.repository.AcaoSustentavelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AcaoSustentavelService {

    private final AcaoSustentavelRepository repositorio;
    private final UsuarioService usuarioService;
    private final PontuacaoService pontuacaoService;
    private final ValidacaoService validacaoService;
    private final NivelService nivelService;
    private final MissaoService missaoService;
    private final ConquistaService conquistaService;

    public AcaoSustentavelService(AcaoSustentavelRepository repositorio,
                                  UsuarioService usuarioService,
                                  PontuacaoService pontuacaoService,
                                  ValidacaoService validacaoService,
                                  NivelService nivelService,
                                  MissaoService missaoService,
                                  ConquistaService conquistaService) {
        this.repositorio = repositorio;
        this.usuarioService = usuarioService;
        this.pontuacaoService = pontuacaoService;
        this.validacaoService = validacaoService;
        this.nivelService = nivelService;
        this.missaoService = missaoService;
        this.conquistaService = conquistaService;
    }

    public List<AcaoSustentavel> listar() {
        return repositorio.findAll();
    }

    public List<AcaoSustentavel> listarPorUsuario(long idUsuario) {
        usuarioService.buscarPorId(idUsuario);
        return repositorio.findByUsuario(idUsuario);
    }

    public AcaoSustentavel buscarPorId(long id) {
        AcaoSustentavel acao = repositorio.findById(id);
        if (acao == null) {
            throw new RecursoNaoEncontradoException("Ação sustentável não encontrada com o id " + id);
        }
        return acao;
    }

    // Registra uma nova ação: calcula os pontos, valida a evidência e,
    // se for aprovada, atualiza pontos, missões, nível e conquistas do usuário
    @Transactional
    public AcaoSustentavel salvar(AcaoSustentavel acao) {
        if (acao.getIdUsuario() <= 0) {
            throw new EcoRankException("O idUsuario é obrigatório");
        }
        validarDados(acao);
        Usuario usuario = usuarioService.buscarPorId(acao.getIdUsuario());

        acao.setCategoria(acao.getCategoria().trim());
        acao.setDescricao(acao.getDescricao().trim());
        acao.setData(LocalDate.now());
        acao.setStatus(Status.PENDENTE);

        pontuacaoService.calcularPontos(acao);
        boolean valida = validacaoService.validarAcao(acao);

        repositorio.save(acao);

        if (valida) {
            processarAcaoAprovada(usuario, acao);
        }
        return acao;
    }

    // Só é possível corrigir uma ação que foi rejeitada (ex: link errado)
    @Transactional
    public AcaoSustentavel atualizar(long id, AcaoSustentavel dados) {
        AcaoSustentavel acao = buscarPorId(id);

        if (acao.getStatus() == Status.APROVADA) {
            throw new EcoRankException("Uma ação já aprovada não pode ser alterada");
        }
        validarDados(dados);

        acao.setCategoria(dados.getCategoria().trim());
        acao.setDescricao(dados.getDescricao().trim());
        acao.setLinkEvidencia(dados.getLinkEvidencia());

        pontuacaoService.calcularPontos(acao);
        boolean valida = validacaoService.validarAcao(acao);

        repositorio.update(acao);

        if (valida) {
            Usuario usuario = usuarioService.buscarPorId(acao.getIdUsuario());
            processarAcaoAprovada(usuario, acao);
        }
        return acao;
    }

    // Se a ação estava aprovada, os pontos dela são devolvidos (retirados do usuário)
    @Transactional
    public void excluir(long id) {
        AcaoSustentavel acao = buscarPorId(id);
        repositorio.deleteById(id);

        if (acao.getStatus() == Status.APROVADA) {
            Usuario usuario = usuarioService.buscarPorId(acao.getIdUsuario());
            usuario.removerPontos(acao.getPontos());
            nivelService.atualizarNivel(usuario);
            usuarioService.atualizarPontuacao(usuario);
        }
    }

    private void processarAcaoAprovada(Usuario usuario, AcaoSustentavel acao) {

        pontuacaoService.adicionarPontos(usuario, acao);
        missaoService.registrarProgresso(usuario);
        nivelService.atualizarNivel(usuario);
        usuarioService.atualizarPontuacao(usuario);

        if (contarAprovadas(usuario.getId()) == 1) {
            conquistaService.desbloquear(
                    usuario.getId(),
                    "Primeira Ação",
                    "Você registrou sua primeira ação sustentável."
            );
        }
    }

    private int contarAprovadas(long idUsuario) {
        int total = 0;
        List<AcaoSustentavel> acoes = repositorio.findByUsuario(idUsuario);

        for (AcaoSustentavel acao : acoes) {
            if (acao.getStatus() == Status.APROVADA) {
                total++;
            }
        }
        return total;
    }

    private void validarDados(AcaoSustentavel acao) {
        if (acao.getCategoria() == null || acao.getCategoria().isBlank()) {
            throw new EcoRankException("A categoria é obrigatória");
        }
        if (acao.getCategoria().trim().length() > 50) {
            throw new EcoRankException("A categoria deve ter no máximo 50 caracteres");
        }
        if (acao.getDescricao() == null || acao.getDescricao().isBlank()) {
            throw new EcoRankException("A descrição da ação é obrigatória");
        }
        if (acao.getDescricao().trim().length() > 300) {
            throw new EcoRankException("A descrição deve ter no máximo 300 caracteres");
        }
        if (acao.getLinkEvidencia() != null && acao.getLinkEvidencia().length() > 500) {
            throw new EcoRankException("O link da evidência deve ter no máximo 500 caracteres");
        }
    }
}

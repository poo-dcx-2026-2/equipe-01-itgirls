package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

/**
 * Implementação inicial e parcial dos usuários.
 *
 * <p>Não existe classe de domínio para o usuário: os dados estão soltos em listas
 * paralelas. É proposital. Uma das primeiras decisões da Entrega 1 é definir se
 * isso deve continuar assim.</p>
 */
public class UsuariosEmMemoria implements UsuarioService {

    //dar uma olhadinha melhor nessa parte

    private final Map<String, Usuario> usuarios = new HashMap<>();

    @Override
    public void cadastrarUsuario(String matricula, String nome) throws RecursoDuplicadoException {

        if (usuarios.containsKey(matricula)) {
            throw new RecursoDuplicadoException("Já existe usuário com a matrícula " + matricula);
        }

        Usuario usuario = new Usuario(matricula, nome);
        usuarios.put(matricula, usuario);
    }

    @Override
    public UsuarioView buscarUsuario(String matricula) throws RecursoNaoEncontradoException {
        //buscar pela chave, inves de peo
        Usuario usuario = usuarios.get(matricula);
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + matricula);
        }
        return paraView(usuario);
    }

    private UsuarioView paraView(Usuario usuario) {
        return new UsuarioView(usuario.getMatricula(), usuario.getNome(), true, 0);
    }

    @Override
    public List<UsuarioView> listarUsuarios() {
        List<UsuarioView> resultado = new ArrayList<>();
        // Percorre os valores armazenados no Map
        for (Usuario u : usuarios.values()) {
            resultado.add(paraView(u));
        }
        resultado.sort((a, b) -> a.nome().compareToIgnoreCase(b.nome()));
        return resultado;
    }

    @Override
    public void desativarUsuario(String matricula)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar desativarUsuario");
    }

    @Override
    public void reativarUsuario(String matricula) throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Entrega 2: implementar reativarUsuario");
    }
}

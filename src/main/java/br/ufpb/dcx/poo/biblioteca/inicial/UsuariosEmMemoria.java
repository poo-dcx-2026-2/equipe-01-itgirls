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
 * Implementação em memória do gerenciamento de usuários.
 * Os usuários são armazenados em um Map, utilizando a matrícula como chave.
 */
public class UsuariosEmMemoria implements UsuarioService {

    private final Map<String, Usuario> usuarios = new HashMap<>();

    @Override
    public void cadastrarUsuario(String matricula, String nome) throws RecursoDuplicadoException {

        exigirTextoPreenchido(matricula, "matrícula");
        exigirTextoPreenchido(nome, "nome");

        if (usuarios.containsKey(matricula)) {
            throw new RecursoDuplicadoException("Já existe usuário com a matrícula " + matricula);
        }

        Usuario usuario = new Usuario(matricula, nome);
        usuarios.put(matricula, usuario);
    }

    @Override
    public UsuarioView buscarUsuario(String matricula) throws RecursoNaoEncontradoException {

        exigirTextoPreenchido(matricula, "matrícula");

        //busca o usuário diretamente pela matrícula (chave do Map).
        Usuario usuario = usuarios.get(matricula);
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + matricula);
        }
        return visaoDe(usuario);
    }

    private UsuarioView visaoDe(Usuario usuario) {
        return new UsuarioView(
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.isAtivo(),
                usuario.getEmprestimosAtivos()
        );
    }

    @Override
    public List<UsuarioView> listarUsuarios() {
        List<UsuarioView> resultado = new ArrayList<>();
        // Percorre os valores armazenados no Map
        for (Usuario u : usuarios.values()) {
            resultado.add(visaoDe(u));
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

    private void exigirTextoPreenchido(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(
                    "O campo " + campo + " é obrigatório."
            );
        }
    }
}

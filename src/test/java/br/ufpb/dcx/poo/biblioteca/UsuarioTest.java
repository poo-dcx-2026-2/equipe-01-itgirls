package br.ufpb.dcx.poo.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;

import java.time.LocalDate;

/** Testes públicos dos usuários. */
class UsuarioTest {

    private Biblioteca biblioteca;

    @BeforeEach
    void criarBibliotecaVazia() {
        biblioteca = Fabrica.novaBiblioteca();
    }

    @Test
    @DisplayName("usuário cadastrado começa ativo e sem empréstimos")
    void cadastrarEBuscar() throws BibliotecaException {
        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        UsuarioView usuario = biblioteca.usuarios().buscarUsuario("2026001");

        assertEquals("Ana Souza", usuario.nome());
        assertTrue(usuario.ativo());
        assertEquals(0, usuario.emprestimosAtivos());
    }

    @Test
    @DisplayName("matrícula repetida é recusada")
    void matriculaDuplicada() throws BibliotecaException {
        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        assertThrows(RecursoDuplicadoException.class,
                () -> biblioteca.usuarios().cadastrarUsuario("2026001", "Outro Nome"));
    }

    @Test
    @DisplayName("buscar usuário inexistente lança RecursoNaoEncontradoException")
    void buscarInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.usuarios().buscarUsuario("0000"));
    }

    @Test
    @DisplayName("nome em branco é entrada inválida")
    void nomeEmBranco() {
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.usuarios().cadastrarUsuario("2026001", "   "));
    }

    @Test
    @DisplayName("listar devolve os usuários ordenados por nome")
    void listarOrdenado() throws BibliotecaException {
        biblioteca.usuarios().cadastrarUsuario("3", "Carlos");
        biblioteca.usuarios().cadastrarUsuario("1", "Ana");
        biblioteca.usuarios().cadastrarUsuario("2", "Bruno");

        assertEquals("Ana", biblioteca.usuarios().listarUsuarios().get(0).nome());
        assertEquals("Carlos", biblioteca.usuarios().listarUsuarios().get(2).nome());
    }

    @Test
    @DisplayName("usuário sem empréstimos ativos pode ser desativado")
    void desativarUsuario() throws BibliotecaException {

        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        biblioteca.usuarios().desativarUsuario("2026001");

        UsuarioView usuario = biblioteca.usuarios().buscarUsuario("2026001");

        assertFalse(usuario.ativo());
    }

    @Test
    @DisplayName("não permite desativar usuário com empréstimo ativo")
    void desativarUsuarioComEmprestimoAtivo() throws Exception {

        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        biblioteca.acervo().cadastrarItem(
                "L1", "Java Efetivo", "Bloch", "livro", 2019
        );

        biblioteca.acervo().adicionarExemplar("L1", "T001");

        biblioteca.emprestimos().emprestar(
                "T001", "2026001", LocalDate.of(2026, 11, 2)
        );

        assertThrows(
                OperacaoNaoPermitidaException.class,
                () -> biblioteca.usuarios().desativarUsuario("2026001")
        );

        assertTrue(
                biblioteca.usuarios().buscarUsuario("2026001").ativo()
        );
    }

    @Test
    @DisplayName("usuário desativado pode ser reativado")
    void reativarUsuario() throws BibliotecaException {

        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        biblioteca.usuarios().desativarUsuario("2026001");

        biblioteca.usuarios().reativarUsuario("2026001");

        assertTrue(
                biblioteca.usuarios().buscarUsuario("2026001").ativo()
        );
    }

    @Test
    @DisplayName("desativar e reativar usuário várias vezes é permitido")
    void operacoesRepetidasUsuario() throws BibliotecaException {

        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        biblioteca.usuarios().desativarUsuario("2026001");
        biblioteca.usuarios().desativarUsuario("2026001");

        assertFalse(
                biblioteca.usuarios().buscarUsuario("2026001").ativo()
        );

        biblioteca.usuarios().reativarUsuario("2026001");
        biblioteca.usuarios().reativarUsuario("2026001");

        assertTrue(
                biblioteca.usuarios().buscarUsuario("2026001").ativo()
        );
    }

    @Test
    @DisplayName("desativar usuário inexistente lança exceção")
    void desativarUsuarioInexistente() {

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> biblioteca.usuarios().desativarUsuario("9999999")
        );
    }

    @Test
    @DisplayName("reativar usuário inexistente lança exceção")
    void reativarUsuarioInexistente() {

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> biblioteca.usuarios().reativarUsuario("9999999")
        );
    }
}
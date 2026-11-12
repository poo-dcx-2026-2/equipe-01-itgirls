
package br.ufpb.dcx.poo.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;

class EmprestimosEmMemoriaTest {

    private Biblioteca biblioteca;

    @BeforeEach
    void criarBibliotecaComDados() throws Exception {
        biblioteca = Fabrica.novaBiblioteca();

        biblioteca.usuarios().cadastrarUsuario("2026001", "Ana Souza");

        biblioteca.acervo().cadastrarItem(
                "L1", "Java Efetivo", "Bloch", "livro", 2019
        );

        biblioteca.acervo().adicionarExemplar("L1", "T001");
    }

    @Test
    @DisplayName("tombo em branco lança DadosInvalidosException")
    void tomboEmBranco() throws Exception {

        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().emprestar(
                        " ", "2026001", LocalDate.of(2026, 11, 11)
                ));

        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        assertEquals(0,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }

    @Test
    @DisplayName("matrícula em branco lança DadosInvalidosException")
    void matriculaEmBranco() throws Exception {

        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().emprestar(
                        "T001", " ", LocalDate.of(2026, 11, 11)
                ));

        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        assertEquals(0,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }

    @Test
    @DisplayName("data nula lança DadosInvalidosException")
    void dataNula() throws Exception {

        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().emprestar(
                        "T001", "2026001", null
                ));

        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        assertEquals(0,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }

    @Test
    @DisplayName("exemplar inexistente lança RecursoNaoEncontradoException")
    void exemplarInexistente() throws Exception {

        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.emprestimos().emprestar(
                        "T999", "2026001", LocalDate.of(2026, 11, 11)
                ));

        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        assertEquals(0,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }

    @Test
    @DisplayName("usuário inexistente lança RecursoNaoEncontradoException")
    void usuarioInexistente() throws Exception {

        assertThrows(RecursoNaoEncontradoException.class,
                () -> biblioteca.emprestimos().emprestar(
                        "T001", "9999999", LocalDate.of(2026, 11, 11)
                ));

        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        assertEquals(0,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }

    @Test
    @DisplayName("exemplar emprestado lança ExemplarIndisponivelException")
    void exemplarIndisponivel() throws Exception {

        biblioteca.usuarios().cadastrarUsuario("2026002", "João Silva");

        // O primeiro usuário realiza o empréstimo.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", LocalDate.of(2026, 11, 11)
        );

        // O segundo usuário tenta pegar o mesmo exemplar.
        assertThrows(ExemplarIndisponivelException.class,
                () -> biblioteca.emprestimos().emprestar(
                        "T001", "2026002", LocalDate.of(2026, 11, 11)
                ));

        assertEquals(StatusExemplar.EMPRESTADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        assertEquals(1,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());

        assertEquals(0,
                biblioteca.usuarios().buscarUsuario("2026002").emprestimosAtivos());
    }

    @Test
    @DisplayName("limite de empréstimos excedido lança LimiteDeEmprestimosExcedidoException")
    void limiteDeEmprestimosExcedido() throws Exception {

        // Adiciona mais três exemplares.
        biblioteca.acervo().adicionarExemplar("L1", "T002");
        biblioteca.acervo().adicionarExemplar("L1", "T003");
        biblioteca.acervo().adicionarExemplar("L1", "T004");

        // O usuário realiza três empréstimos.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", LocalDate.of(2026, 11, 11)
        );

        biblioteca.emprestimos().emprestar(
                "T002", "2026001", LocalDate.of(2026, 11, 11)
        );

        biblioteca.emprestimos().emprestar(
                "T003", "2026001", LocalDate.of(2026, 11, 11)
        );

        // Tenta realizar o quarto empréstimo.
        assertThrows(LimiteDeEmprestimosExcedidoException.class,
                () -> biblioteca.emprestimos().emprestar(
                        "T004", "2026001", LocalDate.of(2026, 11, 11)
                ));

        // O quarto exemplar continua disponível.
        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(3).status());

        // O usuário continua com exatamente três empréstimos ativos.
        assertEquals(3,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }
}

package br.ufpb.dcx.poo.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;

class EmprestimosEmMemoriaTest {

    private Biblioteca biblioteca;
    private static final LocalDate SEGUNDA =
            LocalDate.of(2026, 11, 2);

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
    void emprestimoTemPrazoDeQuatorzeDias() throws Exception {

        biblioteca.emprestimos().emprestar(
                "T001", "2026001", SEGUNDA
        );

        EmprestimoView emprestimo = biblioteca.emprestimos()
                .listarEmprestimosDoUsuario("2026001").get(0);

        assertEquals(
                LocalDate.of(2026, 11, 16),
                emprestimo.dataPrevistaDeDevolucao()
        );
    }

    @Test
    void noDiaPrevistoAindaNaoHaAtraso() throws Exception {

        biblioteca.emprestimos().emprestar(
                "T001", "2026001", SEGUNDA
        );

        List<EmprestimoView> noDia = biblioteca.emprestimos()
                .listarAtrasados(LocalDate.of(2026, 11, 16));

        List<EmprestimoView> noDiaSeguinte = biblioteca.emprestimos()
                .listarAtrasados(LocalDate.of(2026, 11, 17));

        assertEquals(0, noDia.size());
        assertEquals(1, noDiaSeguinte.size());
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

    @Test
    @DisplayName("devolver empréstimo ativo atualiza o status do exemplar")
    void devolverEmprestimoAtivo() throws Exception {

        // O usuário realiza o empréstimo.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", LocalDate.of(2026, 11, 11)
        );

        // O usuário devolve o exemplar.
        biblioteca.emprestimos().devolver(
                "T001", LocalDate.of(2026, 11, 12)
        );

        // O exemplar volta a ficar disponível.
        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        // O usuário não possui mais empréstimos ativos.
        assertEquals(0,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }
    @Test
    @DisplayName("devolver exemplar sem empréstimo ativo lança OperacaoNaoPermitidaException")
    void devolverSemEmprestimoAtivo() throws Exception {

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> biblioteca.emprestimos().devolver(
                        "T001", LocalDate.of(2026, 11, 12)
                ));

        // O exemplar continua disponível.
        assertEquals(StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        // O usuário continua sem empréstimos ativos.
        assertEquals(0,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }

    @Test
    @DisplayName("data anterior ao empréstimo lança DadosInvalidosException")
    void dataDeDevolucaoInvalida() throws Exception {

        // O usuário realiza o empréstimo.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", LocalDate.of(2026, 11, 11)
        );

        // Tenta devolver antes da data do empréstimo.
        assertThrows(DadosInvalidosException.class,
                () -> biblioteca.emprestimos().devolver(
                        "T001", LocalDate.of(2026, 11, 10)
                ));

        // O exemplar continua emprestado.
        assertEquals(StatusExemplar.EMPRESTADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status());

        // O empréstimo continua ativo.
        assertEquals(1,
                biblioteca.usuarios().buscarUsuario("2026001").emprestimosAtivos());
    }

    @Test
    @DisplayName("reservar item sem exemplares disponíveis")
    void reservarItemSemExemplarDisponivel() throws Exception {

        // Cadastra um segundo usuário.
        biblioteca.usuarios().cadastrarUsuario("2026002", "João Silva");

        // Ana realiza o empréstimo do único exemplar.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", SEGUNDA
        );

        // João reserva o item, pois não há exemplares disponíveis.
        String idReserva = biblioteca.emprestimos().reservar(
                "L1", "2026002", SEGUNDA
        );

        // Consulta a fila de reservas.
        List<ReservaView> reservas = biblioteca.emprestimos()
                .listarReservas("L1");

        assertEquals(1, reservas.size());
        assertEquals(idReserva, reservas.get(0).id());
        assertEquals("2026002", reservas.get(0).matricula());
        assertEquals(1, reservas.get(0).posicaoNaFila());

        // O exemplar continua emprestado para Ana.
        assertEquals(StatusExemplar.EMPRESTADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status());
    }

    @Test
    @DisplayName("reservas respeitam a ordem de chegada")
    void reservasRespeitamOrdemDeChegada() throws Exception {

        biblioteca.usuarios().cadastrarUsuario("2026002", "João Silva");
        biblioteca.usuarios().cadastrarUsuario("2026003", "Carla Souza");

        // Ana pega o único exemplar.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", SEGUNDA
        );

        // João entra primeiro na fila.
        String reservaJoao = biblioteca.emprestimos().reservar(
                "L1", "2026002", SEGUNDA
        );

        // Carla entra depois.
        String reservaCarla = biblioteca.emprestimos().reservar(
                "L1", "2026003", SEGUNDA
        );

        List<ReservaView> reservas = biblioteca.emprestimos()
                .listarReservas("L1");

        assertEquals(2, reservas.size());

        assertEquals(reservaJoao, reservas.get(0).id());
        assertEquals("2026002", reservas.get(0).matricula());
        assertEquals(1, reservas.get(0).posicaoNaFila());

        assertEquals(reservaCarla, reservas.get(1).id());
        assertEquals("2026003", reservas.get(1).matricula());
        assertEquals(2, reservas.get(1).posicaoNaFila());
    }

    @Test
    @DisplayName("cancelar reserva com exemplar separado transfere exemplar para próxima reserva")
    void cancelarReservaComExemplarSeparado() throws Exception {

        // Cadastra João e Carla.
        biblioteca.usuarios().cadastrarUsuario("2026002", "João Silva");
        biblioteca.usuarios().cadastrarUsuario("2026003", "Carla Souza");

        // Ana pega o único exemplar.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", SEGUNDA
        );

        // João e Carla entram na fila, nessa ordem.
        String reservaJoao = biblioteca.emprestimos().reservar(
                "L1", "2026002", SEGUNDA
        );

        biblioteca.emprestimos().reservar(
                "L1", "2026003", SEGUNDA
        );

        // Ana devolve o exemplar. João recebe a reserva.
        biblioteca.emprestimos().devolver(
                "T001", SEGUNDA.plusDays(1)
        );

        // João cancela sua reserva.
        biblioteca.emprestimos().cancelarReserva(reservaJoao);

        // Carla deve ser a única pessoa na fila.
        assertEquals(
                1,
                biblioteca.emprestimos().listarReservas("L1").size()
        );

        assertEquals(
                "2026003",
                biblioteca.emprestimos().listarReservas("L1").get(0).matricula()
        );

        // O exemplar deve continuar reservado, agora para Carla.
        assertEquals(
                StatusExemplar.RESERVADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );
    }

    @Test
    @DisplayName("cancelar reserva sem exemplar separado remove apenas a reserva da fila")
    void cancelarReservaSemExemplarSeparado() throws Exception {

        // Cadastra João e Carla.
        biblioteca.usuarios().cadastrarUsuario("2026002", "João Silva");
        biblioteca.usuarios().cadastrarUsuario("2026003", "Carla Souza");

        // Ana pega o único exemplar.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", SEGUNDA
        );

        // João e Carla fazem suas reservas.
        String reservaJoao = biblioteca.emprestimos().reservar(
                "L1", "2026002", SEGUNDA
        );

        biblioteca.emprestimos().reservar(
                "L1", "2026003", SEGUNDA
        );

        // João cancela enquanto ainda está esperando.
        biblioteca.emprestimos().cancelarReserva(reservaJoao);

        // Deve restar apenas a reserva de Carla.
        List<ReservaView> reservas = biblioteca.emprestimos()
                .listarReservas("L1");

        assertEquals(1, reservas.size());
        assertEquals("2026003", reservas.get(0).matricula());

        // O exemplar continua emprestado para Ana.
        assertEquals(
                StatusExemplar.EMPRESTADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );

        // Ana devolve o exemplar.
        biblioteca.emprestimos().devolver(
                "T001", SEGUNDA.plusDays(1)
        );

        // Carla deve receber o exemplar reservado.
        assertEquals(
                StatusExemplar.RESERVADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );
    }

    @Test
    @DisplayName("cancelar reserva inexistente lança RecursoNaoEncontradoException")
    void cancelarReservaInexistente() throws Exception {

        // Cadastra João.
        biblioteca.usuarios().cadastrarUsuario(
                "2026002", "João Silva"
        );

        // Ana pega o único exemplar.
        biblioteca.emprestimos().emprestar(
                "T001", "2026001", SEGUNDA
        );

        // João faz uma reserva.
        biblioteca.emprestimos().reservar(
                "L1", "2026002", SEGUNDA
        );

        // Tenta cancelar uma reserva que não existe.
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> biblioteca.emprestimos().cancelarReserva("999")
        );

        // A reserva verdadeira deve continuar na fila.
        List<ReservaView> reservas = biblioteca.emprestimos()
                .listarReservas("L1");

        assertEquals(1, reservas.size());
        assertEquals("2026002", reservas.get(0).matricula());

        // O exemplar continua emprestado para Ana.
        assertEquals(
                StatusExemplar.EMPRESTADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );
    }

    @Test
    @DisplayName("ciclo completo de reserva respeita a fila e libera exemplar ao cancelar")
    void cicloCompletoDeReserva() throws Exception {

        // Cadastra Bruno e Carla.
        biblioteca.usuarios().cadastrarUsuario("2026002", "Bruno");
        biblioteca.usuarios().cadastrarUsuario("2026003", "Carla");

        // Ana pega o único exemplar.
        biblioteca.emprestimos().emprestar("T001", "2026001", SEGUNDA);

        assertEquals(
                StatusExemplar.EMPRESTADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );

        // Bruno e Carla fazem reservas, nessa ordem.
        String reservaBruno = biblioteca.emprestimos()
                .reservar("L1", "2026002", SEGUNDA);

        String reservaCarla = biblioteca.emprestimos()
                .reservar("L1", "2026003", SEGUNDA);

        List<ReservaView> reservas = biblioteca.emprestimos()
                .listarReservas("L1");

        assertEquals(2, reservas.size());
        assertEquals(reservaBruno, reservas.get(0).id());
        assertEquals(reservaCarla, reservas.get(1).id());

        // Ana devolve o livro. Bruno recebe a reserva.
        biblioteca.emprestimos().devolver("T001", SEGUNDA.plusDays(1));

        assertEquals(
                StatusExemplar.RESERVADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );

        // Carla não pode pegar o livro antes de Bruno.
        assertThrows(
                ExemplarIndisponivelException.class,
                () -> biblioteca.emprestimos()
                        .emprestar("T001", "2026003", SEGUNDA.plusDays(1))
        );

        // Bruno pega o livro.
        biblioteca.emprestimos()
                .emprestar("T001", "2026002", SEGUNDA.plusDays(2));

        assertEquals(
                StatusExemplar.EMPRESTADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );

        // A reserva de Bruno foi atendida. Carla continua na fila.
        reservas = biblioteca.emprestimos().listarReservas("L1");

        assertEquals(1, reservas.size());
        assertEquals(reservaCarla, reservas.get(0).id());

        // Bruno devolve o livro. Carla recebe a reserva.
        biblioteca.emprestimos().devolver("T001", SEGUNDA.plusDays(3));

        assertEquals(
                StatusExemplar.RESERVADO,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );

        reservas = biblioteca.emprestimos().listarReservas("L1");

        assertEquals(1, reservas.size());
        assertEquals("2026003", reservas.get(0).matricula());

        // Carla cancela sua reserva.
        biblioteca.emprestimos().cancelarReserva(reservaCarla);

        assertEquals(0, biblioteca.emprestimos().listarReservas("L1").size());

        // Sem reservas pendentes, o exemplar fica disponível.
        assertEquals(
                StatusExemplar.DISPONIVEL,
                biblioteca.acervo().listarExemplares("L1").get(0).status()
        );
    }
}
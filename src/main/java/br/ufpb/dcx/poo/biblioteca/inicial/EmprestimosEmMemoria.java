package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

public class EmprestimosEmMemoria implements EmprestimoService {

    private final AcervoEmMemoria acervo;
    private final UsuariosEmMemoria usuarios;
    private final List<Emprestimo> emprestimos = new ArrayList<>();
    private final Map<String, FilaDeReservas> filasPorItem = new HashMap<>();
    private int proximoId = 1;

    public EmprestimosEmMemoria(AcervoEmMemoria acervo,
                                UsuariosEmMemoria usuarios) {
        this.acervo = acervo;
        this.usuarios = usuarios;
    }

    private String novoId() {
        return String.valueOf(proximoId++);
    }

    private FilaDeReservas obterFila(String codigoDoItem) {
        return filasPorItem.computeIfAbsent(
                codigoDoItem,
                codigo -> new FilaDeReservas()
        );
    }

    @Override
    public String emprestar(String tombo, String matricula, LocalDate data) throws RecursoNaoEncontradoException, ExemplarIndisponivelException, UsuarioInativoException, LimiteDeEmprestimosExcedidoException {
        if (tombo == null || tombo.isBlank()) {
            throw new DadosInvalidosException("O tombo é obrigatório.");
        }

        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }

        if (data == null) {
            throw new DadosInvalidosException("A data do empréstimo é obrigatória.");
        }

        Exemplar exemplar = acervo.buscarExemplarPorTombo(tombo);

        if (exemplar == null) {
            throw new RecursoNaoEncontradoException(
                    "Exemplar não encontrado: " + tombo
            );
        }

        Usuario usuario = usuarios.buscarUsuarioPorMatricula(matricula);

        if (usuario == null) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado: " + matricula
            );
        }

        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException(
                    "O usuário está inativo."
            );
        }

        FilaDeReservas fila = filasPorItem.get(
                exemplar.getCodigoDoItem()
        );

        Reserva reservaDoUsuario = null;

        if (fila != null) {
            reservaDoUsuario =
                    fila.buscarReservaDoUsuarioComExemplar(matricula, exemplar);
        }

        boolean reservadoParaEsteUsuario =
                reservaDoUsuario != null
                        && exemplar.getStatus() == StatusExemplar.RESERVADO;

        if (exemplar.getStatus() != StatusExemplar.DISPONIVEL
                && !reservadoParaEsteUsuario) {
            throw new ExemplarIndisponivelException(
                    "O exemplar não está disponível para empréstimo."
            );
        }

        if (usuario.getEmprestimosAtivos() >=
                EmprestimoService.LIMITE_DE_EMPRESTIMOS) {
            throw new LimiteDeEmprestimosExcedidoException(
                    "O usuário atingiu o limite de empréstimos."
            );
        }

        Emprestimo emprestimo = new Emprestimo(
                novoId(), exemplar, usuario, data
        );

        // Registra o empréstimo na lista do usuário.
        usuario.adicionarEmprestimo(emprestimo);

        // Registra o empréstimo na lista geral do serviço.
        emprestimos.add(emprestimo);

        exemplar.setStatus(StatusExemplar.EMPRESTADO);

        if (reservaDoUsuario != null) {
            fila.remover(reservaDoUsuario.getId());
        }
        
        return emprestimo.getId();
    }

    @Override
    public void devolver(String tombo, LocalDate data) throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        Emprestimo emprestimoEncontrado = null;

        for (Emprestimo emprestimo : emprestimos) {

            if (emprestimo.getExemplar().getTombo().equals(tombo)
                    && emprestimo.estaAtivo()) {

                emprestimoEncontrado = emprestimo;
                break;
            }
        }
        if (emprestimoEncontrado == null) {
            throw new OperacaoNaoPermitidaException(
                    "Não existe empréstimo ativo para este exemplar."
            );
        }

        // Registra a data da devolução.
        emprestimoEncontrado.registrarDevolucao(data);

       // Atualiza o status do exemplar.
        Exemplar exemplar = emprestimoEncontrado.getExemplar();

        FilaDeReservas fila = filasPorItem.get(
                exemplar.getCodigoDoItem()
        );

        if (fila != null && fila.proxima() != null) {
            fila.proxima().atribuirExemplar(exemplar);
        } else {
            exemplar.setStatus(StatusExemplar.DISPONIVEL);
        }
    }

    @Override
    public List<EmprestimoView> listarEmprestimosDoUsuario(String matricula) throws RecursoNaoEncontradoException {
        Usuario usuario = usuarios.buscarUsuarioPorMatricula(matricula);

        if (usuario == null) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado: " + matricula
            );
        }

        List<Emprestimo> emprestimosDoUsuario = new ArrayList<>();

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getUsuario().getMatricula().equals(matricula)) {
                emprestimosDoUsuario.add(emprestimo);
            }
        }

        emprestimosDoUsuario.sort(
                Comparator.comparing(Emprestimo::getDataDoEmprestimo).reversed()
        );

        List<EmprestimoView> resultado = new ArrayList<>();

        for (Emprestimo emprestimo : emprestimosDoUsuario) {

            StatusEmprestimo status;

            if (emprestimo.estaAtivo()) {
                status = StatusEmprestimo.ATIVO;
            } else {
                status = StatusEmprestimo.DEVOLVIDO;
            }

            resultado.add(new EmprestimoView(
                    emprestimo.getId(),
                    emprestimo.getExemplar().getTombo(),
                    emprestimo.getUsuario().getMatricula(),
                    emprestimo.getDataDoEmprestimo(),
                    emprestimo.getDataPrevistaDeDevolucao(),
                    emprestimo.getDataDaDevolucao(),
                    status
            ));
        }

        return resultado;
    }

    @Override
    public List<EmprestimoView> listarAtrasados(LocalDate data) {
        List<EmprestimoView> resultado = new ArrayList<>();

        for (Emprestimo emprestimo : emprestimos) {

            if (emprestimo.estaAtivo()
                    && emprestimo.getDataPrevistaDeDevolucao().isBefore(data)) {

                resultado.add(new EmprestimoView(
                        emprestimo.getId(),
                        emprestimo.getExemplar().getTombo(),
                        emprestimo.getUsuario().getMatricula(),
                        emprestimo.getDataDoEmprestimo(),
                        emprestimo.getDataPrevistaDeDevolucao(),
                        emprestimo.getDataDaDevolucao(),
                        StatusEmprestimo.ATRASADO
                ));
            }
        }

        return resultado;
    }

    @Override
    public String reservar(String codigoDoItem, String matricula, LocalDate data) throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        if (codigoDoItem == null || codigoDoItem.isBlank()) {
            throw new DadosInvalidosException("O código do item é obrigatório.");
        }

        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }

        if (data == null) {
            throw new DadosInvalidosException("A data da reserva é obrigatória.");
        }

        Item item = acervo.buscarItemInterno(codigoDoItem);

        Usuario usuario = usuarios.buscarUsuarioPorMatricula(matricula);

        if (usuario == null) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado: " + matricula
            );
        }

        if (!usuario.isAtivo()) {
            throw new OperacaoNaoPermitidaException(
                    "O usuário está inativo."
            );
        }

        if (item.exemplaresDisponiveis() > 0) {
            throw new OperacaoNaoPermitidaException(
                    "Existem exemplares disponíveis para empréstimo."
            );
        }

        FilaDeReservas fila = obterFila(codigoDoItem);

        if (fila.possuiReservaDoUsuario(matricula)) {
            throw new OperacaoNaoPermitidaException(
                    "O usuário já possui uma reserva para este item."
            );
        }

        Reserva reserva = new Reserva(
                novoId(),
                codigoDoItem,
                usuario,
                data
        );

        fila.entrar(reserva);

        return reserva.getId();
    }

    @Override
    public List<ReservaView> listarReservas(String codigoDoItem) throws RecursoNaoEncontradoException {
        if (codigoDoItem == null || codigoDoItem.isBlank()) {
            throw new DadosInvalidosException(
                    "O código do item é obrigatório."
            );
        }

        acervo.buscarItemInterno(codigoDoItem);

        FilaDeReservas fila = filasPorItem.get(codigoDoItem);

        if (fila == null) {
            return List.of();
        }

        List<ReservaView> resultado = new ArrayList<>();

        int posicao = 1;

        for (Reserva reserva : fila.getReservas()) {
            resultado.add(new ReservaView(
                    reserva.getId(),
                    reserva.getCodigoDoItem(),
                    reserva.getUsuario().getMatricula(),
                    reserva.getDataDaReserva(),
                    posicao
            ));

            posicao++;
        }

        return resultado;
    }

    @Override
    public void cancelarReserva(String idDaReserva) throws RecursoNaoEncontradoException {
        if (idDaReserva == null || idDaReserva.isBlank()) {
            throw new DadosInvalidosException(
                    "O ID da reserva é obrigatório."
            );
        }

        Reserva reservaCancelada = null;
        FilaDeReservas filaDaReserva = null;

        for (FilaDeReservas fila : filasPorItem.values()) {
            Reserva encontrada = fila.remover(idDaReserva);

            if (encontrada != null) {
                reservaCancelada = encontrada;
                filaDaReserva = fila;
                break;
            }
        }

        if (reservaCancelada == null) {
            throw new RecursoNaoEncontradoException(
                    "Reserva não encontrada: " + idDaReserva
            );
        }

        Exemplar exemplar = reservaCancelada.getExemplarSeparado();

        if (exemplar != null) {
            reservaCancelada.liberarExemplar();

            Reserva proximaReserva = filaDaReserva.proxima();

            if (proximaReserva != null) {
                proximaReserva.atribuirExemplar(exemplar);
            }
        }
    }
}
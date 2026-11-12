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

public class EmprestimosEmMemoria implements EmprestimoService {

    private final AcervoEmMemoria acervo;
    private final UsuariosEmMemoria usuarios;
    private final List<Emprestimo> emprestimos = new ArrayList<>();
    private int proximoId = 1;

    public EmprestimosEmMemoria(AcervoEmMemoria acervo,
                                UsuariosEmMemoria usuarios) {
        this.acervo = acervo;
        this.usuarios = usuarios;
    }

    private String novoId() {
        return String.valueOf(proximoId++);
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

        if (!exemplar.podeSerEmprestadoPara(usuario)) {
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
        emprestimoEncontrado.getExemplar()
                .setStatus(StatusExemplar.DISPONIVEL);
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
        return "";
    }

    @Override
    public List<ReservaView> listarReservas(String codigoDoItem) throws RecursoNaoEncontradoException {
        return List.of();
    }

    @Override
    public void cancelarReserva(String idDaReserva) throws RecursoNaoEncontradoException {

    }
}
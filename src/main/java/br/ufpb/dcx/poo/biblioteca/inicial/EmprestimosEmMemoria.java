package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;

import java.time.LocalDate;
import java.util.List;

public class EmprestimosEmMemoria implements EmprestimoService {

    private final AcervoEmMemoria acervo;
    private final UsuariosEmMemoria usuarios;
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
        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        
        return emprestimo.getId();
    }

    @Override
    public void devolver(String tombo, LocalDate data) throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {

    }

    @Override
    public List<EmprestimoView> listarEmprestimosDoUsuario(String matricula) throws RecursoNaoEncontradoException {
        return List.of();
    }

    @Override
    public List<EmprestimoView> listarAtrasados(LocalDate data) {
        return List.of();
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
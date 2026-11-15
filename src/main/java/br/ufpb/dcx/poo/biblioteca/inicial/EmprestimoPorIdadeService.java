
package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;

import java.time.LocalDate;

public class EmprestimoPorIdadeService {

    private final AcervoEmMemoria acervo;
    private final UsuariosEmMemoria usuarios;
    private final EmprestimoService emprestimos;

    public EmprestimoPorIdadeService(
            AcervoEmMemoria acervo,
            UsuariosEmMemoria usuarios,
            EmprestimoService emprestimos) {

        this.acervo = acervo;
        this.usuarios = usuarios;
        this.emprestimos = emprestimos;
    }

    public String emprestar(
            String tombo,
            String matricula,
            LocalDate data)
            throws RecursoNaoEncontradoException,
            ExemplarIndisponivelException,
            UsuarioInativoException,
            LimiteDeEmprestimosExcedidoException {

        if (tombo == null || tombo.isBlank()) {
            throw new DadosInvalidosException("O tombo é obrigatório.");
        }

        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }

        if (data == null) {
            throw new DadosInvalidosException(
                    "A data do empréstimo é obrigatória.");
        }

        Exemplar exemplar = acervo.buscarExemplarPorTombo(tombo);

        if (exemplar == null) {
            throw new RecursoNaoEncontradoException(
                    "Exemplar não encontrado: " + tombo);
        }

        Usuario usuario = usuarios.buscarUsuarioPorMatricula(matricula);

        if (usuario == null) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado: " + matricula);
        }

        int idade = usuario.calcularIdade(data);

        if (idade < exemplar.getClassificacaoEtaria().getIdadeMinima()) {
            throw new DadosInvalidosException(
                    "O usuário não possui idade suficiente para este exemplar.");
        }

        return emprestimos.emprestar(tombo, matricula, data);
    }

}


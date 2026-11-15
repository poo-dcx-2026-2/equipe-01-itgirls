
package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoPorIdadeServiceTest {

    @Test
    void deveBloquearEmprestimoQuandoUsuarioForMenorQueClassificacao() throws Exception {
        AcervoEmMemoria acervo = new AcervoEmMemoria();
        UsuariosEmMemoria usuarios = new UsuariosEmMemoria();

        acervo.cadastrarItem("L001", "Livro de teste", "Autor", "Literatura", 2020);
        acervo.adicionarExemplar("L001", "T001");

        Exemplar exemplar = acervo.buscarExemplarPorTombo("T001");
        exemplar.setClassificacaoEtaria(ClassificacaoEtaria.IDADE_16);

        usuarios.cadastrarUsuarioComNascimento(
                "U001", "João", LocalDate.of(2010, 11, 15));

        EmprestimosEmMemoria emprestimos =
                new EmprestimosEmMemoria(acervo, usuarios);

        EmprestimoPorIdadeService servico =
                new EmprestimoPorIdadeService(acervo, usuarios, emprestimos);

        assertThrows(DadosInvalidosException.class,
                () -> servico.emprestar(
                        "T001", "U001", LocalDate.of(2026, 11, 14)));
    }

    @Test
    void devePermitirEmprestimoQuandoUsuarioTiverIdadeSuficiente() throws Exception {
        AcervoEmMemoria acervo = new AcervoEmMemoria();
        UsuariosEmMemoria usuarios = new UsuariosEmMemoria();

        acervo.cadastrarItem("L001", "Livro de teste", "Autor", "Literatura", 2020);
        acervo.adicionarExemplar("L001", "T001");

        Exemplar exemplar = acervo.buscarExemplarPorTombo("T001");
        exemplar.setClassificacaoEtaria(ClassificacaoEtaria.IDADE_16);

        usuarios.cadastrarUsuarioComNascimento(
                "U001", "Maria", LocalDate.of(2000, 1, 1));

        EmprestimosEmMemoria emprestimos =
                new EmprestimosEmMemoria(acervo, usuarios);

        EmprestimoPorIdadeService servico =
                new EmprestimoPorIdadeService(acervo, usuarios, emprestimos);

        String id = servico.emprestar(
                "T001", "U001", LocalDate.of(2026, 11, 14));

        assertNotNull(id);
    }
}

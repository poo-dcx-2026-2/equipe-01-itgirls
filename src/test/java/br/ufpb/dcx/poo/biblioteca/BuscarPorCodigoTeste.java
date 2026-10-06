package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.inicial.AcervoEmMemoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BuscarPorCodigoTeste {

    @Test
    void deveEncontrarItemMesmoQuandoCodigoEhOutroObjetoString() throws RecursoNaoEncontradoException, RecursoDuplicadoException {

        AcervoService sistema = new AcervoEmMemoria();

        sistema.cadastrarItem(
                "L001",
                "Clean Code",
                "Robert C. Martin",
                "Programação",
                2008
        );

        String codigoBusca = new String("L001");

        assertNotNull(sistema.buscarItem(codigoBusca));
    }
}
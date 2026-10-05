package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.inicial.AcervoEmMemoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BuscarPorCodigoTeste {
    @Test

    void deveEncontrarItemMesmoQuandoCodigoEhOutroObjetoString() throws RecursoNaoEncontradoException {
        SistemaBiblioteca sistema = new SistemaBiblioteca();
        sistema.cadastrarItem("L001", "Clean Code");


        String codigoBusca = new String("L001");

        AcervoService sistemas = null;
        assertNotNull(sistemas.buscarItem(codigoBusca));
     }


}




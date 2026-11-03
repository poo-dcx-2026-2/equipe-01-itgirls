import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import br.ufpb.dcx.poo.biblioteca.inicial.AcervoEmMemoria;

public class BuscarPorCodigoTeste {

    @Test
    void deveEncontrarItemMesmoQuandoCodigoEhOutroObjetoString()
            throws Exception {

        AcervoEmMemoria sistema = new AcervoEmMemoria();

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




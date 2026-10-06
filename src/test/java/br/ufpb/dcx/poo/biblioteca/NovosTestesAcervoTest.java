package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.inicial.AcervoEmMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NovosTestesAcervoTest {

    private AcervoService acervo;

    @BeforeEach
    void setUp(){
        acervo = new AcervoEmMemoria();
    }

    @Test
    @DisplayName("1.   Entradas invalidas, Deve lancçar DadosInvalisException quando o código ou o título foram nulos ou vazios")
    void deveLancarExcecaoParaEntradasInvalidas(){
        String Autor;
        assertThrows(DadosInvalidosException.class, () ->
                acervo.cadastrarItem("", "Título Válido", "Autor", "Categoria", 2024));
                        assertThrows(DadosInvalidosException.class, () ->
                                acervo.cadastrarItem("L001", "   ", "Autor", "Categoria", 2024));

        assertThrows(DadosInvalidosException.class, () ->
                acervo.cadastrarItem(null, "Título Válido", "Autor", "Categoria", 2024));
    }

    @Test
    @DisplayName("2. Duplicidade - Deve lançar RecursoDuplicadoException ao cadastrar dois itens com o mesmo código")
    void deveLancarExcecaoAoCadastrarCodigoDuplicado() throws Exception {
        acervo.cadastrarItem("L001", "Clean Code", "Robert C. Martin", "Tecnologia", 2008);

        assertThrows(RecursoDuplicadoException.class, () ->
                acervo.cadastrarItem("L001", "Arquitetura Limpa", "Robert C. Martin", "Tecnologia", 2017));
    }

    @Test
    @DisplayName("3. Ordenação - Deve retornar os itens ordenados alfabeticamente por título")
    void deveRetornarItensOrdenadosPorTitulo() throws Exception {
        acervo.cadastrarItem("L003", "Refatoração", "Martin Fowler", "Tecnologia", 1999);
        acervo.cadastrarItem("L001", "Clean Code", "Robert C. Martin", "Tecnologia", 2008);
        acervo.cadastrarItem("L002", "Algoritmos", "Thomas Cormen", "Tecnologia", 2009);

        List<ItemView> itens = acervo.listarItens();

        assertEquals(3, itens.size());
        assertEquals("Algoritmos", itens.get(0).titulo());
        assertEquals("Clean Code", itens.get(1).titulo());
        assertEquals("Refatoração", itens.get(2).titulo());
    }

    @Test
    @DisplayName("4. Buscas sem resultado - Deve retornar lista vazia ao buscar por título inexistente")
    void deveRetornarListaVaziaParaBuscaSemResultado() throws Exception {
        acervo.cadastrarItem("L001", "Clean Code", "Robert C. Martin", "Tecnologia", 2008);

        List<ItemView> resultado = acervo.buscarPorTitulo("Título Que Não Existe");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("5. Exceções - Deve lançar RecursoNaoEncontradoException ao buscar código inexistente")
    void deveLancarExcecaoParaCodigoInexistente() {
        assertThrows(RecursoNaoEncontradoException.class, () ->
                acervo.buscarItem("CODIGO_INEXISTENTE"));
    }

    @Test
    @DisplayName("6. Regressão do Bug - Busca por código deve funcionar com nova instância de String")
    void testeDeRegressaoComNovaString() throws Exception {
        acervo.cadastrarItem("L001", "Clean Code", "Robert C. Martin", "Tecnologia", 2008);

        String codigoBusca = new String("L001");
        ItemView item = acervo.buscarItem(codigoBusca);

        assertNotNull(item);
        assertEquals("Clean Code", item.titulo());
    }
}




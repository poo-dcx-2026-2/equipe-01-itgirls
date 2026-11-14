package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
import br.ufpb.dcx.poo.biblioteca.persistencia.ArquivoDeItens;
import br.ufpb.dcx.poo.biblioteca.persistencia.LinhaDeItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArquivoDeItensTest {

    @TempDir
    Path pasta;

    @Test
    void itensGravadosVoltamIguaisNaLeitura() throws BibliotecaException {
        ArquivoDeItens arquivo = new ArquivoDeItens(pasta);
        List<LinhaDeItem> itens = List.of(
                new LinhaDeItem("L1", "Refatoração", "Fowler", "livro", 2004),
                new LinhaDeItem("J1", "Carcassonne", "Wrede", "jogo", 2000));

        arquivo.gravar(itens);

        assertEquals(itens, new ArquivoDeItens(pasta).ler());
    }

    @Test
    void arquivoAusenteEquivaleAAcervoVazio() throws BibliotecaException {
        assertTrue(new ArquivoDeItens(pasta).ler().isEmpty());
    }

    @Test
    void anoInvalidoNoArquivoLancaPersistenciaException() throws IOException {
        Files.writeString(pasta.resolve("itens.txt"),
                "L1;Refatoração;Fowler;livro;dois mil\n");

        PersistenciaException erro = assertThrows(PersistenciaException.class,
                () -> new ArquivoDeItens(pasta).ler());
        assertTrue(erro.getCause() instanceof NumberFormatException);
    }
}
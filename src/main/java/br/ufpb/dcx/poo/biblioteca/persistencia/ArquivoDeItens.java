package br.ufpb.dcx.poo.biblioteca.persistencia;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArquivoDeItens {

    private static final String SEPARADOR = ";";
    private final Path arquivo;

    public ArquivoDeItens(Path diretorio) {
        this.arquivo = diretorio.resolve("itens.txt");
    }

    public void gravar(List<LinhaDeItem> itens) throws PersistenciaException {
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            try (BufferedWriter saida =
                         Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
                for (LinhaDeItem item : itens) {
                    saida.write(String.join(SEPARADOR,
                            item.codigo(),
                            item.titulo(),
                            item.autoria(),
                            item.categoria(),
                            String.valueOf(item.ano())));
                    saida.newLine();
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível gravar " + arquivo, e);
        }
    }

    public List<LinhaDeItem> ler() throws PersistenciaException {
        List<LinhaDeItem> itens = new ArrayList<>();
        if (Files.notExists(arquivo)) {
            return itens;
        }
        try (BufferedReader entrada =
                     Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            int numero = 0;
            while ((linha = entrada.readLine()) != null) {
                numero++;
                if (!linha.isBlank()) {
                    itens.add(interpretar(linha, numero));
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível ler " + arquivo, e);
        }
        return itens;
    }

    private LinhaDeItem interpretar(String linha, int numero)
            throws PersistenciaException {
        String[] campos = linha.split(SEPARADOR, -1);
        if (campos.length != 5) {
            throw new PersistenciaException(
                    "Linha " + numero + " mal formada: " + linha);
        }
        try {
            return new LinhaDeItem(campos[0], campos[1], campos[2], campos[3],
                    Integer.parseInt(campos[4]));
        } catch (NumberFormatException e) {
            throw falha("Ano inválido na linha " + numero, e);
        }
    }

    private static PersistenciaException falha(String mensagem, Exception causa) {
        PersistenciaException excecao = new PersistenciaException(mensagem);
        excecao.initCause(causa);
        return excecao;
    }
}
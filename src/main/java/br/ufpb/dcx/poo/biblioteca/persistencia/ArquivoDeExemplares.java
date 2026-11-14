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

public class ArquivoDeExemplares {

    private static final String SEPARADOR = ";";
    private final Path arquivo;

    public ArquivoDeExemplares(Path diretorio) {
        this.arquivo = diretorio.resolve("exemplares.txt");
    }

    public void gravar(List<LinhaDeExemplar> exemplares) throws PersistenciaException {
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            try (BufferedWriter saida =
                         Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
                for (LinhaDeExemplar ex : exemplares) {
                    saida.write(String.join(SEPARADOR,
                            ex.tombo(),
                            ex.codigoItem(),
                            ex.status()));
                    saida.newLine();
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível gravar " + arquivo, e);
        }
    }

    public List<LinhaDeExemplar> ler() throws PersistenciaException {
        List<LinhaDeExemplar> exemplares = new ArrayList<>();
        if (Files.notExists(arquivo)) {
            return exemplares;
        }
        try (BufferedReader entrada =
                     Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            int numero = 0;
            while ((linha = entrada.readLine()) != null) {
                numero++;
                if (!linha.isBlank()) {
                    exemplares.add(interpretar(linha, numero));
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível ler " + arquivo, e);
        }
        return exemplares;
    }

    private LinhaDeExemplar interpretar(String linha, int numero)
            throws PersistenciaException {
        String[] campos = linha.split(SEPARADOR, -1);
        if (campos.length != 3) {
            throw new PersistenciaException(
                    "Linha " + numero + " mal formada: " + linha);
        }
        return new LinhaDeExemplar(campos[0], campos[1], campos[2]);
    }

    private static PersistenciaException falha(String mensagem, Exception causa) {
        PersistenciaException excecao = new PersistenciaException(mensagem);
        excecao.initCause(causa);
        return excecao;
    }
}
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

public class ArquivoDeReservas {

    private static final String SEPARADOR = ";";
    private final Path arquivo;

    public ArquivoDeReservas(Path diretorio) {
        this.arquivo = diretorio.resolve("reservas.txt");
    }

    public void gravar(List<LinhaDeReserva> reservas) throws PersistenciaException {
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            try (BufferedWriter saida =
                         Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
                for (LinhaDeReserva res : reservas) {
                    saida.write(String.join(SEPARADOR,
                            res.id(),
                            res.codigoItem(),
                            res.matricula(),
                            res.dataReserva(),
                            res.tomboSeparado() == null ? "" : res.tomboSeparado()));
                    saida.newLine();
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível gravar " + arquivo, e);
        }
    }

    public List<LinhaDeReserva> ler() throws PersistenciaException {
        List<LinhaDeReserva> reservas = new ArrayList<>();
        if (Files.notExists(arquivo)) {
            return reservas;
        }
        try (BufferedReader entrada =
                     Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            int numero = 0;
            while ((linha = entrada.readLine()) != null) {
                numero++;
                if (!linha.isBlank()) {
                    reservas.add(interpretar(linha, numero));
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível ler " + arquivo, e);
        }
        return reservas;
    }

    private LinhaDeReserva interpretar(String linha, int numero)
            throws PersistenciaException {
        String[] campos = linha.split(SEPARADOR, -1);
        if (campos.length != 5) {
            throw new PersistenciaException(
                    "Linha " + numero + " mal formada: " + linha);
        }
        String tombo = campos[4].isEmpty() ? null : campos[4];
        return new LinhaDeReserva(campos[0], campos[1], campos[2], campos[3], tombo);
    }

    private static PersistenciaException falha(String mensagem, Exception causa) {
        PersistenciaException excecao = new PersistenciaException(mensagem);
        excecao.initCause(causa);
        return excecao;
    }
}
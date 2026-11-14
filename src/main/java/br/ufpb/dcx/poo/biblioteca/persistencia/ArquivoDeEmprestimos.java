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

public class ArquivoDeEmprestimos {

    private static final String SEPARADOR = ";";
    private final Path arquivo;

    public ArquivoDeEmprestimos(Path diretorio) {
        this.arquivo = diretorio.resolve("emprestimos.txt");
    }

    public void gravar(List<LinhaDeEmprestimo> emprestimos) throws PersistenciaException {
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            try (BufferedWriter saida =
                         Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
                for (LinhaDeEmprestimo emp : emprestimos) {
                    saida.write(String.join(SEPARADOR,
                            emp.id(),
                            emp.tombo(),
                            emp.matricula(),
                            emp.dataEmprestimo(),
                            emp.dataPrevista(),
                            emp.dataDevolucao() == null ? "" : emp.dataDevolucao()));
                    saida.newLine();
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível gravar " + arquivo, e);
        }
    }

    public List<LinhaDeEmprestimo> ler() throws PersistenciaException {
        List<LinhaDeEmprestimo> emprestimos = new ArrayList<>();
        if (Files.notExists(arquivo)) {
            return emprestimos;
        }
        try (BufferedReader entrada =
                     Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            int numero = 0;
            while ((linha = entrada.readLine()) != null) {
                numero++;
                if (!linha.isBlank()) {
                    emprestimos.add(interpretar(linha, numero));
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível ler " + arquivo, e);
        }
        return emprestimos;
    }

    private LinhaDeEmprestimo interpretar(String linha, int numero)
            throws PersistenciaException {
        String[] campos = linha.split(SEPARADOR, -1);
        if (campos.length != 6) {
            throw new PersistenciaException(
                    "Linha " + numero + " mal formada: " + linha);
        }
        String devolucao = campos[5].isEmpty() ? null : campos[5];
        return new LinhaDeEmprestimo(campos[0], campos[1], campos[2],
                campos[3], campos[4], devolucao);
    }

    private static PersistenciaException falha(String mensagem, Exception causa) {
        PersistenciaException excecao = new PersistenciaException(mensagem);
        excecao.initCause(causa);
        return excecao;
    }
}
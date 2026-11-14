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

public class ArquivoDeUsuarios {

    private static final String SEPARADOR = ";";
    private final Path arquivo;

    public ArquivoDeUsuarios(Path diretorio) {
        this.arquivo = diretorio.resolve("usuarios.txt");
    }

    public void gravar(List<LinhaDeUsuario> usuarios) throws PersistenciaException {
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            try (BufferedWriter saida =
                         Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
                for (LinhaDeUsuario u : usuarios) {
                    saida.write(String.join(SEPARADOR,
                            u.matricula(),
                            u.nome(),
                            String.valueOf(u.ativo())));
                    saida.newLine();
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível gravar " + arquivo, e);
        }
    }

    public List<LinhaDeUsuario> ler() throws PersistenciaException {
        List<LinhaDeUsuario> usuarios = new ArrayList<>();
        if (Files.notExists(arquivo)) {
            return usuarios;
        }
        try (BufferedReader entrada =
                     Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            int numero = 0;
            while ((linha = entrada.readLine()) != null) {
                numero++;
                if (!linha.isBlank()) {
                    usuarios.add(interpretar(linha, numero));
                }
            }
        } catch (IOException e) {
            throw falha("Não foi possível ler " + arquivo, e);
        }
        return usuarios;
    }

    private LinhaDeUsuario interpretar(String linha, int numero)
            throws PersistenciaException {
        String[] campos = linha.split(SEPARADOR, -1);
        if (campos.length != 3) {
            throw new PersistenciaException(
                    "Linha " + numero + " mal formada: " + linha);
        }
        if (!"true".equalsIgnoreCase(campos[2]) && !"false".equalsIgnoreCase(campos[2])) {
            throw new PersistenciaException(
                    "Situação inválida na linha " + numero + ": " + campos[2]);
        }
        return new LinhaDeUsuario(campos[0], campos[1], Boolean.parseBoolean(campos[2]));
    }

    private static PersistenciaException falha(String mensagem, Exception causa) {
        PersistenciaException excecao = new PersistenciaException(mensagem);
        excecao.initCause(causa);
        return excecao;
    }
}
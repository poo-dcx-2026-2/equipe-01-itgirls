package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    // A matrícula e o nome não podem ser alterados depois que o usuário é criado.
    private final String matricula;
    private final String nome;

    // Indica se o usuário está ativo na biblioteca.
    private boolean ativo;

    // Guarda a quantidade de empréstimos que o usuário possui atualmente
    private final List<Emprestimo> emprestimos = new ArrayList<>();


    public Usuario(String matricula, String nome) {

        // Verifica se a matrícula foi informada.
        // Caso esteja nula ou vazia, lança uma exceção.
        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }

        // Verifica se o nome foi informado.
        // Caso esteja nulo ou vazio, lança uma exceção.
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome é obrigatório.");
        }

        // Guarda os dados recebidos nos atributos do usuário.
        this.matricula = matricula;
        this.nome = nome;

        // Todo usuário começa ativo ao ser cadastrado.
        this.ativo = true;

    }

    // Retorna a matrícula do usuário.
    public String getMatricula() {
        return matricula;
    }

    // Retorna o nome do usuário.
    public String getNome() {
        return nome;
    }

    // Retorna se o usuário está ativo.
    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // Padrão Especialista (Passo 7): calcula os ativos em tempo real
    public int getEmprestimosAtivos() {
        int contador = 0;
        for (Emprestimo e : emprestimos) {
            if (e.estaAtivo()) {
                contador++;
            }
        }
        return contador;
    }

}

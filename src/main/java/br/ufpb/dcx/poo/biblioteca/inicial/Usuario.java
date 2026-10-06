package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

public class Usuario {

    // A matrícula e o nome não podem ser alterados depois que o usuário é criado.
    private final String matricula;
    private final String nome;

    // Indica se o usuário está ativo na biblioteca.
    private boolean ativo;

    // Guarda a quantidade de empréstimos que o usuário possui atualmente.
    private int emprestimosAtivos;


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

        // Todo usuário começa sem empréstimos ativos.
        this.emprestimosAtivos = 0;
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

    // Retorna a quantidade de empréstimos ativos do usuário.
    public int getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    // Adiciona um empréstimo à quantidade de empréstimos ativos.
    public void incrementEmprestimosAtivos() {
        this.emprestimosAtivos++;
    }

    // Diminui a quantidade de empréstimos ativos.
    public void decrementEmprestimosAtivos() {

        // Só diminui se houver pelo menos um empréstimo ativo.
        // Isso evita que a quantidade fique negativa.
        if (this.emprestimosAtivos > 0) {
            this.emprestimosAtivos--;
        }
    }
}
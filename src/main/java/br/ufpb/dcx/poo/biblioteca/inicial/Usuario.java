package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;

public class Usuario {

    // A matrícula e o nome não podem ser alterados depois que o usuário é criado.
    private final String matricula;
    private final String nome;

    // Indica se o usuário está ativo na biblioteca.
    private boolean ativo;

    // Guarda a quantidade de empréstimos que o usuário possui atualmente
    private final List<Emprestimo> emprestimos = new ArrayList<>();

    private final LocalDate dataNascimento;

    public Usuario(String matricula, String nome) {
        this(matricula, nome, null, false);
    }

    public Usuario(String matricula, String nome, LocalDate dataNascimento) {
        this(matricula, nome, dataNascimento, true);
    }

    private Usuario(String matricula, String nome,
                    LocalDate dataNascimento, boolean exigirNascimento) {

        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }

        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome é obrigatório.");
        }

        if (exigirNascimento && dataNascimento == null) {
            throw new DadosInvalidosException("A data de nascimento é obrigatória.");
        }

        if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
            throw new DadosInvalidosException("A data de nascimento não poderá estar no futuro.");
        }

        this.matricula = matricula;
        this.nome = nome;
        this.ativo = true;
        this.dataNascimento = dataNascimento;
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

    public void adicionarEmprestimo(Emprestimo emprestimo) {
        emprestimos.add(emprestimo);
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }


    public int calcularIdade(LocalDate dataReferencia) {
        if (dataReferencia == null || dataReferencia.isBefore(dataNascimento)) {
            throw new DadosInvalidosException("Data de referência inválida.");
        }

        return Period.between(dataNascimento, dataReferencia).getYears();
    }

}
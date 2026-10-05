package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

public class Usuario {

    private final String matricula;
    private final String nome;

    private boolean ativo;
    private int emprestimosAtivos;


    public Usuario(String matricula, String nome) {
        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("A matrícula é obrigatória.");
        }
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome é obrigatório.");
        }
        this.matricula = matricula;
        this.nome = nome;

        this.ativo = true;
        this.emprestimosAtivos = 0;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public int getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    public void incrementEmprestimosAtivos() {
        this.emprestimosAtivos++;
    }
    public void decrementEmprestimosAtivos(){
        if (this.emprestimosAtivos > 0) {
            this.emprestimosAtivos--;
        }
    }



}
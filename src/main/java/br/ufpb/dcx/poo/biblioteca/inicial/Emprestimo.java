package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;

public class Emprestimo {
    private StatusEmprestimo status;

    public Emprestimo() {
        this.status = StatusEmprestimo.ATIVO;
    }

    public boolean estaAtivo() {
        return this.status == StatusEmprestimo.ATIVO;
    }
}
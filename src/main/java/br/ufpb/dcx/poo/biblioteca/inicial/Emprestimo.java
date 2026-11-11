package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;

import java.time.LocalDate;

public class Emprestimo {
    private StatusEmprestimo status;
    private LocalDate dataDevolucaoPrevista;
    private boolean ativo = true;

    public Emprestimo(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public boolean estaAtivo() {
        return ativo;
    }

    public boolean estaAtrasado(LocalDate dataAtual) {
        return estaAtivo() && dataAtual.isAfter(dataDevolucaoPrevista);
    }
}
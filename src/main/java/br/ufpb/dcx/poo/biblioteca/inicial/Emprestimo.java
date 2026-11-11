
package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

import java.time.LocalDate;

public class Emprestimo {

    private final String id;
    private final Exemplar exemplar;
    private final Usuario usuario;
    private final LocalDate dataDoEmprestimo;
    private final LocalDate dataPrevistaDeDevolucao;
    private LocalDate dataDaDevolucao;

    public Emprestimo(String id, Exemplar exemplar, Usuario usuario,
                      LocalDate data) {

        this.id = id;
        this.exemplar = exemplar;
        this.usuario = usuario;
        this.dataDoEmprestimo = data;

        this.dataPrevistaDeDevolucao =
                data.plusDays(EmprestimoService.PRAZO_EM_DIAS);
    }

    public boolean estaAtivo() {
        return dataDaDevolucao == null;
    }

    public boolean estaAtrasadoEm(LocalDate data) {
        return estaAtivo() && data.isAfter(dataPrevistaDeDevolucao);
    }

    public void registrarDevolucao(LocalDate data) {
        if (data.isBefore(dataDoEmprestimo)) {
            throw new DadosInvalidosException(
                    "Devolução anterior ao empréstimo."
            );
        }

        this.dataDaDevolucao = data;
    }

    public String getId() {
        return id;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getDataDoEmprestimo() {
        return dataDoEmprestimo;
    }

    public LocalDate getDataPrevistaDeDevolucao() {
        return dataPrevistaDeDevolucao;
    }

    public LocalDate getDataDaDevolucao() {
        return dataDaDevolucao;
    }
}
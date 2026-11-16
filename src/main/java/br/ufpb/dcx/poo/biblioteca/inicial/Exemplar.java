package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

public class Exemplar {

    private final String tombo;
    private final String codigoDoItem;
    private StatusExemplar status;
    private ClassificacaoEtaria classificacaoEtaria;

    public Exemplar(String tombo, String codigoDoItem) {
        if (tombo == null || tombo.isBlank()) {
            throw new DadosInvalidosException("Tombo é obrigatório.");
        }
        if (codigoDoItem == null || codigoDoItem.isBlank()) {
            throw new DadosInvalidosException("O código do item é obrigatorio.");
        }

        this.tombo = tombo;
        this.codigoDoItem = codigoDoItem;
        this.status = StatusExemplar.DISPONIVEL;
        this.classificacaoEtaria = ClassificacaoEtaria.LIVRE;
    }



    public String getTombo() { return tombo; }

    public String getCodigoDoItem() { return codigoDoItem; }

    public StatusExemplar getStatus() { return status; }

    public boolean podeSerEmprestadoPara(Usuario usuario) {
        return status == StatusExemplar.DISPONIVEL;
    }

    public void setStatus(StatusExemplar status) {
        this.status = status;
    }

    public ClassificacaoEtaria getClassificacaoEtaria() {
        return classificacaoEtaria;
    }

    public void setClassificacaoEtaria(ClassificacaoEtaria classificacaoEtaria) {
        if (classificacaoEtaria == null) {
            throw new DadosInvalidosException("A classificação etária é obrigatória.");
        }

        this.classificacaoEtaria = classificacaoEtaria;
    }
}
package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

/**
 * A cópia física de um item. O que se empresta é o exemplar, não o item.
 *
 * <p>Ponto de partida, como {@link Item}.</p>
 */
public class Exemplar {

    //tombo não é final, precisa ser

    private final String tombo;
    private final String codigoDoItem;
    private StatusExemplar status;

    //falta exceçoes
    //fazer um codigoDoItem, inves de salvar o item todo

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
    }

    public String getTombo() { return tombo; }
    public String getCodigoDoItem() { return codigoDoItem; }
    public StatusExemplar getStatus() { return status; }
    public void setStatus(StatusExemplar status) {
        this.status = status;
    }

    //set desnecessario de novo
    //
}

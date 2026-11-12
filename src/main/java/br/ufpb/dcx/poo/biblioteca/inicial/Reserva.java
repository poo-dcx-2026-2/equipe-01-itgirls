package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

import java.time.LocalDate;

public class Reserva {

    private final String id;
    private final String codigoDoItem;
    private final Usuario usuario;
    private final LocalDate dataDaReserva;

    private Exemplar exemplarSeparado;

    public Reserva(String id, String codigoDoItem,
                   Usuario usuario, LocalDate dataDaReserva) {

        if (id == null || id.isBlank()) {
            throw new DadosInvalidosException("O ID é obrigatório.");
        }

        if (codigoDoItem == null || codigoDoItem.isBlank()) {
            throw new DadosInvalidosException("O código do item é obrigatório.");
        }

        if (usuario == null) {
            throw new DadosInvalidosException("O usuário é obrigatório.");
        }

        if (dataDaReserva == null) {
            throw new DadosInvalidosException("A data da reserva é obrigatória.");
        }

        this.id = id;
        this.codigoDoItem = codigoDoItem;
        this.usuario = usuario;
        this.dataDaReserva = dataDaReserva;
        this.exemplarSeparado = null;
    }

    public String getId() {
        return id;
    }

    public String getCodigoDoItem() {
        return codigoDoItem;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getDataDaReserva() {
        return dataDaReserva;
    }

    public Exemplar getExemplarSeparado() {
        return exemplarSeparado;
    }

    public void atribuirExemplar(Exemplar exemplar) {
        if (exemplar == null) {
            throw new DadosInvalidosException("O exemplar não pode ser nulo.");
        }

        this.exemplarSeparado = exemplar;
        exemplar.setStatus(StatusExemplar.RESERVADO);
    }

    public void liberarExemplar() {
        if (exemplarSeparado != null) {
            exemplarSeparado.setStatus(StatusExemplar.DISPONIVEL);
            exemplarSeparado = null;
        }
    }
}
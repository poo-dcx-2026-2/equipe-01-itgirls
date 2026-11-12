package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class FilaDeReservas {

    private final List<Reserva> reservas = new ArrayList<>();

    public void entrar(Reserva reserva) {
        reservas.add(reserva);
    }

    public Reserva proxima() {
        if (reservas.isEmpty()) {
            return null;
        }

        return reservas.get(0);
    }

    public Reserva remover() {
        if (reservas.isEmpty()) {
            return null;
        }

        return reservas.remove(0);
    }

    public Reserva remover(String idDaReserva) {
        for (int i = 0; i < reservas.size(); i++) {
            Reserva reserva = reservas.get(i);

            if (reserva.getId().equals(idDaReserva)) {
                return reservas.remove(i);
            }
        }

        return null;
    }

    public boolean possuiReservaDoUsuario(String matricula) {
        for (Reserva reserva : reservas) {
            if (reserva.getUsuario().getMatricula().equals(matricula)) {
                return true;
            }
        }

        return false;
    }

    public Reserva buscarReservaDoUsuarioComExemplar(
            String matricula, Exemplar exemplar) {

        for (Reserva reserva : reservas) {
            if (reserva.getUsuario().getMatricula().equals(matricula)
                    && reserva.getExemplarSeparado() == exemplar) {
                return reserva;
            }
        }

        return null;
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }
}
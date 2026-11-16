package br.ufpb.dcx.poo.biblioteca.inicial;

public enum ClassificacaoEtaria {

    LIVRE(0),
    IDADE_10(10),
    IDADE_12(12),
    IDADE_14(14),
    IDADE_16(16),
    IDADE_18(18);

    private final int idadeMinima;

    ClassificacaoEtaria(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public int getIdadeMinima() {
        return idadeMinima;
    }
}
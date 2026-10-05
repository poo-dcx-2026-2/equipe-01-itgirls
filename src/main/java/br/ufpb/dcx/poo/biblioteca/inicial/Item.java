package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Um item do acervo: um livro, um filme, um jogo, um instrumento.
 *
 * <p>Esta classe é ponto de partida, não modelo a seguir. Ela existe para que o
 * projeto compile e execute desde o primeiro dia. Ao longo do semestre você vai
 * decidir se ela permanece assim, se ganha invariantes, se vira uma hierarquia,
 * se delega responsabilidades ou se desaparece.</p>
 */
public class Item {

    //os atributos não são final, podem ser mudados, oq não deve acontecer
    //colocar como final

    private final String codigo;
    private final String titulo;
    private final String autoria;
    private final String categoria;
    private final int ano;

    private final List<Exemplar> exemplares = new ArrayList<>();

    //fazer exceçoes, tá aceitando qualquer item

    public Item(String codigo, String titulo, String autoria, String categoria, int ano) {

        if (codigo == null || codigo.isBlank()) {
            throw new DadosInvalidosException("O código é obrigatório.");
        }

        if (titulo == null || titulo.isBlank()) {
            throw new DadosInvalidosException("O título é obrigatório.");
        }

        this.codigo = codigo;
        this.titulo = titulo;
        this.autoria = autoria;
        this.categoria = categoria;
        this.ano = ano;
    }

    public String getCodigo() { return codigo; }

    public String getTitulo() { return titulo; }

    public String getAutoria() { return autoria; }

    public String getCategoria() { return categoria; }

    public int getAno() { return ano; }

    public void adicionarExemplar(Exemplar exemplar) throws RecursoDuplicadoException {
        if (exemplar == null){
            throw new DadosInvalidosException("O exemplar não pode ser nulo.");
        }

        for (Exemplar e : exemplares){
            if (e.getTombo().equals(exemplar.getTombo())){
                throw new RecursoDuplicadoException("Já exeiste um exemplaar com tombo:" + exemplar.getTombo());
            }
        }

    exemplares.add(exemplar);
    }

    public void removerExemplar(Exemplar exemplar){
        if (exemplar != null){
            this.exemplares.remove(exemplar);
        }
    }

    public int totalDeExemplares(){
        return exemplares.size();
    }

    public int exemplaresDisponiveis() {
        int quantidade = 0;

        for (Exemplar exemplar : exemplares) {
            if (exemplar.getStatus() == StatusExemplar.DISPONIVEL) {
                quantidade++;
            }
        }

        return quantidade;
    }

    public List<Exemplar> getExemplares() {
        return new ArrayList<>(exemplares);
    }



    //nao pode ter set e deve ter metodos proprios, os que tão lá no diagrama
    //nada de getExemplares
}

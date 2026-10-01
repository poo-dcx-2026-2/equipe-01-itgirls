package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.List;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

public class AcervoEmMemoria implements AcervoService {

    private final List<Item> itens = new ArrayList<>();

    @Override
    public void cadastrarItem(String codigo, String titulo, String autoria,
                              String categoria, int ano)
            throws RecursoDuplicadoException {

        exigirTextoPreenchido(codigo, "codigo");
        exigirTextoPreenchido(titulo, "titulo");

        if (localizar(codigo) != null) {
            throw new RecursoDuplicadoException("Já existe item com o código " + codigo);
        }
        itens.add(new Item(codigo, titulo, autoria, categoria, ano));
    }

    @Override
    public ItemView buscarItem(String codigo) throws RecursoNaoEncontradoException {
        Item item = localizar(codigo);
        if (item == null) {
            throw new RecursoNaoEncontradoException("Item não encontrado: " + codigo);
        }
        return paraView(item);
    }

    @Override
    public List<ItemView> listarItens() {
        List<ItemView> resultado = new ArrayList<>();
        for (Item item : itens) {
            resultado.add(paraView(item));
        }
        resultado.sort((a, b) -> a.titulo().compareToIgnoreCase(b.titulo()));
        return resultado;
    }

    @Override
    public List<ItemView> buscarPorTitulo(String trecho) {

        List<ItemView> resultado = new ArrayList<>();

        if (trecho == null) {
            return resultado;
        }

        String pesquisa = trecho.toLowerCase();

        for (Item item : itens) {

            if (item.getTitulo()
                    .toLowerCase()
                    .contains(pesquisa)) {

                resultado.add(paraView(item));
            }
        }

        resultado.sort(
                (a, b) ->
                        a.titulo().compareToIgnoreCase(b.titulo()));

        return resultado;
    }

    @Override
    public List<ItemView> buscarPorCategoria(String categoria) {

        List<ItemView> resultado = new ArrayList<>();

        if (categoria == null) {
            return resultado;
        }

        String pesquisa = categoria.toLowerCase();

        for (Item item : itens) {

            if (item.getCategoria()
                    .toLowerCase()
                    .equals(pesquisa)) {

                resultado.add(paraView(item));
            }
        }

        resultado.sort(
                (a, b) ->
                        a.titulo().compareToIgnoreCase(b.titulo())
        );

        return resultado;
    }

    @Override
    public void adicionarExemplar(String codigoDoItem, String tombo) throws RecursoNaoEncontradoException, RecursoDuplicadoException {

        Item item = localizar(codigoDoItem);

        if (item == null) {
            throw new RecursoNaoEncontradoException(
                    "Item não encontrado: " + codigoDoItem);
        }

        for (Item i : itens) {

            for (Exemplar ex : i.getExemplares()) {

                if (ex.getTombo().equals(tombo)) {

                    throw new RecursoDuplicadoException(
                            "Tombo já existe: " + tombo);
                }
            }
        }

        item.getExemplares()
                .add(new Exemplar(tombo, item));
    }

    @Override
    public List<ExemplarView> listarExemplares(String codigoDoItem) throws RecursoNaoEncontradoException {

        Item item = localizar(codigoDoItem);

        if (item == null) {
            throw new RecursoNaoEncontradoException(
                    "Item não encontrado: " + codigoDoItem);
        }

        List<ExemplarView> resultado = new ArrayList<>();

        for (Exemplar exemplar : item.getExemplares()) {

            resultado.add(
                    new ExemplarView(
                            exemplar.getTombo(),
                            item.getCodigo(),
                            exemplar.getStatus()
                    )
            );
        }

        return resultado;
    }

    @Override
    public void baixarExemplar(String tombo) throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {

        Exemplar exemplarEncontrado = null;
        Item itemDoExemplar = null;

        for (Item item : itens) {

            for (Exemplar exemplar : item.getExemplares()) {

                if (exemplar.getTombo().equals(tombo)) {

                    exemplarEncontrado = exemplar;
                    itemDoExemplar = item;
                    break;
                }
            }

            if (exemplarEncontrado != null) {
                break;
            }
        }

        if (exemplarEncontrado == null) {
            throw new RecursoNaoEncontradoException(
                    "Exemplar não encontrado: " + tombo);
        }

        if (exemplarEncontrado.getStatus() != StatusExemplar.DISPONIVEL) {
            throw new OperacaoNaoPermitidaException(
                    "Exemplar não pode ser baixado");
        }

        itemDoExemplar.getExemplares().remove(exemplarEncontrado);
    }

    /**
     * O erro oculto que encontramos foi "==" em "if (item.getCodigo() == codigo) {".
     *
     * Para String devemos usar: equals().
     *
     * Logo, ficará: "if (item.getCodigo().equals(codigo)) {"
     */

    private Item localizar(String codigo) {
        for (Item item : itens) {
            if (item.getCodigo().equals(codigo)) {
                return item;
            }
        }
        return null;
    }

    private ItemView paraView(Item item) {
        int disponiveis = 0;
        for (Exemplar exemplar : item.getExemplares()) {
            if (exemplar.getStatus() == StatusExemplar.DISPONIVEL) {
                disponiveis++;
            }
        }
        return new ItemView(
                item.getCodigo(),
                item.getTitulo(),
                item.getAutoria(),
                item.getCategoria(),
                item.getAno(),
                item.getExemplares().size(),
                disponiveis);
    }

    private static void exigirTextoPreenchido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
        }
    }

    /** Acesso interno usado pelos demais serviços da implementação inicial. */
    List<Item> itens() {
        return itens;
    }
}

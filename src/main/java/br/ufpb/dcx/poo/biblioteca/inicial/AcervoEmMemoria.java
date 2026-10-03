package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;


//dar uma olhada nesse map e nesses metodos

public class AcervoEmMemoria implements AcervoService {

    private final Map<String, Item> itens = new HashMap<>();
    private final Map<String, Exemplar> tombosGlobais = new HashMap<>();

    @Override
    public void cadastrarItem(String codigo, String titulo, String autoria,
                              String categoria, int ano)
            throws RecursoDuplicadoException {

        exigirTextoPreenchido(codigo, "codigo");
        exigirTextoPreenchido(titulo, "titulo");

        if (itens.containsKey(codigo)) {
            throw new RecursoDuplicadoException(
                    "Já existe item com o código " + codigo);
        }

        Item item = new Item(codigo, titulo, autoria, categoria, ano);
        itens.put(codigo, item);

    }

    @Override
    public ItemView buscarItem(String codigo)
            throws RecursoNaoEncontradoException {

        Item item = localizar(codigo);

        if (item == null) {
            throw new RecursoNaoEncontradoException(
                    "Item não encontrado: " + codigo);
        }

        return paraView(item);
    }

    @Override
    public List<ItemView> listarItens() {

        List<ItemView> resultado = new ArrayList<>();

        for (Item item : itens.values()) {
            resultado.add(paraView(item));
        }

        resultado.sort(
                (a, b) ->
                        a.titulo().compareToIgnoreCase(b.titulo()));

        return resultado;
    }

    @Override
    public List<ItemView> buscarPorTitulo(String trecho) {

        List<ItemView> resultado = new ArrayList<>();

        if (trecho == null) {
            return resultado;
        }

        String pesquisa = trecho.toLowerCase();

        for (Item item : itens.values()) {

            if (item.getTitulo()
                    .toLowerCase()
                    .contains(pesquisa)) {

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
    public List<ItemView> buscarPorCategoria(String categoria) {

        List<ItemView> resultado = new ArrayList<>();

        if (categoria == null) {
            return resultado;
        }

        for (Item item : itens.values()) {

            if (item.getCategoria() != null
                    && item.getCategoria().equalsIgnoreCase(categoria)) {

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
    public void adicionarExemplar(String codigoDoItem, String tombo)
            throws RecursoNaoEncontradoException, RecursoDuplicadoException {

        exigirTextoPreenchido(tombo, "tombo");

        Item item = localizar(codigoDoItem);

        if (item == null) {
            throw new RecursoNaoEncontradoException(
                    "Item não encontrado: " + codigoDoItem);
        }

        if (tombosGlobais.containsKey(tombo)) {
            throw new RecursoDuplicadoException(
                    "Tombo já cadastrado: " + tombo);
        }

        Exemplar exemplar = new Exemplar(tombo, codigoDoItem);

        item.adicionarExemplar(exemplar);

        tombosGlobais.put(tombo, exemplar);
    }

    @Override
    public List<ExemplarView> listarExemplares(String codigoDoItem)
            throws RecursoNaoEncontradoException {

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

        resultado.sort(
                (a, b) ->
                        a.tombo().compareToIgnoreCase(b.tombo())
        );

        return resultado;
    }

    @Override
    public void baixarExemplar(String tombo)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {

        Exemplar exemplarEncontrado = null;
        Item itemDoExemplar = null;

        for (Item item : itens.values()) {

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
     * O erro oculto que encontramos foi:
     * if (item.getCodigo() == codigo)
     *
     * Para String devemos usar equals().
     */

    private Item localizar(String codigo) {
        return  itens.get(codigo);
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
                disponiveis
        );
    }

    private static void exigirTextoPreenchido(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(
                    "O campo " + campo + " é obrigatório."
            );
        }
    }

    /** Acesso interno usado pelos demais serviços da implementação inicial. */
    List<Item> itens() {
        return new ArrayList<>(itens.values());
    }
}

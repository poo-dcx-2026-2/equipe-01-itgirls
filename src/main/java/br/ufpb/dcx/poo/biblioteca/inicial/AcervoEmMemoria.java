package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.*;

import br.ufpb.dcx.poo.biblioteca.contrato.*;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;


//dar uma olhada nesse map e nesses metodos

public class AcervoEmMemoria implements AcervoService {
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<String, Item> itens = new HashMap<>();
    private final Map<String, Exemplar> tombosGlobais = new HashMap<>();


    @Override
    public void cadastrarItem(String codigo, String titulo, String autoria,
                              String categoria, int ano)
            throws DadosInvalidosException, RecursoDuplicadoException {

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
            throws DadosInvalidosException,  RecursoNaoEncontradoException {

        exigirTextoPreenchido(codigo , "codigo");

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

        resultado.sort(Comparator.comparing(ItemView::titulo,String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    // os métodos a seguir não estavam implementados

    @Override
    public List<ItemView> buscarPorTitulo(String trecho) {

        // Cria uma lista vazia para guardar os itens encontrados.
        List<ItemView> resultado = new ArrayList<>();

        // Verifica se o trecho informado é nulo ou está vazio.
        // Nesse caso, retorna a lista vazia.
        if (trecho == null || trecho.isBlank()) {
            return resultado;
        }

        // Converte o trecho pesquisado para letras minúsculas.
        // Isso facilita a busca sem diferenciar maiúsculas de minúsculas.
        String pesquisa = trecho.toLowerCase();

        // Percorre todos os itens cadastrados no acervo.
        for (Item item : itens.values()) {

            // Verifica se o item possui título e se o título contém
            // o trecho pesquisado.
            if (item.getTitulo() != null &&
                    item.getTitulo().toLowerCase().contains(pesquisa)) {

                // Adiciona o item encontrado à lista de resultados.
                resultado.add(paraView(item));
            }
        }

        // Ordena os resultados pelo título, ignorando maiúsculas e minúsculas.
        resultado.sort((a, b) -> a.titulo().compareToIgnoreCase(b.titulo()));

        return resultado;
    }

    @Override
    public List<ItemView> buscarPorCategoria(String categoria) {

        List<ItemView> resultado = new ArrayList<>();

        if (categoria == null || categoria.isBlank()) {
            return resultado;
        }

        for (Item item : itens.values()) {

            // Verifica se o item possui categoria e se ela é igual
            // à categoria pesquisada, ignorando maiúsculas e minúsculas.
            if (item.getCategoria() != null &&
                    item.getCategoria().equalsIgnoreCase(categoria)) {

                resultado.add(paraView(item));
            }
        }

        // Ordena os resultados pelo título.
        resultado.sort((a, b) -> a.titulo().compareToIgnoreCase(b.titulo()));

        return resultado;
    }

    @Override
    public void adicionarExemplar(String codigoDoItem, String tombo)
            throws RecursoNaoEncontradoException, RecursoDuplicadoException, DadosInvalidosException {

        // Verifica se o código do item e o tombo foram preenchidos.
        exigirTextoPreenchido(codigoDoItem, "código do item");
        exigirTextoPreenchido(tombo, "tombo");

        // Procura o item pelo código informado.
        Item item = localizar(codigoDoItem);

        // Se o item não existir, lança uma exceção.
        if (item == null) {
            throw new RecursoNaoEncontradoException(
                    "Item não encontrado: " + codigoDoItem);
        }

        // Verifica se já existe outro exemplar com o mesmo tombo.
        if (tombosGlobais.containsKey(tombo)) {
            throw new RecursoDuplicadoException(
                    "Já existe um exemplar cadastrado com tombo: " + tombo);
        }

        // Cria um novo exemplar com o tombo e o código do item.
        Exemplar exemplar = new Exemplar(tombo, codigoDoItem);

        // Adiciona o exemplar ao item correspondente.
        item.adicionarExemplar(exemplar);

        // Adiciona o exemplar ao mapa global de tombos.
        tombosGlobais.put(tombo, exemplar);
    }

    @Override
    public List<ExemplarView> listarExemplares(String codigoDoItem)
            throws RecursoNaoEncontradoException, DadosInvalidosException {

        // Verifica se o código do item foi preenchido.
        exigirTextoPreenchido(codigoDoItem, "código do item");

        Item item = localizar(codigoDoItem);

        if (item == null) {
            throw new RecursoNaoEncontradoException(
                    "Item não encontrado com o código: " + codigoDoItem);
        }

        // Cria uma lista para guardar os exemplares do item.
        List<ExemplarView> resultado = new ArrayList<>();

        // Percorre todos os exemplares cadastrados no item.
        for (Exemplar exemplar : item.getExemplares()) {

            // Cria uma visualização do exemplar e adiciona à lista.
            resultado.add(
                    new ExemplarView(
                            exemplar.getTombo(),
                            item.getCodigo(),
                            exemplar.getStatus()
                    )
            );
        }

        resultado.sort((a, b) -> a.tombo().compareToIgnoreCase(b.tombo()));

        return resultado;
    }

    @Override
    public void baixarExemplar(String tombo)
            throws RecursoNaoEncontradoException,
            OperacaoNaoPermitidaException,
            DadosInvalidosException {

        // Verifica se o tombo foi preenchido.
        exigirTextoPreenchido(tombo, "tombo");

        // Procura o exemplar pelo tombo no mapa global.
        Exemplar exemplarEncontrado = tombosGlobais.get(tombo);

        if (exemplarEncontrado == null) {
            throw new RecursoNaoEncontradoException(
                    "Exemplar não encontrado: " + tombo);
        }

        // Verifica se o exemplar está disponível para ser baixado.
        // Um exemplar que não está disponível não pode ser removido.
        if (exemplarEncontrado.getStatus() != StatusExemplar.DISPONIVEL) {
            throw new OperacaoNaoPermitidaException(
                    "Exemplar não está disponível para ser baixado");
        }

        // Procura o item ao qual o exemplar pertence.
        Item itemDoExemplar =
                localizar(exemplarEncontrado.getCodigoDoItem());

        // Se o item for encontrado, remove o exemplar dele.
        if (itemDoExemplar != null) {
            itemDoExemplar.removerExemplar(exemplarEncontrado);
        }

        // Remove o exemplar do mapa global de tombos.
        tombosGlobais.remove(tombo);
    }

    @Override
    public void cadastrarUsuario(String matricula, String nome)
            throws DadosInvalidosException, RecursoDuplicadoException {
        exigirTextoPreenchido(matricula, "matricula");
        exigirTextoPreenchido(nome, "nome");

        if (usuarios.containsKey(matricula)) {
            throw new RecursoDuplicadoException(
                    "Já existe usurario cadastrado com a matricula:" + matricula);

        }

        Usuario usuario = new Usuario(matricula, nome);
        usuarios.put(matricula, usuario);
    }



    @Override
    public UsuarioView buscarUsuario(String matricula)
            throws DadosInvalidosException, RecursoNaoEncontradoException {

        exigirTextoPreenchido(matricula, "matrícula");

        Usuario usuario = usuarios.get(matricula);

        if (usuario == null) {
            throw new RecursoNaoEncontradoException(
                    "Usuário não encontrado com a matrícula: " + matricula);
        }

        return paraUsuarioView(usuario);

    }

    @Override
    public List<UsuarioView> listarUsuarios() {

        List<UsuarioView> resultado = new ArrayList<>();

        for (Usuario usuario : usuarios.values()) {
            resultado.add(paraUsuarioView(usuario));
        }

        resultado.sort(Comparator.comparing(UsuarioView::nome, String.CASE_INSENSITIVE_ORDER));

        return resultado;
    }


    private UsuarioView paraUsuarioView(Usuario usuario) {
        return new UsuarioView(
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.isAtivo(),
                usuario.getEmprestimosAtivos()
        );


    }

    /**
     * O erro oculto que encontramos foi:
     * if (item.getCodigo().equals(codigo))
     *
     * Para String devemos usar equals().
     */
    //esta parte foi alterada devido o teste de falha pelo erro de comparação com == e foi substituído por equals.

    private Item localizar(String codigo) {
        if(codigo == null)return null;

        for (Item item : itens.values()) {
            if (item.getCodigo().equals(codigo)) {
                return item;
            }
        }
        return  null;
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

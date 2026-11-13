package br.ufpb.dcx.poo.biblioteca.persistencia;

/** O que uma linha do arquivo de itens guarda. Não é a entidade do domínio. */
public record LinhaDeItem(String codigo, String titulo, String autoria, String categoria, int ano) {

}
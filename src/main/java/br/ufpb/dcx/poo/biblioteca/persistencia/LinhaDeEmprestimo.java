package br.ufpb.dcx.poo.biblioteca.persistencia;

public record LinhaDeEmprestimo(String id, String tombo, String matricula, String dataEmprestimo, String dataPrevista, String dataDevolucao) { }
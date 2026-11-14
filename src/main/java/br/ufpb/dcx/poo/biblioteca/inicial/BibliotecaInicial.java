package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.RelatorioService;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.persistencia.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BibliotecaInicial implements Biblioteca {

    private final UsuariosEmMemoria usuarios = new UsuariosEmMemoria();
    private final AcervoEmMemoria acervo = new AcervoEmMemoria();
    private final EmprestimosEmMemoria emprestimos = new EmprestimosEmMemoria(acervo, usuarios);
    private final RelatoriosNaoImplementados relatorios = new RelatoriosNaoImplementados();

    @Override
    public AcervoService acervo() { return acervo; }

    @Override
    public UsuarioService usuarios() { return usuarios; }

    @Override
    public EmprestimoService emprestimos() { return emprestimos; }

    @Override
    public RelatorioService relatorios() { return relatorios; }

    @Override
    public void salvar() throws PersistenciaException {
        Path diretorio = DiretorioDeDados.atual();

        List<LinhaDeItem> linhasItens = new ArrayList<>();
        for (Item item : acervo.itens()) {
            linhasItens.add(new LinhaDeItem(
                    item.getCodigo(),
                    item.getTitulo(),
                    item.getAutoria(),
                    item.getCategoria(),
                    item.getAno()
            ));
        }


        List<LinhaDeExemplar> linhasExemplares = new ArrayList<>();
        for (Exemplar ex : acervo.exemplares()) {
            linhasExemplares.add(new LinhaDeExemplar(
                    ex.getTombo(),
                    ex.getCodigoDoItem(),
                    ex.getStatus().name()
            ));
        }


        List<LinhaDeUsuario> linhasUsuarios = new ArrayList<>();
        for (Usuario u : usuarios.todosUsuarios()) {
            linhasUsuarios.add(new LinhaDeUsuario(
                    u.getMatricula(),
                    u.getNome(),
                    u.isAtivo()
            ));
        }


        List<LinhaDeEmprestimo> linhasEmprestimos = new ArrayList<>();
        for (Emprestimo emp : emprestimos.todosEmprestimos()) {
            linhasEmprestimos.add(new LinhaDeEmprestimo(
                    emp.getId(),
                    emp.getExemplar().getTombo(),
                    emp.getUsuario().getMatricula(),
                    emp.getDataDoEmprestimo().toString(),
                    emp.getDataPrevistaDeDevolucao().toString(),
                    emp.getDataDaDevolucao() == null ? "" : emp.getDataDaDevolucao().toString()
            ));
        }


        List<LinhaDeReserva> linhasReservas = new ArrayList<>();
        for (Reserva res : emprestimos.todasReservas()) {
            String tomboSeparado = res.getExemplarSeparado() == null
                    ? ""
                    : res.getExemplarSeparado().getTombo();
            linhasReservas.add(new LinhaDeReserva(
                    res.getId(),
                    res.getCodigoDoItem(),
                    res.getUsuario().getMatricula(),
                    res.getDataDaReserva().toString(),
                    tomboSeparado
            ));
        }


        new ArquivoDeItens(diretorio).gravar(linhasItens);
        new ArquivoDeExemplares(diretorio).gravar(linhasExemplares);
        new ArquivoDeUsuarios(diretorio).gravar(linhasUsuarios);
        new ArquivoDeEmprestimos(diretorio).gravar(linhasEmprestimos);
        new ArquivoDeReservas(diretorio).gravar(linhasReservas);
    }

    @Override
    public void carregar() throws PersistenciaException {
        Path diretorio = DiretorioDeDados.atual();

        // Lê todos os arquivos primeiro
        List<LinhaDeItem> linhasItens = new ArquivoDeItens(diretorio).ler();
        List<LinhaDeExemplar> linhasExemplares = new ArquivoDeExemplares(diretorio).ler();
        List<LinhaDeUsuario> linhasUsuarios = new ArquivoDeUsuarios(diretorio).ler();
        List<LinhaDeEmprestimo> linhasEmprestimos = new ArquivoDeEmprestimos(diretorio).ler();
        List<LinhaDeReserva> linhasReservas = new ArquivoDeReservas(diretorio).ler();

        // Estruturas temporárias ("carregar ao lado, trocar no fim")
        Map<String, Item> novosItens = new HashMap<>();
        Map<String, Exemplar> novosTombos = new HashMap<>();
        Map<String, Usuario> novosUsuarios = new HashMap<>();
        List<Emprestimo> novosEmprestimos = new ArrayList<>();
        Map<String, FilaDeReservas> novasFilas = new HashMap<>();
        int maiorId = 0;

        // 1. Itens
        for (LinhaDeItem linha : linhasItens) {
            if (linha.codigo() == null || linha.codigo().isBlank()
                    || linha.titulo() == null || linha.titulo().isBlank()) {
                throw new PersistenciaException("Item com código ou título vazio.");
            }
            if (novosItens.containsKey(linha.codigo())) {
                throw new PersistenciaException("Código de item duplicado: " + linha.codigo());
            }
            novosItens.put(linha.codigo(), new Item(
                    linha.codigo(),
                    linha.titulo(),
                    linha.autoria(),
                    linha.categoria(),
                    linha.ano()
            ));
        }

        // 2. Exemplares (referenciam itens)
        for (LinhaDeExemplar linha : linhasExemplares) {
            Item item = novosItens.get(linha.codigoItem());
            if (item == null) {
                throw new PersistenciaException(
                        "Exemplar " + linha.tombo() + " referencia item inexistente: " + linha.codigoItem());
            }
            if (novosTombos.containsKey(linha.tombo())) {
                throw new PersistenciaException("Tombo duplicado: " + linha.tombo());
            }
            StatusExemplar status;
            try {
                status = StatusExemplar.valueOf(linha.status());
            } catch (IllegalArgumentException e) {
                PersistenciaException ex = new PersistenciaException("Status de exemplar inválido: " + linha.status());
                ex.initCause(e);
                throw ex;
            }
            Exemplar exemplar = new Exemplar(linha.tombo(), linha.codigoItem());
            exemplar.setStatus(status);

            try {
                item.adicionarExemplar(exemplar);
            } catch (RecursoDuplicadoException e) {
                PersistenciaException ex = new PersistenciaException("Tombo duplicado no item: " + linha.tombo());
                ex.initCause(e);
                throw ex;
            }

            novosTombos.put(linha.tombo(), exemplar);


        }


        for (LinhaDeUsuario linha : linhasUsuarios) {
            if (linha.matricula() == null || linha.matricula().isBlank()
                    || linha.nome() == null || linha.nome().isBlank()) {
                throw new PersistenciaException("Usuário com matrícula ou nome vazio.");
            }
            if (novosUsuarios.containsKey(linha.matricula())) {
                throw new PersistenciaException("Matrícula duplicada: " + linha.matricula());
            }
            Usuario usuario = new Usuario(linha.matricula(), linha.nome());
            usuario.setAtivo(linha.ativo());
            novosUsuarios.put(linha.matricula(), usuario);
        }

        // 4. Empréstimos (referenciam exemplares e usuários)
        for (LinhaDeEmprestimo linha : linhasEmprestimos) {
            Exemplar exemplar = novosTombos.get(linha.tombo());
            if (exemplar == null) {
                throw new PersistenciaException(
                        "Empréstimo " + linha.id() + " referencia tombo inexistente: " + linha.tombo());
            }
            Usuario usuario = novosUsuarios.get(linha.matricula());
            if (usuario == null) {
                throw new PersistenciaException(
                        "Empréstimo " + linha.id() + " referencia usuário inexistente: " + linha.matricula());
            }

            LocalDate dataEmprestimo;
            LocalDate dataDevolucao = null;
            try {
                dataEmprestimo = LocalDate.parse(linha.dataEmprestimo());
                // Valida também o formato da data prevista
                LocalDate.parse(linha.dataPrevista());
                if (linha.dataDevolucao() != null && !linha.dataDevolucao().isBlank()) {
                    dataDevolucao = LocalDate.parse(linha.dataDevolucao());
                }
            } catch (DateTimeParseException e) {
                PersistenciaException ex = new PersistenciaException("Data inválida no empréstimo " + linha.id());
                ex.initCause(e);
                throw ex;
            }

            Emprestimo emprestimo = new Emprestimo(linha.id(), exemplar, usuario, dataEmprestimo);
            if (dataDevolucao != null) {
                emprestimo.registrarDevolucao(dataDevolucao);
            }
            usuario.adicionarEmprestimo(emprestimo);
            novosEmprestimos.add(emprestimo);

            maiorId = Math.max(maiorId, extrairNumeroId(linha.id()));
        }

        // 5. Reservas (referenciam itens, usuários e opcionalmente exemplar separado)
        for (LinhaDeReserva linha : linhasReservas) {
            if (!novosItens.containsKey(linha.codigoItem())) {
                throw new PersistenciaException(
                        "Reserva " + linha.id() + " referencia item inexistente: " + linha.codigoItem());
            }
            Usuario usuario = novosUsuarios.get(linha.matricula());
            if (usuario == null) {
                throw new PersistenciaException(
                        "Reserva " + linha.id() + " referencia usuário inexistente: " + linha.matricula());
            }

            LocalDate dataReserva;
            try {
                dataReserva = LocalDate.parse(linha.dataReserva());
            } catch (DateTimeParseException e) {
                PersistenciaException ex = new PersistenciaException("Data inválida na reserva " + linha.id());
                ex.initCause(e);
                throw ex;
            }

            Reserva reserva = new Reserva(linha.id(), linha.codigoItem(), usuario, dataReserva);
            if (linha.tomboSeparado() != null && !linha.tomboSeparado().isBlank()) {
                Exemplar exemplarSeparado = novosTombos.get(linha.tomboSeparado());
                if (exemplarSeparado == null) {
                    throw new PersistenciaException(
                            "Reserva " + linha.id() + " referencia tombo separado inexistente: " + linha.tomboSeparado());
                }
                reserva.atribuirExemplar(exemplarSeparado);
            }

            novasFilas.computeIfAbsent(linha.codigoItem(), k -> new FilaDeReservas())
                    .entrar(reserva);

            maiorId = Math.max(maiorId, extrairNumeroId(linha.id()));
        }

        acervo.substituirEstado(novosItens, novosTombos);
        usuarios.substituirEstado(novosUsuarios);
        emprestimos.substituirEstado(novosEmprestimos, novasFilas, maiorId + 1);
    }

    private int extrairNumeroId(String id) throws PersistenciaException {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            PersistenciaException ex = new PersistenciaException("ID inválido no arquivo: " + id);
            ex.initCause(e);
            throw ex;
        }
    }
}
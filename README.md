# Sistema de Gestao de Biblioteca / Acervo

Projeto incremental da disciplina de Programacao Orientada a Objetos - DCX/CCAE/UFPB.
Equipe 01 - itgirls

---

## Equipe

| Nome | Matricula | GitHub |
|---|---|---|
| Ana Livia Patricio Santos | 20250114384 | anasantos029 |
| Anny Beatriz Lima Gaiao | 20250114366 | annygaiao |
| Bianca Vitoria de Almeida Felix | 20250114007 | biancavitoriaf |

---

## Mapa do Projeto

\\\	ext
equipe-01-itgirls/
├── docs/
│   ├── modelo.puml                      # Diagrama de classes no formato PlantUML
│   └── modelo.png                       # Imagem exportada do modelo UML
├── src/
│   ├── main/java/br/ufpb/dcx/poo/biblioteca/
│   │   ├── Fabrica.java                 # Ponto de inicializacao do sistema
│   │   ├── contrato/                    # CONGELADO: Interfaces, excecoes e Views
│   │   └── inicial/                     # Implementacao das regras de negocio
│   │       ├── AcervoEmMemoria.java
│   │       ├── Item.java
│   │       ├── Exemplar.java
│   │       └── Usuario.java
│   └── test/java/br/ufpb/dcx/poo/biblioteca/
│       ├── BuscarPorCodigoTeste.java     # Teste de regressao para o defeito do == / equals
│       └── NovosTestesAcervoTest.java    # Suite cobrindo entradas invalidas, duplicidades, ordenacao e bordas
└── pom.xml                              # Dependencias do projeto (Maven)
\\\

---

## Como Executar os Testes

### Pre-requisitos
* Java JDK 17 ou superior.
* Maven instalado (ou suporte a Maven na IDE).

### Linha de Comando (Terminal)
No diretorio raiz do projeto, execute:

\\\ash
# Executar todos os testes da suite
mvn test
\\\

Status da Suite: Todos os testes legados da Entrega 1 foram reabilitados (remocao do @Disabled) e estao passando 100% junto com as novas suites de testes automatizados.

### Pela IDE (IntelliJ IDEA)
1. Abra o projeto no IntelliJ IDEA.
2. Navegue ate a pasta src/test/java.
3. Clique com o botao direito sobre a pasta e selecione "Run 'All Tests'".

---

## Justificativa das Colecoes Utilizadas

A escolha das colecoes na classe AcervoEmMemoria considerou a eficiencia das operacoes, a unicidade e a complexidade de busca:

1. **Map<String, Item> itens (HashMap)**:
   - Justificativa: Permite a busca direta de itens pelo seu codigo unico com complexidade O(1) em media, alem de evitar duplicidade no cadastro utilizando o codigo do item como chave.

2. **Map<String, Usuario> usuarios (HashMap)**:
   - Justificativa: Garante acesso instantaneo O(1) aos dados do usuario atraves da sua matricula, facilitando validacoes de cadastro e consultas sem iteracoes desnecessarias.

3. **Map<String, Exemplar> tombosGlobais (HashMap)**:
   - Justificativa: Permite controle e busca direta de exemplares por tombo, tornando a verificacao de duplicidade e o processo de baixa de exemplares mais eficientes.

4. **List<Exemplar> exemplares (ArrayList) dentro de Item**:
   - Justificativa: Utilizado para armazenar a sequencia de exemplares pertencentes a um item especifico. Garante iteracao rapida para contagem de exemplares disponiveis e preserva a ordem de insercao.

---

## Extensao Autoral

* **Acervo Escolhido**: Livros
* **Regra de Negocio**: Restricao por classificacao indicativa - A biblioteca precisa validar a idade do usuario antes de realizar o emprestimo de livros por meio da classificacao etaria associada ao exemplar.

---

## Regras do Projeto

1. **Pacote Contrato**: Mantido congelado conforme diretrizes da disciplina.
2. **Fabrica.novaBiblioteca()**: Mantido para prover instancias limpas do sistema para a execucao das suites de teste.

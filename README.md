# Sistema de Gestão de Biblioteca/Acervo

Projeto incremental da disciplina de Programação Orientada a Objetos — DCX/CCAE/UFPB.  
**Equipe 01 - itgirls**

---

## 👥 Equipe

| Nome | Matrícula | GitHub |
|---|---|---|
| **Ana Lívia Patricio Santos** | 20250114384 | `anasantos029` |
| **Anny Beatriz Lima Gaião** | 20250114366 | `annygaiao` |
| **Bianca Vitória de Almeida Félix** | 20250114007 | `biancavitoriaf` |

---

## 🗺️ Mapa do Projeto

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

## 🚀 Como Executar os Testes

### Pré-requisitos
* Java JDK 17 ou superior.
* Maven instalado (ou suporte a Maven na IDE).

### Linha de Comando (Terminal)
No diretório raiz do projeto, execute:

\\\ash
# Executar todos os testes da suite
mvn test
\\\

> **Status da Suite:** Todos os testes legados da Entrega 1 foram reabilitados (remoção do @Disabled) e estão passando 100% junto com as novas suítes de testes automatizados.

### Pela IDE (IntelliJ IDEA)
1. Abra o projeto no IntelliJ IDEA.
2. Navegue até a pasta \src/test/java\.
3. Clique com o botão direito sobre a pasta e selecione **Run 'All Tests'**.

---

## 📚 Justificativa das Coleções Utilizadas

A escolha das coleções na classe \AcervoEmMemoria\ considerou a eficiência das operações, a unicidade e a complexidade de busca:

1. **\Map<String, Item> itens\ (\HashMap\)**:
   - **Justificativa**: Permite a busca direta de itens pelo seu código único com complexidade O(1) em média, além de evitar duplicidade no cadastro utilizando o código do item como chave.

2. **\Map<String, Usuario> usuarios\ (\HashMap\)**:
   - **Justificativa**: Garante acesso instantâneo O(1) aos dados do usuário através da sua matrícula, facilitando validações de cadastro e consultas sem iterações desnecessárias.

3. **\Map<String, Exemplar> tombosGlobais\ (\HashMap\)**:
   - **Justificativa**: Permite controle e busca direta de exemplares por **tombo**, tornando a verificação de duplicidade e o processo de baixa de exemplares mais eficientes.

4. **\List<Exemplar> exemplares\ (\ArrayList\) dentro de \Item\**:
   - **Justificativa**: Utilizado para armazenar a sequência de exemplares pertencentes a um item específico. Garante iteração rápida para contagem de exemplares disponíveis e preserva a ordem de inserção.

---

## 🎨 Extensão Autoral

* **Acervo Escolhido**: Livros
* **Regra de Negócio**: **Restrição por classificação indicativa** — A biblioteca precisa validar a idade do usuário antes de realizar o empréstimo de livros por meio da classificação etária associada ao exemplar.

---

## 📄 Regras do Projeto

1. **Pacote Contrato**: Mantido **congelado** conforme diretrizes da disciplina.
2. **\Fabrica.novaBiblioteca()\**: Mantido para prover instâncias limpas do sistema para a execução das suítes de teste.

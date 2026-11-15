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

```
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
```

---

## Como Executar os Testes

### Pre-requisitos
* Java JDK 17 ou superior.
* Maven instalado (ou suporte a Maven na IDE).

### Linha de Comando (Terminal)
No diretorio raiz do projeto, execute:

```Bash
# Executar todos os testes da suite
mvn test
```


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


## Nossa extensão — Restrição por classificação etária

A extensão implementa uma regra de restrição de empréstimos baseada na classificação etária dos exemplares do acervo.

O objetivo é impedir que usuários realizem empréstimos de livros cuja idade mínima recomendada seja superior à idade que possuem. Para isso, o sistema utiliza a data de nascimento cadastrada para calcular a idade do usuário na data do empréstimo e compará-la com a idade mínima exigida pelo exemplar.

### Como utilizar

1. Cadastrar o usuário informando sua data de nascimento.
2. Definir a classificação etária do exemplar.
3. Realizar a tentativa de empréstimo por meio do serviço `EmprestimoPorIdadeService`.
4. O sistema verifica se o usuário possui idade suficiente. Caso não possua, o empréstimo é impedido. Caso possua, o empréstimo é realizado normalmente.

### Classificações disponíveis

- LIVRE — sem idade mínima.
- IDADE_10 — a partir de 10 anos.
- IDADE_12 — a partir de 12 anos.
- IDADE_14 — a partir de 14 anos.
- IDADE_16 — a partir de 16 anos.
- IDADE_18 — a partir de 18 anos.

### Localização da implementação

A extensão foi implementada fora do pacote `contrato`, sem modificar o funcionamento dos métodos definidos pelas interfaces originais.

Principais classes envolvidas:

- `ClassificacaoEtaria.java`: define as classificações e respectivas idades mínimas.
- `EmprestimoPorIdadeService.java`: realiza a validação da idade antes de encaminhar o empréstimo ao serviço original.
- `Usuario.java`: armazena a data de nascimento e calcula a idade.
- `Exemplar.java`: armazena a classificação etária do exemplar.

### Testes

Foram implementados testes automatizados com JUnit 5 para verificar duas situações:

- Bloqueio do empréstimo quando o usuário possui idade inferior à idade mínima exigida.
- Permissão do empréstimo quando o usuário possui idade igual ou superior à idade mínima exigida.

Os testes foram executados com sucesso.

## Regras do Projeto

1. **Pacote Contrato**: Mantido congelado conforme diretrizes da disciplina.
2. **Fabrica.novaBiblioteca()**: Mantido para prover instancias limpas do sistema para a execucao das suites de teste.

## Relato do defeito

| Sintoma | Como reproduzir | Causa                                | Teste/Proteção          |
|---------|----------|--------------------------------------|--------------------------------------|
| Quebra de compilação ao restaurar o pacote contrato oficial.   | Sobrescrever a interface `AcervoService` pelo contrato original e executar `mvn test`.  | Métodos de usuários dentro de AcervoEmMemoria que não pertenciam ao contrato oficial. | Separação para UsuariosEmMemoria, remoção dos métodos extras e validação via NovosTestesAcervoTest                           |


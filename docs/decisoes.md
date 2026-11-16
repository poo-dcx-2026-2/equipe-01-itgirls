# Decisões do Projeto

## 1. Fila de reservas: composição em vez de herança

**Contexto:** A reserva de itens da biblioteca precisa respeitar estritamente a ordem de chegada dos usuários.

**Decisão:** A classe `FilaDeReservas` utiliza composição, mantendo uma `List<Reserva>` privada internamente e expondo apenas os métodos necessários para gerenciar a fila (`entrar`, `proxima` e `remover`).

**Alternativa rejeitada:** `FilaDeReservas extends ArrayList<Reserva>`. Herdar de `ArrayList` exporia métodos públicos como `add(int, ...)`, `set(...)` e `clear()`, permitindo que outras classes alterassem a ordem arbitrariamente.

**Consequência:** A ordem da fila só pode ser alterada pelos métodos da própria classe, garantindo o encapsulamento da regra de negócio.

## 2. Contagem de empréstimos ativos: Especialista da Informação em Usuario

**Contexto:** A `UsuarioView` precisa exibir a quantidade real de empréstimos ativos de um usuário.

**Decisão:** A classe `Usuario` mantém a lista dos seus próprios empréstimos (`List<Emprestimo>`) e implementa `getEmprestimosAtivos()`, aplicando o padrão GRASP Especialista da Informação.

**Alternativa rejeitada:** Manter um contador numérico manual, sujeito a inconsistências por incrementos e decrementos incorretos.

**Consequência:** A contagem fica encapsulada na entidade que possui as informações necessárias.

## 3. Verificação de empréstimo atrasado: Especialista da Informação em Emprestimo

**Contexto:** O sistema precisa verificar se um empréstimo está atrasado.

**Decisão:** A classe `Emprestimo` implementa `estaAtrasado(LocalDate dataAtual)`, pois possui a data prevista para devolução.

**Alternativa rejeitada:** Fazer o serviço consultar as datas e realizar a comparação externamente.

**Consequência:** A regra de negócio fica encapsulada na própria entidade `Emprestimo`.

## 4. Contagem de exemplares disponíveis: Especialista da Informação em Item

**Contexto:** O sistema precisa saber quantos exemplares de um item estão disponíveis.

**Decisão:** A classe `Item` implementa `exemplaresDisponiveis()`, pois mantém a lista de seus exemplares.

**Alternativa rejeitada:** Percorrer a lista de exemplares externamente dentro do serviço de acervo.

**Consequência:** A contagem fica encapsulada na entidade `Item`.

## 5. Padrão Facade — BibliotecaInicial

**Contexto:** O sistema precisa oferecer um ponto de acesso que facilite a utilização dos serviços da biblioteca, sem exigir que o cliente conheça todos os detalhes internos de implementação.

**Decisão:** A classe `BibliotecaInicial` implementa a interface `Biblioteca` e funciona como uma fachada para o sistema. Ela centraliza o acesso aos serviços de acervo, usuários, empréstimos e relatórios, além das operações de salvar e carregar os dados.

**Alternativa rejeitada:** Exigir que o código cliente conheça e coordene diretamente todos os serviços e suas dependências.

**Consequência:** O código cliente utiliza a interface `Biblioteca` e acessa os serviços por meio dos métodos disponibilizados por `BibliotecaInicial`, mantendo a implementação interna organizada e reduzindo o acoplamento.
## 6. Reservas de usuários desativados

**Contexto:** A equipe precisava definir como o sistema trataria tentativas de reserva feitas por usuários desativados.

**Decisão:** Usuários desativados não podem realizar novas reservas. O método `reservar` verifica se o usuário está ativo e, caso contrário, lança `OperacaoNaoPermitidaException`.

**Alternativa rejeitada:** Permitir que usuários desativados entrem na fila de reservas.

**Consequência:** A regra de atividade do usuário também é aplicada às reservas, impedindo novas operações de reserva por usuários desativados.
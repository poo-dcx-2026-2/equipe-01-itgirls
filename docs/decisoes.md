# Decisões do Projeto 

## Fila de reservas: composição em vez de herança

**Contexto:** A reserva de itens da biblioteca precisa respeitar estritamente a ordem de chegada dos usuários.

**Decisão:** A classe `FilaDeReservas` utiliza composição, mantendo uma `List<Reserva>` privada internamente e expondo apenas os métodos necessários para gerenciar a fila (`entrar`, `proxima` e `remover`).

**Alternativa rejeitada:** `FilaDeReservas extends ArrayList<Reserva>`: Herdar de `ArrayList` exporia métodos públicos como `add(int, ...)`, `set(...)` e `clear()`, permitindo que qualquer classe externa alterasse a ordem arbitrariamente e furasse a fila.

**Consequência:** A ordem da fila só pode ser alterada através dos métodos da própria classe `FilaDeReservas`, garantindo o encapsulamento da regra de negócio e simplificando os testes.


## Contagem de empréstimos ativos: Especialista da Informação no Usuario

**Contexto:** A `UsuarioView` precisa exibir a quantidade real de empréstimos ativos de um determinado usuário.

**Decisão:** A classe `Usuario` guarda a lista dos seus próprios empréstimos (`List<Emprestimo>`) e implementa o método `getEmprestimosAtivos()`, aplicando o padrão **Especialista da Informação** (o `Usuario` detém a informação necessária para realizar a contagem).

**Alternativa rejeitada:** Manter um contador numérico manual (`int emprestimosAtivos`) que exige incremento e decremento externos sujeito a falhas de sincronização, ou fazer o serviço de empréstimos percorrer a base por fora.

**Consequência:** Encapsulamento da contagem dentro da própria entidade `Usuario`, eliminando o risco de inconsistência no contador.



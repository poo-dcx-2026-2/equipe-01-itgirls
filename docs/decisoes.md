# Decisões do Projeto 

## Fila de reservas: composição em vez de herança

**Contexto.** A reserva de itens da biblioteca precisa respeitar estritamente a ordem de chegada dos usuários.

**Decisão.** A classe `FilaDeReservas` utiliza composição, mantendo uma `List<Reserva>` privada internamente e expondo apenas os métodos necessários para gerenciar a fila (`entrar`, `proxima` e `remover`).

**Alternativa rejeitada.** `FilaDeReservas extends ArrayList<Reserva>`: Herdar de `ArrayList` exporia métodos públicos como `add(int, ...)`, `set(...)` e `clear()`, permitindo que qualquer classe externa alterasse a ordem arbitrariamente e furasse a fila.

**Consequência.** A ordem da fila só pode ser alterada através dos métodos da própria classe `FilaDeReservas`, garantindo o encapsulamento da regra de negócio e simplificando os testes.
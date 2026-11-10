# Devolutiva da Entrega 1 — it girls

## Nota: 7,0 de 10 (70 de 100 pontos)

Vocês alteraram o contrato. 

Avaliei o último commit da branch `main` até 12/10/2026 às 23h59 (o prazo original, 05/10, mais uma semana de extensão), pela leitura do código, dos testes, do histórico de commits, dos pull requests e da documentação. 

## Pontos por critério

| Critério | O que é avaliado | Pontos | Nível |
|---|---|---|---|
| 4.1 | Correção funcional e aderência ao contrato | 15 de 20 | Insuficiente |
| 4.2 | Modelagem OO, responsabilidades, coesão e acoplamento | 13 de 25 | Parcial |
| 4.3 | Testes e evidências de depuração | 13 de 15 | Adequado |
| 4.4 | Robustez, exceções e casos-limite | 9 de 10 | Excelente |
| 4.5 | Evolução, refatoração e qualidade do código | 6 de 10 | Parcial |
| 4.6 | Git, build, pipeline e documentação | 6 de 10 | Parcial |
| 4.7 | Processo, checkpoints, explicação e autoria | 8 de 10 | Adequado |
| | **Total** | **70 de 100** | |

## Justificativa (onde tiro pontos)

- Acrescentaram métodos de usuário a AcervoService.

- Ao restaurar o contrato oficial, o projeto não compila por causa de três @Override inválidos.

- AcervoEmMemoria também guarda e opera usuários, duplicando UsuariosEmMemoria e misturando responsabilidades.

- Faltou revisão formal por outra integrante.

## Checklist de correções

- [ ] **Restaurar integralmente o pacote `contrato`.** A equipe acrescentou métodos de usuário em `AcervoService`. Quando o contrato oficial é colocado de volta, o projeto deixa de compilar, porque três métodos têm `@Override` (a marcação que diz "este método vem da interface") e a interface oficial não tem esses métodos. Copiem os arquivos do template oficial por cima dos de vocês, removam os três `@Override` indevidos (ou os próprios métodos, se não forem mais usados) e confiram que `mvn test` passa.
- [ ] **Manter usuários apenas em `UsuariosEmMemoria`.** Hoje `AcervoEmMemoria` também guarda e opera usuários, o que duplica o estado: existem duas listas de usuários que podem discordar. O acervo cuida de itens e exemplares; usuários ficam só no serviço de usuários.
- [ ] **Alinhar o UML ao código.** O diagrama precisa usar a interface do contrato (`UsuarioService`) e mostrar onde os usuários de fato ficam guardados.
- [ ] **Escrever no README o relato do defeito.** Uma seção curta com: o que acontecia (o sintoma), como vocês reproduziram, qual era a causa e qual teste agora protege contra ele.
- [ ] **Registrar a revisão de colega no pull request.** Pull request (PR) é o pedido para juntar uma *branch* (linha de trabalho separada) na `main`. A revisão só conta como evidência se aparecer no GitHub: na aba *Files changed* do PR, o colega que **não** escreveu o código clica em *Review changes*, escreve pelo menos um comentário sobre o que leu e escolhe *Approve* ou *Request changes*. Mensagem de commit dizendo "revisado" não substitui isso.
- [ ] **Integrar os PRs abertos antes do prazo.** Só o que está na `main` no horário de corte é avaliado.

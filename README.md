# Car Wash Management (Desktop)

<p align="left">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/JavaFX-21-5382A1?style=flat-square&logo=java&logoColor=white" alt="JavaFX 21">
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white" alt="Hibernate">
  <img src="https://img.shields.io/badge/JPA-2.1-007396?style=flat-square&logo=java&logoColor=white" alt="JPA 2.1">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Eclipse-2C2255?style=flat-square&logo=eclipseide&logoColor=white" alt="Eclipse">
</p>

Aplicação desktop para gerenciar o dia a dia de um lava-jato: agendas de lavagem por dia,
veículos atendidos, gastos do mês e um fechamento financeiro simples (receita menos despesas
igual a lucro). Projeto de estudo, feito em Java com JavaFX, Hibernate/JPA e MySQL.

> Existe também uma **versão web** deste projeto, feita depois em React e Supabase, com a
> modelagem repensada do zero:
> [car-wash-management-web](https://github.com/JocaMgtt0/Car-wash-management-web).

## Tecnologias

- **Java 21**
- **JavaFX 21** (interface gráfica, telas em FXML montadas no Scene Builder)
- **Hibernate / JPA 2.1** (persistência)
- **MySQL** (banco de dados)
- Projeto Eclipse puro, sem Maven ou Gradle. As dependências ficam como `.jar` em `lib/` e `libs/`

## Arquitetura

O projeto segue uma separação próxima de MVC:

```
src/
├── model/            → Entidades JPA (Agenda, Automovel, Gasto)
│   └── infra/         → DAO<E> genérico + DAOs específicos (AgendaDAO, AutomovelDAO, GastosDAO)
├── controller/        → Um controller JavaFX por tela (liga o FXML à lógica)
├── view/               → Telas em FXML + Launcher.java (ponto de entrada)
└── META-INF/           → persistence.xml (configuração do Hibernate/JPA)
```

**Modelo de dados:**

- `Agenda`: representa um dia de movimento (nome e data), guarda o total arrecadado e tem uma
  lista de `Automovel` (`@OneToMany`, cascade total).
- `Automovel`: um carro lavado dentro de uma agenda (modelo, placa, valor da lavagem), vinculado
  via `@ManyToOne`.
- `Gasto`: uma despesa avulsa (nome, valor, data), sem relação com as outras entidades.

**Camada de acesso a dados:** `DAO<E>` (`model/infra/DAO.java`) centraliza o CRUD genérico
(incluir, remover, merge, buscar por ID, listar) e o ciclo de vida do `EntityManager`. As classes
`AgendaDAO`, `AutomovelDAO` e `GastosDAO` estendem essa classe e adicionam consultas JPQL
específicas (busca por mês e ano, por data, por agenda, etc).

## Como funciona (fluxo das telas)

1. **Tela Inicial** (`TelaInicial.fxml` / `TelaInicialController`): lista todas as agendas
   cadastradas, com filtro por nome e por data. A partir dela dá pra:
   - Criar uma nova agenda (abre `FileAgenda.fxml` como modal)
   - Editar ou excluir uma agenda existente
   - Dar dois cliques numa agenda para abrir seu **Dashboard**
   - Ir para a tela de **Fechamento Mensal**

2. **Dashboard da Agenda** (`Dashboard.fxml` / `DashboardController`): mostra os veículos lavados
   naquele dia e o total arrecadado. Permite registrar uma nova lavagem
   (`FormularioLavagem.fxml`), editar ou excluir uma lavagem. O total da agenda é recalculado a
   cada operação.

3. **Fechamento Mensal** (`FechamentoMes.fxml` / `FechamentoMesController`): relatório por mês e
   ano. Soma a receita de todas as agendas do período, soma os gastos lançados e mostra o lucro
   líquido. De lá também dá pra abrir o gerenciamento de gastos.

4. **Gerenciar Gastos** (`GerenciarGastos.fxml` / `GerenciarGastosController`): CRUD de despesas
   (aluguel, produtos, etc), filtrado por mês, usado para abater da receita no fechamento.

`Launcher.java` é o ponto de entrada: inicia a `Application` do JavaFX, carrega a Tela Inicial e
garante que a conexão com o banco seja fechada ao encerrar o app.

## Como rodar localmente

**Pré-requisitos:** JDK 21, JavaFX SDK 21 configurado no Eclipse (User Library `JavaFx21`), e
MySQL rodando localmente.

1. Clone o repositório:

   ```bash
   git clone https://github.com/JocaMgtt0/Car-wash-management-desktop.git
   ```

2. Crie o banco `lava_jato` no seu MySQL.

3. Copie `src/db.properties.example` para `src/db.properties` e preencha com seu usuário e senha
   do MySQL:

   ```
   db.user=root
   db.password=sua_senha
   ```

   Esse arquivo é ignorado pelo git (veja `.gitignore`), então cada pessoa mantém o seu
   localmente.

4. Confira a URL de conexão em `src/META-INF/persistence.xml` (por padrão `localhost:3306`).

5. Importe o projeto no Eclipse e rode `src/view/Launcher.java`. O Hibernate cria e atualiza as
   tabelas automaticamente (`hibernate.hbm2ddl.auto=update`).

## Estrutura de pastas

```
.classpath, .project, .settings/   → configuração do Eclipse
lib/, libs/                        → dependências (.jar) versionadas no repositório
src/                                → código-fonte
bin/                                → build output (gerado pelo Eclipse, fora do git)
```

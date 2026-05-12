# agi-api

> Automacao de testes da [Dog API](https://dog.ceo/dog-api/documentation/) com **Java 21 + Rest Assured + JUnit 5**, organizada em camadas para validar contrato, comportamento e respostas dos principais endpoints.

![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Rest Assured](https://img.shields.io/badge/Rest%20Assured-5.5.2-green)
![JUnit](https://img.shields.io/badge/JUnit-5.12.2-orange)
![Tests](https://img.shields.io/badge/testes-13%20passing-brightgreen)

---

## Sobre o projeto

A Dog API e uma API publica para consulta de racas e imagens de caes. Este projeto automatiza os endpoints pedidos no desafio tecnico, com foco em:

- **Contrato da API:** validacao via JSON Schema.
- **Comportamento esperado:** status code, campo `status`, estrutura de `message` e mensagens de erro.
- **Qualidade da resposta:** content-type JSON, URL de imagens e tempo de resposta aceitavel.
- **Execucao multiplataforma:** Gradle Wrapper para rodar em Windows, Linux e macOS.

Os testes nao criam, alteram ou excluem dados. Todos os endpoints cobertos sao de leitura.

---

## Tecnologias

| Tecnologia | Versao | Finalidade |
|---|---:|---|
| Java | 21 | Linguagem principal |
| Gradle Wrapper | 9.0.0 | Build e execucao dos testes |
| Rest Assured | 5.5.2 | Chamadas HTTP e validacoes de API |
| JUnit 5 | 5.12.2 | Runner, tags e testes parametrizados |
| AssertJ | 3.26.3 | Assertions fluentes |
| JSON Schema Validator | 5.5.2 | Validacao de contrato |
| Allure Report | 2.29.1 | Relatorio visual de execucao |
| GitHub Actions | - | Pipeline de smoke/regressao |

---

## Pre-requisitos

Antes de executar, verifique se voce tem instalado:

| Requisito | Versao minima | Como verificar |
|---|---:|---|
| JDK | 21 | `java -version` |
| Git | - | `git --version` |

Nao e necessario instalar Gradle manualmente, pois o projeto usa Gradle Wrapper.

> **JAVA_HOME** deve apontar para a raiz do JDK, nao para a pasta `bin`.

Exemplo:

```bash
JAVA_HOME=/caminho/para/jdk-21
```

---

## Configuracao e execucao

### 1. Clone o repositorio

```bash
git clone git@github.com:silasmarques/agi-api.git
cd agi-api
```

### 2. Execute todos os testes

Linux/macOS:

```bash
./gradlew test
```

Windows:

```bat
gradlew.bat test
```

### 3. Execute por grupo

```bash
./gradlew smokeTest
./gradlew regressionTest
./gradlew contractTest
```

### 4. Execute sem paralelismo

Util para debug local:

```bash
./gradlew test -Dparallel=false
```

### 5. Troque o ambiente

Por padrao a suite usa `hml`, configurado em:

```text
src/test/resources/environments/hml.properties
```

Para executar outro ambiente:

```bash
./gradlew test -Denv=dev
```

---

## Endpoints cobertos

| Endpoint | Objetivo |
|---|---|
| `GET /breeds/list/all` | Listar todas as racas e sub-racas disponiveis |
| `GET /breed/{breed}/images` | Listar imagens de uma raca especifica |
| `GET /breeds/image/random` | Retornar uma imagem aleatoria |

Base URL:

```text
https://dog.ceo/api
```

---

## Cenarios de teste

**Total: 13 testes** | Suite organizada por `@Tag`: `smoke`, `regression`, `contract`, `negative`, `breeds`, `images`.

---

### Modulo 1 - Listagem de racas

#### CT-01 - Deve listar todas as racas com contrato valido

```gherkin
Dado  que a Dog API esta disponivel
Quando uma requisicao GET e feita para /breeds/list/all
Entao a resposta deve retornar status HTTP 200
  E   o corpo deve respeitar o JSON Schema de listagem de racas
  E   o campo status deve ser "success"
```

#### CT-02 - Deve retornar racas conhecidas e sub-racas

```gherkin
Dado  que o consumidor precisa conhecer as racas disponiveis
Quando consulta GET /breeds/list/all
Entao a resposta deve conter racas conhecidas como hound, retriever, terrier, akita e pug
  E   a raca hound deve possuir sub-racas
```

#### CT-03 - Deve retornar racas em formato de mapa com listas de sub-racas

```gherkin
Dado  que a resposta de racas precisa ser consumida pela aplicacao
Quando consulta GET /breeds/list/all
Entao message deve ser um mapa nao vazio
  E   cada chave do mapa deve representar uma raca em texto
  E   cada valor deve ser uma lista de sub-racas, mesmo quando vazia
```

#### CT-04 - Deve responder listagem de racas como JSON em tempo aceitavel

```gherkin
Dado  que a aplicacao depende da listagem de racas
Quando consulta GET /breeds/list/all
Entao o content-type deve ser JSON
  E   a resposta deve ocorrer em ate 5000 ms
```

---

### Modulo 2 - Imagens por raca

#### CT-05 - Deve listar imagens de uma raca existente

```gherkin
Dado  que a raca hound existe na Dog API
Quando uma requisicao GET e feita para /breed/hound/images
Entao a resposta deve retornar status HTTP 200
  E   o corpo deve respeitar o JSON Schema de imagens por raca
  E   message deve conter uma lista nao vazia de URLs de imagem
  E   as URLs devem pertencer ao dominio images.dog.ceo
```

#### CT-06 a CT-09 - Racas conhecidas devem retornar imagens validas

```gherkin
Dado  que a raca informada existe na Dog API
Quando consulta GET /breed/{breed}/images
Entao a resposta deve retornar content-type JSON
  E   status deve ser "success"
  E   message deve conter uma lista nao vazia
  E   todas as URLs devem apontar para imagens da raca consultada
```

| CT | Raca |
|---|---|
| CT-06 | `akita` |
| CT-07 | `hound` |
| CT-08 | `pug` |
| CT-09 | `retriever` |

#### CT-10 - Deve responder imagens por raca em tempo aceitavel

```gherkin
Dado  que a aplicacao exibe imagens de uma raca
Quando consulta GET /breed/hound/images
Entao a resposta deve ocorrer em ate 5000 ms
```

#### CT-11 - Deve retornar erro ao consultar raca inexistente

```gherkin
Dado  que a raca "raca-inexistente" nao existe
Quando consulta GET /breed/raca-inexistente/images
Entao a resposta deve retornar status HTTP 404
  E   o corpo deve respeitar o JSON Schema de erro
  E   status deve ser "error"
  E   message deve informar que a raca nao foi encontrada
  E   code deve ser 404
```

---

### Modulo 3 - Imagem aleatoria

#### CT-12 - Deve retornar uma imagem aleatoria com contrato valido

```gherkin
Dado  que a aplicacao precisa exibir uma imagem aleatoria
Quando consulta GET /breeds/image/random
Entao a resposta deve retornar status HTTP 200
  E   o corpo deve respeitar o JSON Schema de imagem aleatoria
  E   status deve ser "success"
  E   message deve ser uma URL valida de imagem
```

#### CT-13 - Deve retornar imagem aleatoria como JSON em tempo aceitavel

```gherkin
Dado  que a imagem aleatoria e usada na experiencia do usuario
Quando consulta GET /breeds/image/random
Entao o content-type deve ser JSON
  E   a resposta deve ocorrer em ate 5000 ms
  E   message deve apontar para uma imagem em images.dog.ceo
```

---

## Arquitetura

```text
src/test/java/br/com/silas/agiapi/
|
|-- assertions/
|   `-- DogApiAssertions.java        # Validacoes reutilizaveis de resposta
|
|-- clients/
|   `-- DogApiClient.java            # Encapsula chamadas HTTP da Dog API
|
|-- config/
|   `-- ConfigManager.java           # Le properties por ambiente
|
|-- core/
|   |-- RequestSpecFactory.java      # Base URI, headers, timeouts e filtros
|   |-- ResponseSpecFactory.java     # Response specs comuns
|   `-- RestAssuredExtension.java    # Configuracao global do Rest Assured
|
`-- tests/dogs/
    |-- BreedListTest.java           # GET /breeds/list/all
    |-- BreedImagesTest.java         # GET /breed/{breed}/images
    `-- RandomImageTest.java         # GET /breeds/image/random
```

```text
src/test/resources/
|
|-- environments/
|   |-- hml.properties
|   `-- dev.properties
|
|-- schemas/
|   |-- breed-list-schema.json
|   |-- breed-images-schema.json
|   |-- random-image-schema.json
|   `-- error-schema.json
|
|-- allure.properties
`-- junit-platform.properties
```

**Principio central:** os testes expressam a regra que esta sendo validada; detalhes de chamada HTTP ficam no client, e validacoes repetidas ficam nas assertions.

```java
Response response = dogApiClient.listBreedImages("hound");

response.then()
        .statusCode(200)
        .body(matchesJsonSchemaInClasspath("schemas/breed-images-schema.json"));

DogApiAssertions.shouldContainBreedImages(response, "hound");
```

---

## Relatorio Allure

Sim, o projeto possui relatorio Allure.

O Allure e integrado por:

- Plugin Gradle `io.qameta.allure`
- Dependencias `allure-junit5` e `allure-rest-assured`
- Anotacoes `@Epic`, `@Feature`, `@Severity` e `@Step`
- Filtro `AllureRestAssured`, que anexa request e response ao relatorio

Para visualizar localmente:

```bash
./gradlew clean test
./gradlew allureServe
```

Para gerar HTML estatico:

```bash
./gradlew allureReport
```

Arquivos gerados:

```text
build/allure-results
build/reports/allure-report
```

O relatorio HTML nativo do Gradle tambem fica disponivel em:

```text
build/reports/tests/test/index.html
```

> No GitHub Actions, os resultados Allure sao enviados como artifact. Publicacao online via GitHub Pages pode ser adicionada depois, caso seja desejado manter o mesmo modelo do `agi-e2e`.

---

## CI/CD - GitHub Actions

Arquivo: `.github/workflows/api-tests.yml`

| Trigger | Execucao |
|---|---|
| `push` para `main` | Smoke tests |
| `pull_request` para `main` | Smoke tests |
| `schedule` | Suite completa |
| `workflow_dispatch` | Suite completa manual |

Artifacts publicados:

- `build/allure-results`
- `build/test-results`

---

## Observacoes tecnicas

- A suite roda em paralelo por padrao via JUnit Platform.
- Os testes de performance usam limite conservador de `5000 ms`, suficiente para detectar lentidao grosseira sem deixar a suite fragil.
- O teste de imagem aleatoria valida formato e origem da URL, mas nao baixa a imagem; isso evita misturar teste da API com disponibilidade de CDN/storage.
- O endpoint de raca inexistente cobre comportamento negativo sem depender de massa de dados externa.

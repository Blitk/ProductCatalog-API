# ProductCatalog API

API REST para gerenciamento de produtos e categorias, desenvolvida com Java 21 e Spring Boot.

O projeto implementa operações CRUD, filtros dinâmicos, paginação, validação de dados e persistência com PostgreSQL.

O objetivo é desenvolver uma API organizada, testável e de fácil execução, aplicando conceitos fundamentais de desenvolvimento Backend.

⸻

## Tecnologias

* Java 21
* Spring Boot 3.5.7
* Maven
* Spring Web
* Spring Data JPA
* Spring Validation
* PostgreSQL
* Spring Data JPA Specifications
* JUnit 5
* Mockito
* MockMvc
* Testcontainers
* Docker
* Docker Compose
* Swagger / OpenAPI

⸻

## Funcionalidades

*Produtos*

* Cadastro de produtos.
* Consulta de produtos.
* Consulta de produto por ID.
* Atualização de produtos.
* Exclusão de produtos.
* Pesquisa por nome.
* Filtro por categoria.
* Filtros combinados por nome, categoria, preço e estoque.
* Paginação e ordenação dos resultados.
* Upload de imagem.

*Categorias*

* Cadastro de categorias.
* Consulta de categorias.
* Consulta de categoria por ID.
* Atualização de categorias.
* Exclusão de categorias.
* Impedimento de exclusão de categorias que possuem produtos associados.

*Validações e tratamento de erros*

* Validação de campos obrigatórios.
* Validação de valores e quantidades.
* Verificação de categorias existentes.
* Prevenção de categorias duplicadas.
* Validação dos parâmetros de filtros.
* Tratamento global de exceções com respostas HTTP padronizadas.

⸻

## Arquitetura

O projeto utiliza uma arquitetura em camadas (Layered Architecture), separando as responsabilidades da aplicação.

```
com.productcatalog
│
├── controller/
│   ├── ProductController.java
│   └── CategoryController.java
│
├── service/
│   ├── ProductService.java
│   ├── ProductImageService.java
│   └── CategoryService.java
│
├── repository/
│   ├── ProductRepository.java
│   └── CategoryRepository.java
│
├── model/
│   ├── Product.java
│   └── Category.java
│
├── dto/
│   ├── ProductRequestDTO.java
│   ├── ProductResponseDTO.java
│   ├── CategoryRequestDTO.java
│   └── CategoryResponseDTO.java
│
├── specification/
│   └── ProductSpecification.java
│
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── BusinessException.java
│   ├── InvalidFilterException.java
│   ├── InvalidImageException.java
│   └── GlobalExceptionHandler.java
│
└── ProductCatalogApplication.java
```

⸻

## Modelo de dados

O banco de dados possui duas entidades principais.

*Category*

```
id	- Long
name - String
description	- String
```

*Product*

```
id - Long
name - String
description - String
price	- BigDecimal
stockQuantity	- Integer
category	- Category
createdAt	- LocalDateTime
imageUrl - String
```

Um produto pertence a uma categoria por meio de um relacionamento *Many-to-One*.

⸻

## Executando localmente

*Pré-requisitos*

* Java 21
* Maven
* PostgreSQL

1. Clone o repositório

```
git clone https://github.com/blitk/ProductCatalog.git
cd ProductCatalog
```


2. Configure o PostgreSQL

Crie o usuário e o banco de dados:

```
CREATE USER productuser WITH PASSWORD 'product123';
CREATE DATABASE productcatalog OWNER productuser;
```

As credenciais acima são destinadas exclusivamente ao ambiente local de desenvolvimento.

3. Configure as variáveis de ambiente

A aplicação utiliza as seguintes variáveis:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/productcatalog
SPRING_DATASOURCE_USERNAME=productuser
SPRING_DATASOURCE_PASSWORD=product123
```

Também é possível utilizar os valores padrão definidos em application.properties, conforme a configuração local do projeto.

4. Execute a aplicação

```
mvn clean install
mvn spring-boot:run
```

A API estará disponível em:

http://localhost:8080

⸻

## Executando com Docker

O projeto também disponibiliza Dockerfile e Docker Compose para facilitar a inicialização da aplicação e do PostgreSQL em containers.

*Pré-requisitos*

* Docker
* Docker Compose

1. Construa o projeto

```
mvn clean package -DskipTests
```

2. Inicie os containers
   
```
docker compose up --build -d
```

*O Docker Compose será responsável por:*

* Criar o container do PostgreSQL.
* Criar o banco de dados productcatalog.
* Configurar as credenciais de desenvolvimento.
* Aguardar o banco de dados ficar saudável.
* Construir e iniciar a API Spring Boot.
* Criar um volume persistente para os dados do PostgreSQL.

3. Verifique os containers

```
docker compose ps
```

4. Consulte os logs

```
docker compose logs -f app
```

5. Encerre os containers

```
docker compose down
```

Para remover também o volume do banco de dados:

```
docker compose down -v
```

Atenção: o comando docker compose down -v remove os dados persistidos no volume do PostgreSQL.

⸻

## Endpoints

Base URL:

http://localhost:8080

*Produtos*

```
POST	/api/products	Cadastrar produto
GET	/api/products	Listar produtos
GET	/api/products/{id}	Buscar produto por ID
PUT	/api/products/{id}	Atualizar produto
DELETE	/api/products/{id}	Excluir produto
GET	/api/products/search	Pesquisar por nome
GET	/api/products/category/{categoryId}	Filtrar por categoria
GET	/api/products/filter	Aplicar filtros combinados
```
*Categorias*

```
POST	/api/categories	Cadastrar categoria
GET	/api/categories	Listar categorias
GET	/api/categories/{id}	Buscar categoria por ID
PUT	/api/categories/{id}	Atualizar categoria
DELETE	/api/categories/{id}	Excluir categoria
```
*Imagens*
```
POST /api/products/{id}/image Enviar imagem
DELETE /api/products/{id}/image Remover imagem
GET /api/products/{id} Consultar produto e imagem
```

⸻

## Exemplos de requisições

*Cadastrar categoria*

POST /api/categories

{
  "name": "Periféricos",
  "description": "Produtos e acessórios para computadores"
}

*Cadastrar produto*

POST /api/products

{
  "name": "Teclado mecânico",
  "description": "Teclado mecânico RGB",
  "price": 199.90,
  "stockQuantity": 15,
  "categoryId": 1
}

*Listar produtos com paginação*

GET /api/products?page=0&size=10&sort=price,asc

Parâmetros:

* page: número da página, começando em zero.
* size: quantidade de registros por página.
* sort: campo e direção da ordenação.

*Pesquisar por nome*

GET /api/products/search?name=teclado&page=0&size=10

*Filtrar por categoria*

GET /api/products/category/1?page=0&size=10

*Utilizar filtros combinados*

GET /api/products/filter?name=teclado&categoryId=1&minPrice=50&maxPrice=300&minStock=5&maxStock=20&page=0&size=10

Os filtros são opcionais e podem ser combinados na mesma requisição.

⸻

## Documentação da API

O projeto utiliza Swagger / OpenAPI para disponibilizar documentação interativa dos endpoints.

Após iniciar a aplicação, acesse:

Swagger UI

http://localhost:8080/swagger-ui/index.html

OpenAPI JSON

http://localhost:8080/v3/api-docs

⸻

## Testes automatizados

Os testes são organizados de acordo com as camadas da aplicação.

```
src/test/java/com/productcatalog
│
├── controller/
│   └── ProductControllerTest.java
│
├── service/
│   ├── ProductServiceTest.java
│   └── CategoryServiceTest.java
│
└── repository/
    └── ProductRepositoryTest.java
```
*Ferramentas utilizadas*

* JUnit 5: estrutura dos testes.
* Mockito: simulação de dependências.
* MockMvc: testes dos endpoints HTTP.
* Testcontainers: testes de integração com PostgreSQL em container.

Executar os testes

```
mvn clean test

```

Executar a verificação completa

```
mvn clean verify
```

Relatório de cobertura

Os testes de integração com Testcontainers precisam de um ambiente Docker em execução.

⸻

## Aprendizados e conceitos aplicados

Durante o desenvolvimento deste projeto, são trabalhados conceitos importantes de desenvolvimento Backend com Java e Spring Boot:

* Desenvolvimento de APIs REST.
* Arquitetura em camadas.
* Injeção de dependências.
* Programação orientada a objetos.
* DTOs e separação de responsabilidades.
* Mapeamento objeto-relacional com JPA.
* Relacionamentos entre entidades.
* Consultas dinâmicas com Specifications.
* Paginação e ordenação com Spring Data.
* Bean Validation.
* Tratamento global de exceções.
* Testes unitários e de integração.
* Containerização com Docker.
* Persistência de dados com PostgreSQL.

⸻

Desenvolvido para fins de aprendizado e demonstração de conhecimentos em desenvolvimento Backend com Java e Spring Boot.

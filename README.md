# sistema-arquivos-virtuais
API REST Gerenciamento de Diretórios e Arquivos Virtuais

# Tecnologias:

- Java openJDK 17.0.2

- SGBD MySQL 8 no container Docker

# Modelagem
Tabela nodes (para representar diretórios e arquivos).

Aqui, a estrutura será genérica, com uma coluna que diferencia entre diretório e arquivo.

Cada entrada pode ter um parent_id que referencia o diretório pai, formando a estrutura hierárquica.

```
CREATE TABLE nodes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type ENUM('directory', 'file') NOT NULL,
    parent_id INT NULL,
    size BIGINT NULL, -- Tamanho em bytes, aplicável apenas para arquivos
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent FOREIGN KEY (parent_id) REFERENCES nodes(id) ON DELETE CASCADE
);

```

Exemplos:

Criar diretório na raiz
```
INSERT INTO nodes (name, type, parent_id) VALUES ('Documents', 'directory', NULL);
```
Criar um arquivo dentro de Documents
```
INSERT INTO nodes (name, type, parent_id, size) 
VALUES ('resume.pdf', 'file', 1, 50000);  -- Supondo que o diretório 'Documents' tem id = 1
```
Criando um subdiretório dentro de Documents
```
INSERT INTO nodes (name, type, parent_id) VALUES ('Photos', 'directory', 1);
```
Listar todos o arquivos de diretórios na raíz
```
SELECT * FROM nodes WHERE parent_id IS NULL;
```
Listar todos os arquivos e diretórios dentro de Documents (id = 1)
```
SELECT * FROM nodes WHERE parent_id = 1;
```
Listar toda a estrutura hierárquica (diretórios e subdiretórios)
```
WITH RECURSIVE dir_structure AS (
    SELECT id, name, parent_id, type
    FROM nodes
    WHERE parent_id IS NULL  
    UNION ALL
    SELECT n.id, n.name, n.parent_id, n.type
    FROM nodes n
    INNER JOIN dir_structure ds ON ds.id = n.parent_id
)
SELECT * FROM dir_structure;

```

# Procedimentos:
  1. Forkar e clonar o repositório
  2. Acessar o diretório clonado sistemas-arquivos-virtuais
     
     ```
     cd  sistemas-arquivos-virtuais
     ```
  4. Build da aplicação no terminal, dentro do diretório acessado anteriormente:
     ```
     ./mvnw clean install
     ```
  5. Iniciar a aplicação no terminal
     ```
      ./mvnw spring-boot:run
     ```
  6. Testes os endpoints /api/nodes aplicando o verbo HTTP adequado para (GET, POST, PUT e DELETE) no Insomnia.

# Teste API
Criar um diretório
```
POST /api/nodes
{
  "name": "Documents",
  "type": "DIRECTORY"
}
```
Criar um arquivo dentro de um diretório
```
POST /api/nodes
{
  "name": "file.txt",
  "type": "FILE",
  "parent": {
    "id": 1
  },
  "size": 12345
}
```
Listar todos os nós
```
GET /api/nodes
```
Listar nós por diretório pai
```
GET /api/nodes/parent/1
```
Atualizar um nó
```
PUT /api/nodes/1
{
  "name": "new_name",
  "type": "DIRECTORY"
}
```
Deletar um nó
```
DELETE /api/nodes/1
```

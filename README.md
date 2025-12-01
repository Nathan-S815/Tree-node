# 🌳 TreeNode Management API

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
 [![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)](https://spring.io/projects/spring-boot)
 [![License](https://img.shields.io/badge/License-MIT-yellow)](https://chatgpt.com/c/LICENSE)

A RESTful API built with Spring Boot for managing hierarchical tree nodes.
 Supports creating, moving, deleting nodes, and retrieving descendants with depth information.

------

## Table of Contents

- [Features](https://chatgpt.com/c/692dd216-8efc-8322-86b7-949c08742547#features)
- [Environment](https://chatgpt.com/c/692dd216-8efc-8322-86b7-949c08742547#environment)
- [Installation](https://chatgpt.com/c/692dd216-8efc-8322-86b7-949c08742547#installation)
- [Project Structure](https://chatgpt.com/c/692dd216-8efc-8322-86b7-949c08742547#project-structure)
- [Database Setup](https://chatgpt.com/c/692dd216-8efc-8322-86b7-949c08742547#database-setup)
- [Application Configuration](https://chatgpt.com/c/692dd216-8efc-8322-86b7-949c08742547#application-configuration)
- [Tree Visualization](https://chatgpt.com/c/692dd216-8efc-8322-86b7-949c08742547#tree-visualization)
- [API Endpoints](https://chatgpt.com/c/692dd216-8efc-8322-86b7-949c08742547#api-endpoints)

------

## Features

- Create child nodes
- Delete nodes and their subtrees
- Move nodes to a new parent (preventing cycles)
- Retrieve all descendants with depth information
- Supports modern Java features (JDK21)
- Transactional operations ensure data consistency

------

## Environment

- JDK 21 or higher
- Maven 3.8+
- MySQL 8+
- Spring Boot 3.x

------

## Installation

1. Clone the project:

```bash
git clone https://github.com/your-repo/treenode.git
cd treenode
```

1. Configure the database (application.yml or application.properties)
2. Build and run:

```bash
mvn clean spring-boot:run
```

1. Access the API:

```
http://localhost:7900/api/nodes
```

------

## Project Structure

```
treenode/
├─ src/
│  ├─ main/
│  │  ├─ java/com/example/treenode/
│  │  │  ├─ controller/       # Controller layer
│  │  │  │  └─ NodeController.java
│  │  │  ├─ service/          # Service layer
│  │  │  │  └─ NodeService.java
│  │  │  ├─ repository/       # Repository layer
│  │  │  │  └─ NodeRepository.java
│  │  │  ├─ entity/           # Entity classes
│  │  │  │  └─ NodeEntity.java
│  │  │  └─ dto/              # Data Transfer Objects
│  │  │     ├─ NodeDto.java
│  │  │     ├─ CreateNodeRequest.java
│  │  │     └─ MoveNodeRequest.java
│  │  └─ resources/
│  │     └─ application.yml
│  └─ test/
│     └─ java/com/example/treenode/
│        └─ NodeServiceTest.java
└─ pom.xml
```

------

## Database Setup

```sql
CREATE DATABASE IF NOT EXISTS treenode CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE treenode;

CREATE TABLE t_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT NULL,
    CONSTRAINT fk_parent FOREIGN KEY (parent_id) REFERENCES t_node(id) ON DELETE CASCADE
);

-- Insert root node
INSERT INTO t_node (id, name, parent_id) VALUES (1, 'ROOT', NULL);
```

------

## Application Configuration

```yaml
server:
  port: 7900
spring:
  datasource:
    url: jdbc:mysql://127.0.0.0.1:3306/your_database?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
    username: 
    password: ""
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

```

------

## Tree Visualization

### ASCII Tree

```
ROOT
├─ Node 1
│  ├─ Node 1.1
│  └─ Node 1.2
└─ Node 2
   └─ Node 2.1
```

### Mermaid Visualization

```mermaid
graph TD
    ROOT --> Node1
    ROOT --> Node2
    Node1 --> Node1_1
    Node1 --> Node1_2
    Node2 --> Node2_1
```

------

## API Endpoints

### Get All Nodes

```
GET /api/nodes
```

### Create Child Node

```
POST /api/nodes/{parentId}/children
Content-Type: application/json

{
  "name": "Child Node"
}
```

### Delete Child Node

```
DELETE /api/nodes/{parentId}/children/{childId}
```

### Move Node

```
PUT /api/nodes/{nodeId}/move
Content-Type: application/json

{
  "newParentId": 3
}
```

### Get All Descendants

```
GET /api/nodes/{id}/descendants
```

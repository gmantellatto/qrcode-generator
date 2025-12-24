# 🔲 QR Code Generator

Aplicação Spring Boot para geração de QR Codes com armazenamento automático na AWS S3.

## 📋 Descrição

Esta aplicação RESTful permite a geração de QR Codes a partir de qualquer texto. Os QR Codes gerados são automaticamente armazenados na AWS S3 e uma URL pública é retornada para acesso à imagem.

### Principais Características

- ✅ Geração de QR Codes através de API REST
- ✅ Upload automático para AWS S3
- ✅ Retorno de URL pública para acesso à imagem
- ✅ Arquitetura Hexagonal (Ports & Adapters)
- ✅ Containerização com Docker
- ✅ QR Codes de 250x250 pixels em formato PNG

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spring Boot 4.0.1** - Framework web
- **Maven 3.9.6** - Gerenciador de dependências
- **ZXing (3.5.2)** - Biblioteca para geração de QR Codes
- **AWS SDK for Java** - Integração com S3
- **Lombok** - Redução de boilerplate
- **Docker** - Containerização
- **Eclipse Temurin 21** - JRE para execução

## 📦 Arquitetura

Projeto segue o padrão de **Arquitetura Hexagonal**:

```
src/main/java/com/gustavom/qrcode/generator/
├── controller/           # Camada de apresentação (API REST)
│   └── QRCodeController.java
├── dto/                  # Objetos de transferência de dados
│   ├── QRCodeGenerateRequest.java
│   └── QRCodeGenerateResponse.java
├── service/              # Regras de negócio
│   └── QRCodeGenerateService.java
├── ports/                # Interfaces (abstração)
│   └── StoragePort.java
└── infrastructure/       # Adaptadores (implementação)
    └── S3StorageAdapter.java
```

### Fluxo de Funcionamento

1. **Controller** recebe requisição POST com texto
2. **Service** gera QR Code usando ZXing (250x250 pixels)
3. **StoragePort** define contrato de armazenamento
4. **S3StorageAdapter** implementa upload para AWS S3
5. **Response** retorna URL pública da imagem

## 🚀 Como Rodar

### Pré-requisitos

- Java 21 ou superior
- Maven 3.9+ (ou use o wrapper `./mvnw`)
- Conta AWS com bucket S3 configurado
- Docker (opcional)

### Opção 1: Executar Localmente com Maven

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd qrcode.generator

# 2. Configure as variáveis de ambiente
export AWS_REGION=us-east-2
export AWS_BUCKET_NAME=seu-bucket-name
export AWS_ACCESS_KEY_ID=sua-access-key
export AWS_SECRET_ACCESS_KEY=sua-secret-key

# 3. Compile o projeto
./mvnw clean package

# 4. Execute a aplicação
java -jar target/qrcode.generator-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em `http://localhost:8080`

### Opção 2: Executar com Docker

#### 1. Criar arquivo `.env`

Copie o arquivo de exemplo e configure suas credenciais AWS:

```bash
# Copiar o template
cp .env.example .env

# Editar com suas credenciais reais
nano .env
```

O arquivo `.env` deve conter:

```bash
AWS_REGION=us-east-2
AWS_BUCKET_NAME=seu-bucket-name
AWS_ACCESS_KEY_ID=sua-access-key
AWS_SECRET_ACCESS_KEY=sua-secret-key
```

⚠️ **Importante**: O arquivo `.env` já está no `.gitignore` e não será versionado!

#### 2. Build da imagem

```bash
docker build -t qrcode-generator:1.0 .
```

#### 3. Execute o container

```bash
docker run --env-file .env -p 8080:8080 qrcode-generator:1.0
```

A aplicação estará disponível em `http://localhost:8080`

## 📡 Como Consumir o Endpoint

### Endpoint: Gerar QR Code

**POST** `/qrcode`

Gera um QR Code a partir do texto fornecido e retorna a URL da imagem armazenada no S3.

#### Request

```http
POST http://localhost:8080/qrcode
Content-Type: application/json

{
  "text": "https://github.com"
}
```

#### Response - Sucesso (200 OK)

```json
{
  "url": "https://qrcode-new-generator-storage.s3.us-east-2.amazonaws.com/abc123-def456.png"
}
```

#### Response - Erro (500 Internal Server Error)

```http
HTTP/1.1 500 Internal Server Error
```

### Exemplos de Uso

#### cURL

```bash
curl -X POST http://localhost:8080/qrcode \
  -H "Content-Type: application/json" \
  -d '{"text": "https://github.com"}'
```

#### HTTPie

```bash
http POST localhost:8080/qrcode text="https://github.com"
```

#### JavaScript (Fetch API)

```javascript
fetch('http://localhost:8080/qrcode', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    text: 'https://github.com'
  })
})
  .then(response => response.json())
  .then(data => console.log('QR Code URL:', data.url))
  .catch(error => console.error('Erro:', error));
```

#### Python (requests)

```python
import requests

response = requests.post(
    'http://localhost:8080/qrcode',
    json={'text': 'https://github.com'}
)

if response.status_code == 200:
    qr_url = response.json()['url']
    print(f'QR Code URL: {qr_url}')
```

#### Postman

1. Método: **POST**
2. URL: `http://localhost:8080/qrcode`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "text": "Seu texto ou URL aqui"
}
```

## ⚙️ Configuração

### Variáveis de Ambiente

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `AWS_REGION` | Região AWS do bucket S3 | `us-east-2` |
| `AWS_BUCKET_NAME` | Nome do bucket S3 | `qrcode-storage` |
| `AWS_ACCESS_KEY_ID` | Chave de acesso AWS | `AKIA...` |
| `AWS_SECRET_ACCESS_KEY` | Chave secreta AWS | `wJalrXUt...` |

### application.properties

```properties
spring.application.name=qrcode.generator

aws.region=${AWS_REGION}
aws.s3.bucket-name=${AWS_BUCKET_NAME}
```

## 🐳 Docker

### Multi-Stage Build

O Dockerfile utiliza build em duas etapas para otimizar o tamanho da imagem:

1. **Stage BUILD**: Compila com Maven (maven:3.9.6-eclipse-temurin-21)
2. **Stage RUNTIME**: Executa com JRE otimizado (eclipse-temurin:21-jre)

## 📁 Estrutura do Projeto

```
qrcode.generator/
├── src/
│   ├── main/
│   │   ├── java/com/gustavom/qrcode/generator/
│   │   │   ├── Application.java
│   │   │   ├── controller/
│   │   │   │   └── QRCodeController.java
│   │   │   ├── dto/
│   │   │   │   ├── QRCodeGenerateRequest.java
│   │   │   │   └── QRCodeGenerateResponse.java
│   │   │   ├── service/
│   │   │   │   └── QRCodeGenerateService.java
│   │   │   ├── ports/
│   │   │   │   └── StoragePort.java
│   │   │   └── infrastructure/
│   │   │       └── S3StorageAdapter.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/gustavom/qrcode/generator/
│           └── ApplicationTests.java
├── .env.example
├── .gitignore
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

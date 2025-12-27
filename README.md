# 🔲 QR Code Generator

Aplicação Spring Boot para geração de QR Codes com armazenamento automático na AWS S3.

## 📋 Descrição

Esta aplicação RESTful permite a geração de QR Codes a partir de qualquer texto. Os QR Codes gerados são automaticamente armazenados na AWS S3 e uma URL pública é retornada para acesso à imagem.

### Principais Características

- ✅ Geração de QR Codes através de API REST
- ✅ Upload automático para AWS S3
- ✅ Retorno de URL pública para acesso à imagem
- ✅ Arquitetura Hexagonal (Ports & Adapters)
- ✅ Tratamento robusto de exceções
- ✅ Testes unitários com Mockito
- ✅ Containerização com Docker
- ✅ QR Codes de 250x250 pixels em formato PNG

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spring Boot 3.4.1** - Framework web
- **Maven 3.9.6** - Gerenciador de dependências
- **ZXing 3.5.2** - Biblioteca para geração de QR Codes
- **AWS SDK for Java 2.29.29** - Integração com S3
- **JUnit 5** - Framework de testes
- **Mockito 5.x** - Biblioteca para mocks em testes
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
├── infrastructure/       # Adaptadores (implementação)
│   └── S3StorageAdapter.java
└── exception/            # Tratamento de exceções customizadas
    ├── GlobalExceptionHandler.java
    ├── QRCodeGenerationException.java
    └── QRCodeUploadException.java
```

### Fluxo de Funcionamento

1. **Controller** recebe requisição POST com texto
2. **Service** valida entrada e gera QR Code usando ZXing (250x250 pixels)
3. **StoragePort** define contrato de armazenamento
4. **S3StorageAdapter** implementa upload para AWS S3
5. **Response** retorna URL pública da imagem
6. **GlobalExceptionHandler** captura e trata exceções apropriadamente

## 🧪 Testes

O projeto inclui testes unitários abrangentes para garantir a qualidade do código.

### Executar Testes

```bash
# Executar todos os testes
./mvnw test

# Executar com relatório detalhado
./mvnw test --debug

# Executar testes de uma classe específica
./mvnw test -Dtest=QRCodeGenerateServiceTest
```

### Estrutura de Testes

```
src/test/java/com/gustavom/qrcode/generator/
├── ApplicationTests.java              # Testes de contexto Spring
├── controller/
│   └── QRCodeControllerIntegrationTest.java
├── service/
│   └── QRCodeGenerateServiceTest.java # Testes unitários do service
└── exception/
    └── GlobalExceptionHandlerTest.java
```

### Cenários Testados

- ✅ Geração e upload de QR Code com texto válido
- ✅ Validação de entrada vazia
- ✅ Validação de entrada nula
- ✅ Validação de texto com apenas espaços
- ✅ Tratamento de erro no upload para S3
- ✅ Tratamento de erro na geração do QR Code
- ✅ Captura correta de exceções pelo GlobalExceptionHandler
- ✅ Respostas HTTP apropriadas para cada tipo de erro

### Exemplo de Teste Unitário

```java
@Test
@DisplayName("Deve gerar QR Code e fazer upload com sucesso")
void shouldGenerateAndUploadQRCodeSuccessfully() {
    // Arrange
    String text = "https://example.com";
    String expectedUrl = "https://storage.example.com/qrcode.png";
    
    when(storagePort.uploadFile(any(byte[].class), anyString(), eq("image/png")))
        .thenReturn(expectedUrl);

    // Act
    QRCodeGenerateResponse result = qrCodeGenerateService.generateAndUploadQRCode(text);

    // Assert
    assertNotNull(result);
    assertEquals(expectedUrl, result.url());
    verify(storagePort, times(1)).uploadFile(any(byte[].class), anyString(), eq("image/png"));
}
```

## 🚨 Tratamento de Erros

A aplicação possui tratamento robusto de exceções com respostas HTTP apropriadas.

### Exceções Customizadas

#### QRCodeGenerationException

Lançada quando há falha na geração do QR Code (problemas com a biblioteca ZXing).

```java
throw new QRCodeGenerationException("Falha ao codificar o QR Code", text, e);
```

**Resposta HTTP:** `500 Internal Server Error`

#### QRCodeUploadException

Lançada quando há falha no upload para AWS S3 (timeout, credenciais inválidas, etc.).

```java
throw new QRCodeUploadException("Falha ao fazer upload", e);
```

**Resposta HTTP:** `500 Internal Server Error`

#### IllegalArgumentException

Lançada quando a entrada é inválida (texto vazio, nulo ou apenas espaços).

```java
if (text == null || text.isBlank()) {
    throw new IllegalArgumentException("O texto não pode ser vazio");
}
```

**Resposta HTTP:** `400 Bad Request`

### GlobalExceptionHandler

Centraliza o tratamento de todas as exceções da aplicação usando `@RestControllerAdvice`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(QRCodeGenerationException.class)
    public ResponseEntity<ErrorResponse> handleQRCodeGenerationException(QRCodeGenerationException ex) {
        ErrorResponse error = new ErrorResponse("Erro ao gerar QR Code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    // ... outros handlers
}
```

### Respostas de Erro

#### Erro 400 - Bad Request

Entrada inválida (texto vazio, nulo ou apenas espaços).

```json
{
  "message": "O texto não pode ser vazio",
  "status": 400,
  "timestamp": "2025-12-27T18:30:00"
}
```

#### Erro 500 - Internal Server Error

Falha na geração do QR Code ou upload para S3.

```json
{
  "message": "Erro ao gerar QR Code",
  "status": 500,
  "timestamp": "2025-12-27T18:30:00"
}
```

```json
{
  "message": "Erro ao fazer upload do QR Code",
  "status": 500,
  "timestamp": "2025-12-27T18:30:00"
}
```

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

# 3. Compile e teste o projeto
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

#### Response - Erro de Validação (400 Bad Request)

```json
{
  "message": "O texto não pode ser vazio",
  "status": 400,
  "timestamp": "2025-12-27T18:30:00"
}
```

#### Response - Erro Interno (500 Internal Server Error)

```json
{
  "message": "Erro ao gerar QR Code",
  "status": 500,
  "timestamp": "2025-12-27T18:30:00"
}
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
  .then(response => {
    if (!response.ok) {
      throw new Error('Erro ao gerar QR Code');
    }
    return response.json();
  })
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
else:
    print(f'Erro: {response.status_code} - {response.json()}')
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
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── QRCodeGenerateRequest.java
│   │   │   │   └── QRCodeGenerateResponse.java
│   │   │   ├── service/
│   │   │   │   └── QRCodeGenerateService.java
│   │   │   ├── ports/
│   │   │   │   └── StoragePort.java
│   │   │   ├── infrastructure/
│   │   │   │   └── S3StorageAdapter.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── QRCodeGenerationException.java
│   │   │       └── QRCodeUploadException.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/gustavom/qrcode/generator/
│       │   ├── ApplicationTests.java
│       │   ├── controller/
│       │   │   └── QRCodeControllerIntegrationTest.java
│       │   ├── service/
│       │   │   └── QRCodeGenerateServiceTest.java
│       │   └── exception/
│       │       └── GlobalExceptionHandlerTest.java
│       └── resources/
│           └── application-test.properties
├── .env.example
├── .gitignore
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 📝 Boas Práticas Implementadas

- ✅ Arquitetura Hexagonal (Ports & Adapters)
- ✅ Separação de responsabilidades
- ✅ Injeção de dependências
- ✅ Testes unitários com mocks
- ✅ Tratamento de exceções customizado
- ✅ Validação de entrada
- ✅ Uso de DTOs (Records)
- ✅ Containerização com Docker
- ✅ Configuração externalizada
- ✅ Logs apropriados (via SLF4J)
- ✅ Respostas HTTP consistentes e semânticas


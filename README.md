# APK Scanner and Processor

A Spring Boot application for processing Android APK files, including batch uploading, virus scanning, signature replacement, package name modifications, and uploading to object storage services.

## Features

- Upload APK files
- Process APK files (decompile, modify, recompile)
- Replace package names
- Sign APK files with custom signatures
- Upload processed APK files to object storage (Alibaba OSS, Tencent COS)
- Track task status and results

## Technology Stack

- Java 8+
- Spring Boot 2.x
- Maven
- MySQL with MyBatis Plus
- RESTful API
- Object Storage Integration (Alibaba OSS, Tencent COS)

## Prerequisites

- JDK 8 or higher
- Maven
- MySQL
- APKTool (for APK decompilation and recompilation)
- Zipalign (for APK alignment)
- Jarsigner (for APK signing)

## Setup

1. Clone the repository
2. Configure MySQL database (see `src/main/resources/schema.sql`)
3. Update `application.yml` with your database credentials
4. Build the project: `mvn clean package`
5. Run the application: `java -jar target/apktool-0.0.1-SNAPSHOT.jar`

## API Endpoints

### APK Processing

- `POST /api/upload` - Upload an APK file
- `POST /api/task/create` - Create a new processing task
- `POST /api/task/start/{id}` - Start a processing task
- `GET /api/task/{id}` - Get task status and result

### Signature Management

- `POST /api/signature` - Add a new signature configuration
- `GET /api/signatures` - Get all signature configurations
- `DELETE /api/signature/{id}` - Delete a signature configuration

### OSS Configuration

- `POST /api/oss` - Add a new OSS configuration
- `GET /api/oss` - Get all OSS configurations
- `DELETE /api/oss/{id}` - Delete an OSS configuration

## Task Processing Flow

1. User uploads APK → Store locally
2. Create task with signature and OSS configurations
3. Start task processing
4. Decompile APK using APKTool
5. Modify package name in AndroidManifest.xml and smali files
6. Recompile APK
7. Align and sign APK
8. Upload to object storage
9. Return task status and download URL

## Configuration

The application uses `application.yml` for configuration. Key settings include:

- Server port
- Database connection
- File upload limits
- Upload directory path

## License

This project is licensed under the MIT License - see the LICENSE file for details.

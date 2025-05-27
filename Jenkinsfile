pipeline {
    agent any

    tools {
        jdk 'OpenJDK-17' // O la versión de JDK que tu proyecto Java necesite
        maven 'Maven3'   // O el nombre de tu configuración de Maven
    }

    environment {
        SONAR_TOKEN_CRED_ID = 'sonarqube'
        SONAR_HOST_URL = 'http://sonarqube:9000'
        SONARQUBE_SERVER_CONFIG_NAME = 'sonarqube' // Nombre de la config del servidor SonarQube en Jenkins

        // Nombres según tu docker-compose.yml y Dockerfile
        DOCKER_IMAGE_NAME = "micro/product" // De tu docker-compose.yml
        DOCKER_IMAGE_TAG_FROM_COMPOSE = "1.0.0" // De tu docker-compose.yml
        DOCKER_CONTAINER_NAME = "product_app_2025" // De tu docker-compose.yml
        
        // Nombre del JAR según tu Dockerfile ARG
        // Es mejor si esto se puede inferir del pom.xml o si es consistente.
        // Si el pom.xml define <finalName>micro-product</finalName> y <version>0.0.1-SNAPSHOT</version>
        // entonces el JAR será target/micro-product-0.0.1-SNAPSHOT.jar
        EXPECTED_JAR_NAME = "micro-product-0.0.1-SNAPSHOT.jar" 
    }

    stages {
        stage('📥 Checkout SCM') {
            steps {
                echo "=== INICIANDO CHECKOUT ==="
                checkout scm
                echo "Checkout completado."
            }
        }

        stage('🛠️ Build & Unit Test (Maven)') {
            steps {
                echo "=== COMPILANDO PROYECTO Y EJECUTANDO TESTS UNITARIOS CON MAVEN ==="
                sh "mvn clean package -DskipTests=false" // Asegúrate que los tests se ejecuten
                echo "✅ Build y empaquetado completados. Archivo JAR esperado: ./target/${env.EXPECTED_JAR_NAME}"
                // Verificar que el JAR existe
                sh "test -f target/${env.EXPECTED_JAR_NAME} && echo 'JAR encontrado' || (echo 'ERROR: JAR NO ENCONTRADO target/${env.EXPECTED_JAR_NAME}' && exit 1)"
                archiveArtifacts artifacts: "target/${env.EXPECTED_JAR_NAME}", fingerprint: true
            }
        }

        stage('📊 SonarQube Analysis (Maven)') {
            steps {
                echo "=== EJECUTANDO ANÁLISIS SONARQUBE CON MAVEN PLUGIN ==="
                withCredentials([string(credentialsId: env.SONAR_TOKEN_CRED_ID, variable: 'SONAR_LOGIN_TOKEN')]) {
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.host.url=${env.SONAR_HOST_URL} \
                          -Dsonar.login=${SONAR_LOGIN_TOKEN} \
                          -Dsonar.projectKey=${env.DOCKER_CONTAINER_NAME} \
                          -Dsonar.projectName='${env.DOCKER_CONTAINER_NAME} (Micro Product)'
                    """
                }
                echo "✅ Análisis de SonarQube con Maven completado."
            }
        }

        stage('🚪 Quality Gate') {
            steps {
                echo "=== VERIFICANDO QUALITY GATE DE SONARQUBE ==="
                timeout(time: 10, unit: 'MINUTES') {
                    def qg = waitForQualityGate serverName: env.SONARQUBE_SERVER_CONFIG_NAME, abortPipeline: true
                    echo "✅ Quality Gate PASÓ exitosamente."
                }
                echo "=== FIN QUALITY GATE ==="
            }
        }

        stage('🐳 Build Docker Image (using docker-compose)') {
            // Docker Compose se encargará de construir la imagen según la sección 'build:'
            // No necesitamos un 'docker build' separado si docker-compose lo maneja.
            // Solo nos aseguramos de que el contexto y Dockerfile sean correctos.
            steps {
                echo "=== PREPARANDO PARA CONSTRUIR IMAGEN DOCKER CON DOCKER-COMPOSE ==="
                echo "Docker Compose usará el contexto '..' y el Dockerfile 'docker/Dockerfile'"
                echo "La imagen se llamará '${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG_FROM_COMPOSE}'"
                // No hay un paso explícito de 'docker build' aquí porque docker-compose build o up --build lo hará.
            }
        }
        
        stage('🚀 Deploy to Docker (using docker-compose)') {
            steps {
                echo "=== DESPLEGANDO APLICACIÓN CON DOCKER COMPOSE ==="
                script {
                    // El docker-compose.yml está en la raíz del proyecto checkout (según tu estructura de VSCode)
                    // pero el 'context: ..' en el docker-compose.yml es relativo al propio docker-compose.yml.
                    // Si el Jenkinsfile está en la raíz y docker-compose.yml está en ./docker/
                    def composeFile = "docker/docker-compose.yml"

                    sh "docker-compose -f ${composeFile} down --remove-orphans || true"
                    
                    // 'up --build -d' reconstruirá la imagen si es necesario y luego la levantará.
                    sh "docker-compose -f ${composeFile} up --build -d" 
                    
                    echo "✅ Aplicación desplegada. Contenedor: ${env.DOCKER_CONTAINER_NAME}"
                    echo "Esperando a que la aplicación inicie (30 segundos)..."
                    sh "sleep 30" 
                }
            }
        }

        stage('🔍 Validate Deployment') {
            steps {
                echo "=== VALIDANDO DESPLIEGUE ==="
                echo "Accede a las siguientes URLs en tu navegador:"
                echo "Consola H2: http://localhost:8086/h2-console/"
                echo "Swagger UI: http://localhost:8086/swagger-ui/index.html#/"
                
                // Prueba de conectividad básica
                sh "curl -s -f -o /dev/null http://localhost:8086/swagger-ui/index.html || (echo 'ERROR: Swagger UI no accesible en http://localhost:8086/swagger-ui/index.html' && exit 1)"
                sh "curl -s -f -o /dev/null http://localhost:8086/h2-console || (echo 'ERROR: H2 Console no accesible en http://localhost:8086/h2-console' && exit 1)"
                echo "✅ Pruebas de conectividad básicas pasaron."
            }
        }
    }

    post {
        always {
            echo "=== POST-PROCESO SIEMPRE ==="
            script {
                def composeFile = "docker/docker-compose.yml"
                // Descomentar si quieres limpiar después de cada build, incluso si falla
                // echo "Limpiando contenedores de Docker Compose..."
                // sh "docker-compose -f ${composeFile} down --remove-orphans || true"
            }
            cleanWs()
            echo "✅ Workspace limpio"
        }
        success {
            echo "🎉 ¡PIPELINE COMPLETADO EXITOSAMENTE!"
        }
        failure {
            echo "💥 PIPELINE FALLÓ"
            // Es útil dejar los contenedores arriba si falla para poder depurar.
            // Si quieres limpiarlos siempre, mueve el 'docker-compose down' a 'always'.
        }
    }
}
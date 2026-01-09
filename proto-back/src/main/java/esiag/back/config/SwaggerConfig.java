package esiag.back.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI pour la documentation de l'API REST.
 * Accessible via /swagger-ui.html ou /swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI parkingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestion de Parking - SIRIUS ING2")
                        .description("API REST pour la gestion d'un parking intelligent. " +
                                "Cette API permet de gérer les places de parking, les réservations, " +
                                "les stationnements, les véhicules, les personnes et les services annexes.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe SIRIUS ING2")
                                .email("contact@sirius-ing2.fr"))
                        .license(new License()
                                .name("Licence MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Serveur de développement local")
                ));
    }
}

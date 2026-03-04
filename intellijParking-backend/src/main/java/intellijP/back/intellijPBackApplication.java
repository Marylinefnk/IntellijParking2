package intellijP.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import intellijP.back.services.ReservationPlaceService;
import intellijP.back.services.GuidageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class intellijPBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(intellijPBackApplication.class, args);
	}
    @Bean
    public CommandLineRunner demo(GuidageService guidageService) {
        return args -> {
            try {
                System.out.println("\n\n");
                System.out.println("DÉMARRAGE DE LA DÉMONSTRATION");
                guidageService.afficherToutesLesReservations();

                int ida = 18;
                guidageService.afficherCheminVersPlace(Long.valueOf(ida));
            } catch (Exception e) {
                System.out.println("[DEMO] Skipped: " + e.getMessage());
            }
        };
    }
}

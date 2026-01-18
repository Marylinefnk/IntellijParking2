package esiag.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import esiag.back.services.ReservationPlaceService;
import esiag.back.services.GuidageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EsiagBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsiagBackApplication.class, args);
	}
    @Bean
    public CommandLineRunner demo(GuidageService guidageService) {
        return args -> {
            System.out.println("\n\n");
            System.out.println("DÉMARRAGE DE LA DÉMONSTRATION");


            guidageService.afficherToutesLesReservations();

            int id = 1;
            guidageService.afficherCheminVersPlace(Long.valueOf(id));

        };
    }
}

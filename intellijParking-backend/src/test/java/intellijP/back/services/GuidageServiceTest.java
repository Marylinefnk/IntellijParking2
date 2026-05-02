package intellijP.back.services;

import intellijP.back.models.Noeud;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class GuidageServiceTest {

    @Test
    void testCalculeDistanceEntre2Noeuds() {
        Noeud noeud1 = new Noeud();
        noeud1.setPositionX(0.0);
        noeud1.setPositionY(0.0);

        Noeud noeud2 = new Noeud();
        noeud2.setPositionX(3.0);
        noeud2.setPositionY(4.0);

        double result = GuidageService.calculerDistance(noeud1, noeud2);
        assertEquals(5.0, result, 0.001);

    }

}
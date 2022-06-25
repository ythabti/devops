package com.esprit.examen.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import com.esprit.examen.entities.Contrat;
import com.esprit.examen.entities.Cours;
import com.esprit.examen.entities.Formateur;
import com.esprit.examen.entities.Poste;
import com.esprit.examen.entities.Session;
import com.esprit.examen.entities.TypeCours;
import com.esprit.examen.repositories.FormateurRepository;
import com.esprit.examen.repositories.SessionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.event.TransactionalEventListener;
@RunWith(SpringRunner.class)
@SpringBootTest
public class SessionServiceTest {

    @Autowired
    SessionRepository  sr;
    @Autowired
    ICoursService  cs;
    @Autowired
    IFormateurService  fs;
    @Autowired
    ISessionService  ss;
    @Autowired
    FormateurRepository  fr;
    private static final Logger l = LogManager.getLogger(SessionServiceTest.class);
    @Test
    @TransactionalEventListener    
    public void testAddSession() {

        Session session = new Session(null, new Date(20141212), new Date(20201212), 50L, "description");
        Long dataPreTest = sr.count();
        Session savedSession =sr.save(session); 
		Long dataAfterTest = sr.count();
		assertThat(dataPreTest).isEqualTo(dataAfterTest -1);
        assertThat(savedSession.getId()).isEqualTo(session.getId());
        l.info("add session : "+ session);
        ss.supprimerSession(session.getId());
        
    }

    @Test
    public void testAffecterFormateurASession() {


        Session session = new Session(null, new Date(20141212), new Date(20201212), 50L, "description");
        Formateur formateur = new Formateur(null,"ayari","sami",Poste.Ingénieur,Contrat.CDI,"h@gmail.com","psdpsd");
        Session savedSession = sr.save(session);
        Formateur savedFormateur = fr.save(formateur);
        ss.affecterFormateurASession(savedFormateur.getId(), savedSession.getId());
        Session afterSession = sr.getOne(savedSession.getId());
        assertThat(afterSession.getFormateur().getId()).isEqualTo(savedFormateur.getId());
        l.info("formateur : "+ formateur + "affecter a" +  session);


    }

    @Test
    public void testModifierSession() {
        Session session = new Session(null, new Date(20141212), new Date(20201212), 50L, "description");
        Formateur formateur = new Formateur(null,"ayari","sami",Poste.Ingénieur,Contrat.CDI,"h@gmail.com","psdpsd");
        formateur.setAdmin(true);
        session.setDescription("modified");
        ss.modifierSession(session);
		assertThat(session.getDescription()).isEqualTo("modified");
        l.info(" session : "+ session + "modified");

    }

    @Test
    public void testSupprimerSession() {
        Session session = new Session(null, new Date(20151212), new Date(20201010), 50L, "description");
        Session savedSession =sr.save(session);
        Long dataPreTest = sr.count();
        ss.supprimerSession(savedSession.getId());
        Long dataAfterTest = sr.count();
		assertThat(dataPreTest).isEqualTo(dataAfterTest +1);
        l.info(" this session has been deleted : "+ session);

    }

    @Test
    public void testPlusLongSession() {
        Session session = ss.plusLongSession();
        Long oldDuration = session.getDuree();
        session.setDuree(oldDuration++);
        sr.save(session);
        assertThat(ss.plusLongSession().getDuree()).isEqualTo(session.getDuree());
        l.info(" the longest session is : "+ ss.plusLongSession());
    }
}

package leksjon.entitet1;

import javax.persistence.*;
import java.util.List;
import javax.swing.*;

public class KontoDAO{
    private EntityManagerFactory emf;
    /* OBS! EntityManagerFactory er thread safe, men det er ikke 
    * EntityManger! Objektvariabel medf�rer at vi m� synkronisere metodene.
    * Vi l�ser det med � ha EntityManger bare lokalt. Unng�r tr�dproblematikk!
    */
    //private EntityManager em;
  
            
    public KontoDAO(EntityManagerFactory emf){
        this.emf = emf;
    }
    
    //Metoden er laget for � lagre nye b�ker
    //Merk at persist() vil fungere som en SQL INSERT
    //Boka (ISBN) kan derfor ikke vre lagret i DB fra f�r!!
    public void lagreNyKonto(Konto konto){
        EntityManager em  = getEM();
        try{
             em.getTransaction().begin();
             em.persist(konto);//f�rer boka inn i lagringskontekt (persistence context)
             em.getTransaction().commit();//lagring skjer her
        }finally{
            lukkEM(em);
        }
    }
    
    //Finner en bok basert p� prim�rtn�kkel
    public Konto finnKonto(int nr){
        EntityManager em = getEM();
        try{
            return em.find(Konto.class, nr);
        }finally{
            lukkEM(em);
        }
    }
    
    //Endrer en eksisterenede bok, vi bruker merge for � sikre at boka
    //f�res inn i lagringskonteksten (m� det om den har v�rt serialisert)
    public void endreKonto(Konto konto){
        EntityManager em = getEM();
        try{
            em.getTransaction().begin();
            Konto b = em.merge(konto);//s�rger for � f�re entiteten inn i lagringskonteksten
            em.getTransaction().commit();//merk at endringene gjort utenfor transaksjonen blir lagret!!!
        }finally{
            lukkEM(em);
        }
    }
    
     public void overfør(Konto k1, Konto k2, double sum) {
        EntityManager em = getEM();
        try{
            em.getTransaction().begin();
            
            JOptionPane.showConfirmDialog(null, "Her er det pause");

            k1.trekk(sum);
            k2.trekk(-sum);
            em.merge(k1);
            em.merge(k2);
            
            em.getTransaction().commit();//merk at endringene gjort utenfor transaksjonen blir lagret!!!
        
        }finally{
            lukkEM(em);
        }
    }
    
    public void slettKonto(int kontoNr){
        EntityManager em = getEM();
        try{
            Konto b = finnKonto(kontoNr);
            em.getTransaction().begin();
            em.remove(b);//remove m� kalles i en transaksjon
            em.getTransaction().commit();
        }finally{
            lukkEM(em);
        }
    }
    
    //sprring som henter alle bker
    public List<Konto> getAlleKonto(){
        EntityManager em = getEM();
        try{
            Query q = em.createQuery("SELECT OBJECT(o) FROM Konto o");
            //SELECT o FROM BOK o gir samme resultat
            //MERK at Bok m� ha stor B (samme som klassenavn)
            return q.getResultList();
        }finally{
            lukkEM(em);
        }
    }
    
    //her bruker vi en navngitt sprring (NamedQuery). Denne finner du i Bok-klassen
    //Slike legges alts i entitetsklassen og gir
    //mulighet for optimalisering av sprring ala PreparedStatement
    public int getAntallKonto(){
        EntityManager em = getEM();
        try{
            Query q = em.createNamedQuery("finnAntallKonto");
            Long ant = (Long)q.getSingleResult();
            return ant.intValue();
        }finally{
            lukkEM(em);
        }
    }
    
    //Merk at begge sprringene i metoden fungerer (en utkommentert) 
    //Ofte kan nok den frste vre  foretrekke.
    public List<Konto> getKontoForEier(String eier){
        EntityManager em = getEM();
        try{
            Query q = em.createQuery("SELECT OBJECT(a) FROM Konto a WHERE a.eier= :eier");
            //Query q = em.createQuery("SELECT OBJECT(a) FROM Bok a WHERE a.forfatter='" +forfatter + "'");
            q.setParameter("eier",eier);
            return q.getResultList();
        }finally{
            lukkEM(em);
        }
    }
     
    private EntityManager getEM(){
        return emf.createEntityManager();
    }
    
    private void lukkEM(EntityManager em){
        if (em != null && em.isOpen()) em.close();
    }
       
    /* En liten "testklient" */
    public static void main(String args[]) throws Exception{
        EntityManagerFactory emf = null;
        KontoDAO fasade = null;
        System.out.println("starter2...");
        try{
            emf = Persistence.createEntityManagerFactory("EntitetPU");
	    //LeksjonStandaloneEntitetPU=Persistence Unit Name, se persistence.xml
            System.out.println("konstruktor ferdig " + emf);
            fasade = new KontoDAO(emf);
            System.out.println("konstruktor ferdig");
            
            int konr1 = Integer.parseInt(JOptionPane.showInputDialog(null, "Konto nr 1"));
            int konr2 = Integer.parseInt(JOptionPane.showInputDialog(null, "Konto nr 2"));
            int belop = Integer.parseInt(JOptionPane.showInputDialog(null, "belop fra nr 1 til 2"));
            
            Konto konto = fasade.finnKonto(3);
            fasade.overfør(fasade.finnKonto(konr1), fasade.finnKonto(konr2), belop);
            
//            //lager en bok med setMetodene i bok
//            Konto konto = new Konto();
//            konto.setKontoNr(3);
//            konto.setEier("Ivar");
//            konto.setSaldo(100);
//            fasade.lagreNyKonto(konto);//lagrer boka
//            
//            //lager en ny bok med konstruktor i stedet for setMetodene
//            konto = new Konto(4, "Roobie", 5);//tar alle parametre Id som lages automatisk
//            fasade.lagreNyKonto(konto);       
//            
//            //Skriv ut bkene som er lagret
//            System.out.println("Følgende kontoer er lagret i DB:");
//            List<Konto> liste = fasade.getAlleKonto();
//            for (Konto b : liste){
//                System.out.println("---" + b);
//            }
//            
//            konto = (Konto)liste.get(0);
//            konto.setEier("Endret tittel");
//            fasade.endreKonto(konto);
//            
//            konto = fasade.finnKonto(konto.getKontoNr());//henter ut boka p nytt
//            System.out.println("Konto er nå endret, og blitt slik: " + konto);
//            
//            //finner antall bker i db
//            int antall = fasade.getAntallKonto();
//            System.out.println("Antall konto i db=" +antall);
//            
//            //lister ut alle bker for en bestemt forfatter
//            liste = fasade.getKontoForEier("Ivar");
//            System.out.println("F�lgende b�ker finnes for denne forfatteren: " + liste.size());
//            for (Konto b : liste){
//                System.out.println("\t" + b.getKontoNr());
//            }
        }finally{
            emf.close();
        }
    }
}

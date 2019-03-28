package leksjon.entitet1;

import java.util.*;
import javax.persistence.*;
import java.io.*;

@Entity
@NamedQuery(name="finnAntallKonto", query="SELECT COUNT(o) from Konto o")

public class Konto implements Serializable{
    
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private int kontoNr;
    private double saldo;
    private String eier;
    
    @Version
    private int laas;
   
    public Konto(){}//m ha en konstruktor med tom parameterliste ihht JavaBeans standarden
    
    public Konto(int kontoNr, String eier, double saldo){
        this.kontoNr = kontoNr;
        this.eier = eier;
        this.saldo = saldo;
    }

    public int getKontoNr() {
        return kontoNr;
    }

    public void setKontoNr(int kontoNR) {
        this.kontoNr = kontoNR;
    }
 
    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getEier() {
        return eier;
    }

    public void setEier(String eier) {
        this.eier = eier;
    }
    
    public void trekk(double belop){
        this.saldo -= belop;
    }
    
    public String toString(){
        return "Konto: " + kontoNr + ". eier: " + eier + ". saldo: " + saldo;    
    }
}

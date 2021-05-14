/*
    Uriel Caracuel Barrera - 2º DAM
    AppAgenda.java
*/
package appagenda;

import entidades.Provincia;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AppAgenda {

    public static void main(String[] args) {
        
        //MAPEAR SI EXISTEN LAS TABLAS Y CREARLAS
        Map<String, String> emfProperties = new HashMap<>();
        // emfProperties.put("javax.persistence.jdbc.user", "APP");
        // emfProperties.put("javax.persistence.jdbc.password", "App");
        emfProperties.put("javax.persistence.schemageneration.database.action", "create");
        emfProperties.put("javax.persistence.jdbc.url", "jdbc:derby:BDAgenda;create=true");
        
        // Conexión a la BD
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        
        Provincia prov1 = new Provincia(1,"Sevilla");
        
        //PREPARAR LA TRANSACCION
        Provincia provinciaCadiz=new Provincia(1,"Cádiz");
        Provincia provinciaSevilla=new Provincia();
        provinciaSevilla.setNombre("Sevilla");
        
        //COMENZAR TRANSACCION
        em.getTransaction().begin();
        //MODIFICACIONES
        em.persist(provinciaCadiz);
        //em.persist(provinciaSevilla);
        

        //FIN MODIFICACIONES
        
        //ENVIAR A COMMIT
        em.getTransaction().commit();
        //deshacer el commit em.getTransaction().rollback();
        
        
        
        em.close();
        emf.close();
        try{
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } catch (SQLException ex){
        }
    }

}

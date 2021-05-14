/*
    Uriel Caracuel Barrera - 2º DAM
    ConsultaProvincias.java
*/

package appagenda;

import entidades.Provincia;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;


public class ConsultaProvincias {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();

        Query queryProvincias = em.createNamedQuery("Provincia.findAll");
        List<Provincia> listProvincias = queryProvincias.getResultList();

        System.out.println("-----LISTA DE PROVINCIAS----");
        //BUCLE PARA RECORRER LA COLLECION COMPLETA 
        for (Provincia provincia : listProvincias) {
            System.out.println(provincia.getNombre());
        }

        //BUSCAR POR NOMBRE
        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz = queryProvinciaCadiz.getResultList();

        System.out.println("-----LISTA DE PROVINCIAS: BUSQUEDA POR NOMBRE----");
        for (Provincia provinciaCadiz : listProvinciasCadiz) {
            System.out.printf(provinciaCadiz.getId() + ":");
            System.out.println(provinciaCadiz.getNombre());
        }

        //BUSCAR POR ID CON ENTITY MANAGER
        Provincia provinciaId2 = em.find(Provincia.class, 2);

        System.out.println("---BUSCAR POR ID----");
        if (provinciaId2 != null) {
            System.out.printf(provinciaId2.getId() + ":");
            System.out.println(provinciaId2.getNombre());
        } else {
            System.out.println("No hay ninguna provincia con ID=2");
        }

        //MODIFICAR OBJETOS DE LA TABLA
        //PREPARAR TRANSACCION
        Query queryProvinciaCadiz2 = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz2.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz2 = queryProvinciaCadiz2.getResultList();

        em.getTransaction().begin();

        System.out.println("MODIFICAR UN REGISTRO");
        //RECORRE LA COLECCION EN ESTE CASO SOLO BUSCA QUIEN TENGA NOMBRE CADIZ Y LE ASIGNA CODIGO  
        for (Provincia provinciaCadiz : listProvinciasCadiz) {

            //DATOS ANTES
            System.out.println(".....ANTES....");
            System.out.printf(provinciaCadiz.getId() + ":");
            System.out.printf(provinciaCadiz.getNombre() + ":");
            System.out.println(provinciaCadiz.getCodigo());

            //ASIGNACION Y MERGE
            provinciaCadiz.setCodigo("CA");
            em.merge(provinciaCadiz);

            //DESPUES DEL MERGE
            System.out.println("-----DESPUES---------");
            System.out.printf(provinciaCadiz.getId() + ":");
            System.out.printf(provinciaCadiz.getNombre() + ":");
            System.out.println(provinciaCadiz.getCodigo());

        }
        //REALIZA EL COOMIT
        em.getTransaction().commit();

        //ELIMINAR OBJETOS
        System.out.println(".....ELIMINAR OBJETOS....");
        Provincia provinciaId15 = em.find(Provincia.class, 15);
        em.getTransaction().begin();
        if (provinciaId15 != null) {
            em.remove(provinciaId15);
            System.out.println("----Borrado correctamente----");
        } else {
            System.out.println("No hay ninguna provincia con ID=15");
        }
        em.getTransaction().commit();
        //cerrar ENtitymanager y factory
        em.close();
        emf.close();
        
        try {
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } catch (SQLException ex) {
        }
    }

}

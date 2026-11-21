/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *Clase que se encarga de conectarse a la base de datos
 * @author Juan Sánchez Santiago
 */
public class DBUtil {
    /**
     * Método que se conecta a la base de datos
     * @return EntityManger con la conexión a la base de datos
     */
    public static EntityManager conectar(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("proyectoSanchezDB");
        return emf.createEntityManager();
    }
}

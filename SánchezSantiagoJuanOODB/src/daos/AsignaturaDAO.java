/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import modelos.Asignatura;

/**
 *Clase encargada del acceso a datos de Asignatura
 * @author Juan Sánchez
 */
//No hay mucha cosa en Asignatura porque es una clase auxiliar (en parte porque
//no me ha dado tiempo a desarrollarla), por lo que sirve más de base que otra cosa
//Dejo este primer método por si se quiere meter una Asignatura, pero no lo uso
public class AsignaturaDAO {
    /**
     * Método que inserta una Asignatura en la base de datos
     * @param a Asignatura a insertar en la base de datos
     * @return  1 si hay éxito, 0 si se produce un error
     */
    public static int insertarAsignatura(Asignatura a){
        //No comento mucho porque es la misma inserción que en Alumno
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        em.persist(a);
        try{
           em.getTransaction().commit(); 
        }catch(RollbackException ex){
            resultado = 0;
        }
        em.close();
        return resultado;
    }
    /**
     * Método que consulta los datos de todas las asignaturas de la base de datos
     * @return Lista con las asignaturas de la base de datos
     */
    public static List<Asignatura> consultaVagaAsignaturas(){
        //Igual que antes, no hay mucho que comentar que no haya dicho en AlumnoDAO
        EntityManager em = DBUtil.conectar();
        List<Asignatura> asignaturas;
        TypedQuery<Asignatura> q = em.createQuery("Select a FROM Asignatura a", Asignatura.class);
        asignaturas = q.getResultList();
        em.close();
        return asignaturas;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import modelos.Alumno;
import modelos.Curso;
import modelos.Profesor;

/**
 *Clase que se encarga del acceso a datos de Profesor
 * @author Juan Sánchez
 */
public class ProfesorDAO {
    /**
     * Método que inserta un Profeosr
     * @param p Profesor a insertar
     * @return  1 si hay éxito, 0 si se produce un error
     */
    public static int insertarProfesor(Profesor p){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        if(em.find(Alumno.class, p.getDni()) == null){
            em.persist(p);
        }else return 0;
        try{
           em.getTransaction().commit(); 
        }catch(RollbackException ex){
            resultado = 0;
        }
        em.close();
        return resultado;
    }
    /**
     * Método que consulta los datos simples de los profesores
     * @return Lista con los datos simples de todos los profesores
     */
    public static List<Profesor> consultaVagaProfesores(){
        EntityManager em = DBUtil.conectar();
        List<Profesor> profesores;
        TypedQuery<Profesor> q = em.createQuery("SELECT p FROM Profesor p", Profesor.class);
        profesores = q.getResultList();
        em.close();
        return profesores;
    };
    /**
     * Método que consulta los datos simples de los profesores según su nombre
     * @param nombre Cadena que representa el nombre del Profesor
     * @return       Lista con los profesores que tienen ese nombre y sus datos
     *               simples
     */
    public static List<Profesor> consultaVagaProfesorNombre(String nombre){
        EntityManager em = DBUtil.conectar();
        List<Profesor> profesores;
        TypedQuery<Profesor> q = em.createQuery("SELECT p FROM Profesor p WHERE p.nombre = :nombre", Profesor.class);
        q.setParameter("nombre", nombre);
        profesores = q.getResultList();
        em.close();
        return profesores;
    };
    /**
     * Método que consulta los datos simples de los profesores según su especialidad
     * @param especialidad Cadena que representa la especialidad del Profesor
     * @return             Lista con los profesores con esa especialidad y sus
     *                     datos simples
     */
    public static List<Profesor> consultaVagaProfesorEspecialidad(String especialidad){
        EntityManager em = DBUtil.conectar();
        List<Profesor> profesores;
        TypedQuery<Profesor> q = em.createQuery("SELECT p FROM Profesor p WHERE p.especialidad = :especialidad", Profesor.class);
        q.setParameter("especialidad", especialidad);
        profesores = q.getResultList();
        em.close();
        return profesores;
    };
    /**
     * Método que consulta todos los datos de un Profesor en función de su DNI
     * @param dni Cadena que representa el DNI del Profesor
     * @return    Profesor con todos sus datos
     */
    public static Profesor consultaTotalProfesor(String dni){
        EntityManager em = DBUtil.conectar();
        TypedQuery<Profesor> q = em.createQuery("SELECT DISTINCT p FROM Profesor p LEFT JOIN FETCH p.cursos WHERE p.dni = :dni", Profesor.class);
        q.setParameter("dni", dni);
        Profesor p = q.getSingleResult();
        return p;
    }
    /**
     * Método que actualiza los datos de un Profesor
     * @param p Profesor con datos actualizados que guardar
     * @return  1 si hay éxito, 0 si se produce un error
     */
    public static int actualizarProfesor(Profesor p){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        em.merge(p);
        try{
           em.getTransaction().commit(); 
        }catch(RollbackException ex){
            resultado = 0;
        }
        em.clear();
        em.close();
        return resultado;
    }
    /**
     * Método que elimina un Profesor en función de su DNI
     * @param dni Cadena que representa el DNI del Profeosr
     * @return    1 si hay éxito, 0 si se produce un error
     */
    public static int eliminarProfesor(String dni){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        Profesor p = em.find(Profesor.class, dni);
        for(Curso c: p.getCursos()){
            c.setProfesor(null);
        }
        //Como Profesor no es el dueño de su relación con Cursos, al borrarlo
        //los cursos también se actualizan 
        em.remove(p);
        try{
           em.getTransaction().commit(); 
        }catch(RollbackException ex){
            resultado = 0;
        }
        em.clear();
        em.close();
        return resultado;
    }
    /**
     * Método que elimina la relación de un Profesor con un Curso
     * @param idCurso Entero que representa el id de un Curso
     * @return        1 si hay éxito, 0 si se produce un error
     */
    public static int quitarCurso(int idCurso){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        //Como es el Curso el dueño de la relación, debo quitarle a este 
        //el profesor para que se realice el cambio.
        //Lo hice así porque veía que tenía más sentido que un Profesor se 
        //quitara un Curso que fuera el Curso el que se quitara el Profesor
        Curso c = em.find(Curso.class, idCurso);
        c.setProfesor(null);
        try{
           em.getTransaction().commit(); 
        }catch(RollbackException ex){
            resultado = 0;
        }
        em.clear();
        em.close();
        return resultado;
    }
}

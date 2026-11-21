/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import modelos.Matricula;
import modelos.Profesor;

/**
 *Clase que se encarga del acceso a datos de Matrícula
 * @author Juan Sánchez Santiago
 */
public class MatriculaDAO {
    /**
     * Método que inserta una Matrícula
     * @param m Matrícula a insertar
     * @return  1 si hay éxito, 0 si se produce un error
     */
    public static int insertarMatricula(Matricula m){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        if(em.find(Profesor.class, m.getAlumno().getDni()) == null){
            em.merge(m);
        }else resultado = 0;
        try{
           em.getTransaction().commit(); 
        }catch(RollbackException ex){
            resultado = 0;
        }
        em.close();
        return resultado;
    }
    /**
     * Método que elimina una Matrícula y sus relaciones con el resto de Objetos
     * @param id  Entero que representa el id de la Matrícula
     * @param dni Cadena con el DNI de su Alumno
     * @return    1 si hay éxito, 0 si se produce un error
     */
    public static int eliminarMatricula(int id, String dni){
        EntityManager em = DBUtil.conectar();
        int resultado;
        em.getTransaction().begin();
        //Se borra la matrícula
        Matricula m = em.find(Matricula.class, id);
        em.remove(m);
        em.getTransaction().commit();
        resultado = em.find(Matricula.class, id) == null? 1 : 0;
        em.clear();
        em.close();
        //Se vuelve a cambiar la media del Alumno
        AlumnoDAO.establecerMediaAlumno(dni);
        return resultado; 
    }
    /**
     * Método que recoge todos los datos de todas las matrículas
     * @return Lista con todas las matrículas y sus datos
     */
    public static List<Matricula> consultaTotalMatriculas(){
        EntityManager em = DBUtil.conectar();
        List<Matricula> matriculas;
        TypedQuery<Matricula> q = em.createQuery("SELECT m FROM Matricula m LEFT JOIN FETCH m.alumno LEFT JOIN FETCH m.curso", Matricula.class);
        matriculas = q.getResultList();
        em.close();
        return matriculas;
    }
    /**
     * Método que consulta todas las matrículas de un Curso
     * @param id Entero que representa el id del Curso
     * @return   Lista con todos los datos de todas las matrículas de un Curso
     */
    public static List<Matricula> consultaTotalMatriculasCurso(int id){
        EntityManager em = DBUtil.conectar();
        List<Matricula> matriculas;
        TypedQuery<Matricula> q = em.createQuery("SELECT m FROM Matricula m LEFT JOIN FETCH m.alumno WHERE m.curso.id = :id", Matricula.class);
        q.setParameter("id", id);
        matriculas = q.getResultList();
        em.close();
        return matriculas;
    }
    /**
     * Método que consulta las matrículas de un curso en función de si se están
     * cursando o han sido cursadas
     * @param id        Entero que representa el id del Curso
     * @param esActiva  Boolean para realizar una consulta u otra. True busca las
     * matrículas no calificadas. False busca las matrículas calificadas
     * @return          Lista con las matrículas calificadas o no de un Curso
     */
    public static List<Matricula> consultaTotalMatriculasActivasCurso(int id, boolean esActiva){
        EntityManager em = DBUtil.conectar();
        List<Matricula> matriculas;
        //Se construye la sentencia con el StringBuilder en función del boolean
        StringBuilder constructor = new StringBuilder();
        constructor.append("SELECT m FROM Matricula m LEFT JOIN FETCH m.alumno WHERE m.curso.id = :id AND m.nota");
        
        String pasadoOPresente = esActiva? " == -1": " <> -1";
        
        constructor.append(pasadoOPresente);
        TypedQuery<Matricula> q = em.createQuery(constructor.toString(), Matricula.class);
        q.setParameter("id", id);
        matriculas = q.getResultList();
        em.close();
        return matriculas;
    }
    /**
     * Método que consultatodos los datos de todas las matrículas de un Alumno
     * @param dni Cadena que representa el DNI del Alumno al que buscar las matrículas
     * @return    Lista con las matrículas y todos su datos del Alumno
     */
    public static List<Matricula> consultaTotalMatriculasAlumno(String dni){
        EntityManager em = DBUtil.conectar();
        List<Matricula> matriculas;
        String query = "SELECT m FROM Matricula m LEFT JOIN FETCH m.alumno LEFT JOIN FETCH m.curso LEFT JOIN FETCH m.curso.asignatura WHERE m.alumno.dni = :dni";
        TypedQuery<Matricula> q = em.createQuery(query, Matricula.class);
        q.setParameter("dni", dni);
        matriculas = q.getResultList();
        em.close();
        return matriculas;
    }
    /**
     * Método que devuelve las matrículas calificadas o no de un Alumno
     * @param dni       Cadena que representa el DNI del Alumno
     * @param esActiva  Boolean para realizar una consulta u otra. True busca las
     * matrículas no calificadas. False busca las matrículas calificadas
     * @return          Lista con las matrículas calificadas o no del Alumno
     */
    public static List<Matricula> consultaTotalMatriculasActivasAlumno(String dni, boolean esActiva){
        EntityManager em = DBUtil.conectar();
        List<Matricula> matriculas;
        //Mismo procedimiento que en el método anterior
        StringBuilder constructor = new StringBuilder();
        constructor.append("SELECT m FROM Matricula m LEFT JOIN FETCH m.alumno LEFT JOIN FETCH m.curso  LEFT JOIN FETCH m.curso.asignatura WHERE m.alumno.dni = :dni AND m.nota");
        
        String pasadoOPresente = esActiva? " == -1": " <> -1";
        constructor.append(pasadoOPresente);
        
        TypedQuery<Matricula> q = em.createQuery(constructor.toString(), Matricula.class);
        q.setParameter("dni", dni);
        matriculas = q.getResultList();
        em.close();
        return matriculas;
    }
    /**
     * Método que establce la nota de una Matrícula
     * @param idMatricula Entero que representa el id de la Matrícula
     * @param nota        Double con la nota a establecer
     * @return            1 si hay éxito, 0 si se produce un error
     */
    public static int establecerNota(int idMatricula, double nota){
        EntityManager em = DBUtil.conectar();
        em.getTransaction().begin();
        Matricula m = em.find(Matricula.class, idMatricula);
        m.setNota(nota);
        em.getTransaction().commit();
        em.clear();
        em.close();
        //Devuelvo el valor de establecer la nota porque solo se cambiará con
        //éxito si se produce un cambio en la media
        return AlumnoDAO.establecerMediaAlumno(m.getAlumno().getDni());
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import modelos.Asignatura;
import modelos.Curso;
import modelos.Matricula;
import modelos.Profesor;

/**
 *Clase que se encarga del acceso a datos de Curso
 * @author Juan Sánchez
 */
public class CursoDAO {
    /**
     * Método que inserta un Curso en la base de datos
     * @param c Curso a insertar en la base de datos
     * @return  1 si hay éxito, 0 si se produce un error
     */
    public static int insertarCurso(Curso c){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        em.persist(c);
        try{
           em.getTransaction().commit(); 
        }catch(RollbackException ex){
            resultado = 0;
        }
        em.close();
        return resultado;
    }
    /**
     * Método que elimina un Curso de la base de datos
     * @param id    Entero con el id del Curso
     * @return      1 si hay éxito, 0 si se produce un error
     */
    public static int eliminarCurso(int id){
        EntityManager em = DBUtil.conectar();
        int resultado;
        //Recogo los alumnos que tiene matrículas ya evaluadas de ese Curso
        //para cambiarles la nota media cuando este se borre
        List<String> dniAlumnos = new ArrayList();
        for(Matricula m : MatriculaDAO.consultaTotalMatriculasActivasCurso(id, false)){
            dniAlumnos.add(m.getAlumno().getDni());
        }
        em.getTransaction().begin();
        Curso c = em.find(Curso.class, id);
        //Borro el Curso, como en Alumno, sus matrículas se borran porque tiene
        //un cascade REMOVE con matrícula
        em.remove(c);
        em.getTransaction().commit();
        //Compruebo si existe para devolver un valor u otro
        resultado = em.find(Curso.class, id) == null? 1 : 0;
        //Limpio la caché
        em.clear();
        em.close();
        //Ejecuto el establecimiento de la media después de cerrar el flujo
        //porque si no provoca un error
        for(String dni : dniAlumnos){
            AlumnoDAO.establecerMediaAlumno(dni);
        }
        return resultado; 
    }
    /**
     * Método que consulta los datos simples de un Curso
     * @param id    Entero que representa el id del Curso
     * @return      Curso con los datos obtenidos
     */
    public static Curso consultaVagaCurso(int id){
        EntityManager em = DBUtil.conectar();
        Curso c = em.find(Curso.class, id);
        em.close();
        return c;
    }
    /**
     * Método que recoge todos los datos de un Curso
     * @param id    Entero que representa el id del Curso
     * @return      Curso con todos sus datos
     */
    public static Curso consultaTotalCurso(int id){
        EntityManager em = DBUtil.conectar();
        Curso c = em.find(Curso.class, id);
        //Se realizan diferentes LEFT JOIN FETCH para conseguir todos los datos
        TypedQuery<Curso> q = em.createQuery("SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.asignatura LEFT JOIN FETCH c.matriculas LEFT JOIN FETCH c.profesor WHERE c.id = :id", Curso.class);
        q.setParameter("id", id);
        return c;
    }
    /**
     * Método que recoge todos los Cursos con todos sus datos si tienen
     * determinada Asignatura
     * @param id    Entero con el que buscar la Asignatura
     * @return      Lista con los cursos de una Asignatura y todos sus datos
     */
    public static List<Curso> consultaTotalCursosAsignatura(int id){
        EntityManager em = DBUtil.conectar();
        List<Curso> cursos;
        //No suelo pasar objetos porque si esos objetos vienen de otro flujo
        //se produce un error
        String query = "SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.matriculas LEFT JOIN FETCH c.asignatura LEFT JOIN FETCH c.profesor WHERE c.asignatura.id =:id";
        TypedQuery<Curso> q = em.createQuery(query, Curso.class);
        q.setParameter("id", id);
        cursos = q.getResultList();
        em.close();
        return cursos;
    }
    /**
     * Método que recoge los Cursos de una Asignatura y sus Profesores 
     * @param id    Entero con el que buscar la Asignatura
     * @return      Lista con los cursos de una Asignatura y los datos de sus profesores 
     */
    public static List<Curso> consultaCursosProfesorPorAsignatura(int id){
        /*
         Utilizo este método en vez de el de arriba porque supongo que es más
         eficiente cuando solo necesito recoger el Profesor 
         */
        EntityManager em = DBUtil.conectar();
        List<Curso> cursos;
        StringBuilder constructor = new StringBuilder();
        constructor.append("SELECT c FROM Curso c LEFT JOIN FETCH c.profesor");
        if(id != -1){
            constructor.append(" WHERE c.asignatura.id =:id");
        }
        TypedQuery<Curso> q = em.createQuery(constructor.toString(), Curso.class);
        q.setParameter("id", id);
        cursos = q.getResultList();
        em.close();
        return cursos;
    }
    /**
     * Método que recoge los datos simples de cursos en función de su Asignatura
     * @param asignatura   Asignatura con el que buscar a la Asignatura
     * @return             Lista con los cursos de una Asignatura y sus datos simples
     */
    public static List<Curso> consultaVagaCursosAsignatura(Asignatura asignatura){
        EntityManager em = DBUtil.conectar();
        List<Curso> cursos;
        /*
        La excepción a la regla, esta vez me es útil recibir el objeto para juntar
        dos métodos en uno. Ya que así compruebo los cursos con Asignatura y
        sin asignatura
        */
        /*
        Este código lo uso en algunos métodos para mezclar métodos similares. 
        StringBuilder construye una consulta u otra en función de si asignatura
        es null
        */
        StringBuilder constructor = new StringBuilder();
        constructor.append("SELECT c FROM Curso c WHERE c.asignatura");
        if(asignatura == null){
            constructor.append(" IS NULL");
        }else{
            constructor.append(" = :asignatura");
        }
        TypedQuery<Curso> q = em.createQuery(constructor.toString(), Curso.class);
        if(asignatura != null){
            q.setParameter("asignatura", asignatura);
        }
        //Finalmente, sea como sea, recojo los datos
        cursos = q.getResultList();
        em.close();
        return cursos;
    }
    /**
     * Método que devuelve una lista de Cursos en función de su Profesor
     * @param dni  Cadena con el dni del profesor a buscar
     * @return     Lista con los cursos que tienen ese Profesor
     */
    public static List<Curso> consultaTotalCursosProfesor(String dni){
        EntityManager em = DBUtil.conectar();
        List<Curso> cursos;
        StringBuilder constructor = new StringBuilder();
        //Hago lo mismo que en el método anterior. Esta vez para buscar con Profesor
        //y sin Profesor
        constructor.append("SELECT DISTINCT c FROM Curso c JOIN FETCH c.matriculas WHERE c.profesor");
        if(dni == null){
            constructor.append(" IS NULL");
        }else{
            constructor.append(" = :profesor");
        }
        TypedQuery<Curso> q = em.createQuery(constructor.toString(), Curso.class);
        //Si el DNI no es null entonces busco al Profesor y lo paso como parámetro
        if(dni != null){
            Profesor p = em.find(Profesor.class, dni);
            q.setParameter("profesor", p);
        }
        cursos = q.getResultList();
        em.close();
        return cursos;
    }
    /**
     * Método que devuelve los datos simples de los cursos
     * @return Lista con los datos simples de los cursos
     */
    public static List<Curso> consultaVagaCursos(){
        EntityManager em = DBUtil.conectar();
        List<Curso> cursos;
        TypedQuery<Curso> q = em.createQuery("SELECT c FROM Curso c", Curso.class);
        cursos = q.getResultList();
        em.close();
        return cursos;
    }
    /**
     * Método que devuelve todos los datos de los cursos
     * @return Lista con todos los datos de los cursos
     */
    public static List<Curso> consultaTotalCursos(){
        EntityManager em = DBUtil.conectar();
        List<Curso> cursos;
        TypedQuery<Curso> q = em.createQuery("SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.asignatura  LEFT JOIN FETCH c.matriculas LEFT JOIN FETCH c.profesor", Curso.class);
        cursos = q.getResultList();
        em.close();
        return cursos;
    }
    /**
     * Método que actualiza los datos de un Curso
     * @param c Curso a actualizar
     * @return  1 si hay éxito, 0 si se produce un error
     */
    public static int actualizarCurso(Curso c){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        //No utilizo merge porque Curso no tiene cargadas sus matrículas y eso
        //haría que se borraran por tener orphan removal
        TypedQuery<Curso> q = em.createQuery("UPDATE Curso c SET c.nombre = :nombre, c.asignatura = :asignatura, c.profesor = :profesor WHERE c.id = :id", Curso.class);
        q.setParameter("nombre", c.getNombre());
        q.setParameter("asignatura", c.getAsignatura());
        q.setParameter("profesor", c.getProfesor());
        q.setParameter("id", c.getId());
        resultado = q.executeUpdate();
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


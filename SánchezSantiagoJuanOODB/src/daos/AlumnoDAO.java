/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daos;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import modelos.Alumno;

/**
 *Clase encargada del acceso a datos del objeto Alumno
 * @author Juan Sánchez
 */
public class AlumnoDAO {
    //No hay un insertarAlumno porque no tiene sentido insertar un Alumno
    //si no es con una Matrícula
    
    /**
     * Método que consulta los datos que no son objetos de Alumno
     * @param dni   Cadena con el dni del Alumno
     * @return      Alumno con sus datos básicos
     */
    public static Alumno consultaVagaAlumno(String dni){
        //En cada acceso a datos abro y cierro el EntityManager porque supono
        //que es lo mejor. También me ha supuesto un dolor de cabeza importante
        EntityManager em = DBUtil.conectar();
        //em.finf busca al objeto por su clave primaria
        Alumno a = em.find(Alumno.class, dni);
        em.close();
        return a;
    }
    /**
     * Método que consulta los datos simples de todos los alumnos
     * @return  Lista con los datos simples de todos los alumnos 
     */
    public static List<Alumno> consultaVagaAlumnos(){
        EntityManager em = DBUtil.conectar();
        TypedQuery<Alumno> q = em.createQuery("SELECT a FROM Alumno a", Alumno.class);
        //getResultList devuelve la lista de resultados obtenidos
        List<Alumno> alumnos = q.getResultList();
        em.close();
        return alumnos;
    }
    /**
     * Método que devuelve todos los datos de un Alumno en función de su DNI
     * @param dni   Cadena con el DNI del Alumno
     * @return      Alumno con todos sus datos
     */
    public static Alumno consultaTotalAlumno(String dni){
        EntityManager em = DBUtil.conectar();
        //El DISTINCT lo uso porque, al poder recibir varias matrículas,
        //los datos recibidos se duplican(hasta donde yo he entendido)
        //LEFT JOIN FETCH recoge los objetos con sus datos simples que concuerdan con 
        //el objeto Alumno obtenido. Es decir trae una consulta vaga de aquello que se 
        //señale
        TypedQuery<Alumno> q = em.createQuery("SELECT DISTINCT a FROM Alumno a LEFT JOIN FETCH a.matriculas WHERE a.dni = :dni", Alumno.class);
        q.setParameter("dni", dni);
        Alumno a = q.getSingleResult();
        em.close();
        return a;
    }
    /**
     * Método que devuelve una lista de alumnos en función de su nombre
     * @param nombre    Cadena con el nombre del Alumno
     * @return          Lista con los alumnos con ese nombre
     */
    public static List<Alumno> consultaVagaAlumnoPorNombre(String nombre){
        EntityManager em = DBUtil.conectar();
        //Es una lista porque puede darse que hayan alumnos con el mismo nombre
        List<Alumno> alumnos;
        TypedQuery<Alumno> q = em.createQuery("SELECT a FROM Alumno a WHERE a.nombre = :nombre", Alumno.class);
        q.setParameter("nombre", nombre);
        alumnos = q.getResultList();
        em.close();
        return alumnos;
    }
    /**
     * Método que elimina un Alumno en función de su DNI
     * @param dni   Cadena con el DNI del Alumno
     * @return      1 si hay éxito, 0 si se produce un error
     */
    public static int eliminarAlumno(String dni){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        //Busco al alumno y lo borro
        //Como Alumno tiene casade REMOVE con matrículas, estas se eliminan
        Alumno a = em.find(Alumno.class, dni);
        em.remove(a);
        //El try es para recibir información del éxito de la consulta
        try{
           em.getTransaction().commit(); 
        }catch(RollbackException ex){
            resultado = 0;
        }
        //Investigando vi que se supone que lo mejor es limpiar el caché tras 
        //una eliminación, como cuando se hace una edición. 
        //Aunque yo como abro y cierro el EntityManager siempre
        //no creo que sea muy necesario. Pero no molesta tampoco
        em.clear();
        em.close();
        return resultado;
    }
    /**
     * Método que actualiza los datos simples del alumno
     * @param a Alumno con los datos modificados
     * @return  1 si hay éxito, 0 si se produce un error
     */
    public static int actualizarAlumno(Alumno a){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        em.getTransaction().begin();
        /*Por lo que he entendido em.merge busca el Objeto en la base de datos,
        si encuentra su id, compara y actualiza los datos. Como no hay un casade
        MERGE de Alumno a Matricula, puedo pasar un Alumno con datos simples y
        las matrículas no desaparecerán
        */
        em.merge(a);
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
     * Método que establece la nota media de un Alumno consultando su nota en
     * todas sus matrículas que no estén activas
     * @param dni   DNI del Alumno al que modificar la nota
     * @return      1 si hay éxito, 0 si se produce un error
     */
    public static int establecerMediaAlumno(String dni){
        EntityManager em = DBUtil.conectar();
        int resultado = 1;
        Double queryNotaMedia;
        double notaAcademica;
        em.getTransaction().begin();
        //Recojo la nota media que surge de mirar sus matrículas cursadas
        TypedQuery<Double> qNotaMedia = em.createQuery("SELECT AVG(m.nota) FROM Matricula m WHERE m.alumno.dni = :dni AND m.nota > -1", Double.class);
        qNotaMedia.setParameter("dni", dni);
        queryNotaMedia = qNotaMedia.getSingleResult();
        //Si no hay matrículas, el valor será null, así que lo compruebo para
        //poder tratarlo cuando muestre al Alumno en las tablas
        notaAcademica = queryNotaMedia == null? -1 : queryNotaMedia;
        
        //Busco al Alumno al que modificar la nota
        Alumno a = em.find(Alumno.class, dni);
        //Si por un casual la nota no cambiara, se ahorran los cambios en la base de datos
        if(notaAcademica != a.getMediaAcademica()){
            //Si no, se establece la nota
            a.setMediaAcademica(notaAcademica);
        }
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

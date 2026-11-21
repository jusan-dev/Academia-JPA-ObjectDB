/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilidades;

import javax.swing.table.DefaultTableModel;

/**
 *Clase que hereda del DefaultTableModel y que permite hacer que ninguna celda
 * de la tabla que lo implemente sea editable
 * @author Juan Sánchez
 */
public class MiDefaultTableModel extends DefaultTableModel{
    /**
     * Método que evita la edición de cualquier tabla
     * @param row    Entero que representa una fila de la tabla
     * @param column Entero que representa una column de la tabla
     * @return       False en todos los casos, para evitar que cualquier
     *               celda sea editable
     */
    @Override
    public boolean isCellEditable(int row, int column){
        return false;
    }
    //Buscando cómo hacer que una tabla no se edite me encontré con este método.
    //Tampoco me quedó muy claro su funcionamiento pero comprendo cuál es su resultado.
    //Supoongo que el modelo recorre todas las celdas de la tabla con los dos parámetros
    //y, en este caso, evita que sean editables.
}

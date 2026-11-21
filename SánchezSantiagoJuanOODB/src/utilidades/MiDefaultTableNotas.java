
package utilidades;


/**
 *Clase que permite la edición de la sexta columna de una tabla
 * @author Juan Sánchez
 */
public class MiDefaultTableNotas extends MiDefaultTableModel{
    /**
     * Método para permitir la edición de la sexta columna de la tabla
     * @param row    Entero que representa una fila de la tabla
     * @param column Entero que representa una columna de la tabla
     * @return       True si es es la sexta columna, false en el resto de casos
     */
    @Override
    public boolean isCellEditable(int row, int column){
        return column == 5;
    }
    //Como el método de DefaultTableModel no es lo más moldeable, tengo que 
    //hacer una nueva clase para permitir la edición de una columna. Puede ser
    //que haya una forma más elegante, pero no me dió tiempo a investigar los
    //intringulis de DefaultTableModel.
}

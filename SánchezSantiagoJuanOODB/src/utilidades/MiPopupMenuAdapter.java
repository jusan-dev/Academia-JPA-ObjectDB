package utilidades;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *Clase que implementa PopupMenuListener para que al utilizar
 * el procedimiento popupMenuWillBecomeVisible el código quede más claro
 * @author Juan Sánchez
 */
public class MiPopupMenuAdapter implements PopupMenuListener{
    
    /**
     *Método utilizado cuando el menu popup va a aparecer
     *
     * @param e PopupMenuEvent que contiene la fuente del evento
     */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }
     /**
     *Método utilizado cuando el menu popup va a desaparecer
     *
     * @param e PopupMenuEvent que contiene la fuente del evento
     */
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }
     /**
     *Método utilizado cuando el menu popup se cancela
     *
     * @param e PopupMenuEvent que contiene la fuente del evento
     */
    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }
    
}


package selectioncanvas;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * @author Yehia Mokhtar Farag
 */
public class SelectioncanvasComponentState extends JavaScriptComponentState {
    private String value;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

package d3diagrams;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * @author Yehia Mokhtar Farag
 */
public class VennDiagramComponentState extends JavaScriptComponentState {
    private String value;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
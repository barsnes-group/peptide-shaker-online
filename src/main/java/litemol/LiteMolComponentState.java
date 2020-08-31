
package litemol;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * @author y-mok
 */
public class LiteMolComponentState extends JavaScriptComponentState {
    private String value;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
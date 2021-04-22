
package litemol;

import com.vaadin.shared.ui.JavaScriptComponentState;
import io.vertx.core.json.JsonObject;

/**
 * @author Yehia Mokhtar Farag
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
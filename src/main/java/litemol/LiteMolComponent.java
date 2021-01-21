package litemol;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

@StyleSheet({"../../VAADIN/litemol/css/mylitemolstyle.css", "../../VAADIN/litemol/css/LiteMol-plugin.css"})
//@JavaScript({"../../VAADIN/js/LiteMol-plugin.js", "../../VAADIN/js/mylitemol-connector.js", "../../VAADIN/js/mylitemollibrary.js", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js"})
public class LiteMolComponent extends AbstractJavaScriptComponent {

    ArrayList<ValueChangeListener> listeners
            = new ArrayList<>();

    public LiteMolComponent() {
        LiteMolComponent.this.addFunction("onClick", (JsonArray arguments) -> {
            getState().setValue(arguments.getObject(0).toString());
            listeners.forEach((listener) -> {
                listener.valueChange();
            });
        });

        LiteMolComponent.this.addValueChangeListener(() -> {
            LiteMolComponent.this.getValue();
        });
    }

    public void addValueChangeListener(
            ValueChangeListener listener) {
        listeners.add(listener);
    }

    public JsonObject getValue() {
        return new JsonObject(getState().getValue());
    }

    public void setValue(JsonObject value) {
        getState().setValue(value.encodePrettily());
    }

    @Override
    protected LiteMolComponentState getState() {
        return (LiteMolComponentState) super.getState();
    }

    public interface ValueChangeListener extends Serializable {

        void valueChange();
    }

}

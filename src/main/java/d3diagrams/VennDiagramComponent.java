package d3diagrams;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonArray;
import java.io.Serializable;
import java.util.ArrayList;

//@JavaScript({"../../VAADIN/js/venn.js", "../../VAADIN/js/myD3library.js", "../../VAADIN/js/myD3component-connector.js", "../../VAADIN/js/d3.v5.min.js"}) //,  "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js"
public abstract class VennDiagramComponent extends AbstractJavaScriptComponent {



    public VennDiagramComponent() {
        VennDiagramComponent.this.setWidth(100, Unit.PERCENTAGE);
        VennDiagramComponent.this.setHeight(100, Unit.PERCENTAGE);

        VennDiagramComponent.this.addFunction("onClick", (JsonArray arguments) -> {
            getState().setValue(arguments.getString(0));
            listeners.forEach((listener) -> {
                listener.valueChange();
            });
        });

        VennDiagramComponent.this.addValueChangeListener(() -> {
            String value = VennDiagramComponent.this.getValue();
            SelectionPerformed(value);
        });

    }

    public abstract void SelectionPerformed(String value);

    public interface ValueChangeListener extends Serializable {

        void valueChange();
    }
    ArrayList<ValueChangeListener> listeners
            = new ArrayList<>();

    public void addValueChangeListener(
            ValueChangeListener listener) {
        listeners.add(listener);
    }


     public void setValue(String value,int w,int h) {
        getState().setValue("serverRequest:data;" + value + ";" + + Math.max(100, w) + "," + Math.max(100, h));
    }

    public String getValue() {
        return getState().getValue();
    }

    public void setSize(int width, int height) {
        getState().setValue("serverRequest:sizeonly;" + Math.max(100,width) + ";" + Math.max(100,height));

    }

    @Override
    protected VennDiagramComponentState getState() {
        return (VennDiagramComponentState) super.getState();
    }

}

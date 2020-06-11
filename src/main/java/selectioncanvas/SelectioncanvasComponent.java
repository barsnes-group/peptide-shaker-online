/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selectioncanvas;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonArray;
import java.io.Serializable;
import java.util.ArrayList;

//@JavaScript({"../../VAADIN/js/mylibrary.js", "../../VAADIN/js/mycomponent-connector.js", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js"})
public abstract class SelectioncanvasComponent extends AbstractJavaScriptComponent {

    private String lastSelectedValue = "";

    public SelectioncanvasComponent() {
        SelectioncanvasComponent.this.setWidth(500, Unit.PIXELS);
        SelectioncanvasComponent.this.setHeight(500, Unit.PIXELS);

        SelectioncanvasComponent.this.addFunction("onClick", (JsonArray arguments) -> {
            getState().setValue(arguments.getString(0));
            listeners.forEach((listener) -> {
                listener.valueChange();
            });
        });

        SelectioncanvasComponent.this.addValueChangeListener(() -> {
           
            String value = SelectioncanvasComponent.this.getValue();
            String[] valueArr = value.split(",");
            if (valueArr.length < 4) {
                return;
            }
            try {

                if ((valueArr[4] + "").contains("1")) {
                    int startx = (int) ((double) Double.valueOf(valueArr[0]));
                    int starty = (int) ((double) Double.valueOf(valueArr[1]));
                    int endx = (int) ((double) Double.valueOf(valueArr[2]));
                    int endy = (int) ((double) Double.valueOf(valueArr[3]));
                    if (endx == -1 && endy == -1 && startx == -1 && starty == -1) {
                        rightSelectionIsPerformed(0, 0);
                    } else if ((endx == -1 & endy == -1) || (endx == startx & endy == endx)) {
//                    leftSelectionIsPerformed(startx, starty);

                    } else {
                        dragSelectionIsPerformed(startx, starty, endx, endy);
                    }

                } else {
                    rightSelectionIsPerformed(0, 0);
                }
            } catch (NumberFormatException nfx) {
                nfx.printStackTrace();
            }
        });
    }

    public abstract void dragSelectionIsPerformed(double startX, double startY, double endX, double endY);

    public abstract void rightSelectionIsPerformed(double startX, double startY);

    public abstract void leftSelectionIsPerformed(double startX, double startY);

    public interface ValueChangeListener extends Serializable {

        void valueChange();
    }
    ArrayList<ValueChangeListener> listeners
            = new ArrayList<>();

    public void addValueChangeListener(
            ValueChangeListener listener) {
        listeners.add(listener);
    }

    public void setValue(String value) {
        getState().setValue(value);
    }

    public String getValue() {
        return getState().getValue();
    }

    public void setSize(int width, int height,boolean freeYSelection) {
        this.setWidth(width, Unit.PIXELS);
        this.setHeight(height, Unit.PIXELS);
        this.setValue(width + "," + height+","+freeYSelection);

    }

    @Override
    protected SelectioncanvasComponentState getState() {
        return (SelectioncanvasComponentState) super.getState();
    }

}

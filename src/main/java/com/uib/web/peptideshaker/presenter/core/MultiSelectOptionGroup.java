package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.HorizontalLayout;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents drop-down list component
 *
 * @author Yehia Farag
 */
public class MultiSelectOptionGroup extends HorizontalLayout implements LayoutEvents.LayoutClickListener {

    private final OptionGroup listI;
    private final OptionGroup listII;
    private final OptionGroup listIII;
    private final OptionGroup listIV;
    private final boolean expandable;

    /**
     * Constructor to initialize the main attributes.
     */
    public MultiSelectOptionGroup(String title, boolean expandable) {
        this.expandable = expandable;
        MultiSelectOptionGroup.this.setSizeUndefined();
        MultiSelectOptionGroup.this.setSpacing(false);
        if (expandable) {
            MultiSelectOptionGroup.this.addLayoutClickListener(MultiSelectOptionGroup.this);
        }
        MultiSelectOptionGroup.this.setStyleName("optiongroupframe");
        if (title != null) {
            listI = new OptionGroup(title);
        } else {
            listI = new OptionGroup();
        }

        listI.setCaptionAsHtml(true);
        listI.setSizeUndefined();
        listI.setMultiSelect(true);
        listI.setStyleName("optiongroup");

        MultiSelectOptionGroup.this.addComponent(listI);
        if (title != null) {
            listII = new OptionGroup(" ");
        } else {
            listII = new OptionGroup();
        }
        listII.setCaptionAsHtml(true);
        listII.setSizeUndefined();
        listII.setMultiSelect(true);
        listII.setStyleName("optiongroup");

        MultiSelectOptionGroup.this.addComponent(listII);
        if (title != null) {
            listIII = new OptionGroup(" ");
        } else {
            listIII = new OptionGroup();
        }
        listIII.setCaptionAsHtml(true);
        listIII.setSizeUndefined();
        listIII.setMultiSelect(true);
        listIII.setStyleName("optiongroup");

        MultiSelectOptionGroup.this.addComponent(listIII);

         if (title != null) {
           listIV = new OptionGroup(" ");
        } else {
            listIV = new OptionGroup();
        }
        
        listIV.setCaptionAsHtml(true);
        listIV.setSizeUndefined();
        listIV.setMultiSelect(true);
        listIV.setStyleName("optiongroup");

        MultiSelectOptionGroup.this.addComponent(listIV);

    }

    /**
     * Update the list
     *
     * @param idToCaptionMap list of ids and names
     */
    public void updateList(Map<String, String> idToCaptionMap) {
        listI.removeAllItems();
        listI.clear();
        listII.removeAllItems();
        listII.clear();
        listIII.removeAllItems();
        listIII.clear();
        int counter = 0;
        OptionGroup list = listI;
        if (idToCaptionMap.size() < 11) {
            for (String id : idToCaptionMap.keySet()) {
                if (counter == 3) {
                    list = listII;
                }
                if (counter == 6) {
                    list = listIII;
                }
                if (counter == 9) {
                    list = listIV;
                }
                list.addItem(id);
                list.setItemCaption(id, idToCaptionMap.get(id));
                counter++;
            }
        } else {
            int rowsNumber = (int) Math.nextUp((double) idToCaptionMap.size() / 3.0);
            for (String id : idToCaptionMap.keySet()) {
                if (counter == rowsNumber) {
                    list = listII;
                }
                if (counter == (rowsNumber * 2)) {
                    list = listIII;
                }
                list.addItem(id);
                list.setItemCaption(id, idToCaptionMap.get(id));
                counter++;
            }
        }
        listII.setVisible(!listII.getItemIds().isEmpty());
        listIII.setVisible(!listIII.getItemIds().isEmpty());
        listIV.setVisible(!listIV.getItemIds().isEmpty());

    }

    /**
     * Get selection value
     *
     * @return String id of the selected item
     */
    public Set<String> getSelectedValue() {
        listI.removeStyleName("error");
        if (listI.isValid() || listII.isValid() || listIII.isValid()) {
            Set<String> set = new LinkedHashSet<>();
            if (listI.getValue() != null) {
                set.addAll((Set<String>) listI.getValue());
            }
            if (listII.getValue() != null) {
                set.addAll((Set<String>) listII.getValue());
            }
            if (listIII.getValue() != null) {
                set.addAll((Set<String>) listIII.getValue());
            }
            if (listIV.getValue() != null) {
                set.addAll((Set<String>) listIV.getValue());
            }
            if (set.isEmpty()) {
                return null;
            }
            return set;
        }
        listI.addStyleName("error");
        return null;
    }

    /**
     * Set selection value
     *
     * @return String id of the selected item
     */
    public void setSelectedValue(Set<String> values) {
        listI.setValue(values);
        listI.setData(listI.getValue());
    }

    /**
     * Set selection value
     *
     * @param valueId id of the selected item
     */
    public void setSelectedValue(String valueId) {
        if (valueId.equalsIgnoreCase("")) {
            listI.getItemIds().forEach((o) -> {
                listI.unselect(o);
            });
            listII.getItemIds().forEach((o) -> {
                listII.unselect(o);
            });
            listIII.getItemIds().forEach((o) -> {
                listIII.unselect(o);
            });
            listI.setData(null);
            listII.setData(null);
            listIII.setData(null);
            listIV.setData(null);

            return;
        }
        listI.select(valueId);
        listI.setData(listI.getValue());
    }

    /**
     * Set the list is required to have a value.
     *
     * @param required the selection is required
     * @param requiredMessage the error appear if no data selected
     */
    public void setRequired(boolean required, String requiredMessage) {
        listI.setRequired(required);
        listI.setRequiredError(requiredMessage);

    }

    public void setViewList(boolean view) {
        if (view || !expandable) {
            listI.removeStyleName("hidelist");
            listI.addStyleName("showlist");
        } else {
            listI.addStyleName("hidelist");
            listI.removeStyleName("showlist");
        }

    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        if (event.getClickedComponent() != null) {
            if (listI.getStyleName().contains("hidelist")) {
                listI.removeStyleName("hidelist");
                listI.addStyleName("showlist");
            } else if (listI.getStyleName().contains("hidelist")) {
                listI.addStyleName("hidelist");
                listI.removeStyleName("showlist");
            }
        } else if (listI.getStyleName().contains("hidelist")) {
            listI.removeStyleName("hidelist");
            listI.addStyleName("showlist");
        } else {
            listI.addStyleName("hidelist");
            listI.removeStyleName("showlist");

        }
    }

    public boolean isValid() {
        listI.setRequired(true);
        boolean check = listI.isValid();
        listI.setRequired(!check);
        return check;
    }

    public boolean isModified() {
        return !listI.getValue().toString().equalsIgnoreCase(listI.getData() + "");
    }

    public void addValueChangeListener(Property.ValueChangeListener listener) {
        listI.addValueChangeListener(listener);
        listII.addValueChangeListener(listener);
        listIII.addValueChangeListener(listener);
        listIV.addValueChangeListener(listener);
    }

    public void selectAll() {
        listI.getItemIds().forEach((o) -> {
            listI.select(o);
        });
        listII.getItemIds().forEach((o) -> {
            listII.select(o);
        });
        listIII.getItemIds().forEach((o) -> {
            listIII.select(o);
        });
        listIV.getItemIds().forEach((o) -> {
            listIV.select(o);
        });
    }

    public void unselectAll() {
        listI.getItemIds().forEach((o) -> {
            listI.unselect(o);
        });
        listII.getItemIds().forEach((o) -> {
            listII.unselect(o);
        });
        listIII.getItemIds().forEach((o) -> {
            listIII.unselect(o);
        });
        listIV.getItemIds().forEach((o) -> {
            listIV.unselect(o);
        });
    }

    public void selectByIndexes(Set<Integer> indexes) {
        indexes.forEach((index) -> {
            selectByIndex(index);
        });
    }

    public void selectByIndex(int index) {
        if (index < 3) {
            listI.select(listI.getItemIds().toArray()[index]);
        } else if (index >= 3 && index < 6) {
            listII.select(listII.getItemIds().toArray()[index - 3]);
        } else if (index >= 6 && index < 9) {
            listIII.select(listIII.getItemIds().toArray()[index - 6]);
        } else if (index >= 9) {
            listIV.select(listIV.getItemIds().toArray()[index - 9]);
        }
    }

}

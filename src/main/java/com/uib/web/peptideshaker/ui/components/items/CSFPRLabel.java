package com.uib.web.peptideshaker.ui.components.items;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Link;

/**
 * This class represents link to CSF-PR
 *
 * @author Yehia Farag
 */
public class CSFPRLabel extends Link implements Comparable<CSFPRLabel> {

    private final Boolean available;
    private final String link;

    public CSFPRLabel(String accession, boolean available,String link) {
        this.available = available; 
        this.link = link;
        if (available) {
           
//            link = "/searchby:Protein*Accession___searchkey:" + accession + "__";
            CSFPRLabel.this.setIcon(new ThemeResource("img/csf_logo.png"));
            CSFPRLabel.this.setResource(new ExternalResource(link));
            CSFPRLabel.this.setDescription("View in CSF-PR");

        } else {
            link = "not available on CSF-PR";
            CSFPRLabel.this.setIcon(new ThemeResource("img/csf_logo_disable.png"));
            CSFPRLabel.this.setDescription("Protein is not available on CSF-PR");
        }
        CSFPRLabel.this.setStyleName("imgonly");
        CSFPRLabel.this.setEnabled(available);

        CSFPRLabel.this.setTargetName("_blank");

    }

    @Override
    public int compareTo(CSFPRLabel t) {
        return this.available.compareTo(t.available);
    }

    @Override
    public String toString() {
        return link;
    }

}

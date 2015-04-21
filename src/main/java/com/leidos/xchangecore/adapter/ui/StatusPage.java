package com.leidos.xchangecore.adapter.ui;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

@SuppressWarnings("serial")
public class StatusPage
    extends WebPage {

    // Label status;

    /**
     * @param window
     * @param deleted
     * @param updated
     * @param created
     */
    public StatusPage(final ModalWindow window, int created, int updated, int deleted) {

        add(new Label("created", "Created: " + created + " records"));
        add(new Label("updated", "Updated: " + updated + " records"));
        add(new Label("deleted", "Deleted: " + deleted + " records"));

        add(new AjaxLink<Void>("close") {

            @Override
            public void onClick(AjaxRequestTarget target) {

                window.close(target);
            }
        });
    }
}
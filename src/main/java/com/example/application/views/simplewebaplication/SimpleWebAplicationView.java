package com.example.application.views.simplewebaplication;

import com.example.application.data.entity.SampleWebApplication_;
import com.example.application.data.service.SampleWebApplication_Service;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("SimpleWebAplication")
@Route(value = "simpleWebAplication/:sampleWebApplication_ID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class SimpleWebAplicationView extends Div implements BeforeEnterObserver {

    private final String SAMPLEWEBAPPLICATION__ID = "sampleWebApplication_ID";
    private final String SAMPLEWEBAPPLICATION__EDIT_ROUTE_TEMPLATE = "simpleWebAplication/%s/edit";

    private final Grid<SampleWebApplication_> grid = new Grid<>(SampleWebApplication_.class, false);

    private TextField nama;
    private TextField email;
    private TextField address;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<SampleWebApplication_> binder;

    private SampleWebApplication_ sampleWebApplication_;

    private final SampleWebApplication_Service sampleWebApplication_Service;

    public SimpleWebAplicationView(SampleWebApplication_Service sampleWebApplication_Service) {
        this.sampleWebApplication_Service = sampleWebApplication_Service;
        addClassNames("simple-web-aplication-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("address").setAutoWidth(true);
        grid.setItems(query -> sampleWebApplication_Service.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent()
                        .navigate(String.format(SAMPLEWEBAPPLICATION__EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SimpleWebAplicationView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(SampleWebApplication_.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sampleWebApplication_ == null) {
                    this.sampleWebApplication_ = new SampleWebApplication_();
                }
                binder.writeBean(this.sampleWebApplication_);
                sampleWebApplication_Service.update(this.sampleWebApplication_);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(SimpleWebAplicationView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> sampleWebApplication_Id = event.getRouteParameters().get(SAMPLEWEBAPPLICATION__ID)
                .map(Long::parseLong);
        if (sampleWebApplication_Id.isPresent()) {
            Optional<SampleWebApplication_> sampleWebApplication_FromBackend = sampleWebApplication_Service
                    .get(sampleWebApplication_Id.get());
            if (sampleWebApplication_FromBackend.isPresent()) {
                populateForm(sampleWebApplication_FromBackend.get());
            } else {
                Notification.show(String.format("The requested sampleWebApplication_ was not found, ID = %s",
                        sampleWebApplication_Id.get()), 3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(SimpleWebAplicationView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nama = new TextField("Nama");
        email = new TextField("Email");
        address = new TextField("Address");
        formLayout.add(nama, email, address);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(SampleWebApplication_ value) {
        this.sampleWebApplication_ = value;
        binder.readBean(this.sampleWebApplication_);

    }
}

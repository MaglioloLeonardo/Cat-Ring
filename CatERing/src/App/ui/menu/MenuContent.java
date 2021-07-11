package App.ui.menu;

import App.BusinessLogic.CatERing;
import App.BusinessLogic.UserManagement.UseCaseLogicException;
import App.BusinessLogic.MenuManagement.Menu;
import App.BusinessLogic.MenuManagement.MenuItem;
import App.BusinessLogic.MenuManagement.Section;
import App.BusinessLogic.RecipeManagement.Recipe;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import App.ui.general.EventsInfoDialog;

import java.io.IOException;
import java.util.Optional;

public class MenuContent {
    @FXML
    Label titleLabel;

    @FXML
    ListView<Section> sectionList;

    @FXML
    Label itemsTitle;

    @FXML
    ToggleButton freeItemsToggle;

    @FXML
    ListView<MenuItem> itemsList;

    @FXML
    Button addItemButton;

    @FXML
    Pane itemsPane;
    @FXML
    GridPane centralPane;
    Pane emptyPane;
    boolean paneVisible = true;


    MenuManagement menuManagementController;

    public void initialize() {
        Menu toview = CatERing.getInstance().getMenuManager().getCurrentMenu();
        if (toview != null) {
            titleLabel.setText(toview.getTitle());

            sectionList.setItems(toview.getSections());
        }

        sectionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        sectionList.getSelectionModel().select(null);
        sectionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Section>() {
            @Override
            public void changed(ObservableValue<? extends Section> observableValue, Section oldSection, Section newSection) {
                if (newSection != null && newSection != oldSection) {
                    if (!paneVisible) {
                        centralPane.getChildren().remove(emptyPane);
                        centralPane.add(itemsPane, 1, 0);
                        paneVisible = true;
                    }
                    itemsTitle.setText("Voci di " + newSection.getName());
                    freeItemsToggle.setSelected(false);
                    itemsList.setItems(newSection.getItems());
                    // enable other section actions
                    addItemButton.setDisable(false);
                } else if (newSection == null) {
                    // disable section actions
                }
            }
        });

        itemsList.setItems(FXCollections.emptyObservableList());
        itemsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        itemsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MenuItem>() {
            @Override
            public void changed(ObservableValue<? extends MenuItem> observableValue, MenuItem oldItem, MenuItem newItem) {
                if (newItem != null && newItem != oldItem) {
                    // enable item actions
                } else if (newItem == null) {
                    // disable item actions
                }
            }
        });
        emptyPane = new BorderPane();
        centralPane.getChildren().remove(itemsPane);
        centralPane.add(emptyPane, 1, 0);
        paneVisible = false;

        freeItemsToggle.setSelected(false);
    }

    @FXML
    public void exitButtonPressed() {
        menuManagementController.showMenuList();
    }

    @FXML
    public void publishButtonPressed() {
        try {
            CatERing.getInstance().getMenuManager().publish();
        } catch (UseCaseLogicException ex) {
            ex.printStackTrace();
        }
        menuManagementController.showMenuList();
    }

    @FXML
    public void addSectionPressed() {
        TextInputDialog dial = new TextInputDialog("Sezione");
        dial.setTitle("Aggiungi una sezione");
        dial.setHeaderText("Scegli il nome per la nuova sezione");
        Optional<String> result = dial.showAndWait();

        if (result.isPresent()) {
            try {
                CatERing.getInstance().getMenuManager().defineSection(result.get());
            } catch (UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML void addItemPressed() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("insert-item-dialog.fxml"));
        try {
            BorderPane pane = loader.load();
            InsertItemDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            Section selSection = sectionList.getSelectionModel().getSelectedItem();
            stage.setTitle("Inserisci una voce " + (selSection == null ?
                    "libera" : "nella sezione " + selSection.getName()));
            stage.setScene(new Scene(pane));

            stage.showAndWait();

            Optional<Recipe> chosen = controller.getSelectedRecipe();
            Optional<String> desc = controller.getDescription();
            if (chosen.isPresent()) {
                if (selSection != null) {
                    if (desc.isPresent()) {
                        CatERing.getInstance().getMenuManager().insertItem(chosen.get(), selSection, desc.get());
                    } else {
                        CatERing.getInstance().getMenuManager().insertItem(chosen.get(), selSection);
                    }
                } else {
                    if (desc.isPresent()) {
                        CatERing.getInstance().getMenuManager().insertItem(chosen.get(), desc.get());
                    } else {
                        CatERing.getInstance().getMenuManager().insertItem(chosen.get());
                    }
                }
            }
        } catch (IOException | UseCaseLogicException ex) {
            ex.printStackTrace();
        }
    }

    public void setMenuManagementController(MenuManagement menuManagement) {
        menuManagementController = menuManagement;
    }

    @FXML
    public void eventInfoButtonPressed() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../general/events-info-dialog.fxml"));
        try {
            BorderPane pane = loader.load();
            EventsInfoDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Eventi presenti nel sistema");
            stage.setScene(new Scene(pane, 600, 400));


            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void freeItemsToggleChanged() {
        if (freeItemsToggle.isSelected()) {
            this.sectionList.getSelectionModel().select(null);
            if (!paneVisible) {
                centralPane.getChildren().remove(emptyPane);
                centralPane.add(itemsPane, 1, 0);
                paneVisible = true;
            }
            itemsTitle.setText("Voci libere:");
            itemsList.setItems(CatERing.getInstance().getMenuManager().getCurrentMenu().getFreeItems());
            addItemButton.setDisable(false);
        } else {
            itemsTitle.setText("Voci");
            itemsList.setItems(FXCollections.emptyObservableList());
            addItemButton.setDisable(sectionList.getSelectionModel().getSelectedItem() == null);
            if (sectionList.getSelectionModel().getSelectedItem() == null && paneVisible) {
                centralPane.getChildren().remove(itemsPane);
                centralPane.add(emptyPane, 1, 0);
                paneVisible = false;
            }
        }
    }

    @FXML
    public void editFeaturesButtonPressed() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-features-dialog.fxml"));
        try {
            BorderPane pane = loader.load();
            MenuFeaturesDialog controller = loader.getController();

            Stage stage = new Stage();

            controller.setOwnStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Caratteristiche del menu");
            stage.setScene(new Scene(pane));


            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void editTitleButtonPressed() {
        TextInputDialog dial = new TextInputDialog(CatERing.getInstance().getMenuManager().getCurrentMenu().getTitle());
        dial.setTitle("Modifica il titolo");
        dial.setHeaderText("Scegli un nuovo titolo");
        Optional<String> result = dial.showAndWait();

        if (result.isPresent()) {
            try {
                CatERing.getInstance().getMenuManager().changeTitle(result.get());
                this.titleLabel.setText(result.get());
            } catch(UseCaseLogicException ex) {
                ex.printStackTrace();
            }
        }
    }
}

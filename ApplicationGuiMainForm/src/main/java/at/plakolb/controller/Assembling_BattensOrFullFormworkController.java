/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.*;
import at.plakolb.calculationlogic.db.entity.*;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.util.UtilityFormat;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Over this view its possible to calculate either the battens or the full
 * formwork.
 *
 * @author Kepplinger
 */
public class Assembling_BattensOrFullFormworkController implements Initializable, Observer {

    private static Assembling_BattensOrFullFormworkController instance;

    @FXML
    private ChoiceBox<String> cb_roofType;
    @FXML
    private Pane contentPane;
    @FXML
    private Label title;
    @FXML
    private AnchorPane ap_general;
    @FXML
    private ComboBox<Product> cb_product;
    @FXML
    private TextField tf_assemblingDuration;
    @FXML
    private TextField tf_price;
    @FXML
    private TextField tf_workCosts;
    @FXML
    private Label lb_productCosts;
    @FXML
    private Label lb_assemblingCosts;
    @FXML
    private Label lb_totalCosts;
    @FXML
    private VBox vbox_firstSelection;
    @FXML
    private VBox vb_brick;
    @FXML
    private VBox vb_sheet;
    @FXML
    private VBox vbox_normal;

    private double price;
    private Worth workCosts;
    private Worth assemblingDuration;
    private Worth productCosts;
    private Worth montageCosts;
    private Worth totalCosts;
    private Component component;

    private int loadedIndex = -1;

    private Node tiledRoof;
    private Node sheetRoof;

    /**
     * Initializes the controller class and all worth objects. Also adds many
     * change listeners to verify the user input.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        cb_roofType.getItems().addAll("Ziegeldach", "Blechdach");
        ParameterController parameterController = new ParameterController();

        workCosts = new Worth(parameterController.findParameterPByShortTerm("KPLV"));
        assemblingDuration = new Worth(parameterController.findParameterPByShortTerm("ZPLV"));
        montageCosts = new Worth(parameterController.findParameterPByShortTerm("KMLatVoll"));
        productCosts = new Worth(parameterController.findParameterPByShortTerm("KPLatVoll"));
        totalCosts = new Worth(parameterController.findParameterPByShortTerm("GKLatVoll"));

        lb_assemblingCosts.setText(UtilityFormat.getStringForLabel(montageCosts));
        lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));
        lb_totalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));

        tf_assemblingDuration.setText(UtilityFormat.getStringForTextField(assemblingDuration));
        tf_workCosts.setText(UtilityFormat.getStringForTextField(workCosts));

        tf_price.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            setPrice();
            calculate();
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });

        tf_assemblingDuration.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_assemblingDuration, assemblingDuration);
            calculate();
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });

        tf_workCosts.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            UtilityFormat.setWorthFromTextField(tf_workCosts, workCosts);
            calculate();
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });


        cb_product.getSelectionModel().selectedItemProperty().addListener((source, oldValue, newValue) -> {
            if (newValue != null) {
                tf_price.setText(UtilityFormat.getStringForTextField(newValue.getPriceUnit()));
            }
        });

        if (ProjectViewController.getOpenedProject() != null) {
            if (ProjectViewController.getOpenedProject().getRoofMaterial() != null
                    && !ProjectViewController.getOpenedProject().getRoofMaterial().equals("")) {
                VBox box = (VBox) vbox_firstSelection.getParent();
                box.getChildren().remove(vbox_firstSelection);
                vbox_normal.setVisible(true);
            }
            load();
        } else {
            String categoryString = loadedIndex == 0 ? "L" : "VS";
            Category category = new CategoryController().findCategoryByShortTerm(categoryString);

            component = new Component();
            component.setComponentType("Produkt");
            component.setCategory(category);
        }

        try {
            tiledRoof = FXMLLoader.load(getClass().getResource("/fxml/Assembling_TiledRoof.fxml"));
            sheetRoof = FXMLLoader.load(getClass().getResource("/fxml/Assembling_SheetRoof.fxml"));
        } catch (IOException ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't open Assembling_TiledRoof.fxml and Assembling_SheetRoof.fxml", ex);
        }

        //Add change listener to change the fxml file of the content pane if the selection has changed.
        cb_roofType.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            ap_general.setVisible(true);
            ProductController productController = new ProductController();
            if (newValue.intValue() == 0) {
                contentPane.getChildren().clear();
                contentPane.getChildren().add(tiledRoof);
                title.setText("Lattung");
                ProjectViewController.getOpenedProject().setRoofMaterial("Ziegeldach");
                cb_product.setItems(FXCollections.observableArrayList(productController.findByProductTypeOrderByName(ProductType.BATTEN)));
            } else if (newValue.intValue() == 1) {
                contentPane.getChildren().clear();
                contentPane.getChildren().add(sheetRoof);
                title.setText("Vollschalung");
                ProjectViewController.getOpenedProject().setRoofMaterial("Blechdach");
                cb_product.setItems(FXCollections.observableArrayList(productController.findByProductTypeOrderByName(ProductType.FORMWORK)));
            }

            if (oldValue.intValue() != -1) {
                price = 0;
                tf_price.clear();
            }

            String categoryString = newValue.intValue() == 0 ? "L" : "VS";
            component.setCategory(new CategoryController().findCategoryByShortTerm(categoryString));
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.TRUE);
        });

        vb_brick.setOnMouseClicked((event) -> {
            VBox box = (VBox) vbox_firstSelection.getParent();
            box.getChildren().remove(vbox_firstSelection);
            vbox_normal.setVisible(true);
            cb_roofType.getSelectionModel().select("Ziegeldach");
        });

        vb_sheet.setOnMouseClicked((event) -> {
            VBox box = (VBox) vbox_firstSelection.getParent();
            box.getChildren().remove(vbox_firstSelection);
            vbox_normal.setVisible(true);
            cb_roofType.getSelectionModel().select("Blechdach");
        });

        if (loadedIndex != -1) {
            cb_roofType.getSelectionModel().select(ProjectViewController.getOpenedProject().getRoofMaterial());
            ModifyController.getInstance().setAssembling_battensOrFullFormwork(Boolean.FALSE);
        }
    }

    public static Assembling_BattensOrFullFormworkController getInstance() {
        return instance;
    }

    private void setPrice() {
        tf_price.setText(tf_price.getText().replace('.',',').replaceAll("[^\\d,]", ""));
        tf_price.setText(UtilityFormat.removeUnnecessaryCommas(tf_price.getText()));

        if (tf_price.getText().isEmpty()) {
            this.price = 0;
        } else if (tf_price.getText().isEmpty() || Double.valueOf(tf_price.getText().replace(',','.')) < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Der Preis muss eine positive Zahl sein!\nEingabe: \"" + price + "\"", ButtonType.OK);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        } else {
            this.price = Double.valueOf(tf_price.getText().replace(',','.'));
            component.setPriceComponent(price);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        calculate();
    }

    public Component getComponent() {
        return component;
    }

    public Worth getMaterial() {
        return productCosts;
    }

    public Worth getWage() {
        return montageCosts;
    }

    public Worth getTotalCosts() {
        return totalCosts;
    }

    /**
     * Loads all required values from the database into the view.
     */
    public void load() {
        WorthController worthController = new WorthController();
        ParameterController parameterController = new ParameterController();
        Project project = ProjectViewController.getOpenedProject();

        if (project != null && cb_product != null) {

            workCosts = (worthController.findWorthByShortTermAndProjectId("KPLV", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KPLV", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("KPLV"));
            assemblingDuration = (worthController.findWorthByShortTermAndProjectId("ZPLV", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("ZPLV", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("ZPLV"));
            montageCosts = (worthController.findWorthByShortTermAndProjectId("KMLatVoll", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KMLatVoll", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("KMLatVoll"));
            productCosts = (worthController.findWorthByShortTermAndProjectId("KPLatVoll", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("KPLatVoll", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("KPLatVoll"));
            totalCosts = (worthController.findWorthByShortTermAndProjectId("GKLatVoll", project.getId()) != null)
                    ? worthController.findWorthByShortTermAndProjectId("GKLatVoll", project.getId()) : new Worth(parameterController.findParameterPByShortTerm("GKLatVoll"));

            if ("Ziegeldach".equals(ProjectViewController.getOpenedProject().getRoofMaterial())) {
                loadedIndex = 0;
            } else if ("Blechdach".equals(ProjectViewController.getOpenedProject().getRoofMaterial())) {
                loadedIndex = 1;
            }

            String categoryString = loadedIndex == 0 ? "L" : "VS";
            Category category = new CategoryController().findCategoryByShortTerm(categoryString);

            component = new ComponentController().findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(), "Produkt", category.getId());

            if (component != null) {
                Double componentPrice = component.getPriceComponent();
                cb_product.getSelectionModel().select(component.getProduct());
                if (componentPrice != null) {
                    tf_price.setText(UtilityFormat.getStringForTextField(componentPrice));
                }
            } else {
                component = new Component();
                component.setComponentType("Produkt");
                component.setProject(project);
                component.setCategory(category);
            }

            lb_assemblingCosts.setText(UtilityFormat.getStringForLabel(montageCosts));
            lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));
            lb_totalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));

            tf_assemblingDuration.setText(UtilityFormat.getStringForTextField(assemblingDuration));
            tf_workCosts.setText(UtilityFormat.getStringForTextField(workCosts));
        }

        calculate();
    }

    /**
     * Calculates all required values.
     */
    public void calculate() {
        try {
            int index = cb_roofType.getSelectionModel().getSelectedIndex();

            if (Assembling_TiledRoofController.getInstance() != null)
                Assembling_TiledRoofController.getInstance().calculate();

            if (Assembling_SheetRoofController.getInstance() != null)
                Assembling_SheetRoofController.getInstance().calculate();

            //Kosten Produkt Lattung oder Vollschalung
            if (Assembling_SheetRoofController.getInstance() != null) {
                productCosts.setWorth(price * (index == 0 ? Assembling_TiledRoofController.getInstance().getLength().getWorth()
                        : Assembling_SheetRoofController.getInstance().getFormwork().getWorth()));
                lb_productCosts.setText(UtilityFormat.getStringForLabel(productCosts));
            }

            //Kosten Montage Lattung oder Vollschalung
            montageCosts.setWorth(workCosts.getWorth() * assemblingDuration.getWorth());
            lb_assemblingCosts.setText(UtilityFormat.getStringForLabel(montageCosts));

            //Gesamtkosten Lattung oder Vollschalung
            totalCosts.setWorth(productCosts.getWorth() + montageCosts.getWorth());
            lb_totalCosts.setText(UtilityFormat.getStringForLabel(totalCosts));

        } catch (Exception ex) {
            if (ProjectViewController.isProjectOpened()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Werte können nicht berechnet werden!\nFehlerinformation: " + ex.getLocalizedMessage(), ButtonType.OK);
                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                alert.showAndWait();
                Logging.getLogger().log(Level.SEVERE, "Assembling_BattensOrFullFormworkController: caluclate method didn't work.", ex);
            }
        }

        Product product = cb_product.getSelectionModel().getSelectedItem();

        if (Assembling_SheetRoofController.getInstance() != null) {
            if (product != null) {
                component.setDescription(product.getName());
                component.setLengthComponent(product.getLengthProduct());
                component.setWidthComponent(product.getWidthProduct());
                component.setHeightComponent(product.getHeightProduct());
                component.setProduct(product);
                component.setUnit(product.getUnit());
            } else {
                component.setDescription(title.getText());
                component.setLengthComponent(null);
                component.setWidthComponent(null);
                component.setHeightComponent(null);
                component.setProduct(null);
                component.setUnit(null);
            }
            component.setPriceComponent(price);

            if (cb_roofType.getSelectionModel().getSelectedIndex() == 0) {
                component.setNumberOfProducts(Assembling_TiledRoofController.getInstance().getLength().getWorth());
            } else {
                component.setNumberOfProducts(Assembling_SheetRoofController.getInstance().getFormwork().getWorth());
            }
        }
    }

    /**
     * Checks which rooftype is selected, to save the correct values.
     */
    public void persist() {
        if (loadedIndex >= 0 && loadedIndex != cb_roofType.getSelectionModel().selectedIndexProperty().get()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Zuletzt wurde \"" + cb_roofType.getItems().get(loadedIndex) + "\" als Dachmaterial gespeichert.\nWollen Sie nun das Dachmaterial auf \"" + cb_roofType.getSelectionModel().getSelectedItem() + "\" ändern?");
            alert.setOnCloseRequest((event) -> {
                if (alert.getResult() == ButtonType.NO) {
                    if (loadedIndex == 0) {
                        saveTiledRoof();
                    } else {
                        saveSheetRoof();
                    }
                } else if (cb_roofType.getSelectionModel().getSelectedIndex() == 0) {
                    saveTiledRoof();
                } else {
                    saveSheetRoof();
                }
                saveRemaining();
            });
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.YES);
            alert.getButtonTypes().add(ButtonType.NO);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        } else {
            if (cb_roofType.getSelectionModel().getSelectedIndex() == 0) {
                saveTiledRoof();
            } else {
                saveSheetRoof();
            }
            saveRemaining();
        }
    }

    /**
     * Persists all values from the view to the database.
     */
    private void saveRemaining() {
        Project project = ProjectViewController.getOpenedProject();
        WorthController worthController = new WorthController();
        ComponentController componentController = new ComponentController();

        try {
            if (project != null && !ProjectViewController.isProjectOpened()) {
                component.setDescription("Lattung/Vollschalung");

                assemblingDuration.setProject(project);
                workCosts.setProject(project);
                montageCosts.setProject(project);
                productCosts.setProject(project);
                totalCosts.setProject(project);
                component.setProject(project);

                worthController.create(assemblingDuration);
                worthController.create(workCosts);
                worthController.create(montageCosts);
                worthController.create(productCosts);
                worthController.create(totalCosts);
                componentController.create(component);
            } else {
                worthController.edit(assemblingDuration);
                worthController.edit(workCosts);
                worthController.edit(montageCosts);
                worthController.edit(productCosts);
                worthController.edit(totalCosts);
                componentController.edit(component);
            }
        } catch (Exception ex) {
            Logging.getLogger().log(Level.SEVERE, "Assembling_BattensOrFullFormworkController: persist method didn't work.", ex);
        }
    }

    /**
     * Persists the data from the additional view inside this one.
     */
    private void saveSheetRoof() {
        Assembling_SheetRoofController.getInstance().persist();
    }

    /**
     * Persists the data from the additional view inside this one.
     */
    private void saveTiledRoof() {
        Assembling_TiledRoofController.getInstance().persist();
    }
}

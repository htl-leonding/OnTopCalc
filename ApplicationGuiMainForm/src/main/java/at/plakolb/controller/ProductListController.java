/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.AssemblyController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.db.entity.Assembly;
import at.plakolb.calculationlogic.db.entity.Component;
import at.plakolb.calculationlogic.db.entity.Product;
import at.plakolb.calculationlogic.db.entity.Unit;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.util.UtilityFormat;
import at.plakolb.edit.ProductNameCell;
import at.plakolb.edit.ProductUnitCell;
import at.plakolb.edit.ProductValueCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ProductListController implements Initializable {

    private static ProductListController instance;

    @FXML
    private TableView<Product> tv_Products;
    @FXML
    private TableColumn<Product, String> tc_Name;
    @FXML
    private TableColumn<Product, String> tc_Width;
    @FXML
    private TableColumn<Product, String> tc_Height;
    @FXML
    private TableColumn<Product, String> tc_Length;
    @FXML
    private TableColumn<Product, String> tc_ColourFactor;
    @FXML
    private TableColumn<Product, String> tc_PriceUnit;
    @FXML
    private TableColumn<Product, Unit> tc_Unit;
    @FXML
    private TableColumn tc_Buttons;
    @FXML
    private MenuButton mb_ProductTypesFilter;

    private ObservableList<Product> products;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        products = null;

        tv_Products.setEditable(true);
        VBox placeholder = new VBox(new ImageView(new Image("/images/cloud.png")), new Label("Keine Daten vorhanden"));
        placeholder.setAlignment(Pos.CENTER);
        tv_Products.setPlaceholder(placeholder);

        tc_Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_Name.setCellFactory((TableColumn<Product, String> param) -> new ProductNameCell());
        tc_Name.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            product.setName(event.getNewValue());
            refreshTable();
        });

        tc_Width.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getWidthProduct() != null && param.getValue().getWidthProduct() != 0) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getWidthProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Width.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_Width.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                product.setWidthProduct(Double.parseDouble(event.getNewValue().replace(',','.')));
            } else {
                product.setWidthProduct(0d);
            }
            refreshTable();
        });

        tc_Height.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getHeightProduct() != null && param.getValue().getHeightProduct() != 0) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getHeightProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Height.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_Height.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                product.setHeightProduct(Double.parseDouble(event.getNewValue().replace(',','.')));
            } else {
                product.setHeightProduct(0d);
            }
            refreshTable();
        });

        tc_Length.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getLengthProduct() != null && param.getValue().getLengthProduct() != 0) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getLengthProduct()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_Length.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_Length.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                product.setLengthProduct(Double.parseDouble(event.getNewValue().replace(',','.')));
            } else {
                product.setLengthProduct(0d);
            }
            refreshTable();
        });

        tc_PriceUnit.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getPriceUnit() != null && param.getValue().getPriceUnit() != 0) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getPriceUnit()) + " €");
            } else {
                return new ReadOnlyObjectWrapper<>("0 €");
            }
        });
        tc_PriceUnit.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_PriceUnit.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            String price = "";
            Product product = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            price = event.getNewValue();

            product.setPriceUnit(Double.parseDouble(price.replace(',','.')));
            refreshTable();
        });

        tc_ColourFactor.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> param) -> {
            if (param.getValue().getColorFactor() != null && param.getValue().getColorFactor() != 0) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForTableColumn(param.getValue().getColorFactor()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_ColourFactor.setCellFactory((TableColumn<Product, String> param) -> new ProductValueCell());
        tc_ColourFactor.setOnEditCommit((CellEditEvent<Product, String> event) -> {
            Product product = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            if (!event.getNewValue().equals("")) {
                product.setColorFactor(Double.parseDouble(event.getNewValue().replace(',','.')));
            } else {
                product.setColorFactor(0d);
            }
            refreshTable();
        });

        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        tc_Unit.setCellFactory((TableColumn<Product, Unit> param) -> new ProductUnitCell());
        tc_Unit.setOnEditCommit((CellEditEvent<Product, Unit> event) -> {
            Product product = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            product.setUnit(event.getNewValue());
        });

        tc_Buttons.setCellFactory((new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
            @Override
            public TableCell call(final TableColumn<Product, String> param) {
                final TableCell<Product, String> cell = new TableCell<Product, String>() {

                    final Label delete = new Label();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            delete.setId("deleteFinal");
                            delete.setTooltip(new Tooltip("Produkt löschen"));
                            delete.setOnMouseClicked((MouseEvent event) -> {
                                try {
                                    boolean isUsed = false;
                                    Product product = tv_Products.getSelectionModel().getSelectedItem();
                                    
                                    for (Component component : new ComponentController().findAll()) {
                                        if (component.getProduct() != null && component.getProduct().equals(product)) {
                                            isUsed = true;
                                        }
                                    }

                                    for (Assembly assembly : new AssemblyController().findAll()) {
                                        if (assembly.getProduct() != null && assembly.getProduct().equals(product)) {
                                            isUsed = true;
                                        }
                                    }

                                    if (isUsed) {
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Vorsicht! Das ausgewählte Produkt wird bereits bei verschiedenen Bauteilen verwendet. Wenn Sie es jetzt fortfahren werden diese Bauteile auch gelöscht.",
                                                ButtonType.YES, ButtonType.CANCEL);
                                        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                                        alert.showAndWait();
                                        if (alert.getResult() == ButtonType.YES) {
                                            products.remove(product);
                                            new ProductController().destroy(product.getId());
                                            refreshTable();
                                        }
                                    } else {
                                        products.remove(product);
                                        new ProductController().destroy(product.getId());
                                        refreshTable();
                                    }
                                } catch (Exception ex) {
                                    Logging.getLogger().log(Level.SEVERE, "Couldn't delete product.", ex);
                                }
                            });
                            setGraphic(delete);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        }));

        tv_Products.setItems(FXCollections.observableArrayList(new ProductController().findAll()));

        addProductTypesWithFilter(mb_ProductTypesFilter);
        refreshTable();
    }

    public static ProductListController getInstance() {
        return instance;
    }

    /**
     * Adds a new product to the database.
     *
     * @param event
     */
    @FXML
    private void addProduct(ActionEvent event) {

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/ProductCreator.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Produkt erstellen");
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException ex) {
            Logging.getLogger().log(Level.SEVERE, "Couldn't open ProductCreator.fxml.", ex);
        }
    }

    /**
     * Adds all Product Types to the Menu Button and adds a filter to every
     * item.
     */
    private void addProductTypesWithFilter(MenuButton menuButton) {
        for (ProductType productType : ProductType.values()) {
            MenuItem menuItem = new MenuItem(productType.toString());
            menuItem.setOnAction(eventHandler -> {
                menuButton.setText(menuItem.getText());
                refreshTable();
            });
            menuButton.getItems().add(menuItem);
        }

        MenuItem menuItem = new MenuItem("Alle");
        menuItem.setOnAction(eventHandler -> {
            filterList(null);
            menuButton.setText("Alle");
        });
        menuButton.getItems().add(menuItem);
    }

    /**
     * Filters the List View by the selected Product Type
     *
     * @param productType
     */
    private void filterList(ProductType productType) {
        ProductController productController = new ProductController();

        if (productType == null) {
            products = FXCollections.observableArrayList(productController.findAll());
        } else {
            products = FXCollections.observableArrayList(productController.findByProductTypeOrderByName(productType));
        }
        tv_Products.setItems(products);
    }

    public void setUnitFromCell(ProductUnitCell cell) {
        products.get(cell.getIndex()).setUnit(cell.getSelectedUnit());
        refreshTable();
    }

    public void persistsProducts() {
        ProductController productController = new ProductController();

        if (products != null) {
            products.stream().forEach((product) -> {
                try {
                    if (product.getId() == null) {
                        productController.create(product);
                    } else {
                        productController.edit(product);
                    }
                } catch (Exception ex) {
                    Logging.getLogger().log(Level.SEVERE, "Couldn't perists products.", ex);
                }
            });
        }
    }

    /**
     * Refreshes the Table View.
     */
    public void refreshTable() {
        persistsProducts();
        filterList(ProductType.getProductType(mb_ProductTypesFilter.getText()));
        tv_Products.getColumns().get(0).setVisible(false);
        tv_Products.getColumns().get(0).setVisible(true);
    }
}

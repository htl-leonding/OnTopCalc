package at.plakolb.controller;

/*	HTL Leonding	*/
import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.entity.Category;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author in130079
 */
public class Project_ConstructionmaterialListController implements Initializable {

    private static Project_ConstructionmaterialListController instance;
    @FXML
    private TableView<Component> tv_Materials;
    @FXML
    private TableColumn<Component, Category> tc_Category;
    @FXML
    private TableColumn<Component, String> tc_ProductName;
    @FXML
    private TableColumn<Component, Double> tc_Length;
    @FXML
    private TableColumn<Component, Integer> tc_Amount;
    @FXML
    private TableColumn<Component, Double> tc_Volume;
    @FXML
    private TableColumn<Component, Double> tc_PricePerCubic;
    @FXML
    private TableColumn<Component, Double> tc_Price;
    @FXML
    private TableColumn<Component, Double> tc_CuttingHours;
    @FXML
    private TableColumn<Component, Double> tc_CuttingPricePerHours;
    @FXML
    private TableColumn<Component, Double> tc_CuttingPrice;
    @FXML
    private ComboBox<Category> cb_Category;
    @FXML
    private ComboBox<Product> cb_Product;
    @FXML
    private TextField tf_Amount;
    @FXML
    private Label lb_CubicSum;
    @FXML
    private Label lb_MaterialCostSum;
    @FXML
    private Label lb_CuttingTimeSum;
    @FXML
    private Label lb_CuttingCostSum;
    @FXML
    private Label lb_TotalCosts;
    @FXML
    private TableColumn<?, ?> tc_Deletion;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        tc_Category.setCellValueFactory(new PropertyValueFactory<>("category"));
        tc_ProductName.setCellValueFactory(new PropertyValueFactory<>("product"));
        tc_Length.setCellValueFactory(new PropertyValueFactory<>("lengthComponent"));
        tc_Amount.setCellValueFactory(new PropertyValueFactory<>("numberOfProducts"));

        tc_Volume.setCellValueFactory((CellDataFeatures<Component, Double> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<Double>(round(component.getWidthComponent() / 100 * component.getLengthComponent() * component.getHeightComponent() / 100 * component.getNumberOfProducts(), 4)) {
            };
        });

        tc_PricePerCubic.setCellValueFactory((CellDataFeatures<Component, Double> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<Double>(round(component.getPriceComponent() / ((component.getWidthComponent() / 100.0) * component.getLengthComponent() * (component.getHeightComponent() / 100.0)), 2 )) {
            };
        });

        tc_Price.setCellValueFactory(new PropertyValueFactory<>("priceComponent"));
        tc_CuttingHours.setCellValueFactory(new PropertyValueFactory<>("tailoringHours"));
        tc_CuttingPricePerHours.setCellValueFactory(new PropertyValueFactory<>("tailoringPricePerHour"));

        tc_CuttingPrice.setCellValueFactory((CellDataFeatures<Component, Double> param) -> {
            Component component = param.getValue();
            return new ReadOnlyObjectWrapper<Double>(component.getTailoringHours() * component.getTailoringPricePerHour()) {
            };
        });
        
        cb_Category.setItems(FXCollections.observableArrayList(new CategoryController().findAll()));
        cb_Product.setItems(FXCollections.observableArrayList(new ProductController().findByProductTypeOrderByName(ProductType.WOOD)));
        cb_Category.getSelectionModel().select(0);
        cb_Product.getSelectionModel().select(0);
    }

    public static Project_ConstructionmaterialListController getInstance() {
        return instance;
    }

    /**
     * Adds a new component from the UI elements.
     * @param event 
     */
    @FXML
    private void addProduct(ActionEvent event) {
        ParameterController parameterController = new ParameterController();
        Product product = cb_Product.getValue();
        Category category = cb_Category.getValue();

        Component component = new Component("",
                product.getWidthProduct(),
                product.getHeightProduct(),
                product.getLengthProduct(),
                product.getPriceUnit() * Integer.parseInt(tf_Amount.getText()),
                Integer.parseInt(tf_Amount.getText()),
                category,
                product.getUnit(),
                product,
                ProjectViewController.getInstance().getOpenedProject());

        component.setTailoringHours(parameterController.findParameterPByShortTerm("KZG").getDefaultValue());
        component.setTailoringPricePerHour(parameterController.findParameterPByShortTerm("KPSZ").getDefaultValue());

        new ComponentController().create(component);

        refreshTable();
    }

    /**
     * Refreshes the TableView
     */
    public void refreshTable() {
        tv_Materials.setItems(FXCollections.observableArrayList(new ComponentController().findAll()));
        tv_Materials.getColumns().get(0).setVisible(false);
        tv_Materials.getColumns().get(0).setVisible(true);
        calculateCosts();
    }
    
    /**
     * Calulates the sums of certain TableColumns
     */
    private void calculateCosts(){
        lb_CubicSum.setText(String.valueOf(getColmunSum(tc_Volume)) + " m³");
        lb_MaterialCostSum.setText(String.valueOf(getColmunSum(tc_Price)) + " €");
        lb_CuttingTimeSum.setText(String.valueOf(getColmunSum(tc_CuttingHours)) + " h");
        lb_CuttingCostSum.setText(String.valueOf(getColmunSum(tc_CuttingPrice)) + " €");
        lb_TotalCosts.setText(String.valueOf(getColmunSum(tc_CuttingPrice) + getColmunSum(tc_Price)) + " €");
    }

    /**
     * Calulates the sum of one TableColumn.
     * @param column
     * @return 
     */
    private double getColmunSum(TableColumn<Component, Double> column){
        double sum = 0;
        for (Component component : tv_Materials.getItems()) {
            sum += column.getCellData(component);
        }
        return sum;
    }
    
    /**
     * Rounds a double.
     * @param value
     * @param places
     * @return 
     */
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
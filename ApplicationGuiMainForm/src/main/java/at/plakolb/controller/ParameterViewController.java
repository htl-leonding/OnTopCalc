/*	HTL Leonding	*/
package at.plakolb.controller;

import at.plakolb.calculationlogic.db.controller.ParameterController;
import at.plakolb.calculationlogic.db.entity.ParameterP;
import at.plakolb.calculationlogic.db.entity.Unit;
import at.plakolb.calculationlogic.util.Logging;
import at.plakolb.calculationlogic.util.UtilityFormat;
import at.plakolb.edit.ParameterCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * FXML Controller class
 *
 * @author Andreas
 */
public class ParameterViewController implements Initializable {

    private static ParameterViewController instance;

    @FXML
    private TableView<ParameterP> tv_Prameter;
    @FXML
    private TableColumn<ParameterP, String> tc_LongTerm;
    @FXML
    private TableColumn<ParameterP, String> tc_DefaultValue;
    @FXML
    private TableColumn<ParameterP, Unit> tc_Unit;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;

        VBox placeholder = new VBox(new ImageView(new Image("/images/cloud.png")),new Label("Keine Daten vorhanden"));
        placeholder.setAlignment(Pos.CENTER);
        tv_Prameter.setPlaceholder(placeholder);
        
        tv_Prameter.setEditable(true);

        tc_LongTerm.setCellValueFactory(new PropertyValueFactory<>("longTerm"));

        tc_DefaultValue.setCellValueFactory((TableColumn.CellDataFeatures<ParameterP, String> param) -> {
            if (param.getValue().getDefaultValue() != null) {
                return new ReadOnlyObjectWrapper<>(UtilityFormat.getStringForLabel(param.getValue().getDefaultValue()));
            } else {
                return new ReadOnlyObjectWrapper<>("");
            }
        });
        tc_DefaultValue.setCellFactory((TableColumn<ParameterP, String> param) -> new ParameterCell());
        tc_DefaultValue.setOnEditCommit((CellEditEvent<ParameterP, String> event) -> {
            ParameterP parameter = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            parameter.setDefaultValue(Double.parseDouble(event.getNewValue().replace(',','.')));
            refreshTable();
        });

        tc_Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        tv_Prameter.setItems(FXCollections.observableArrayList(new ParameterController().findEditableParameter()));
    }

    public static ParameterViewController getInstance() {
        return instance;
    }

    /**
     * Refreshes the TableView.
     */
    public void refreshTable() {

        ParameterController parameterController = new ParameterController();

        for (ParameterP parameter : tv_Prameter.getItems()){
            try {
                parameterController.edit(parameter);
            } catch (Exception ex) {
                Logging.getLogger().log(Level.SEVERE, "Couldn't refresh parameters.", ex);
            }
        }

        tv_Prameter.getColumns().get(0).setVisible(false);
        tv_Prameter.getColumns().get(0).setVisible(true);
    }
}

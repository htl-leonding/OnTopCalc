package at.plakolb.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author Kepplinger
 */
public class AssemblingController implements Initializable {

    @FXML
    private TabPane tb_AssemblingPane;
    private static AssemblingController instance;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
    }

    public static AssemblingController getInstance() {
        return instance;
    }

    /**
     * Returns the TabPane for the ProjectViewController
     *
     * @return
     */
    public TabPane getTb_AssemblingPane() {
        return tb_AssemblingPane;
    }

    public void persist() {
        Assembling_FormworkController.getInstance().persist();
        Assembling_VisibleFormworkController.getInstance().persistVisibleFormwork();
        Assembling_FoilController.getInstance().persist();
        Assembling_SealingBandController.getInstance().persist();
        Assembling_SheetRoofController.getInstance().persist();
        Assembling_TiledRoofController.getInstance().persist();
        Assembling_CounterBattenController.getInstance().persist();
        Assembling_BattensOrFullFormworkController.getInstance().persist();
    }
}

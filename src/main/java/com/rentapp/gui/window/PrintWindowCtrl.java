package com.rentapp.gui.window;

import com.rentapp.db_object.Accessory;
import com.rentapp.db_object.Rent;
import com.rentapp.gui.scene.SceneCtrl;
import com.rentapp.table.ClientRow;
import com.rentapp.table.RentedRow;
import com.rentapp.util.DBQuery;
import com.rentapp.util.PDFExport;
import com.rentapp.util.Print;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;


public class PrintWindowCtrl implements Initializable {
    @FXML
    private Button cancel;
    @FXML
    private Button endButton;
    @FXML
    private Button printButton;
    @FXML
    private CheckBox contract;
    @FXML
    private CheckBox protocol;
    @FXML
    private VBox rootVBox;
    @FXML
    private Label title;

    private boolean protocolPrinted = false;
    private Function<Integer, Integer> invokeUpdate;
    private Rent rent;
    private RentedRow rentedRow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contract.setSelected(true);
        protocol.setSelected(true);
    }
    @FXML
    void closeWindow(ActionEvent event) {
        SceneCtrl.closeWindow(event);
    }

    @FXML
    void endAction(ActionEvent event) {
        if(protocolPrinted){
            invokeUpdate.apply(0);
            SceneCtrl.closeWindow(event);
        } else {
            Optional<ButtonType> res = SceneCtrl.showMessageWindow("Pytanie", "Nie wydrukowano dokumentu wypożyczenia.\nCzy na pewno chcesz zatwierdzić wypożyczenie?", true);
            if(res.isPresent() && res.get().getButtonData() == ButtonBar.ButtonData.YES){
                invokeUpdate.apply(0);
                SceneCtrl.closeWindow(event);
            }
        }
    }

    @FXML
    void printExportDocuments(ActionEvent event) {
        String [] clientInfo = new String[] {
                rent.getClient().getCompany(),
                rent.getClient().getName(),
                rent.getClient().getAdress(),
                rent.getClient().getPeselNip(),
                rent.getClient().getIdentityCard(),
                rent.getClient().getPhoneNumber(),
        };

        String [] rentInfo = rent.getRentInfoPDF();

        Stage owner = (Stage)((Node)event.getSource()).getScene().getWindow();

        if(contract.isSelected() && protocol.isSelected()){
            if(!PDFExport.exportContract(
                    rent.getClient().getContractNumber(),
                    rent.getFromWhenPretty(),
                    clientInfo
            )){
                return;
            }
            if(!PDFExport.exportRentDocument(
                    rent.getClientRentIDPretty(),
                    rent.getClient().getContractNumber(),
                    clientInfo,
                    rentInfo,
                    rent.getNoteList(),
                    rent.getFromWhenPretty()
            )){
                return;
            }
            String path = PDFExport.RENT_DOCS_DIR + rent.getClient().getName() + '_' + rent.getClientRentIDPretty().replace('/', '_') + ".pdf";
            if(Print.printDocument("Protokół", path, 2, owner)){
                protocolPrinted = true;
            }
            path = PDFExport.CONTRACTS_DIR + rent.getClient().getName() + "_" + rent.getClient().getContractNumber().replace('/', '_') + ".pdf";
            Print.printDocument("Umowa", path, 2, owner);


        } else if (contract.isSelected() && !protocol.isSelected()) {
            if(!PDFExport.exportContract(
                    rent.getClient().getContractNumber(),
                    rent.getFromWhenPretty(),
                    clientInfo
            )){
                return;
            }
            String path = PDFExport.CONTRACTS_DIR + rent.getClient().getName() + "_" + rent.getClient().getContractNumber().replace('/', '_') + ".pdf";
            Print.printDocument("Umowa", path, 2, owner);
        } else if(protocol.isSelected() && !contract.isSelected()){
            if(!PDFExport.exportRentDocument(
                    rent.getClientRentIDPretty(),
                    rent.getClient().getContractNumber(),
                    clientInfo,
                    rentInfo,
                    rent.getNoteList(),
                    rent.getFromWhenPretty()
            )){
                return;
            }
            String path = PDFExport.RENT_DOCS_DIR + rent.getClient().getName() + '_' + rent.getClientRentIDPretty().replace('/', '_') + ".pdf";
            if(Print.printDocument("Protokół", path, 2, owner)){
                protocolPrinted = true;
            }
        }
    }

//    public void printDocuments(ActionEvent event){
//        int clientID = Integer.parseInt(rentedRow.getClientID());
//        int clientRentID = rentedRow.getClientRentalID();
//        int clientRentIDYear = rentedRow.getClientRentalIDYear();
//        String clientName = rentedRow.getClient();
//        if(contract.isSelected() && protocol.isSelected()){
//            String clientContractNumber = DBQuery.queryClientContractNumber(clientID);
//            String contractPath = PDFExport.CONTRACTS_DIR + clientName + "_" + clientContractNumber.replace('/', '_') + ".pdf";
//            if(Print.printDocument("Umowa", contractPath, 2, SceneCtrl.stage)){
//                String rentPath = PDFExport.RENT_DOCS_DIR + clientName + "_" + clientContractNumber.replace('/', '_') + ".pdf";
//                Print.printDocument("Protokół", rentPath, 2, SceneCtrl.stage);
//            }
//        } else if(contract.isSelected()){
//            String clientContractNumber = DBQuery.queryClientContractNumber(clientID);
//            String contractPath = PDFExport.CONTRACTS_DIR + clientName + "_" + clientContractNumber.replace('/', '_') + ".pdf";
//            Print.printDocument("Umowa", contractPath, 2, SceneCtrl.stage);
//        } else if(protocol.isSelected()){
//            String rentPath = PDFExport.RENT_DOCS_DIR + clientName + "_" + clientRentID + "_" + clientRentIDYear + ".pdf";
//            Print.printDocument("Protokół", rentPath, 2, SceneCtrl.stage);
//        }
//    }

    public void setUpdate(Function<Integer, Integer> invokeUpdate){
        this.invokeUpdate = invokeUpdate;
    }

    public void setRent(RentedRow rentedRow){
        rent = new Rent(Integer.parseInt(rentedRow.getRentalID()));
        ClientRow client = DBQuery.queryClientData(rentedRow.getClientID());
        if(client == null) {
            SceneCtrl.showMessageWindow("Błąd", "Nie udało się pobrać danych klienta");
            return;
        }
        rent.setClient(client);
        rent.setiDEquip(DBQuery.queryRentedEquip(rentedRow.getRentalID()));
        Map<Integer, Accessory> iDAccessory = DBQuery.queryRentedAccessory(rentedRow.getRentalID());
        Map<Integer, Integer> accessoryIDQty = DBQuery.queryRentedAccessoryQty(rentedRow.getRentalID());
        rent.addAccessory(iDAccessory, accessoryIDQty);
        rent.setFromWhen(rentedRow.getFromWhen());
        rent.setProbableToWhen(rentedRow.getProbableToWhen());
        rent.setLongTerm(rentedRow.isLongTerm());
        System.out.println("longTerm: " + rentedRow.isLongTerm());
        rent.setDepositPayment(rentedRow.getDepositPayment());
        rent.setDepositGross(Integer.parseInt(rentedRow.getDepositGross().replaceAll("[^\\d.]", "")));
        rent.setTotPerDayGross(Double.parseDouble(rentedRow.getPriceGross().replaceAll("[^\\d.]", "")));
        rent.setNote(rentedRow.getNotes());
        rent.setPlaceOfWork(DBQuery.queryRentedEquipPlaceOfWork(rentedRow.getRentalID()));
        rent.setClientRentID(rentedRow.getClientRentalID(), rentedRow.getClientRentalIDYear() + "");

        endButton.setDisable(true);
        endButton.setVisible(false);
        cancel.setVisible(false);
    }

    public void setSelection(boolean contract, boolean protocol){
        this.contract.setSelected(contract);
        this.protocol.setSelected(protocol);
    }
    public void setRent(Rent rent){
        this.rent = rent;
    }

    public VBox getRootVBox() {
        return rootVBox;
    }


}

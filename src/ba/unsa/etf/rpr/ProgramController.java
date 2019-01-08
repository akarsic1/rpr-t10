package ba.unsa.etf.rpr;

import com.sun.jdi.connect.spi.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.text.TableView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class ProgramController implements Initializable {
    public BorderPane mainPane;
    public TableView tabela;
    public TableView tabela1;
    private GeografijaDAO baza ;
    private GradoviReport report;

   /* public void doSave(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            int brojac = 0;
            Element rootElement = doc.createElement("biblioteka");
            doc.appendChild(rootElement);
            for (Grad iterator : grad){
                brojac++;
                Element grad = doc.createElement("knjiga");
                rootElement.appendChild(grad);
                Attr atribut = doc.createAttribute("brojStranica");
                atribut.setValue(Integer.toString(iterator.getBrojStanovnika()));
                grad.setAttributeNode(atribut);
            }
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            System.out.println(result);
            StreamResult newResult = new StreamResult(System.out);
            transformer.transform(source, newResult);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }*/


    @FXML
    public void otvori(ActionEvent actionEvent){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF File", "*.pdf"),
                new FileChooser.ExtensionFilter("DOCX File", "*.docx"),
                new FileChooser.ExtensionFilter("XSLX Files", "*.xslx")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);

    }

    @FXML
    public void snimi(ActionEvent actionEvent){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF File", "*.pdf"),
                new FileChooser.ExtensionFilter("DOCX File", "*.docx"),
                new FileChooser.ExtensionFilter("XSLX Files", "*.xslx")
        );
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile == null) {
            System.out.println("Ne postoji");
            return;
        }

    }

    public void stampaj(ActionEvent actionEvent) {
        try {
            GradoviReport report = new GradoviReport();
            report.showReport(GeografijaDAO.getConnection());
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void noviIzvjestaj(ActionEvent actionEvent) {
        try {
            DrzavaReport report = new DrzavaReport();
            report.showReport(GeografijaDAO.getConnection());
        } catch (JRException e) {
            e.printStackTrace();
        }
    }


    public ProgramController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void selectLanguage(Locale locale) {
        Stage primaryStage = (Stage)mainPane.getScene().getWindow();
        Locale.setDefault(locale);
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("program.fxml"), bundle);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        primaryStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(480);
    }


    public void setBa(ActionEvent actionEvent) {
        selectLanguage(new Locale("bs","BA"));
    }

    public void setEn(ActionEvent actionEvent) {
        selectLanguage(new Locale("en","US"));
    }

    public void setDe(ActionEvent actionEvent) {
        selectLanguage(new Locale("de","DE"));
    }

    public void setFr(ActionEvent actionEvent) {
        selectLanguage(new Locale("fr","FR"));
    }
}
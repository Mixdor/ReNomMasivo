package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    private static String rutaSeleccionada = "";
    static ArrayList<String> ArrayListCarga = new ArrayList<>();
    static ArrayList<String> ArrayListRespaldo = new ArrayList<>();
    static ArrayList<String> ArrayListArchivos = new ArrayList<>();

    //@FXML private Window idFrame;
    @FXML private Button btnCargar;
    @FXML private Button btnCarpeta;
    @FXML private Button btnAplicar;
    @FXML private Button btnCtrlZ;
    @FXML private TextField tfSerie;
    @FXML private TextField tfTemporada;
    @FXML private TextField tfCapitulos;
    @FXML private TextField tfEmpiezaEn;
    @FXML private TextField tfDirectorio;
    @FXML private TextArea textAreaLista;
    @FXML private Label lbEjemplo;

    @FXML public void temporada() {
        File carpeta = new File(tfDirectorio.getText());
        File[] archivos = carpeta.listFiles();
        btnAplicar.setDisable(tfSerie.getText().equals("") || tfTemporada.getText().equals("") || tfCapitulos.getText().equals("") || tfEmpiezaEn.getText().equals("") || archivos.length != Integer.parseInt(tfCapitulos.getText()) || Integer.parseInt(tfCapitulos.getText()) != ArrayListCarga.size());

        if (!tfSerie.getText().equals("")&&!tfTemporada.getText().equals("")&&!tfEmpiezaEn.getText().equals("")){
            editarEjemplo(tfSerie,tfTemporada,tfEmpiezaEn,lbEjemplo);
        }
        else{
            lbEjemplo.setText("");
        }
    }

    @FXML public void capitulos() {
        btnCarpeta.setDisable(false);
        btnCargar.setDisable(true);
        if(!Objects.equals(rutaSeleccionada, "")) {
            if(!tfTemporada.getText().equals("")) {
                File carpeta = new File(tfDirectorio.getText());
                File[] archivos = carpeta.listFiles();
                if(String.valueOf(Objects.requireNonNull(archivos).length).equals(tfCapitulos.getText())) {
                    btnCargar.setDisable(false);
                    btnAplicar.setDisable(ArrayListCarga.isEmpty());
                }
                else {
                    btnAplicar.setDisable(true);
                }
            }
        }
    }

    @FXML public void iniciarEn(){
        File carpeta = new File(tfDirectorio.getText());
        File[] archivos = carpeta.listFiles();
        btnAplicar.setDisable(tfSerie.getText().equals("") || tfTemporada.getText().equals("") || tfCapitulos.getText().equals("") || tfEmpiezaEn.getText().equals("") || archivos.length != Integer.parseInt(tfCapitulos.getText()) || Integer.parseInt(tfCapitulos.getText()) != ArrayListCarga.size());
        if (!tfSerie.getText().equals("")&&!tfTemporada.getText().equals("")&&!tfEmpiezaEn.getText().equals("")){
            editarEjemplo(tfSerie,tfTemporada,tfEmpiezaEn,lbEjemplo);
        }
        else{
            lbEjemplo.setText("");
        }

    }

    @FXML public void serie(){
        File carpeta = new File(tfDirectorio.getText());
        File[] archivos = carpeta.listFiles();
        btnAplicar.setDisable(tfSerie.getText().equals("") || tfTemporada.getText().equals("") || tfCapitulos.getText().equals("") || tfEmpiezaEn.getText().equals("") || archivos.length != Integer.parseInt(tfCapitulos.getText()) || Integer.parseInt(tfCapitulos.getText()) != ArrayListCarga.size());
        if (!tfSerie.getText().equals("")&&!tfTemporada.getText().equals("")&&!tfEmpiezaEn.getText().equals("")){
            editarEjemplo(tfSerie,tfTemporada,tfEmpiezaEn,lbEjemplo);
        }
        else{
            lbEjemplo.setText("");
        }

    }

    @FXML public void carpeta() {
        try {
            tfDirectorio.setText(ObtenerDirectorio(btnCarpeta.getScene().getWindow()));
            btnCtrlZ.setDisable(true);
            btnAplicar.setDisable(true);
            ArrayListRespaldo.clear();
            File carpeta = new File(tfDirectorio.getText());
            File[] archivos = carpeta.listFiles();
            ListarArchivos(archivos,textAreaLista, btnCarpeta.getScene().getWindow());
            btnCargar.setDisable(true);
            try {
                if(tfTemporada.getText().equals("")) {
                    btnAplicar.setDisable(true);
                }
                else {
                    if(String.valueOf(Objects.requireNonNull(archivos).length).equals(tfCapitulos.getText())&& !tfTemporada.getText().equals("")) {
                        btnCargar.setDisable(false);
                    }
                }
            } catch(Exception e) {
                showWarning("Error","El campo de Capitulos no ha resivido el tipo de dato esperado", btnCarpeta.getScene().getWindow());
            }
        } catch(Exception e) {
            showInformation("Atención","No ha selecionado ninguna carpeta", btnCarpeta.getScene().getWindow());
        }

    }

    @FXML public void cargar() {
        String direccion = "";
        try{
            direccion = ObtenerArchivo(btnCargar.getScene().getWindow());
            if(!direccion.equals("")){
                ArrayListCarga.clear();
                File doc = new File(direccion);
                try {
                    BufferedReader obj = new BufferedReader(new FileReader(doc));
                    String strng;
                    while ((strng = obj.readLine()) != null){
                        ArrayListCarga.add(strng);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btnAplicar.setDisable(false);
                if (ArrayListCarga.toArray().length!=Integer.parseInt(tfCapitulos.getText())){
                    ArrayListCarga.clear();
                    btnAplicar.setDisable(true);
                    showWarning("Error","El numero de titulos del archivo\nNo coincide con el campo capitulos",btnCargar.getScene().getWindow());
                }
                else {
                    for(int i=0;i<ArrayListCarga.toArray().length;i++){
                        if(!VerificarCaracteres(ArrayListCarga.get(i))) {
                            showWarning("Alerta", "Archivo con caracteres ilegales:\n" +
                                    "\\  /  :  *  ?  \"  <  >  |",btnCargar.getScene().getWindow());
                            i=ArrayListCarga.toArray().length;
                            ArrayListCarga.clear();
                            btnAplicar.setDisable(true);
                        }
                    }
                }
            }
        } catch(Exception e) {
            showInformation("Atención","No ha selecionado ninguna archivo", btnCargar.getScene().getWindow());
        }
    }

    @FXML public void aplicar() {
        AplicarCambio(tfSerie, tfTemporada, tfCapitulos, tfEmpiezaEn, btnCtrlZ);

        File carpeta = new File(tfDirectorio.getText());
        File[] archivos = carpeta.listFiles();

        ListarArchivos(archivos, textAreaLista, btnAplicar.getScene().getWindow());

    }

    @FXML public void deshacer() {
        File carpetaOriginal = new File(tfDirectorio.getText());
        File[] archivosOriginal = carpetaOriginal.listFiles();
        DeshacerCambio(Objects.requireNonNull(archivosOriginal));
        File carpetaRestaurada = new File(tfDirectorio.getText());
        File[] archivosRestaurados = carpetaRestaurada.listFiles();
        ListarArchivos(archivosRestaurados, textAreaLista,btnCtrlZ.getScene().getWindow());
        btnCtrlZ.setDisable(true);
    }

    public void initialize() {    }

    public static void editarEjemplo(TextField tfSerie, TextField tfTemporada, TextField tfEmpiezaEn, Label lbEjemplo){
        String temp = "", emp = "";
        if (Integer.parseInt(tfTemporada.getText())<10){
            temp = "0"+tfTemporada.getText();
        }
        else {
            temp = tfTemporada.getText();
        }
        if (Integer.parseInt(tfEmpiezaEn.getText())<10){
            emp = "0"+tfEmpiezaEn.getText();
        }
        else {
            emp = tfEmpiezaEn.getText();
        }
        if(Integer.parseInt(tfTemporada.getText())==0){
            lbEjemplo.setText(tfSerie.getText()+""+emp+". Ejemplo de capitulo");
        }
        else{
            lbEjemplo.setText(tfSerie.getText()+""+temp+"x"+emp+". Ejemplo de capitulo");
        }
    }

    public static String ObtenerDirectorio(Window primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if(!Objects.equals(rutaSeleccionada, "")) {
            directoryChooser.setInitialDirectory(new File(rutaSeleccionada));
        }
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        rutaSeleccionada = selectedDirectory.getAbsolutePath();
        return rutaSeleccionada;
    }

    public static String ObtenerArchivo(Window primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona el archivo con los titulos");
        // Agregar filtros para facilitar la busqueda
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        // Obtener la imagen seleccionada
        File file = fileChooser.showOpenDialog(primaryStage);
        return file.getAbsolutePath();
    }

    public static void ListarArchivos(File [] archivos, TextArea textAreaLista, Window primaryStage) {

        ArrayList<String> ArrayListMostrarArchivos = new ArrayList<String>();
        ArrayListArchivos.clear();
        textAreaLista.setText("");
        if (archivos == null || archivos.length == 0) {
            showInformation("Atención", "No hay elementos dentro de la carpeta actual",primaryStage);
        }
        else {
            for (File archivo : archivos) {
                ArrayListArchivos.add(archivo.getName());
                ArrayListMostrarArchivos.add(String.format("%s %s",
                        archivo.isDirectory() ? "[Folder]" : "[   File  ]",
                        archivo.getName()
                ));
            }
            ArrayListArchivos.sort(naturalOrdering());
            ArrayListMostrarArchivos.sort(naturalOrdering());

            for (int i=0; i< archivos.length; i++) {

                textAreaLista.appendText(ArrayListMostrarArchivos.get(i));
                int b=archivos.length-1;
                if(i!=b) {
                    textAreaLista.appendText("\n");
                }
            }
        }
    }

    public static void AplicarCambio(TextField tfSeries, TextField tfTemp, TextField tfCap, TextField tfEmpiezaEn, Button btnDeshacer) {
        int j=Integer.parseInt(tfEmpiezaEn.getText());
        for(int i=0;i<Integer.parseInt(tfCap.getText());i++){

            File oldfile = new File(rutaSeleccionada+"\\"+ArrayListArchivos.get(i));
            String fileName = oldfile.getAbsolutePath();
            String ext = "";
            final Pattern PATTERN = Pattern.compile("(.*)\\.(.*)");
            Matcher m = PATTERN.matcher(fileName);
            if (m.find()) {
                ext = m.group(2);
            }

            if(Integer.parseInt(tfTemp.getText())<=0) {
                File newfile;
                if(j<10) {
                    newfile = new File(rutaSeleccionada + "\\" + tfSeries.getText() + "0" + j + ". " + ArrayListCarga.get(i) + "." + ext);
                }
                else {
                    newfile = new File(rutaSeleccionada + "\\" + tfSeries.getText() + j + ". " + ArrayListCarga.get(i) + "." + ext);
                }
                try {
                    Files.move(oldfile.toPath(),newfile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                if(j<10&&Integer.parseInt(tfTemp.getText())<10) {
                    File newfile = new File(rutaSeleccionada+"\\"+tfSeries.getText()+"0"+tfTemp.getText()+"x0"+j+". "+ArrayListCarga.get(i)+"."+ext);

                    System.out.println(oldfile);
                    System.out.println(newfile);
                    try {
                        Files.move(oldfile.toPath(),newfile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else if(j<10&&Integer.parseInt(tfTemp.getText())>=10) {
                    File newfile = new File(rutaSeleccionada+"\\"+tfSeries.getText()+tfTemp.getText()+"x0"+j+". "+ArrayListCarga.get(i)+"."+ext);
                    try {
                        Files.move(oldfile.toPath(),newfile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else if(j>=10&&Integer.parseInt(tfTemp.getText())<10) {
                    File newfile = new File(rutaSeleccionada+"\\"+tfSeries.getText()+"0"+tfTemp.getText()+"x"+j+". "+ArrayListCarga.get(i)+"."+ext);
                    try {
                        Files.move(oldfile.toPath(),newfile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    File newfile = new File(rutaSeleccionada+"\\"+tfSeries.getText()+tfTemp.getText()+"x"+j+". "+ArrayListCarga.get(i)+"."+ext);
                    try {
                        Files.move(oldfile.toPath(),newfile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            j++;
        }
        ArrayListRespaldo.clear();
        ArrayListRespaldo.addAll(ArrayListArchivos);
        btnDeshacer.setDisable(false);
    }

    public static boolean VerificarCaracteres(String palabra) {
        boolean verificador=true;
        char [] letras = palabra.toCharArray();
        for (char letra : letras) {
            if (letra == '\\' || letra == '/' || letra == ':' || letra == '*' || letra == '?' || letra == '"' || letra == '<' || letra == '>' || letra == '|') {
                verificador = false;
                break;
            }
        }
        return verificador;
    }

    public static Comparator<String> naturalOrdering() {
        final Pattern compile = Pattern.compile("(\\d+)|(\\D+)");
        return (s1, s2) -> {
            final Matcher matcher1 = compile.matcher(s1);
            final Matcher matcher2 = compile.matcher(s2);
            while (true) {
                final boolean found1 = matcher1.find();
                final boolean found2 = matcher2.find();
                if (!found1 || !found2) {
                    return Boolean.compare(found1, found2);
                } else if (!matcher1.group().equals(matcher2.group())) {
                    if (matcher1.group(1) == null || matcher2.group(1) == null) {
                        return matcher1.group().compareTo(matcher2.group());
                    } else {
                        return Integer.valueOf(matcher1.group(1)).compareTo(Integer.valueOf(matcher2.group(1)));
                    }
                }
            }
        };
    }

    public static void DeshacerCambio(File [] archivos) {

        for(int i=0;i<archivos.length;i++) {
            File oldfile = new File(rutaSeleccionada+"\\"+ArrayListArchivos.get(i));
            File newfile = new File(rutaSeleccionada+"\\"+ArrayListRespaldo.get(i));

            if (oldfile.renameTo(newfile)) {
                System.out.println("archivo restaurado");

            } else {
                System.out.println("error al restaurar");
            }
        }
        ArrayListRespaldo.clear();
    }

    public static void showInformation(String title, String message, Window primaryStage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    public static void showWarning(String title, String message, Window primaryStage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }

    public static String showTextInput(String title, String message, String defaultValue, Window primaryStage) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(title);
        dialog.setHeaderText("");
        dialog.setContentText(message);
        dialog.initOwner(primaryStage);
        dialog.setGraphic(null);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);

    }
}

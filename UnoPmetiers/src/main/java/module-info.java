module org.example.unopmetiers {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.unopmetiers to javafx.fxml;
    exports org.example.unopmetiers;
}
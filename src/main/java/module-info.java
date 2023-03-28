module com.example.networkchatserver1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.networkchatserver1 to javafx.fxml;
    exports com.example.networkchatserver1;
}
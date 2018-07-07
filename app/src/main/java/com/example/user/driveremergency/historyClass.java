package com.example.user.driveremergency;

public class historyClass {
    public String time;
    public String Source;
    public String Destination;
    public String FinalFare;
    public String DriverName;
    public String Vehicle_type;
    public String Vehicle_plate;

    public historyClass(String time, String Source, String Destination, String Final_Fare, String Driver_name, String Vehicle_type, String Vehicle_plate) {
        this.time = time;
        this.Source = Source;
        this.Destination = Destination;
        this.FinalFare = Final_Fare;
        this.DriverName = Driver_name;
        this.Vehicle_plate = Vehicle_plate;
        this.Vehicle_type = Vehicle_type;
    }
}

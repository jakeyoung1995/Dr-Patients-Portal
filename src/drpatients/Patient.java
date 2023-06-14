package drpatients;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "patient")
public class Patient {
    private int id;
    private String name;
    private String insuranceCardNumber;

    public Patient() {}

    public Patient(int id, String name, String insuranceCardNumber) {
        this.id = id;
        this.name = name;
        this.insuranceCardNumber = insuranceCardNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInsuranceCardNumber() {
        return insuranceCardNumber;
    }

    public void setInsuranceCardNumber(String insuranceCardNumber) {
        this.insuranceCardNumber = insuranceCardNumber;
    }

    @Override
    public String toString() {
        return name + " -- " + insuranceCardNumber;
    }
}
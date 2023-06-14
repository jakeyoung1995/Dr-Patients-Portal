package drpatients;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "doctor")
public class Doctor {
    private int id;
    private String name;
    private int numPatients;
    private List<Patient> patients;

    public Doctor() {}

    public Doctor(int id, String name, int numPatients) {
        this.id = id;
        this.name = name;
        this.numPatients = numPatients;
        this.patients = new ArrayList<>();
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

    public int getNumPatients() {
        return numPatients;
    }

    public void setNumPatients(int numPatients) {
        this.numPatients = numPatients;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" -- ").append(numPatients).append(": ");
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            sb.append(i).append(": ").append(p.getName()).append(" ==> ").append(p.getInsuranceCardNumber());
            if (i != patients.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
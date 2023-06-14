package drpatients;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "doctorList")
public class DoctorList {
    private List<Doctor> doctors;
    private AtomicInteger doctorId;
    private List<Patient> patients;
    private AtomicInteger patientId;

    public DoctorList() {
        doctors = new CopyOnWriteArrayList<Doctor>();
        doctorId = new AtomicInteger();
        patients = new CopyOnWriteArrayList<Patient>();
        patientId = new AtomicInteger();
    }

    @XmlElement
    @XmlElementWrapper(name = "doctors")
    public List<Doctor> getDoctors() {
        return this.doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    @XmlElement
    @XmlElementWrapper(name = "patients")
    public List<Patient> getPatients() {
        return this.patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Doctor d : doctors) {
            sb.append(d.toString()).append("\n");
        }
        return sb.toString();
    }

    public Doctor findDoctor(int id) {
        Doctor doctor = null;
        for (Doctor d : doctors) {
            if (d.getId() == id) {
                doctor = d;
                break;
            }
        }
        return doctor;
    }

    public Patient findPatient(int id) {
        Patient patient = null;
        for (Patient p : patients) {
            if (p.getId() == id) {
                patient = p;
                break;
            }
        }
        return patient;
    }

    public int addDoctor(String name, int numPatients, List<Patient> patients) {
        int id = doctorId.incrementAndGet();
        Doctor d = new Doctor(id, name, numPatients);
        doctors.add(d);
        for (int i = 0; i < numPatients; i++) {
            Patient patient = patients.get(i);
            int patientId = addPatient(patient.getName(), patient.getInsuranceCardNumber());
            d.addPatient(findPatient(patientId));
        }
    
        return id;
    }

    public int addPatient(String name, String insuranceCardNumber) {
        int id = patientId.incrementAndGet();
        Patient p = new Patient(id, name, insuranceCardNumber);
        patients.add(p);
        return id;
    }

    public void addPatientToDoctor(int doctorId, int patientId) {
        Doctor doctor = getDoctor(doctorId);
        Patient patient = getPatient(patientId);
        doctor.addPatient(patient);
    }

    public Doctor getDoctor(int id) {
        for (Doctor doctor : doctors) {
            if (doctor.getId() == id) {
                return doctor;
            }
        }
        return null; // return null if no doctor with given id is found
    }
    
    public Patient getPatient(int id) {
        for (Patient patient : patients) {
            if (patient.getId() == id) {
                return patient;
            }
        }
        return null; // return null if no patient with given id is found
    }

}

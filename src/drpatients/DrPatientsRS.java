package drpatients;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;



@Path("/")
public class DrPatientsRS {
    @Context 
    private ServletContext sctx;          // dependency injection
    private static DoctorList dlist; // set in populate()

	public DrPatientsRS() {}
	

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML}) 
    public Response getXml() {
	checkContext();
	return Response.ok(dlist, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML}) // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/xml");
    }

	@GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json")
    public Response getJson() {
	checkContext();
	return Response.ok(toJson(dlist), "application/json").build();
    }

    @GET    
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json/{id: \\d+}")
    public Response getJson(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/json");
    }

    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN}) 
    public String getPlain() {
	checkContext();
	return dlist.toString();
    }

	@GET 
	@Path("/plain/{id: \\d+}")
	@Produces({MediaType.TEXT_PLAIN})
	public Response getPlain(@PathParam("id") int id) {
		checkContext();
		Doctor doctor = dlist.getDoctor(id);
		if (doctor == null) {
			String msg = "There is no doctor with ID " + id + ".\n";
			return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
		} else {
			String doctorString = doctor.toString();
			return Response.ok(doctorString, MediaType.TEXT_PLAIN).build();
		}
	}
	

	@POST
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/create")
	public Response create(@FormParam("data") String data) {
		checkContext();
	
		String[] parts = data.split("!");
	
		String name = parts[0];
		int numPatients = Integer.parseInt(parts[1]);
	
		List<Patient> patients = new ArrayList<Patient>();
		for (int i = 2; i < parts.length; i += 2) {
			String patientName = parts[i];
			String insuranceCardNumber = parts[i + 1];
			Patient p = new Patient(i / 2, patientName, insuranceCardNumber);
			patients.add(p);
		}
	
		String msg = null;
		if (name == null || numPatients < 0 || patients == null || patients.size() != numPatients) {
			msg = "Invalid input. Required fields are missing or invalid.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		}
	
		for (int i = 0; i < numPatients; i++) {
			Patient patient = patients.get(i);
			patient.setId(i + 1);
		}
	
		int doctorId = dlist.addDoctor(name, numPatients, patients);
	
		msg = "Doctor " + name + " added with " + numPatients + " patients.\n";
		return Response.ok(msg, "text/plain").build();
	}
	
	
	@PUT
	@Produces({MediaType.TEXT_PLAIN})
	@Path("/update")
	public Response updateDoctor(@FormParam("id") int id,
								  @FormParam("name") String name) {
		checkContext();
	
		// Check if the doctor exists
		Doctor doctor = dlist.getDoctor(id);
		if (doctor == null) {
			String msg = "There is no doctor with ID " + id + ". Cannot update.\n";
			return Response.status(Response.Status.BAD_REQUEST).
					entity(msg).
					type(MediaType.TEXT_PLAIN).
					build();
		}
	
		// Check if the name is provided
		if (name == null) {
			String msg = "Invalid input. Doctor name is required to update.\n";
			return Response.status(Response.Status.BAD_REQUEST).
					entity(msg).
					type(MediaType.TEXT_PLAIN).
					build();
		}
	
		// Update doctor's name
		doctor.setName(name);
	
		String msg = "Doctor " + id + " has been updated with the new name: " + name + "\n";
		return Response.ok(msg, "text/plain").build();
	}

	@DELETE
	@Produces({MediaType.TEXT_PLAIN})
	@Path("/delete/{id: \\d+}")
	public Response deleteDoctor(@PathParam("id") int id) {
		checkContext();
		String msg = null;
		Doctor d = dlist.getDoctor(id);
		if (d == null) {
			msg = "There is no doctor with ID " + id + ". Cannot delete.\n";
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(msg)
					.type(MediaType.TEXT_PLAIN)
					.build();
		}
		dlist.getDoctors().remove(d);
		msg = "Doctor " + id + " and their associated patients have been deleted.\n";
		return Response.ok(msg, "text/plain").build();
	}

    //** utilities
    private void checkContext() {
	if (dlist == null) populate();
    }

	private void populate() {
		dlist = new DoctorList();
	
		String drFilename = "/WEB-INF/data/drs.db";
		InputStream drIn = sctx.getResourceAsStream(drFilename);
	
		// Read the data into the list of doctors.
		if (drIn != null) {
			try {
				BufferedReader drReader = new BufferedReader(new InputStreamReader(drIn));
				String drRecord = null;
				int numPatientsAdded = 0; // keep track of the total number of patients added
				while ((drRecord = drReader.readLine()) != null) {
					String[] drParts = drRecord.split("!");
					String drName = drParts[0];
					int numPatients = Integer.parseInt(drParts[1]);
	
					List<Patient> patients = new ArrayList<Patient>();
					String ptFilename = "/WEB-INF/data/patients.db";
					InputStream ptIn = sctx.getResourceAsStream(ptFilename);
	
					// Read the data into the list of patients.
					if (ptIn != null) {
						try {
							BufferedReader ptReader = new BufferedReader(new InputStreamReader(ptIn));
							String ptRecord = null;
							int patientCount = 0;
							while ((ptRecord = ptReader.readLine()) != null) {
								if (patientCount >= numPatientsAdded && patientCount < numPatientsAdded + numPatients) {
									String[] ptParts = ptRecord.split("!");
									String ptName = ptParts[0];
									String insuranceCardNumber = ptParts[1];
									Patient p = new Patient(patientCount + 1, ptName, insuranceCardNumber); // add patient ID here
									patients.add(p);
								}
								patientCount++;
							}
							numPatientsAdded += numPatients;
						} catch (Exception e) {
							throw new RuntimeException("I/O failed!");
						} finally {
							try {
								ptIn.close();
							} catch (Exception e) {
							}
						}
					}
	
					int drId = dlist.addDoctor(drName, numPatients, patients);
				}
			} catch (Exception e) {
				throw new RuntimeException("I/O failed!");
			} finally {
				try {
					drIn.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// Doctor --> JSON document
	private String toJson(Doctor doctor) {
		String json = "If you see this, there's a problem.";
		try {
			json = new ObjectMapper().writeValueAsString(doctor);
		}
		catch(Exception e) { }
		return json;
	}

	// DoctorList --> JSON document
	private String toJson(DoctorList dlist) {
		String json = "If you see this, there's a problem.";
		try {
			json = new ObjectMapper().writeValueAsString(dlist);
		}
		catch(Exception e) { }
		return json;
	}

	// Generate an HTTP error response or typed OK response.
	private Response toRequestedType(int id, String type) {
		Doctor doctor = dlist.findDoctor(id);
		if (doctor == null) {
			String msg = id + " is a bad ID.\n";
			return Response.status(Response.Status.BAD_REQUEST).
					entity(msg).
					type(MediaType.TEXT_PLAIN).
					build();
		}
		else if (type.contains("json"))
			return Response.ok(toJson(doctor), type).build();
		else
			return Response.ok(doctor, type).build(); // toXml is automatic
	}

	// Generate an HTTP error response or typed OK response.
	private Response toRequestedType(String type) {
		if (type.contains("json"))
			return Response.ok(toJson(dlist), type).build();
		else
			return Response.ok(dlist, type).build(); // toXml is automatic
	}
}
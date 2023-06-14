# Dr-Patients-Portal
DrPatients is a web service that manages a list of doctors and their associated patients. The web service is built using Java and uses the Jersey framework to handle RESTful requests.

## Resources:
The main resources for this web service are doctors and patients.
A doctor has the following attributes:
- ID (integer)
- Name (string)
- Number of Patients (integer)
- List of Patients (array of patient objects)
A patient has the following attributes:
- ID (integer)
- Name (string)
- Insurance Card Number (integer)

## Representations:
This web service supports three types of representations: XML, JSON, and Plain text.
Available Operations:
### 1.) Add a Doctor
- HTTP Method: POST
- URL: /drpatients/resourcesDr/create
- Request Body: name(string), numPatients(integer), patients(array of patient objects)
- Response Body: success message or error message
### 2.) Delete a Doctor
- HTTP Method: DELETE
- URL: /drpatients/resourcesDr/delete/{id}
- Path Parameter: id(integer)
- Response Body: success message or error message
### 3.) Update a Doctor Name
- HTTP Method: PUT
- URL: /drpatients/resourcesDr/update
- Request Body: id(integer), name(string)
- Response Body: success message or error message
### 4.) Get all Doctors
- HTTP Method: GET
- URL: /drpatients/resourcesDr/{representation}
- Path Parameter: representation (string) - “xml”, “json”, or “plain”
- Response Body: list of all doctors and their patients in the requested representation
### 5.) Get a Doctor by ID
- HTTP Method: GET
- URL: /drpatients/resourcesDr/{representation}/{id}
- Path Parameters: representation (string) - “xml”, “json”, or “plain”, id (integer)
- Response Body: the doctor with the requested ID in the requested representation

## WS API Used:
The DrPatients web service uses the Jersey framework to handle RESTful requests. It uses
annotations to define the available operations and their input and output types.

## Approach/Experience:
The approach taken for this web service was to first define the resources and their attributes,
and then to implement the CRUD operations for those resources. The Jersey framework was
chosen due to its ease of use and support for RESTful services. Additionally, Jersey has more
documentation available which makes it easier to find resources during troubleshooting and
implementation. The web service was thoroughly tested through implementing various
debugging code segments that helped identify issues within the code. Errors were also
identified through server logs.

## Main Troubleshooting Cases:
The most prevalent troubleshooting occurred when implementing an updated populate() method
which would correctly read from two separate database files to be parsed and used to create
our doctor and patient lists and objects. There were several different implementations used for
this method but ultimately it was decided to first initialize a new doctor list and create an input
stream to read from. Next, it reads each line using a BufferedReader and split into parts using
the ‘!’ delimiter to extract the doctor name and the number of associated patients. It then reads
the patient data and extracts the patient name and insurance card number, then creates a new
patient object and adds it to the list of patients. Once the patient data is read, it adds the doctor
to the DoctorList and continues to the next doctor.
Because of the relationships between doctors and patients, it presented challenges with REST
API endpoints that handle our HTTP requests. More specifically, the API that is used to add a
doctor and associated patients. Through several different implementations, including attempting
to convert to json and using json to input our data using our curl utility, it was decided that we
could change the method to take in a single parameter, data, which is a string that contains the
name of the doctor, the number of patients associated with the doctor, and the patient data for
each patient separated by “!”. The reason this implementation method was decided is because it
performs similarly to how the populate() method works which ultimately makes the web service
easier for the user

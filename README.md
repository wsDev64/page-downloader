# Getting Started

### Applications for:
* Determine file size without downloading.
* Determination of the size of web resources and its embedded files
The following guides illustrate how to use some features concretely:

### How to run 
#### If you have java and maven 
*  ./mvnw clean install && java -jar target/tech-task-0.0.1-SNAPSHOT.jar

#### Another way run using docker 
 * docker build -t tech-task:latest . 
 * docker run -d  -p 8080:8080 tech-task:latest
 
 ####Application available on port :8080
 
 ### Example of request and response 
 #### For web page
 * REQUEST: curl http://localhost:8080/page/size?url=https://www.google.com
 * RESPONSE: {"resourcesInfo":[{"size":14744,"Url":"https://www.google.com"},{"size":5885,"Url":"https://www.google.com/images/branding/googlelogo/1x/googlelogo_white_background_color_272x92dp.png"},{"size":672,"Url":"https://www.google.com/textinputassistant/tia.png"}],"totalSize":21301,"resource":"https://www.google.com"}
 #### For file 
 * REQUEST: curl http://localhost:8080/file/size?url=https://dzone.com/themes/dz20/images/DZLogo.png
 * RESPONSE: {"size":20414}
 
  
STEPS TO RUN THE SERVER:

Step 1) Install Java 21 if not available on your PC and set "Java_Home" in the environment variable.

Step 2) Install IntelliJ Idea IDE

Step 3) Extract the Authenticating.rar file and open the project in IntelliJ idea. After opening the Authenticating file, hit the run button.

Step 4) Now install the app that you want to Test (remember, since the backend is deployed on a server with public IP, your phone and pc in which the backend is running need to be on the same wifi network).

Step 5) In order to find the IPv4 address of the machine in which the server code is running.
	WINDOWS: open cmd, type ipconfig /all and hit enter. in the wifi section, you can find the IPv4 address.
	LINUX: open the terminal, type ifconfig and hit enter. in the wifi section, you can find the inet(ipv4) address.

Step 6) Now enter the same ipv4 address of the server in the app (again, this is only because backend code is not hosted in the server with public IP)

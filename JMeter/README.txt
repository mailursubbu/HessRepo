Download and unzip the the latest JMeter. In my machine its at "C:\Users\nm_su\Downloads\apache-jmeter-3.1\"
Update the windows PATH environment variable with JAVA_HOME/bin directory.In my machine it is "c:\Program Files\Java\jdk1.8.0_101\jre\bin".

Import the provisioning director public certificate into java default keystore, using below commands. If already done, pls ignore this step.
cd c:\Program Files\Java\jdk1.8.0_101\jre\lib\security
keytool -importcert -keystore cacerts -alias provCspireNetKs -file "C:\Users\nm_su\Projects\provisioning\JMeter\cspireNet.cer"

Launch the Jmeter using the above java home as below.
  cd to JMeter_installation_dir/bin 
  cd  C:\Users\nm_su\Downloads\apache-jmeter-3.1\bin
  java -jar ApacheJMeter.jar
  
  From GUI, open the script MobiProvLoadTest.jmx
  
  Update the "User defined variables" in the script, as per your environment.

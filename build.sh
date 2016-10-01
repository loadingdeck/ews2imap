cp="./ews-java-api-2.0-SNAPSHOT.jar:./javax.mail.jar:./"

cd mcpy
cd emailModel
javac -cp ../../ Header.java
javac -cp ../../ EmailAddress.java
javac -cp ../../ Email.java
cd ..
cd ..

javac -cp $cp ExchangeSource.java
javac -cp $cp ImapSink.java
javac -cp $cp Main.java

./gradlew -q fatJar
cp build/libs/alarm-all-1.0.jar .
docker build -t smarthome/alarm .
rm alarm-all-1.0.jar


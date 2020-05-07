./gradlew -q fatJar
cp build/libs/termometer-all-1.0.jar .
docker build -t smarthome/thermometer .
rm termometer-all-1.0.jar


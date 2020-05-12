./gradlew -q fatJar
cp build/libs/lights-all-1.0.jar .
docker build -t smarthome/lights .
rm lights-all-1.0.jar


./gradlew -q fatJar
cp build/libs/light_sensor-all-1.0.jar .
docker build -t smarthome/light_sensor .
rm light_sensor-all-1.0.jar


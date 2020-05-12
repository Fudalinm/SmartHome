./gradlew -q fatJar
cp build/libs/blinds-all-1.0.jar .
docker build -t smarthome/blinds .
rm blinds-all-1.0.jar


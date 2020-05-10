./gradlew -q fatJar
cp build/libs/controller-all-1.0.jar .
docker build -t smarthome/controller .
rm controller-all-1.0.jar


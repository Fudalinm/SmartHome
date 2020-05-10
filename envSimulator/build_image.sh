./gradlew -q fatJar
cp build/libs/envSimulator-all-1.0.jar .
docker build -t smarthome/env_simulator .
rm envSimulator-all-1.0.jar


# java-utilities

Gradle project. 

## Build process

./gradlew clean build shadowJar

#### smoke test
gradle run --args="--help"

#### execution
- copy deliverable and fat jar
java-utilities/lib/build/libs
mkdir -pv /mnt/c/workspace/TESTS/$(date +%F) && cp lib/build/libs/appJavaUtils-all.jar /mnt/c/workspace/TESTS/$(date +%F)

pushd /mnt/c/workspace/TESTS/$(date +%F)
java -jar appJavaUtils-all.jar --start "2026-03-10" --end "2026-03-14"
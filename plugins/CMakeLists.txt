cmake_minimum_required(VERSION 2.8.6)

find_package(Java)
include(UseJava)

project(FitNesse-P4 Java)

# download fitnesse server package
file(
    DOWNLOAD
    http://fitnesse.org/fitnesse-standalone.jar?responder=releaseDownload&release=20130530
    ${CMAKE_CURRENT_BINARY_DIR}/fitnesse-standalone.jar
    EXPECTED_MD5 c357d8717434947ed4dbbf8de51a8016
)

add_jar(PerforceCmSystem fitnesse/wiki/cmSystems/PerforceCmSystem.java)


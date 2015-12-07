README for competition
==========================

## Define and run slotcar competitions

### prerequisites
#### build
simply do mvn clean install

"competitions" requires the typical AngularJS/Spring Boot dev environment with NodeJS, Maven, Java 8

#### run
run like
java -jar thewarfile.war --spring.profiles.active=dev [additional settings]

runtime parameters can be found in src/main/resources/application-<xyz>.yml

requires a mysql database named "competition". Will be setup automatically via liquibase at first start

login with admin/admin and proceed to menu item "competition" to create a new competition

see the actual scoreboard on http://yourhost:yourport/scoreBoard.html (updated as events pour in)

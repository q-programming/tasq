TasQ - Project tracking application
=========================================
Appliation to track progress within project using either time estimation based or Agile style.
Designed and implemented from scratch using [Spring MVC 4 Quickstart Maven Archetype](https://github.com/q-programming/spring-mvc-quickstart-archetype.git)
Still lots of work left here but it's pretty functionalbe already

Live
----
Aplication is live and can be browsed under [This link](http://q-programming.pl/tasq/)

Use following credentials to login:
* login: demo@q-programming.pl
* pass: demoUser

Installation
------------
* Setup either local or remote PostgreSQL create 'tasq' DB and change parameters in 
	`/src/main/resources/persistence.properties`. Schema will be created on first app boot
* Update `/src/main/resources/email.properties` to point to your account
* Default app language is set as well in  `/src/main/resources/project.properties`.
* Default app directory is also set in `project.properties`
* Deploy on tomcat 
* Create directory on server to match app dir ( default is `/usr/local/tasq` ) 
* Make sure that tomcat7 is owner of this dir , execute ux command ( for tomcat7 it's ) : 
<br>`chwon -R tomcat7:tomcat7 /usr/local/tasq`
* Map avatar directory in tomcat config `/etc/tomcat7/server.xml` 
<br> `<Context docBase="/usr/local/tasq/avatar" path="/avatar" />` in 
`<Host name="localhost"  appBase="webapps"unpackWARs="true" autoDeploy="true">` section ( at the bottom of config )
* First registered user will be made application administrator

Language
--------
For now supported is polish(pl) and english(en), but can be easly added as whole app is created to support multiple locale
In order to add your language throw in new file in `src/main/webapp/WEB-INF/i18n/messages_XX.properties`
and adding select option to settings panel : `src\main\webapp\WEB-INF\views\user\settings.jsp`

	<option value="XX" <c:if test="${user.language eq 'XX'}">selected</c:if>>
	     <s:message code="lang.XX" text="XXXXXX" />
	</option>

Also recomended to add lang.xx code to other languages properties files . This will be changed later to facilitate whole process,

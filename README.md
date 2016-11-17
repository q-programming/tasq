Tasker - Project tracking application
=========================================
Appliation to track progress within project using either time estimation based or Agile style.
Designed and implemented from scratch using [Spring MVC 4 Quickstart Maven Archetype](https://github.com/q-programming/spring-mvc-quickstart-archetype.git)
It was developed from 18 March 2014 and finished around September 2015, still developed

Live
----
Aplication is live and can be browsed under [This link](http://q-programming.pl/tasq/)

Use following credentials to login:
* login: demo@q-programming.pl
* pass: demoUser

Installation
------------
* All application settings are stored in `/src/main/resources/application.properties`
* Setup either local or remote PostgreSQL create 'tasq' DB and update `application.properties` db parameters. Schema will be created on first app boot
* Update email settings in `application.properties` to point to your SMTP server. It's required to send e-mails with account registration.
If running app localy , FakeSMTP can be used.
* Set Default app language
* Set Default app directory
* All properties (including e-mail) can be changed later on by Administrator in "Manage application" view

* Build with maven using command `mvn package`
* Create directory on server to match app dir ( default is `/usr/local/tasq` )
* Make sure that tomcat7 is owner of this dir , execute ux command ( for tomcat7 it's ) : 
<br>`chwon -R tomcat7:tomcat7 /usr/local/tasq`
* Map avatar directory in tomcat config `/etc/tomcat7/server.xml` 
<br> `<Context docBase="/usr/local/tasq/avatar" path="/avatar" />` in 
`<Host name="localhost"  appBase="webapps"unpackWARs="true" autoDeploy="true">` section ( at the bottom of config )
* To use custom properties file , add VM arg `properties.location` for example :
`-Dproperties.location=/usr/local/tasq/application.properties`
* To launch tomcat with custom properties add property to JAVA_OPTS in catalina.sh
* Deploy on tomcat (copy to webapp dir)

* First registered user will be made application administrator, default theme, and logo will also be created. This step is crucial as if failed , there might be issues with showing logo etc.
* To show signin form right away for not logged user, change `skip.landing.page` property in `application.properties` to true. Otherwise landing page will be shown with basic application information
* Be sure to read help, especially Administrator section to know how to work with application 

Language
--------
For now supported is polish(pl) and english(en), but can be easly added as whole app is created to support multiple locale
In order to add your language throw in new file in `src/main/webapp/WEB-INF/i18n/messages_XX.properties`
and adding select option to settings panel : `src\main\webapp\WEB-INF\views\user\settings.jsp`

	<option value="XX" <c:if test="${user.language eq 'XX'}">selected</c:if>>
	     <s:message code="lang.XX" text="XXXXXX" />
	</option>

Also recommended to add lang.xx code to other languages properties files . This will be changed later to facilitate whole process,
Third party localisation plugins like trumbowyg (Richtext editor) will be required as well , please see https://alex-d.github.io/Trumbowyg/documentation.html#add-localization

Licence
--------
This application was created only be me , if you would like to change something , please notify me . I would love to see it :)
All application is under GNU GPL License and uses some components under Apache License
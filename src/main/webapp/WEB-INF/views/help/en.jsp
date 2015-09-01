<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="row white-frame">
	<div id="menu" class="col-md-2 col-sm-2 col-lg-2" role="complementary">
		<nav class="bs-docs-sidebar hidden-print hidden-xs hidden-sm affix">
			<ul class="nav bs-docs-sidenav">
				<li><a href="#about">About Tasker</a></li>
				<li><a href="#main">Main menu</a>
					<ul class="nav">
						<li><a href="#personal">Personal menu</a></li>
						<li><a href="#settings">User Settings menu</a></li>
						<li><a href="#events">Events</a></li>
						<li><a href="#watch">Watching</a></li>
					</ul></li>
				<li><a href="#users">Users</a></li>
				<li><a href="#proj"><s:message code="project.projects"
							text="Projects" /></a>
					<ul class="nav">
						<li><a href="#proj-view">Browsing all projects</a></li>
						<li><a href="#proj-details">View projects details</a></li>
						<li><a href="#proj-create">Creating projects</a></li>
						<li><a href="#proj-edit">Managing projects</a></li>
					</ul></li>
				<li><a href="#task"><s:message code="task.tasks"
							text="Tasks" /></a>
					<ul class="nav">
						<li><a href="#task-view">Browsing all tasks</a></li>
						<li><a href="#task-details">View task details</a></li>
						<li><a href="#task-create">Creating tasks</a></li>
						<li><a href="#task-import">Importing tasks</a></li>
						<li><a href="#task-export">Exporting tasks</a></li>
						<li><a href="#task-edit">Editing tasks</a></li>
						<li><a href="#task-work">Working with tasks</a></li>
					</ul></li>
				<li><a href="#agile"><s:message code="agile.agile"
							text="Agile" /></a>
					<ul class="nav">
						<li><a href="#scrum">SCRUM</a>
							<ul class="nav">
								<li><a href="#scrum-backlog">Backlog</a></li>
								<li><a href="#scrum-board">Board</a></li>
								<li><a href="#scrum-reports">Reports</a></li>
							</ul></li>
						<li><a href="#kanban">Kanban</a>
							<ul class="nav">
								<li><a href="#kanban-board">Board</a></li>
								<li><a href="#kanban-reports">Reports</a></li>
							</ul></li>
					</ul></li>

				<security:authorize access="hasRole('ROLE_ADMIN')">
					<li><a href="#admin">Administration</a>
						<ul class="nav">
							<li><a href="#a_users">Users</a></li>
							<li><a href="#a_projects">Projects</a>
							<li><a href="#a_task_remove">Removing Tasks</a>
							<li><a href="#a_roles">Roles</a></li>
						</ul></li>
				</security:authorize>
			</ul>
			<a href="#top" class="back-to-top"><s:message
					code="main.backtotop" /></a>
		</nav>
	</div>
	<div class="col-md-10 col-sm-10 col-lg-10" role="main">
		<a class="anchor" id="top"></a>
		<div>
			<a class="anchor" id="about"></a>
			<h3>About Tasker</h3>
			<p>
				"Tasker" is application to track progress within project using
				either time estimation or agile style.<br> It was designed and
				implemented from scratch using <a
					href="https://github.com/q-programming/spring-mvc-quickstart-archetype.git"><i
					class="fa fa-code-fork"></i> Spring MVC 4 Quickstart Maven
					Archetype</a> <br>While developing it whole code was stored on
				github and can be backtracked in <a class="more-link"
					href="https://github.com/q-programming/tasq" target="_blank"><i
					class="fa fa-github-alt"></i> GitHub page</a></br> To help build process it
				had it's own <a class="more-link"
					href="http://q-programming.pl/jenkins.html" target="_blank"
					style="margin: 0px 0px 0px -4px;"><img
					src="http://q-programming.pl/assets/images/jenkins.png"
					alt="Q-Programming" style="margin-bottom: 0px;"> Jenkins page</a>
				<br> <br> Application creation process was started on May
				18 2014 and over a year to have it in stable version
			</p>
			<%--------------------------------MAIN MENU ------------------------------------%>
			<hr>
			<a class="anchor" id="main"></a>
			<h2>Menu</h2>
			<p>Main Menu is located at top of every "Tasker" page and gives
				quick access to commonly used options</p>
			Menu items are :
			<ul>
				<li class="wider"><b><a href="#proj"><s:message
								code="project.projects" text="Projects" /></a></b> Gives access to
					projects which are created or available for user. First are
					displayed 5 last visited projects.<br> To show all , choose <b><a
						href="#proj-view"><i class="fa fa-list"></i> <s:message
								code="project.showAll" text="Projects" /></a></b><br> Nearly every
					user can create his own project from here using <b><a
						href="#proj-create"><i class="fa fa-plus"></i> <s:message
								code="project.create" text="Create project" /></a></b></li>
				<li class="wider"><b><a href="#tasks"><s:message
								code="task.tasks" text="Tasks" /> </a></b> - Gives access to tasks.
					Last 5 visited task are shown first, and all tasks can be shown
					using <i class="fa fa-list"></i> <s:message code="task.showAll"
						text="Show all" /> option</li>
				<li class="wider"><b><a href="#agile"> <s:message
								code="agile.agile" text="Agile" />
					</a></b> lists agile boards for last visited projects. All boards can be
					viewed using <i class="fa fa-list"></i> <s:message
						code="agile.showAll" text="Show all" /></li>
				<li class="wider"><a href="#create"><i class="fa fa-plus"></i>&nbsp;<i
						class="fa fa-lg fa-check-square"></i></a> Shows task creation screen</li>
				<li class="wider"><form id="searchForm"
						class="form-search form-inline">
						<input id="searchField" type="text" name="query"
							class="form-control search-query input-sm"
							placeholder="<s:message code="task.search"/>"
							style="border-radius: 10px; width: 150px;" /> - Search input
						box. Shows all task with inputed text within task name, its
						description or tag. Additionally when user starts to type keyword,
						all similar task tags are shown in drop-down list. After user
						confirms selection, all tasks matching this criteria are displayed
						on <b><a href="#task-view">Browse all tasks</a></b> view , with
						query as one of filters
					</form></li>
				<security:authorize access="hasRole('ROLE_ADMIN')">
					<li class="wider"><a href="#admin"
						class="btn btn-default btn-xs" type="button"><i
							class="fa fa-wrench"></i></a><span class="admin-button">Admin</span>
						- Drop-down menu to manage users or execute some additional task
						related actions</li>
				</security:authorize>
				<li class="wider"><a href="#users"
					class="btn btn-default btn-xs"><i class="fa fa-users"></i></a>
					Shows all users within application</li>
				<li class="wider"><i class="fa fa-question-circle"></i> Help
					(this page)</li>
				<li><a href="#personal"><img
						src="<c:url value="/resources/img/avatar.png" />"
						style="height: 50px; border: 1px solid"><span class="caret"></span></a>
					Personal menu under user's avatar</li>
			</ul>
			<%------------------------PERSONAL MENU---------------------------------- --%>
			<hr>
			<a class="anchor" id="personal"></a>
			<h3>Personal menu</h3>
			Under user's avatar there is Personal menu where user can basic
			options, notifications and watch screen.<br> If there are some
			unread notification, there will be number displayed next to user's
			avatar.<br> From this menu user can do following:
			<ul>
				<li><a href="#settings"><i class="fa fa-cog"></i> <s:message
							code="menu.settings" text="Settings" /></a> - Shows users settings
					like language, theme etc.</li>
				<li><a href="#events"> <i class="fa fa-bell-o"></i>&nbsp;<s:message
							code="events.events" />
				</a> - Shows all system events</li>
				<li><a href="#watch"> <i class="fa fa-eye"></i>&nbsp;<s:message
							code="events.watching" />
				</a> - Shows all currently watched tasks for logged user</li>
				<li><a href="/tasq/logout"><i class="fa fa-power-off"></i>
						<s:message code="menu.logout" text="Log out" /></a> - ends current
					user session and logouts</li>
			</ul>
			<%-------------------------MY SETTINGS -------------------------------- --%>
			<hr>
			<a class="anchor" id="settings"></a>
			<h3>
				<s:message code="panel.settings"></s:message>
			</h3>
			<p>Using this view, user can change some basic options regarding
				"Tasker" application.</p>
			<h4>Avatar</h4>
			<p>
				By default avatar is set to lovely hamster. However it's possible to
				overwrite the default avatar with custom one. When user uploads new
				image and saves profile this image will be stored in app home dir:
				<code>${projHome}/avatar</code>
			</p>

			<h4>
				<i class="fa fa-envelope-o"></i>
				<s:message code="panel.emails" />
			</h4>
			<p>
				Here email notification can be changed. When user sings up into
				"Tasker" for the first time, notifications are marked as true by
				defaults.<br> If somebody don't want to receive any
				notification this checkbox must be de-selected
			</p>
			<h4>
				<i class="fa fa-globe"></i>
				<s:message code="panel.language" />
			</h4>
			<p>Whole "Tasker" app is "dynamically localized" which means that
				whole application can be easily translated to any language by
				translating messages properties file. On launch date only English
				and Polish languages were released, but any new requested language
				can be easily added with minimal development effort</p>
			<h4>
				<i class="fa fa-paint-brush"></i>
				<s:message code="panel.theme"></s:message>
			</h4>
			<p>
				Here user can change theme for application. By default it's
				<s:message code="panel.theme.darkblue" />
				.<br> On launch date application comes with 3 other themes:
				<s:message code="panel.theme.lightblue" />
				,
				<s:message code="panel.theme.green" />
				,
				<s:message code="panel.theme.red" />
			</p>
			<h4>
				<i class="fa fa-users"></i>
				<s:message code="panel.invite" />
			</h4>
			<p>
				New users can be invited to use tasker, by using this form. Input
				valid e-mail address and click "<b><i class="fa fa-user-plus"></i>
					<s:message code="panel.invite" /></b> " to send predefined e-mail to
				inputed address
			</p>
			<hr>
			<%--------------------------------EVENTS ----------------------------%>
			<a class="anchor" id="events"></a>
			<h3>
				<s:message code="events.events" />
			</h3>
			<p>
				"Tasker" has notification system which will signal all important
				events within application. For example, If there will be some
				administration activity related to user's account. A system
				notification will be sent.<br> Each application user can <a
					href="#watch"><i class="fa fa-eye"></i>&nbsp; Watch project</a>, to
				track all events related to project he finds interesting. If there
				will be something new in project(comment, new file or task status
				update), new notification will be then added to account. After each
				page refresh, application checks if there is new notification and
				adds unread count next to user's avatar.<br>For example, If
				there is one unread notification <span class="badge theme"
					style="float: none;">1</span> will be shown.<br> In order to
				view all notification <b><i class="fa fa-bell"></i>&nbsp;<s:message
						code="events.events" />&nbsp;(1)</b> must be chosen from Personal
				Menu.<br> In this screen we can view unread ones ( bolded )
				mark all as read or delete all.
			</p>
			<hr>
			<%--------------------WATCH ----------------------------%>
			<a class="anchor" id="watch"></a>
			<h3>
				<s:message code="events.watching" />
			</h3>
			<p>
				It's possible to sign up for watch notifications about task. If it
				will be edited , some user will add new comment or state of it will
				be changed, all users who are watching this project will receive
				in-system notification.<br> Furthermore if user selected "Mail
				notification" in My settings, he will receive email notification
				about watched event<br> In order to sign up for project
				notification press watch button
				<button id="watch" class="btn btn-default btn-sm">
					<i id="watch_icon" class="fa fa-lg fa-eye"></i>
				</button>
				while being in task details screen.<br> All currently watched
				tasks are displayed in <b><i class="fa fa-eye"></i>&nbsp;<s:message
						code="events.watching" /></b> view which can be accessed through <a
					href="#personal">Personal Menu</a><br>To stop receiving
				notification about project, press stop watching
				<button id="watch" class="btn btn-default btn-sm">
					<i id="watch_icon" class="fa fa-lg fa-eye-slash"></i>
				</button>
				button located inside task details or in Watched screen inside
				Personal menu
			</p>
			<hr>
			<%---------------------------------USERS------------------------------------- --%>
			<h2>Users</h2>
			<a class="anchor" id="users"></a>
			<p>
				Each user that registered into application can be viewed in modal
				User window shown after clicking
				<button class="btn btn-default btn-xs">
					<i class="fa fa-user"></i>
				</button>
				button in main menu<br> Every user is shown in this view with
				his avatar.<br>Additionally if user is online ( logged into
				Tasker and performed action within 15 min) &nbsp;<i
					class="fa fa-user a-tooltip" style="color: mediumseagreen" title=""
					data-original-title="Online"></i> will be shown next to his name.<br>From
				this screen we can sent e-mail message by selecting <i
					class="fa fa-envelope" style="color: black;"></i> icon message as
				well.
			</p>
			<hr>
			<%--------------------------------PROJECTS ------------------------------------%>
			<a class="anchor" id="proj"></a>
			<h2>Projects</h2>
			<a class="anchor" id="proj-view"></a>
			<h3>Browsing all projects</h3>
			<p>
				Each user can view project to which he is assigned ( or is admin )
				To do so, please select <b><i class="fa fa-list"></i> <s:message
						code="project.showAll" text="Projects" /></b> from <b><s:message
						code="project.projects" text="Projects" /><span class="caret"></span></b>
				menu
			</p>
			<p>On this screen every project into which user is assigned is
				shown here. Basic information like name , description and who is
				main administrator of project is displayed.</p>
			<p>
				Project that are set as active are highlighted and <i
					class="fa fa-refresh fa-spin"></i> icon is shown in active button.
				When creating new task, active project is always taken first. To
				change active project, please click <span class="btn btn-default "><i
					class="fa fa-refresh"></i> </span> button in corresponding row
			</p>
			<hr>
			<%-------------------------DETAILS ------------------------------%>
			<a class="anchor" id="proj-details"></a>
			<h3>View projects details</h3>
			<p>
				In order to show project details , please either select it from <b><s:message
						code="project.projects" text="Projects" /><span class="caret"></span></b>
				menu , or click on Project ID , on task screen
			</p>
			<p>Project details screen all project information related to this
				project</p>
			<div>
				<img class="responsive"
					src="<c:url value="/resources/img/help/project_details.png"/>"
					usemap="#proj_details_map"></img>
				<map id="proj_details_map" name="proj_details_map">
					<area shape="rect" coords="6,14,177,33" title="Project Name" alt=""
						href="#proj-1" target="_self">
					<area shape="rect" coords="6,38,123,55" title="Project description"
						alt="" href="#proj-2" target="_self">
					<area shape="rect" coords="716,3,771,25"
						title="Activate / deactivate project " alt="" href="#proj-3"
						target="_self">
					<area shape="rect" coords="771,4,797,52" title="Project management"
						alt="" href="#proj-4" target="_self">
					<area shape="rect" coords="7,77,799,101"
						title="Open/Ongoing/Closed tasks " alt="" href="#proj-5"
						target="_self">
					<area shape="rect" coords="41,106,755,296"
						title="Open/Closed Chart" alt="" href="#proj-6" target="_self">
					<area shape="rect" coords="0,300,157,338"
						title="Latest events in project" alt="" href="#proj-7"
						target="_self">
					<area shape="rect" coords="196,349,268,385"
						title="Events list pagination" alt="" href="#proj-8"
						target="_self">
					<area shape="rect" coords="434,301,618,332" title="Tasks list"
						alt="" href="#proj-9" target="_self">
					<area shape="rect" coords="675,302,797,326"
						title="Hide closed tasks" alt="" href="#proj-10" target="_self">
					<area shape="rect" coords="676,327,800,350"
						title="Show all/hide all Subtasks" alt="" href="#proj-11"
						target="_self">
				</map>
				<p>
				<ol>
					<li><a class="anchor" id="proj-1"></a><b>Project Name</b></li>
					<li><a class="anchor" id="proj-2"></a><b>Project
							description</b></li>
					<li><a class="anchor" id="proj-3"></a><b>Activate/Deactivate
							project</b> - set displayed project as active</li>
					<li><a class="anchor" id="proj-4"></a><b>Project
							management</b> - set up project, add new users, set admins etc.</li>
					<li><a class="anchor" id="proj-5"></a><b>Open/Ongoing/Closed
							tasks</b> - Progress bar showing ratio of open/closed tasks within
						project</li>
					<li><a class="anchor" id="proj-6"></a><b>Open/Closed Chart</b>
						- shows how work was done in project. How many taks were created
						and closed</li>
					<li><a class="anchor" id="proj-7"></a><b>Latest events in
							project</b></li>
					<li><a class="anchor" id="proj-8"></a><b>Events list
							pagination</b></li>
					<li><a class="anchor" id="proj-9"></a><b>Tasks list</b></li>
					<li><a class="anchor" id="proj-10"></a><b>Hide closed
							tasks</b> - by default closed tasks are hidden in this view. In order
						to view all task deselect this checkbox</li>
					<li><a class="anchor" id="proj-11"></a><b>Show all/hide
							all Subtasks</b> - if one of tasks have subtasks <i
						class="fa fa-plus-square"></i> icon will be shown before it's
						name. After clicking this button all subtasks for every task will
						be expanded</li>

				</ol>
				</p>
			</div>
			<%----------------------------CREATE  ---------------------------------------%>
			<a class="anchor" id="proj-create"></a>
			<h3>Creating project</h3>
			<p>
				In order to create new project start by selecting <b><s:message
						code="project.projects" text="Projects" /> <span class="caret"></span></b>
				and choose <b><i class="fa fa-plus"></i> <s:message
						code="project.create" text="Create project" /></b> option from top
				menu bar.<br>This will show you "New project" creation screen
				on which all details about new project can be filled in
			</p>
			<table class="table">
				<tr>
					<td class="col-md-2"><b><s:message code="project.id" /></b></td>
					<td>Unique ID of project. This will be used when creating new
						task to generate its ids.ID should not contain any numbers and
						consist of maximum 5 letters. Chose wisely as it won't be possible
						to change project ID after it has been created</td>
				</tr>
				<tr>
					<td><b><s:message code="project.name" /></b></td>
					<td>Name of new project. It must not be empty. It can be
						changed at anytime</td>
				</tr>
				<tr>
					<td><b><s:message code="project.agile.type" /></b></td>
					<td>Choose agile type of newly created project
						<ul>
							<li><b>SCRUM</b> - is an iterative and incremental agile
								software development methodology for managing product
								development. It defines "a flexible, holistic product
								development strategy where a development team works as a unit to
								reach a common goal", challenges assumptions of the
								"traditional, sequential approach" to product development, and
								enables teams to self-organize by encouraging physical
								co-location or close online collaboration of all team members,
								as well as daily face-to-face communication among all team
								members and disciplines in the project. A key principle of scrum
								is its recognition that during production processes, the
								customers can change their minds about what they want and need
								(often called "requirements churn"), and that unpredicted
								challenges cannot be easily addressed in a traditional
								predictive or planned manner. As such, scrum adopts an empirical
								approach—accepting that the problem cannot be fully understood
								or defined, focusing instead on maximizing the team's ability to
								deliver quickly and respond to emerging requirements.<a
								class="superscript" target="_blank"
								href="https://en.wikipedia.org/wiki/Scrum_(software_development)">[source]</a></li>
							<li><b>Kanban</b> - is a method for managing knowledge work
								with an emphasis on just-in-time delivery while not overloading
								the team members. In this approach, the process, from definition
								of a task to its delivery to the customer, is displayed for
								participants to see. Team members pull work from a queue. Kanban
								in the context of software development can mean a visual
								process-management system that tells what to produce, when to
								produce it, and how much to produce - inspired by the Toyota
								Production System and by Lean manufacturing.<a
								class="superscript" target="_blank"
								href="https://en.wikipedia.org/wiki/Kanban_(development)">[source]</a></li>
						</ul>
					</td>
				</tr>

				<tr>
					<td><b><s:message code="project.description" /></b></td>
					<td>This section should contain description what the project
						will be about, what's its goal, and how the project will be
						created. If project will use some technologies or some new
						solutions it's recommended to describe them as well. When creating
						new project this field can't be empty</td>
				</tr>
			</table>
			<hr>
			<%--------------------------------EDIT ------------------------------------%>
			<a class="anchor" id="proj-edit"></a>
			<h3>Editing and managing project</h3>
			<p>
				Each project after it has been created can be edited. To manage your
				project click <span class="btn btn-default"><i
					class="fa fa-wrench"></i></span> on project details screen
			</p>
			<h3>Default project settings</h3>
			After project has been created you can edit it's description by
			clicking <i class="fa fa-pencil"></i> icon next to project name.<br>It's
			imposible to change name of project or it's ID once it has been
			created
			<h4>Task progress in project</h4>
			<p>Tasker has two option two track progress within project.</p>
			<ul>
				<li><b>Estimated time tracked</b> - Each task before "sprint"
					is started, should have estimated time set right away. Sprint is in
					quotes on purpouse, as if time tracked it's not completely SCRUM
					anymore. Regardles this option can still be used to track progress
					for team<br></li>
				<li><b>Story points</b> recommended for agile projects. Each
					task then is measured with points - which measures complexity of
					this task. Each team can freely translate how point is translated
					to work time. It's important to remember that 1SP is <b>not
						necessary</b> 1h!</li>
			</ul>
			<p>Please note that it's not recomended to switch how progress is
				tracked while project is ongoing or has already some sprint which
				were tracked. This can cause some errors and very odd charts in
				reports</p>
			<h4>Default task priority</h4>
			<p>Every new task created within this projcet will have this
				priority. Of course it can be freely changes by user</p>
			<h4>Default task type</h4>
			<p>New task created for this projcet will have this task type. It
				can be changed by user before creation is finished</p>
			<h4>Default task asignee</h4>
			<p>When new task will be created chosen user will be inputed into
				assignee field. If user would like to assign someone else , he can
				choose him/her from autocomplete field on task creation screen</p>
			<h3>People</h3>
			<h4>Admins</h4>
			<p>
				All project admins are listed in here. Project admin has access to
				this screen, plus additionally can perform any operation on tasks
				within this project to maintain order etc.<br> To add new
				project admin , user first has to be added to project as memmber.
				Next click
				<button class="btn btn-default btn-sm " title="">
					<i class="fa fa-plus"></i><i class="fa fa-wrench"></i>
				</button>
				in his row. If user is already admin and you would like to remove
				this priviliges from here , click button located in same place but
				with minus symbol :
				<button class="btn btn-default btn-sm " title="">
					<i class="fa fa-minus"></i><i class="fa fa-wrench"></i>
				</button>
			<h4>Project members</h4>
			<p>
				Every poroject memmber is listed in here ( with Role in
				parenthesis). To add new existing user into project click <span
					class="btn btn-default btn-sm" title=""><i
					class="fa fa-lg fa-user-plus"></i></span>. This will show field with
				autocompletion of all application registered users. Chosing one of
				names will automatically start process of adding user to project. To
				browse through all application users click <span
					class="btn btn-default btn-sm" title=""><i
					class="fa fa-lg fa-users"></i></span> button either next to add button on
				located in top menu next to avatar. <br> To remove user from
				project , choose
				<button class="btn btn-default btn-sm " title="">
					<i class="fa fa-user-times"></i>
				</button>
				button in users row
			<hr>
			<a class="anchor" id="task"></a>
			<h2>Tasks</h2>
			<a class="anchor" id="task-view"></a>
			<h3>Browsing all tasks</h3>
			<p>
				To browse all task select <b><i class="fa fa-list"></i> All
					tasks</b> option from <b>Tasks<span class="caret"></span></b> menu
			</p>
			<p>By default all open tasks from currently active project are
				shown. To show tasks from other project choose it from dropdown
				list( only your project are listed here )</p>
			<p>
				Displayed tasks can be filtered by priority or status.<br> In
				order to filter by priority select caret(<span class="caret"></span>)
				near Type text in table header<br> To filter by status , select
				it from drop down menu in Status<span class="caret"></span> also in
				table header. <br>Each aplied filter is shown next to Task
				Project combobox, and can be removed by clicking <i
					class="fa fa-times"
					style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
				symbol
			</p>
			<p>
				If Task has any subtasks <i class="fa fa-plus-square"></i> symbol
				will be shown next to it's ID. Subtasks can be expanded manually for
				each task with subtasks, or by clicking <b>Show all subtasks</b> <i
					class="fa fa-plus-square"></i> in top right corner, just below
				export button
			<p>
				From this view user can also <b><a href="#task-export">Export
						tasks</a></b> by clicking <a class="btn btn-default"> <i
					class="fa fa-upload"></i> Export
				</a> button
			<hr>
			<%-------------------------DETAILS ------------------------------%>
			<a class="anchor" id="task-details"></a>
			<h3>View task details</h3>
			<p>
				<img class="responsive"
					src="<c:url value="/resources/img/help/task_details.png"/>"
					usemap="#task_details_map"></img>

			</p>
			<hr>
			<%-------------------------CREATING------------------------------%>
			<a class="anchor" id="task-create"></a>
			<h3>Creating tasks</h3>
			<p>TODO</p>
			<hr>
			<%-------------------------EDIT ------------------------------%>
			<a class="anchor" id="task-edit"></a>
			<h3>Editing tasks</h3>
			<p>TODO</p>
			<hr>
			<%-------------------------WORKING ------------------------------%>
			<a class="anchor" id="task-work"></a>
			<h3>Working with tasks</h3>
			<p>TODO</p>
			<hr>
			<%--------------------------------AGILE ------------------------------------%>
			<a class="anchor" id="agile"></a>
			<h2>Agile</h2>
			<p>TODO</p>
			<hr>
			<%-----------------------------	KANBAN ---------------------------------%>
			<a class="anchor" id="scrum"></a>
			<h3>SCRUM</h3>
			<p>TODO</p>
			<a class="anchor" id="scrum-backlog"></a>
			<h4>Backlog</h4>
			<p>TODO</p>
			<a class="anchor" id="scrum-board"></a>
			<h4>Board</h4>
			<p>TODO</p>
			<a class="anchor" id="scrum-reports"></a>
			<h4>Reports</h4>
			<p>TODO</p>
			<hr>
			<%-----------------------------	KANBAN ---------------------------------%>
			<a class="anchor" id="kanban"></a>
			<h3>Kanban</h3>
			<p>TODO</p>
			<a class="anchor" id="kanban-board"></a>
			<h4>Board</h4>
			<p>TODO</p>
			<a class="anchor" id="kanban-reports"></a>
			<h4>Reports</h4>
			<p>TODO</p>
			<hr>
			<security:authorize access="hasRole('ROLE_ADMIN')">
				<%------------------------------ADMIN ---------------------------------------%>
				<a class="anchor" id="admin"></a>
				<h2>Administration</h2>
				<span class="admin-button">Admin</span>
				<p>
					Being administrator is not a hard work, presuming that it will be
					used by mature users :)<br> There are only couple activities
					that will be performed, especially at the beginning when users will
					start using application There are couple things that Tasker
					administrators can do within application. For full description
					check <a href="#a_roles">Roles</a> section
				</p>
				<p>
					Whenever user with role Administration is logged he will have extra
					option in main menu<br>(and in fact this help section will be
					shown)<br>
				</p>
				<p>
					<button class="btn btn-default btn-xs" type="button">
						<i class="fa fa-wrench"></i>
					</button>
					Option from drop down menu: <a class="anchor" id="a_users"></a>
				</p>
				<ul>
					<li><i class="fa fa-users"></i> <s:message
							code="menu.manage.users" /> - similar to regular Show users
						screen but with additional option to assign roles. Please be aware
						that it's not possible to remove last admin from application (
						there has to be at least one at any time)</li>
					<li><i class="fa fa-lg fa-check-square"></i> <s:message
							code="menu.manage.tasks" /> - Various task related action,
						mostly depreciated, used during development phase</li>
					<li>Other - option left for future usage</li>
				</ul>
				<a class="anchor" id="a_projects"></a>
				<hr>
				<h3>Projects</h3>
				<p>
					Having administration rights, enables <a href="#proj-edit">Edit
						Project</a> operation on all projects created in Tasker. <br>Here
					are some system information if manual operations will be required
				</p>
				<ul>
					<li>After project was created it's created in `project` db</li>
					<li>All task attachments are located in <code>${projHome}/ID/TASK_ID</code>
						where `ID` is unique project id and
					</li>
				</ul>
				<hr>
				<a class="anchor" id="task_remove"></a>
				<h3>Removing tasks</h3>
				<p>TODO</p>
				<hr>
				<a class="anchor" id="a_roles"></a>
				<h3>Roles</h3>
				<p>
					In order to not mess up application each user has application role
					assigned to keep things in order. <br> Following roles are
					available
				<table class="table">
					<tr>
						<td class="col-md-2"><b><s:message code="role.admin" /></b></td>
						<td>All below and
							<ul>
								<li>Edit any project</li>
								<li>Assign users to any project</li>
								<li>Add to project administrators</li>
								<li>Edit any tasks</li>
								<li>Change state and priority of any task in any project</li>
								<li>Delete task</li>
							</ul>
						</td>
					</tr>
					<tr>
						<td class="col-md-2"><b><s:message code="role.user" /></b></td>
						<td>Power user <br>Can do all below and
							<ul>
								<li>Edit project to which he is assigned</li>
								<li>Assign users to project (if is project admin)</li>
								<li>Add to project administrators (if is project admin)</li>
								<li>Edit tasks in his projects</li>
								<li>Link tasks in his project</li>
								<li>Start/stop sprint if is project admin</li>
								<li>Perform release if is project admin</li>
							</ul>
						</td>
					</tr>
					<tr>
						<td class="col-md-2"><b><s:message code="role.reporter" /></b></td>
						<td>Regular user <br>Can do all below and
							<ul>
								<li>Create tasks in project in which he is assigned</li>
								<li>Edit tasks he created</li>
								<li>Change state or priority of task he is assigned or
									created</li>
								<li>Comment on task which is not in Closed state</li>
							</ul>
						</td>
					</tr>
					<tr>
						<td class="col-md-2"><b><s:message code="role.viewer" /></b></td>
						<td>Demo user <br>
							<ul>
								<li>Login into application</li>
								<li>Change user settings, add avatar</li>
								<li>Watch tasks</li>
								<li>View all users in Tasker</li>
								<li>View tasks in project he is assigned to</li>
								<li>View Task activity log</li>
								<li>View agile boards, reports and backlog</li>
								<li>Invite new users to application</li>
							</ul>
						</td>
					</tr>


				</table>


			</security:authorize>
		</div>
	</div>
</div>
<script src="<c:url value="/resources/js/imageMapResizer.min.js" />"></script>
<script>
	$(document).ready(function() {
		$('map').imageMapResize();
		$('body').scrollspy({
			target : '#menu'
		})

	});

	
</script>
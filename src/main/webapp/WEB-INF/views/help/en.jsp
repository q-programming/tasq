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
						<li><a href="#task-subtask">Subtasks</a></li>
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
				<li class="wider"><b><a href="#task"><s:message
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
			<h4>Default project settings</h4>
			After project has been created you can edit it's description by
			clicking <i class="fa fa-pencil"></i> icon next to project name.<br>It's
			imposible to change name of project or it's ID once it has been
			created <b>Task progress in project</b>
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
			<b>Default task priority</b>
			<p>Every new task created within this projcet will have this
				priority. Of course it can be freely changes by user</p>
			<b>Default task type</b>
			<p>New task created for this projcet will have this task type. It
				can be changed by user before creation is finished</p>
			<b>Default task asignee</b>
			<p>When new task will be created chosen user will be inputed into
				assignee field. If user would like to assign someone else , he can
				choose him/her from autocomplete field on task creation screen</p>
			<h4>People</h4>
			<b>Admins</b>
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
				<b>Project members</b>
			<p>
				Every project member is listed in here ( with Role in parenthesis).
				To add new existing user into project click <span
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
				<map name="task_details_map">
					<area shape="rect" coords="7, 2, 275, 34" href="#task-1"
						title="Project ID/ [ Task ID ] Task name" />
					<area shape="rect" coords="6, 61, 187, 76" href="#task-2"
						title="Current task status" />
					<area shape="rect" coords="4, 76, 187, 94" href="#task-3"
						title="Priority" />
					<area shape="rect" coords="4, 95, 187, 112" href="#task-4"
						title="Task tags" />
					<area shape="rect" coords="4, 115, 371, 149" href="#task-5"
						title="Description of task" />
					<area shape="rect" coords="2, 150, 190, 178" href="#task-6"
						title="Story points ( if task is esitmated)" />
					<area shape="rect" coords="6, 183, 66, 216" href="#task-7"
						title="Log work button" />
					<area shape="rect" coords="70, 185, 143, 214" href="#task-8"
						title="Start timer" />
					<area shape="rect" coords="6, 215, 262, 259" href="#task-9"
						title="Time bars" />
					<area shape="rect" coords="8, 265, 561, 318" href="#task-10"
						title="Related tasks list" />
					<area shape="rect" coords="6, 324, 562, 422" href="#task-11"
						title="Subtask list" />
					<area shape="rect" coords="4, 457, 84, 481" href="#task-12"
						title="Activity log tab" />
					<area shape="rect" coords="88, 453, 176, 482" href="#task-13"
						title="Comments tab" />
					<area shape="rect" coords="714, 0, 740, 35" href="#task-14"
						title="Show edit menu button" />
					<area shape="rect" coords="745, 0, 769, 35" href="#task-15"
						title="Start/stop watching this task" />
					<area shape="rect" coords="774, 2, 797, 34" href="#task-16"
						title="Delete task (Admin only)" />
					<area shape="rect" coords="568, 43, 779, 100" href="#task-17"
						title="Task owner and current assignee" />
					<area shape="rect" coords="568, 107, 781, 174" href="#task-18"
						title="Creation, last update and due dates" />
					<area shape="rect" coords="568, 181, 786, 235" href="#task-19"
						title="Sprints" />
				</map>
			</p>
			<p>
			<ol>
				<li><a class="anchor" id="task-1"></a><b>Project ID/ [ Task
						ID ] Task name</b></li>
				<li><a class="anchor" id="task-2"></a><b>Current task
						status</b> - in order to change this status click <span class="caret"></span>
					and choose new one</li>
				<li><a class="anchor" id="task-3"></a><b>Task priority</b> - to
					change click <span class="caret"></span> and choose new</li>
				<li><a class="anchor" id="task-4"></a><b>Tags</b> - to add new
					tag , input it. If there is similar tag, autocomplete box will
					popup. To remove tag , click <i class="fa fa-times"
					style="font-size: smaller; margin-left: 3px; color: lightgray"></i>
					symbol<br> To view all tasks with this tag, click it. This
					will move you to <b><a href="#task-view">Browse all tasks</a></b>
					view with applied tag filter.</li>
				<li><a class="anchor" id="task-5"></a><b>Task description</b></li>
				<li><a class="anchor" id="task-6"></a><b>Story points</b> - if
					task is estimated, story points for it will be shown. If no points
					were set ,question mark will be shown instead. This value can be
					changed either from <b><a href="#task-edit">Edit menu</a></b>, or
					from this view quickly by hovering over <span class="badge theme">?</span>
					to reveal extra <i class="fa fa-pencil"></i> button .<br>
					Click it to show input field <span class="badge theme"> <input
						class="point-input" style="display: inline-block;"> <span
						style="cursor: pointer;"><i class="fa fa-check"
							style="vertical-align: text-top"></i></span> <span
						style="cursor: pointer;"><i class="fa fa-times"
							style="vertical-align: text-top"></i></span>
				</span> Confirm it by either enter key, or clicking check-mark sign. Cancel
					input by clicking x <br>You have to be either task owner or
					project admin to do so</li>
				<li><a class="anchor" id="task-7"></a><b>Log work button</b> -
					use it to log time spent on this task. More information in <b><a
						href="#task-work">Working with tasks</a></b> section</li>
				<li><a class="anchor" id="task-8"></a><b>Start timer button</b>
					- starts/stops active timmer on this task</li>
				<li><a class="anchor" id="task-9"></a><b>Time bars</b> - shows
					how much time was estimated, how much logged and remaining</li>
				<li><a class="anchor" id="task-10"></a><b>Related tasks</b> -
					Shows list of all task that are related/linked to it. In order to
					add new link , press <a class="btn btn-default btn-xxs" href="#"
					title=""> <i class="fa fa-plus"></i><i
						class="fa fa-lg fa-link fa-flip-horizontal"></i>
				</a> button ( this option is also avaible under edit menu)</li>
				<li><a class="anchor" id="task-11"></a><b>Subtasks list</b> -
					all subtasks are shown here with type, priority and progress. To
					create new subtask , click <a class="btn btn-default btn-xxs"
					href="#"> <i class="fa fa-plus"></i> <i
						class="fa fa-lg fa-sitemap"></i>
				</a> button ( option available from edit menu as well )</li>
				<li><a class="anchor" id="task-12"></a><b>Activity log tab</b>
					- click to show all activities related to this task</li>
				<li><a class="anchor" id="task-13"></a><b>Comments tab</b> -
					click to switch to comments tab. All comments related to this task
					are listed ( sorted from newest).<br> New comment can be added
					by clicking
					<button id="comments_add" class="btn btn-default btn-sm">
						<i class="fa fa-comment"></i>&nbsp; Add comment
					</button> button</li>
				<li><a class="anchor" id="task-14"></a><b>Show edit menu</b> -
					more information in <b><a href="#task-edit">Editing task</a></b>
					section</li>
				<li><a class="anchor" id="task-15"></a><b>Start/Stop
						watching task</b></li>
				<li><a class="anchor" id="task-16"></a><b>Delete task</b><span
					class="admin-button">Admin</span> - deletes this task ( Admin only)
					<security:authorize access="hasRole('ROLE_ADMIN')">
						More information in <a href="#a_task_remove">Removing tasks</a>
					</security:authorize></li>
				<li><a class="anchor" id="task-17"></a><b>Task owner and
						current assignee</b> - you can quickly assign someone to this task by
					clicking <span class="btn btn-default btn-sm "><i
						class="fa fa-lg fa-user-plus"></i> </span> button</li>
				<li><a class="anchor" id="task-18"></a><b>Creation, last
						update and due dates</b></li>
				<li><a class="anchor" id="task-19"></a><b>Sprints</b> - if task
					belongs or belonged to one of sprints, it will be listed here</li>
			</ol>
			<hr>
			<%-------------------------CREATING------------------------------%>
			<a class="anchor" id="task-create"></a>
			<h3>Creating tasks</h3>
			<p>
				There are two ways to create new task.<br>It's either directly
				from menu , clicking <a class="btn btn-xs" title=""
					style="padding: 5px 15px; border: 1px solid"> <i
					class="fa fa-plus"></i>&nbsp;<i class="fa fa-lg fa-check-square"></i></a>
				button , or by choosing <b><i class="fa fa-plus"></i> <s:message
						code="task.create" text="Create task" /></b> from under <b><s:message
						code="task.tasks" text="Tasks" /><span class="caret"></span></b> menu<br>
				This will show you "Create task" creation screen on which all
				details about new task can be filled in.
			</p>
			<table class="table">
				<tr>
					<td class="col-md-2"><b><s:message code="task.name"
								text="Summary" /></b></td>
					<td>Brief summary of what this task will be about</td>
				</tr>
				<tr>
					<td><b><s:message code="task.description"
								text="Description" /></b></td>
					<td>Description of task. All information required to finish it
						should be placed here. Basic styling is available through built-in
						rich text editor</td>
				</tr>
				<tr>
					<td><b><s:message code="project.project" /></b></td>
					<td>Project for which this task will be created.By default
						user's active project is always selected<br> After user
						select project , task type and priority will be automatically
						updated to default project value ( overriding already selected
						value)

					</td>
				</tr>
				<tr>
					<td><b><s:message code="task.assign" /></b></td>
					<td>User to whom this task will be assigned. Autocomplete of
						all project users is added in this field. If input box will be
						blank , task will be unassigned. Project default assignee is
						always entered when this screen is viewed. Default assignee can be
						changed on <b><a href="#proj-edit">Project management</a></b>
						screen
					</td>
				</tr>
				<tr>
					<td><a class="anchor" id="task-types"></a> <b><s:message
								code="task.type" /></b></td>
					<td>Type of task. This helps to filter task for whole team.

						Additionally types like "Task", "Bug" and "Iddle" have estimation
						disabled by default. Project default task type is always selected
						when this screen is viewed. This can be changed on <b><a
							href="#proj-edit">Project management</a></b> screen <br>There
						are following task types available:
						<ul>
							<li><t:type type="TASK" show_text="false" list="false" /> -
								a simple activity. Usually not too big, and most of the time
								without fixed estimation. It can also be used to group other
								task ( by linking as related )</li>
							<li><t:type type="USER_STORY" show_text="false" list="false" />
								- feature, case scenario or bigger thing to be done. In most
								cases before work has been started on it , it should be
								estimated</li>
							<li><t:type type="ISSUE" show_text="false" list="false" />
								- used mostly report that something is not right within project
								but not might not be bug at all</li>
							<li><t:type type="BUG" show_text="false" list="false" /> -
								if something is broken, task of this type can be created</li>
							<li><t:type type="IDLE" show_text="false" list="false" />-
								sometimes it can happen that team is not working, waiting for
								something to be unblocked, or just waiting idly for new tasks</li>
						</ul>

					</td>
				</tr>

				<tr>
					<td><b><s:message code="task.priority" /></b></td>
					<td>how important this task is</td>
				</tr>

				<tr>
					<td><b>Sprint</b></td>
					<td>If there is already existing sprint which is not yet
						finished , newly created task can be added right away after
						creation by selecting it from combobox. Please note that if sprint
						is active, its scope can change</td>
				</tr>

				<tr>
					<td><b><s:message code="task.estimate" /></b></td>
					<td>Time estimation of how much this task can take. Standard
						format should be used : *w *d *h *m (weeks, days, hours, minutes ;
						where * is any number )</td>
				</tr>
				<tr>
					<td><b><s:message code="task.storyPoints" /></b></td>
					<td>complexity measurement of newly created task. This value
						can be changed later on via Edit menu, from Detials screen , or
						from Backlog view
						<p></p>
						<p>
							<i class="fa fa-exclamation-circle"></i>&nbsp;If task is not
							estimated, have no points , or optional estimate time, please
							select <input type="checkbox" name="estimated" id="estimated"
								value="true"><b>Task without estimation</b> checkbox.<br>If
							selected this task can be added to sprint ( even active ) and not
							affecting sprint scope.
						</p>
					</td>
				</tr>
				<tr>
					<td><b><s:message code="task.dueDate" /></b></td>
					<td>If task should be finished before some date, enter this
						date here. Tasks from user's projects, with due date soon to
						present day are shown on home page</td>
				</tr>
				<tr>
					<td><b><s:message code="task.files" /></b></td>
					<td>Choose files that you want to add to this task. This can
						be some documents, excel sheets or image<br> In order to add
						more than one file, clicking <span id="addMoreFiles"
						class="btn btn-default btn-sm"> <i class="fa fa-plus"></i><i
							class="fa fa-file"></i>&nbsp;Add more files
					</span> button will add extra slot for it. Attached files are shown on
						Task detail screen. If file is an image, it will be shown as
						thumbnail
					</td>
				</tr>
			</table>

			<hr>
			<%-------------------------EDIT ------------------------------%>
			<a class="anchor" id="task-edit"></a>
			<h3>Editing tasks</h3>
			<p>
				If user has permission to edit task , extra button <span
					class="btn btn-default btn-sm a-tooltip"> <i
					class="fa fa-lg fa-pencil"></i>
				</span> will be shown on <a href="#task-details"> Task details view</a>
				(14).<br> Following actions are aviable:
			</p>
			<table class="table">
				<tr>
					<td class="col-md-2"><b><i class="fa fw fa-pencil"></i>&nbsp;Edit
							task</b></td>
					<td>allows user to edit already existing task. Each change
						will be logged and can be reviewed by task "Activity log".<br>
						<i class="fa fa-exclamation-circle"></i>&nbsp;Please note , that
						if task is in already started sprint, changing story points value
						will affect sprint scope
					</td>
				</tr>
				<tr>
					<td class="col-md-2"><b><i
							class="fa fw fa-link fa-flip-horizontal"></i>&nbsp;Link</b></td>
					<td>Starts linking procces. If two tasks have something in
						common, blocks or are duplicating. They can be linked . Every
						linked task is shown on task detials screen.<br> To link task
						, start by clicking this menu action, or <span
						class="btn btn-default btn-xxs " style="min-width: 37px;">
							<i class="fa fa-plus"></i><i
							class="fa fa-lg fa-link fa-flip-horizontal"></i>
					</span> button. Choose link relation, and then start typing in field ID or
						name of task that should be linked
					</td>
				</tr>
				<tr>
					<td class="col-md-2"><b><i class="fa fw fa-sitemap"></i>&nbsp;Add
							subtask</b></td>
					<td>shows create subtask screen which is nearly the same as
						regular task create screen , but without story points section</td>
				</tr>
				<tr>
					<td class="col-md-2"><b><i class="fa fw fa-file"></i>&nbsp;Attach
							file</b></td>
					<td>allows to add files ( documentaion , pictures , excels
						...). The same action is available via <span
						class="btn btn-default btn-xxs"> <i class="fa fa-plus"></i>
							<i class="fa fa-lg fa-file"></i>
					</span> button ( shown only if there is at least one file atached )
					</td>
				</tr>
			</table>
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
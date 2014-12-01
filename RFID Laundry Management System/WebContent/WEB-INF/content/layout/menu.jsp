<%@ page language="java" import="java.text.SimpleDateFormat" contentType="text/html; charset=utf-8"%>
<%@ include file="taglibs.jsp"%>

<script>
$(function() {
	ddsmoothmenu.init({
		mainmenuid : "smoothmenu1", 	//menu DIV id
		orientation : 'h', 				//Horizontal or vertical menu: Set to "h" or "v"
		classname : 'ddsmoothmenu', 	//class added to menu's outer DIV
		//customtheme: ["#1c5a80", "#18374a"],
		contentsource : "markup" 		//"markup" or ["container_id", "path_to_menu_file"]
	});
});
</script>

<div id="smoothmenu1" class="ddsmoothmenu">
	<ul>
		<s:if test="%{ hasUserGroupResourceAuthority('ProcessMgtMenu') == true }">
			<li><a href="#"><s:text name="menu.level1.process.mgt" /></a>
				<ul>
					<s:if test="%{ hasUserGroupResourceAuthority('/general/ironing-delivery-fixed-reader!getMainPage.action') == true }">
						<li><s:a namespace="/general" action="ironing-delivery-fixed-reader" method="getMainPage"><s:text name="menu.level2.iron.delivery" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/general/cloth-rack-handheld!getMainPage.action') == true }">
						<li><s:a namespace="/general" action="cloth-rack-handheld" method="getMainPage"><s:text name="menu.level2.cloth.rack.place" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/general/cloth-association-handheld!getMainPage.action') == true }">
						<li><s:a namespace="/general" action="cloth-association-handheld" method="getMainPage"><s:text name="menu.level2.cloth.association" /></s:a></li>
					</s:if>
	
					<s:if test="%{ hasUserGroupResourceAuthority('BackupProcedureSubMenu') == true }">
						<li><a href="#"><s:text name="menu.level2.backup.procedure" /></a>
							<ul>
								<s:if test="%{ hasUserGroupResourceAuthority('/general/cloth-collection-handheld!getMainPage.action') == true }">
									<li><s:a namespace="/general" action="cloth-collection-handheld" method="getMainPage"><s:text name="menu.level3.dirty.cloth.recv" /></s:a></li>
								</s:if>
								<s:if test="%{ hasUserGroupResourceAuthority('/general/ironing-delivery-handheld!getMainPage.action') == true }">
									<li><s:a namespace="/general" action="ironing-delivery-handheld" method="getMainPage"><s:text name="menu.level3.iron.delivery" /></s:a></li>
								</s:if>
							</ul>
						</li>
					</s:if>
				</ul>
			</li>
		</s:if>

		<s:if test="%{ hasUserGroupResourceAuthority('/general/receipt!getListPage.action') == true }">
			<li><s:a namespace="/general" action="receipt" method="getListPage"><s:text name="menu.level1.wash.receipt.mgt" /></s:a></li>
		</s:if>
		
		<s:if test="%{ hasUserGroupResourceAuthority('/general/inventory!getListPage.action') == true }">
			<li><a href="#"><s:text name="menu.level1.inv.mgt" /></a>
				<ul>
					<s:if test="%{ hasUserGroupResourceAuthority('/general/inventory!getListPage.action') == true }">
						<li><s:a namespace="/general" action="inventory" method="getListPage"><s:text name="menu.level2.cloth.summary" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/general/inventory!getListPageWithReadyStatus.action') == true }">
						<li><s:a namespace="/general" action="inventory" method="getListPageWithReadyStatus"><s:text name="menu.level2.cloth.distribute" /></s:a></li>
					</s:if>
				</ul>
			</li>
		</s:if>

		<s:if test="%{ hasUserGroupResourceAuthority('/general/special-event!getListPage.action') == true }">
			<li><s:a namespace="/general" action="special-event" method="getListPage"><s:text name="menu.level1.special.event.mgt" /></s:a></li>
		</s:if>

		<s:if test="%{ hasUserGroupResourceAuthority('StaffMgtMenu') == true }">
			<li><a href="#"><s:text name="menu.level1.staff.mgt" /></a>
				<ul>
					<s:if test="%{ hasUserGroupResourceAuthority('/master/staff!getNewPage.action') == true }">
						<li><s:a namespace="/master" action="staff" method="getNewPage"><s:text name="menu.level2.new.staff" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/master/staff!getListPage.action') == true }">
						<li><s:a namespace="/master" action="staff" method="getListPage"><s:text name="menu.level2.staff.summary" /></s:a></li>
					</s:if>
				</ul>
			</li>
		</s:if>

		<s:if test="%{ hasUserGroupResourceAuthority('ClothTypeMgtMenu') == true }">
			<li><a href="#"><s:text name="menu.level1.cloth.type.mgt" /></a>
				<ul>
					<s:if test="%{ hasUserGroupResourceAuthority('/master/cloth-type!getNewPage.action') == true }">
						<li><s:a namespace="/master" action="cloth-type" method="getNewPage"><s:text name="menu.level2.new.cloth.type" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/master/cloth-type!getListPage.action') == true }">
						<li><s:a namespace="/master" action="cloth-type" method="getListPage"><s:text name="menu.level2.cloth.type.summary" /></s:a></li>
					</s:if>
				</ul>
			</li>
		</s:if>

		<s:if test="%{ hasUserGroupResourceAuthority('RackMgtMenu') == true }">
			<li><a href="#"><s:text name="menu.level1.zone.mgt" /></a>
				<ul>
					<s:if test="%{ hasUserGroupResourceAuthority('/master/zone!getNewPage.action') == true }">
						<li><s:a namespace="/master" action="zone" method="getNewPage"><s:text name="menu.level2.new.rack" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/master/zone!getListPage.action') == true }">
						<li><s:a namespace="/master" action="zone" method="getListPage"><s:text name="menu.level2.rack.summary" /></s:a></li>
					</s:if>
				</ul>
			</li>
		</s:if>

		<s:if test="%{ hasUserGroupResourceAuthority('ReportMenu') == true }">
			<li><a href="#"><s:text name="menu.level1.report" /></a>
				<ul>
					<s:if test="%{ hasUserGroupResourceAuthority('/report/monthly-laundry-recv-report!getReportPage.action') == true }">
						<li><s:a namespace="/report" action="monthly-laundry-recv-report" method="getReportPage"><s:text name="menu.level2.monthly.laundry.recv.report" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/report/monthly-laundry-dist-report!getReportPage.action') == true }">
						<li><s:a namespace="/report" action="monthly-laundry-dist-report" method="getReportPage"><s:text name="menu.level2.monthly.laundry.dist.report" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/report/monthly-reported-missing-report!getReportPage.action') == true }">
						<li><s:a namespace="/report" action="monthly-reported-missing-report" method="getReportPage"><s:text name="menu.level2.monthly.reported.missing.report" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/report/daily-incident-report!getReportPage.action') == true }">
						<li><s:a namespace="/report" action="daily-incident-report" method="getReportPage"><s:text name="menu.level2.daily.inc.report" /></s:a></li>
					</s:if>
					<s:if test="%{ hasUserGroupResourceAuthority('/report/washing-count-report!getReportPage.action') == true }">
						<li><s:a namespace="/report" action="washing-count-report" method="getReportPage"><s:text name="menu.level2.washing.count.report" /></s:a></li>
					</s:if>
				</ul>
			</li>
		</s:if>
		
		<s:if test="%{ hasUserGroupResourceAuthority('/general/alert!getListPage.action') == true }">
			<li><s:a namespace="/general" action="alert" method="getListPage"><s:text name="menu.level1.alert.setting" /></s:a></li>
		</s:if>
		
		<s:if test="%{ hasUserGroupResourceAuthority('UserMgtMenu') == true }">
			<li><a href="#"><s:text name="menu.level1.user.mgt" /></a>
				<ul>
					<s:if test="%{ hasUserGroupResourceAuthority('UserAccountMgtSubMenu') == true }">
						<li><a href="#"><s:text name="menu.level2.user.mgt"/></a>
							<ul>
								<s:if test="%{ hasUserGroupResourceAuthority('/system/user-management!usersCreate.action') == true }">
									<li><s:a theme="simple" namespace="/system" action="user-management" method="usersCreate"><s:text name="menu.level3.new.user" /></s:a></li>
								</s:if>
								<s:if test="%{ hasUserGroupResourceAuthority('/system/user-management!usersList.action') == true }">
									<li><s:a theme="simple" namespace="/system" action="user-management" method="usersList"><s:text name="menu.level3.user.summary" /></s:a></li>
								</s:if>
							</ul>
						</li>
					</s:if>
					
					<s:if test="%{ hasUserGroupResourceAuthority('UserGroupMgtSubMenu') == true }">
						<li><a href="#"><s:text name="menu.level2.group.mgt"/></a>
							<ul>
								<s:if test="%{ hasUserGroupResourceAuthority('/system/user-group-management!groupsCreate.action') == true }">
									<li><s:a theme="simple" namespace="/system" action="user-group-management" method="groupsCreate"><s:text name="menu.level3.new.group" /></s:a></li>
								</s:if>
								<s:if test="%{ hasUserGroupResourceAuthority('/system/user-group-management!groupsList.action') == true }">
									<li><s:a theme="simple" namespace="/system" action="user-group-management" method="groupsList"><s:text name="menu.level3.group.summary" /></s:a></li>
								</s:if>
							</ul>
						</li>
					</s:if>
				</ul>
			</li>
		</s:if>
		
		<li><a href="#"><s:text name="menu.level1.personal.setting" /></a>
			<ul>
				<li>
					<s:a theme="simple" namespace="/system" action="personal-setting" method="getMainPage"><s:text name="menu.level2.account.setting" />
						<s:param name="users.username"><s:property value="user.username" /></s:param>
					</s:a>
				</li>
				<li><a href="#"><s:text name="menu.level2.language.setting" /></a>
					<ul>
						<s:iterator value="systemLanguageList" var="systemLanguage">
							<li>
								<s:url var="link" includeParams="get" encode="true">
									<s:param name="request_locale" value="#systemLanguage"/>
								</s:url>
								<s:a href="%{link}"><s:text name="%{#systemLanguage.systemResourceKey}"/></s:a>
							</li>
						</s:iterator>
						
<!-- Alternative Way Example -->
<!-- 						<li> -->
<%-- 							length: <s:property value="@web.actions.BaseAction$SystemLanguage@values().length"/><br/> --%>
<!-- 						</li> -->
						
<%-- 						<s:iterator value="@web.actions.BaseAction$SystemLanguage@values()" var="systemLanguage"> --%>
<!-- 							<li> -->
<%-- 								<s:url var="link" includeParams="get" encode="true"> --%>
<%-- 									<s:param name="request_locale" value="#systemLanguage"/> --%>
<%-- 								</s:url> --%>
<%-- 								<s:a href="%{link}"><s:text name="%{#systemLanguage.systemResourceKey}"/></s:a> --%>
<!-- 							</li> -->
<%-- 						</s:iterator> --%>
<!-- Alternative Way Example -->
					</ul>
				</li>
			</ul>
		</li>

		<li><a href="<s:url value="/j_spring_security_logout"/>"><s:text name="menu.level1.logout" /></a></li>
	</ul>
	<br style="clear: left" />
</div>

<hr />

<div class="loginInfo">
	<ul>
		<li><s:text name="menu.loginid" />: <s:property value="user.username" /></li>
		
	</ul>
<%-- 	<li><s:text name="label.date" />: <%= (new SimpleDateFormat("yyyy-MM-dd")).format(new java.util.Date()) %></li> --%>
<%-- 	<h3 class="fnName"><s:property value="methodTitle" /></h3> --%>
</div>
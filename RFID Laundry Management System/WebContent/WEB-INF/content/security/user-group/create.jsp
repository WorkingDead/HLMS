<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<%-- The URL use by going back to list define here --%>
<s:url var="backToListUrl" action="user-group-management" namespace="/system" method="groupsList"/>

<%-- The URL use by going back to create define here --%>
<s:url var="backToCreateUrl" action="user-group-management" namespace="/system" method="groupsCreate"/>

<script>

	$(function() {

		//When saving success, user can choose to go to where
		$("body").delegate("#groupsConfirmForm", "groupsConfirmForm.success", function(e, result, result2) {
			optionsAvailableMsgDialog("", result.actionMessages, 
					"<s:text name="btn.continue"/>",
					function(){
						$(location).attr('href',"<s:property value="#backToCreateUrl"/>");
					},
					"<s:text name="btn.ok"/>",
					function(){
						$(location).attr('href',"<s:property value="#backToListUrl"/>");
					}
				);
		});
		$("body").delegate("#groupsConfirmForm", "groupsConfirmForm.failure", function(e, result, result2) {
// 			alertDialog("", result.errors, "<s:text name="btn.ok"/>");
		});	
		//When saving success, user can choose to go to where
	})


</script>

<!-- Modal Window: Popup Box for editing Currency-->
<div id="popUpBox1" class="popUpBoxDiv hidden">
</div>

<h3 class="fnName"><s:text name="security.groups.page.title.new" /></h3>

<div class="body">
<s:form theme="simple" id="groupsConfirmForm" namespace="/system" action="user-group-management" method="post" cssClass="ajaxForm">

	<div class="actionErrors" ref="groupsConfirmForm"></div>
	<div class="actionMessages" ref="groupsConfirmForm"></div>

	<fieldset class="fieldsetStyle01" id="newGroupFieldset" name="newGroupFieldset">
		
		<legend>
			<s:text name="security.groups.page.title.new"></s:text>
		</legend>

		<ul>
			<li>
				<label for="groups.groupName" class="width180"><s:text name="security.groups.name"></s:text>: </label>
				<s:textfield theme="simple" name="groups.groupName" cssClass="inputText" />
			</li>

			<li>
				<label for="groups.enabledString" class="width180"><s:text name="security.groups.status"></s:text>: </label>
				<s:select
					theme="simple"
					name="groups.enabledString"
					list="enableDisableStatusList"
					listKey="value"
					listValue="getText(resKey)"
					emptyOption="false"
					cssClass="inputText" />
			</li>
			
			<li>
				<label class="width180"><s:text name="security.groups.privilege"></s:text>: </label>
				<div class="multipleCheckbox" id="accessRight">
					
						<ul>
							<s:set var="emptyString" value=""/>			<%-- For comparing empty string --%>

							<s:set var="resourceGroup" value=""/>		<%-- Initiate the group variable --%>
							<s:set var="resourceSubGroup" value=""/>	<%-- Initiate the sub group variable --%>
							
							<s:iterator value="resourceList" var="resource">
							
								<s:if test="#resourceGroup != #resource.resourceGroup.resKey">

									<%-- To detect whether it is not the first time to run group --%>
									<s:if test="#resourceGroup != #emptyString">
									
										<%-- To detect whether there is a sub group which need to be ended --%>
										<s:if test="#resourceSubGroup != #emptyString">
											</ul></li>									<%-- End the sub group --%>
											<s:set var="resourceSubGroup" value=""/>	<%-- Reset the sub group variable to indicate no sub group need to be ended --%>
										</s:if>
										<%-- To detect whether there is a sub group which need to be ended --%>
									
										</ul></li>
									</s:if>
									<%-- To detect whether it is not the first time to run group --%>

									<%-- What would be done for the group --%>
									<li>
										<s:if test="#resource.checked == true">
											<s:checkbox theme="simple" fieldValue="%{id}"
												cssClass="groupCheckall" groupRef="%{ getText(#resource.resourceGroup.resKey) }"
												name="checkedResourceList" value="true">
											</s:checkbox>
											<s:property value="%{ getText(name) }" />
										</s:if>
										<s:else>
											<s:checkbox theme="simple" fieldValue="%{id}"
												cssClass="groupCheckall" groupRef="%{ getText(#resource.resourceGroup.resKey) }"
												name="checkedResourceList">
											</s:checkbox>
											<s:property value="%{ getText(name) }" />
										</s:else>
									</li>
									<%-- What would be done for the group --%>

									<%-- To start a group area --%>
									<li><ul>
									<%-- To start a group area --%>
									
									<%-- Record the current group --%>
									<s:set var="resourceGroup" value="#resource.resourceGroup.resKey"/>
									<%-- Record the current group --%>
								</s:if>
								<s:else>
								
									<s:if test="#resourceSubGroup != #resource.resourceSubGroup.resKey">

										<%-- To detect whether it is not the first time to run sub group --%>
										<s:if test="#resourceSubGroup != #emptyString">
											</ul></li>									<%-- End the sub group --%>
										</s:if>
										<%-- To detect whether it is not the first time to run sub group --%>
										
										<%-- What would be done for the sub group --%>
										<li>
											<s:if test="#resource.checked == true">
												<s:checkbox theme="simple" fieldValue="%{id}"
													cssClass="subGroupCheckall groupMemberCheck" subGroupRef="%{ getText(#resource.resourceSubGroup.resKey) }" groupCheckallTarget="%{ getText(#resource.resourceGroup.resKey) }"
													name="checkedResourceList" value="true">
												</s:checkbox>
												<s:property value="%{ getText(name) }" />
											</s:if>
											<s:else>
												<s:checkbox theme="simple" fieldValue="%{id}"
													cssClass="subGroupCheckall groupMemberCheck" subGroupRef="%{ getText(#resource.resourceSubGroup.resKey) }" groupCheckallTarget="%{ getText(#resource.resourceGroup.resKey) }"
													name="checkedResourceList">
												</s:checkbox>
												<s:property value="%{ getText(name) }" />
											</s:else>	
										</li>
										<%-- What would be done for the sub group --%>
										
										<%-- To start a sub group area --%>
										<li><ul>
										<%-- To start a sub group area --%>

										<%-- Record the current sub group --%>
										<s:set var="resourceSubGroup" value="#resource.resourceSubGroup.resKey"/>
										<%-- Record the current sub group --%>
									</s:if>
									<s:else>

										<%-- To detect when the sub group has already ended but still in the same group --%>
										<s:if test="#resourceSubGroup != #emptyString">
											<s:if test="#resource.resourceSubGroup == null">
												</ul></li>									<%-- End the sub group --%>
												<s:set var="resourceSubGroup" value=""/>	<%-- Reset the sub group variable to indicate no sub group need to be ended --%>
											</s:if>
										</s:if>
										<%-- To detect when the sub group has already ended but still in the same group --%>

										<s:if test="#resourceSubGroup == #emptyString">		<%-- To detect whether it is group --%>
										
											<li>
												<s:if test="#resource.checked == true">		<%-- Checked --%>
													<s:checkbox theme="simple" fieldValue="%{id}"
														cssClass="groupMemberCheck" groupCheckallTarget="%{ getText(#resource.resourceGroup.resKey) }"
														name="checkedResourceList" value="true">
													</s:checkbox>
													<s:property value="%{ getText(name) }" />
												</s:if>
												<s:else>									<%-- Not Checked --%>
													<s:checkbox theme="simple" fieldValue="%{id}"
														cssClass="groupMemberCheck" groupCheckallTarget="%{ getText(#resource.resourceGroup.resKey) }"
														name="checkedResourceList">
													</s:checkbox>
													<s:property value="%{ getText(name) }" />
												</s:else>
											</li>
											
										</s:if>
										<s:else>											<%-- To detect whether it is sub group --%>
										
											<li>
												<s:if test="#resource.checked == true">		<%-- Checked --%>
													<s:checkbox theme="simple" fieldValue="%{id}"
														cssClass="subGroupMemberCheck" subGroupCheckallTarget="%{ getText(#resource.resourceSubGroup.resKey) }"
														name="checkedResourceList" value="true">
													</s:checkbox>
													<s:property value="%{ getText(name) }" />
												</s:if>
												<s:else>									<%-- Not Checked --%>
													<s:checkbox theme="simple" fieldValue="%{id}"
														cssClass="subGroupMemberCheck" subGroupCheckallTarget="%{ getText(#resource.resourceSubGroup.resKey) }"
														name="checkedResourceList">
													</s:checkbox>
													<s:property value="%{ getText(name) }" />
												</s:else>
											</li>
										
										</s:else>

									</s:else>

								</s:else>

							</s:iterator>
							
							<s:if test="#resourceSubGroup != #emptyString">		<%-- To detect whether there is a sub group which need to be ended --%>
								<s:set var="resourceSubGroup" value=""/>		<%-- End the sub group --%>
								</ul></li>
							</s:if>

							<s:if test="#resourceGroup != #emptyString">		<%-- To detect whether there is a group which need to be ended --%>
								<s:set var="resourceGroup" value=""/>			<%-- End the group --%>
								</ul></li>
							</s:if>
							
						</ul>
				
				</div>
			</li>
		</ul>

	</fieldset>

	<div class="buttonArea">
		<!-- the button event-handling is in utils.js -->
		<s:submit theme="simple" method="create" key="btn.add" cssClass="button blue buttonMargin ajaxButton" />
		<s:reset theme="simple" method="" key="btn.reset" cssClass="button rosy buttonMargin" />
	</div>
</s:form>
</div>
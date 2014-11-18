<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<h3 class="fnName"><s:text name="page.title.special.event.edit" /></h3>

<div class="body">
<s:form theme="simple" id="editForm" namespace="/general" action="special-event" method="post" cssClass="ajaxForm">
	
	<fieldset id="generalInfoFieldset" name="generalInfoFieldset" class="fieldsetStyle02">
		<legend><s:text name="fieldset.legend.generalinfo"/></legend>
		
		<!-- This div's ref must set to form id -->
		<div class="actionErrors" ref="editForm"></div>
		<div class="actionMessages" ref="editForm"></div>
		
		<s:hidden theme="simple" name="specialEvent.id" />
		<s:hidden theme="simple" name="specialEvent.staff.id" />
		
		<ul>
			<li>
				<label for="seName"><s:text name="special.event.special.event"/>:</label>
				<s:textfield theme="simple" name="seName" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:property value="%{getText(specialEvent.specialEventName.value)}" escape="false"/>
					</s:param>
				</s:textfield>
			</li>
			<li>
				<s:if test="specialEvent.specialEventName.toString() == 'ClothLost' && specialEvent.specialEventStatus.toString() != 'Finished'">
					<label for="eventStatusSelectionbox"><s:text name="special.event.status"/>: </label>
					<s:select
						theme="simple"
						id="eventStatusSelectionbox"
						name="specialEvent.specialEventStatus" 
						list="specialEventStatusList"
						listValue="getText(value)"
						emptyOption="false"
						cssClass="inputText"
					 />
				</s:if>
				<s:else>
					<label for="seStatus"><s:text name="special.event.status"/>:</label>
					<s:textfield theme="simple" name="seStatus" cssClass="displayText" readonly="true" >
						<s:param name="value">
							<s:property value="%{getText(specialEvent.specialEventStatus.value)}" escape="false"/>
						</s:param>
					</s:textfield>
				</s:else>
			</li>
			
			<li>
				<label for="tbCreationDate"><s:text name="label.creation.date"/>:</label>
				<s:textfield theme="simple" name="tbCreationDate" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:date name="specialEvent.creationDate" format="dd-MM-yyyy HH:mm:ss"/>
					</s:param>
				</s:textfield>
			</li>
			<li>
				<label for="tbLastModifyDate"><s:text name="label.last.update.date"/>:</label>
				<s:textfield theme="simple" name="tbLastModifyDate" cssClass="displayText" readonly="true" >
					<s:param name="value">
						<s:date name="specialEvent.lastModifyDate" format="dd-MM-yyyy HH:mm:ss"/>
					</s:param>
				</s:textfield>
			</li>
			
			
			<li>
				<label for=specialEvent.staff.dept.nameCht><s:text name="staff.dept"/>:</label>
				<s:textfield theme="simple" name="specialEvent.staff.dept.nameCht" cssClass="displayText" readonly="true" />
			</li>
			<li>
				<label for="specialEvent.staff.code"><s:text name="staff.code"/>: </label>
				<s:textfield theme="simple" name="specialEvent.staff.code" readonly="true" cssClass="displayText" />
			</li>
			
			<li>
				<label for="specialEvent.staff.nameCht"><s:text name="staff.name.cht"/>: </label>
				<s:textfield theme="simple" name="specialEvent.staff.nameCht" readonly="true" cssClass="displayText" />
			</li>
			
			<li>
				<label for="specialEvent.staff.nameEng"><s:text name="staff.name.eng"/>: </label>
				<s:textfield theme="simple" name="specialEvent.staff.nameEng" readonly="true" cssClass="displayText" />
			</li>
			
			<li>
				<label for="specialEvent.cloth.clothType.name"><s:text name="cloth.type"/>: </label>

				<s:if test="specialEvent.cloth == null">
					<s:textfield theme="simple" name="specialEvent.clothType.name" readonly="true" cssClass="displayText" />
				</s:if>
				<s:else>
					<s:textfield theme="simple" name="specialEvent.cloth.clothType.name" readonly="true" cssClass="displayText" />
				</s:else>

			</li>
			
			<li>
				<label for="specialEvent.cloth.size"><s:text name="cloth.size"/>: </label>
				<s:textfield theme="simple" name="specialEvent.cloth.size" readonly="true" cssClass="displayText" />
			</li>
			
			<li>
				<s:if test="specialEvent.specialEventName.toString() == 'ClothLost' && specialEvent.specialEventStatus.toString() != 'Finished'">
					<label for="specialEvent.cloth.code"><s:text name="cloth.code"/>: </label>
					<s:textfield theme="simple" name="specialEvent.cloth.code" readonly="false" cssClass="inputText" />
				</s:if>
				<s:else>
					<label for="specialEvent.cloth.code"><s:text name="cloth.code"/>: </label>
					<s:textfield theme="simple" name="specialEvent.cloth.code" readonly="true" cssClass="displayText" />
				</s:else>
			</li>
		</ul>
	</fieldset>
	
	<div class="buttonArea">
		<s:if test="specialEvent.specialEventName.toString() == 'ClothLost' && specialEvent.specialEventStatus.toString() != 'Finished'">
			<s:submit theme="simple" type="button" key="btn.save" method="update" cssClass="button blue buttonMargin ajaxButton" />
			<s:submit theme="simple" type="button" key="btn.cancel" id="cancelButton" cssClass="button rosy buttonMargin" />
		</s:if>
		<s:else>
			<s:submit theme="simple" type="button" key="btn.back" id="backButton" cssClass="button rosy buttonMargin" />
		</s:else>
	</div>
</s:form>

</div>



<script>

$(function() {
	
	//////////////////////////////////////////////////
	// Go to list page if save success
	//////////////////////////////////////////////////
	<s:url var="getListPageUrl" namespace="/general" action="special-event" method="getListPage"/>
	var url = "<s:property value="#getListPageUrl"/>";
	
	$("body").delegate("#editForm", "editForm.success", function(e, result, result2) {
		
		msgDialog("", result.actionMessages, "<s:text name="btn.ok"/>",
				
				function()
				{
					$(location).attr('href', url);
				}
			);
	});
	
	// Press 'Cancel' button will direct to the List Page
	$("#cancelButton").on('click', function(){
		$(location).attr('href', url);
		return false;
	});
	
	// Press 'Back' button will direct to the List Page
	$("#backButton").on('click', function(){
		$(location).attr('href', url);
		return false;
	});
});

</script>
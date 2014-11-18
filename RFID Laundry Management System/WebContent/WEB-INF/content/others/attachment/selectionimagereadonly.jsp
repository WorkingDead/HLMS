<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<% Long now = System.currentTimeMillis(); %>

<table class="nospacing">
	<tr>
		<td valign="top" rowspan="2">
			<label for="attachmentDiv" class="labelForMultiRowDiv">
				<s:text name="label.image"></s:text>:
			</label>
		</td>
	</tr>
	<tr>
		<td>
			<ul id="customer-qq-upload-list<%=now %>">
				<s:if test="attachmentList!=null">
					<s:iterator value="attachmentList.attachments">
						<li class=" qq-upload-success">
							<input disabled="disabled" name="selectedImageId" id="selectedImageId<s:property value="id"/>"
							<s:if test="selected">checked="checked"</s:if>
							 value="<s:property value="id"/>" type="radio"/>
				            <span class="qq-upload-file">
				            	<a href="<s:property value="#showAttachmentUrl"></s:property>?attachmentId=<s:property value="id"/>" target="_blank">
				            		<s:property value="filename"/>
				            	</a>
				            </span>
				            <span class="qq-upload-size"><s:property value="fileSize"/></span>
				            <br/>
				            <img src="<s:property value="#showAttachmentUrl"></s:property>?attachmentId=<s:property value="id"/>&thumbnail=true"/>
			        	</li>
					</s:iterator>
				</s:if>
			</ul>
		</td>
	</tr>
</table>
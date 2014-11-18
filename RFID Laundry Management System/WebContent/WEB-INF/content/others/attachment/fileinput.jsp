<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<% Long now = System.currentTimeMillis(); %>

<%-- URLs --%>
<%-- upload attachment url --%>
<s:url var="uploadAttachmentUrl" namespace="/others" action="attachment" method="uploadAttachment"></s:url>

<%-- upload attachment url --%>
<s:url var="showAttachmentUrl" namespace="/others" action="attachment" method="showAttachment"></s:url>

<table class="nospacing">
	<tr>
		<td valign="top" rowspan="2">
			<label for="attachmentDiv" class="labelForMultiRowDiv">
				<s:text name="label.attachment"></s:text>:
			</label>
		</td>
		<td>
			<div id="attachmentDiv<%=now %>" class="alignLeftDiv">
				<noscript>
					<p>Please enable JavaScript to use file uploader.</p>
				</noscript>
			</div>
		</td>
	</tr>
	<tr>
		<td>
			<ul id="customer-qq-upload-list<%=now %>">
				<s:if test="attachmentList!=null">
					<s:iterator value="attachmentList.attachments">
						<li class=" qq-upload-success">
				            <span class="qq-upload-file">
				            	<a href="<s:property value="#showAttachmentUrl"></s:property>?attachmentId=<s:property value="id"/>" target="_blank">
				            		<s:property value="filename"/>
				            	</a>
				            </span>
				            <span class="qq-upload-size"><s:property value="fileSize"/></span>
				            <span class="qq-upload-file-delete"><a href="#" class="deleteAttachmentA<%=now %>" ref="<s:property value="id"/>">Delete</a></span>
			        	</li>
					</s:iterator>
				</s:if>
<!-- 				<li> -->
<!-- 		            <span class="qq-upload-file">asdasd</span> -->
<!-- 		            <span class="qq-upload-size">10MB</span> -->
<!-- 	        	</li> -->
			</ul>
		</td>
	</tr>
</table>


<div id="upload-input-list<%=now %>" class="hidden">
	<!-- adding hidden input for add/del attachment -->
</div>

<script>
	$(function(){
		
		var index = 0;
		var delindex = 0;
		
		var uploader = new qq.FileUploader({
			element : document.getElementById('attachmentDiv<%=now %>'),
			action : '<s:property value="#uploadAttachmentUrl"/>',
			params: {
				"struts.enableJSONValidation" : "true",
				"attachmentType" : "File"
			},
			onComplete: function(id, fileName, responseJSON){
				if(responseJSON.success){
					
					$('#qqFileLi<%=now %>' + id + ' span.qq-upload-file-delete').replaceWith(function() {
					    return '<span class="qq-upload-file-delete"><a href="#" class="deleteAttachmentA<%=now %>" ref="' + responseJSON.attachmentId + '">Delete</a></span>';
					});
					
					$('#qqFileLi<%=now %>' + id + ' span.qq-upload-file').replaceWith(function() {
					    var url = "<s:property value="#showAttachmentUrl"/>" + "?attachmentId=" + responseJSON.attachmentId;
					    return '<span class="qq-upload-file"><a href="' + url + '" target="_blank">' + $(this).text() + '</a></span>';
					});
					
					var input = $("<input/>")
						.attr("type", "hidden")
						.attr("name", "addAttachments[" + index+ "]")
						.attr("value", responseJSON.attachmentId);
					$("#upload-input-list<%=now %>").append(input);
					
					index++;
				}
			},
			qqFileName: "fileAttachment.upload",
			listElement: document.getElementById('customer-qq-upload-list<%=now %>'),
			fileTemplate: 
			'<li id="qqFileLi<%=now %>{id}">' +
	            '<span class="qq-upload-file"></span>' +
	            '<span class="qq-upload-spinner"></span>' +
	            '<span class="qq-upload-size"></span>' +
	            '<a class="qq-upload-cancel" href="#">Cancel</a>' +
	            '<span class="qq-upload-failed-text">Failed</span>' +
	            '<span class="qq-upload-file-delete"></span>' +
	        '</li>',   
		});
		
		$(".deleteAttachmentA<%=now %>").live('click', function(){
			
			var id = $(this).attr("ref");
			$(this).closest('li').hide();
			
			var input = $("<input/>")
				.attr("type", "hidden")
				.attr("name", "delAttachments[" + delindex+ "]")
				.attr("value", id);
			$("#upload-input-list<%=now %>").append(input);
			
			delindex++;
			
			return false;
		});
	});
</script>
<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/content/layout/taglibs.jsp"%>

<% Long now = System.currentTimeMillis(); %>

<%-- URLs --%>
<%-- upload attachment url --%>
<s:url var="uploadAttachmentUrl" namespace="/others" action="attachment" method="uploadAttachment"></s:url>

<%-- upload attachment url --%>
<s:url var="showAttachmentUrl" namespace="/others" action="attachment" method="showAttachment"></s:url>

<label for="attachmentDiv"><s:text name="label.image"/>:</label>
<div class="qq-div">
	<div id="attachmentDiv<%=now%>">
		<noscript>
			<p>Please enable JavaScript to use file uploader.</p>
		</noscript>
	</div>

	<ul id="customer-qq-upload-list<%=now%>">
		<s:if test="attachmentList!=null">
			<s:iterator value="attachmentList.attachments">
				<li class="qq-upload-success">

				    <span class="qq-upload-file">
				    	<a href="<s:property value="#showAttachmentUrl"></s:property>?attachmentId=<s:property value="id"/>" target="_blank">
				    		<s:property value="filename"/>
				    	</a>
				    </span>

					<span class="qq-upload-size"><s:property value="fileSize" /></span>

					<span class="qq-upload-file-delete">
						<a href="#" class="deleteAttachmentA<%=now%>" ref="<s:property value="id"/>">Delete</a>
					</span>

					<br />

<%-- IE 8 can not resize automatically and therefore the height is fixed 150 px --%>
					<img height="150px" src="<s:property value="#showAttachmentUrl"></s:property>?attachmentId=<s:property value="id"/>&thumbnail=true" />
				</li>
			</s:iterator>
		</s:if>
	</ul>
</div>

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
			allowedExtensions: ['jpg', 'jpeg', 'png', 'gif', 'bmp'],
			params: {
				"struts.enableJSONValidation" : "true",
				"attachmentType" : "SoleImage"
			},
			onComplete: function(id, fileName, responseJSON){
				if(responseJSON.success){
					
					$(".deleteAttachmentA<%=now %>").trigger("click");
					
					$('#qqFileLi<%=now %>' + id + ' span.qq-upload-file-delete').replaceWith(function() {
					    return '<span class="qq-upload-file-delete"><a href="#" class="deleteAttachmentA<%=now %>" ref="' + responseJSON.attachmentId + '">Delete</a></span>';
					});
					
					$('#qqFileLi<%=now %>' + id + ' span.qq-upload-file').replaceWith(function() {
					    var url = "<s:property value="#showAttachmentUrl"/>" + "?attachmentId=" + responseJSON.attachmentId;
					    return '<span class="qq-upload-file"><a href="' + url + '" target="_blank">' + $(this).text() + '</a></span>';
					});

<%-- IE 8 can not resize automatically and therefore the height is fixed 150 px --%>
					$('#qqFileLi<%=now %>' + id + ' span.qq-upload-image-thumbnail').replaceWith(function() {
					    var url = "<s:property value="#showAttachmentUrl"/>" + "?attachmentId=" + responseJSON.attachmentId + "&thumbnail=true";
					    return '<img src="' + url + '" height="150px"/>';
					});
					
					var input = $("<input/>")
						.attr("type", "hidden")
						.attr("name", "addAttachments[" + index+ "]")
						.attr("value", responseJSON.attachmentId);
					$("#upload-input-list<%=now %>").append(input);
					
					index++;
				}
			},
			qqFileName: "soleImageAttachment.upload",
			listElement: document.getElementById('customer-qq-upload-list<%=now %>'),
			fileTemplate: 
			'<li id="qqFileLi<%=now %>{id}">' +
	            '<span class="qq-upload-file"></span>' +
	            '<span class="qq-upload-spinner"></span>' +
	            '<span class="qq-upload-size"></span>' +
	            '<a class="qq-upload-cancel" href="#">Cancel</a>' +
	            '<span class="qq-upload-failed-text">Failed</span>' +
	            '<span class="qq-upload-file-delete"></span>' +
	            '<br/><span class="qq-upload-image-thumbnail"></span>' + 
	        '</li>',   
		});
		
		$(".deleteAttachmentA<%=now %>").live('click', function(){
			
			if ( $(this).closest('li').is(":visible") == false ) {
				return false;
			}
			
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
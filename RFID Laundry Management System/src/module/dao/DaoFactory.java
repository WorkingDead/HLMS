package module.dao;

public class DaoFactory
{
	// All DAO bean name define here
	public static final String CLOTH = "cloth";
	public static final String CLOTH_DAO = "ClothDao";
	
	public static final String HISTORY_CLOTH = "history_cloth";
	public static final String HISTORY_CLOTH_DAO = "HistoryClothDao";
	
	public static final String CLOTH_TYPE = "cloth_type";
	public static final String CLOTH_TYPE_DAO = "ClothTypeDao";
	
	public static final String ZONE = "zone";
	public static final String ZONE_DAO = "ZoneDao";
	
	public static final String STAFF = "staff";
	public static final String STAFF_DAO = "StaffDao";
	
	public static final String STF = "stf";
	public static final String STF_DAO = "StfDao";
	
	public static final String DEPARTMENT = "department";
	public static final String DEPARTMENT_DAO = "DepartmentDao";
	
	public static final String RECEIPT = "receipt";
	public static final String RECEIPT_DAO = "ReceiptDao";
	
	public static final String RECEIPT_PATTERN_IRON = "receipt_pattern_iron";
	public static final String RECEIPT_PATTERN_IRON_DAO = "ReceiptPatternIronDao";
	
	
	public static final String TRANSACTION = "\"transaction\"";		// in ms-sql, table name "transaction" must be specially handle 
	public static final String TRANSACTION_DAO = "TransactionDao";
	
	public static final String SPECIAL_EVENT = "special_event";
	public static final String SPECIAL_EVENT_DAO = "SpecialEventDao";
	
	public static final String ROLL_CONTAINER = "roll_container";
	public static final String ROLL_CONTAINER_DAO = "RollContainerDao";
	
	public static final String ALERT = "alert";
	public static final String ALERT_DAO = "AlertDao";
	
	public static final String GenerateNoRecord = "generate_no_record";
	public static final String GenerateNoRecordDao = "GenerateNoRecordDao";
	
	/* HQL & SQL & Special */
	public static final String HQL_DAO = "HQLDao";
	public static final String SQL_DAO = "SQLDao";
	public static final String SPECIAL_CRITERIA_DAO = "SpecialCriteriaDao";
	/* HQL & SQL & Special */
	
	
	/* System */
	public static final String USERS = "users";
	public static final String USERS_DAO = "UsersDao";
	
	public static final String GROUPS = "groups";
	public static final String GROUPS_DAO = "GroupsDao";

	public static final String GROUP_AUTHORITIES = "group_authorities";
	public static final String GROUP_AUTHORITIES_DAO = "GroupAuthoritiesDao";
	
	public static final String SECURITY_RESOURCE = "security_resource";
	public static final String SECURITY_RESOURCE_DAO = "SecurityResourceDao";
	/* System */
	

	/* Others */
	public static final String Attachment = "attachments";
	public static final String AttachmentDao = "attachmentDao";
	
		public static final String ImageAttachment = "image_attachments";
		public static final String ImageAttachmentDao = "imageAttachmentDao";
		
			public static final String SelectionImageAttachment = "selection_image_attachments";
			public static final String SelectionImageAttachmentDao = "selectionImageAttachmentDao";
			
			public static final String SoleImageAttachment = "sole_image_attachments";
			public static final String SoleImageAttachmentDao = "soleImageAttachmentDao";
			
		public static final String FileAttachment = "file_attachments";
		public static final String FileAttachmentDao = "fileAttachmentDao";
		
	//attachment list for object mapping
	public static final String AttachmentList = "attachments_list";
	public static final String AttachmentListDao = "attachmentListDao";
	/* Others */
}

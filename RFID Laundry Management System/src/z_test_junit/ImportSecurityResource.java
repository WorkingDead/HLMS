package z_test_junit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import module.dao.system.SecurityResource;
import module.dao.system.SecurityResource.ResourceGroup;
import module.dao.system.SecurityResource.ResourceSubGroup;
import module.dao.system.SecurityResource.ResourceType;
import module.service.ServiceFactory;
import module.service.all.SystemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.ibm.icu.impl.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class ImportSecurityResource {

	@Resource(name=ServiceFactory.SystemService)
	public SystemService systemService;



	public static enum ResourceValue {

		processMgt(
				"menu.level1.process.mgt",
				ResourceType.MENU,
				10,
				ResourceGroup.Process_Mgt,
				null,
				"ProcessMgtMenu, " +
				"/general/ironing-delivery-fixed-reader, " +
				"/general/cloth-rack-handheld, " + 
				"/general/, " +
				"BackupProcedureSubMenu, " +
				"/general/cloth-collection-handheld, " + 
				"/general/ironing-delivery-handheld",
				""
				),

				ironDelivery(
						"menu.level2.iron.delivery", 
						ResourceType.URL,
						10, 
						ResourceGroup.Process_Mgt, 
						null,
						"/general/ironing-delivery-fixed-reader!getMainPage.action", 
						""),
				
				rackPlace(
						"menu.level2.cloth.rack.place", 
						ResourceType.URL,
						10, 
						ResourceGroup.Process_Mgt, 
						null,
						"/general/cloth-rack-handheld!getMainPage.action", 
						""),

				clothAssociation(
						"menu.level2.cloth.association", 
						ResourceType.URL,
						10, 
						ResourceGroup.Process_Mgt, 
						null,
						"/general/cloth-association-handheld!getMainPage.action", 
						""),

				backupProcedure(
						"menu.level2.backup.procedure",
						ResourceType.SUBMENU,
						10,
						ResourceGroup.Process_Mgt,
						ResourceSubGroup.Backup_Procedure,
						"BackupProcedureSubMenu, " +
						"/general/cloth-collection-handheld!getMainPage.action, " + 
						"/general/ironing-delivery-handheld!getMainPage.action",
						""
						),
		
						dirtyClothRecv(
								"menu.level3.dirty.cloth.recv", 
								ResourceType.URL, 
								10, 
								ResourceGroup.Process_Mgt, 
								ResourceSubGroup.Backup_Procedure,
								"/general/cloth-collection-handheld!getMainPage.action", 
								""),
						
						ironDeliveryHandheld(
								"menu.level3.iron.delivery", 
								ResourceType.URL, 
								10, 
								ResourceGroup.Process_Mgt, 
								ResourceSubGroup.Backup_Procedure,
								"/general/ironing-delivery-handheld!getMainPage.action", 
								""),



		washReceiptMgt(
				"menu.level1.wash.receipt.mgt", 
				ResourceType.MENU,
				10, 
				ResourceGroup.Wash_Receipt_Mgt, 
				null,
				"/general/receipt!getListPage.action", 
				""),



		invMgt(
				"menu.level1.inv.mgt", 
				ResourceType.MENU,
				10, 
				ResourceGroup.Inv_Mgt, 
				null,
				"/general/inventory!getListPage.action", 
				""),



		specialEventMgt(
				"menu.level1.special.event.mgt", 
				ResourceType.MENU,
				10, 
				ResourceGroup.Special_Event_Mgt, 
				null,
				"/general/special-event!getListPage.action", 
				""),



		staffMgt(
				"menu.level1.staff.mgt", 
				ResourceType.MENU, 
				10, 
				ResourceGroup.Staff_Mgt, 
				null,
				"StaffMgtMenu, " +
				"/master/staff!getNewPage.action, " +
				"/master/staff!getListPage.action", 
				""
				),

				newStaff(
						"menu.level2.new.staff", 
						ResourceType.URL,
						10, 
						ResourceGroup.Staff_Mgt, 
						null,
						"/master/staff!getNewPage.action", 
						""),
				
				staffSummary(
						"menu.level2.staff.summary", 
						ResourceType.URL,
						10, 
						ResourceGroup.Staff_Mgt, 
						null,
						"/master/staff!getListPage.action", 
						""),



		clothTypeMgt(
				"menu.level1.cloth.type.mgt", 
				ResourceType.MENU, 
				10, 
				ResourceGroup.Cloth_Type_Mgt, 
				null,
				"ClothTypeMgtMenu, " +
				"/master/cloth-type!getNewPage.action, " +
				"/master/cloth-type!getListPage.action", 
				""
				),

				newClothType(
						"menu.level2.new.cloth.type", 
						ResourceType.URL,
						10, 
						ResourceGroup.Cloth_Type_Mgt, 
						null,
						"/master/cloth-type!getNewPage.action", 
						""),
				
				clothTypeSummary(
						"menu.level2.cloth.type.summary", 
						ResourceType.URL,
						10, 
						ResourceGroup.Cloth_Type_Mgt, 
						null,
						"/master/cloth-type!getListPage.action", 
						""),



		rackMgt(
				"menu.level1.zone.mgt", 
				ResourceType.MENU, 
				10, 
				ResourceGroup.Rack_Mgt, 
				null,
				"RackMgtMenu, " +
				"/master/zone!getNewPage.action, " +
				"/master/zone!getListPage.action", 
				""
				),

				newRack(
						"menu.level2.new.rack", 
						ResourceType.URL,
						10, 
						ResourceGroup.Rack_Mgt, 
						null,
						"/master/zone!getNewPage.action", 
						""),
				
				rackSummary(
						"menu.level2.rack.summary", 
						ResourceType.URL,
						10, 
						ResourceGroup.Rack_Mgt, 
						null,
						"/master/zone!getListPage.action", 
						""),



		reportMenu(
				"menu.level1.report", 
				ResourceType.MENU, 
				10, 
				ResourceGroup.Report, 
				null,
				"ReportMenu, " +
				"/report/monthly-laundry-recv-report!, " +
				"/report/monthly-laundry-dist-report!, " +
				"/report/monthly-reported-missing-report!, " +
				"/report/daily-incident-report!, " +  
				"/report/washing-count-report!", 
				""
				),
		
				reportMonthlyLaundryRecv(
						"menu.level2.monthly.laundry.recv.report", 
						ResourceType.URL,
						10, 
						ResourceGroup.Report, 
						null,
						"/report/monthly-laundry-recv-report!getReportPage.action", 
						""),
				
				reportMonthlyLaundryDist(
						"menu.level2.monthly.laundry.dist.report", 
						ResourceType.URL,
						10, 
						ResourceGroup.Report, 
						null,
						"/report/monthly-laundry-dist-report!getReportPage.action", 
						""),
				
				reportMonthlyReportedMissing(
						"menu.level2.monthly.reported.missing.report", 
						ResourceType.URL,
						10, 
						ResourceGroup.Report, 
						null,
						"/report/monthly-reported-missing-report!getReportPage.action", 
						""),
				
				reportDailyIncident(
						"menu.level2.daily.inc.report", 
						ResourceType.URL,
						10, 
						ResourceGroup.Report, 
						null,
						"/report/daily-incident-report!getReportPage.action", 
						""),
						
				reportWashingCount(
						"menu.level2.washing.count.report",
						ResourceType.URL,
						10, 
						ResourceGroup.Report, 
						null,
						"/report/washing-count-report!getReportPage.action",
						""
						),



		alertSetting(
				"menu.level1.alert.setting", 
				ResourceType.MENU,
				10, 
				ResourceGroup.Alert_Setting, 
				null,
				"/general/alert!getListPage.action", 
				""),		



		userMgtMenu(
				"menu.level1.user.mgt",
				ResourceType.MENU,
				10,
				ResourceGroup.User_Mgt,
				null,
				"UserMgtMenu, " +
				"UserGroupMgtSubMenu, " +
				"/system/user-group-management, " + 
				"/system/user-group-management, " +
				"UserAccountMgtSubMenu, " +
				"/system/user-management, " + 
				"/system/user-management",
				""
				),

				userAccountMgtSubMenu(
						"menu.level2.user.mgt",
						ResourceType.SUBMENU,
						10,
						ResourceGroup.User_Mgt,
						ResourceSubGroup.User_Account_Management,
						"UserAccountMgtSubMenu, " +
						"/system/user-management!usersCreate.action, " + 
						"/system/user-management!usersList.action",
						""
						),

					usersCreate(
							"menu.level3.new.user", 
							ResourceType.URL, 
							10, 
							ResourceGroup.User_Mgt, 
							ResourceSubGroup.User_Account_Management,
							"/system/user-management!usersCreate.action", 
							""),
					usersList(
							"menu.level3.user.summary", 
							ResourceType.URL, 
							10, 
							ResourceGroup.User_Mgt, 
							ResourceSubGroup.User_Account_Management,
							"/system/user-management!usersList.action", 
							""),

				userGroupMgtSubMenu(
						"menu.level2.group.mgt",
						ResourceType.SUBMENU,
						10,
						ResourceGroup.User_Mgt,
						ResourceSubGroup.User_Group_Management,
						"UserGroupMgtSubMenu, " +
						"/system/user-group-management!groupsCreate.action, " + 
						"/system/user-group-management!groupsList.action",
						""
						),

					groupsCreate(
							"menu.level3.new.group", 
							ResourceType.URL,
							10, 
							ResourceGroup.User_Mgt, 
							ResourceSubGroup.User_Group_Management,
							"/system/user-group-management!groupsCreate.action", 
							""),
					groupsList(
							"menu.level3.group.summary",
							ResourceType.URL,
							10,
							ResourceGroup.User_Mgt,
							ResourceSubGroup.User_Group_Management,
							"/system/user-group-management!groupsList.action", 
							"")
		;
		
		private String name;
		private ResourceType type;
		private Integer priority;
		private ResourceGroup resourceGroup;
		private ResourceSubGroup resourceSubGroup;
		private String resource;
		private String description;

		ResourceValue(String name, ResourceType type, Integer priority, ResourceGroup resourceGroup, ResourceSubGroup resourceSubGroup, String resource, String description)
		{
			this.name = name;
			this.type = type;
			this.priority = priority;
			this.resourceGroup = resourceGroup;
			this.resourceSubGroup = resourceSubGroup;
			this.resource = resource;
			this.description = description;
		}


		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public ResourceType getType() {
			return type;
		}

		public void setType(ResourceType type) {
			this.type = type;
		}

		public Integer getPriority() {
			return priority;
		}

		public void setPriority(Integer priority) {
			this.priority = priority;
		}

		public ResourceGroup getResourceGroup() {
			return resourceGroup;
		}

		public void setResourceGroup(ResourceGroup resourceGroup) {
			this.resourceGroup = resourceGroup;
		}
		
		public ResourceSubGroup getResourceSubGroup() {
			return resourceSubGroup;
		}

		public void setResourceSubGroup(ResourceSubGroup resourceSubGroup) {
			this.resourceSubGroup = resourceSubGroup;
		}
		
		public String getResource() {
			return resource;
		}

		public void setResource(String resource) {
			this.resource = resource;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}



	@Test(timeout = 5000)
	public void test() {
		
		try {
			List<SecurityResource> toBeSaveList = new LinkedList<SecurityResource>();
			Iterator<ResourceValue> resourceValueIterator = Arrays.asList(ResourceValue.values()).iterator();
			while ( resourceValueIterator.hasNext() ) {
				
				ResourceValue resourceValue = resourceValueIterator.next();

				SecurityResource userGroupResource = new SecurityResource();
				userGroupResource.setName( resourceValue.getName() );
				userGroupResource.setType( resourceValue.getType() );
				userGroupResource.setPriority( resourceValue.getPriority() );
				userGroupResource.setResourceGroup( resourceValue.getResourceGroup() );
				userGroupResource.setResourceSubGroup( resourceValue.getResourceSubGroup() );
				userGroupResource.setResource( resourceValue.getResource() );
				userGroupResource.setDescription( resourceValue.getDescription() );
				
				toBeSaveList.add(userGroupResource);
			}
			
			systemService.saveList(SecurityResource.class, toBeSaveList);
		}
		catch (Exception e) {
			e.printStackTrace();
			
			Assert.fail(e);
		}
	}
}
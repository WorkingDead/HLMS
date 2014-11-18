package module.dao.system;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import module.dao.BaseBoSystem;
import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.SECURITY_RESOURCE)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.SECURITY_RESOURCE,
indexes={
	@Index(name="ix_01", columnNames={"type", "resource"}),
	@Index(name="ix_02", columnNames={"resource", "type"}),
	} ) 
public class SecurityResource extends BaseBoSystem
{
	private static final long serialVersionUID = -3492238389607061948L;

	public static enum ResourceType
	{
		URL,
		MENU,
		SUBMENU,
		BUTTON,
		METHOD,
		OTHER
		;
	}
	
	public static enum ResourceGroup {
		Process_Mgt("security.resource.group.processMgt"),
		Wash_Receipt_Mgt("security.resource.group.washReceiptMgt"),
		Inv_Mgt("security.resource.group.invMgt"),
		Special_Event_Mgt("security.resource.group.specialEventMgt"),
		Staff_Mgt("security.resource.group.staffMgt"),
		Cloth_Type_Mgt("security.resource.group.clothTypeMgt"),
		Rack_Mgt("security.resource.group.rackMgt"),
		Report("security.resource.group.report"),
		Alert_Setting("security.resource.group.alertSetting"),
		User_Mgt("security.resource.group.userMgt"),
		;
		
		private String resKey;
		
		ResourceGroup(String resKey)
		{
			this.resKey = resKey;
		}

		public String getResKey() {
			return resKey;
		}

		public void setResKey(String resKey) {
			this.resKey = resKey;
		}
	}
	
	public static enum ResourceSubGroup {
		Backup_Procedure("security.resource.subGroup.backupProcedure"),
		User_Group_Management("security.resource.subGroup.userGroupManagement"),
		User_Account_Management("security.resource.subGroup.userAccountManagement"),
		;
		
		private String resKey;
		
		ResourceSubGroup(String resKey)
		{
			this.resKey = resKey;
		}

		public String getResKey() {
			return resKey;
		}

		public void setResKey(String resKey) {
			this.resKey = resKey;
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="name", length=100, unique=true, nullable = false)
	private String name;

	@Column(name="type", length=50, nullable = false)
	@Enumerated(EnumType.STRING)
	private ResourceType type;

	@Column(name="priority", nullable=false)
	private Integer priority;

	@Column(name="resource_group", length=50, nullable=false)
	@Enumerated(EnumType.STRING)
	private ResourceGroup resourceGroup;
	
	@Column(name="resource_sub_group", length=50)
	@Enumerated(EnumType.STRING)
	private ResourceSubGroup resourceSubGroup;

	@Column(name="resource", length=255, unique=true, nullable=false)
	private String resource;
	
	@ManyToMany(mappedBy="securityResource")
	private Set<GroupAuthorities> groupAuthorities;
	
	@Column(name="description", length=1000)
	private String description;

	@Transient
	private Boolean checked = true;		//Default True
	


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Set<GroupAuthorities> getGroupAuthorities() {
		return groupAuthorities;
	}

	public void setGroupAuthorities(Set<GroupAuthorities> groupAuthorities) {
		this.groupAuthorities = groupAuthorities;
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

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
}

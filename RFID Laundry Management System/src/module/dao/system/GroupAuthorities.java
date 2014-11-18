package module.dao.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import module.dao.BaseBoSystem;
import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.GROUP_AUTHORITIES)
public class GroupAuthorities extends BaseBoSystem
{

	private static final long serialVersionUID = -5852734143375380685L;

	public static enum GroupAuthoritiesType
	{
		ROLE_SUPER_ADMIN,
		ROLE_ADMIN,
		ROLE_USER,
		ROLE_OTHER
		;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

//	@Column(name = "group_id" ,insertable=false, updatable=false)
//	private Long groupId;

	@Column(name = "authority", length=50, nullable = false)
	@Enumerated(EnumType.STRING)
	private GroupAuthoritiesType authority;

	@ManyToOne
	@JoinColumn(name="group_id", insertable=false, updatable=false)
	@ForeignKey(name="fk_group_authorities_info_group_id")
	private Groups groups;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(
			name = "group_authorities_x_security_resource",
			joinColumns = {@JoinColumn(name="group_authorities_id")}        //unique in here may be wrong
			,inverseJoinColumns = {@JoinColumn(name="security_resource_id")}
	)
	@ForeignKey(name="fk_gaxr_group_authorities_id",				//Foreign key name cannot be too long  
				inverseName = "fk_gaxr_security_resource_id")
	private Set<SecurityResource> securityResource = new HashSet<SecurityResource>();



	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

//	public Long getGroupId() {
//		return groupId;
//	}
//
//	public void setGroupId(Long groupId) {
//		this.groupId = groupId;
//	}

	public GroupAuthoritiesType getAuthority()
	{
		return authority;
	}

	public void setAuthority(GroupAuthoritiesType authority)
	{
		this.authority = authority;
	}

	public Groups getGroups()
	{
		return groups;
	}

	public void setGroups(Groups groups)
	{
		this.groups = groups;
	}

	public Set<SecurityResource> getSecurityResource()
	{
		return securityResource;
	}

	public void setSecurityResource(Set<SecurityResource> securityResource)
	{
		this.securityResource = securityResource;
	}
}

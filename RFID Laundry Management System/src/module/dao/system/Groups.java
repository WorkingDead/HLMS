package module.dao.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import module.dao.BaseBoSystem;
import module.dao.DaoFactory;

import web.actions.BaseAction;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;

@Entity
@Table(name=DaoFactory.GROUPS)
public class Groups extends BaseBoSystem
{
	private static final long serialVersionUID = 2919495635427891359L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	//@Column(name = "id") // 非必要，在欄位名稱與屬性名稱不同時使用
	private Long id;

	@Column(name = "group_name", length=100, unique=true, nullable=false)
	private String groupName;

	@Column(name = "enabled", nullable=false)
	private Boolean enabled;

	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)  //mappedBy is mapped to the object field!
//	@Fetch(FetchMode.SUBSELECT)
	@JoinColumn(name="group_id")
	@OrderBy("id")
	private Set<GroupAuthorities> groupAuthorities = new HashSet<GroupAuthorities>();
	
	@ManyToMany(mappedBy="groups")
	private Set<Users> users = new HashSet<Users>();

	@Column(name="remark", length=1000)
	private String remark;
	
	//注意!!一定要有Constructor!!####################################
	public Groups()
	{}



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
//	@RequiredStringValidator(  key = BaseAction.ErrorMessage_Required )
//	@StringLengthFieldValidator( minLength = "1", maxLength = "100", key = BaseAction.ErrorMessage_StringLength )
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public String getEnabledString() {
		return enabled.toString();
	}
	public void setEnabledString(String enabledString) {
		
		if ( enabledString.length() <= 0 )
			this.setEnabled( null );
		else
			this.setEnabled( Boolean.parseBoolean( enabledString ) );
	}

	public Set<GroupAuthorities> getGroupAuthorities() {
		return groupAuthorities;
	}
	public void setGroupAuthorities(Set<GroupAuthorities> groupAuthorities) {
		this.groupAuthorities = groupAuthorities;
	}

	public Set<Users> getUsers() {
		return users;
	}
	public void setUsers(Set<Users> users) {
		this.users = users;
	}
	
//	@StringLengthFieldValidator( minLength = "1", maxLength = "1000", key = BaseAction.ErrorMessage_StringLength )
	public String getRemark()
	{
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = trimString( remark );
	}
}

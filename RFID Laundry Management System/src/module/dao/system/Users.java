package module.dao.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import module.dao.BaseBoSystem;
import module.dao.DaoFactory;

import org.hibernate.annotations.ForeignKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import web.actions.BaseAction;
import web.actions.BaseAction.SystemLanguage;

import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;

@Entity
@Table(name=DaoFactory.USERS)
public class Users extends BaseBoSystem implements UserDetails
{
	private static final long serialVersionUID = 6339561923280474704L;

	@Id
	@Column(name = "username", length=100, unique=true, nullable=false)
	private String username;

	@Column(name = "password", length=100, nullable=false)
	private String password;

	@Transient
	private String confirmPassword;
	
	@Column(name = "enabled", nullable=false)
	private Boolean enabled;
	
	@Transient
	private String enabledString;
	
	@Column(name = "user_display_name", length=100, nullable=false)
	private String userDisplayName;

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "group_members",
	joinColumns = {@JoinColumn(name="username", unique = true)}        //unique in here may be wrong
	,inverseJoinColumns = {@JoinColumn(name="group_id")}				//but unique reason is one user can belong to one group only
	)
	@ForeignKey(name="fk_group_members_username", inverseName = "fk_group_members_group_id")	//Foreign key name cannot be too long  
	private Set<Groups> groups = new HashSet<Groups>();

	@Column(name="lang", nullable=false)
	@Enumerated(EnumType.STRING)
	private SystemLanguage lang;
	
	@Column(name="email")
	private String email;
	
	@Column(name="remark", length=1000)
	private String remark;



	//注意!!一定要有Constructor!!####################################
	public Users()
	{}

//	@RequiredStringValidator(  key = BaseAction.ErrorMessage_Required )
//	@StringLengthFieldValidator( minLength = "1", maxLength = "100", key = BaseAction.ErrorMessage_StringLength )
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
		this.userDisplayName = username;
	}
	
//	@RequiredStringValidator(  key = BaseAction.ErrorMessage_Required )		//Controlled by validate method because this cannot be used for create and update method at the same time
//	@StringLengthFieldValidator( minLength = "3", maxLength = "4", key = BaseAction.ErrorMessage_StringLength )
//	@FieldExpressionValidator( expression = "password eq confirmPassword", key = BaseAction.ErrorMessage_PasswordAndConfirmPassword )		//The example of this validator
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
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

//	@RequiredStringValidator(  key = BaseAction.ErrorMessage_Required )
//	@StringLengthFieldValidator( minLength = "1", maxLength = "100", key = BaseAction.ErrorMessage_StringLength )
	public String getUserDisplayName() {
		return userDisplayName;
	}
	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public Set<Groups> getGroups() {
		return groups;
	}
	public void setGroups(Set<Groups> groups) {
		this.groups = groups;
	}

	public SystemLanguage getLang() {
		return lang;
	}
	public void setLang(SystemLanguage lang) {
		this.lang = lang;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

//	@StringLengthFieldValidator( minLength = "1", maxLength = "1000", key = BaseAction.ErrorMessage_StringLength )
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = trimString( remark );
	}
	
	public String getGroupName() {
		
		List<String> outputStringList = new LinkedList<String>();

		Iterator<Groups> iterator = getGroups().iterator();
		while ( iterator.hasNext() ) {
			Groups groups = iterator.next();
			outputStringList.add( groups.getGroupName() );
		}

		if ( outputStringList.size() == 0 )
			return "";
		else {
			
			String output = "";
			for ( int i = 0; i < outputStringList.size(); i++ ) {
				
				if ( i == 0 )
					output = outputStringList.get(i);
				else
					output = output + ", " + outputStringList.get(i);
			}
			
			return output;
		}
	}
	
	
	//loading custom authorities from group
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
		
		for(Groups g : groups)
		{
			for(GroupAuthorities a : g.getGroupAuthorities())
			{
				auths.add(new SimpleGrantedAuthority(a.getAuthority().toString()));
			}
		}
		
		return auths;
	}

	//TODO
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	//TODO
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	//TODO
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}


}

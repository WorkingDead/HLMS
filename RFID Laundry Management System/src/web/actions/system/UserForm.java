package web.actions.system;

import web.actions.BaseForm;
import module.dao.system.Groups;

public class UserForm extends BaseForm
{
	private Groups groups;

//	@RequiredFieldValidator( key = BaseAction.ErrorMessage_Required )
	public Groups getGroups()
	{
		return groups;
	}
	public void setGroups(Groups groups)
	{
		this.groups = groups;
	}
}

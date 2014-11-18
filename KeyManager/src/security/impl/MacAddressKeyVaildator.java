package security.impl;

import security.iface.KeyValidator;
import utils.network.MAddress;

public class MacAddressKeyVaildator extends KeyValidator {

	@Override
	public boolean isValid(String key) {
		if(MAddress.getMacAddress().toUpperCase().contains(key.toUpperCase()))
		{
			return true;
		}
		return false;
	}

}

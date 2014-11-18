package security.impl;

import security.iface.KeyValidator;
import utils.io.HDDSerialID;

public class HDDSerialKeyVaildator extends KeyValidator {

	@Override
	public boolean isValid(String key) {
		return HDDSerialID.hasHDDSerialID(key);
	}

}

package module.security.setting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("securitySetting")
public class SecuritySetting {
	
	@Value("${security.keypath}")
	private String keyPath;

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

}

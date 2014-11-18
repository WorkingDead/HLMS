package module.ale;

import java.util.List;

import epcglobal.ale.xsd._1.ECReports;

public interface ECSpecHandler {

	public void handle(List<String> rawHexs, ECReports ecReports);
	
}

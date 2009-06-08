package tipi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import com.dexels.navajo.tipi.*;
import com.dexels.navajo.tipi.tipixml.CaseSensitiveXMLElement;
import com.dexels.navajo.tipi.tipixml.XMLElement;
import com.dexels.navajo.tipi.tipixml.XMLParseException;

public class TipiChartingExtension extends TipiAbstractXMLExtension implements TipiExtension {

	public TipiChartingExtension() throws XMLParseException,
			IOException {
		loadXML();
	}

	public void initialize(TipiContext tc) {
		// Do nothing
		
	}

	

}

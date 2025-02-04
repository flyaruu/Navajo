package tipipackage;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import tipi.TipiExtension;

public class TipiOSGiWhiteboardExtensionProvider extends TipiManualExtensionRegistry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5065605262245351282L;
	private final BundleContext bundleContext;

	public TipiOSGiWhiteboardExtensionProvider(BundleContext bc) {
		this.bundleContext = bc;
	}
	
	@Override
	public void registerTipiExtension(TipiExtension te) {
		// ignore manual adding
		// register OSGi service to add
	}
	

	public List<TipiExtension> getExtensionList() {
		List<TipiExtension> extensionList = new LinkedList<TipiExtension>();
		try {
			Collection<ServiceReference<TipiExtension>> aa = bundleContext.getServiceReferences(TipiExtension.class,"(type=tipiExtension)");
			for (ServiceReference<TipiExtension> serviceReference : aa) {
				extensionList.add(bundleContext.getService(serviceReference));
			}
			
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return extensionList;
	}

}

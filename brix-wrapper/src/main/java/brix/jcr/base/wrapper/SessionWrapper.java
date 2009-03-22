package brix.jcr.base.wrapper;

import brix.jcr.base.BrixSession;
import brix.jcr.base.action.AbstractActionHandler;
import brix.jcr.base.action.CompoundActionHandler;
import brix.jcr.base.event.ChangeLog;
import brix.jcr.base.event.ChangeLogActionHandler;
import brix.jcr.base.event.EventsListener;
import brix.jcr.base.filter.ValueFilter;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.jcr.Credentials;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class SessionWrapper extends BaseWrapper<Session> implements BrixSession
{

    private SessionWrapper(Session session)
    {
        super(session, null);
        
        changeLogActionHandler = new ChangeLogActionHandler(new ChangeLog(), this);
        actionHandler.addHandler(changeLogActionHandler);
    }

    public static SessionWrapper wrap(Session session)
    {
        if (session == null)
        {
            return null;
        }
        else
        {
            return new SessionWrapper(session);
        }
    }

    public void addLockToken(String lt)
    {
        getDelegate().addLockToken(lt);
    }

    public void checkPermission(String absPath, String actions) throws AccessControlException,
            RepositoryException
    {
        getDelegate().checkPermission(absPath, actions);
    }

    public void exportDocumentView(String absPath, ContentHandler contentHandler,
            boolean skipBinary, boolean noRecurse) throws SAXException,
            RepositoryException
    {
        getDelegate().exportDocumentView(absPath, contentHandler, skipBinary, noRecurse);
    }

    public void exportDocumentView(String absPath, OutputStream out, boolean skipBinary,
            boolean noRecurse) throws IOException, RepositoryException
    {
        getDelegate().exportDocumentView(absPath, out, skipBinary, noRecurse);
    }

    public void exportSystemView(String absPath, ContentHandler contentHandler, boolean skipBinary,
            boolean noRecurse) throws SAXException, RepositoryException
    {
        getDelegate().exportSystemView(absPath, contentHandler, skipBinary, noRecurse);
    }

    public void exportSystemView(String absPath, OutputStream out, boolean skipBinary,
            boolean noRecurse) throws IOException, RepositoryException
    {
        getDelegate().exportSystemView(absPath, out, skipBinary, noRecurse);
    }

    public Object getAttribute(String name)
    {
        return getDelegate().getAttribute(name);
    }

    public String[] getAttributeNames()
    {
        return getDelegate().getAttributeNames();
    }

    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior)
            throws RepositoryException
    {
        return getDelegate().getImportContentHandler(parentAbsPath, uuidBehavior);
    }

    public Item getItem(String absPath) throws RepositoryException
    {
        return ItemWrapper.wrap(getDelegate().getItem(absPath), this);
    }

    public String[] getLockTokens()
    {
        return getDelegate().getLockTokens();
    }

    public String getNamespacePrefix(String uri) throws RepositoryException
    {
        return getDelegate().getNamespacePrefix(uri);
    }

    public String[] getNamespacePrefixes() throws RepositoryException
    {
        return getDelegate().getNamespacePrefixes();
    }

    public String getNamespaceURI(String prefix) throws RepositoryException
    {
        return getDelegate().getNamespaceURI(prefix);
    }

    public Node getNodeByUUID(String uuid) throws RepositoryException
    {
        return NodeWrapper.wrap(getDelegate().getNodeByUUID(uuid), this);
    }

    public Repository getRepository()
    {
        return getDelegate().getRepository();
    }

    public Node getRootNode() throws RepositoryException
    {
        return NodeWrapper.wrap(getDelegate().getRootNode(), this);
    }

    public String getUserID()
    {
        return getDelegate().getUserID();
    }

    public ValueFactory getValueFactory() throws RepositoryException
    {
        return ValueFactoryWrapper.wrap(getDelegate().getValueFactory(), this);
    }

    public Workspace getWorkspace()
    {
        return WorkspaceWrapper.wrap(getDelegate().getWorkspace(), this);
    }

    public boolean hasPendingChanges() throws RepositoryException
    {
        return getDelegate().hasPendingChanges();
    }

    public Session impersonate(Credentials credentials) throws RepositoryException
    {
        return SessionWrapper.wrap(getDelegate().impersonate(credentials));
    }

    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior)
            throws IOException, RepositoryException
    {
    	getActionHandler().beforeSessionImportXML(parentAbsPath);
        getDelegate().importXML(parentAbsPath, in, uuidBehavior);
        getActionHandler().afterSessionImportXML(parentAbsPath);
    }

    public boolean isLive()
    {
        return getDelegate().isLive();
    }

    public boolean itemExists(String absPath) throws RepositoryException
    {
        return getDelegate().itemExists(absPath);
    }

    public void logout()
    {
        getDelegate().logout();
    }

    public void move(String srcAbsPath, String destAbsPath) throws RepositoryException
    {
    	getActionHandler().beforeSessionNodeMove(srcAbsPath, destAbsPath);
        getDelegate().move(srcAbsPath, destAbsPath);
        getActionHandler().afterSessionNodeMove(srcAbsPath, destAbsPath);
    }

    public void refresh(boolean keepChanges) throws RepositoryException
    {
    	getActionHandler().beforeSessionRefresh(keepChanges);
        getDelegate().refresh(keepChanges);
        getActionHandler().afterSessionRefresh(keepChanges);
    }

    public void removeLockToken(String lt)
    {
        getDelegate().removeLockToken(lt);
    }

    public void save() throws RepositoryException
    {
    	getActionHandler().beforeSessionSave();
        getDelegate().save();
        getActionHandler().afterSessionSave();
    }

    public void setNamespacePrefix(String prefix, String uri) throws RepositoryException
    {
        getDelegate().setNamespacePrefix(prefix, uri);
    }

    final Set<Node> raisedSaveEvent = new HashSet<Node>();
    
    private final CompoundActionHandler actionHandler = new CompoundActionHandler();
    
    public CompoundActionHandler getActionHandler()
	{
		return actionHandler;	
	}
    
    private final ChangeLogActionHandler changeLogActionHandler;
    
    
    public void addActionHandler(AbstractActionHandler handler)
    {
    	actionHandler.addHandler(handler);
    	
    }
    
    public void addEventsListener(EventsListener listener)
    {
    	changeLogActionHandler.registerEventsListener(listener);
    }
    
    private final Map<String, Object> attributesMap = new HashMap<String, Object>();
    
    public Map<String, Object> getAttributesMap()
    {
    	return attributesMap;
    }
    
    private ValueFilter valueFilter = new ValueFilter();
    
    public void setValueFilter(ValueFilter valueFilter)
    {
    	if (valueFilter == null)
    	{
    		throw new IllegalArgumentException("Argument 'valueFilter' may not be null.");
    	}
    	this.valueFilter = valueFilter;
    }
    
    public ValueFilter getValueFilter()
    {
    	return valueFilter;
    }
}

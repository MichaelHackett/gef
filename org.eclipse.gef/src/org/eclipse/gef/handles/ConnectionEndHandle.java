/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.handles;

import org.eclipse.draw2d.ConnectionLocator;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.ConnectionEndpointTracker;

/**
 * A handle used at the end of the {@link org.eclipse.draw2d.Connection}.  
 * This is treated differently than the start of the Connection.
 */
public final class ConnectionEndHandle
	extends ConnectionHandle
{

/**
 * Creates a new ConnectionEndHandle, sets its owner to <code>owner</code>,
 * and sets its locator to a {@link ConnectionLocator}.
 * @param owner the ConnectionEditPart owner
 */
public ConnectionEndHandle(ConnectionEditPart owner) {
	setOwner(owner);
	setLocator(new ConnectionLocator(getConnection(), ConnectionLocator.TARGET));
}

/**
 * Creates a new ConnectionEndHandle with its owner set to <code>owner</code>.
 * If the handle is fixed, it cannot be dragged.
 * @param owner the ConnectionEditPart owner
 * @param fixed if true, handle cannot be dragged
 */
public ConnectionEndHandle(ConnectionEditPart owner, boolean fixed) {
	super(fixed);
	setOwner(owner);
	setLocator(new ConnectionLocator(getConnection(), ConnectionLocator.TARGET));
}

/**
 * Creates a new ConnectionEndHandle.
 */
public ConnectionEndHandle() { }

/**
 * Creates and returns a new {@link ConnectionEndpointTracker}.
 * @return the new ConnectionEndpointTracker
 */
protected DragTracker createDragTracker() {
	if (isFixed())
		return null;
	ConnectionEndpointTracker tracker;
	tracker = new ConnectionEndpointTracker((ConnectionEditPart)getOwner());
	tracker.setCommandName(RequestConstants.REQ_RECONNECT_TARGET);
	tracker.setDefaultCursor(getCursor());
	return tracker;
}

}

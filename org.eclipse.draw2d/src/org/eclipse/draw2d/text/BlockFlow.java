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
package org.eclipse.draw2d.text;

import java.util.List;

import org.eclipse.swt.SWT;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A <code>FlowFigure</code> represented by a single {@link BlockBox} fragment containing
 * one or more lines. A BlockFlow is a creator of LineBoxes, which its children require
 * during layout. A BlockFlow can be thought of as a paragraph.
 * 
 * <P>BlockFlows should be nested inside other BlockFlows, but it is also valid to place
 * them in InlineFlows. {@link FlowPage} can be used as a "root" block and can be added to
 * normal draw2d Figures.
 * 
 * <P>Only {@link FlowFigure}s can be added to a BlockFlow.
 * 
 * <P>WARNING: This class is not intended to be subclassed by clients.
 * @author hudsonr
 * @since 2.1
 */
public class BlockFlow extends FlowFigure {

private int aligment = PositionConstants.LEFT;

final BlockBox blockBox;
private int orientation = SWT.NONE;
private boolean bidiValid;

/**
 * Constructs a new BlockFlow. */
public BlockFlow() {
	blockBox = createBlockBox();
}

/**
 * BlockFlows contribute a paragraph separator so as to keep the Bidi state of the text 
 * on either side of this block from affecting each other.  Since each block is like a
 * different paragraph, it does not contribute any actual text to its containing block.
 * 
 * @see org.eclipse.draw2d.text.FlowFigure#contributeBidi(org.eclipse.draw2d.text.BidiProcessor)
 */
protected void contributeBidi(BidiProcessor proc) {
	// the paragraph separator
	proc.addControlText("\u2029"); //$NON-NLS-1$
}

BlockBox createBlockBox() {
	return new BlockBox(this);
}

/**
 * @see org.eclipse.draw2d.text.FlowFigure#createDefaultFlowLayout()
 */
protected FlowFigureLayout createDefaultFlowLayout() {
	return new BlockFlowLayout(this);
}

/**
 * Returns this block's Bidi orientation.  If none was set on this block, it
 * will inherit the one from its containing block.  If there is no containing block, it
 * will return the default orientation (SWT.LEFT_TO_RIGHT).
 * 
 * @return SWT.RIGHT_TO_LEFT or SWT.LEFT_TO_RIGHT
 * @see #setOrientation(int)
 * @since 3.1
 */
public int getOrientation() {
	if (orientation != SWT.NONE)
		return orientation;
	IFigure parent = getParent();
	while (parent != null && !(parent instanceof BlockFlow))
		parent = parent.getParent();
	if (parent != null)
		return ((BlockFlow)parent).getOrientation();
	return SWT.LEFT_TO_RIGHT;
}

/**
 * Returns the BlockBox associated with this.
 * @return This BlockFlow's BlockBox
 */
protected BlockBox getBlockBox() {
	return blockBox;
}

int getBottomMargin() {
	int margin = 0;
	if (getBorder() instanceof FlowBorder) {
		FlowBorder border = (FlowBorder)getBorder();
		return border.getBottomMargin();
	}
	List children = getChildren();
	int childIndex = children.size() - 1;
	if (childIndex >= 0 && children.get(childIndex) instanceof BlockFlow) {
		margin = Math.max(margin,
				((BlockFlow)children.get(childIndex)).getBottomMargin());
	}
	return margin;
}

int getLeftMargin() {
	if (getBorder() instanceof FlowBorder)
		return ((FlowBorder)getBorder()).getLeftMargin();
	return 0;
}

int getRightMargin() {
	if (getBorder() instanceof FlowBorder)
		return ((FlowBorder)getBorder()).getRightMargin();
	return 0;
}

int getTopMargin() {
	int margin = 0;
	if (getBorder() instanceof FlowBorder) {
		FlowBorder border = (FlowBorder)getBorder();
		return border.getTopMargin();
	}
	List children = getChildren();
	if (children.size() > 0 && children.get(0) instanceof BlockFlow) {
		margin = Math.max(margin,
				((BlockFlow)children.get(0)).getTopMargin());
	}
	return margin;
}

/**
 * Returns the horizontal alignment.
 * @return the horizontal alignment
 */
public int getHorizontalAligment() {
	return aligment & PositionConstants.LEFT_CENTER_RIGHT;
}

/**
 * Overridden to do nothing since a BlockFlow is unaffected by changes in its containing
 * block.
 * @see org.eclipse.draw2d.text.FlowFigure#invalidateBidi()
 */
protected void invalidateBidi() {
	// override super to do nothing
}

/**
 * @see org.eclipse.draw2d.Figure#paintBorder(org.eclipse.draw2d.Graphics)
 */
public void paintBorder(Graphics graphics) {
	if (getBorder() instanceof FlowBorder) {
		Rectangle where = getBlockBox().toRectangle();
		where.crop(new Insets(getTopMargin(), getLeftMargin(),
				getBottomMargin(), getRightMargin()));
		((FlowBorder)getBorder()).paint(this, graphics, where, true, true);
	} else
		super.paintBorder(graphics);
	if (selectionStart != -1) {
		graphics.restoreState();
		graphics.setXORMode(true);
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.fillRectangle(getBounds());
	}
}

/**
 * @see org.eclipse.draw2d.text.FlowFigure#postValidate()
 */
public void postValidate() {
	Rectangle newBounds = getBlockBox().toRectangle();
	newBounds.crop(new Insets(getTopMargin(), getLeftMargin(),
			getBottomMargin(), getRightMargin()));
	setBounds(newBounds);
}

/**
 * @see FlowFigure#revalidate()
 */
public void revalidate() {
	BlockFlowLayout layout = (BlockFlowLayout)getLayoutManager();
	layout.blockContentsChanged();
	super.revalidate();
}

/**
 * A Block will invalidate the Bidi state of all its children, so that it is 
 * re-evaluated when this block is next validated.
 * @see org.eclipse.draw2d.text.FlowFigure#revalidateBidi(org.eclipse.draw2d.IFigure)
 */
protected void revalidateBidi(IFigure origin) {
	if (bidiValid) {
		bidiValid = false;
		revalidate();
	}
}

/**
 * Sets the orientation for this block.  Orientation can be one of:
 * <UL>
 *   <LI>{@link SWT#LEFT_TO_RIGHT}
 *   <LI>{@link SWT#RIGHT_TO_LEFT}
 *   <LI>{@link SWT#NONE}
 * </UL>
 * <code>NONE</code> is used to indicate that orientation should be inherited from the
 * encompassing block, or LEFT_TO_RIGHT if no parent block exists.
 * 
 * @param orientation LTR, RTL or NONE
 * @since 3.1
 */
public void setOrientation(int orientation) {
	if (this.orientation == orientation)
		return;
	if (orientation == SWT.RIGHT_TO_LEFT || orientation == SWT.LEFT_TO_RIGHT)
		this.orientation = orientation;
	else 
		this.orientation = SWT.NONE;
	revalidate();
}

/**
 * Sets the horitontal aligment of the block. Valid values are:
 * <UL>
 *   <LI>{@link org.eclipse.draw2d.PositionConstants#LEFT}</LI>
 *   <LI>{@link org.eclipse.draw2d.PositionConstants#RIGHT}</LI>
 *   <LI>{@link org.eclipse.draw2d.PositionConstants#CENTER}</LI>
 * </UL>
 * Blocks with a Bidi orientation of SWT.RIGHT_TO_LEFT will be right-aligned if their
 * horizontal alignment is PositionConstants.LEFT, and left-aligned if their alignment
 * is PositionConstants.RIGHT.
 * @param value the aligment */
public void setHorizontalAligment(int value) {
	if (!(value == PositionConstants.LEFT
		|| value == PositionConstants.RIGHT
		|| value == PositionConstants.CENTER))
		throw new IllegalArgumentException(
			"Horizontal Aligment must be one of: LEFT, CENTER, RIGHT");//$NON-NLS-1$
	this.aligment &= ~PositionConstants.LEFT_CENTER_RIGHT;
	this.aligment |= value;
	revalidate();
}

/**
 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
 */
protected boolean useLocalCoordinates() {
	return true;
}

/**
 * Re-evaluate the Bidi state of all the fragments if it has been 
 * invalidated.
 * @see org.eclipse.draw2d.IFigure#validate()
 */
public void validate() {
	if (!bidiValid) {
		BidiProcessor.INSTANCE.setOrientation(getOrientation());
		super.contributeBidi(BidiProcessor.INSTANCE);
		BidiProcessor.INSTANCE.process();
		bidiValid = true;
	}
	super.validate();
}

}
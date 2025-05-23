/*******************************************************************************
 * Copyright (c) 2010, 2024 bndtools project and others.
 *
* This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Neil Bartlett <njbartlett@gmail.com> - initial API and implementation
 *     BJ Hargrave <bj@hargrave.dev> - ongoing enhancements
 *     Christoph Rueger <chrisrueger@gmail.com> - ongoing enhancements
 *     Peter Kriens <peter.kriens@aqute.biz> - ongoing enhancements
 *     Christoph Läubrich - Adapt to PDE codebase
*******************************************************************************/
package org.eclipse.pde.bnd.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

public class EditorUtils {
	public static boolean saveEditorIfDirty(final IEditorPart editor, String dialogTitle, String message) {
		if (editor.isDirty()) {
			if (MessageDialog.openConfirm(editor.getEditorSite()
				.getShell(), dialogTitle, message)) {
				IRunnableWithProgress saveRunnable = monitor -> editor.doSave(monitor);
				IWorkbenchWindow window = editor.getSite()
					.getWorkbenchWindow();
				try {
					window.run(false, false, saveRunnable);
				} catch (InvocationTargetException e1) {} catch (InterruptedException e1) {
					Thread.currentThread()
						.interrupt();
				}
			}
		}
		return !editor.isDirty();
	}

	public static final IFormPart findPartByClass(IManagedForm form, Class<? extends IFormPart> clazz) {
		IFormPart[] parts = form.getParts();
		for (IFormPart part : parts) {
			if (clazz.isInstance(part)) {
				return part;
			}
		}
		return null;
	}

	/**
	 * Creates a help button with icon and tool-tip.
	 *
	 * @param url
	 * @param tooltipText
	 */
	public static final Action createHelpButton(String url, String tooltipText) {
		Action btn = new Action("Help", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				Program.launch(url);
			}
		};
		btn.setEnabled(true);
		btn.setToolTipText(tooltipText);
		btn.setImageDescriptor(Resources.getImageDescriptor("help.svg"));

		return btn;
	}

	/**
	 * Creates a help button with help-icon, text and tool-tip.
	 *
	 * @param url
	 * @param buttonText
	 * @param tooltipText
	 */
	public static final ActionContributionItem createHelpButtonWithText(String url, String buttonText, String tooltipText) {
		Action btn = createHelpButton(url, tooltipText);
		btn.setText(buttonText);

		// the ActionContributionItem is required to display text below the icon
		// of the button
		ActionContributionItem helpContrib = new ActionContributionItem(btn);
		helpContrib.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		return helpContrib;
	}
}

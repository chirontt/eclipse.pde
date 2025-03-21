/*******************************************************************************
 * Copyright (c) 2010, 2020 bndtools project and others.
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
*******************************************************************************/
package org.eclipse.pde.bnd.ui;

import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FileExtensionFilter extends ViewerFilter {

	private final String[]	extensions;
	private final boolean	caseInsensitive;

	/**
	 * Construct a filter from a list of supported extensions.
	 */
	public FileExtensionFilter(String[] extensions, boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
		this.extensions = new String[extensions.length];

		for (int i = 0; i < extensions.length; i++) {
			if (extensions[i].length() > 0 && extensions[i].charAt(0) == '.') {
				this.extensions[i] = extensions[i];
			} else {
				this.extensions[i] = "." + extensions[i];
			}

			if (caseInsensitive) {
				this.extensions[i] = this.extensions[i].toLowerCase();
			}
		}
	}

	/**
	 * Construct a filter from a single supported extension.
	 */
	public FileExtensionFilter(String extension, boolean caseInsensitive) {
		this(new String[] {
			extension
		}, caseInsensitive);
	}

	/**
	 * Construct a case-insensitive filter from a list of supported extensions.
	 *
	 * @param extensions
	 */
	public FileExtensionFilter(String[] extensions) {
		this(extensions, true);
	}

	/**
	 * Construct a case-insensitive filter from a single supported extension.
	 *
	 * @param extension
	 */
	public FileExtensionFilter(String extension) {
		this(extension, true);
	}

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof IFile) {
			String fileName = ((IFile) element).getName();
			if (caseInsensitive) {
				fileName = fileName.toLowerCase(Locale.ENGLISH);
			}
			for (String extension : this.extensions) {
				if (fileName.endsWith(extension)) {
					return true;
				}
			}
			return false;
		}

		if (element instanceof IProject && !((IProject) element).isOpen()) {
			return false;
		}

		if (element instanceof IContainer) {
			try {
				IResource[] resources = ((IContainer) element).members();
				for (IResource element2 : resources) {
					if (select(viewer, parent, element2)) {
						return true;
					}
				}
			} catch (CoreException e) {}
		}
		return false;
	}

}

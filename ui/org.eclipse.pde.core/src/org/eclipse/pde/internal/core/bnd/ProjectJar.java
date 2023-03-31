/*******************************************************************************
 * Copyright (c) 2023 Christoph Läubrich and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.bnd;

import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import java.io.InputStream;
import java.util.function.Predicate;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class ProjectJar extends Jar {

	private IFolder outputFolder;

	public ProjectJar(IProject project, Predicate<IResource> filter) throws CoreException {
		super(project.getName());
		if (project.hasNature(JavaCore.NATURE_ID)) {
			IJavaProject javaProject = JavaCore.create(project);
			IPath outputLocation = javaProject.getOutputLocation();
			IWorkspaceRoot workspaceRoot = project.getWorkspace().getRoot();
			outputFolder = workspaceRoot.getFolder(outputLocation);
			FileResource.addResources(this, outputFolder, filter);
			IClasspathEntry[] classpath = javaProject.getResolvedClasspath(true);
			for (IClasspathEntry cp : classpath) {
				if (cp.getEntryKind() == IClasspathEntry.CPE_SOURCE && !cp.isTest()) {
					IPath location = cp.getOutputLocation();
					if (location != null) {
						IFolder otherOutputFolder = workspaceRoot.getFolder(location);
						FileResource.addResources(this, otherOutputFolder, filter);
					}
				}
			}
		}
	}

	@Override
	public boolean putResource(String path, Resource resource, boolean overwrite) {
		if (resource instanceof FileResource) {
			return super.putResource(path, resource, overwrite);
		}
		IFile file = outputFolder.getFile(path);
		try {
			if (file.exists()) {
				if (overwrite) {
					try (InputStream stream = resource.openInputStream()) {
						file.setContents(stream, true, false, null);
					}
				}
			} else {
				mkdirs(file);
				try (InputStream stream = resource.openInputStream()) {
					file.create(stream, true, null);
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return super.putResource(path, new FileResource(file), overwrite);
	}

	private void mkdirs(IResource resource) throws CoreException {
		if (resource == null) {
			return;
		}
		mkdirs(resource.getParent());
		if (resource instanceof IFolder folder) {
			if (!folder.exists()) {
				folder.create(true, true, null);
			}
		}
	}

	public IFolder getOutputFolder() {
		return outputFolder;
	}

}
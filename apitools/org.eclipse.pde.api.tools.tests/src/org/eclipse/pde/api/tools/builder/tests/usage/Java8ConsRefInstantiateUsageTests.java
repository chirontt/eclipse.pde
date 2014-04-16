package org.eclipse.pde.api.tools.builder.tests.usage;

/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import junit.framework.Test;

import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.api.tools.internal.problems.ApiProblemFactory;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IElementDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblem;

public class Java8ConsRefInstantiateUsageTests extends Java8UsageTest {
	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public Java8ConsRefInstantiateUsageTests(String name) {
		super(name);
	}

	/**
	 * @return the test class for this suite
	 */
	public static Test suite() {
		return buildTestSuite(Java8ConsRefInstantiateUsageTests.class);
	}

	@Override
	protected IPath getTestSourcePath() {
		return super.getTestSourcePath().append("methodref"); //$NON-NLS-1$
	}

	/**
	 * Returns the problem id with the given kind
	 * 
	 * @param kind
	 * @return the problem id
	 */
	protected int getProblemId(int kind, int flags) {
		return ApiProblemFactory.createProblemId(IApiProblem.CATEGORY_USAGE, IElementDescriptor.TYPE, kind, flags);
	}


	/**
	 * Tests illegal references to method reference and constructor reference
	 * (full)
	 */
	public void testConsRefInstantiateF() {
		x1(false);
	}

	/**
	 * Tests illegal references to method reference and constructor reference
	 * (incremental)
	 */
	public void testConsRefInstantiateI() {
		x1(true);
	}

	private void x1(boolean inc) {
		int[] pids = new int[] {

				getProblemId(IApiProblem.ILLEGAL_INSTANTIATE, IApiProblem.NO_FLAGS),
				getProblemId(IApiProblem.ILLEGAL_INSTANTIATE, IApiProblem.NO_FLAGS),
				getProblemId(IApiProblem.ILLEGAL_INSTANTIATE, IApiProblem.NO_FLAGS) };
		setExpectedProblemIds(pids);
		String typename = "testConstructorRefInstantiate"; //$NON-NLS-1$

		String[][] args = new String[][] {
				{ "ConstructorReference2", typename }, //$NON-NLS-1$ 
				{ "ConstructorReference2", typename }, //$NON-NLS-1$ 
				{ "ConstructorReference2", typename } //$NON-NLS-1$ 

		};
		setExpectedMessageArgs(args);
		setExpectedLineMappings(new LineMapping[] {
				new LineMapping(30, pids[0], args[0]),
				new LineMapping(32, pids[1], args[1]),
				new LineMapping(34, pids[2], args[2])

		});

		deployUsageTest(typename, inc);
	}


}
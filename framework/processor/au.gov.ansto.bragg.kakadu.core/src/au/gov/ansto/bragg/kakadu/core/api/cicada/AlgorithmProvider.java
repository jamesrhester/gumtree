/*******************************************************************************
 * Copyright (c) 2007 Australian Nuclear Science and Technology Organisation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Danil Klimontov (Bragg Institute) - initial API and implementation
 *******************************************************************************/
package au.gov.ansto.bragg.kakadu.core.api.cicada;

import java.util.List;

import au.gov.ansto.bragg.kakadu.core.data.UIAlgorithm;

public interface AlgorithmProvider {

//	List<Instrument> getAvailableInstruments();
//	
//	List<UIAlgorithm> getAlgorithms(Instrument instrument);

	List<UIAlgorithm> getAvailableAlgorithms();
	
}

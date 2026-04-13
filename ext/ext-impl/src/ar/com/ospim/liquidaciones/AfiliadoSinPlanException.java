/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ar.com.ospim.liquidaciones;

import com.liferay.portal.PortalException;

/**
 * <a href="AfiliadoSinPlanException"><b><i>View Source
 * </i></b></a>
 *
 * @author Carlos Rivas
 *
 */
public class AfiliadoSinPlanException extends PortalException {

	private static final long serialVersionUID = 43431539987302214L;

	public AfiliadoSinPlanException() {
		super();
	}

	public AfiliadoSinPlanException(String msg) {
		super(msg);
	}

	public AfiliadoSinPlanException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AfiliadoSinPlanException(Throwable cause) {
		super(cause);
	}

}
package com.dexels.navajo.functions;

import com.dexels.navajo.parser.TMLExpressionException;


/**
 * <p>Title: Navajo Product Project</p>
 * <p>Description: This is the official source for the Navajo server</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Dexels BV</p>
 * @author Arjen Schoneveld
 * @version $Id$
 *
 * DISCLAIMER
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL DEXELS BV OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, ORGetPropertyDirection
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

public class GetPropertyDirection extends GetPropertyAttribute {

	public GetPropertyDirection() {
	}

	public String remarks() {
		return "Gets the type of property as a string";
	}

	public Object evaluate() throws com.dexels.navajo.parser.TMLExpressionException {
		if (getOperands().size() != 1) {
			throw new TMLExpressionException(this, "Invalid function call");
		}
		Object o = getOperand(0);
		if (!(o instanceof String)) {
			throw new TMLExpressionException(this, "String argument expected");
		}
		String propertyName = (String) o;

		return getAttribute(propertyName, "direction");
	}

	public String usage() {
		return "GetPropertyDirection(property name)";
	}

}
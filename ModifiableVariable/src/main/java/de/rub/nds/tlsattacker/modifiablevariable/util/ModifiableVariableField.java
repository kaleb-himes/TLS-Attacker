/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS.
 *
 * Copyright (C) 2015 Chair for Network and Data Security,
 *                    Ruhr University Bochum
 *                    (juraj.somorovsky@rub.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.rub.nds.tlsattacker.modifiablevariable.util;

import java.lang.reflect.Field;

/**
 * Represents an object with its modifiable variable field.
 * 
 * @author Juraj Somorovsky - juraj.somorovsky@rub.de
 */
public class ModifiableVariableField {

    private Object object;

    private Field field;

    public ModifiableVariableField() {

    }

    public ModifiableVariableField(Object o, Field f) {
	this.object = o;
	this.field = f;
    }

    public Object getObject() {
	return object;
    }

    public void setObject(Object object) {
	this.object = object;
    }

    public Field getField() {
	return field;
    }

    public void setField(Field field) {
	this.field = field;
    }

}

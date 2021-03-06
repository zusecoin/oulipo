/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.oulipo.browser.tables;

import org.oulipo.rdf.model.User;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class UserTree extends RecursiveTreeObject<UserTree> {

	public final StringProperty address;

	public final StringProperty name;

	public final StringProperty publicKey;

	public UserTree(User user) {
		this.address = new SimpleStringProperty(user.subject.value);
		this.publicKey = new SimpleStringProperty(user.publicKey);
		this.name = new SimpleStringProperty(user.xandle);
	}
}

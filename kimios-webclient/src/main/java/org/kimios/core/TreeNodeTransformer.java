/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.core;

import flexjson.transformer.AbstractTransformer;

public class TreeNodeTransformer extends AbstractTransformer {

	private boolean withLeaf;

	public TreeNodeTransformer(boolean withLeaf) {
		this.withLeaf = withLeaf;
	}

	public void transform(Object object) {
		getContext().writeOpenObject();
		DMSTreeNode node = (DMSTreeNode) object;

		// Write out the fields
		getContext().writeName("creationDate");
		getContext().transform(node.getCreationDate());
		getContext().writeComma();
		getContext().writeName("dmEntityUid");
		getContext().transform(node.getDmEntityUid());
		getContext().writeComma();
		getContext().writeName("uid");
		getContext().transform(node.getUid());
		getContext().writeComma();
		getContext().writeName("id");
		getContext().transform(node.getId());
		getContext().writeComma();
		getContext().writeName("name");
		getContext().transform(node.getName());
		getContext().writeComma();
		getContext().writeName("extension");
		getContext().transform(node.getExtension());
		getContext().writeComma();
		getContext().writeName("icon");
		getContext().transform(node.getIcon());
		getContext().writeComma();
		getContext().writeName("owner");
		getContext().transform(node.getOwner());
		getContext().writeComma();
		getContext().writeName("ownerSource");
		getContext().transform(node.getOwnerSource());
		getContext().writeComma();
		getContext().writeName("path");
		getContext().transform(node.getPath());
		getContext().writeComma();
		getContext().writeName("type");
		getContext().transform(node.getType());
		getContext().writeComma();
		getContext().writeName("text");
		getContext().transform(node.getText());
		if (withLeaf) {
                        getContext().writeComma();
			getContext().writeName("leaf");
			getContext().transform(node.isLeaf());
		}
		getContext().writeCloseObject();

	}
}

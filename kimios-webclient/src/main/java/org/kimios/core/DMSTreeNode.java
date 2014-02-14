/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kimios.core;

import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.Folder;
import org.kimios.kernel.ws.pojo.Workspace;

import java.util.Calendar;

/**
 * 
 * @author Fabien Alin
 */
public class DMSTreeNode {



    private static String WORKSPACE_CLS = "dm-entity-tab-properties-workspace";
    private static String FOLDER_CLS = "dm-entity-tab-properties-folder";

	private String contextPath;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String _contextPath) {
		this.contextPath = _contextPath;
		switch (type) {
		case 1:
			this.iconCls = WORKSPACE_CLS;
			break;
		case 2:
			this.iconCls = FOLDER_CLS;
			break;
		case 3:
			this.iconCls = (this.extension != null
							&& !this.extension.equalsIgnoreCase("") ? this.extension
							: "unknown");
			break;
		}
	}

	private String id;
	private long uid;
	private int type;
	private String name;
	private String text;
	private boolean leaf;
	private String iconCls;
	private String extension = "";
	private String path;
	private Calendar creationDate;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getCreationDate() {
		if (creationDate == null)
			return -1;
		return creationDate.getTimeInMillis();
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerSource() {
		return ownerSource;
	}

	public void setOwnerSource(String ownerSource) {
		this.ownerSource = ownerSource;
	}

	private String owner;
	private String ownerSource;

	public DMSTreeNode(Workspace w) {
		this.id = w.getUid() + "_1";
		this.text = w.getName();
		this.leaf = false;
		this.iconCls = WORKSPACE_CLS;
		this.type = 1;
		this.uid = w.getUid();
		this.name = w.getName();
		this.path = w.getPath();
		this.creationDate = w.getCreationDate();
		this.owner = w.getOwner();
		this.ownerSource = w.getOwnerSource();
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public DMSTreeNode(Folder f) {
		this.id = f.getUid() + "_2";
		this.text = f.getName();
		this.leaf = false;
		this.iconCls = FOLDER_CLS;
		this.type = 2;
		this.uid = f.getUid();
		this.name = f.getName();
		this.path = f.getPath();
		this.creationDate = f.getCreationDate();
		this.owner = f.getOwner();
		this.ownerSource = f.getOwnerSource();
	}

	public DMSTreeNode(Document d) {
		this.id = d.getUid() + "_3";
		this.text = d.getName();
		this.leaf = true;
		this.iconCls =  (d.getExtension() != null
						&& !d.getExtension().equalsIgnoreCase("") ? d
						.getExtension() : "unknown");
		this.type = 3;
		this.uid = d.getUid();
		this.name = d.getName();
		this.extension = d.getExtension();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public String getText() {
		return text;
	}

	public void setText(String name) {
		this.text = name;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getDmEntityUid() {
		return uid;
	}

	public void setDmEntityUid(long dmEntityUid) {
		this.uid = dmEntityUid;
	}

}

package com.femto.post.entity;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Optionalstare implements Serializable {
	String name;
	List<PostMessage> pms;

	public Optionalstare(String name, List<PostMessage> pms) {
		super();
		this.name = name;
		this.pms = pms;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PostMessage> getPms() {
		return pms;
	}

	public void setPms(List<PostMessage> pms) {
		this.pms = pms;
	}
}

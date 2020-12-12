package mequie.client.response;

import java.util.ArrayList;
import java.util.List;

public class UInfoResponse {
	private String user;
	private List<String> groups = new ArrayList<>();
	private List<String> memberGroups = new ArrayList<>();
	private List<String> ownerGroups = new ArrayList<>();
	private boolean error = false;
	
	public UInfoResponse(String user, List<String> groups, List<String> ownerGroups, List<String> memberGroups) {
		super();
		this.user = user;
		this.groups = groups;
		this.memberGroups = memberGroups;
		this.ownerGroups = ownerGroups;
	}

	public UInfoResponse(String utilizador) {
		user = utilizador;
	}

	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public List<String> getGroups() {
		return groups;
	}
	
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	public void addGroup(String g) {
		groups.add(g);
	}
	
	public void addMemberGroup(String g) {
		memberGroups.add(g);
	}
	
	public void addOwnerGroup(String g) {
		ownerGroups.add(g);
	}
	
	public List<String> getMemberGroups(){
		return memberGroups;
	}
	
	public List<String> getOwnerGroups(){
		return ownerGroups;
	}
	
	public void setError() {
		error = true;
	}
	
	public boolean getError() {
		return error;
	}

	@Override
	public String toString() {
		return "user: " + user + "\ngroups: " + groups.toString();
	}
		
	
}

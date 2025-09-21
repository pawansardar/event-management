package com.eventmgmt.auth_service.dto.response;

import java.util.List;

public class UserPageResponse {
	private List<UserResponse> users;
	private int currentPage;
	private long totalItems;
	private int totalPages;
	
	public UserPageResponse() {}

	public UserPageResponse(List<UserResponse> users, int currentPage, long totalItems, int totalPages) {
		super();
		this.users = users;
		this.currentPage = currentPage;
		this.totalItems = totalItems;
		this.totalPages = totalPages;
	}

	public List<UserResponse> getUsers() {
		return users;
	}

	public void setUsers(List<UserResponse> users) {
		this.users = users;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public long getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(long totalItems) {
		this.totalItems = totalItems;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
}
